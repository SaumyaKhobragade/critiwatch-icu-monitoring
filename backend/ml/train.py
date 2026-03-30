"""Machine learning training pipeline for CritiWatch risk prediction."""

from __future__ import annotations

import argparse
from pathlib import Path
from typing import Any

import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

try:
    from ml.evaluate import evaluate_model, print_model_evaluation
    from ml.preprocess import prepare_training_dataset, split_training_data
    from ml.utils import (
        backend_root,
        ensure_directory,
        load_dataset,
        load_json_file,
        now_utc_iso,
        save_json_file,
    )
except ModuleNotFoundError:
    from evaluate import evaluate_model, print_model_evaluation
    from preprocess import prepare_training_dataset, split_training_data
    from utils import (
        backend_root,
        ensure_directory,
        load_dataset,
        load_json_file,
        now_utc_iso,
        save_json_file,
    )


def parse_args() -> argparse.Namespace:
    default_data_path = backend_root() / "ml" / "data" / "training_dataset.csv"
    default_artifacts_dir = backend_root() / "ml" / "artifacts"

    parser = argparse.ArgumentParser(description="Train CritiWatch deterioration baseline models")
    parser.add_argument("--data-path", type=Path, default=default_data_path)
    parser.add_argument("--artifacts-dir", type=Path, default=default_artifacts_dir)
    parser.add_argument("--column-map-path", type=Path, default=None)
    parser.add_argument("--label-column", type=str, default=None)
    parser.add_argument(
        "--label-strategy",
        type=str,
        choices=["auto", "existing", "derived"],
        default="auto",
        help="Use existing labels, derive labels, or auto-detect",
    )
    parser.add_argument("--binary", action="store_true", help="Train Stable vs Unstable")
    parser.add_argument("--test-size", type=float, default=0.2)
    parser.add_argument("--random-state", type=int, default=42)
    parser.add_argument("--synthetic-rows", type=int, default=1200)
    parser.add_argument(
        "--no-engineered-features",
        action="store_true",
        help="Disable engineered flags and pulse pressure",
    )
    return parser.parse_args()


def _build_models(random_state: int) -> dict[str, Pipeline]:
    logistic = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            ("scaler", StandardScaler()),
            (
                "model",
                LogisticRegression(
                    max_iter=2000,
                    class_weight="balanced",
                    random_state=random_state,
                ),
            ),
        ]
    )

    random_forest = Pipeline(
        steps=[
            ("imputer", SimpleImputer(strategy="median")),
            (
                "model",
                RandomForestClassifier(
                    n_estimators=350,
                    max_depth=None,
                    min_samples_leaf=1,
                    class_weight="balanced_subsample",
                    random_state=random_state,
                ),
            ),
        ]
    )

    return {
        "logistic_regression": logistic,
        "random_forest": random_forest,
    }


def _choose_best_model(results: dict[str, dict[str, Any]]) -> str:
    ranked = sorted(
        results.items(),
        key=lambda item: (item[1]["f1_weighted"], item[1]["accuracy"]),
        reverse=True,
    )
    return ranked[0][0]


def train() -> None:
    args = parse_args()

    column_mapping = None
    if args.column_map_path:
        column_mapping = load_json_file(args.column_map_path)

    raw_df, data_source = load_dataset(
        data_path=args.data_path,
        synthetic_rows=args.synthetic_rows,
        random_state=args.random_state,
    )

    if data_source == "csv":
        print(f"Loaded dataset from CSV: {args.data_path}")
    else:
        print(
            f"CSV not found at {args.data_path}. Generated synthetic dataset "
            f"with {args.synthetic_rows} rows for training."
        )

    print(f"Detected columns: {list(raw_df.columns)}")

    features, labels, metadata = prepare_training_dataset(
        raw_df=raw_df,
        label_column=args.label_column,
        label_strategy=args.label_strategy,
        column_mapping=column_mapping,
        binary_mode=args.binary,
        include_engineered_features=not args.no_engineered_features,
    )

    X_train, X_test, y_train, y_test = split_training_data(
        features=features,
        labels=labels,
        test_size=args.test_size,
        random_state=args.random_state,
    )

    print(f"Rows used for training: {metadata['usable_rows']}")
    print(f"Feature columns ({len(metadata['feature_columns'])}): {metadata['feature_columns']}")
    print(f"Label source: {metadata['label_source']}")
    print(f"Class distribution: {metadata['class_distribution']}")

    model_candidates = _build_models(args.random_state)
    trained_models: dict[str, Pipeline] = {}
    evaluation_results: dict[str, dict[str, Any]] = {}

    label_order = sorted(labels.unique())

    for model_name, pipeline in model_candidates.items():
        pipeline.fit(X_train, y_train)
        predictions = pipeline.predict(X_test)
        metrics = evaluate_model(y_true=y_test, y_pred=predictions, label_order=label_order)

        trained_models[model_name] = pipeline
        evaluation_results[model_name] = metrics
        print_model_evaluation(model_name, metrics)

    best_model_name = _choose_best_model(evaluation_results)
    best_model = trained_models[best_model_name]

    artifacts_dir = ensure_directory(args.artifacts_dir)

    best_model_path = artifacts_dir / "best_model.pkl"
    comparison_path = artifacts_dir / "model_comparison.json"
    summary_path = artifacts_dir / "training_summary.json"
    feature_config_path = artifacts_dir / "feature_config.json"

    joblib.dump(best_model, best_model_path)

    save_json_file(comparison_path, evaluation_results)
    save_json_file(
        feature_config_path,
        {
            "feature_columns": metadata["feature_columns"],
            "feature_source_columns": metadata["feature_source_columns"],
            "label_source": metadata["label_source"],
            "binary_mode": args.binary,
            "label_strategy": args.label_strategy,
        },
    )

    summary_payload = {
        "timestamp_utc": now_utc_iso(),
        "data_source": data_source,
        "dataset_path": str(args.data_path),
        "rows_total": metadata["dataset_rows"],
        "rows_used": metadata["usable_rows"],
        "rows_dropped": metadata["dropped_rows"],
        "selected_model": best_model_name,
        "selected_model_metrics": evaluation_results[best_model_name],
        "features_used": metadata["feature_columns"],
        "label_source": metadata["label_source"],
        "class_distribution": metadata["class_distribution"],
        "artifact_paths": {
            "best_model": str(best_model_path),
            "model_comparison": str(comparison_path),
            "feature_config": str(feature_config_path),
            "training_summary": str(summary_path),
        },
    }
    save_json_file(summary_path, summary_payload)

    print("\n=== Training Summary ===")
    print(f"Selected model: {best_model_name}")
    print(f"Best weighted F1: {evaluation_results[best_model_name]['f1_weighted']:.4f}")
    print(f"Artifacts saved in: {artifacts_dir}")
    print(f"- {best_model_path.name}")
    print(f"- {comparison_path.name}")
    print(f"- {feature_config_path.name}")
    print(f"- {summary_path.name}")


if __name__ == "__main__":
    train()
