from __future__ import annotations

from typing import Any

import pandas as pd
from sklearn.model_selection import train_test_split

CANONICAL_FEATURES = [
    "heart_rate",
    "spo2",
    "systolic_bp",
    "diastolic_bp",
    "respiratory_rate",
    "temperature",
]

FEATURE_ALIASES: dict[str, list[str]] = {
    "heart_rate": ["heart_rate", "hr", "heartrate", "heart rate", "pulse", "pulse_rate"],
    "spo2": ["spo2", "sp02", "spo_2", "oxygen_saturation", "oxygen_sat", "o2_sat"],
    "systolic_bp": ["systolic_bp", "sbp", "systolic", "sys_bp", "blood_pressure_systolic"],
    "diastolic_bp": ["diastolic_bp", "dbp", "diastolic", "dia_bp", "blood_pressure_diastolic"],
    "respiratory_rate": ["respiratory_rate", "rr", "resp_rate", "respiratory rate"],
    "temperature": ["temperature", "temp", "body_temperature", "body_temp"],
}

DEFAULT_LABEL_ALIASES = ["risk_level", "risk", "label", "target", "class", "outcome"]

PLAUSIBLE_RANGES: dict[str, tuple[float, float]] = {
    "heart_rate": (25.0, 240.0),
    "spo2": (50.0, 100.0),
    "systolic_bp": (60.0, 260.0),
    "diastolic_bp": (30.0, 160.0),
    "respiratory_rate": (6.0, 70.0),
    "temperature": (30.0, 110.0),
}


def _normalize_name(value: str) -> str:
    return value.strip().lower().replace("-", "_").replace(" ", "_")


def _find_column(df: pd.DataFrame, options: list[str]) -> str | None:
    normalized = {_normalize_name(col): col for col in df.columns}
    for option in options:
        key = _normalize_name(option)
        if key in normalized:
            return normalized[key]
    return None


def resolve_feature_columns(
    df: pd.DataFrame,
    column_mapping: dict[str, str] | None = None,
) -> dict[str, str]:
    resolved: dict[str, str] = {}
    provided_mapping = column_mapping or {}

    for feature in CANONICAL_FEATURES:
        user_value = provided_mapping.get(feature)
        if user_value:
            match = _find_column(df, [user_value])
            if not match:
                raise ValueError(
                    f"Mapped column '{user_value}' for feature '{feature}' was not found in dataset"
                )
            resolved[feature] = match
            continue

        match = _find_column(df, FEATURE_ALIASES[feature])
        if not match:
            raise ValueError(
                f"Could not resolve required feature column '{feature}'. "
                f"Available columns: {list(df.columns)}"
            )
        resolved[feature] = match

    return resolved


def _to_celsius(temp_value: float) -> float:
    if pd.isna(temp_value):
        return temp_value
    if temp_value > 45.0:
        return (temp_value - 32.0) * (5.0 / 9.0)
    return temp_value


def _normalize_existing_label(value: Any) -> str | None:
    if pd.isna(value):
        return None

    normalized = str(value).strip().lower()
    mapping = {
        "stable": "Stable",
        "low": "Stable",
        "normal": "Stable",
        "0": "Stable",
        "warning": "Warning",
        "medium": "Warning",
        "moderate": "Warning",
        "1": "Warning",
        "unstable": "Warning",
        "critical": "Critical",
        "high": "Critical",
        "severe": "Critical",
        "2": "Critical",
    }
    return mapping.get(normalized)


def derive_risk_label(row: pd.Series) -> str:
    severe_hits = 0
    warning_hits = 0

    heart_rate = row["heart_rate"]
    spo2 = row["spo2"]
    systolic_bp = row["systolic_bp"]
    respiratory_rate = row["respiratory_rate"]
    temperature = row["temperature"]

    if pd.notna(spo2) and spo2 < 90:
        severe_hits += 1
    if pd.notna(systolic_bp) and systolic_bp < 90:
        severe_hits += 1

    if pd.notna(heart_rate) and heart_rate > 120:
        warning_hits += 1
    if pd.notna(respiratory_rate) and respiratory_rate > 24:
        warning_hits += 1

    temp_c = _to_celsius(temperature)
    if pd.notna(temp_c) and temp_c >= 38.0:
        warning_hits += 1

    if severe_hits >= 1 and warning_hits >= 2:
        return "Critical"
    if severe_hits >= 1:
        return "Critical"
    if warning_hits >= 1:
        return "Warning"
    return "Stable"


