package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true) // 设置主键
    public int rid;

    @ColumnInfo(name = "time")
    public Long dateTime;

    @ColumnInfo(name = "type")
    public String dataType;

    @ColumnInfo(name = "prevalue")
    public double preValue;

    @ColumnInfo(name = "meavalue")
    public double meaValue;

}