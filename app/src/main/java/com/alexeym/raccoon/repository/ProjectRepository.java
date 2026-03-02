package com.alexeym.raccoon.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.alexeym.raccoon.data.AppDatabase;
import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.data.ProjectDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private final ProjectDao projectDao;
    private final ExecutorService executor;

    public ProjectRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        projectDao = db.projectDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Project>> getAllProjects() {
        return projectDao.getAll();
    }

    public LiveData<List<Project>> getProjectsByType(int type) {
        return projectDao.getByType(type);
    }

    public LiveData<List<Project>> getAllProjectsSorted() {
        return projectDao.getAllSorted();
    }

    public LiveData<List<Project>> getProjectsByTypeSorted(int type) {
        return projectDao.getByTypeSorted(type);
    }

    public LiveData<List<Project>> getExpensesSorted() {
        return projectDao.getExpensesSorted();
    }

    public LiveData<Integer> getBalance() {
        return projectDao.getBalance();
    }

    public void insert(Project project) {
        executor.execute(() -> projectDao.insert(project));
    }

    public void update(Project project) {
        executor.execute(() -> projectDao.update(project));
    }

    public void delete(Project project) {
        executor.execute(() -> projectDao.delete(project));
    }
}
