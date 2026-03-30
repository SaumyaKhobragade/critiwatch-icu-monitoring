from __future__ import annotations

from app.schemas.predict import PredictRequest, PredictResponse

ENGINE_TYPE = "rule-based"
MODEL_VERSION = "v1"


def _to_celsius(temperature: float) -> tuple[float, str]:
    if temperature > 45.0:
        celsius = (temperature - 32.0) * (5.0 / 9.0)
        return celsius, "F"
    return temperature, "C"


def run_rule_based_prediction(payload: PredictRequest) -> PredictResponse:
    score = 0.0
    factors: list[str] = []
    critical_hits = 0
    warning_hits = 0

    if payload.spo2 < 90:
        score += 35.0
        critical_hits += 1
        factors.append(f"Low SpO2 detected ({payload.spo2}%)")

    if payload.systolic_bp < 90:
        score += 35.0
        critical_hits += 1
        factors.append(
            f"Low blood pressure detected ({payload.systolic_bp}/{payload.diastolic_bp} mmHg)"
        )

    if payload.heart_rate > 120:
        score += 20.0
        warning_hits += 1
        factors.append(f"High heart rate detected ({payload.heart_rate} bpm)")

    if payload.respiratory_rate > 24:
        score += 15.0
        warning_hits += 1
        factors.append(f"High respiratory rate detected ({payload.respiratory_rate}/min)")

    temp_c, source_unit = _to_celsius(payload.temperature)
    if temp_c >= 38.0:
        score += 15.0
        warning_hits += 1
        if source_unit == "F":
            factors.append(
                f"Elevated temperature detected ({payload.temperature:.1f} F / {temp_c:.1f} C)"
            )
        else:
            factors.append(f"Elevated temperature detected ({temp_c:.1f} C)")

    if payload.diastolic_bp < 60:
        score += 10.0
        warning_hits += 1
        factors.append(f"Low diastolic pressure detected ({payload.diastolic_bp} mmHg)")

    abnormal_count = critical_hits + warning_hits
    if abnormal_count >= 3:
        score += 10.0
        factors.append("Multiple abnormal vitals detected simultaneously")

    score = min(100.0, round(score, 1))

    if score >= 70.0 or critical_hits >= 2:
        risk_level = "Critical"
    elif score >= 30.0 or critical_hits >= 1 or warning_hits >= 2:
        risk_level = "Warning"
    else:
        risk_level = "Stable"

    if not factors:
        factors = ["All monitored vitals are within expected safe ranges"]

    if risk_level == "Critical":
        summary = "Critical deterioration risk detected. Immediate clinical review is recommended."
    elif risk_level == "Warning":
        summary = "Warning-level deterioration risk detected. Increase monitoring frequency."
    else:
        summary = "Patient appears stable based on current rule-based vital assessment."

    return PredictResponse(
        patient_id=payload.patient_id,
        risk_level=risk_level,
        risk_score=score,
        summary=summary,
        factors=factors,
        engine_type=ENGINE_TYPE,
        model_version=MODEL_VERSION,
    )
