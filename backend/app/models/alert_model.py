from dataclasses import dataclass


@dataclass
class AlertModel:
    alert_id: str
    patient_id: str
    level: str
    message: str
    timestamp: str
