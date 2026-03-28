from pydantic import BaseModel


class Settings(BaseModel):
    app_name: str = "CritiWatch Backend"
    env: str = "development"
    host: str = "127.0.0.1"
    port: int = 8000


settings = Settings()
