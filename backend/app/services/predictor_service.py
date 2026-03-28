from app.core.constants import RISK_HIGH, RISK_LOW, RISK_MEDIUM
from app.schemas.predict_schema import PredictRequest, PredictResponse


def predict_risk(payload: PredictRequest) -> PredictResponse:
    high_flags = 0

    if payload.spo2 < 90:
        high_flags += 1
    if payload.heart_rate > 120 or payload.heart_rate < 45:
        high_flags += 1
    if payload.systolic_bp < 90 or payload.systolic_bp > 180:
        high_flags += 1
    if payload.respiratory_rate > 30 or payload.respiratory_rate < 8:
        high_flags += 1
    if payload.temperature >= 102.0:
        high_flags += 1

    if high_flags >= 2:
        return PredictResponse(
            patient_id=payload.patient_id,
            risk_level=RISK_HIGH,
            score=0.9,
            reason="Multiple vital signs outside safe range",
        )

    if high_flags == 1:
        return PredictResponse(
            patient_id=payload.patient_id,
            risk_level=RISK_MEDIUM,
            score=0.6,
            reason="One critical vital sign threshold crossed",
        )

    return PredictResponse(
        patient_id=payload.patient_id,
        risk_level=RISK_LOW,
        score=0.2,
        reason="Vitals currently in normal range",
    )
