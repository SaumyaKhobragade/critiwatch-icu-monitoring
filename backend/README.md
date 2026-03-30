# CritiWatch Backend

FastAPI backend scaffold for the CritiWatch ICU monitoring simulation.

## Run locally

```bash
cd backend
python -m venv .venv
.venv\\Scripts\\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Key endpoints

- `GET /health`
- `GET /model-info`
- `POST /predict`
- `POST /predict/risk` (backward-compatible alias)
- `POST /simulate-vitals`

## Predict request example

```json
{
  "patient_id": "P001",
  "heart_rate": 128,
  "spo2": 88,
  "systolic_bp": 86,
  "diastolic_bp": 55,
  "respiratory_rate": 30,
  "temperature": 101.2
}
```

## Predict response example

```json
{
  "patient_id": "P001",
  "risk_level": "Critical",
  "risk_score": 95.0,
  "summary": "Critical deterioration risk detected. Immediate clinical review is recommended.",
  "factors": [
    "Low SpO2 detected (88%)",
    "Low blood pressure detected (86/55 mmHg)",
    "High heart rate detected (128 bpm)",
    "High respiratory rate detected (30/min)",
    "Elevated temperature detected (101.2 F / 38.4 C)",
    "Low diastolic pressure detected (55 mmHg)",
    "Multiple abnormal vitals detected simultaneously"
  ],
  "engine_type": "rule-based",
  "model_version": "v1"
}
```

## Simulate-vitals request example

```json
{
  "patient_id": "P001",
  "mode": "warning"
}
```

## Simulate-vitals response example

```json
{
  "patient_id": "P001",
  "heart_rate": 119,
  "spo2": 92,
  "systolic_bp": 97,
  "diastolic_bp": 66,
  "respiratory_rate": 27,
  "temperature": 100.7,
  "expected_risk_level": "Warning",
  "summary": "Generated warning-pattern vitals for moderate deterioration simulation."
}
```

## Validation error shape

```json
{
  "error": {
    "code": "validation_error",
    "message": "Request validation failed",
    "details": [
      {
        "field": "body.spo2",
        "message": "Input should be less than or equal to 100",
        "type": "less_than_equal"
      }
    ]
  }
}
```

## Structure notes

The project already includes additional modules for demo APIs (`app/api`, `app/db`, `app/models`).
The prediction/simulation foundation lives in:

- `app/routes` (health, model info, predict, simulate)
- `app/schemas/predict.py`
- `app/schemas/simulate.py`
- `app/services/risk_rules.py`
- `app/services/simulation_service.py`
- `app/utils/error_handlers.py`

This keeps the current rule engine modular and ready for ML replacement later.
