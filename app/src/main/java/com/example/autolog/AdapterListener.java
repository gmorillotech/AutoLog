package com.example.autolog;

import com.example.autolog.data.Car;

// Used to pass data between the MainActivity and CarAdapter
public interface AdapterListener {
    void onUpdate(Car car);
    void onDelete(Car car);

}
