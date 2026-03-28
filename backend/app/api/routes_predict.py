from fastapi import APIRouter

from app.schemas.predict_schema import PredictRequest, PredictResponse
from app.services.predictor_service import predict_risk

router = APIRouter()


@router.post("/risk", response_model=PredictResponse)
def predict(payload: PredictRequest) -> PredictResponse:
    return predict_risk(payload)
