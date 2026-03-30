from __future__ import annotations

import random
from datetime import UTC, datetime

from app.schemas.predict import PredictRequest
from app.schemas.simulate import SimulateVitalsRequest, SimulateVitalsResponse
from app.services.risk_rules import run_rule_based_prediction

SUPPORTED_MODES = ("stable", "warning", "critical", "random")

MODE_SUMMARY = {
    "stable": "Generated stable baseline vitals for routine monitoring tests.",
    "warning": "Generated warning-pattern vitals for moderate deterioration simulation.",
    "critical": "Generated critical-pattern vitals for emergency deterioration simulation.",
}


class UnsupportedSimulationModeError(ValueError):
    pass


def _rng_for(payload: SimulateVitalsRequest) -> random.Random:
    # Hour-level seed keeps demo runs predictable but not static forever.
    hour_seed = datetime.now(UTC).strftime("%Y%m%d%H")
    seed = f"{payload.patient_id or 'demo'}:{payload.mode}:{hour_seed}"
    return random.Random(seed)


def _generate_vitals(mode: str, rng: random.Random) -> tuple[int, int, int, int, int, float]:
    if mode == "stable":
        return (
            rng.randint(68, 95),
            rng.randint(95, 100),
            rng.randint(110, 128),
            rng.randint(70, 84),
            rng.randint(12, 20),
            round(rng.uniform(97.0, 99.4), 1),
        )

    if mode == "warning":
        return (
            rng.randint(121, 138),
            rng.randint(90, 94),
            rng.randint(92, 108),
            rng.randint(60, 74),
            rng.randint(25, 32),
            round(rng.uniform(100.4, 101.8), 1),
        )

    if mode == "critical":
        return (
            rng.randint(130, 165),
            rng.randint(82, 89),
            rng.randint(70, 89),
            rng.randint(45, 58),
            rng.randint(30, 42),
            round(rng.uniform(101.9, 104.0), 1),
        )

    raise UnsupportedSimulationModeError(mode)


def simulate_vitals(payload: SimulateVitalsRequest) -> SimulateVitalsResponse:
    if payload.mode not in SUPPORTED_MODES:
        raise UnsupportedSimulationModeError(payload.mode)

    rng = _rng_for(payload)
    target_mode = payload.mode
    if target_mode == "random":
        target_mode = rng.choice(("stable", "warning", "critical"))

    heart_rate, spo2, systolic_bp, diastolic_bp, respiratory_rate, temperature = _generate_vitals(
        target_mode,
        rng,
    )

    prediction = run_rule_based_prediction(
        PredictRequest(
            patient_id=payload.patient_id,
            heart_rate=heart_rate,
            spo2=spo2,
            systolic_bp=systolic_bp,
            diastolic_bp=diastolic_bp,
            respiratory_rate=respiratory_rate,
            temperature=temperature,
        )
    )

    return SimulateVitalsResponse(
        patient_id=payload.patient_id,
        heart_rate=heart_rate,
        spo2=spo2,
        systolic_bp=systolic_bp,
        diastolic_bp=diastolic_bp,
        respiratory_rate=respiratory_rate,
        temperature=temperature,
        expected_risk_level=prediction.risk_level,
        summary=MODE_SUMMARY[target_mode],
    )
