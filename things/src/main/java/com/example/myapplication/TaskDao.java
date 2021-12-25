package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task ORDER BY tid DESC")
    List<Task> getAll();

    @Query("SELECT * FROM Task  ORDER BY tid DESC LIMIT (:num) OFFSET (:start)")
    List<Task>  getNum(int num, int start);

    @Query("SELECT count(*) FROM Task")
    int findTaskCount();

    @Insert
    void insertAll(Task... tasks);

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM Task WHERE tid > 0")
    void deleteAll();

    @Query("DELETE FROM Task WHERE tid = (:tid)")
    void deleteById(int tid);

    @Query("UPDATE Task SET enable = (:isEnable) WHERE tid = (:tid)")
    void updateById(int tid, int isEnable);

}
