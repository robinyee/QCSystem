package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AlertLog {
    @PrimaryKey(autoGenerate = true) // 设置主键
    public int alertid;

    @ColumnInfo(name = "alert_time")
    public Long alertTime;

    @ColumnInfo(name = "error_id")
    public int errorId;

    @ColumnInfo(name = "error_msg")
    public String errorMsg;

    @ColumnInfo(name = "reset_flag")
    public int resetFlag;

    @ColumnInfo(name = "reset_time")
    public Long resetTime;

}