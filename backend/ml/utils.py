from __future__ import annotations

import json
from datetime import UTC, datetime
from pathlib import Path
from typing import Any

import numpy as np
import pandas as pd


def backend_root() -> Path:
    return Path(__file__).resolve().parents[1]


def ensure_directory(path: Path | str) -> Path:
    directory = Path(path)
    directory.mkdir(parents=True, exist_ok=True)
    return directory


def now_utc_iso() -> str:
    return datetime.now(UTC).replace(microsecond=0).isoformat()


def to_builtin(value: Any) -> Any:
    if isinstance(value, dict):
        return {str(k): to_builtin(v) for k, v in value.items()}
    if isinstance(value, list):
        return [to_builtin(item) for item in value]
    if isinstance(value, tuple):
        return [to_builtin(item) for item in value]
    if isinstance(value, np.generic):
        return value.item()
    return value


def load_json_file(path: Path | str) -> dict[str, Any]:
    with Path(path).open("r", encoding="utf-8") as handle:
        return json.load(handle)


def save_json_file(path: Path | str, payload: dict[str, Any]) -> None:
    output_path = Path(path)
    ensure_directory(output_path.parent)
    with output_path.open("w", encoding="utf-8") as handle:
        json.dump(to_builtin(payload), handle, indent=2)


def generate_synthetic_dataset(rows: int = 1200, random_state: int = 42) -> pd.DataFrame:
    rng = np.random.default_rng(random_state)
    profiles = rng.choice(["stable", "warning", "critical"], size=rows, p=[0.55, 0.30, 0.15])

    samples: list[dict[str, Any]] = []
    for profile in profiles:
        if profile == "stable":
            sample = {
                "heart_rate": int(rng.integers(65, 96)),
                "spo2": int(rng.integers(95, 101)),
                "systolic_bp": int(rng.integers(110, 130)),
                "diastolic_bp": int(rng.integers(70, 85)),
                "respiratory_rate": int(rng.integers(12, 21)),
                "temperature": round(float(rng.uniform(97.0, 99.4)), 1),
                "risk_level": "Stable",
            }
        elif profile == "warning":
            sample = {
                "heart_rate": int(rng.integers(121, 141)),
                "spo2": int(rng.integers(90, 95)),
                "systolic_bp": int(rng.integers(92, 109)),
                "diastolic_bp": int(rng.integers(60, 75)),
                "respiratory_rate": int(rng.integers(25, 33)),
                "temperature": round(float(rng.uniform(100.4, 101.8)), 1),
                "risk_level": "Warning",
            }
        else:
            sample = {
                "heart_rate": int(rng.integers(130, 166)),
                "spo2": int(rng.integers(82, 90)),
                "systolic_bp": int(rng.integers(70, 90)),
                "diastolic_bp": int(rng.integers(45, 59)),
                "respiratory_rate": int(rng.integers(30, 43)),
                "temperature": round(float(rng.uniform(101.9, 104.0)), 1),
                "risk_level": "Critical",
            }
        samples.append(sample)

    return pd.DataFrame(samples)


def load_dataset(data_path: Path | str, synthetic_rows: int, random_state: int) -> tuple[pd.DataFrame, str]:
    csv_path = Path(data_path)
    if csv_path.exists():
        frame = pd.read_csv(csv_path)
        return frame, "csv"

    frame = generate_synthetic_dataset(rows=synthetic_rows, random_state=random_state)
    return frame, "synthetic"
