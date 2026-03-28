from app.schemas.auth_schema import LoginResponse

_DEMO_USERS = {
    "doctor": {"password": "doctor123", "role": "doctor"},
    "nurse": {"password": "nurse123", "role": "nurse"},
    "intern": {"password": "intern123", "role": "intern"},
}


def login_user(username: str, password: str) -> LoginResponse | None:
    user = _DEMO_USERS.get(username)
    if user is None or user["password"] != password:
        return None
    return LoginResponse(token=f"demo-token-{username}", role=user["role"])
