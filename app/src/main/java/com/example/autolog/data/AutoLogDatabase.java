package com.example.autolog.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.autolog.data.DateConverter;

@Database(
        entities = {Car.class, ServiceRecord.class, Modification.class},
        version = 27, exportSchema = false
)

@TypeConverters({DateConverter.class})


public abstract class AutoLogDatabase extends RoomDatabase{
    public abstract AutoLogDao autoLogDao();
    public abstract ServiceRecordDao serviceRecordDao();

    private static volatile AutoLogDatabase INSTANCE;
    public static AutoLogDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (AutoLogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AutoLogDatabase.class,
                            "autolog_db"
                    )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
