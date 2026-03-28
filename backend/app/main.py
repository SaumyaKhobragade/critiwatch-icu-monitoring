from fastapi import FastAPI

from app.api.routes_alerts import router as alerts_router
from app.api.routes_auth import router as auth_router
from app.api.routes_meta import router as meta_router
from app.api.routes_patients import router as patients_router
from app.api.routes_predict import router as predict_router
from app.api.routes_vitals import router as vitals_router


app = FastAPI(title="CritiWatch Backend", version="0.1.0")

app.include_router(meta_router)
app.include_router(auth_router, prefix="/auth", tags=["auth"])
app.include_router(patients_router, prefix="/patients", tags=["patients"])
app.include_router(vitals_router, prefix="/vitals", tags=["vitals"])
app.include_router(predict_router, prefix="/predict", tags=["predict"])
app.include_router(alerts_router, prefix="/alerts", tags=["alerts"])
