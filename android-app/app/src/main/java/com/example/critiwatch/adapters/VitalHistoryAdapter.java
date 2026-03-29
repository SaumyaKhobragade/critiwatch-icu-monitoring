package com.example.critiwatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.R;
import com.example.critiwatch.models.VitalSign;

import java.util.List;

public class VitalHistoryAdapter extends RecyclerView.Adapter<VitalHistoryAdapter.VitalHistoryViewHolder> {

    private final List<VitalSign> vitalSigns;

    public VitalHistoryAdapter(List<VitalSign> vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    @NonNull
    @Override
    public VitalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading, parent, false);
        return new VitalHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VitalHistoryViewHolder holder, int position) {
        VitalSign vitalSign = vitalSigns.get(position);
        holder.tvReadingTime.setText(vitalSign.getTimestamp());
        holder.tvHistoryHr.setText(String.valueOf(vitalSign.getHeartRate()));
        holder.tvHistorySpO2.setText(vitalSign.getSpo2() + "%");
        holder.tvHistoryBp.setText(vitalSign.getBloodPressure());
        holder.tvHistoryRr.setText(String.valueOf(vitalSign.getRespiratoryRate()));
        holder.tvHistoryTemp.setText(vitalSign.getTemperatureText() + "C");
    }

    @Override
    public int getItemCount() {
        return vitalSigns.size();
    }

    static class VitalHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvReadingTime;
        private final TextView tvHistoryHr;
        private final TextView tvHistorySpO2;
        private final TextView tvHistoryBp;
        private final TextView tvHistoryRr;
        private final TextView tvHistoryTemp;

        VitalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReadingTime = itemView.findViewById(R.id.tvReadingTime);
            tvHistoryHr = itemView.findViewById(R.id.tvHistoryHR);
            tvHistorySpO2 = itemView.findViewById(R.id.tvHistorySpO2);
            tvHistoryBp = itemView.findViewById(R.id.tvHistoryBP);
            tvHistoryRr = itemView.findViewById(R.id.tvHistoryRR);
            tvHistoryTemp = itemView.findViewById(R.id.tvHistoryTemp);
        }
    }
}
