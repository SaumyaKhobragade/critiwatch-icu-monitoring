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

## Structure notes

The project already includes additional modules for demo APIs (`app/api`, `app/db`, `app/models`).
The new backend skeleton for the prediction foundation lives in:

- `app/routes` (health, model info, predict)
- `app/schemas/predict.py`
- `app/services/risk_rules.py`

This keeps the initial rule-based engine modular and easy to replace with ML later.
