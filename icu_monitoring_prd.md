
# Project Requirements Document (PRD)

## Project Title
**Smart ICU Patient Monitoring System with Mobile Interface and Predictive Analytics**

## Alternate Resume-Friendly Names
- Smart ICU Monitoring and Early Warning Mobile System
- AI-Assisted ICU Monitoring App (Android + FastAPI)
- Mobile ICU Observation and Risk Prediction System

## Document Metadata
- Document Type: Final Project Requirements Document
- Project Type: Android Studio Lab Project
- Frontend: Android app in Java
- Backend: Python FastAPI service
- Domain: Healthcare simulation / ICU monitoring
- Intended Usage: Academic submission, demo project, resume/portfolio showcase
- Product Nature: Demo/simulation system, not a real medical device

---

## 1. Executive Summary
This project is a mobile-based ICU patient monitoring simulation system built using **Java in Android Studio** and a **Python FastAPI backend**. The application allows users such as a doctor, nurse, or ICU intern to log in, view multiple patients, inspect current vital signs, observe graphs over time, request deterioration-risk predictions, and receive notifications for abnormal or critical conditions.

The project is intentionally designed to satisfy common Android lab requirements while still feeling like a complete and practical system. It combines:
- Android UI and lifecycle handling
- Intents and multi-page navigation
- SharedPreferences for session persistence
- SQLite-based local storage
- Notifications and dialog interactions
- Date and time selection components
- Network calls to a backend
- Predictive analytics and alert generation
- Mock data simulation for demo purposes

The application will **not** connect to real ICU bedside hardware in this version. Instead, it will support **synthetic/mock vitals**, **manual data entry**, and **dataset replay mode** so the project remains feasible within academic timelines.

---

## 2. Problem Statement
ICU monitoring systems are usually attached to hospital equipment and viewed on dedicated clinical displays. For a student project, it is useful to simulate a simplified system that lets a caregiver monitor a patient's health from a mobile device and receive early warning predictions when the condition appears to worsen.

The main problem this project solves is:

> How can we design a mobile ICU monitoring system that is realistic enough to demonstrate remote patient observation and predictive alerting, while still being feasible for an Android lab project?

This project answers that by focusing on:
- mobile visualization,
- backend-based scoring/prediction,
- local persistence,
- synthetic and replayable data,
- and alert-driven navigation.

---

## 3. Product Vision
Build a polished, multi-screen Android application that simulates ICU monitoring and provides risk prediction through a Python backend, while clearly demonstrating Android fundamentals and system design skills.

---

## 4. Objectives

### 4.1 Primary Objectives
1. Build a multi-activity Android application in Java.
2. Integrate the Android app with a Python FastAPI backend.
3. Simulate ICU patient data and show it in a meaningful UI.
4. Provide a deterioration prediction or early warning score.
5. Store patient data locally on the device.
6. Implement all mandatory Android lab components.
7. Make the final project large and polished enough for resume use.

### 4.2 Secondary Objectives
1. Add graph-based trend visualization.
2. Provide alerting through Android notifications.
3. Support local offline caching.
4. Allow date-range and time-range based history review.
5. Support demo scenarios such as Stable, Warning, Critical, and Recovery.

### 4.3 Resume Objectives
The project should clearly demonstrate:
- Android app development in Java
- REST API integration
- local database handling
- event-driven UI design
- alerts and notifications
- healthcare data visualization
- backend analytics / prediction logic

---

## 5. Scope

### 5.1 In Scope
- Android app in Java
- FastAPI backend in Python
- Login and session handling
- Patient list and detail screens
- Patient creation and editing
- Mock data generation and/or dataset replay
- Real-time-ish monitoring via polling
- Graphs for history and trends
- Risk prediction endpoint
- Alerts page and notification handling
- SharedPreferences
- SQLite local database
- Date/time filtering and reminder scheduling

### 5.2 Out of Scope
- Direct integration with real ICU machines
- Actual hospital deployment
- Regulatory or clinical-grade diagnosis
- Full hospital management software
- Complex deep learning pipelines
- Real-time streaming from live bedside devices
- Wearable/IoT hardware integration in the first version

---

## 6. Target Users and Roles

### 6.1 User Roles
1. **Doctor**
   - Views all patients
   - Reviews detailed vitals
   - Checks risk predictions
   - Reviews alerts and trends

2. **Nurse**
   - Views assigned patients
   - Monitors vitals and alerts
   - Schedules observation reminders
   - Adds notes or acknowledges alerts

3. **ICU Intern / Student**
   - Uses the system in demo mode
   - Replays mock scenarios
   - Learns how trends and alerts are visualized

4. **Admin (Optional Demo Role)**
   - Manages demo users or patient seed data
   - Reviews overall system state

### 6.2 Assumptions About Users
- Users need quick summaries before opening full details.
- Users prefer color-coded status indicators.
- Users need a patient-centered workflow rather than raw API data.
- Users may use the application in a demo environment without stable internet.

---

## 7. Success Criteria
The project is considered successful if:
1. All mandatory lab requirements are implemented and can be demonstrated live.
2. A user can log in and navigate across multiple screens using intents.
3. Patients can be created, viewed, and stored locally.
4. The backend can return a risk prediction for a set of vitals.
5. The app can show graphs and historical readings.
6. A notification can open the patient detail or alert page using an intent.
7. The project looks like a coherent end-to-end product rather than a collection of separate demos.

---

## 8. Core Product Concept
The app simulates an ICU monitoring station on a phone.

Each patient has:
- Demographics and bed information
- Latest vitals
- Historical readings
- Current risk level
- Alert history
- Optional notes and reminders

The backend provides:
- synthetic vitals,
- trend summaries,
- prediction/risk score,
- alert recommendations.

The app provides:
- mobile UI,
- local storage,
- screen navigation,
- visual charts,
- notifications,
- and interaction flows.

---

## 9. Mandatory Android Lab Requirement Mapping

| Mandatory Requirement | How It Will Be Used in This Project |
|---|---|
| Constraint Layout | All major activities use ConstraintLayout for clean responsive layouts |
| Event Handlers | Buttons, spinners, RecyclerView items, dialogs, date/time pickers, notification taps |
| Spinner | Ward filter, role selection, graph metric selection, refresh interval selection |
| Calendar and Clock | DatePickerDialog and TimePickerDialog for history filters and reminder scheduling |
| Intents | Activity navigation and passing patient IDs, risk status, alert source |
| Dialog Boxes and Toasts | Confirmation dialogs, warning dialogs, save success toasts, network error toasts |
| Notification Redirect using Intent | Tapping high-risk alert notification opens PatientDetailActivity or AlertsActivity |
| Shared Preferences | Login session, remember me, API base URL, user role, settings |
| SQL Database | Local SQLite database storing patients, readings, alerts, reminders, prediction history |

---

## 10. Recommended Tech Stack