def _clean_feature_ranges(features: pd.DataFrame) -> pd.DataFrame:
    cleaned = features.copy()
    for feature in CANONICAL_FEATURES:
        cleaned[feature] = pd.to_numeric(cleaned[feature], errors="coerce")
        lower, upper = PLAUSIBLE_RANGES[feature]
        cleaned.loc[(cleaned[feature] < lower) | (cleaned[feature] > upper), feature] = pd.NA
    return cleaned


def _add_engineered_features(features: pd.DataFrame) -> pd.DataFrame:
    enriched = features.copy()

    enriched["low_spo2_flag"] = (enriched["spo2"] < 90).fillna(False).astype(int)
    enriched["hypotension_flag"] = (enriched["systolic_bp"] < 90).fillna(False).astype(int)
    enriched["tachycardia_flag"] = (enriched["heart_rate"] > 120).fillna(False).astype(int)

    temp_c = enriched["temperature"].apply(_to_celsius)
    enriched["fever_flag"] = (temp_c >= 38.0).fillna(False).astype(int)

    enriched["pulse_pressure"] = enriched["systolic_bp"] - enriched["diastolic_bp"]
    return enriched


def _resolve_label_column(df: pd.DataFrame, explicit_label_column: str | None) -> str | None:
    if explicit_label_column:
        match = _find_column(df, [explicit_label_column])
        if not match:
            raise ValueError(f"Label column '{explicit_label_column}' not found in dataset")
        return match

    return _find_column(df, DEFAULT_LABEL_ALIASES)


def prepare_training_dataset(
    raw_df: pd.DataFrame,
    label_column: str | None = None,
    label_strategy: str = "auto",
    column_mapping: dict[str, str] | None = None,
    binary_mode: bool = False,
    include_engineered_features: bool = True,
) -> tuple[pd.DataFrame, pd.Series, dict[str, Any]]:
    if label_strategy not in {"auto", "existing", "derived"}:
        raise ValueError("label_strategy must be one of: auto, existing, derived")

    resolved_features = resolve_feature_columns(raw_df, column_mapping)

    features = raw_df[[resolved_features[name] for name in CANONICAL_FEATURES]].rename(
        columns={resolved_features[name]: name for name in CANONICAL_FEATURES}
    )
    features = _clean_feature_ranges(features)

    if include_engineered_features:
        features = _add_engineered_features(features)

    used_label_column = _resolve_label_column(raw_df, label_column)
    labels: pd.Series | None = None
    label_source = ""

    if label_strategy in {"auto", "existing"} and used_label_column:
        normalized_labels = raw_df[used_label_column].apply(_normalize_existing_label)
        if normalized_labels.notna().sum() > 0:
            labels = normalized_labels
            label_source = f"existing:{used_label_column}"

    if labels is None:
        if label_strategy == "existing":
            raise ValueError(
                "Requested label_strategy='existing', but no usable label column was found"
            )
        labels = features[CANONICAL_FEATURES].apply(derive_risk_label, axis=1)
        label_source = "derived_rules"

    if binary_mode:
        labels = labels.map(lambda x: "Stable" if x == "Stable" else "Unstable")

    valid_rows = labels.notna()
    features = features.loc[valid_rows].reset_index(drop=True)
    labels = labels.loc[valid_rows].reset_index(drop=True)

    metadata: dict[str, Any] = {
        "dataset_rows": int(raw_df.shape[0]),
        "usable_rows": int(features.shape[0]),
        "dropped_rows": int(raw_df.shape[0] - features.shape[0]),
        "feature_columns": list(features.columns),
        "feature_source_columns": resolved_features,
        "label_source": label_source,
        "class_distribution": labels.value_counts().to_dict(),
    }

    return features, labels, metadata


def split_training_data(
    features: pd.DataFrame,
    labels: pd.Series,
    test_size: float,
    random_state: int,
) -> tuple[pd.DataFrame, pd.DataFrame, pd.Series, pd.Series]:
    stratify = labels if labels.value_counts().min() >= 2 else None
    return train_test_split(
        features,
        labels,
        test_size=test_size,
        random_state=random_state,
        stratify=stratify,
    )
