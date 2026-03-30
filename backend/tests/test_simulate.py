from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_simulate_vitals_stable_mode() -> None:
    response = client.post("/simulate-vitals", json={"patient_id": "P001", "mode": "stable"})
    assert response.status_code == 200

    data = response.json()
    assert data["patient_id"] == "P001"
    assert data["expected_risk_level"] == "Stable"
    assert 95 <= data["spo2"] <= 100
    assert "summary" in data


def test_simulate_vitals_warning_mode() -> None:
    response = client.post("/simulate-vitals", json={"patient_id": "P001", "mode": "warning"})
    assert response.status_code == 200
    assert response.json()["expected_risk_level"] == "Warning"


def test_simulate_vitals_critical_mode() -> None:
    response = client.post("/simulate-vitals", json={"patient_id": "P001", "mode": "critical"})
    assert response.status_code == 200
    assert response.json()["expected_risk_level"] == "Critical"


def test_simulate_vitals_rejects_unsupported_mode() -> None:
    response = client.post("/simulate-vitals", json={"patient_id": "P001", "mode": "unsafe"})
    assert response.status_code == 400

    body = response.json()
    assert body["error"]["code"] == "bad_request"
    assert "Unsupported simulation mode" in body["error"]["message"]
