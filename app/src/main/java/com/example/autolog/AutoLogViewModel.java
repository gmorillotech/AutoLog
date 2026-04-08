package com.example.autolog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.autolog.data.AutoLogDao;
import com.example.autolog.data.AutoLogDatabase;
import com.example.autolog.data.Car;
import com.example.autolog.data.ServiceRecord;
import com.example.autolog.data.ServiceRecordDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Manages UI data and survives configuration changes
public class AutoLogViewModel extends AndroidViewModel {
    private final AutoLogDao autoLogDao;
    private final ServiceRecordDao serviceRecordDao;
    private final LiveData<List<Car>> allCars;
    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public AutoLogViewModel(@NonNull Application application) {
        super(application);
        AutoLogDatabase db = AutoLogDatabase.getInstance(application);
        autoLogDao = db.autoLogDao();
        serviceRecordDao = db.serviceRecordDao();
        allCars = autoLogDao.getAllCars();
    }

    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    public LiveData<Car> getCarById(long carId) {
        return autoLogDao.getCarById(carId);
    }

    public LiveData<List<ServiceRecord>> getRecordsForCar(long carId) {
        return serviceRecordDao.getRecordsForCar(carId);
    }

    public void insertServiceRecord(ServiceRecord record) {
        databaseWriteExecutor.execute(() -> {
            serviceRecordDao.insert(record);
        });
    }

    public void updateServiceRecord(ServiceRecord record) {
        databaseWriteExecutor.execute(() -> {
            serviceRecordDao.update(record);
        });
    }

    public void deleteServiceRecord(ServiceRecord record) {
        databaseWriteExecutor.execute(() -> {
            serviceRecordDao.delete(record);
        });
    }

    public void insert(Car car) {
        databaseWriteExecutor.execute(() -> {
            autoLogDao.insertCar(car);
        });
    }

    public void delete(Car car) {
        databaseWriteExecutor.execute(() -> {
            autoLogDao.deleteCar(car);
        });
    }

    public void update(Car car) {
        databaseWriteExecutor.execute(() -> {
            autoLogDao.updateCar(car);
        });
    }
}
