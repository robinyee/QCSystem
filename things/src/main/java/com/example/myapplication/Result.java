package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Result {
    @PrimaryKey(autoGenerate = true) // 设置主键
    public int rid;

    @ColumnInfo(name = "time")
    public Long dateTime;

    @ColumnInfo(name = "type")
    public String dataType;

    @ColumnInfo(name = "value")
    public double dataValue;

}