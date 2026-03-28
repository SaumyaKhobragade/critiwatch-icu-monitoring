from fastapi import APIRouter

from app.schemas.vital_schema import VitalReading
from app.services.simulator_service import generate_vital_reading

router = APIRouter()


@router.get("/{patient_id}/latest", response_model=VitalReading)
def latest_vitals(patient_id: str) -> VitalReading:
    return generate_vital_reading(patient_id)
