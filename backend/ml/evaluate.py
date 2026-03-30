from __future__ import annotations

from typing import Any

from sklearn.metrics import (
    accuracy_score,
    classification_report,
    confusion_matrix,
    precision_recall_fscore_support,
)


def evaluate_model(
    y_true,
    y_pred,
    label_order: list[str] | None = None,
) -> dict[str, Any]:
    labels = label_order or sorted(set(y_true) | set(y_pred))

    precision_weighted, recall_weighted, f1_weighted, _ = precision_recall_fscore_support(
        y_true,
        y_pred,
        average="weighted",
        zero_division=0,
    )
    precision_macro, recall_macro, f1_macro, _ = precision_recall_fscore_support(
        y_true,
        y_pred,
        average="macro",
        zero_division=0,
    )

    return {
        "accuracy": float(accuracy_score(y_true, y_pred)),
        "precision_weighted": float(precision_weighted),
        "recall_weighted": float(recall_weighted),
        "f1_weighted": float(f1_weighted),
        "precision_macro": float(precision_macro),
        "recall_macro": float(recall_macro),
        "f1_macro": float(f1_macro),
        "confusion_matrix": confusion_matrix(y_true, y_pred, labels=labels).tolist(),
        "labels": labels,
        "classification_report": classification_report(
            y_true,
            y_pred,
            labels=labels,
            zero_division=0,
            output_dict=True,
        ),
        "classification_report_text": classification_report(
            y_true,
            y_pred,
            labels=labels,
            zero_division=0,
        ),
    }


def print_model_evaluation(model_name: str, metrics: dict[str, Any]) -> None:
    print(f"\n=== {model_name} Evaluation ===")
    print(f"Accuracy           : {metrics['accuracy']:.4f}")
    print(f"Precision (weighted): {metrics['precision_weighted']:.4f}")
    print(f"Recall (weighted)  : {metrics['recall_weighted']:.4f}")
    print(f"F1 (weighted)      : {metrics['f1_weighted']:.4f}")
    print("Confusion Matrix:")
    for row in metrics["confusion_matrix"]:
        print(f"  {row}")
    print("Classification Report:")
    print(metrics["classification_report_text"])
