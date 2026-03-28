from pydantic import BaseModel


class PredictRequest(BaseModel):
    patient_id: str
    heart_rate: int
    spo2: int
    systolic_bp: int
    respiratory_rate: int
    temperature: float


class PredictResponse(BaseModel):
    patient_id: str
    risk_level: str
    score: float
    reason: str