### 10.1 Android Frontend
- **Language:** Java
- **IDE:** Android Studio
- **UI Layout:** ConstraintLayout
- **Navigation:** Multiple Activities using explicit Intents
- **List Rendering:** RecyclerView
- **Charts:** MPAndroidChart or GraphView
- **API Calls:** Retrofit + OkHttp + Gson
- **Local Storage:** SQLite via Room or SQLiteOpenHelper
- **Session Storage:** SharedPreferences
- **Notifications:** NotificationCompat + PendingIntent
- **Date/Time Inputs:** DatePickerDialog and TimePickerDialog
- **Dialogs:** AlertDialog / MaterialAlertDialogBuilder
- **Optional Background Refresh:** WorkManager or Handler/Runnable based polling

### 10.2 Python Backend
- **Language:** Python
- **Framework:** FastAPI
- **ASGI Server:** Uvicorn
- **Validation:** Pydantic
- **Database (optional backend persistence):** SQLite using SQLAlchemy
- **Data Processing:** pandas / numpy (optional but useful)
- **ML Layer (optional):** scikit-learn + joblib

### 10.3 Development Utilities
- Git and GitHub
- Postman or Swagger UI for endpoint testing
- Emulator or physical Android device
- CSV-based mock dataset storage for replay scenarios

### 10.4 Tech Stack Rationale
- Java satisfies the Android lab expectation and is easy to present during viva/demonstration.
- FastAPI is lightweight, fast to develop, and clean for REST APIs.
- SQLite keeps the project self-contained and demo-friendly.
- Retrofit simplifies API integration on Android.
- Graph libraries improve professionalism and help explain patient trends.

---

## 11. High-Level Architecture

### 11.1 Architecture Summary
The app uses a **client-server architecture**:

1. Android app collects user interactions.
2. Android app stores local cache/history in SQLite.
3. Android app calls FastAPI endpoints for live or simulated readings and predictions.
4. Backend computes risk and returns structured JSON.
5. Android app updates UI and generates alerts/notifications.

### 11.2 Data Flow
1. User logs in.
2. Dashboard requests patient list.
3. User opens a patient.
4. App fetches latest vitals and local history.
5. App requests risk prediction from backend.
6. If response indicates warning or high risk, app stores alert locally and optionally shows notification.
7. User taps notification; app opens the related patient detail screen.

### 11.3 Communication Pattern
- REST API over HTTP
- JSON request/response bodies
- Poll-based refresh every few seconds in monitoring mode
- Local-first caching for faster UI updates

---

## 12. Product Modules

### 12.1 Authentication and Session Module
Responsibilities:
- Login
- Optional registration
- Session persistence
- Logout
- Role selection or role display

### 12.2 Patient Management Module
Responsibilities:
- Patient list
- Patient search/filter
- Add patient
- Edit patient
- Delete or archive patient (optional)

### 12.3 Monitoring Module
Responsibilities:
- Show latest vitals
- Refresh current vitals
- Switch scenario mode
- Start/stop simulated monitoring

### 12.4 History and Graph Module
Responsibilities:
- View filtered historical readings
- Pick date and time range
- Choose metric from spinner
- Display line charts

### 12.5 Prediction Module
Responsibilities:
- Send vital readings to backend
- Receive score, severity, confidence/explanation
- Show prediction report and risk badge

### 12.6 Alerts Module
Responsibilities:
- Create alerts when thresholds or risk exceed limits
- Store alerts locally
- Show notification
- Open relevant page on tap
- Mark alert as acknowledged

### 12.7 Reminder and Scheduling Module
Responsibilities:
- Select reminder date
- Select reminder time
- Store reminder locally
- Trigger local notification

### 12.8 Settings Module
Responsibilities:
- Logout
- Toggle notifications
- Change refresh interval
- Save default ward filter
- Configure API base URL for demo

---

## 13. Feature List

### 13.1 Must-Have Features
1. Login screen
2. Session persistence
3. Patient dashboard
4. Patient detail page
5. Add/edit patient form
6. Current vital signs display
7. Mock data generation
8. Risk prediction call to backend
9. Graph view page with date/time filter
10. Alert generation and notifications
11. Local SQLite storage
12. SharedPreferences settings
13. Toasts and dialogs
14. Notification-to-intent navigation

### 13.2 Should-Have Features
1. Search patients by name or bed number
2. Filter by ICU ward/unit
3. Alert history page
4. Notes for a patient
5. Manual entry of vitals for testing
6. Scenario replay mode (Stable / Warning / Critical / Recovery)

### 13.3 Good Stretch Features
1. Prediction explanation page showing what caused the warning
2. Sync queue to resend failed API requests later
3. Export patient summary to text or PDF in future
4. Simple admin statistics page
5. Dark mode toggle
6. Backend JWT auth

---

## 14. Page Inventory

| Page / Activity | Purpose | Key Components |
|---|---|---|
| SplashActivity | App launch, session check, branding | Logo, app name, loading indicator |
| LoginActivity | User login | EditTexts, button, remember me, toast, dialog |
| RegisterActivity (optional but recommended) | Create demo account | Form fields, role spinner, validation |
| DashboardActivity | Show patient summary list | Toolbar, search, spinner, RecyclerView, FAB |
| AddEditPatientActivity | Create or modify patient details | Form, spinners, date picker, save button |
| PatientDetailActivity | Full patient overview | Cards, vitals, buttons, prediction summary |
| LiveMonitoringActivity | Repeated live updates / scenario control | Current vitals, start/stop, scenario buttons |
| GraphsActivity | Historical charts and filtering | Spinner, date/time pickers, chart, list |
| PredictionReportActivity | Detailed risk report | Score, severity, reasons, refresh button |
| AlertsActivity | Alert list and acknowledgement | RecyclerView, filter, severity chips/spinner |
| ScheduleReminderActivity | Set observation reminders | DatePicker, TimePicker, note field |
| SettingsActivity | User/session/app preferences | Switches, spinner, logout, base URL |
| AboutActivity (optional) | Project info and disclaimer | Text content, version, credits |

Recommended final page count for a strong project: **10 to 12 screens**.

---

## 15. Detailed Page Specifications

### 15.1 SplashActivity
**Purpose:**
- Display branding while checking if user session exists.

**UI Elements:**
- App logo ImageView
- App title TextView
- Tagline TextView
- ProgressBar

**Behavior:**
- Read SharedPreferences
- If logged in, navigate to DashboardActivity
- Else, navigate to LoginActivity

**Event Handlers:**
- Delayed handler after 1 to 2 seconds

**Data Used:**
- SharedPreferences: `is_logged_in`, `username`, `role`, `auth_token`

---

### 15.2 LoginActivity
**Purpose:**
- Authenticate demo user and create session

**UI Elements:**
- TextInputEditText or EditText: username
- TextInputEditText or EditText: password
- CheckBox: Remember me
- Button: Login
- TextView/Button: Go to Register
- ProgressBar

**Validation Rules:**
- Username required
- Password required
- Minimum password length can be 6 for demo mode

**Behavior:**
- On login click, validate fields
- Call backend `/auth/login` or use demo validation
- Show toast on success or failure
- Save session values in SharedPreferences
- Move to DashboardActivity

