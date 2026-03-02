package com.alexeym.raccoon.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert
    void insert(Project project);

    @Update
    void update(Project project);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    LiveData<List<Project>> getAll();

    @Query("SELECT * FROM projects WHERE type = :type ORDER BY createdAt DESC")
    LiveData<List<Project>> getByType(int type);

    @Query("SELECT COALESCE(SUM(CASE WHEN type = 1 THEN amount WHEN type = -1 THEN -amount ELSE 0 END), 0) FROM projects")
    LiveData<Integer> getBalance();

    @Query("SELECT * FROM projects ORDER BY CASE WHEN type = 0 THEN 0 WHEN type = 1 THEN 1 ELSE 2 END, updatedAt DESC")
    LiveData<List<Project>> getAllSorted();

    @Query("SELECT * FROM projects WHERE type = :type ORDER BY updatedAt DESC")
    LiveData<List<Project>> getByTypeSorted(int type);

    @Query("SELECT * FROM projects WHERE type = -1 ORDER BY updatedAt DESC")
    LiveData<List<Project>> getExpensesSorted();
}
