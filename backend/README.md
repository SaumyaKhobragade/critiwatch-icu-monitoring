# CritiWatch Backend

FastAPI backend scaffold for the CritiWatch ICU monitoring simulation.

## Run

```bash
cd backend
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Structure

- `app/api`: route modules
- `app/services`: business logic
- `app/schemas`: request/response schemas
- `app/models`: domain model stubs
- `app/db`: persistence helpers
- `app/utils`: shared utility helpers
- `data`: demo input data and replay scenarios
- `model_artifacts`: trained model outputs (ignored in git)
- `tests`: backend test stubs
