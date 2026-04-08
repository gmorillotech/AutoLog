package com.example.autolog.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "service_records",
        foreignKeys = @ForeignKey(entity = Car.class,
                parentColumns = "carId",
                childColumns = "carId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "carId")})
public class ServiceRecord implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    public long carId;
    public String serviceType;
    public LocalDate datePerformed;
    public int mileage;
    public String notes;

    // Getter and setters
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public long getCarId() { return carId; }
    public void setCarId(long carId) { this.carId = carId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public LocalDate getDatePerformed() { return datePerformed; }
    public void setDatePerformed(LocalDate datePerformed) { this.datePerformed = datePerformed; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
