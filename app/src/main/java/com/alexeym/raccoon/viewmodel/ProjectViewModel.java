package com.alexeym.raccoon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.repository.ProjectRepository;

import java.util.List;

public class ProjectViewModel extends AndroidViewModel {

    public static final int FILTER_ALL = 999;
    public static final int FILTER_IN_WORK = 0;
    public static final int FILTER_COMPLETED = 1;
    private final ProjectRepository repository;
    private final LiveData<List<Project>> allProjects;
    private final LiveData<Integer> balance;

    private final MutableLiveData<Integer> filter = new MutableLiveData<>(FILTER_ALL);
    private final LiveData<List<Project>> projects;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        repository = new ProjectRepository(application);
        allProjects = repository.getAllProjects();
        balance = repository.getBalance();
        this.projects = Transformations.switchMap(filter, value -> {
            if (value == null || value == FILTER_ALL) {
                return repository.getAllProjectsSorted();
            } else {
                return repository.getProjectsByTypeSorted(value);
            }
        });
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public LiveData<List<Project>> getProjects() {
        return projects;
    }

    public void setFilter(int filterValue) {
        filter.setValue(filterValue);
    }

    public int getCurrentFilter() {
        Integer v = filter.getValue();
        return v == null ? FILTER_ALL : v;
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
