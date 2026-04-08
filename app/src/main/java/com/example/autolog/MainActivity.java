package com.example.autolog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.autolog.data.Car;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterListener {

    private RecyclerView myRecycler;

    private AutoLogViewModel autoLogViewModel;
    private CarAdapter carAdapter;
    private Car carToUpdatePhoto;

    private String newCarImagePath = null;
    private Uri tempPhotoUri;
    private File photoFile;
    private ImageView dialogImagePreview;

    private ImageView updateDialogImageView;

    private TextView noCarsText;



    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult( new ActivityResultContracts.RequestPermission(), isGranted-> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required,", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher
            = registerForActivityResult(new ActivityResultContracts.GetContent(),
            this::handleGalleryResult);

    private final ActivityResultLauncher<Uri> takePictureLauncher
            = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            this::handleCameraResult);

    private ActivityResultLauncher<Intent> dashboardLauncher;

    private void launchCamera() {
        try{
            photoFile = createImageFile();
        }catch (IOException ex){
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            return;
        }

        if(photoFile != null) {
            tempPhotoUri = FileProvider.getUriForFile(this,
                    "com.example.autolog.fileprovider",
                    photoFile);
            takePictureLauncher.launch(tempPhotoUri);
        }
    }

    private void tryToLaunchCamera() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            launchCamera();
        }else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null ) {
            getSupportActionBar().setTitle("");
        }
        noCarsText = findViewById(R.id.text_no_cars);

        dashboardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        autoLogViewModel.getAllCars().observe(this, cars -> {
                            carAdapter.setCars(cars);
                        });
                    }
                }
        );
        myRecycler = findViewById(R.id.carsRecycle);
        autoLogViewModel = new ViewModelProvider(this).get(AutoLogViewModel.class);
        setupRecyclerView();

        autoLogViewModel.getAllCars().observe(this, cars -> {
            carAdapter.setCars(cars);

            boolean hasCars = cars != null && !cars.isEmpty();
            myRecycler.setVisibility(hasCars ? View.VISIBLE : View.GONE);
            noCarsText.setVisibility(hasCars ? View.GONE : View.VISIBLE);
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_car) {
            showAddCarDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        carAdapter = new CarAdapter(this, dashboardLauncher);
        myRecycler.setAdapter(carAdapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));

        carAdapter.setAdapterListener(this);
    }

    private void showAddCarDialog() {
        newCarImagePath = null;
        View dialogView = getLayoutInflater().inflate(R.layout.activity_add_car, null);


        final Button addPhotoButton = dialogView.findViewById(R.id.button_add_photo);
        dialogImagePreview = dialogView.findViewById(R.id.image_preview);

        final EditText addMake = dialogView.findViewById(R.id.add_make);
        final EditText addModel = dialogView.findViewById(R.id.add_model);
        final EditText addYear = dialogView.findViewById(R.id.add_year);
        final EditText addVin = dialogView.findViewById(R.id.add_vin);
        final EditText addMiles = dialogView.findViewById(R.id.add_miles);

        addPhotoButton.setOnClickListener(v ->{
            showImagePickerDialog();

        });

        dialogImagePreview.setOnClickListener(v ->{
            showImagePickerDialog();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.App_Custom_Dialog);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setTextColor(getResources().getColor(R.color.teal, getTheme()));
            negativeButton.setTextColor(getResources().getColor(R.color.teal, getTheme()));

            positiveButton.setTypeface(positiveButton.getTypeface(), Typeface.BOLD);
            negativeButton.setTypeface(negativeButton.getTypeface(), Typeface.BOLD);

            positiveButton.setOnClickListener(View ->{
                String make = addMake.getText().toString().trim();
                String model = addModel.getText().toString().trim();
                String yearStr = addYear.getText().toString().trim();
                String vin = addVin.getText().toString().trim();
                String milesStr = addMiles.getText().toString().trim();


                if(make.isEmpty()){
                    addMake.setError("Make cannot be empty");
                    return;
                }else{
                    addMake.setError(null);
                }

                if(model.isEmpty()){
                    addModel.setError("Model cannot be empty");
                    return;
                }else{
                    addModel.setError(null);
                }

                if(yearStr.isEmpty()){
                    addYear.setError("Year cannot be empty");
                    return;
                }else{
                    addYear.setError(null);
                }

                if(milesStr.isEmpty()) {
                    addMiles.setError("Mileage cannot be empty");
                    return;
                } else {
                    addMiles.setError(null);
                }

                int miles;
                int year;
                try{
                    year = Integer.parseInt(yearStr);
                    miles = Integer.parseInt(milesStr);
                } catch (NumberFormatException e){
                    Toast.makeText(this, "Please enter a valid year", Toast.LENGTH_SHORT).show();
                    return;
                }

                Car newCar = new Car(make, model, year, vin, newCarImagePath);
                newCar.setMiles(miles);

                if(newCarImagePath != null){
                    newCar.setImagePath(newCarImagePath);
                }
                autoLogViewModel.insert(newCar);
                Toast.makeText(this, make + " added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
        dialog.show();

    }
    private void showUpdateDialog(Car car) {
        carToUpdatePhoto = car;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.App_Custom_Dialog);
        final View dialogView = getLayoutInflater().inflate(R.layout.activity_update_car, null);
        builder.setView(dialogView);

        final EditText editUpdateMake = dialogView.findViewById(R.id.edit_update_make);
        final EditText editUpdateModel = dialogView.findViewById(R.id.edit_update_model);
        final EditText editUpdateYear = dialogView.findViewById(R.id.edit_update_year);
        final EditText editUpdateMileage = dialogView.findViewById(R.id.edit_update_mileage);
        final EditText editUpdateVin = dialogView.findViewById(R.id.edit_update_vin);
        final FrameLayout imageContainer = dialogView.findViewById(R.id.image_container);


        updateDialogImageView = dialogView.findViewById(R.id.update_car_image);
        Glide.with(this)
                .load(car.getImagePath())
                .placeholder(R.drawable.car_placeholder_foreground)
                .error(R.drawable.car_placeholder_foreground)
                .circleCrop()
                .into(updateDialogImageView);

        editUpdateMake.setText(car.getStrMake());
        editUpdateModel.setText(car.getStrModel());
        editUpdateYear.setText(String.valueOf(car.getYear()));
        editUpdateMileage.setText(String.valueOf(car.getMiles()));
        editUpdateVin.setText(car.getVin());

        imageContainer.setOnClickListener(v -> {
            showImagePickerDialog();
        });

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(dialogInterface -> {
            carToUpdatePhoto = null;
            updateDialogImageView = null;
        });


        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setTextColor(getResources().getColor(R.color.teal, getTheme()));
            negativeButton.setTextColor(getResources().getColor(R.color.teal, getTheme()));
            positiveButton.setTypeface(positiveButton.getTypeface(), Typeface.BOLD);
            negativeButton.setTypeface(positiveButton.getTypeface(), Typeface.BOLD);


            positiveButton.setOnClickListener(View -> {
                String newMake = editUpdateMake.getText().toString().trim();
                String newModel = editUpdateModel.getText().toString().trim();
                String newYearStr = editUpdateYear.getText().toString().trim();
                String newMileageStr = editUpdateMileage.getText().toString().trim();
                String newVin = editUpdateVin.getText().toString().trim();

                if(newMake.isEmpty()){
                    editUpdateMake.setError("Make cannot be empty");
                    return;
                }else{
                    editUpdateMake.setError(null);
                }

                if(newModel.isEmpty()){
                    editUpdateModel.setError("Model cannot be empty");
                    return;
                }else{
                    editUpdateModel.setError(null);
                }

                if(newYearStr.isEmpty()){
                    editUpdateYear.setError("Year cannot be empty");
                    return;
                }else{
                    editUpdateYear.setError(null);
                }

                int newYear;
                try{
                    newYear = Integer.parseInt(newYearStr);
                }catch(NumberFormatException e){
                    editUpdateYear.setError("Please enter a valid number");
                    return;
                }

                int newMileage = 0;
                if (!newMileageStr.isEmpty()) {
                    try {
                        newMileage = Integer.parseInt(newMileageStr);
                    } catch (NumberFormatException e) {
                        editUpdateMileage.setError("Please enter a valid number");
                        return; // Stop if mileage is invalid
                    }
                }
                car.setStrMake(newMake);
                car.setStrModel(newModel);
                car.setYear(newYear);
                car.setMiles(newMileage);
                car.setVin(newVin);

                autoLogViewModel.update(car);
                Toast.makeText(this, "Update for " + car.getStrMake(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private void showImagePickerDialog(){
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");

        builder.setItems(options, (dialog, item)->{
            String selectedOption = options[item].toString();

            if ("Take Photo".equals(selectedOption)) {
                File photoFile = null;
                tryToLaunchCamera();
            }else if( "Choose from Gallery".equals(selectedOption)){
                    pickImageLauncher.launch("image/*");

            } else if("Cancel".equals(selectedOption)){
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    private void handleCameraResult(boolean success) {
        if (success) {

            if (tempPhotoUri != null) {
                String imagePath = tempPhotoUri.toString();

                if (carToUpdatePhoto != null && updateDialogImageView != null) {
                    carToUpdatePhoto.setImagePath(imagePath);
                    Glide.with(this).load(imagePath).circleCrop().into(updateDialogImageView);
                } else {
                    newCarImagePath = imagePath;
                    updateDialogPreview(tempPhotoUri);
                }
            } else {
                Toast.makeText(this, "Error: Photo data could not be read.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGalleryResult(Uri uri){
        if(uri != null){
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String imagePath = uri.toString();

            if(carToUpdatePhoto != null){
                carToUpdatePhoto.setImagePath(imagePath);
                Glide.with(this).load(imagePath).circleCrop().into(updateDialogImageView);
            } else {
                newCarImagePath = imagePath;
                updateDialogPreview(uri);
            }
        }
    }

    private void updateDialogPreview(Uri imageUri){
        if(dialogImagePreview!= null){
            View parentView = (View) dialogImagePreview.getParent();
            Button addPhotoButton = parentView.findViewById(R.id.button_add_photo);
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(dialogImagePreview);

            addPhotoButton.setVisibility(View.GONE);
            dialogImagePreview.setVisibility(View.VISIBLE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }


    @Override
    public void onUpdate(Car car) {

        Log.d("MainActivity", "Update clicked for car: " + car.getStrMake());
        showUpdateDialog(car);
    }

    @Override
    public void onDelete(Car car) {
        autoLogViewModel.delete(car);
        Toast.makeText(this, car.getStrMake() + " deleted", Toast.LENGTH_SHORT).show();
    }

}