package com.alexeym.raccoon.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class Project {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public int amount;
    public int type; // 1 = income, 0 = in work, -1 = expense

    public long createdAt;
    public long updatedAt;
}
