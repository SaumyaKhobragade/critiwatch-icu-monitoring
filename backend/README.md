# CritiWatch Backend

FastAPI backend scaffold for the CritiWatch ICU monitoring simulation.

## Run API locally

```bash
cd backend
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Key API endpoints

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

## Simulate-vitals request example

```json
{
  "patient_id": "P001",
  "mode": "warning"
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

## ML pipeline (baseline training)

Training files are in `backend/ml/`.

- `ml/train.py`: end-to-end training entrypoint
- `ml/preprocess.py`: column mapping, cleaning, label strategy, feature engineering
- `ml/evaluate.py`: metrics and confusion-matrix evaluation
- `ml/utils.py`: dataset loading, synthetic fallback, JSON helpers
- `ml/artifacts/`: saved model and reports

Run training:

```bash
cd backend
python ml/train.py --data-path ml/data/training_dataset.csv
```

If the CSV path does not exist, the trainer automatically uses a synthetic ICU-style dataset so you can still verify the pipeline.

Optional column mapping file example (`ml/data/column_map.json`):

```json
{
  "heart_rate": "HR",
  "spo2": "SpO2",
  "systolic_bp": "SBP",
  "diastolic_bp": "DBP",
  "respiratory_rate": "RespRate",
  "temperature": "Temp"
}
```

Then run:

```bash
python ml/train.py --data-path ml/data/your_dataset.csv --column-map-path ml/data/column_map.json
```
