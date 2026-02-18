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

    @Query("SELECT SUM(CASE WHEN type = 1 THEN amount WHEN type = -1 THEN -amount ELSE 0 END ) "+
            "FROM projects")
    LiveData<Integer> getBalance();
}
