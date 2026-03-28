from pydantic import BaseModel


class AlertItem(BaseModel):
    alert_id: str
    patient_id: str
    level: str
    message: str
    timestamp: str
