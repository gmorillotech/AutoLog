package com.example.autolog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autolog.data.Car;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Manages and displays the list of cars in RecyclerView
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {

    private List<Car> carList = new ArrayList<>();
    private AdapterListener listener;

    private final ActivityResultLauncher<Intent> dashboardLauncher;

    public CarAdapter(Context context, ActivityResultLauncher<Intent> launcher) {
        this.dashboardLauncher = launcher;
    }

    public void setAdapterListener(AdapterListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Car car = carList.get(position);

        String make = car.getStrMake();
        String model = car.getStrModel();
        String combinedMakeAndModel = make + " " + model;
        holder.make_and_model.setText(combinedMakeAndModel);

        holder.year.setText(String.valueOf(car.getYear()));
        holder.vin.setText(car.getVin());

        String formattedMiles = String.format(Locale.US, "%,d Miles", car.getMiles());
        holder.miles.setText(formattedMiles);

        String imagePath = car.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagePath)
                    .placeholder(R.drawable.car_placeholder_foreground)
                    .error(R.drawable.car_placeholder_foreground)
                    .into(holder.carImage);
        } else {
            holder.carImage.setImageResource(R.drawable.car_placeholder_foreground);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), DashboardActivity.class);
            intent.putExtra("SELECTED_CAR", car);
            dashboardLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void setCars(List<Car> cars) {
        this.carList = cars;
        notifyDataSetChanged();
    }

    // Holds the views for a single item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView year, vin, miles, make_and_model;
        private final ImageView update, delete, carImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            make_and_model = itemView.findViewById(R.id.make_and_model);
            year = itemView.findViewById(R.id.year);
            vin = itemView.findViewById(R.id.vin);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
            carImage = itemView.findViewById(R.id.car_image);
            miles = itemView.findViewById(R.id.miles);

            update.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION){
                    listener.onUpdate(carList.get(position));
                }
            });

            delete.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    Car carToDelete = carList.get(position);

                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete the " + carToDelete.getYear() + " " + carToDelete.getStrMake() + " " + carToDelete.getStrModel() + "?")
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Delete", (dialog, which) -> listener.onDelete(carToDelete))
                            .show();
                }
            });
        }
    }
}
