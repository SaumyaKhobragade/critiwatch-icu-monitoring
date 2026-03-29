package com.example.critiwatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.R;
import com.example.critiwatch.models.AlertItem;
import com.example.critiwatch.utils.Constants;

import java.util.List;
import java.util.Locale;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    public interface OnAlertActionListener {
        void onAlertSelected(AlertItem alertItem);

        void onOpenPatientFile(AlertItem alertItem);

        void onMarkResolved(AlertItem alertItem);
    }

    private final List<AlertItem> alertItems;
    private final OnAlertActionListener onAlertActionListener;

    public AlertAdapter(List<AlertItem> alertItems, OnAlertActionListener onAlertActionListener) {
        this.alertItems = alertItems;
        this.onAlertActionListener = onAlertActionListener;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertItem item = alertItems.get(position);

        holder.tvSeverityBadge.setText(item.getSeverity().toUpperCase(Locale.US));
        holder.tvAlertTimestamp.setText(item.getTimestamp());
        holder.tvAlertValue.setText(item.getValue());
        holder.tvAlertUnit.setText(item.getUnit());
        holder.tvAlertType.setText(item.getType());
        holder.tvAlertPatientName.setText(item.getPatientLabel());
        holder.tvAlertDescription.setText(item.getDescription());

        int chipBackgroundRes;
        int severityColor;
        if (Constants.RISK_CRITICAL.equalsIgnoreCase(item.getSeverity())) {
            chipBackgroundRes = R.drawable.bg_chip_critical;
            severityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_critical);
        } else if (Constants.RISK_WARNING.equalsIgnoreCase(item.getSeverity())) {
            chipBackgroundRes = R.drawable.bg_chip_warning;
            severityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_warning);
        } else {
            chipBackgroundRes = R.drawable.bg_chip_stable;
            severityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_stable);
        }

        holder.tvSeverityBadge.setBackgroundResource(chipBackgroundRes);
        holder.tvSeverityBadge.setTextColor(severityColor);
        holder.tvAlertValue.setTextColor(severityColor);
        holder.viewSeverityIndicator.setBackgroundColor(severityColor);

        View.OnClickListener alertClick = v -> {
            if (onAlertActionListener != null) {
                onAlertActionListener.onAlertSelected(item);
            }
        };
        holder.itemView.setOnClickListener(alertClick);
        holder.btnOpenPatientFile.setOnClickListener(v -> {
            if (onAlertActionListener != null) {
                onAlertActionListener.onOpenPatientFile(item);
            }
        });
        holder.btnMarkResolved.setOnClickListener(v -> {
            if (onAlertActionListener != null) {
                onAlertActionListener.onMarkResolved(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertItems.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        private final View viewSeverityIndicator;
        private final TextView tvSeverityBadge;
        private final TextView tvAlertTimestamp;
        private final TextView tvAlertValue;
        private final TextView tvAlertUnit;
        private final TextView tvAlertType;
        private final TextView tvAlertPatientName;
        private final TextView tvAlertDescription;
        private final Button btnOpenPatientFile;
        private final Button btnMarkResolved;

        AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSeverityIndicator = itemView.findViewById(R.id.viewSeverityIndicator);
            tvSeverityBadge = itemView.findViewById(R.id.tvSeverityBadge);
            tvAlertTimestamp = itemView.findViewById(R.id.tvAlertTimestamp);
            tvAlertValue = itemView.findViewById(R.id.tvAlertValue);
            tvAlertUnit = itemView.findViewById(R.id.tvAlertUnit);
            tvAlertType = itemView.findViewById(R.id.tvAlertType);
            tvAlertPatientName = itemView.findViewById(R.id.tvAlertPatientName);
            tvAlertDescription = itemView.findViewById(R.id.tvAlertDescription);
            btnOpenPatientFile = itemView.findViewById(R.id.btnOpenPatientFile);
            btnMarkResolved = itemView.findViewById(R.id.btnMarkResolved);
        }
    }
}
