# Stitch Frontend Generation Prompt for CritiWatch

## Objective
Generate a **complete frontend prototype** for a project called **CritiWatch: ICU Vital Monitoring and Deterioration Prediction Platform**.

The output must be built in **plain HTML, CSS, and JavaScript only**. Do **not** use React, Vue, Angular, Tailwind, Bootstrap, Material UI, or any external UI framework unless explicitly unavoidable. The purpose is to create a frontend prototype that can later be **manually converted into Android XML layouts and Java activity logic**.

This is **not** the final Android implementation. It is a visual and interaction prototype that should closely map to Android screens, components, and flows.

---

## Core Purpose of the App
CritiWatch is a **mobile ICU patient monitoring application** that allows a doctor, nurse, or ICU staff member to:
- log in
- view ICU patients
- inspect live and historical vitals
- view deterioration risk predictions
- receive high-risk alerts
- inspect trends using graphs
- review prediction explanations and alert reasons
- manage local settings and session state

This is a **simulation app**, not a real medical device. It should look realistic, professional, clean, modern, and suited for a final-year engineering project.

---

## Important Output Requirements

### Technology Constraints
Generate the frontend using:
- HTML
- CSS
- JavaScript

Do not use:
- React
- JSX
- TypeScript
- Tailwind
- Bootstrap
- SASS/SCSS
- backend code
- Python code
- Java code

### Design Constraints
The layout should feel like a **mobile-first healthcare application**.

Design goals:
- clean
- professional
- medical dashboard aesthetic
- readable typography
- clear status indicators
- soft but serious color palette
- visual emphasis on critical alerts
- mobile screen proportions
- easy conversion into Android ConstraintLayout / LinearLayout / RecyclerView-style screens later

### Structural Constraints
Each page should be designed as a separate screen or well-separated section so it can later map to:
- one Android Activity, or
- one Fragment, or
- one layout XML file

Avoid highly web-specific patterns that are difficult to convert to Android, such as:
- floating web navbars that depend on hover behavior
- overly complex responsive desktop grids
- browser-specific interactions
- advanced CSS animations that would be hard to replicate in Android

Prefer designs that map well to Android UI components:
- cards
- toolbars
- lists
- input fields
- buttons
- chips
- dialog-like overlays
- tabs or segmented controls
- status banners
- graph containers
- notification cards

---

## App Branding
Use the following project identity:

- **App Name:** CritiWatch
- **Full Title:** ICU Vital Monitoring and Deterioration Prediction Platform
- **Tagline:** Smart mobile ICU monitoring with predictive alerts

The UI should prominently reflect this identity on login, splash, and settings/about areas.

---

## Design Language
Use a consistent healthcare-themed design system.

### Suggested visual direction
- Primary color: deep blue / teal family
- Secondary color: white / light gray
- Accent colors:
  - green for stable
  - amber/orange for warning
  - red for critical
- Rounded cards
- Shadow kept minimal and clean
- Clear spacing
- Strong hierarchy between patient name, current vitals, and risk level

### Typography hierarchy
- App title large and bold
- Section titles medium bold
- Vital labels medium
- Readings large and easy to scan
- Secondary metadata small but readable

### Icons / visual motifs
If icons are included, use placeholder or simple icon-like shapes and mention what they represent. Examples:
- heart rate
- oxygen
- temperature
- blood pressure
- respiratory rate
- alerts
- dashboard
- settings
- history

---

## Required Screens / Pages
Generate the following pages in detail.

---

# 1. Splash Screen
## Purpose
Initial branding screen shown before login.

## Must include
- CritiWatch logo area or placeholder logo
- App name: CritiWatch
- Full subtitle: ICU Vital Monitoring and Deterioration Prediction Platform
- Small loading indicator or “Initializing monitoring environment...” text
- Clean background

## Notes for Android conversion
This should later become a simple splash activity or launch screen.

---

# 2. Login Screen
## Purpose
User authentication screen.

## Must include
- App logo / branding area
- Username or email input
- Password input
- Show/hide password toggle mock
- Login button
- Optional “Remember me” checkbox or toggle
- Optional link/button for demo login
- Optional “Forgot password” text
- Small footer note: “Demo academic project — not for clinical use”

## Interaction expectations
- Invalid fields should show inline validation states
- Login button should have enabled/disabled appearance
- Show a mock toast/snackbar/status message region for success or failure

## Android mapping notes
This should later map to EditText, Button, CheckBox, Toast, and SharedPreferences session logic.

---

# 3. Dashboard / Patient List Screen
## Purpose
Main landing page after login.

## Must include
- Top app bar with title “Dashboard”
- User greeting area, e.g. “Welcome, Doctor”
- Search bar for patient lookup
- Summary cards showing:
  - total patients
  - high-risk patients
  - active alerts
  - average ward occupancy or mock stat
