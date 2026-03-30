from fastapi import APIRouter

from app.schemas.predict import PredictRequest, PredictResponse
from app.services.risk_rules import run_rule_based_prediction

router = APIRouter(tags=["predict"])


@router.post("/predict", response_model=PredictResponse)
def predict(payload: PredictRequest) -> PredictResponse:
    return run_rule_based_prediction(payload)


# Backward-compatible alias for existing frontend/tests.
@router.post("/predict/risk", response_model=PredictResponse)
def predict_risk(payload: PredictRequest) -> PredictResponse:
    return run_rule_based_prediction(payload)
