package com.example.autolog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.autolog.data.Car;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateCarFragment extends DialogFragment {

    private AutoLogViewModel carViewModel;
    private Car carToUpdate;
    private ImageView updateDialogImageView;
    private String newImagePath = null;
    private Uri tempPhotoUri;
    private File photoFile;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult( new ActivityResultContracts.RequestPermission(), isGranted-> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher
            = registerForActivityResult(new ActivityResultContracts.GetContent(),
            this::handleGalleryResult);

    private final ActivityResultLauncher<Uri> takePictureLauncher
            = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            this::handleCameraResult);


    public static UpdateCarFragment newInstance(Car car) {
        UpdateCarFragment fragment = new UpdateCarFragment();
        Bundle args = new Bundle();
        args.putParcelable("CAR_TO_UPDATE", car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carToUpdate = getArguments().getParcelable("CAR_TO_UPDATE");
            if (carToUpdate != null) {
                newImagePath = carToUpdate.getImagePath();
            }
        }
        carViewModel = new ViewModelProvider(this).get(AutoLogViewModel.class);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.App_Custom_Dialog);
        final View dialogView = getLayoutInflater().inflate(R.layout.activity_update_car, null);
        builder.setView(dialogView);

        final EditText editUpdateMake = dialogView.findViewById(R.id.edit_update_make);
        final EditText editUpdateModel = dialogView.findViewById(R.id.edit_update_model);
        final EditText editUpdateYear = dialogView.findViewById(R.id.edit_update_year);
        final EditText editUpdateMileage = dialogView.findViewById(R.id.edit_update_mileage);
        final EditText editUpdateVin = dialogView.findViewById(R.id.edit_update_vin);
        final FrameLayout imageContainer = dialogView.findViewById(R.id.image_container);
        updateDialogImageView = dialogView.findViewById(R.id.update_car_image);

        Glide.with(getContext()).load(carToUpdate.getImagePath()).placeholder(R.drawable.car_placeholder_foreground).error(R.drawable.car_placeholder_foreground).circleCrop().into(updateDialogImageView);
        editUpdateMake.setText(carToUpdate.getStrMake());
        editUpdateModel.setText(carToUpdate.getStrModel());
        editUpdateYear.setText(String.valueOf(carToUpdate.getYear()));
        editUpdateMileage.setText(String.valueOf(carToUpdate.getMiles()));
        editUpdateVin.setText(carToUpdate.getVin());

        imageContainer.setOnClickListener(v -> showImagePickerDialog());
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setTextColor(getResources().getColor(R.color.teal, requireActivity().getTheme()));
            negativeButton.setTextColor(getResources().getColor(R.color.teal, requireActivity().getTheme()));
            positiveButton.setTypeface(positiveButton.getTypeface(), android.graphics.Typeface.BOLD);
            negativeButton.setTypeface(positiveButton.getTypeface(), android.graphics.Typeface.BOLD);

            positiveButton.setOnClickListener(v -> {
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
                try {
                    newYear = Integer.parseInt(newYearStr);
                } catch (NumberFormatException e) {
                    editUpdateYear.setError("Please enter a valid number");
                    return;
                }

                int newMileage = 0;
                if (!newMileageStr.isEmpty()) {
                    try {
                        newMileage = Integer.parseInt(newMileageStr);
                    } catch (NumberFormatException e) {
                        editUpdateMileage.setError("Please enter a valid number");
                        return;
                    }
                }

                carToUpdate.setStrMake(newMake);
                carToUpdate.setStrModel(newModel);
                carToUpdate.setYear(newYear);
                carToUpdate.setVin(newVin);
                carToUpdate.setMiles(newMileage);
                carToUpdate.setImagePath(newImagePath);

                carViewModel.update(carToUpdate);
                Toast.makeText(getContext(), "Update for " + carToUpdate.getStrMake(), Toast.LENGTH_SHORT).show();

                Bundle result = new Bundle();
                result.putParcelable("UPDATED_CAR", carToUpdate);
                getParentFragmentManager().setFragmentResult("REQUEST_CAR_UPDATE", result);

                dialog.dismiss();
            });
        });
        return dialog;
    }

    private void showImagePickerDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Photo");

        builder.setItems(options, (dialog, item) -> {
            String selectedOption = options[item].toString();
            if ("Take Photo".equals(selectedOption)) {
                tryToLaunchCamera();
            } else if ("Choose from Gallery".equals(selectedOption)) {
                pickImageLauncher.launch("image/*");
            } else if ("Cancel".equals(selectedOption)) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void tryToLaunchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile != null) {
            tempPhotoUri = FileProvider.getUriForFile(requireContext(),
                    "com.example.autolog.fileprovider",
                    photoFile);
            takePictureLauncher.launch(tempPhotoUri);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void handleCameraResult(boolean success) {
        if (success && tempPhotoUri != null) {
            newImagePath = tempPhotoUri.toString();
            Glide.with(getContext()).load(newImagePath).circleCrop().into(updateDialogImageView);
        } else {
            Toast.makeText(getContext(), "Camera cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGalleryResult(Uri uri) {
        if (uri != null) {
            requireActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            newImagePath = uri.toString();
            Glide.with(getContext()).load(newImagePath).circleCrop().into(updateDialogImageView);
        }
    }
}
