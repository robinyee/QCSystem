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

    @Insert
    void insertAll(Task... tasks);

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM Task WHERE tid = (:tid)")
    void deleteById(int tid);

    @Query("UPDATE Task SET enable = (:isEnable) WHERE tid = (:tid)")
    void updateById(int tid, int isEnable);

}
