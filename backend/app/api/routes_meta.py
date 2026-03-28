from fastapi import APIRouter

router = APIRouter(tags=["meta"])


@router.get("/")
def root() -> dict:
    return {"service": "critiwatch-backend", "status": "ok"}


@router.get("/health")
def health() -> dict:
    return {"status": "healthy"}