**Event Handlers:**
- Login button click
- Register text click
- Back press should ask exit confirmation or minimize app

**SharedPreferences Written:**
- `is_logged_in`
- `username`
- `remember_me`
- `role`
- `auth_token`

---

### 15.3 RegisterActivity
**Purpose:**
- Register a demo user for presentation completeness

**UI Elements:**
- Username field
- Email field
- Password field
- Confirm password field
- Spinner: Role (Doctor, Nurse, Intern)
- Button: Register

**Validation Rules:**
- Required fields must not be empty
- Password and confirm password must match
- Email format basic validation

**Event Handlers:**
- Register button click
- Spinner selection listener

**Output:**
- Success toast
- Optional dialog confirming role selection
- Redirect back to LoginActivity

---

### 15.4 DashboardActivity
**Purpose:**
- Main home screen after login
- Show list of patients with summary information

**UI Elements:**
- Toolbar with app title
- SearchView or EditText search bar
- Spinner: ward/unit filter
- RecyclerView for patient cards
- FloatingActionButton: Add Patient
- Button/Icon: Alerts
- Button/Icon: Settings
- Optional summary cards: total patients, high-risk count, active alerts

**Patient Card Suggested Content:**
- Patient name
- Patient ID
- Bed number
- Latest heart rate, SpO2, BP
- Last updated time
- Risk badge (Low / Medium / High)

**Behavior:**
- Load cached patients from SQLite first
- Sync with backend if available
- Apply spinner filter on ward
- Search by name/bed number
- Tap patient card to open PatientDetailActivity
- FAB opens AddEditPatientActivity

**Event Handlers:**
- RecyclerView item click
- Spinner item selected
- Search text changed
- Add patient button click
- Alerts icon click
- Settings icon click

**Storage:**
- Reads patients from SQLite
- Optional sync from backend
- Saves selected ward filter in SharedPreferences

---

### 15.5 AddEditPatientActivity
**Purpose:**
- Create or update a patient record

**UI Elements:**
- Patient name EditText
- Age EditText / Number input
- Spinner: Sex
- Spinner: ICU unit or ward
- Bed number EditText
- Diagnosis EditText
- Button: Admission Date (DatePicker)
- Button: Save
- Button: Cancel

**Validation Rules:**
- Name required
- Age required and numeric
- Bed number required
- Ward required

**Behavior:**
- Save data into local SQLite
- Optionally sync to backend
- Show toast on save
- If editing existing patient, load fields first

**Event Handlers:**
- Date picker open
- Save click
- Cancel click
- Spinner selection changes

**Database Tables Affected:**
- `patients`

---

### 15.6 PatientDetailActivity
**Purpose:**
- Show one patient in detail

**UI Elements:**
- Toolbar with patient name
- Demographic card
- Current vitals card
- Risk summary card
- Buttons:
  - Live Monitoring
  - View Graphs
  - View Prediction Report
  - Schedule Reminder
  - Add Note (optional)
- RecyclerView or section for recent alerts

**Displayed Fields:**
- Patient name, age, sex, ward, bed, diagnosis
- HR, SpO2, systolic BP, diastolic BP, RR, temperature
- Last prediction score and level
- Last updated timestamp

**Behavior:**
- Open from dashboard or notification
- Load patient data from intent extras using `patient_id`
- Fetch latest vitals from backend or local cache
- Show risk chip color
- If an alert exists, highlight warning section

**Event Handlers:**
- Button clicks for graphs, prediction, reminder, live mode
- Alert item click

**Storage:**
- Reads patient, latest vitals, alerts, prediction history from local DB

---

### 15.7 LiveMonitoringActivity
**Purpose:**
- Simulate ongoing monitoring using periodic polling or local scenario playback

**UI Elements:**
- Start button
- Stop button
- Buttons or chips for scenario selection:
  - Stable
  - Warning
  - Critical
  - Recovery
- TextViews or cards for each vital
- Mini graph or chart preview (optional)
- TextView showing next refresh countdown (optional)

**Behavior:**
- Poll backend every 3 to 5 seconds in active mode
- Update vitals on screen
- Store readings locally
- Evaluate alert conditions after each update
- Create local notification when high risk occurs

**Event Handlers:**
- Start click
- Stop click
- Scenario button click

**Notes:**
This page makes the project feel much larger and more realistic.

---

### 15.8 GraphsActivity
**Purpose:**
- Show historical trends and allow date/time range selection

**UI Elements:**
- Spinner: metric selector (HR, SpO2, BP, RR, Temp, Risk Score)
- Button: Start Date
- Button: Start Time
- Button: End Date
- Button: End Time
- Button: Apply Filter
- LineChart
- RecyclerView or table of readings under chart
- Empty-state message if no data in range

**Behavior:**
- Load history from SQLite and/or backend
- Filter by chosen metric and time range
- Draw line chart
- Allow changing metric without leaving page

**Event Handlers:**
- Spinner item selection
- Date picker click
- Time picker click
- Apply button click

**Mandatory Requirement Coverage:**
- Spinner
- Calendar
- Clock
- Event handlers

---

### 15.9 PredictionReportActivity
**Purpose:**
- Present backend prediction in a clear, explainable format

**UI Elements:**
- Risk score value
- Severity badge
- Prediction timestamp
- Contributing factors section
- Recommended action text
- Refresh prediction button
- Compare with previous prediction button (optional)

**Behavior:**
- Call `/predict/risk` or load latest prediction
- Save prediction locally to history table
- Show if trend is worsening or improving

**Example Display:**
- Risk Score: 78/100
- Level: High
- Reasons: SpO2 low, RR high, SBP low
- Suggested Action: Immediate clinical review (demo text only)

---

### 15.10 AlertsActivity
**Purpose:**
- Central screen for all alerts

**UI Elements:**
- RecyclerView list of alerts
- Spinner: filter by severity or status
- Button: Acknowledge selected alert (optional)
- Text summary of active alert count

**Alert Row Fields:**
- Patient name
- Severity
- Timestamp
- Short reason
- Status (new / acknowledged)

**Behavior:**
- Load alerts from SQLite
- Tap row opens PatientDetailActivity
- Long press can show dialog to acknowledge/delete

**Event Handlers:**
- RecyclerView item click
- Spinner change
- Long press or acknowledge action

---

### 15.11 ScheduleReminderActivity
**Purpose:**
- Let the user schedule a reminder for manual observation or follow-up

**UI Elements:**
- Date button -> DatePickerDialog
- Time button -> TimePickerDialog
- Note EditText
- Save reminder button
- Cancel button

**Behavior:**
- Save reminder to local DB
- Schedule local notification/alarm
- Show toast on success

**Why this page matters:**
- Cleanly satisfies the Calendar + Clock requirement
- Adds practical workflow value

---

### 15.12 SettingsActivity
**Purpose:**
- Manage preferences and session values

**UI Elements:**
- Switch: enable notifications
- Spinner: dashboard refresh interval
- Spinner: default ward filter
- EditText: API base URL (optional advanced setting)
- Button: Clear local cache (with confirmation dialog)
- Button: Logout