- Ward/unit filter dropdown area
- Risk filter chips or segmented control:
  - All
  - Stable
  - Warning
  - Critical
- Scrollable patient list using cards

## Each patient card should show
- Patient name
- Patient ID
- Bed/ward number
- Age and gender
- Current heart rate
- Current SpO2
- Current blood pressure
- Current temperature
- Current risk badge: Stable / Warning / Critical
- Last updated timestamp
- Button(s): “View Details” and optional “Predict”

## Interaction expectations
- Clicking a patient card opens Patient Details page
- Clicking Predict shows a quick prediction result state
- Search filters the list
- Ward spinner changes visible data

## Android mapping notes
Should be easy to convert into RecyclerView + CardView-like rows and Spinner/filter buttons.

---

# 4. Patient Detail Screen
## Purpose
Detailed single-patient monitoring page.

## Must include
- Back navigation button
- Patient name and ID header
- Bed number / ward / admission status area
- Prominent risk banner at top
- Last updated time

## Vital cards section
Show individual metric cards for:
- Heart rate
- SpO2
- Blood pressure
- Respiratory rate
- Temperature

Each card should display:
- metric label
- current value
- unit
- mini trend direction indicator (up/down/stable)
- status color state

## Additional sections
- Clinical notes preview area
- Observation summary area
- Buttons:
  - View Trends
  - Run Prediction
  - View Alert History
  - Add Note

## Risk explanation section
Show a card that explains why the patient is high/medium/low risk. Example:
- low SpO2
- elevated heart rate
- low systolic BP

## Android mapping notes
This screen should later map to a vertically scrollable layout with multiple cards inside nested sections.

---

# 5. Trends / Graph Screen
## Purpose
Historical graph view of patient vitals.

## Must include
- Patient name at top
- Date range selector area
- Time range selector area
- Vital selector tabs or buttons:
  - Heart Rate
  - SpO2
  - BP
  - Respiration
  - Temperature
- Large graph container
- Legend and summary statistics below graph

## Show mock graph areas for
- line chart for trends
- optional dual line chart for systolic and diastolic BP

## Below the graph include
- min value
- max value
- average value
- observed anomalies count
- trend interpretation summary

## Notes
Do not build actual advanced chart logic unless simple JS chart placeholders are used. The visual structure matters more than perfect chart implementation.

## Android mapping notes
This must later be easy to replace using MPAndroidChart or graph containers in Android.

---

# 6. Prediction Result Screen / Section
## Purpose
Display backend-generated risk assessment.

## Must include
- Prediction title
- Risk probability percentage
- Risk label: Low / Medium / High
- Explanation panel listing top contributing vitals
- Short recommendation text
- Timestamp of prediction
- Button for “Refresh Prediction”
- Button for “View Full Patient Data”

## Optional enhanced content
- Confidence indicator
- Comparison with previous prediction
- Mini trend arrow showing whether risk is increasing

## Android mapping notes
This should later map to a prediction activity or fragment receiving JSON data from FastAPI.

---

# 7. Alerts Screen
## Purpose
Centralized list of patient alerts.

## Must include
- App bar title “Alerts”
- Filters:
  - All alerts
  - Critical alerts
  - Warning alerts
  - Resolved alerts
- Alert cards list

## Each alert card should show
- Patient name
- Patient ID
- Alert severity
- Alert type, e.g. Low SpO2 / Tachycardia / Hypotension
- Timestamp
- Short description
- Button “Open Patient”
- Button “Mark Resolved”

## Interaction expectations
- Open Patient navigates to patient detail page
- Resolve changes visual state
- Critical alerts should visually stand out strongly

## Android mapping notes
This should be easy to implement with RecyclerView and notification redirects.

---

# 8. Notification Redirect Demo Screen / State
## Purpose
Demonstrate how a high-priority notification would redirect to a specific patient page.

## Must include
- A visible mock notification component or modal preview
- Text such as:
  - “Critical Alert: Patient P102 — SpO2 dropped to 86%”
- CTA button: “Open Patient Record”

## Notes
This screen exists to make the redirection flow obvious for conversion into Android notification + intent behavior.

---

# 9. Add Note / Observation Screen
## Purpose
Allow staff to add patient observations.

## Must include
- Patient reference at top
- Text area for observation note
- Observation category selector
- Date and time selection UI
- Save button
- Cancel button

## Optional categories
- General observation
- Medication note
- Emergency note
- Follow-up note

## Android mapping notes
This screen should map well to EditText, Spinner, DatePickerDialog, TimePickerDialog, and SQLite insertion logic.

---

# 10. Settings Screen
## Purpose
App preferences and local configuration.

## Must include
- User profile summary
- Toggle / switch style controls for:
  - dark mode mock
  - notifications enabled
  - auto-refresh enabled
  - remember session
