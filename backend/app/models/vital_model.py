from dataclasses import dataclass


@dataclass
class VitalModel:
    patient_id: str
    heart_rate: int
    spo2: int
    systolic_bp: int
    diastolic_bp: int
    respiratory_rate: int
    temperature: float
    timestamp: str
