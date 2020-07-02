package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Calibration {
    @PrimaryKey(autoGenerate = true) // 设置主键
    public int cid;

    @ColumnInfo(name = "time")
    public Long dateTime;

    @ColumnInfo(name = "by_value")
    public double byValue;

    @ColumnInfo(name = "csn_value")
    public double csnValue;

    @ColumnInfo(name = "gmsj_value")
    public double gmsjValue;

    @ColumnInfo(name = "coefficient")
    public double coefficient;

    @ColumnInfo(name = "new_value")
    public double newValue;

}