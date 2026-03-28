from dataclasses import dataclass


@dataclass
class PatientModel:
    id: str
    name: str
    age: int
    bed: str
    status: str
