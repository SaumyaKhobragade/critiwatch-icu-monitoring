from fastapi import APIRouter

router = APIRouter(tags=["model-info"])


@router.get("/model-info")
def get_model_info() -> dict:
    return {
        "engine_type": "rule-based",
        "model_version": "v1",
        "features_used": [
            "heart_rate",
            "spo2",
            "systolic_bp",
            "diastolic_bp",
            "respiratory_rate",
            "temperature",
        ],
        "current_mode": "local_rule_engine",
        "planned_ml_model": "logistic_regression",
        "notes": "Prediction currently uses deterministic ICU-friendly threshold rules.",
    }