**Behavior:**
- Persist all values using SharedPreferences
- Logout clears session and returns to LoginActivity
- Clear cache dialog removes local temp records

**Event Handlers:**
- Switch change
- Spinner selection
- Clear cache click
- Logout click

---

### 15.13 AboutActivity (Optional)
**Purpose:**
- Show project credits, disclaimer, version, and technology used

**Useful for viva/demo:**
- Helps present the project professionally.

---

## 16. Navigation Map

### 16.1 Primary Navigation
- SplashActivity -> LoginActivity
- SplashActivity -> DashboardActivity (if session exists)
- LoginActivity -> DashboardActivity
- LoginActivity -> RegisterActivity
- RegisterActivity -> LoginActivity
- DashboardActivity -> AddEditPatientActivity
- DashboardActivity -> PatientDetailActivity
- DashboardActivity -> AlertsActivity
- DashboardActivity -> SettingsActivity
- PatientDetailActivity -> LiveMonitoringActivity
- PatientDetailActivity -> GraphsActivity
- PatientDetailActivity -> PredictionReportActivity
- PatientDetailActivity -> ScheduleReminderActivity
- AlertsActivity -> PatientDetailActivity
- Notification tap -> PatientDetailActivity

### 16.2 Intent Extras Plan
Suggested extras:
- `patient_id`
- `patient_name`
- `alert_id`
- `source_screen`
- `risk_level`
- `opened_from_notification`

---

## 17. Detailed User Flows

### 17.1 Login Flow
1. User opens app.
2. Splash checks SharedPreferences.
3. If session missing, login screen appears.
4. User enters username/password.
5. Validation runs.
6. Backend login call returns success.
7. SharedPreferences save session values.
8. Toast: Login successful.
9. Dashboard opens.

### 17.2 Add Patient Flow
1. User taps FAB on dashboard.
2. AddEditPatientActivity opens.
3. User fills patient details.
4. Admission date selected through DatePicker.
5. User taps save.
6. Data stored in SQLite.
7. Toast confirms save.
8. Dashboard refreshes.

### 17.3 Monitoring Flow
1. User opens patient details.
2. User enters live monitoring page.
3. User chooses scenario or starts auto-refresh.
4. App polls backend.
5. New vitals saved locally.
6. Prediction call evaluates risk.
7. If risk is high, alert saved and notification shown.
8. User taps notification to reopen patient page.

### 17.4 Graph Review Flow
1. User opens GraphsActivity.
2. User selects metric using spinner.
3. User selects start and end date/time.
4. App fetches filtered local history.
5. Chart renders line graph.
6. User can change metric and compare trends.

### 17.5 Alert Handling Flow
1. Alert is generated from high-risk prediction.
2. Local notification appears.
3. User taps notification.
4. App opens PatientDetailActivity with alert context.
5. User reviews data and optionally acknowledges alert in AlertsActivity.

---

## 18. Functional Requirements by Module

### 18.1 Authentication Requirements
- FR-A1: System shall allow user login with username and password.
- FR-A2: System shall validate empty fields before sending request.
- FR-A3: System shall store login session in SharedPreferences.
- FR-A4: System shall allow logout and clear session.
- FR-A5: System may support demo registration.

### 18.2 Patient Management Requirements
- FR-P1: System shall display a list of all stored patients.
- FR-P2: System shall support adding a new patient.
- FR-P3: System shall support editing patient details.
- FR-P4: System shall support search by patient name or bed number.
- FR-P5: System shall support ward filtering using spinner.

### 18.3 Monitoring Requirements
- FR-M1: System shall display latest vitals for selected patient.
- FR-M2: System shall refresh live vitals in monitoring mode.
- FR-M3: System shall support mock scenario selection.
- FR-M4: System shall store received vitals in local DB.

### 18.4 Prediction Requirements
- FR-R1: System shall send vitals to backend for prediction.
- FR-R2: System shall receive a structured risk response.
- FR-R3: System shall display severity level and score.
- FR-R4: System shall store prediction results locally.

### 18.5 Alerts Requirements
- FR-L1: System shall generate alert records for high-risk conditions.
- FR-L2: System shall show Android notification for selected alert types.
- FR-L3: Notification tap shall open patient-related page via intent.
- FR-L4: System shall allow alert acknowledgement.

### 18.6 Graph and History Requirements
- FR-G1: System shall store historical readings with timestamps.
- FR-G2: System shall filter history by date/time range.
- FR-G3: System shall allow metric selection using spinner.
- FR-G4: System shall display chart for selected metric.

### 18.7 Settings Requirements
- FR-S1: System shall store user preferences in SharedPreferences.
- FR-S2: System shall allow notification toggle.
- FR-S3: System shall allow refresh interval selection.
- FR-S4: System shall allow logout.

---

## 19. Non-Functional Requirements

### 19.1 Performance
- Dashboard should load cached patient list in under 2 seconds.
- Monitoring screen updates should feel smooth even on emulator.
- Graph page should render recent readings quickly.

### 19.2 Reliability
- If backend is unavailable, app should still show cached data.
- App should not crash on null API values.
- API errors should be surfaced through toast/snackbar/dialog.

### 19.3 Usability
- Color-code risk levels clearly.
- Keep navigation shallow and obvious.
- Important vitals must be readable without scrolling too much.

### 19.4 Maintainability
- Code should be organized into activities, adapters, models, database, API, utils, and services.
- Naming should be consistent.
- DTOs/models should mirror API payloads cleanly.

### 19.5 Security (Demo-Level)
- Do not store plain-text password in SQLite.
- Store token and session only as needed in SharedPreferences.
- Use fake/demo data in presentation, not real patient identity data.

### 19.6 Privacy Disclaimer
The app is for academic demonstration only and must not be described as a production clinical device.

---

## 20. UI/UX Design Guidelines

### 20.1 Visual Style
- Clean medical dashboard look
- White or light neutral background
- Accent colors for severity:
  - Green = Stable / Low risk
  - Yellow/Amber = Warning / Medium risk
  - Red = Critical / High risk
  - Blue = Informational

### 20.2 Card-Based Layout
Use cards for:
- Current vitals
- Risk summary
- Patient summary
- Alert summary

### 20.3 Typography and Readability
- Large numeric text for vitals
- Secondary smaller text for timestamps and labels
- Avoid crowded screens

### 20.4 Empty States
- No patients found
- No history in selected range
- No alerts yet
- Backend offline / using cached data

### 20.5 Dialog Recommendations
Use dialog boxes for:
- Confirm logout
- Confirm delete/clear cache
- Warning when invalid date range is selected
- High-risk pop-up inside monitoring page

### 20.6 Toast Recommendations
Use toasts for:
- Login success/failure
- Patient saved
- Reminder set
- Network error
- Data synced

---

## 21. Local Data Model

### 21.1 Entity: UserSession (stored in SharedPreferences)
Fields:
- username
- role
- authToken
- rememberMe
- isLoggedIn