- Refresh interval dropdown
- Default ward/unit dropdown
- About app card
- Logout button

## About card should include
- App name
- Version placeholder
- Academic project disclaimer
- Team/student placeholder

## Android mapping notes
Should later map to SharedPreferences-backed settings UI.

---

# 11. Profile / About Screen (optional but recommended)
## Purpose
Professional finishing screen.

## Must include
- User name
- Role
- Organization / ICU unit placeholder
- App version
- About project explanation
- Contact / support placeholder

This can be merged with settings if needed, but generating it separately is useful.

---

# 12. Dialog / Toast / Status UI States
## Purpose
Show reusable interaction states that will later become Android dialogs and toast messages.

## Generate examples for:
- Logout confirmation dialog
- Delete note confirmation dialog
- Critical warning dialog
- Success toast
- Error toast
- Sync completed message

These do not need to be separate full pages, but should be included as UI states or modal examples.

---

## Required Components Across the Prototype
The generated frontend must visibly include or simulate the following components because they will later be converted into Android widgets:

- text inputs
- password input
- buttons
- dropdown/select fields
- checkbox or switch
- card lists
- status chips/badges
- graph placeholders
- alert banners
- modal/dialog overlays
- tab-like selectors
- date picker trigger control
- time picker trigger control
- notification preview component

---

## Data to Use in the Mock UI
Use realistic but fictional ICU patient data.

### Example patient fields
- Patient Name
- Patient ID
- Age
- Gender
- Bed Number
- Ward/Unit
- Heart Rate
- SpO2
- Blood Pressure
- Respiratory Rate
- Temperature
- Risk Level
- Last Updated

### Sample patients
Include around 5 to 8 patient cards with mixed conditions:
- at least 2 stable
- at least 2 warning
- at least 1 critical

### Example values
- Stable patient:
  - HR 82 bpm
  - SpO2 98%
  - BP 120/80
  - RR 16/min
  - Temp 98.4°F
- Warning patient:
  - HR 110 bpm
  - SpO2 93%
  - BP 102/68
  - RR 22/min
  - Temp 100.1°F
- Critical patient:
  - HR 132 bpm
  - SpO2 86%
  - BP 85/55
  - RR 30/min
  - Temp 101.3°F

---

## Navigation Flow Expectations
The prototype should clearly support this flow:

1. Splash
2. Login
3. Dashboard
4. Patient Details
5. Trends / Prediction / Alerts / Add Note
6. Settings

Also include visible navigation paths such as:
- dashboard to details
- details to trends
- alert to patient page
- settings to logout

If possible, use simple JS-based navigation between sections or pages.

---

## File Structure Expectations for Generated Frontend
Generate the frontend in a simple structure like:

```text
/critiwatch-frontend
  index.html
  login.html
  dashboard.html
  patient-detail.html
  trends.html
  alerts.html
  settings.html
  add-note.html
  css/
    styles.css
  js/
    app.js
    mock-data.js
  assets/
    icons/
    images/
```

If Stitch prefers a single-page structure, still organize code clearly and label each screen distinctly.

---

## Conversion Awareness: HTML/CSS/JS to Android XML
The generated frontend must be created with future Android conversion in mind.

This means:
- Use clear containers representing top bar, content section, bottom action area
- Keep forms vertically structured
- Use repeated cards for list items
- Use simple spacing and alignment rules
- Avoid CSS tricks that cannot map to Android layouts
- Keep graph areas inside rectangular containers
- Keep interaction labels explicit

For each major screen, structure content so it can later be mapped to:
- ConstraintLayout
- LinearLayout
- ScrollView
- RecyclerView item layouts
- Spinner
- Button
- EditText
- TextView
- CardView or MaterialCardView equivalent

---

## What to Optimize For
The generated frontend should optimize for:
- clarity of screen structure
- mobile realism
- Android convertibility
- academic project professionalism
- visual completeness for portfolio/demo use

Do not optimize for:
- advanced web engineering
- SEO
- desktop website behavior
- browser-heavy animations

---

## Output Request
Please generate:

1. A full frontend prototype in **plain HTML, CSS, and JavaScript**
2. Separate pages or clearly separated sections for each required screen
3. Realistic mock data populated into the UI
4. Clean CSS styling suitable for a healthcare dashboard
5. Simple JavaScript interactions for:
   - navigation
   - mock filter behavior
   - mock dialogs/modals
   - mock notifications
   - simple page state changes
6. Clear class names and section naming so manual Android conversion is easier

---

## Final Instruction
This frontend is being used as a **visual prototype for later Android Java + XML implementation**. Therefore, prioritize **screen completeness, component clarity, and mobile UI structure** over fancy web effects.

Design it like a serious engineering project prototype for a healthcare monitoring app.
