from app.schemas.patient_schema import Patient

_PATIENTS = [
    Patient(id="P001", name="Arjun Mehta", age=62, bed="ICU-01", status="stable"),
    Patient(id="P002", name="Neha Sharma", age=54, bed="ICU-02", status="warning"),
    Patient(id="P003", name="Ravi Kumar", age=47, bed="ICU-03", status="critical"),
]


def list_patients() -> list[Patient]:
    return _PATIENTS


def get_patient_by_id(patient_id: str) -> Patient | None:
    for patient in _PATIENTS:
        if patient.id == patient_id:
            return patient
    return None
