package com.example.autolog.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "modification",
        foreignKeys = @ForeignKey(
                entity = Car.class,
                parentColumns = "carId",
                childColumns = "carId",
                onDelete = ForeignKey.CASCADE
        ))

public class Modification {

    @PrimaryKey(autoGenerate = true)
    public long modsId;
    public long carId;
    public String modName;
    public int date;
    public double cost;
    public String strNotes;
}
