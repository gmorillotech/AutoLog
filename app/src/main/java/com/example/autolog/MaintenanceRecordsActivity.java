package com.example.autolog;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autolog.data.AutoLogDatabase;
import com.example.autolog.data.ServiceRecord;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class MaintenanceRecordsActivity extends AppCompatActivity implements ServiceRecordAdapter.OnRecordInteractionListener {

    public static final String EXTRA_CAR_ID = "com.example.autolog.EXTRA_CAR_ID";
    public static final String EXTRA_CAR_NAME = "com.example.autolog.EXTRA_CAR_NAME";

    private AutoLogViewModel autoLogViewModel;
    private ServiceRecordAdapter adapter;
    private TextView noRecordsTextView;
    private FloatingActionButton fab;

    private long carId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_records);

        carId = getIntent().getLongExtra(EXTRA_CAR_ID, -1L);
        String carName = getIntent().getStringExtra(EXTRA_CAR_NAME);

        if (carId == -1L) {
            Log.e("MaintenanceRecords", "Invalid carId (-1) passed to activity. Finishing.");
            Toast.makeText(this, "Error: Could not load car records.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(carName != null ? carName : "Maintenance Records");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view_maintenance);
        noRecordsTextView = findViewById(R.id.text_no_records);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceRecordAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setListener(this);

        autoLogViewModel = new ViewModelProvider(this).get(AutoLogViewModel.class);
        autoLogViewModel.getRecordsForCar(carId).observe(this, serviceRecords -> {
            adapter.setRecords(serviceRecords);
            boolean hasRecords = serviceRecords != null && !serviceRecords.isEmpty();
            noRecordsTextView.setVisibility(hasRecords ? View.GONE : View.VISIBLE);
            recyclerView.setVisibility(hasRecords ? View.VISIBLE : View.GONE);
        });

        fab = findViewById(R.id.fab_add_record);
        fab.setOnClickListener(v -> showAddOrEditServiceRecordDialog(null));
    }

    @Override
    public void onEditRecord(ServiceRecord record) {
        showAddOrEditServiceRecordDialog(record);
    }

    @Override
    public void onDeleteRecord(ServiceRecord record) {
        autoLogViewModel.deleteServiceRecord(record);
        Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
    }

    private void showAddOrEditServiceRecordDialog(final ServiceRecord recordToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service_record, null);
        builder.setView(dialogView);

        final boolean isEditing = recordToEdit != null;
        builder.setTitle(isEditing ? "Edit Service Record" : "Add Service Record");

        final AutoCompleteTextView spinnerServiceType = dialogView.findViewById(R.id.spinner_service_type);
        final EditText inputDate = dialogView.findViewById(R.id.input_date_performed);
        final EditText inputMileage = dialogView.findViewById(R.id.input_mileage);
        final EditText inputNotes = dialogView.findViewById(R.id.input_notes);

        String[] serviceTypes = {"Oil Change", "Tire Rotation", "Brake Service", "Battery Replacement", "Air Filter", "Other"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, serviceTypes);
        spinnerServiceType.setAdapter(spinnerAdapter);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        inputDate.setFocusable(false);
        inputDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            if (isEditing && recordToEdit.getDatePerformed() != null) {
                LocalDate date = recordToEdit.getDatePerformed();
                c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                        inputDate.setText(selectedDate.format(formatter));
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        if (isEditing) {
            spinnerServiceType.setText(recordToEdit.getServiceType(), false);
            inputDate.setText(recordToEdit.getDatePerformed().format(formatter));
            inputMileage.setText(String.valueOf(recordToEdit.getMileage()));
            inputNotes.setText(recordToEdit.getNotes());
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String serviceType = spinnerServiceType.getText().toString();
            String dateStr = inputDate.getText().toString();
            String mileageStr = inputMileage.getText().toString();
            String notes = inputNotes.getText().toString();

            if (serviceType.isEmpty() || dateStr.isEmpty() || mileageStr.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                LocalDate dateObject = LocalDate.parse(dateStr, formatter);
                int mileage = Integer.parseInt(mileageStr);

                ServiceRecord recordToSave = isEditing ? recordToEdit : new ServiceRecord();
                if (!isEditing) {
                    recordToSave.setCarId(carId);
                }

                recordToSave.setServiceType(serviceType);
                recordToSave.setDatePerformed(dateObject);
                recordToSave.setMileage(mileage);
                recordToSave.setNotes(notes);

                if (isEditing) {
                    autoLogViewModel.updateServiceRecord(recordToSave);
                    Toast.makeText(this, "Record updated!", Toast.LENGTH_SHORT).show();
                } else {
                    autoLogViewModel.insertServiceRecord(recordToSave);
                    Toast.makeText(this, "Service record saved!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("AddEditServiceDialog", "Error parsing or saving record", e);
                Toast.makeText(this, "Error saving record. Please check values.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}
