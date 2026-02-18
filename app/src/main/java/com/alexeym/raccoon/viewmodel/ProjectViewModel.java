package com.alexeym.raccoon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.repository.ProjectRepository;

import java.util.List;

public class ProjectViewModel extends AndroidViewModel {

    private final ProjectRepository repository;
    private final LiveData<List<Project>> allProjects;
    private final LiveData<Integer> balance;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        repository = new ProjectRepository(application);
        allProjects = repository.getAllProjects();
        balance = repository.getBalance();
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public void insert(Project project) {
        repository.insert(project);
    }

    public void update(Project project) {
        repository.update(project);
    }

    public void delete(Project project) {
        repository.delete(project);
    }
}
