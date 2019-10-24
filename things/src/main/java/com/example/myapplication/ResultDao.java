package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultDao {
    @Query("SELECT * FROM result")
    List<Result> getAll();

    @Query("SELECT * FROM result  ORDER BY time DESC LIMIT (:num) OFFSET (:start)")
    List<Result>  getNum(int num, int start);

    @Query("SELECT * FROM result WHERE rid IN (:resultIds)")
    List<Result> loadAllByIds(int[] resultIds);

    @Query("SELECT * FROM result WHERE type IN (:resultTypes)")
    List<Result> loadAllByType(String[] resultTypes);

    @Query("SELECT * FROM result WHERE time >= (:startTime) AND time <= (:endTime)")
    Result findByTime(Long startTime, Long endTime);

    @Insert
    void insertAll(Result... results);

    @Insert
    void insert(Result result);

    @Delete
    void delete(Result result);

    @Query("DELETE FROM result WHERE time <= (:time)")
    void deleteByTime(Long time);
}
