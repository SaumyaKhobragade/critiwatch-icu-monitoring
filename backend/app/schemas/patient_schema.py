from pydantic import BaseModel


class Patient(BaseModel):
    id: str
    name: str
    age: int
    bed: str
    status: str
