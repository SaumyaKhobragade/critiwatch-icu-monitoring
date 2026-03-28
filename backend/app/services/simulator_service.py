from datetime import UTC, datetime

from app.schemas.vital_schema import VitalReading


def generate_vital_reading(patient_id: str) -> VitalReading:
    return VitalReading(
        patient_id=patient_id,
        heart_rate=92,
        spo2=96,
        systolic_bp=122,
        diastolic_bp=79,
        respiratory_rate=18,
        temperature=98.6,
        timestamp=datetime.now(UTC).isoformat(),
    )
