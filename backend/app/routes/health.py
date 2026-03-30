from fastapi import APIRouter

router = APIRouter(tags=["health"])


@router.get("/health")
def get_health() -> dict:
    return {
        "status": "ok",
        "message": "CritiWatch backend is running",
        "version": "0.3.0",
    }
