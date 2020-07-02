package com.example.myapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Result.class, AlertLog.class, Calibration.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
    public abstract AlertLogDao alertLogDao();
    public abstract CalibrationDao calibrationDao();
}
