package com.example.critiwatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.critiwatch.R;
import com.example.critiwatch.models.ClinicalNote;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ClinicalNoteAdapter extends RecyclerView.Adapter<ClinicalNoteAdapter.ClinicalNoteViewHolder> {

    private final List<ClinicalNote> notes = new ArrayList<>();

    @NonNull
    @Override
    public ClinicalNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinical_note, parent, false);
        return new ClinicalNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicalNoteViewHolder holder, int position) {
        ClinicalNote note = notes.get(position);
        holder.tvNoteText.setText(note.getNoteText());

        String timestamp = note.getUpdatedAt();
        if (timestamp == null || timestamp.trim().isEmpty()) {
            timestamp = note.getCreatedAt();
        }
        holder.tvNoteTimestamp.setText(
                timestamp == null || timestamp.trim().isEmpty()
                        ? "Saved recently"
                        : "Saved " + DateTimeUtils.toRelativeTime(timestamp)
        );
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void submitNotes(List<ClinicalNote> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    static class ClinicalNoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNoteText;
        private final TextView tvNoteTimestamp;

        ClinicalNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteText = itemView.findViewById(R.id.tvNoteText);
            tvNoteTimestamp = itemView.findViewById(R.id.tvNoteTimestamp);
        }
    }
}