### 21.2 Entity: Patient
Fields:
- local_id
- backend_id
- patient_code
- full_name
- age
- sex
- ward
- bed_no
- diagnosis
- admission_date
- created_at
- updated_at
- is_active

### 21.3 Entity: VitalReading
Fields:
- local_id
- backend_id
- patient_id
- heart_rate
- spo2
- systolic_bp
- diastolic_bp
- respiratory_rate
- temperature_c
- risk_score_snapshot
- scenario_label
- source_type (mock/manual/backend)
- reading_timestamp
- synced_flag

### 21.4 Entity: PredictionHistory
Fields:
- local_id
- patient_id
- risk_score
- risk_level
- confidence_text_or_value
- reasons_json_or_text
- recommendation_text
- prediction_timestamp

### 21.5 Entity: Alert
Fields:
- local_id
- patient_id
- alert_type
- severity
- message
- created_at
- acknowledged_at
- status
- opened_from_notification_flag

### 21.6 Entity: Reminder
Fields:
- local_id
- patient_id
- reminder_date
- reminder_time
- note
- status
- created_at

### 21.7 Entity: Note (Optional)
Fields:
- local_id
- patient_id
- note_text
- author_name
- created_at

---

## 22. Suggested SQLite Schema

### 22.1 patients
- id INTEGER PRIMARY KEY AUTOINCREMENT
- backend_id TEXT
- patient_code TEXT UNIQUE
- full_name TEXT NOT NULL
- age INTEGER NOT NULL
- sex TEXT NOT NULL
- ward TEXT NOT NULL
- bed_no TEXT NOT NULL
- diagnosis TEXT
- admission_date TEXT
- created_at TEXT
- updated_at TEXT
- is_active INTEGER DEFAULT 1

### 22.2 vital_readings
- id INTEGER PRIMARY KEY AUTOINCREMENT
- backend_id TEXT
- patient_id INTEGER NOT NULL
- heart_rate REAL
- spo2 REAL
- systolic_bp REAL
- diastolic_bp REAL
- respiratory_rate REAL
- temperature_c REAL
- risk_score_snapshot REAL
- scenario_label TEXT
- source_type TEXT
- reading_timestamp TEXT NOT NULL
- synced_flag INTEGER DEFAULT 0

### 22.3 prediction_history
- id INTEGER PRIMARY KEY AUTOINCREMENT
- patient_id INTEGER NOT NULL
- risk_score REAL
- risk_level TEXT
- confidence_value REAL
- reasons_text TEXT
- recommendation_text TEXT
- prediction_timestamp TEXT

### 22.4 alerts
- id INTEGER PRIMARY KEY AUTOINCREMENT
- patient_id INTEGER NOT NULL
- alert_type TEXT
- severity TEXT
- message TEXT
- status TEXT DEFAULT 'NEW'
- created_at TEXT
- acknowledged_at TEXT

### 22.5 reminders
- id INTEGER PRIMARY KEY AUTOINCREMENT
- patient_id INTEGER NOT NULL
- reminder_datetime TEXT
- note TEXT
- status TEXT DEFAULT 'ACTIVE'
- created_at TEXT

### 22.6 notes (optional)
- id INTEGER PRIMARY KEY AUTOINCREMENT
- patient_id INTEGER NOT NULL
- note_text TEXT
- author_name TEXT
- created_at TEXT

---

## 23. SharedPreferences Design

### 23.1 Required Keys
- `is_logged_in`
- `username`
- `role`
- `auth_token`
- `remember_me`
- `notifications_enabled`
- `default_ward`
- `refresh_interval_seconds`
- `api_base_url`
- `theme_mode`

### 23.2 Usage Rules
- Store only lightweight configuration/session data.
- Do not store full patient history in SharedPreferences.
- Clear login keys on logout.

---

## 24. Backend Scope and Responsibilities

### 24.1 Backend Responsibilities
The FastAPI backend should do more than just one prediction call. To make the project stronger, the backend should provide:
- authentication support,
- patient list responses,
- simulated vital generation,
- prediction scoring,
- alert classification,
- and optional history replay.

### 24.2 Backend Feature Set
1. Demo authentication
2. Return patient list and patient detail
3. Generate latest mock vitals
4. Return historical vitals for a date range
5. Compute deterioration risk
6. Return alert recommendations
7. Provide health-check endpoint
8. Optional model training or model loading endpoint (not exposed in production demo)

---

## 25. API Design

### 25.1 Base URL
- Android emulator local backend example: `http://10.0.2.2:8000/`
- Real device example on same network: `http://<laptop-ip>:8000/`

### 25.2 Authentication Endpoints

#### POST /auth/login
**Request Body**
```json
{
  "username": "doctor1",
  "password": "demo123"
}
```

**Response**
```json
{
  "success": true,
  "token": "demo-token-123",
  "role": "Doctor",
  "username": "doctor1"
}
```

#### POST /auth/register (optional)
**Request Body**
```json
{
  "username": "intern1",
  "email": "intern1@example.com",
  "password": "demo123",
  "role": "Intern"
}
```

---

### 25.3 Patient Endpoints

#### GET /patients
Returns all patients.

#### GET /patients/{patient_id}
Returns a single patient profile.

#### POST /patients
Creates a patient.

#### PUT /patients/{patient_id}
Updates a patient.

#### DELETE /patients/{patient_id}
Optional soft delete.

**Example Patient Response**
```json
{
  "patient_id": 101,
  "patient_code": "ICU-101",
  "full_name": "Aarav Sharma",
  "age": 62,
  "sex": "Male",
  "ward": "Medical ICU",
  "bed_no": "B12",
  "diagnosis": "Post-operative observation",
  "admission_date": "2026-03-25"
}
```

---

### 25.4 Vitals Endpoints

#### GET /patients/{patient_id}/vitals/latest
Returns latest vitals.

#### GET /patients/{patient_id}/vitals/history
Query Params:
- `start_datetime`
- `end_datetime`
- `metric` (optional)

#### POST /patients/{patient_id}/vitals/mock
Used to create a new synthetic reading.

**Example Latest Vitals Response**
```json
{
  "patient_id": 101,
  "heart_rate": 118,
  "spo2": 91,
  "systolic_bp": 96,
  "diastolic_bp": 62,
  "respiratory_rate": 28,
  "temperature_c": 38.6,
  "scenario_label": "Warning",
  "reading_timestamp": "2026-03-27T10:20:00"
}
```

---

### 25.5 Prediction Endpoints

#### POST /predict/risk
**Request Body**
```json
{
  "patient_id": 101,
  "heart_rate": 118,
  "spo2": 91,
  "systolic_bp": 96,
  "diastolic_bp": 62,
  "respiratory_rate": 28,
  "temperature_c": 38.6
}
```

**Response**
```json
{
  "patient_id": 101,
  "risk_score": 78,
  "risk_level": "HIGH",
  "reasons": [
    "SpO2 below safe threshold",
    "Respiratory rate elevated",
    "Blood pressure low"
  ],
  "recommendation": "Immediate clinical review suggested (demo output)",
  "prediction_timestamp": "2026-03-27T10:20:01"
}
```

