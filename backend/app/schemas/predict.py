from typing import Optional

from pydantic import BaseModel, Field, model_validator


class PredictRequest(BaseModel):
    patient_id: Optional[str] = Field(default=None, description="Optional patient identifier")
    heart_rate: int = Field(..., ge=20, le=250, description="Heart rate in beats per minute")
    spo2: int = Field(..., ge=0, le=100, description="Oxygen saturation percentage")
    systolic_bp: int = Field(..., ge=40, le=260, description="Systolic blood pressure in mmHg")
    diastolic_bp: int = Field(..., ge=20, le=180, description="Diastolic blood pressure in mmHg")
    respiratory_rate: int = Field(..., ge=5, le=80, description="Respiratory rate per minute")
    temperature: float = Field(
        ...,
        ge=25.0,
        le=110.0,
        description="Body temperature (Celsius or Fahrenheit)",
    )

    @model_validator(mode="after")
    def validate_pressure_pair(self) -> "PredictRequest":
        if self.diastolic_bp > self.systolic_bp:
            raise ValueError("diastolic_bp cannot be greater than systolic_bp")
        return self


class PredictResponse(BaseModel):
    patient_id: Optional[str] = None
    risk_level: str
    risk_score: float
    summary: str
    factors: list[str]
    engine_type: str
    model_version: str
