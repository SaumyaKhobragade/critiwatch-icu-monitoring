package com.example.critiwatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.R;
import com.example.critiwatch.models.Patient;
import com.example.critiwatch.utils.Constants;

import java.util.List;
import java.util.Locale;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    private final List<Patient> patients;
    private final OnPatientClickListener onPatientClickListener;

    public PatientAdapter(List<Patient> patients, OnPatientClickListener onPatientClickListener) {
        this.patients = patients;
        this.onPatientClickListener = onPatientClickListener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);

        holder.tvPatientName.setText(safe(patient.getName(), "Unknown Patient"));
        holder.tvPatientMeta.setText(safe(patient.getMetaLine(), "Bed -"));
        holder.tvHeartRate.setText(String.valueOf(patient.getHeartRate()));
        holder.tvSpo2.setText(patient.getSpo2() + "%");
        holder.tvBloodPressure.setText(safe(patient.getBloodPressure(), "--/--"));
        holder.tvRespRate.setText(String.valueOf(patient.getRespiratoryRate()));
        holder.tvLastUpdated.setText("Last updated: " + safe(patient.getLastUpdated(), "--"));

        String risk = safe(patient.getRiskLevel(), Constants.RISK_STABLE);
        String riskUpper = risk.toUpperCase(Locale.US);
        holder.tvStatusBadge.setText(riskUpper);

        if (Constants.RISK_CRITICAL.equalsIgnoreCase(risk)) {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_critical);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_critical));
        } else if (Constants.RISK_WARNING.equalsIgnoreCase(risk)) {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_warning);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_warning));
        } else {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_chip_stable);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_stable));
        }

        View.OnClickListener clickListener = v -> {
            if (onPatientClickListener != null) {
                onPatientClickListener.onPatientClick(patient);
            }
        };
        holder.itemView.setOnClickListener(clickListener);
        holder.tvViewDetails.setOnClickListener(clickListener);
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPatientName;
        private final TextView tvPatientMeta;
        private final TextView tvStatusBadge;
        private final TextView tvHeartRate;
        private final TextView tvSpo2;
        private final TextView tvBloodPressure;
        private final TextView tvRespRate;
        private final TextView tvLastUpdated;
        private final TextView tvViewDetails;

        PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientMeta = itemView.findViewById(R.id.tvPatientMeta);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvHeartRate = itemView.findViewById(R.id.tvHeartRate);
            tvSpo2 = itemView.findViewById(R.id.tvSpO2);
            tvBloodPressure = itemView.findViewById(R.id.tvBloodPressure);
            tvRespRate = itemView.findViewById(R.id.tvRespRate);
            tvLastUpdated = itemView.findViewById(R.id.tvLastUpdated);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
        }
    }
}
