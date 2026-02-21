package com.alexeym.raccoon;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import com.alexeym.raccoon.data.Project;

import com.alexeym.raccoon.viewmodel.ProjectViewModel;

public class ProjectsFragment extends Fragment {

    private ProjectViewModel viewModel;
    private ProjectAdapter adapter;

    public ProjectsFragment() {
        super(R.layout.fragment_projects);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProjectAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnProjectLongClickListener(project -> showProjectOptions(project));

        viewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        viewModel.getAllProjects().observe(getViewLifecycleOwner(), projects -> {
            adapter.setProjects(projects);
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add_project);

        fab.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_project, null);

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etAmount = dialogView.findViewById(R.id.et_amount);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Project")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    String title = etTitle.getText().toString().trim();
                    String amountStr = etAmount.getText().toString().trim();

                    if (title.isEmpty() || amountStr.isEmpty()) return;

                    int amount = Integer.parseInt(amountStr);

                    Project project = new Project();
                    project.title = title;
                    project.amount = amount;
                    project.type = 0; // in work
                    project.createdAt = System.currentTimeMillis();
                    project.updatedAt = System.currentTimeMillis();

                    viewModel.insert(project);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showProjectOptions(Project project) {

        String[] options = {"Mark as Completed", "Mark as In Work", "Delete"};

        new AlertDialog.Builder(requireContext())
                .setTitle(project.title)
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        project.type = 1;
                        project.updatedAt = System.currentTimeMillis();
                        viewModel.update(project);
                    }

                    if (which == 1) {
                        project.type = 0;
                        project.updatedAt = System.currentTimeMillis();
                        viewModel.update(project);
                    }

                    if (which == 2) {
                        viewModel.delete(project);
                    }
                })
                .show();
    }
}