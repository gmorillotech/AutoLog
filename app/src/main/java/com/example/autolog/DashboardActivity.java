package com.example.autolog;

import android.app.Activity;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.autolog.data.Car;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DashboardActivity extends AppCompatActivity {

    private Car selectedCar;
    private View includedCarCard;
    private AutoLogViewModel autoLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        autoLogViewModel = new ViewModelProvider(this).get(AutoLogViewModel.class);

        includedCarCard = findViewById(R.id.dashboard_car_card);

        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("");
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        }

        MaterialButton maintenanceButton = findViewById(R.id.btn_maintenance_records);
        MaterialButton modificationsButton = findViewById(R.id.btn_modifications_log);
        maintenanceButton.setEnabled(false);

        getSupportFragmentManager().setFragmentResultListener("REQUEST_CAR_UPDATE", this, ((requestKey, result) -> {
            Car updatedCar = result.getParcelable("UPDATED_CAR");

            if(updatedCar != null){
                this.selectedCar = updatedCar;
                updateUiWithCarData();
            }

        }));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_CAR")) {
            Car staleCar = intent.getParcelableExtra("SELECTED_CAR");

            if (staleCar != null) {
                autoLogViewModel.getCarById(staleCar.getCarId()).observe(this, freshCar -> {
                    if (freshCar != null) {
                        this.selectedCar = freshCar;
                        updateUiWithCarData();
                        maintenanceButton.setEnabled(true);
                    }
                });
            }
        }

        maintenanceButton.setOnClickListener(v -> {
            if (selectedCar == null) {
                Toast.makeText(DashboardActivity.this, "Car data is still loading...", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent newIntent = new Intent(DashboardActivity.this, MaintenanceRecordsActivity.class);
            String carName = selectedCar.getStrMake() + " " + selectedCar.getStrModel();
            newIntent.putExtra(MaintenanceRecordsActivity.EXTRA_CAR_ID, selectedCar.getCarId());
            newIntent.putExtra(MaintenanceRecordsActivity.EXTRA_CAR_NAME, carName);
            startActivity(newIntent);
        });

        modificationsButton.setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUiWithCarData() {
        if (selectedCar != null && includedCarCard != null) {

            ImageView carImage = includedCarCard.findViewById(R.id.car_image);
            TextView yearText = includedCarCard.findViewById(R.id.year);
            TextView make_and_model = includedCarCard.findViewById(R.id.make_and_model);
            TextView vinText = includedCarCard.findViewById(R.id.vin);
            ImageView updateButton = includedCarCard.findViewById(R.id.update);
            ImageView deleteButton = includedCarCard.findViewById(R.id.delete);

            TextView mileageText = findViewById(R.id.miles);

            LinearLayout oilContainer = findViewById(R.id.container_oil);
            LinearLayout tireContainer = findViewById(R.id.container_tire);
            LinearLayout brakeContainer = findViewById(R.id.container_brake);
            LinearLayout registrationContainer = findViewById(R.id.container_registration);
            LinearLayout wipersContainer = findViewById(R.id.container_wipers);

            includedCarCard.setVisibility(View.VISIBLE);
            yearText.setText(String.valueOf(selectedCar.getYear()));
            make_and_model.setText(selectedCar.getStrMake() + " " + selectedCar.getStrModel());

            if (oilContainer != null) {
                oilContainer.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Oil Change Options")
                            .setItems(new String[]{"I Just Performed This Service (Reset Progress)", "Set or Change Interval"}, (dialog, which) -> {
                                if (which == 0) {
                                    performOilChangeReset(true);
                                } else {
                                    showOilIntervalDialog();
                                }
                            })
                            .show();
                });
            }
            if (tireContainer != null) {
                tireContainer.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Tire Rotation Options")
                            .setItems(new String[]{"I Just Performed This Service (Reset Progress)", "Set or Change Interval"}, (dialog, which) -> {
                                if (which == 0) {
                                    performTireRotationReset();
                                } else {
                                    showTireIntervalDialog();
                                }
                            })
                            .show();
                });
            }
            if (brakeContainer != null) {
                brakeContainer.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Brake Inspection Options")
                            .setItems(new String[]{"I Just Performed This Service (Reset Progress)", "Set or Change Interval"}, (dialog, which) -> {
                                if (which == 0) {
                                    performBrakeCheckReset();
                                } else {
                                    showBrakeIntervalDialog();
                                }
                            })
                            .show();
                });
            }
            if (registrationContainer != null) {
                registrationContainer.setOnClickListener(v -> showRegistrationIntervalDialog());
            }
            if (wipersContainer != null) {
                wipersContainer.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Wiper Replacement Options")
                            .setItems(new String[]{"I Just Performed This Service (Reset Progress)", "Set or Change Interval"}, (dialog, which) -> {
                                if (which == 0) {
                                    performWipersChangeReset();
                                } else {
                                    showWipersIntervalDialog();
                                }
                            })
                            .show();
                });
            }

            if(mileageText != null){
                String formattedMiles = String.format(Locale.US, "%,d", selectedCar.getMiles());
                mileageText.setText(formattedMiles + " Miles");
            }

            if(deleteButton != null){
                deleteButton.setVisibility(View.GONE);
            }

            if (updateButton != null) {
                updateButton.setOnClickListener(v->{
                    UpdateCarFragment editFragment = UpdateCarFragment.newInstance(selectedCar);
                    editFragment.show(getSupportFragmentManager(),"UpdateCarFragment");
                });
            }

            if (selectedCar.getImagePath() != null && !selectedCar.getImagePath().isEmpty()) {
                Glide.with(this)
                        .load(selectedCar.getImagePath())
                        .placeholder(R.drawable.car_placeholder_foreground)
                        .error(R.drawable.car_placeholder_foreground)
                        .into(carImage);
            } else {
                carImage.setImageResource(R.drawable.car_placeholder_foreground);
            }

            updateAllProgressBars(selectedCar);
        }
    }


    private void showRegistrationIntervalDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Set Registration Info");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_and_spinner, null);
        builder.setView(dialogView);

        EditText dateInput = dialogView.findViewById(R.id.input_date);
        Spinner intervalSpinner = dialogView.findViewById(R.id.spinner_interval);
        final LocalDate[] selectedDate = { selectedCar.getNextRegistrationDate() };
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        dateInput.setFocusable(false);
        dateInput.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            if (selectedDate[0] != null) {
                c.set(selectedDate[0].getYear(), selectedDate[0].getMonthValue() - 1, selectedDate[0].getDayOfMonth());
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedDate[0] = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        dateInput.setText(selectedDate[0].format(formatter));
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        List<String> intervalOptions = new ArrayList<>();
        List<Integer> intervalValuesMonths = new ArrayList<>();
        intervalOptions.add("Not Set");
        intervalValuesMonths.add(0);
        for (int i = 1; i <= 10; i++) {
            intervalOptions.add(i + (i == 1 ? " year" : " years"));
            intervalValuesMonths.add(i * 12);
        }
        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervalOptions);
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(intervalAdapter);

        if (selectedDate[0] != null) {
            dateInput.setText(selectedDate[0].format(formatter));
        }
        int currentIntervalMonths = selectedCar.getRegistrationIntervalMonths();
        intervalSpinner.setSelection(intervalValuesMonths.indexOf(currentIntervalMonths));


        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int selectionPosition = intervalSpinner.getSelectedItemPosition();
            int intervalMonths = intervalValuesMonths.get(selectionPosition);
            LocalDate chosenDate = selectedDate[0];

            boolean didSave = saveRegistrationInfo(chosenDate, intervalMonths);

            if (didSave) {
                dialog.dismiss();
            }
        });
    }



    private void showOilIntervalDialog() {
        final String[] options = {
                "3,000 miles or 3 months",
                "5,000 miles or 6 months",
                "7,500 miles or 12 months"
        };

        final int[] milesValues = {3000, 5000, 7500};
        final int[] monthsValues = {3, 6, 12};

        int currentSelection = -1;
        for (int i = 0; i < milesValues.length; i++) {
            if (selectedCar.getOilIntervalMiles() == milesValues[i] &&
                    selectedCar.getOilIntervalMonths() == monthsValues[i]) {
                currentSelection = i;
                break;
            }
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select Oil Change Interval");
        builder.setSingleChoiceItems(options, currentSelection, null);

        builder.setPositiveButton("Save", (dialog, which) -> {
            int selectedPosition = ((androidx.appcompat.app.AlertDialog) dialog).getListView().getCheckedItemPosition();
            if (selectedPosition != -1) {
                int selectedMiles = milesValues[selectedPosition];
                int selectedMonths = monthsValues[selectedPosition];

                selectedCar.setOilIntervalMiles(selectedMiles);
                selectedCar.setOilIntervalMonths(selectedMonths);

                performOilChangeReset(false);
                Toast.makeText(this, "Oil interval saved!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showTireIntervalDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Tire Rotation Interval");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_dual_spinner, null);
        builder.setView(dialogView);

        Spinner milesSpinner = dialogView.findViewById(R.id.spinner_miles);
        Spinner monthsSpinner = dialogView.findViewById(R.id.spinner_months);

        List<String> milesOptions = new ArrayList<>();
        Integer[] milesValuesArray = {0, 3000, 5000, 7500, 10000, 12500, 15000};
        List<Integer> milesValues = new ArrayList<>(List.of(milesValuesArray));

        milesOptions.add("Not Set");
        for (int i = 1; i < milesValues.size(); i++) {
            milesOptions.add(String.format(Locale.US, "%,d miles", milesValues.get(i)));
        }
        ArrayAdapter<String> milesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, milesOptions);
        milesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        milesSpinner.setAdapter(milesAdapter);

        List<String> monthsOptions = new ArrayList<>();
        Integer[] monthsValuesArray = {0, 3, 6, 12, 18, 24};
        List<Integer> monthsValues = new ArrayList<>(List.of(monthsValuesArray));

        monthsOptions.add("Not Set");
        for (int i = 1; i < monthsValues.size(); i++) {
            monthsOptions.add(monthsValues.get(i) + " months");
        }
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsOptions);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(monthsAdapter);

        int currentMiles = selectedCar.getTireIntervalMiles();
        int currentMonths = selectedCar.getTireIntervalMonths();
        milesSpinner.setSelection(milesValues.indexOf(currentMiles));
        monthsSpinner.setSelection(monthsValues.indexOf(currentMonths));

        builder.setPositiveButton("Save", (dialog, which) -> {
            int milesPosition = milesSpinner.getSelectedItemPosition();
            int monthsPosition = monthsSpinner.getSelectedItemPosition();

            int selectedMiles = milesValues.get(milesPosition);
            int selectedMonths = monthsValues.get(monthsPosition);

            selectedCar.setTireIntervalMiles(selectedMiles);
            selectedCar.setTireIntervalMonths(selectedMonths);

            performTireRotationReset();
            Toast.makeText(this, "Tire interval saved!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showBrakeIntervalDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Brake Inspection Interval");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_dual_spinner, null);
        builder.setView(dialogView);

        Spinner milesSpinner = dialogView.findViewById(R.id.spinner_miles);
        Spinner monthsSpinner = dialogView.findViewById(R.id.spinner_months);

        List<String> milesOptions = new ArrayList<>();
        Integer[] milesValuesArray = {0, 3000, 5000, 7500, 10000, 12500, 15000, 50000};
        List<Integer> milesValues = new ArrayList<>(List.of(milesValuesArray));

        milesOptions.add("Not Set");
        for (int i = 1; i < milesValues.size(); i++) {
            milesOptions.add(String.format(Locale.US, "%,d miles", milesValues.get(i)));
        }
        ArrayAdapter<String> milesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, milesOptions);
        milesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        milesSpinner.setAdapter(milesAdapter);

        List<String> monthsOptions = new ArrayList<>();
        Integer[] monthsValuesArray = {0, 3, 6, 12, 18, 24, 48};
        List<Integer> monthsValues = new ArrayList<>(List.of(monthsValuesArray));

        monthsOptions.add("Not Set");
        for (int i = 1; i < monthsValues.size(); i++) {
            monthsOptions.add(monthsValues.get(i) + " months");
        }
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsOptions);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(monthsAdapter);

        milesSpinner.setSelection(milesValues.indexOf(selectedCar.getBrakeIntervalMiles()));
        monthsSpinner.setSelection(monthsValues.indexOf(selectedCar.getBrakeIntervalMonths()));

        builder.setPositiveButton("Save", (dialog, which) -> {
            int milesPosition = milesSpinner.getSelectedItemPosition();
            int monthsPosition = monthsSpinner.getSelectedItemPosition();

            int selectedMiles = milesValues.get(milesPosition);
            int selectedMonths = monthsValues.get(monthsPosition);

            selectedCar.setBrakeIntervalMiles(selectedMiles);
            selectedCar.setBrakeIntervalMonths(selectedMonths);

            performBrakeCheckReset();
            Toast.makeText(this, "Brake interval saved!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showWipersIntervalDialog() {
        final String[] options = { "Every 6 months", "Every 8 months", "Every 12 months" };
        final int[] monthsValues = {6, 8, 12};

        int currentSelection = -1;
        for (int i = 0; i < monthsValues.length; i++) {
            if (selectedCar.getWiperIntervalMonths() == monthsValues[i]) {
                currentSelection = i;
                break;
            }
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select Wiper Replacement Interval");
        builder.setSingleChoiceItems(options, currentSelection, null);

        builder.setPositiveButton("Save", (dialog, which) -> {
            int selectedPosition = ((androidx.appcompat.app.AlertDialog) dialog).getListView().getCheckedItemPosition();

            if (selectedPosition != -1) {
                int selectedMonths = monthsValues[selectedPosition];
                selectedCar.setWiperIntervalMonths(selectedMonths);
                performWipersChangeReset();
                Toast.makeText(this, "Wiper interval saved!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    public static class ProgressResult {
        public int progressPercentage;
        public String progressText;
    }

    private ProgressResult calculateMaintenanceProgress(
            int currentMileage, int nextServiceMileage, int lastServiceMileage, int mileageInterval,
            LocalDate nextServiceDate, LocalDate lastServiceDate, int monthsInterval) {

        ProgressResult result = new ProgressResult();
        result.progressPercentage = 0;
        result.progressText = "Not Set";

        if (mileageInterval <= 0 && monthsInterval <= 0) {
            return result;
        }

        int mileageProgress = 0;
        int milesOverdue = 0;
        if (mileageInterval > 0) {
            int milesDriven = currentMileage - lastServiceMileage;
            if (milesDriven >= 0) {
                mileageProgress = (int) (((double) milesDriven / mileageInterval) * 100);
            }
            milesOverdue = currentMileage - nextServiceMileage;
        }

        int timeProgress = 0;
        long daysRemaining = 0;
        if (monthsInterval > 0 && nextServiceDate != null) {
            LocalDate startDate = lastServiceDate;
            if (startDate == null) {
                startDate = LocalDate.now();
            }

            long daysPassed = ChronoUnit.DAYS.between(startDate, LocalDate.now());
            long totalDaysInInterval = ChronoUnit.DAYS.between(startDate, nextServiceDate);

            if (totalDaysInInterval > 0) {
                timeProgress = (int) (((double) daysPassed / totalDaysInInterval) * 100);
            }
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), nextServiceDate);
        }

        boolean useMileage = (mileageInterval > 0) && (mileageProgress >= timeProgress);

        if (useMileage) {
            result.progressPercentage = mileageProgress;
            if (milesOverdue > 0) {
                result.progressText = "Overdue by " + String.format(Locale.US, "%,d", milesOverdue) + " miles";
            } else {
                result.progressText = "Due at " + String.format(Locale.US, "%,d", nextServiceMileage) + " miles";
            }
        } else if (monthsInterval > 0) {
            result.progressPercentage = timeProgress;
            if (daysRemaining < 0) {
                result.progressText = "Overdue by " + Math.abs(daysRemaining) + " days";
            } else {
                long monthsLeft = ChronoUnit.MONTHS.between(LocalDate.now(), nextServiceDate);
                if (monthsLeft < 1 && daysRemaining < 31) {
                    result.progressText = "Due in " + daysRemaining + (daysRemaining == 1 ? " day" : " days");
                } else {
                    result.progressText = "Due in " + monthsLeft + (monthsLeft == 1 ? " month" : " months");
                }
            }
        }

        if (result.progressPercentage >= 100) {
            result.progressPercentage = 100;
        }

        return result;
    }

    private void updateAllProgressBars(Car car) {
        if (car == null) return;

        int tealColor = getResources().getColor(R.color.teal, getTheme());
        int redColor = getResources().getColor(R.color.red, getTheme());
        ColorStateList tealColorStateList = ColorStateList.valueOf(tealColor);
        ColorStateList redColorStateList = ColorStateList.valueOf(redColor);
        final int DANGER_THRESHOLD = 90;

        // Oil Change
        ProgressBar oilProgressBar = findViewById(R.id.progress_oil);
        TextView oilStatusText = findViewById(R.id.text_oil_status);
        if (car.getOilIntervalMiles() > 0 || car.getOilIntervalMonths() > 0) {
            ProgressResult oilResult = calculateMaintenanceProgress(
                    car.getMiles(), car.getNextOil(), car.getLastOilChangeMileage(),
                    car.getOilIntervalMiles(), car.getNextOilChangeDate(), car.getLastOilChangeDate(),
                    car.getOilIntervalMonths()
            );
            oilProgressBar.setProgress(oilResult.progressPercentage);
            oilStatusText.setText(oilResult.progressText);
            oilProgressBar.setProgressTintList(oilResult.progressPercentage >= DANGER_THRESHOLD ? redColorStateList : tealColorStateList);
        } else {
            oilProgressBar.setProgress(0);
            oilStatusText.setText("Not Set");
            oilProgressBar.setProgressTintList(tealColorStateList);
        }

        // Tire Rotation
        ProgressBar tireProgressBar = findViewById(R.id.progress_tire);
        TextView tireStatusText = findViewById(R.id.text_tire_status);
        if (car.getTireIntervalMiles() > 0 || car.getTireIntervalMonths() > 0) {
            ProgressResult tireResult = calculateMaintenanceProgress(
                    car.getMiles(), car.getNextTireRotation(), car.getLastTireRotationMileage(),
                    car.getTireIntervalMiles(), car.getNextTireRotationDate(), car.getLastTireRotationDate(),
                    car.getTireIntervalMonths()
            );
            tireProgressBar.setProgress(tireResult.progressPercentage);
            tireStatusText.setText(tireResult.progressText);
            tireProgressBar.setProgressTintList(tireResult.progressPercentage >= DANGER_THRESHOLD ? redColorStateList : tealColorStateList);
        } else {
            tireProgressBar.setProgress(0);
            tireStatusText.setText("Not Set");
            tireProgressBar.setProgressTintList(tealColorStateList);
        }

        // Brake Inspection
        ProgressBar brakeProgressBar = findViewById(R.id.progress_brake);
        TextView brakeStatusText = findViewById(R.id.text_brake_status);
        if (car.getBrakeIntervalMiles() > 0 || car.getBrakeIntervalMonths() > 0) {
            ProgressResult brakeResult = calculateMaintenanceProgress(
                    car.getMiles(), car.getNextBrakeIns(), car.getLastBrakeCheckMileage(),
                    car.getBrakeIntervalMiles(), car.getNextBrakeCheckDate(), car.getLastBrakeCheckDate(),
                    car.getBrakeIntervalMonths()
            );
            brakeProgressBar.setProgress(brakeResult.progressPercentage);
            brakeStatusText.setText(brakeResult.progressText);
            brakeProgressBar.setProgressTintList(brakeResult.progressPercentage >= DANGER_THRESHOLD ? redColorStateList : tealColorStateList);
        } else {
            brakeProgressBar.setProgress(0);
            brakeStatusText.setText("Not Set");
            brakeProgressBar.setProgressTintList(tealColorStateList);
        }

        // Wipers
        ProgressBar wipersProgressBar = findViewById(R.id.progress_wipers);
        TextView wiperStatusText = findViewById(R.id.text_wiper_status);
        if (car.getWiperIntervalMonths() > 0) {
            ProgressResult wipersResult = calculateMaintenanceProgress(
                    0, 0, 0, 0,
                    car.getNextWipersChangeDate(), car.getLastWipersChangeDate(), car.getWiperIntervalMonths()
            );
            wipersProgressBar.setProgress(wipersResult.progressPercentage);
            wiperStatusText.setText(wipersResult.progressText);
            wipersProgressBar.setProgressTintList(wipersResult.progressPercentage >= DANGER_THRESHOLD ? redColorStateList : tealColorStateList);
        } else {
            wipersProgressBar.setProgress(0);
            wiperStatusText.setText("Not Set");
            wipersProgressBar.setProgressTintList(tealColorStateList);
        }

        // Registration
        ProgressBar registrationProgressBar = findViewById(R.id.progress_registration);
        TextView registrationProgressText = findViewById(R.id.text_registration_status);
        if (car.getRegistrationIntervalMonths() > 0 && car.getNextRegistrationDate() != null) {
            LocalDate nextDueDate = car.getNextRegistrationDate();
            LocalDate today = LocalDate.now();

            if (nextDueDate.isBefore(today) || nextDueDate.isEqual(today)) {
                LocalDate lastDueDate = car.getNextRegistrationDate();
                LocalDate newNextDueDate = lastDueDate.plusMonths(car.getRegistrationIntervalMonths());

                car.setNextRegistrationDate(newNextDueDate);
                car.setRegistrationLastSetDate(lastDueDate);

                autoLogViewModel.update(car);
            }

            ProgressResult registrationResult = calculateMaintenanceProgress(
                    0, 0, 0, 0,
                    car.getNextRegistrationDate(), car.getRegistrationLastSetDate(), car.getRegistrationIntervalMonths()
            );

            registrationProgressBar.setProgress(registrationResult.progressPercentage);
            registrationProgressText.setText(registrationResult.progressText);
            registrationProgressBar.setProgressTintList(registrationResult.progressPercentage >= DANGER_THRESHOLD ? redColorStateList : tealColorStateList);

        } else {
            registrationProgressBar.setProgress(0);
            registrationProgressText.setText("Not Set");
            registrationProgressBar.setProgressTintList(tealColorStateList);
        }
    }


    private boolean saveRegistrationInfo(LocalDate nextDate, int intervalMonths) {
        if (intervalMonths <= 0) {
            Toast.makeText(this, "Please set a registration interval.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (nextDate == null) {
            Toast.makeText(this, "Please select the registration due date.", Toast.LENGTH_LONG).show();
            return false;
        }

        selectedCar.setRegistrationIntervalMonths(intervalMonths);
        selectedCar.setNextRegistrationDate(nextDate);
        selectedCar.setRegistrationLastSetDate(LocalDate.now());

        autoLogViewModel.update(selectedCar);
        Toast.makeText(this, "Registration info saved!", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void performOilChangeReset(boolean showToast) {
        if (selectedCar.getOilIntervalMiles() <= 0 && selectedCar.getOilIntervalMonths() <= 0) {
            Toast.makeText(this, "Please set an oil change interval first.", Toast.LENGTH_LONG).show();
            showOilIntervalDialog();
            return;
        }

        int currentMiles = selectedCar.getMiles();
        LocalDate today = LocalDate.now();

        selectedCar.setLastOilChangeMileage(currentMiles);
        selectedCar.setLastOilChangeDate(today);

        if (selectedCar.getOilIntervalMiles() > 0) {
            selectedCar.setNextOil(currentMiles + selectedCar.getOilIntervalMiles());
        }
        if (selectedCar.getOilIntervalMonths() > 0) {
            selectedCar.setNextOilChangeDate(today.plusMonths(selectedCar.getOilIntervalMonths()));
        }

        autoLogViewModel.update(selectedCar);
        updateAllProgressBars(selectedCar);

        if (showToast) {
            Toast.makeText(this, "Oil change service recorded! Progress has been reset.", Toast.LENGTH_LONG).show();
        }
    }

    private void performTireRotationReset() {
        if (selectedCar.getTireIntervalMiles() <= 0 && selectedCar.getTireIntervalMonths() <= 0) {
            Toast.makeText(this, "Please set a tire rotation interval first.", Toast.LENGTH_LONG).show();
            showTireIntervalDialog();
            return;
        }

        int currentMiles = selectedCar.getMiles();
        LocalDate today = LocalDate.now();

        selectedCar.setLastTireRotationMileage(currentMiles);
        selectedCar.setLastTireRotationDate(today);

        if (selectedCar.getTireIntervalMiles() > 0) {
            selectedCar.setNextTireRotation(currentMiles + selectedCar.getTireIntervalMiles());
        }
        if (selectedCar.getTireIntervalMonths() > 0) {
            selectedCar.setNextTireRotationDate(today.plusMonths(selectedCar.getTireIntervalMonths()));
        }

        autoLogViewModel.update(selectedCar);
        updateAllProgressBars(selectedCar);
        Toast.makeText(this, "Tire rotation service recorded!", Toast.LENGTH_SHORT).show();
    }

    private void performBrakeCheckReset() {
        if (selectedCar.getBrakeIntervalMiles() <= 0 && selectedCar.getBrakeIntervalMonths() <= 0) {
            Toast.makeText(this, "Please set a brake inspection interval first.", Toast.LENGTH_LONG).show();
            showBrakeIntervalDialog();
            return;
        }

        int currentMiles = selectedCar.getMiles();
        LocalDate today = LocalDate.now();

        selectedCar.setLastBrakeCheckMileage(currentMiles);
        selectedCar.setLastBrakeCheckDate(today);

        if (selectedCar.getBrakeIntervalMiles() > 0) {
            selectedCar.setNextBrakeIns(currentMiles + selectedCar.getBrakeIntervalMiles());
        }
        if (selectedCar.getBrakeIntervalMonths() > 0) {
            selectedCar.setNextBrakeCheckDate(today.plusMonths(selectedCar.getBrakeIntervalMonths()));
        }

        autoLogViewModel.update(selectedCar);
        updateAllProgressBars(selectedCar);
        Toast.makeText(this, "Brake inspection service recorded!", Toast.LENGTH_SHORT).show();
    }

    private void performWipersChangeReset() {
        if (selectedCar.getWiperIntervalMonths() <= 0) {
            Toast.makeText(this, "Please set a wiper replacement interval first.", Toast.LENGTH_LONG).show();
            showWipersIntervalDialog();
            return;
        }

        LocalDate today = LocalDate.now();
        selectedCar.setLastWipersChangeDate(today);
        selectedCar.setNextWipersChangeDate(today.plusMonths(selectedCar.getWiperIntervalMonths()));

        autoLogViewModel.update(selectedCar);
        updateAllProgressBars(selectedCar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
