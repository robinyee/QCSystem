package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface RecordDao {
    @Query("SELECT * FROM Record ORDER BY time DESC")
    List<Record> getAll();

    @Query("SELECT * FROM Record  ORDER BY time DESC LIMIT (:num) OFFSET (:start)")
    List<Record>  getNum(int num, int start);

    @Query("SELECT * FROM Record WHERE rid IN (:resultIds)")
    List<Record> loadAllByIds(int[] resultIds);

    @Query("SELECT * FROM Record WHERE type IN (:types)")
    List<Record> loadAllByType(String[] types);

    @Query("SELECT * FROM Record WHERE time >= (:startTime) AND time <= (:endTime) ORDER BY time DESC")
    List<Record> findByTime(Long startTime, Long endTime);

    @Query("SELECT count(*) FROM Record")
    int findRecordCount();

    @Insert
    void insertAll(Record... records);

    @Insert
    void insert(Record record);

    @Delete
    void delete(Record record);

    @Query("DELETE FROM Record WHERE time <= (:time)")
    void deleteByTime(Long time);

}
