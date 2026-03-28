# API Spec (Draft)

## Base URL
- Local emulator: `http://10.0.2.2:8000`
- Local machine: `http://127.0.0.1:8000`

## Planned Endpoints
- `GET /health`
- `POST /auth/login`
- `GET /patients`
- `GET /patients/{patient_id}/vitals/latest`
- `POST /predict/risk`
- `GET /alerts`
