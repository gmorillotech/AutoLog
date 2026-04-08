package com.example.autolog.data;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "car")
public class Car implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long carId;
    @NonNull
    private String strMake;
    @NonNull
    private String strModel;
    private int year;
    @NonNull
    private String vin;

    @ColumnInfo(name = "image_path")
    private String imagePath;
    private int miles;

    private int oilIntervalMiles, oilIntervalMonths, tireIntervalMiles, tireIntervalMonths,
            brakeIntervalMiles, brakeIntervalMonths, wiperIntervalMonths, registrationIntervalMonths;
    private int lastOilChangeMileage, lastTireRotationMileage, lastBrakeCheckMileage, lastWipersChangeMileage;
    private LocalDate lastOilChangeDate, lastTireRotationDate, lastBrakeCheckDate, lastWipersChangeDate;
    private int nextOil, nextTireRotation, nextBrakeIns, nextWipers;
    private LocalDate nextOilChangeDate, nextTireRotationDate, nextBrakeCheckDate, nextWipersChangeDate,
            nextRegistrationDate, registrationLastSetDate;

    // Constructor
    public Car(@NonNull String strMake, @NonNull String strModel, int year, @NonNull String vin, String imagePath, int miles,
               int oilIntervalMiles, int oilIntervalMonths, int tireIntervalMiles, int tireIntervalMonths, int brakeIntervalMiles, int brakeIntervalMonths, int wiperIntervalMonths, int registrationIntervalMonths,
               int lastOilChangeMileage, LocalDate lastOilChangeDate, int lastTireRotationMileage, LocalDate lastTireRotationDate, int lastBrakeCheckMileage, LocalDate lastBrakeCheckDate, int lastWipersChangeMileage, LocalDate lastWipersChangeDate,
               int nextOil, LocalDate nextOilChangeDate, int nextTireRotation, LocalDate nextTireRotationDate, int nextBrakeIns, LocalDate nextBrakeCheckDate, int nextWipers, LocalDate nextWipersChangeDate, LocalDate nextRegistrationDate, LocalDate registrationLastSetDate) {
        this.strMake = strMake;
        this.strModel = strModel;
        this.year = year;
        this.vin = vin;
        this.imagePath = imagePath;
        this.miles = miles;
        this.oilIntervalMiles = oilIntervalMiles;
        this.oilIntervalMonths = oilIntervalMonths;
        this.tireIntervalMiles = tireIntervalMiles;
        this.tireIntervalMonths = tireIntervalMonths;
        this.brakeIntervalMiles = brakeIntervalMiles;
        this.brakeIntervalMonths = brakeIntervalMonths;
        this.wiperIntervalMonths = wiperIntervalMonths;
        this.registrationIntervalMonths = registrationIntervalMonths;
        this.lastOilChangeMileage = lastOilChangeMileage;
        this.lastOilChangeDate = lastOilChangeDate;
        this.lastTireRotationMileage = lastTireRotationMileage;
        this.lastTireRotationDate = lastTireRotationDate;
        this.lastBrakeCheckMileage = lastBrakeCheckMileage;
        this.lastBrakeCheckDate = lastBrakeCheckDate;
        this.lastWipersChangeMileage = lastWipersChangeMileage;
        this.lastWipersChangeDate = lastWipersChangeDate;
        this.nextOil = nextOil;
        this.nextOilChangeDate = nextOilChangeDate;
        this.nextTireRotation = nextTireRotation;
        this.nextTireRotationDate = nextTireRotationDate;
        this.nextBrakeIns = nextBrakeIns;
        this.nextBrakeCheckDate = nextBrakeCheckDate;
        this.nextWipers = nextWipers;
        this.nextWipersChangeDate = nextWipersChangeDate;
        this.nextRegistrationDate = nextRegistrationDate;
        this.registrationLastSetDate = registrationLastSetDate;
    }

    @Ignore
    public Car(@NonNull String strMake, @NonNull String strModel, int year, @NonNull String vin, String imagePath) {
        this(strMake, strModel, year, vin, imagePath, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, null, 0, null, 0, null, 0, null, null, null);
    }

    // Parcelable implementation
    protected Car(Parcel in) {
        carId = in.readLong();
        strMake = in.readString();
        strModel = in.readString();
        year = in.readInt();
        vin = in.readString();
        imagePath = in.readString();
        miles = in.readInt();
        oilIntervalMiles = in.readInt();
        oilIntervalMonths = in.readInt();
        tireIntervalMiles = in.readInt();
        tireIntervalMonths = in.readInt();
        brakeIntervalMiles = in.readInt();
        brakeIntervalMonths = in.readInt();
        wiperIntervalMonths = in.readInt();
        registrationIntervalMonths = in.readInt();
        lastOilChangeMileage = in.readInt();
        lastOilChangeDate = (LocalDate) in.readSerializable();
        lastTireRotationMileage = in.readInt();
        lastTireRotationDate = (LocalDate) in.readSerializable();
        lastBrakeCheckMileage = in.readInt();
        lastBrakeCheckDate = (LocalDate) in.readSerializable();
        lastWipersChangeMileage = in.readInt();
        lastWipersChangeDate = (LocalDate) in.readSerializable();
        nextOil = in.readInt();
        nextOilChangeDate = (LocalDate) in.readSerializable();
        nextTireRotation = in.readInt();
        nextTireRotationDate = (LocalDate) in.readSerializable();
        nextBrakeIns = in.readInt();
        nextBrakeCheckDate = (LocalDate) in.readSerializable();
        nextWipers = in.readInt();
        nextWipersChangeDate = (LocalDate) in.readSerializable();
        nextRegistrationDate = (LocalDate) in.readSerializable();
        registrationLastSetDate = (LocalDate) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(carId);
        dest.writeString(strMake);
        dest.writeString(strModel);
        dest.writeInt(year);
        dest.writeString(vin);
        dest.writeString(imagePath);
        dest.writeInt(miles);
        dest.writeInt(oilIntervalMiles);
        dest.writeInt(oilIntervalMonths);
        dest.writeInt(tireIntervalMiles);
        dest.writeInt(tireIntervalMonths);
        dest.writeInt(brakeIntervalMiles);
        dest.writeInt(brakeIntervalMonths);
        dest.writeInt(wiperIntervalMonths);
        dest.writeInt(registrationIntervalMonths);
        dest.writeInt(lastOilChangeMileage);
        dest.writeSerializable(lastOilChangeDate);
        dest.writeInt(lastTireRotationMileage);
        dest.writeSerializable(lastTireRotationDate);
        dest.writeInt(lastBrakeCheckMileage);
        dest.writeSerializable(lastBrakeCheckDate);
        dest.writeInt(lastWipersChangeMileage);
        dest.writeSerializable(lastWipersChangeDate);
        dest.writeInt(nextOil);
        dest.writeSerializable(nextOilChangeDate);
        dest.writeInt(nextTireRotation);
        dest.writeSerializable(nextTireRotationDate);
        dest.writeInt(nextBrakeIns);
        dest.writeSerializable(nextBrakeCheckDate);
        dest.writeInt(nextWipers);
        dest.writeSerializable(nextWipersChangeDate);
        dest.writeSerializable(nextRegistrationDate);
        dest.writeSerializable(registrationLastSetDate);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) { return new Car(in); }
        @Override
        public Car[] newArray(int size) { return new Car[size]; }
    };

    // Getters and setters
    public long getCarId() { return carId; }
    public void setCarId(long carId) { this.carId = carId; }
    @NonNull public String getStrMake() { return strMake; }
    public void setStrMake(@NonNull String strMake) { this.strMake = strMake; }
    @NonNull public String getStrModel() { return strModel; }
    public void setStrModel(@NonNull String strModel) { this.strModel = strModel; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    @NonNull public String getVin() { return vin; }
    public void setVin(@NonNull String vin) { this.vin = vin; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getMiles() { return miles; }
    public void setMiles(int miles) { this.miles = miles; }
    public int getOilIntervalMiles() { return oilIntervalMiles; }
    public void setOilIntervalMiles(int oilIntervalMiles) { this.oilIntervalMiles = oilIntervalMiles; }
    public int getOilIntervalMonths() { return oilIntervalMonths; }
    public void setOilIntervalMonths(int oilIntervalMonths) { this.oilIntervalMonths = oilIntervalMonths; }
    public int getTireIntervalMiles() { return tireIntervalMiles; }
    public void setTireIntervalMiles(int tireIntervalMiles) { this.tireIntervalMiles = tireIntervalMiles; }
    public int getTireIntervalMonths() { return tireIntervalMonths; }
    public void setTireIntervalMonths(int tireIntervalMonths) { this.tireIntervalMonths = tireIntervalMonths; }
    public int getBrakeIntervalMiles() { return brakeIntervalMiles; }
    public void setBrakeIntervalMiles(int brakeIntervalMiles) { this.brakeIntervalMiles = brakeIntervalMiles; }
    public int getBrakeIntervalMonths() { return brakeIntervalMonths; }
    public void setBrakeIntervalMonths(int brakeIntervalMonths) { this.brakeIntervalMonths = brakeIntervalMonths; }
    public int getWiperIntervalMonths() { return wiperIntervalMonths; }
    public void setWiperIntervalMonths(int wiperIntervalMonths) { this.wiperIntervalMonths = wiperIntervalMonths; }
    public int getRegistrationIntervalMonths() { return registrationIntervalMonths; }
    public void setRegistrationIntervalMonths(int registrationIntervalMonths) { this.registrationIntervalMonths = registrationIntervalMonths; }
    public int getLastOilChangeMileage() { return lastOilChangeMileage; }
    public void setLastOilChangeMileage(int lastOilChangeMileage) { this.lastOilChangeMileage = lastOilChangeMileage; }
    public LocalDate getLastOilChangeDate() { return lastOilChangeDate; }
    public void setLastOilChangeDate(LocalDate lastOilChangeDate) { this.lastOilChangeDate = lastOilChangeDate; }
    public int getLastTireRotationMileage() { return lastTireRotationMileage; }
    public void setLastTireRotationMileage(int lastTireRotationMileage) { this.lastTireRotationMileage = lastTireRotationMileage; }
    public LocalDate getLastTireRotationDate() { return lastTireRotationDate; }
    public void setLastTireRotationDate(LocalDate lastTireRotationDate) { this.lastTireRotationDate = lastTireRotationDate; }
    public int getLastBrakeCheckMileage() { return lastBrakeCheckMileage; }
    public void setLastBrakeCheckMileage(int lastBrakeCheckMileage) { this.lastBrakeCheckMileage = lastBrakeCheckMileage; }
    public LocalDate getLastBrakeCheckDate() { return lastBrakeCheckDate; }
    public void setLastBrakeCheckDate(LocalDate lastBrakeCheckDate) { this.lastBrakeCheckDate = lastBrakeCheckDate; }
    public int getLastWipersChangeMileage() { return lastWipersChangeMileage; }
    public void setLastWipersChangeMileage(int lastWipersChangeMileage) { this.lastWipersChangeMileage = lastWipersChangeMileage; }
    public LocalDate getLastWipersChangeDate() { return lastWipersChangeDate; }
    public void setLastWipersChangeDate(LocalDate lastWipersChangeDate) { this.lastWipersChangeDate = lastWipersChangeDate; }
    public int getNextOil() { return nextOil; }
    public void setNextOil(int nextOil) { this.nextOil = nextOil; }
    public LocalDate getNextOilChangeDate() { return nextOilChangeDate; }
    public void setNextOilChangeDate(LocalDate nextOilChangeDate) { this.nextOilChangeDate = nextOilChangeDate; }
    public int getNextTireRotation() { return nextTireRotation; }
    public void setNextTireRotation(int nextTireRotation) { this.nextTireRotation = nextTireRotation; }
    public LocalDate getNextTireRotationDate() { return nextTireRotationDate; }
    public void setNextTireRotationDate(LocalDate nextTireRotationDate) { this.nextTireRotationDate = nextTireRotationDate; }
    public int getNextBrakeIns() { return nextBrakeIns; }
    public void setNextBrakeIns(int nextBrakeIns) { this.nextBrakeIns = nextBrakeIns; }
    public LocalDate getNextBrakeCheckDate() { return nextBrakeCheckDate; }
    public void setNextBrakeCheckDate(LocalDate nextBrakeCheckDate) { this.nextBrakeCheckDate = nextBrakeCheckDate; }
    public int getNextWipers() { return nextWipers; }
    public void setNextWipers(int nextWipers) { this.nextWipers = nextWipers; }
    public LocalDate getNextWipersChangeDate() { return nextWipersChangeDate; }
    public void setNextWipersChangeDate(LocalDate nextWipersChangeDate) { this.nextWipersChangeDate = nextWipersChangeDate; }
    public LocalDate getNextRegistrationDate() { return nextRegistrationDate; }
    public void setNextRegistrationDate(LocalDate nextRegistrationDate) { this.nextRegistrationDate = nextRegistrationDate; }
    public LocalDate getRegistrationLastSetDate() { return registrationLastSetDate; }
    public void setRegistrationLastSetDate(LocalDate registrationLastSetDate) { this.registrationLastSetDate = registrationLastSetDate; }
}
