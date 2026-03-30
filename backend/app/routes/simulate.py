from fastapi import APIRouter, HTTPException

from app.schemas.simulate import SimulateVitalsRequest, SimulateVitalsResponse
from app.services.simulation_service import (
    SUPPORTED_MODES,
    UnsupportedSimulationModeError,
    simulate_vitals,
)

router = APIRouter(tags=["simulate-vitals"])


@router.post("/simulate-vitals", response_model=SimulateVitalsResponse)
def simulate(payload: SimulateVitalsRequest) -> SimulateVitalsResponse:
    try:
        return simulate_vitals(payload)
    except UnsupportedSimulationModeError:
        raise HTTPException(
            status_code=400,
            detail={
                "message": f"Unsupported simulation mode: {payload.mode}",
                "supported_modes": list(SUPPORTED_MODES),
            },
        )
