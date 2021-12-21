package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
class Task {
    @PrimaryKey(autoGenerate = true) // 设置主键
    public int tid;

    @ColumnInfo(name = "starttime")
    public Long startTime;

    @ColumnInfo(name = "endtime")
    public String endTime;

    @ColumnInfo(name = "cron")
    public String cron;

    @ColumnInfo(name = "task")
    public String task;

    @ColumnInfo(name = "enable")
    public int enable;

}
