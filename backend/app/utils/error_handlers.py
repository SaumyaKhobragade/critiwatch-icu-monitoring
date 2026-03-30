from fastapi import FastAPI, HTTPException
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse


def _error_payload(code: str, message: str, details: list[dict] | None = None) -> dict:
    return {
        "error": {
            "code": code,
            "message": message,
            "details": details or [],
        }
    }


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(_, exc: RequestValidationError) -> JSONResponse:
        details = [
            {
                "field": ".".join(str(part) for part in err.get("loc", [])),
                "message": err.get("msg", "Invalid value"),
                "type": err.get("type", "validation_error"),
            }
            for err in exc.errors()
        ]
        return JSONResponse(
            status_code=422,
            content=_error_payload(
                code="validation_error",
                message="Request validation failed",
                details=details,
            ),
        )

    @app.exception_handler(HTTPException)
    async def http_exception_handler(_, exc: HTTPException) -> JSONResponse:
        detail_payload: list[dict] = []
        if isinstance(exc.detail, dict):
            detail_payload = [exc.detail]
            message = str(exc.detail.get("message", "Request failed"))
        elif isinstance(exc.detail, list):
            detail_payload = [{"message": str(item)} for item in exc.detail]
            message = "Request failed"
        else:
            message = str(exc.detail)
            detail_payload = [{"message": message}] if message else []

        return JSONResponse(
            status_code=exc.status_code,
            content=_error_payload(
                code="bad_request" if exc.status_code < 500 else "server_error",
                message=message or "Request failed",
                details=detail_payload,
            ),
        )
