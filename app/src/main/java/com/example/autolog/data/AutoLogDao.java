package com.example.autolog.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AutoLogDao {

    // Car entity operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCar(Car car);
    @Update
    void updateCar(Car car);
    @Delete
    void deleteCar(Car car);
    @Query("SELECT * FROM car ORDER BY carId ASC")
    LiveData<List<Car>> getAllCars();
    @Query("SELECT * FROM car WHERE carId =:carId")
    LiveData<Car> getCarById(long carId);

    // Modifications entity operations (Coming soon)
    @Insert
    long insertModification(Modification modification); // Coming soon
    @Update
    void updateModification(Modification modification); // Coming soon
    @Query("SELECT * FROM modification WHERE carId = :carId ORDER BY date DESC") // Coming soon
    LiveData<List<Modification>> getModsForCar(long carId);
}