---

### 25.6 Alerts Endpoints

#### GET /alerts
Returns alert list.

#### POST /alerts/generate
Optional explicit alert generation for a patient.

#### PUT /alerts/{alert_id}/acknowledge
Marks alert as acknowledged.

---

### 25.7 Utility Endpoints

#### GET /health
Simple backend health check.

#### GET /meta/wards
Returns wards for spinner population.

#### GET /meta/scenarios
Returns scenario list: Stable, Warning, Critical, Recovery.

---

## 26. Backend Validation and Error Handling

### 26.1 Validation Rules
- Heart rate cannot be negative.
- SpO2 should be between 0 and 100.
- Blood pressure values should be positive.
- Temperature should be within human-relevant demo range.
- Unknown patient_id should return 404.

### 26.2 Error Response Format
```json
{
  "success": false,
  "message": "Patient not found"
}
```

### 26.3 Error Cases to Handle in Android
- No internet connection
- Backend server unavailable
- Timeout
- Invalid response format
- Empty history range
- Unauthorized request

---

## 27. Prediction Strategy

### 27.1 Recommended First Version: Rule-Based Early Warning Score
For a college project, the best approach is a **deterministic risk engine** first, not a complicated ML model. This is easier to debug, easier to explain in viva, and more reliable for demo day.

### 27.2 Sample Scoring Logic
Assign points based on thresholds.

**Heart Rate**
- 60 to 100 -> 0
- 101 to 120 -> 1
- 121 to 130 -> 2
- >130 or <40 -> 3

**SpO2**
- >=95 -> 0
- 92 to 94 -> 1
- 90 to 91 -> 2
- <90 -> 3

**Respiratory Rate**
- 12 to 20 -> 0
- 21 to 24 -> 1
- 25 to 30 -> 2
- >30 or <8 -> 3

**Systolic BP**
- 100 to 140 -> 0
- 90 to 99 -> 1
- 80 to 89 -> 2
- <80 or >180 -> 3

**Temperature**
- 36.0 to 37.5 -> 0
- 37.6 to 38.5 -> 1
- 38.6 to 39.5 -> 2
- >39.5 or <35.0 -> 3

### 27.3 Risk Mapping
- Total 0 to 2 -> LOW
- Total 3 to 5 -> MEDIUM
- Total 6 or more -> HIGH

### 27.4 Why This Is Good
- Simple enough to implement fast
- Easy to explain to faculty
- Produces meaningful output
- Can later be replaced with ML without changing the UI much

### 27.5 Optional Phase 2: ML Model
If time allows, train a basic model using a public dataset or synthetic labeled data to classify risk. Possible model choices:
- Logistic Regression
- Random Forest
- XGBoost (only if already familiar)

Recommendation: Keep ML optional. Make the rule-based model the guaranteed submission path.

---

## 28. Mock Data Strategy

### 28.1 Why Mock Data Is Essential
Real ICU hardware integration is out of scope, so the system must support believable simulated data.

### 28.2 Supported Data Modes
1. **Manual Entry Mode**
   - User enters vitals manually
   - Good for testing specific edge cases

2. **Random Stable Mode**
   - Generates normal ranges

3. **Warning Scenario Mode**
   - Generates values that slowly worsen

4. **Critical Scenario Mode**
   - Generates dangerous vitals immediately

5. **Recovery Scenario Mode**
   - Starts abnormal and gradually improves

6. **Dataset Replay Mode**
   - Reads preloaded CSV rows and replays them as time series

### 28.3 Suggested Ranges for Mock Generation

**Stable**
- HR: 70 to 95
- SpO2: 96 to 100
- SBP: 105 to 130
- DBP: 65 to 85
- RR: 12 to 18
- Temp: 36.4 to 37.4

**Warning**
- HR: 100 to 120
- SpO2: 91 to 95
- SBP: 90 to 105
- RR: 20 to 28
- Temp: 37.8 to 38.8

**Critical**
- HR: 125 to 150
- SpO2: 82 to 90
- SBP: 70 to 89
- RR: 28 to 36
- Temp: 39.0 to 40.5 or <35

**Recovery**
- Values gradually move from warning to stable across successive readings

### 28.4 Interval Strategy
- Dashboard refresh: every 10 to 15 seconds
- Live monitoring refresh: every 3 to 5 seconds
- Prediction request: after each new reading in monitoring mode, or manually on demand

---

## 29. Dataset Strategy

### 29.1 Recommended Practical Approach
Use **mock/synthetic data for the working app**, and optionally use a public dataset to shape scenarios or train a simple model.

### 29.2 Dataset Sources to Mention in Project Report
- ICU/EHR dataset source for research/training inspiration
- High-resolution vital signs dataset for scenario replay inspiration
- Small CSV-based custom demo dataset prepared by you for clean presentation

### 29.3 Best Academic Strategy
- Phase 1: Build full app using synthetic data
- Phase 2: Add public-dataset-inspired scenario templates
- Phase 3: Optional simple classifier trained offline

### 29.4 Important Presentation Note
In your documentation and viva, say:

> This version is a simulation and early warning demonstrator using mock and replayable data, not a clinical monitoring device.

---

## 30. Alert Logic

### 30.1 Alert Trigger Conditions
An alert should be generated if:
- prediction risk level is HIGH,
- or a vital crosses a critical threshold,
- or there is a sudden worsening trend across recent readings.

### 30.2 Sample Threshold Rules
- SpO2 < 90
- HR > 130 or HR < 40
- SBP < 80
- RR > 30 or RR < 8
- Temp > 39.5 or Temp < 35.0

### 30.3 Alert Severity Mapping
- INFO: mild anomaly
- WARNING: moderate risk or medium score
- CRITICAL: high risk or severe threshold breach

### 30.4 Notification Policy
- Show notification for WARNING and CRITICAL only
- Use different notification titles and messages
- Tapping notification opens patient details
- Avoid spamming repeated notifications every second; cooldown can be 30 to 60 seconds per patient

---

## 31. Graph and Analytics Requirements

### 31.1 Metrics To Plot
- Heart Rate
- SpO2
- Systolic BP
- Diastolic BP
- Respiratory Rate
- Temperature
- Risk Score

### 31.2 Graph Types
- Line chart for time-series trends
- Optional secondary bar chart for alert count by severity

### 31.3 Graph Features
- Filter by date/time range
- Select metric with spinner
- Show min/max/average for selected range (optional but strong feature)
- Show empty-state message when no data exists

### 31.4 Analytics Summary Card (Optional)
For selected date range, show:
- average HR
- lowest SpO2
- highest temperature
- alert count in range

---

## 32. Notification Design

### 32.1 Notification Types
1. High-risk patient alert
2. Scheduled reminder notification
3. Optional sync failure notification

### 32.2 Notification Content Example
**Title:** Critical Alert - Aarav Sharma
**Text:** SpO2 dropped below 90. Tap to view patient details.

### 32.3 Notification Tap Behavior
- Opens PatientDetailActivity
- Passes `patient_id`
- Optionally passes `alert_id` and `opened_from_notification=true`

