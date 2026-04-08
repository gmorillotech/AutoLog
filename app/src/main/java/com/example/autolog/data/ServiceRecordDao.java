package com.example.autolog.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServiceRecordDao {

    @Insert
    long insert(ServiceRecord record);
    @Update
    void update(ServiceRecord record);
    @Delete
    void delete(ServiceRecord record);
    @Query("SELECT * FROM service_records WHERE carId = :carId ORDER BY datePerformed DESC")
    LiveData<List<ServiceRecord>> getRecordsForCar(long carId);
    @Query("SELECT * FROM service_records WHERE carId = :carId AND serviceType = :serviceType ORDER BY datePerformed DESC")
    LiveData<List<ServiceRecord>> getRecordsForCarByType(long carId, String serviceType);
}
