# CritiWatch

## ICU Vital Monitoring and Deterioration Prediction Platform

CritiWatch is a mobile-based ICU monitoring simulation system built as an Android Studio lab project. It combines an **Android app written in Java** with a **Python FastAPI backend** to simulate ICU patient monitoring, visualize vital signs, store local patient history, and generate deterioration-risk predictions.

> This project is a **healthcare simulation and decision-support prototype** for academic/demo purposes. It is **not a medical device**, does not replace clinicians, and does not directly connect to real ICU hardware in the current version.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Project Goals](#project-goals)
- [Key Features](#key-features)
- [Lab Requirement Coverage](#lab-requirement-coverage)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Application Flow](#application-flow)
- [Pages / Screens](#pages--screens)
- [Prediction / ML Design](#prediction--ml-design)
- [Data Sources and Demo Modes](#data-sources-and-demo-modes)
- [Database Design](#database-design)
- [Backend API Design](#backend-api-design)
- [Project Structure](#project-structure)
- [Installation and Setup](#installation-and-setup)
- [Build and Run Guide](#build-and-run-guide)
- [Future Enhancements](#future-enhancements)
- [Resume Value](#resume-value)
- [Suggested GitHub Topics](#suggested-github-topics)
- [License / Academic Note](#license--academic-note)

---

## Overview

CritiWatch is designed to simulate the mobile monitoring of ICU patients and provide early warning support through backend-based prediction. The project demonstrates the following in one integrated system:

- multi-screen Android development in Java
- REST API communication with FastAPI
- local session handling with SharedPreferences
- local persistent storage with SQLite
- chart-based vital sign visualization
- event-driven alerts and notifications
- mock data simulation and dataset replay
- hybrid risk prediction using threshold rules and optional ML

The main idea is simple: the Android application displays patient vitals and history, while the backend evaluates the readings and returns a risk score or risk category such as **Low**, **Medium**, or **High**.

---

## Problem Statement

ICU patient monitoring traditionally relies on bedside monitors and hospital infrastructure. For an academic project, connecting directly to real machines is impractical. CritiWatch solves this by simulating the monitoring workflow on a mobile device while still preserving the important software concepts:

- viewing multiple patient records
- inspecting live and historical vitals
- spotting abnormal trends
- receiving deterioration alerts
- using backend intelligence to summarize risk

This makes the project technically rich, feasible to build, and suitable for academic evaluation and resume presentation.

---

## Project Goals

### Primary Goals

- Build a polished Android app in Java with multiple activities and real navigation flows.
- Integrate Android with a Python FastAPI backend.
- Simulate ICU patient data instead of relying on real hardware.
- Visualize time-series vital data using charts.
- Predict deterioration risk through a backend service.
- Store local records and history using SQLite.
- Fulfill all mandatory Android Studio lab requirements.

### Secondary Goals

- Create a project large enough to showcase on GitHub and a resume.
- Support both manual entry and automated mock data generation.
- Add explainable alerts instead of only raw vital numbers.
- Keep the architecture modular and extensible for future upgrades.

---

## Key Features

### Core Monitoring Features

- User login with session persistence
- Dashboard showing ICU patients and latest status
- Patient detail page with current vital signs
- Real-time simulation or periodic mock updates
- Historical graphs for heart rate, SpO2, blood pressure, respiratory rate, and temperature
- Risk prediction from backend
- Alert notification for critical conditions
- Alert history page
- Filter, sort, and patient selection options

### Backend Features

- Vital sign simulation endpoints
- Risk prediction endpoint
- Threshold-based anomaly detection
- Dataset replay support
- Optional model serving using scikit-learn
- JSON-based API response design

### Local Storage Features

- SQLite tables for patients, vitals, predictions, alerts, and optional notes
- SharedPreferences for session and settings
- Cached readings for offline review

### UI / UX Features

- Toast messages for quick feedback
- Dialog boxes for confirmations and warnings
- Notification click redirect using Intent extras
- Date picker and time picker for history filtering
- Spinner-based filtering and time-range selection
- ConstraintLayout-based responsive screens

---

## Lab Requirement Coverage

The project is intentionally mapped to common Android lab requirements.

| Lab Requirement               | Where It Is Used                                                                              |
| ----------------------------- | --------------------------------------------------------------------------------------------- |
| Constraint Layout             | All major screens use `ConstraintLayout` as base layout                                     |
| Event Handlers                | Button clicks, item selection, form validation, dialog actions, notification taps             |
| Spinner                       | Filter by ward, patient, date range preset, demo mode                                         |
| Calendar and Clock            | `DatePickerDialog` and `TimePickerDialog` for selecting history windows                   |
| Intents                       | Screen navigation, passing patient ID, redirecting from notifications                         |
| Dialog Boxes and Toasts       | Logout confirmation, deletion confirmation, abnormal reading warnings, success/error feedback |
| Notification Redirect to Page | High-risk alert notification opens Patient Detail or Alerts page                              |
| Shared Preferences            | Login state, remembered username, app settings, preferred refresh interval                    |
| SQL Database                  | SQLite used for local caching of patient info, vitals, alerts, predictions                    |

---

## Tech Stack

### Frontend

- **Language:** Java
- **IDE:** Android Studio
- **UI Layout:** ConstraintLayout
- **Networking:** Retrofit or Volley
- **Charts:** MPAndroidChart
- **Local Database:** SQLite with `SQLiteOpenHelper`
- **Session Storage:** SharedPreferences
- **Lists:** RecyclerView
- **Background Scheduling (optional):** WorkManager / Handler / Timer
- **JSON Parsing:** Gson / built-in serialization depending on networking layer

### Backend

- **Language:** Python
- **Framework:** FastAPI
- **Server:** Uvicorn
- **Data Handling:** pandas, NumPy
- **ML / Prediction:** scikit-learn
- **Validation:** Pydantic
- **Storage (optional backend-side):** CSV / JSON / SQLite / PostgreSQL for later expansion

### Development / Tooling

- Git and GitHub
- Postman or Insomnia for API testing
- Jupyter Notebook for model experimentation (optional)
- Android Emulator or physical Android device

---

## System Architecture

```text
+------------------------+        HTTP / JSON        +---------------------------+
|  Android App (Java)    |  <--------------------->  |  FastAPI Backend (Python) |
|------------------------|                            |---------------------------|
| Login / Session        |                            | Auth / Demo User Logic    |
| Patient Dashboard      |                            | Mock Data Generator       |
| Patient Detail View    |                            | Dataset Replay Service    |
| Graphs and History     |                            | Rules Engine              |
| Alerts and Notifications|                           | ML Prediction Service     |
| SQLite Local Cache     |                            | Risk Scoring Logic        |
| SharedPreferences      |                            | JSON API Endpoints        |
+------------------------+                            +---------------------------+
            |
            |
            v
+------------------------+
| Local SQLite Database  |
| Patients / Vitals      |
| Alerts / Predictions   |
+------------------------+
```

### Architectural Style

The project follows a simple layered architecture:

#### Android Layers

- **UI Layer**: Activities, adapters, layouts
- **Data Layer**: API service, repositories, DB helper
- **Utility Layer**: constants, validators, notification helper, session manager

#### Backend Layers

- **API Layer**: FastAPI route handlers
- **Service Layer**: simulation, prediction, anomaly logic
- **Model Layer**: Pydantic schemas, optional ML model files
- **Data Layer**: CSV/dataset loader and preprocessing logic

---

## Application Flow

1. User launches the app.
2. Splash screen checks whether a session exists in SharedPreferences.
3. If logged in, user goes directly to dashboard; otherwise goes to login screen.
4. Dashboard loads patient list from local cache and/or backend.
5. User taps a patient card to open patient detail view.
6. Patient detail screen displays current vitals and prediction summary.
7. User can open graph/history page and filter time using date/time pickers.
8. If vitals cross thresholds, app shows dialog/toast and creates notification.
9. Tapping the notification opens the patient detail page using Intent extras.
10. User can review alerts, change settings, or log out.

---

## Pages / Screens

### 1. Splash Screen

**Purpose:** Entry point and session check.

**Responsibilities:**

- show app logo/name
- display short loading screen
- check SharedPreferences for active session
- redirect to Login or Dashboard

### 2. Login Screen

**Purpose:** Authenticate the user.

**Components:**

- username/email field
- password field
- login button
- optional sign-up button
- remember-me checkbox (optional)

**Behaviors:**

- validate empty fields
- show Toast on invalid input
- save session using SharedPreferences
- navigate to dashboard on success

### 3. Signup Screen (Optional but recommended)

**Purpose:** Create demo user account.

**Components:**

- name
- username/email
- password
- confirm password
- role spinner

**Behaviors:**

- input validation
- insert user locally or via backend

### 4. Dashboard Screen

**Purpose:** Show overview of all monitored ICU patients.

**Components:**

- RecyclerView of patient cards
- search bar (optional)
- ward/patient filter spinner
- summary cards for stable/warning/critical counts
- refresh button

**Behaviors:**

- fetch patients from local DB and backend
- show latest vitals and risk badge
- click item to open Patient Detail

### 5. Patient Detail Screen

**Purpose:** Show complete current status for one patient.

**Components:**

- patient demographic summary
- latest heart rate, SpO2, BP, respiratory rate, temperature
- last updated time
- risk level badge
- predict button / refresh button
- buttons for Graphs, Alerts, Notes

**Behaviors:**

- fetch patient details
- call prediction endpoint
- show warning dialog for critical states
- save results locally

### 6. Graph / History Screen

**Purpose:** Show time-series trends for selected vitals.

**Components:**

- chart view using MPAndroidChart
- date picker
- time picker
- range-selection spinner
- vital-type selector spinner

**Behaviors:**

- load historical readings from SQLite / backend
- render line charts
- filter by date and time

### 7. Alerts Screen

**Purpose:** Display active and historical alerts.

**Components:**

- RecyclerView of alerts
- severity chips/tags
- clear alert button
- sort/filter spinner

**Behaviors:**

- show alert reason, time, patient name
- click alert to open patient detail
- remove or archive alert locally

### 8. Settings Screen

**Purpose:** Control preferences and app behavior.

**Components:**

- logout button
- toggles for notifications
- refresh interval spinner
- default dashboard filter
- clear local data button

**Behaviors:**

- save preferences using SharedPreferences
- show confirmation dialogs for destructive actions

### 9. Manual Data Entry / Simulation Control Screen (Recommended)

**Purpose:** Let user inject custom vitals or choose demo scenarios.

**Components:**

- form for entering vitals
- demo mode spinner
- simulate button
- save button

**Behaviors:**

- validate ranges
- insert into backend/local DB
- useful for demonstrations and testing

---

## Prediction / ML Design

### Prediction Goal

The system predicts **deterioration risk** using current or recent vital signs.

### Suggested Output

- `Low`
- `Medium`
- `High`

Or internally:

- probability score between `0.0` and `1.0`

### Recommended Model Strategy

Use a **hybrid approach**:

#### 1. Rules Engine

Immediate threshold-based logic such as:

- SpO2 < 90 => critical concern
- systolic BP < 90 => shock risk
- heart rate > 120 or < 45 => abnormal
- respiratory rate > 24 => warning
- temperature > 100.4 F => fever signal

This is useful for:

- instant alerts
- explainability
- reliable demo behavior

#### 2. ML Classifier (Optional but recommended)

Use a lightweight model trained on dataset rows or rule-derived labels.

Recommended models:

- Logistic Regression
- Decision Tree
- Random Forest

Best academic choice:

- **Logistic Regression** for interpretability
- **Random Forest** as improved optional version

### Input Features

- heart_rate
- spo2
- systolic_bp
- diastolic_bp
- respiratory_rate
- temperature
- optional derived trend features such as moving average or delta from last reading

### Example Output JSON

```json
{
  "patient_id": 101,
  "risk_probability": 0.87,
  "risk_level": "High",
  "factors": [
    "Low SpO2",
    "Low systolic BP",
    "High respiratory rate"
  ],
  "recommended_action": "Immediate clinical review suggested"
}
```

### Important Note

The model is **not** intended to diagnose disease. It only estimates whether vitals resemble a stable or deteriorating pattern.

---

## Data Sources and Demo Modes

### Recommended Data Sources

- MIMIC-III or MIMIC-IV (if accessible and manageable)
- Kaggle ICU or vital-sign datasets
- Synthetic mock vitals generated in backend

### Demo Modes

#### 1. Mock Mode

Backend generates realistic but fake vitals.

#### 2. Dataset Replay Mode

Read rows from a dataset and replay them as time-series observations.

#### 3. Manual Entry Mode

User manually enters vitals to trigger predictions and alerts.

### Why Mocking Is Important

This keeps the project feasible without connecting to real hospital machines and makes demos consistent.

---

## Database Design

The Android app stores local data using SQLite.

### Table: `users`

| Column   | Type                | Notes                       |
| -------- | ------------------- | --------------------------- |
| id       | INTEGER PRIMARY KEY | local user id               |
| name     | TEXT                | full name                   |
| username | TEXT UNIQUE         | login username              |
| password | TEXT                | demo only; hash if improved |
| role     | TEXT                | doctor/nurse/intern/admin   |

### Table: `patients`

| Column       | Type                | Notes                   |
| ------------ | ------------------- | ----------------------- |
| id           | INTEGER PRIMARY KEY | patient id              |
| name         | TEXT                | patient full name       |
| age          | INTEGER             | patient age             |
| gender       | TEXT                | patient gender          |
| bed_no       | TEXT                | bed identifier          |
| ward         | TEXT                | ICU unit name           |
| diagnosis    | TEXT                | simulated diagnosis     |
| status       | TEXT                | stable/warning/critical |
| last_updated | TEXT                | ISO timestamp           |

### Table: `vitals`

| Column           | Type                | Notes                        |
| ---------------- | ------------------- | ---------------------------- |
| id               | INTEGER PRIMARY KEY | vital row id                 |
| patient_id       | INTEGER             | FK to patient                |
| heart_rate       | REAL                | bpm                          |
| spo2             | REAL                | percentage                   |
| systolic_bp      | REAL                | mmHg                         |
| diastolic_bp     | REAL                | mmHg                         |
| respiratory_rate | REAL                | breaths/min                  |
| temperature      | REAL                | F or C, use one consistently |
| recorded_at      | TEXT                | timestamp                    |
| source           | TEXT                | mock/manual/backend          |

### Table: `predictions`

| Column           | Type                | Notes               |
| ---------------- | ------------------- | ------------------- |
| id               | INTEGER PRIMARY KEY | prediction id       |
| patient_id       | INTEGER             | FK to patient       |
| risk_probability | REAL                | numeric score       |
| risk_level       | TEXT                | low/medium/high     |
| factors          | TEXT                | JSON/string summary |
| predicted_at     | TEXT                | timestamp           |

### Table: `alerts`

| Column     | Type                | Notes                 |
| ---------- | ------------------- | --------------------- |
| id         | INTEGER PRIMARY KEY | alert id              |
| patient_id | INTEGER             | FK to patient         |
| title      | TEXT                | alert title           |
| message    | TEXT                | explanation           |
| severity   | TEXT                | warning/high/critical |
| created_at | TEXT                | timestamp             |
| is_read    | INTEGER             | 0 or 1                |

### Table: `notes` (Optional)

| Column     | Type                | Notes                       |
| ---------- | ------------------- | --------------------------- |
| id         | INTEGER PRIMARY KEY | note id                     |
| patient_id | INTEGER             | FK to patient               |
| note_text  | TEXT                | clinical note / observation |
| created_at | TEXT                | timestamp                   |

---

## Backend API Design

### Base URL

```text
http://<server-ip>:8000
```

### 1. Health Check

**GET** `/health`

Response:

```json
{ "status": "ok" }
```

### 2. Login (Optional)

**POST** `/auth/login`

Request:

```json
{
  "username": "demo_doctor",
  "password": "123456"
}
```

### 3. List Patients

**GET** `/patients`

Response:

```json
[
  {
    "id": 101,
    "name": "Rahul Sharma",
    "bed_no": "ICU-02",
    "status": "warning",
    "last_updated": "2026-03-28T10:30:00"
  }
]
```

### 4. Get Patient Details

**GET** `/patients/{patient_id}`

### 5. Get Latest Vitals

**GET** `/patients/{patient_id}/latest-vitals`

### 6. Get Vital History

**GET** `/patients/{patient_id}/history?from=...&to=...`

### 7. Predict Risk

**POST** `/predict`

Request:

```json
{
  "patient_id": 101,
  "heart_rate": 128,
  "spo2": 88,
  "systolic_bp": 85,
  "diastolic_bp": 55,
  "respiratory_rate": 30,
  "temperature": 101.2
}
```

Response:

```json
{
  "risk_probability": 0.91,
  "risk_level": "High",
  "factors": ["Low SpO2", "Low BP", "High respiratory rate"],
  "recommended_action": "Immediate review suggested"
}
```

### 8. Simulate New Reading

**POST** `/simulate/{patient_id}`

### 9. Trigger Demo Scenario

**POST** `/demo/scenario`

Possible scenarios:

- stable
- warning
- critical
- recovery

### 10. Get Alerts

**GET** `/alerts`

---

## Project Structure

Below is a recommended structure for both the Android app and the FastAPI backend.

### Android Project Structure

```text
CritiWatch/
├── android-app/
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/example/critiwatch/
│   │   │   │   │   ├── activities/
│   │   │   │   │   │   ├── SplashActivity.java
│   │   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   │   ├── SignupActivity.java
│   │   │   │   │   │   ├── DashboardActivity.java
│   │   │   │   │   │   ├── PatientDetailActivity.java
│   │   │   │   │   │   ├── GraphHistoryActivity.java
│   │   │   │   │   │   ├── AlertsActivity.java
│   │   │   │   │   │   ├── SettingsActivity.java
│   │   │   │   │   │   └── ManualEntryActivity.java
│   │   │   │   │   ├── adapters/
│   │   │   │   │   │   ├── PatientAdapter.java
│   │   │   │   │   │   ├── AlertAdapter.java
│   │   │   │   │   │   └── VitalHistoryAdapter.java
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── ApiClient.java
│   │   │   │   │   │   ├── ApiService.java
│   │   │   │   │   │   └── NetworkUtils.java
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── DatabaseHelper.java
│   │   │   │   │   │   ├── PatientDao.java
│   │   │   │   │   │   ├── VitalDao.java
│   │   │   │   │   │   ├── AlertDao.java
│   │   │   │   │   │   └── PredictionDao.java
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Patient.java
│   │   │   │   │   │   ├── VitalSign.java
│   │   │   │   │   │   ├── Prediction.java
│   │   │   │   │   │   ├── AlertItem.java
│   │   │   │   │   │   └── ApiResponse.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── PatientRepository.java
│   │   │   │   │   │   ├── AlertRepository.java
│   │   │   │   │   │   └── PredictionRepository.java
│   │   │   │   │   ├── services/
│   │   │   │   │   │   ├── NotificationHelper.java
│   │   │   │   │   │   ├── SessionManager.java
│   │   │   │   │   │   ├── ValidationService.java
│   │   │   │   │   │   └── DemoScheduler.java
│   │   │   │   │   └── utils/
│   │   │   │   │       ├── Constants.java
│   │   │   │   │       ├── DateTimeUtils.java
│   │   │   │   │       └── RiskUtils.java
│   │   │   │   ├── res/
│   │   │   │   │   ├── layout/
│   │   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   │   ├── activity_signup.xml
│   │   │   │   │   │   ├── activity_dashboard.xml
│   │   │   │   │   │   ├── activity_patient_detail.xml
│   │   │   │   │   │   ├── activity_graph_history.xml
│   │   │   │   │   │   ├── activity_alerts.xml
│   │   │   │   │   │   ├── activity_settings.xml
│   │   │   │   │   │   ├── activity_manual_entry.xml
│   │   │   │   │   │   ├── item_patient.xml
│   │   │   │   │   │   ├── item_alert.xml
│   │   │   │   │   │   └── item_vital_history.xml
│   │   │   │   │   ├── drawable/
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   ├── strings.xml
│   │   │   │   │   │   └── themes.xml
│   │   │   │   │   ├── mipmap/
│   │   │   │   │   └── menu/
│   │   │   │   └── AndroidManifest.xml
│   │   ├── build.gradle
│   │   └── proguard-rules.pro
│   ├── build.gradle
│   ├── settings.gradle
│   └── gradle.properties
│
├── backend/
│   ├── app/
│   │   ├── main.py
│   │   ├── api/
│   │   │   ├── routes_auth.py
│   │   │   ├── routes_patients.py
│   │   │   ├── routes_predict.py
│   │   │   ├── routes_simulation.py
│   │   │   └── routes_alerts.py
│   │   ├── core/
│   │   │   ├── config.py
│   │   │   └── constants.py
│   │   ├── models/
│   │   │   ├── request_models.py
│   │   │   └── response_models.py
│   │   ├── services/
│   │   │   ├── prediction_service.py
│   │   │   ├── simulation_service.py
│   │   │   ├── alert_service.py
│   │   │   ├── patient_service.py
│   │   │   └── dataset_service.py
│   │   ├── ml/
│   │   │   ├── train_model.py
│   │   │   ├── preprocess.py
│   │   │   ├── model.pkl
│   │   │   └── label_encoder.pkl
│   │   ├── data/
│   │   │   ├── sample_patients.json
│   │   │   ├── sample_vitals.csv
│   │   │   └── scenarios/
│   │   │       ├── stable.json
│   │   │       ├── warning.json
│   │   │       ├── critical.json
│   │   │       └── recovery.json
│   │   └── utils/
│   │       ├── datetime_utils.py
│   │       └── risk_rules.py
│   ├── requirements.txt
│   └── README_backend.md
│
├── docs/
│   ├── PRD.md
│   ├── API_SPEC.md
│   ├── DATABASE_SCHEMA.md
│   └── SCREENSHOTS/
│
├── assets/
│   ├── app-logo/
│   ├── icons/
│   └── demo-images/
│
├── .gitignore
├── LICENSE
└── README.md
```

### Folder Responsibility Summary

#### `activities/`

Contains Android Activities for each screen.

#### `adapters/`

RecyclerView adapters for patient cards, alerts, and history lists.

#### `api/`

Networking setup and HTTP client configuration.

#### `database/`

SQLite helper classes and DAO-style wrappers for local storage.

#### `models/`

Java model classes and API payload models.

#### `services/`

Helpers for session management, notification creation, validation, and background simulation logic.

#### `utils/`

App-wide constants and utility functions.

#### `backend/app/api/`

FastAPI route modules grouped by domain.

#### `backend/app/services/`

Business logic for prediction, simulation, and alert generation.

#### `backend/app/ml/`

Training scripts, preprocessing code, and serialized ML model artifacts.

---

## Installation and Setup

### Prerequisites

#### Android Side

- Android Studio installed
- Java SDK configured
- Android Emulator or Android phone

#### Backend Side

- Python 3.10+
- `pip` installed
- virtual environment tool

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/critiwatch-icu-monitoring.git
cd critiwatch-icu-monitoring
```

### 2. Start the FastAPI Backend

```bash
cd backend
python -m venv venv
source venv/bin/activate    # Linux / macOS
venv\Scripts\activate       # Windows
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 3. Configure Android Base URL

In your Android constants or Retrofit config, set the backend URL:

```java
public static final String BASE_URL = "http://10.0.2.2:8000/";
```

Use:

- `10.0.2.2` for Android emulator to reach localhost
- local machine IP for physical phone testing

### 4. Open Android App in Android Studio

- Open the `android-app` folder in Android Studio
- Sync Gradle
- Install dependencies
- Run on emulator or device

---

## Build and Run Guide

### Recommended Demo Sequence

1. Launch backend server.
2. Run Android app.
3. Login using demo credentials.
4. Open dashboard and select a patient.
5. Load patient details and current vitals.
6. Trigger a simulation scenario such as `critical`.
7. Request risk prediction.
8. Observe alert, notification, and patient detail redirect.
9. Open graph/history page and filter using date/time selectors.
10. Show saved alerts and local history in SQLite-backed screens.

### Suggested Demo Credentials

```text
Username: demo_doctor
Password: 123456
```

---

## Future Enhancements

- integration with IoT or biomedical sensor devices
- WebSocket-based live streaming of vitals
- stronger authentication with JWT
- Room database instead of raw SQLite helper classes
- role-based dashboards for doctor/nurse/admin
- predictive trend charts and explainability views
- cloud deployment of backend
- PDF report generation
- medication reminders and note sharing
- FHIR / HL7 style interoperability in future versions

---

## License / Academic Note

This repository is intended for academic, learning, and portfolio purposes.

- The data used may be synthetic, replayed, or derived from publicly available datasets.
- The predictions are educational and demonstrative only.
- The application should not be used for real clinical decision-making.

---

## Author

**Saumya Khobragade**
Final Project - Android Studio Lab
Project: **CritiWatch**
