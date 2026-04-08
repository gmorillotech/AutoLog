package com.example.autolog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.autolog.data.ServiceRecord;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceRecordAdapter extends RecyclerView.Adapter<ServiceRecordAdapter.ServiceRecordViewHolder> {

    private List<ServiceRecord> serviceRecords = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private OnRecordInteractionListener listener;

    public interface OnRecordInteractionListener {
        void onEditRecord(ServiceRecord record);
        void onDeleteRecord(ServiceRecord record);
    }

    public void setListener(OnRecordInteractionListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ServiceRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_record_row, parent, false);
        return new ServiceRecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRecordViewHolder holder, int position) {
        ServiceRecord currentRecord = serviceRecords.get(position);

        holder.serviceType.setText(currentRecord.getServiceType());
        holder.datePerformed.setText(currentRecord.getDatePerformed().format(DATE_FORMATTER));

        String formattedMiles = String.format(Locale.US, "%,d Miles", currentRecord.getMileage());
        holder.mileage.setText(formattedMiles);

        String notes = currentRecord.getNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            holder.notes.setText(notes);
            holder.notes.setVisibility(View.VISIBLE);
        } else {
            holder.notes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return serviceRecords.size();
    }

    public void setRecords(List<ServiceRecord> records) {
        this.serviceRecords = records;
        notifyDataSetChanged();
    }

    class ServiceRecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView serviceType;
        private final TextView datePerformed;
        private final TextView mileage;
        private final TextView notes;
        private final ImageView editButton;
        private final ImageView deleteButton;

        public ServiceRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceType = itemView.findViewById(R.id.text_service_type);
            datePerformed = itemView.findViewById(R.id.text_date_performed);
            mileage = itemView.findViewById(R.id.text_mileage);
            notes = itemView.findViewById(R.id.text_notes);
            editButton = itemView.findViewById(R.id.button_edit_record);
            deleteButton = itemView.findViewById(R.id.delete_record);


            editButton.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditRecord(serviceRecords.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    ServiceRecord recordToDelete = serviceRecords.get(position);

                    new MaterialAlertDialogBuilder(itemView.getContext())
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this service record?")
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Delete", (dialog, which) -> {
                                listener.onDeleteRecord(recordToDelete);
                            })
                            .show();
                }
            });
        }
    }
}
