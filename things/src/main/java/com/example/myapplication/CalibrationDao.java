package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CalibrationDao {
    @Query("SELECT * FROM calibration ORDER BY time DESC")
    List<Calibration> getAll();

    @Query("SELECT * FROM calibration  ORDER BY time DESC LIMIT (:num) OFFSET (:start)")
    List<Calibration>  getNum(int num, int start);

    @Query("SELECT * FROM calibration WHERE cid IN (:cids)")
    List<Calibration> loadAllByIds(int[] cids);

    @Query("SELECT * FROM calibration WHERE time >= (:startTime) AND time <= (:endTime) ORDER BY time DESC")
    List<Calibration> findByTime(Long startTime, Long endTime);

    @Query("SELECT count(*) FROM calibration")
    int findCalibrationCount();

    @Insert
    void insertAll(Calibration... calibrations);

    @Insert
    void insert(Calibration calibration);

    @Delete
    void delete(Calibration calibration);

    @Query("DELETE FROM calibration WHERE time <= (:time)")
    void deleteByTime(Long time);

    @Query("DELETE FROM calibration WHERE cid = (:id)")
    void deleteById(int id);
}
