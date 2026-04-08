package com.example.autolog.data;

import androidx.room.TypeConverter;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateConverter {

    // Converts a LocalDate object to a long
    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // Converts a Long back to a LocalDate object
    @TypeConverter
    public static LocalDate toLocalDate(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        }
        return java.time.Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
