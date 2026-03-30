from fastapi import FastAPI

from app.api.routes_alerts import router as alerts_router
from app.api.routes_auth import router as auth_router
from app.api.routes_patients import router as patients_router
from app.api.routes_vitals import router as vitals_router
from app.routes.health import router as health_router
from app.routes.model_info import router as model_info_router
from app.routes.predict import router as predict_router

app = FastAPI(
    title="CritiWatch Backend",
    version="0.2.0",
    description="Rule-based ICU deterioration prediction API scaffold.",
)

app.include_router(health_router)
app.include_router(model_info_router)
app.include_router(predict_router)

# Existing non-ML demo routes retained for compatibility with current app flows.
app.include_router(auth_router, prefix="/auth", tags=["auth"])
app.include_router(patients_router, prefix="/patients", tags=["patients"])
app.include_router(vitals_router, prefix="/vitals", tags=["vitals"])
app.include_router(alerts_router, prefix="/alerts", tags=["alerts"])
