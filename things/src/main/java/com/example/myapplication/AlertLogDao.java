package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlertLogDao {
    @Query("SELECT * FROM alertLog ORDER BY alert_time DESC")
    List<AlertLog> getAll();

    @Query("SELECT * FROM alertLog  ORDER BY alert_time DESC LIMIT (:num) OFFSET (:start)")
    List<AlertLog>  getNum(int num, int start);

    @Query("SELECT * FROM alertLog WHERE alertid IN (:alertId)")
    List<AlertLog> loadAllByIds(int[] alertId);

    @Query("SELECT * FROM alertLog WHERE error_id IN (:errorId)")
    List<AlertLog> loadAllByErrorId(String[] errorId);

    @Query("SELECT * FROM alertLog WHERE alert_time >= (:startTime) AND alert_time <= (:endTime) ORDER BY alert_time DESC")
    List<AlertLog> findByTime(Long startTime, Long endTime);

    @Query("SELECT count(*) FROM alertlog")
    int findAlertLogCount();

    @Insert
    void insertAll(AlertLog... alertlog);

    @Insert
    void insert(AlertLog alertlog);

    @Update
    void Update(AlertLog... alertlog);

    @Query("UPDATE alertLog SET reset_flag = (:flag), reset_time = (:resetTime) WHERE alert_time = (:alertTime)")
    void updateByTime(Long alertTime, int flag, Long resetTime);

    @Query("UPDATE alertLog SET reset_flag = (:flag), reset_time = (:resetTime) WHERE alertid = (:id)")
    void updateById(int id, int flag, Long resetTime);

    @Query("UPDATE alertLog SET reset_flag = 1, reset_time = (:resetTime) WHERE reset_flag = 0")
    void updateByFlag(Long resetTime);

    @Delete
    void delete(AlertLog alertLog);

    @Query("DELETE FROM alertLog WHERE alert_time <= (:alertTime)")
    void deleteByTime(Long alertTime);
}
