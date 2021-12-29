package com.example.myapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class, Task.class, AlertLog.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordDao recordDao();
    public abstract TaskDao taskDao();
    public abstract AlertLogDao alertLogDao();
}
