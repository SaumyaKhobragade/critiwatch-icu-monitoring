from fastapi import APIRouter

router = APIRouter(tags=["model-info"])


@router.get("/model-info")
def get_model_info() -> dict:
    return {
        "engine_type": "rule-based",
        "model_version": "v1",
        "planned_ml_model": "logistic_regression",
        "features_used": [
            "heart_rate",
            "spo2",
            "systolic_bp",
            "diastolic_bp",
            "respiratory_rate",
            "temperature",
        ],
    }
