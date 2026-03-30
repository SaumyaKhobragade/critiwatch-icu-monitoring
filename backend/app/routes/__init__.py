from app.routes.health import router as health_router
from app.routes.model_info import router as model_info_router
from app.routes.predict import router as predict_router
from app.routes.simulate import router as simulate_router

__all__ = ["health_router", "model_info_router", "predict_router", "simulate_router"]
