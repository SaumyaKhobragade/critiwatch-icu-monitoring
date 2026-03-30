from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_predict_risk_endpoint() -> None:
    payload = {
        "patient_id": "P001",
        "heart_rate": 130,
        "spo2": 88,
        "systolic_bp": 85,
        "diastolic_bp": 55,
        "respiratory_rate": 32,
        "temperature": 101.0,
    }
    response = client.post("/predict/risk", json=payload)
    assert response.status_code == 200

    data = response.json()
    assert data["risk_level"] in {"Stable", "Warning", "Critical"}
    assert isinstance(data["risk_score"], (int, float))
    assert isinstance(data["summary"], str)
    assert isinstance(data["factors"], list)
    assert data["engine_type"] == "rule-based"
    assert data["model_version"] == "v1"


def test_predict_endpoint_validation_rejects_invalid_spo2() -> None:
    payload = {
        "patient_id": "P001",
        "heart_rate": 95,
        "spo2": 120,
        "systolic_bp": 120,
        "diastolic_bp": 80,
        "respiratory_rate": 18,
        "temperature": 37.0,
    }
    response = client.post("/predict", json=payload)
    assert response.status_code == 422
