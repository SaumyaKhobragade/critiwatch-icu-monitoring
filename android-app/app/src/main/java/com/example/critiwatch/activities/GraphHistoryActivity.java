package com.example.critiwatch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.adapters.VitalHistoryAdapter;
import com.example.critiwatch.database.DatabaseSeeder;
import com.example.critiwatch.database.PatientDao;
import com.example.critiwatch.database.VitalDao;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.Constants;
import com.example.critiwatch.utils.DateTimeUtils;
import com.example.critiwatch.utils.SystemUiUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphHistoryActivity extends AppCompatActivity {

    private static final String METRIC_ALL = "All Vitals";
    private static final String METRIC_HEART_RATE = "Heart Rate";
    private static final String METRIC_SPO2 = "SpO2";
    private static final String METRIC_BLOOD_PRESSURE = "Blood Pressure";
    private static final String METRIC_TEMPERATURE = "Temperature";

    private final List<VitalSign> allVitalHistory = new ArrayList<>();
    private final List<VitalSign> visibleVitalHistory = new ArrayList<>();

    private String patientId;
    private String patientName;
    private String patientBed;
    private String selectedMetric = METRIC_ALL;
    private Date activeFilterStart;
    private Calendar filterCalendar;
    private boolean hasDateSelection;
    private boolean hasTimeSelection;

    private VitalDao vitalDao;
    private PatientDao patientDao;
    private VitalHistoryAdapter vitalHistoryAdapter;

    private Spinner spinnerMetric;
    private TextView tvEmptyHistory;
    private TextView tvSelectedDate;
    private TextView tvSelectedTime;
    private Button btnApplyFilter;
    private RecyclerView rvVitalHistory;
    private CardView cardHeartRateChart;
    private CardView cardSpo2Chart;
    private CardView cardBloodPressureChart;
    private CardView cardTemperatureChart;
    private LineChart chartHeartRate;
    private LineChart chartSpo2;
    private LineChart chartBloodPressure;
    private LineChart chartTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graph_history);

        DatabaseSeeder.seedIfEmpty(this);
        vitalDao = new VitalDao(this);
        patientDao = new PatientDao(this);

        SystemUiUtils.applySystemBarStyling(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!readPatientExtras()) {
            return;
        }

        bindViews();
        bindPatientHeader();
        setupMetricSpinner();
        setupDateTimeFilterControls();
        setupReadingRecyclerView();
        configureCharts();
        setupClickActions();
        setupBottomNavigation();
        reloadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadHistory();
    }

    private void bindViews() {
        spinnerMetric = findViewById(R.id.spinnerMetric);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);

        rvVitalHistory = findViewById(R.id.rvVitalHistory);
        if (rvVitalHistory == null) {
            int fallbackId = getResources().getIdentifier("rvRecentReadings", "id", getPackageName());
            if (fallbackId != 0) {
                rvVitalHistory = findViewById(fallbackId);
            }
        }

        cardHeartRateChart = findViewById(R.id.cardHeartRateChart);
        cardSpo2Chart = findViewById(R.id.cardSpo2Chart);
        cardBloodPressureChart = findViewById(R.id.cardBloodPressureChart);
        cardTemperatureChart = findViewById(R.id.cardTemperatureChart);

        chartHeartRate = findViewById(R.id.chartHeartRate);
        chartSpo2 = findViewById(R.id.chartSpo2);
        chartBloodPressure = findViewById(R.id.chartBloodPressure);
        chartTemperature = findViewById(R.id.chartTemperature);
    }

    private boolean readPatientExtras() {
        Intent source = getIntent();
        if (source == null) {
            Toast.makeText(this, "Missing patient context", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        patientId = source.getStringExtra(Constants.EXTRA_PATIENT_ID);
        patientName = source.getStringExtra(Constants.EXTRA_PATIENT_NAME);
        patientBed = source.getStringExtra(Constants.EXTRA_PATIENT_BED);

        if (patientId == null || patientId.trim().isEmpty()) {
            Toast.makeText(this, "Missing patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        int id = parseId(patientId);
        if (id <= 0) {
            Toast.makeText(this, "Invalid patient id", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        Patient patient = patientDao.getPatientById(id);
        if (patient != null) {
            if (patientName == null || patientName.trim().isEmpty()) {
                patientName = patient.getName();
            }
            if (patientBed == null || patientBed.trim().isEmpty()) {
                patientBed = patient.getBedNumber();
            }
        }

        if (patientName == null || patientName.trim().isEmpty()) {
            patientName = "Unknown Patient";
        }
        if (patientBed == null || patientBed.trim().isEmpty()) {
            patientBed = "-";
        }
        return true;
    }

    private void bindPatientHeader() {
        TextView tvPatientName = findViewById(R.id.tvPatientName);
        TextView tvPatientMeta = findViewById(R.id.tvPatientMeta);

        if (tvPatientName != null) {
            tvPatientName.setText(patientName);
        }
        if (tvPatientMeta != null) {
            tvPatientMeta.setText(patientId + " • Bed " + patientBed);
        }
    }

    private void setupMetricSpinner() {
        if (spinnerMetric == null) {
            return;
        }

        List<String> metrics = new ArrayList<>();
        metrics.add(METRIC_ALL);
        metrics.add(METRIC_HEART_RATE);
        metrics.add(METRIC_SPO2);
        metrics.add(METRIC_BLOOD_PRESSURE);
        metrics.add(METRIC_TEMPERATURE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                metrics
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetric.setAdapter(adapter);
        spinnerMetric.setSelection(0);
        spinnerMetric.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedMetric = metrics.get(position);
                applyHistoryFiltersAndRender();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedMetric = METRIC_ALL;
                applyHistoryFiltersAndRender();
            }
        });
    }

    private void setupDateTimeFilterControls() {
        if (tvSelectedDate == null || tvSelectedTime == null || btnApplyFilter == null) {
            return;
        }

        filterCalendar = Calendar.getInstance();
        hasDateSelection = false;
        hasTimeSelection = false;
        renderSelectedFilterText();

        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        tvSelectedTime.setOnClickListener(v -> showTimePickerDialog());
        btnApplyFilter.setOnClickListener(v -> {
            if (!hasDateSelection && !hasTimeSelection) {
                activeFilterStart = null;
                applyHistoryFiltersAndRender();
                Toast.makeText(this, "Date/time filter cleared", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hasDateSelection && hasTimeSelection) {
                // If only time is selected, apply it from today.
                Calendar today = Calendar.getInstance();
                filterCalendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
                filterCalendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
                filterCalendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
                hasDateSelection = true;
            }

            if (hasDateSelection && !hasTimeSelection) {
                filterCalendar.set(Calendar.HOUR_OF_DAY, 0);
                filterCalendar.set(Calendar.MINUTE, 0);
                filterCalendar.set(Calendar.SECOND, 0);
                filterCalendar.set(Calendar.MILLISECOND, 0);
            }

            activeFilterStart = filterCalendar.getTime();
            applyHistoryFiltersAndRender();
            Toast.makeText(this, "Filter applied", Toast.LENGTH_SHORT).show();
        });

        tvSelectedDate.setOnLongClickListener(v -> {
            hasDateSelection = false;
            activeFilterStart = null;
            renderSelectedFilterText();
            applyHistoryFiltersAndRender();
            Toast.makeText(this, "Date filter cleared", Toast.LENGTH_SHORT).show();
            return true;
        });

        tvSelectedTime.setOnLongClickListener(v -> {
            hasTimeSelection = false;
            activeFilterStart = null;
            renderSelectedFilterText();
            applyHistoryFiltersAndRender();
            Toast.makeText(this, "Time filter cleared", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void showDatePickerDialog() {
        Calendar source = filterCalendar == null ? Calendar.getInstance() : filterCalendar;
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    filterCalendar.set(Calendar.YEAR, year);
                    filterCalendar.set(Calendar.MONTH, month);
                    filterCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    hasDateSelection = true;
                    renderSelectedFilterText();
                },
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH),
                source.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showTimePickerDialog() {
        Calendar source = filterCalendar == null ? Calendar.getInstance() : filterCalendar;
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    filterCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    filterCalendar.set(Calendar.MINUTE, minute);
                    filterCalendar.set(Calendar.SECOND, 0);
                    filterCalendar.set(Calendar.MILLISECOND, 0);
                    hasTimeSelection = true;
                    renderSelectedFilterText();
                },
                source.get(Calendar.HOUR_OF_DAY),
                source.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }

    private void renderSelectedFilterText() {
        if (tvSelectedDate != null) {
            if (hasDateSelection) {
                tvSelectedDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(filterCalendar.getTime()));
            } else {
                tvSelectedDate.setText("Select Date");
            }
        }
        if (tvSelectedTime != null) {
            if (hasTimeSelection) {
                tvSelectedTime.setText(new SimpleDateFormat("HH:mm", Locale.US).format(filterCalendar.getTime()));
            } else {
                tvSelectedTime.setText("Select Time");
            }
        }
    }

    private void setupReadingRecyclerView() {
        if (rvVitalHistory == null) {
            return;
        }
        rvVitalHistory.setLayoutManager(new LinearLayoutManager(this));
        vitalHistoryAdapter = new VitalHistoryAdapter(visibleVitalHistory);
        rvVitalHistory.setAdapter(vitalHistoryAdapter);
    }

    private void configureCharts() {
        configureSingleMetricChart(chartHeartRate, false);
        configureSingleMetricChart(chartSpo2, false);
        configureSingleMetricChart(chartTemperature, false);
        configureSingleMetricChart(chartBloodPressure, true);
    }

    private void configureSingleMetricChart(LineChart chart, boolean showLegend) {
        if (chart == null) {
            return;
        }
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("No data available");
        chart.setNoDataTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setExtraBottomOffset(4f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-25f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.border_color));

        chart.getAxisRight().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(showLegend);
        legend.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void reloadHistory() {
        allVitalHistory.clear();
        int id = parseId(patientId);
        if (id > 0) {
            allVitalHistory.addAll(vitalDao.getVitalsByPatientId(id));
        }
        applyHistoryFiltersAndRender();
    }

    private void applyHistoryFiltersAndRender() {
        visibleVitalHistory.clear();
        if (activeFilterStart == null) {
            visibleVitalHistory.addAll(allVitalHistory);
        } else {
            for (VitalSign vitalSign : allVitalHistory) {
                Date recordedAt = DateTimeUtils.parse(vitalSign.getTimestamp());
                if (recordedAt == null || !recordedAt.before(activeFilterStart)) {
                    visibleVitalHistory.add(vitalSign);
                }
            }
        }

        if (vitalHistoryAdapter != null) {
            vitalHistoryAdapter.notifyDataSetChanged();
        }
        updateEmptyState();
        updateChartVisibility();
        renderCharts();
    }

    private void updateEmptyState() {
        boolean empty = visibleVitalHistory.isEmpty();
        if (tvEmptyHistory != null) {
            tvEmptyHistory.setVisibility(empty ? View.VISIBLE : View.GONE);
            if (empty) {
                tvEmptyHistory.setText("No vital history available for this patient yet.");
            }
        }
        if (rvVitalHistory != null) {
            rvVitalHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }

    private void updateChartVisibility() {
        if (visibleVitalHistory.isEmpty()) {
            setVisible(cardHeartRateChart, false);
            setVisible(cardSpo2Chart, false);
            setVisible(cardBloodPressureChart, false);
            setVisible(cardTemperatureChart, false);
            return;
        }

        if (METRIC_ALL.equals(selectedMetric)) {
            setVisible(cardHeartRateChart, true);
            setVisible(cardSpo2Chart, true);
            setVisible(cardBloodPressureChart, true);
            setVisible(cardTemperatureChart, true);
            return;
        }
        setVisible(cardHeartRateChart, METRIC_HEART_RATE.equals(selectedMetric));
        setVisible(cardSpo2Chart, METRIC_SPO2.equals(selectedMetric));
        setVisible(cardBloodPressureChart, METRIC_BLOOD_PRESSURE.equals(selectedMetric));
        setVisible(cardTemperatureChart, METRIC_TEMPERATURE.equals(selectedMetric));
    }

    private void renderCharts() {
        List<VitalSign> chartVitals = getVitalsChronologically(visibleVitalHistory);
        List<String> xLabels = buildTimeLabels(chartVitals);

        renderSingleLineChart(
                chartHeartRate,
                buildHeartRateEntries(chartVitals),
                "Heart Rate",
                ContextCompat.getColor(this, R.color.primary_accent),
                xLabels,
                35f,
                190f
        );

        renderSingleLineChart(
                chartSpo2,
                buildSpo2Entries(chartVitals),
                "SpO2",
                ContextCompat.getColor(this, R.color.info_accent),
                xLabels,
                70f,
                100f
        );

        renderBloodPressureChart(
                chartBloodPressure,
                buildSystolicEntries(chartVitals),
                buildDiastolicEntries(chartVitals),
                xLabels
        );

        renderSingleLineChart(
                chartTemperature,
                buildTemperatureEntries(chartVitals),
                "Temperature",
                ContextCompat.getColor(this, R.color.status_warning),
                xLabels,
                33f,
                42f
        );
    }

    private void renderSingleLineChart(
            LineChart chart,
            List<Entry> entries,
            String label,
            int color,
            List<String> xLabels,
            float yMin,
            float yMax
    ) {
        if (chart == null) {
            return;
        }
        if (entries.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        LineDataSet dataSet = createDataSet(entries, label, color);
        applyYAxisBounds(chart, entries, yMin, yMax);
        chart.setData(new LineData(dataSet));
        applyXAxisLabels(chart, xLabels);
        chart.invalidate();
    }

    private void renderBloodPressureChart(
            LineChart chart,
            List<Entry> systolicEntries,
            List<Entry> diastolicEntries,
            List<String> xLabels
    ) {
        if (chart == null) {
            return;
        }
        if (systolicEntries.isEmpty() && diastolicEntries.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        LineDataSet systolicSet = createDataSet(
                systolicEntries,
                "Systolic",
                ContextCompat.getColor(this, R.color.status_critical)
        );
        LineDataSet diastolicSet = createDataSet(
                diastolicEntries,
                "Diastolic",
                ContextCompat.getColor(this, R.color.status_warning)
        );

        LineData lineData = new LineData();
        if (!systolicEntries.isEmpty()) {
            lineData.addDataSet(systolicSet);
        }
        if (!diastolicEntries.isEmpty()) {
            lineData.addDataSet(diastolicSet);
        }

        List<Entry> mergedEntries = new ArrayList<>();
        mergedEntries.addAll(systolicEntries);
        mergedEntries.addAll(diastolicEntries);
        applyYAxisBounds(chart, mergedEntries, 40f, 220f);
        chart.setData(lineData);
        applyXAxisLabels(chart, xLabels);
        chart.invalidate();
    }

    private LineDataSet createDataSet(List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2.2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(entries.size() <= 24);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(2.4f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setHighLightColor(color);
        return dataSet;
    }

    private void applyXAxisLabels(LineChart chart, List<String> labels) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(Math.min(6, Math.max(2, labels.size())), true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index < 0 || index >= labels.size()) {
                    return "";
                }
                return labels.get(index);
            }
        });
    }

    private void applyYAxisBounds(LineChart chart, List<Entry> entries, float hardMin, float hardMax) {
        if (chart == null || entries == null || entries.isEmpty()) {
            return;
        }

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (Entry entry : entries) {
            min = Math.min(min, entry.getY());
            max = Math.max(max, entry.getY());
        }

        if (min == Float.MAX_VALUE || max == Float.MIN_VALUE) {
            return;
        }

        float padding = Math.max(2f, (max - min) * 0.15f);
        float axisMin = min - padding;
        float axisMax = max + padding;

        if (!Float.isNaN(hardMin)) {
            axisMin = Math.max(hardMin, axisMin);
        }
        if (!Float.isNaN(hardMax)) {
            axisMax = Math.min(hardMax, axisMax);
        }
        if (axisMax - axisMin < 1f) {
            axisMax = axisMin + 1f;
        }

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(axisMin);
        leftAxis.setAxisMaximum(axisMax);
    }

    private List<Entry> buildHeartRateEntries(List<VitalSign> vitals) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < vitals.size(); i++) {
            entries.add(new Entry(i, vitals.get(i).getHeartRate()));
        }
        return entries;
    }

    private List<Entry> buildSpo2Entries(List<VitalSign> vitals) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < vitals.size(); i++) {
            entries.add(new Entry(i, vitals.get(i).getSpo2()));
        }
        return entries;
    }

    private List<Entry> buildSystolicEntries(List<VitalSign> vitals) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < vitals.size(); i++) {
            entries.add(new Entry(i, vitals.get(i).getSystolicBp()));
        }
        return entries;
    }

    private List<Entry> buildDiastolicEntries(List<VitalSign> vitals) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < vitals.size(); i++) {
            entries.add(new Entry(i, vitals.get(i).getDiastolicBp()));
        }
        return entries;
    }

    private List<Entry> buildTemperatureEntries(List<VitalSign> vitals) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < vitals.size(); i++) {
            entries.add(new Entry(i, (float) vitals.get(i).getTemperature()));
        }
        return entries;
    }

    private List<String> buildTimeLabels(List<VitalSign> vitals) {
        List<String> labels = new ArrayList<>();
        for (VitalSign vitalSign : vitals) {
            labels.add(formatShortTime(vitalSign.getTimestamp()));
        }
        return labels;
    }

    private List<VitalSign> getVitalsChronologically(List<VitalSign> source) {
        List<VitalSign> sorted = new ArrayList<>(source);
        Collections.sort(sorted, new Comparator<VitalSign>() {
            @Override
            public int compare(VitalSign first, VitalSign second) {
                Date firstDate = DateTimeUtils.parse(first.getTimestamp());
                Date secondDate = DateTimeUtils.parse(second.getTimestamp());
                if (firstDate == null && secondDate == null) {
                    return 0;
                }
                if (firstDate == null) {
                    return -1;
                }
                if (secondDate == null) {
                    return 1;
                }
                return firstDate.compareTo(secondDate);
            }
        });
        return sorted;
    }

    private String formatShortTime(String timestamp) {
        Date parsed = DateTimeUtils.parse(timestamp);
        if (parsed == null) {
            return "";
        }
        return new SimpleDateFormat("HH:mm", Locale.US).format(parsed);
    }

    private void setupClickActions() {
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        Button btnBack = findOptionalButtonByName("btnBack");
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Button btnViewFullHistory = findViewById(R.id.btnViewFullHistory);
        if (btnViewFullHistory != null) {
            btnViewFullHistory.setOnClickListener(v ->
                    Toast.makeText(this, "Loaded " + visibleVitalHistory.size() + " local readings", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private Button findOptionalButtonByName(String idName) {
        int viewId = getResources().getIdentifier(idName, "id", getPackageName());
        if (viewId == 0) {
            return null;
        }
        View view = findViewById(viewId);
        return view instanceof Button ? (Button) view : null;
    }

    private void setVisible(View view, boolean visible) {
        if (view == null) {
            return;
        }
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private int parseId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}