### 32.4 Notification Channels
Create channels such as:
- `critical_alerts`
- `reminders`
- `general_updates`

---

## 33. Dialog Boxes and Toast Usage Plan

### 33.1 Dialogs
- Logout confirmation dialog
- Clear cache confirmation dialog
- Delete patient confirmation dialog (optional)
- Invalid date range warning dialog
- High-risk acknowledgement dialog

### 33.2 Toasts
- Login successful
- Login failed
- Patient saved successfully
- Reminder scheduled
- Data sync failed
- Backend unavailable; showing cached data

---

## 34. Error Handling Strategy

### 34.1 Android-Side Errors
- Empty input -> inline error or toast
- Failed network call -> toast + fallback to local data
- Empty chart range -> dialog
- JSON parse error -> generic error dialog

### 34.2 Backend-Side Errors
- 400 for invalid data
- 404 for missing patient
- 500 for unexpected internal issue

### 34.3 Demo Safety Strategy
For demo day, add a switch or hidden debug action allowing fallback to local mock generation if backend is unavailable.

---

## 35. Security and Demo-Safety Considerations
- This is a non-clinical educational project.
- Use fictional patient names in the demo.
- Avoid storing actual passwords in SQLite.
- Avoid hardcoding sensitive URLs into public GitHub if using a remote host.
- Add a disclaimer screen or footer if presenting externally.

---

## 36. Recommended Android Implementation Pattern

### 36.1 Recommended Structure
Although this is a Java lab project, keep the project modular:
- `activities` for screens
- `adapters` for RecyclerViews
- `api` for Retrofit client and service interfaces
- `db` for SQLite/Room helpers and DAOs
- `models` for patient, vitals, alert, prediction objects
- `repository` for data access logic
- `services` for notifications and reminders
- `utils` for constants, date formatting, validation

### 36.2 Why This Matters
A modular structure makes the project look professional and keeps the codebase easier to explain in interviews.

---

## 37. Android Folder Structure

```text
android-app/
  app/
    src/
      main/
        java/com/example/icumonitor/
          activities/
            SplashActivity.java
            LoginActivity.java
            RegisterActivity.java
            DashboardActivity.java
            AddEditPatientActivity.java
            PatientDetailActivity.java
            LiveMonitoringActivity.java
            GraphsActivity.java
            PredictionReportActivity.java
            AlertsActivity.java
            ScheduleReminderActivity.java
            SettingsActivity.java
            AboutActivity.java

          adapters/
            PatientAdapter.java
            AlertAdapter.java
            ReadingAdapter.java

          api/
            ApiClient.java
            ApiService.java
            AuthInterceptor.java

          db/
            AppDatabaseHelper.java
            PatientDao.java
            VitalDao.java
            AlertDao.java
            ReminderDao.java
            PredictionDao.java

          models/
            LoginRequest.java
            LoginResponse.java
            Patient.java
            VitalReading.java
            PredictionResult.java
            AlertItem.java
            ReminderItem.java
            NoteItem.java

          repository/
            PatientRepository.java
            MonitoringRepository.java
            PredictionRepository.java
            AlertRepository.java
            SettingsRepository.java

          receivers/
            ReminderReceiver.java
            NotificationActionReceiver.java

          services/
            NotificationHelper.java
            SessionManager.java
            ReminderScheduler.java
            MonitoringPollManager.java

          utils/
            Constants.java
            DateTimeUtils.java
            ValidationUtils.java
            NetworkUtils.java
            RiskColorUtils.java

        res/
          layout/
            activity_splash.xml
            activity_login.xml
            activity_register.xml
            activity_dashboard.xml
            activity_add_edit_patient.xml
            activity_patient_detail.xml
            activity_live_monitoring.xml
            activity_graphs.xml
            activity_prediction_report.xml
            activity_alerts.xml
            activity_schedule_reminder.xml
            activity_settings.xml
            activity_about.xml
            item_patient.xml
            item_alert.xml
            item_reading.xml

          drawable/
          mipmap/
          values/
            colors.xml
            strings.xml
            themes.xml
          menu/
            menu_dashboard.xml
            menu_patient_detail.xml

        AndroidManifest.xml

  build.gradle
  settings.gradle
```

### 37.1 Note on SQLite Implementation Choice
Two acceptable options:
1. **Room over SQLite** for cleaner code and easier maintenance.
2. **SQLiteOpenHelper** if faculty explicitly expects manual SQL handling.

Recommendation: Build with Room if allowed, otherwise use SQLiteOpenHelper and keep schema simple.

---

## 38. Backend Folder Structure

```text
backend/
  app/
    main.py

    api/
      routes_auth.py
      routes_patients.py
      routes_vitals.py
      routes_predict.py
      routes_alerts.py
      routes_meta.py

    core/
      config.py
      constants.py

    schemas/
      auth_schema.py
      patient_schema.py
      vital_schema.py
      predict_schema.py
      alert_schema.py

    models/
      patient_model.py
      vital_model.py
      alert_model.py
      user_model.py

    services/
      auth_service.py
      patient_service.py
      simulator_service.py
      predictor_service.py
      alert_service.py
      history_service.py

    db/
      database.py
      seed_data.py

    utils/
      datetime_utils.py
      risk_rules.py
      scenario_generator.py

  data/
    demo_patients.json
    vitals_seed.csv
    replay_scenarios/
      stable.csv
      warning.csv
      critical.csv
      recovery.csv

  model_artifacts/
    risk_model.pkl

  tests/
    test_auth.py
    test_predict.py
    test_patients.py

  requirements.txt
  README.md
  .env.example
```

### 38.1 Backend Design Notes
- Keep routes thin and push business logic into services.
- Put scoring logic in `predictor_service.py`.
- Put mock data generation in `simulator_service.py`.
- Keep seed/demo data separate under `data/`.

---

## 39. Suggested Class Responsibilities (Android)

### 39.1 SessionManager
- Manage login state in SharedPreferences
- Save token, username, role
- Provide logout helper

### 39.2 NotificationHelper
- Create channels
- Build notifications
- Create PendingIntent for patient detail navigation

### 39.3 ReminderScheduler
- Schedule reminder alarms or worker tasks
- Trigger reminder notification

### 39.4 MonitoringPollManager
- Start/stop polling on monitoring page
- Request new reading from backend
- Save returned reading to local DB

### 39.5 PatientRepository
- Fetch from local DB
- Sync with backend
- Hide data source details from activities

---

## 40. Suggested Backend Service Responsibilities

### 40.1 auth_service.py
- Verify demo users
- Issue fake/demo token

### 40.2 patient_service.py
- Return patient list
- Create/update patient

### 40.3 simulator_service.py
- Generate scenario-based vitals
- Replay CSV rows for a patient

### 40.4 predictor_service.py
- Apply risk rules
- Return score, level, reasons, recommendation

### 40.5 alert_service.py
- Translate prediction result into alert severity/message
- Store or return alert data

### 40.6 history_service.py
- Return filtered time-series data

