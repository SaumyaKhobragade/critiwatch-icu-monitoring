from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_predict_risk_endpoint() -> None:
    payload = {
        "patient_id": "P001",
        "heart_rate": 130,
        "spo2": 88,
        "systolic_bp": 85,
        "respiratory_rate": 32,
        "temperature": 101.0,
    }
    response = client.post("/predict/risk", json=payload)
    assert response.status_code == 200
    assert response.json()["risk_level"] in {"LOW", "MEDIUM", "HIGH"}
