from fastapi import APIRouter, HTTPException

from app.schemas.patient_schema import Patient
from app.services.patient_service import get_patient_by_id, list_patients

router = APIRouter()


@router.get("", response_model=list[Patient])
def get_patients() -> list[Patient]:
    return list_patients()


@router.get("/{patient_id}", response_model=Patient)
def get_patient(patient_id: str) -> Patient:
    patient = get_patient_by_id(patient_id)
    if patient is None:
        raise HTTPException(status_code=404, detail="Patient not found")
    return patient
