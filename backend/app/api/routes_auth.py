from fastapi import APIRouter, HTTPException

from app.schemas.auth_schema import LoginRequest, LoginResponse
from app.services.auth_service import login_user

router = APIRouter()


@router.post("/login", response_model=LoginResponse)
def login(payload: LoginRequest) -> LoginResponse:
    result = login_user(payload.username, payload.password)
    if result is None:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    return result
