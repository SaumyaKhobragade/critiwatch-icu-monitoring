from typing import Optional

from pydantic import BaseModel, Field, field_validator


class SimulateVitalsRequest(BaseModel):
    patient_id: Optional[str] = Field(default=None, description="Optional patient identifier")
    mode: str = Field(
        default="random",
        description="Simulation mode: stable, warning, critical, random",
    )

    @field_validator("mode")
    @classmethod
    def normalize_mode(cls, value: str) -> str:
        normalized = (value or "").strip().lower()
        if not normalized:
            raise ValueError("mode is required")
        return normalized


class SimulateVitalsResponse(BaseModel):
    patient_id: Optional[str] = None
    heart_rate: int
    spo2: int
    systolic_bp: int
    diastolic_bp: int
    respiratory_rate: int
    temperature: float
    expected_risk_level: str
    summary: str
