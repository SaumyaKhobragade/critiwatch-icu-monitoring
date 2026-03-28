from datetime import UTC, datetime

from app.schemas.alert_schema import AlertItem


def list_alerts() -> list[AlertItem]:
    return [
        AlertItem(
            alert_id="ALT-001",
            patient_id="P002",
            level="MEDIUM",
            message="SpO2 dropped below 92% in last reading",
            timestamp=datetime.now(UTC).isoformat(),
        )
    ]