---

## 41. Acceptance Criteria

### 41.1 General Acceptance
- App installs and runs on emulator/device
- Backend starts successfully
- App can communicate with backend
- User can demonstrate every required Android feature

### 41.2 Screen-Level Acceptance
- Login works and stores session
- Dashboard loads at least 5 demo patients
- Patient detail shows vitals and risk summary
- Live monitoring updates values on the screen
- Graphs page filters by date/time and metric
- Alerts page shows at least one generated alert in a critical scenario
- Settings persist after app restart
- Notification tap opens patient page

### 41.3 Database Acceptance
- Added patients persist after app restart
- Vitals and alerts remain visible from local DB
- Reminder persists and can trigger notification

### 41.4 Prediction Acceptance
- Backend returns LOW, MEDIUM, HIGH classification based on input vitals
- Reasons are included in prediction response

---

## 42. Testing Plan

### 42.1 Manual Test Cases
1. Login with valid credentials
2. Login with invalid credentials
3. Add patient and verify dashboard update
4. Edit patient and verify save
5. Select ward from spinner and confirm filtering
6. Open graphs page and change metric spinner
7. Select invalid date range and verify dialog
8. Trigger critical scenario and verify notification
9. Tap notification and verify page redirect
10. Logout and verify session cleared

### 42.2 Backend Test Cases
1. `/health` returns success
2. `/auth/login` validates credentials
3. `/patients` returns list
4. `/patients/{id}/vitals/latest` returns a complete reading
5. `/predict/risk` returns score and level
6. Invalid patient_id returns 404

### 42.3 UI Testing Considerations
- Orientation changes (if supported)
- Empty patient list state
- No internet state
- Fast repeated button presses

---

## 43. Development Phases

### Phase 1: Project Setup
- Create Android project and backend project
- Set up base theme, packages, Retrofit, local DB skeleton
- Create seed backend endpoints and demo users

### Phase 2: Core Navigation and Authentication
- Splash, login, register, dashboard
- SharedPreferences session logic
- Multi-activity navigation using intents

### Phase 3: Patient Management
- Add/edit patient forms
- SQLite patient table
- Dashboard list and search/filter

### Phase 4: Monitoring and Prediction
- Latest vitals UI
- Mock data backend
- Prediction endpoint integration
- Patient detail and live monitoring screens

### Phase 5: History, Graphs, Alerts
- Vital history storage
- Graph page
- Alerts list and notifications
- Reminder scheduling

### Phase 6: Polish and Demo Preparation
- Improve UI consistency
- Add about page/disclaimer
- Seed good demo scenarios
- Prepare screenshots and final README

---

## 44. Risks and Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Scope becomes too large | High | Build guaranteed MVP first, then add stretch features |
| Backend integration issues | High | Keep fallback local mock mode |
| Charts take too long | Medium | Start with one chart on one page first |
| ML model becomes unstable | High | Use rule-based risk engine as default |
| Too much time spent on authentication | Medium | Keep login simple and demo-focused |
| Data looks unrealistic | Medium | Use scenario-based ranges and replay CSV data |
| Notification not opening correct page | Medium | Test PendingIntent extras early |

---

## 45. MVP Definition
The minimum viable version that still looks strong must include:
- Splash
- Login
- Dashboard
- Add patient
- Patient detail
- Graphs page
- Alerts page
- Settings page
- SharedPreferences
- SQLite storage
- Date/time picker
- Spinner
- Notification with page redirect
- FastAPI prediction endpoint
- Mock data generation

If time becomes limited, keep this MVP and polish it.

---

## 46. Stretch Features Worth Adding Only After MVP
- Alert acknowledgement sync to backend
- Patient note system
- Replay mode from CSV file
- Risk trend comparison over time
- Admin statistics screen
- Dark mode
- Offline-first sync queue
- Backend JWT tokens

---

## 47. Demo Data Recommendations

### 47.1 Demo Patient Count
Create 5 to 8 demo patients.

### 47.2 Recommended Demo Mix
- 2 stable patients
- 2 warning patients
- 1 critical patient
- 1 recovery patient
- optional extra post-operative observation patient

### 47.3 Why This Helps
It gives variety during demonstration and makes dashboard filtering more meaningful.

---

## 48. Suggested Naming Conventions

### 48.1 Patient Code Format
- ICU-101
- ICU-102
- ICU-103

### 48.2 Alert ID Format (backend or local display)
- ALT-0001
- ALT-0002

### 48.3 Scenario Labels
- STABLE
- WARNING
- CRITICAL
- RECOVERY

---

## 49. Suggested Resume Description
Use a concise project description such as:

> Developed a multi-screen Android ICU monitoring simulation app in Java with a FastAPI backend, featuring patient dashboards, vital-sign trend visualization, local SQLite persistence, alert notifications, and backend-driven deterioration risk prediction.

Possible bullet points:
- Built an Android application in Java using ConstraintLayout, Intents, SharedPreferences, SQLite, dialogs, toasts, and notification-based navigation.
- Integrated FastAPI backend endpoints for mock patient monitoring, risk scoring, and alert generation.
- Implemented graph-based time-series visualization and local caching of vitals, alerts, and prediction history.

---

## 50. Future Scope
- Integrate with real monitor hardware or IoT gateway
- Web dashboard for central ICU station
- Role-based access with JWT and refresh tokens
- Better ML model trained on real public datasets
- Doctor notes and nurse task checklists
- Cloud database and live sync
- Audio alerts and escalation workflows

---

## 51. Final Recommendation on Project Positioning
Position this project as:

> A healthcare monitoring and early warning simulation system, not as a final clinical product.

That framing makes the project realistic, ethical, and technically strong.

---

## 52. Final Build Recommendation
Build in this order:
1. UI skeleton and navigation
2. SharedPreferences login session
3. SQLite patient CRUD
4. FastAPI mock vitals endpoint
5. Patient detail + current vitals
6. Rule-based prediction endpoint
7. Alerts and notifications
8. Graphs and history filtering
9. Reminder scheduling
10. Polish, test, present

This order keeps risk low and ensures you always have a working demo.

---

## 53. References for Implementation Decisions
The following sources are useful for implementation and were considered while shaping the recommended stack and scope:
- Android app notifications and notification navigation
- Android SharedPreferences guidance
- Android Room/SQLite guidance
- FastAPI official tutorial and app structure guidance
- Public ICU/vitals datasets such as MIMIC-IV and VitalDB for inspiration or optional replay/training support

---

## 54. Submission Note
If your faculty asks why real ICU integration is not included, the correct answer is:

> This project focuses on mobile monitoring workflow, local storage, simulated data pipelines, and backend-based early warning prediction. Real clinical device integration is intentionally left as future work due to hardware, protocol, data privacy, and medical-grade validation constraints.

---

## 55. Final One-Line Project Summary
**Smart ICU Patient Monitoring System with Mobile Interface and Predictive Analytics is a Java Android + FastAPI project that simulates ICU patient observation, graphs vital trends, stores data locally, and generates predictive alerts on a mobile device.**
