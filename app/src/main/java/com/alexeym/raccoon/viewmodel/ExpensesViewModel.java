package com.alexeym.raccoon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.repository.ProjectRepository;

import java.util.List;

public class ExpensesViewModel extends AndroidViewModel {

    private final ProjectRepository repository;
    private final LiveData<List<Project>> expenses;

    public ExpensesViewModel(@NonNull Application application) {
        super(application);
        repository = new ProjectRepository(application);
        expenses = repository.getExpensesSorted();
    }

    public LiveData<List<Project>> getExpenses() {
        return expenses;
    }

    public void insert(Project expense) {
        repository.insert(expense);
    }

    public void update(Project expense) {
        repository.update(expense);
    }

    public void delete(Project expense) {
        repository.delete(expense);
    }
}
