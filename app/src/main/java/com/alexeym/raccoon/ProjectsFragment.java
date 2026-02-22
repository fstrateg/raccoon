package com.alexeym.raccoon;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

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

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.RaccoonDialogTheme)
                .setView(dialogView)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView btnSave = dialogView.findViewById(R.id.btn_save);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {

            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty()) return;

            int amount = Integer.parseInt(amountStr);

            Project project = new Project();
            project.title = title;
            project.amount = amount;
            project.type = 0;
            project.createdAt = System.currentTimeMillis();
            project.updatedAt = System.currentTimeMillis();

            viewModel.insert(project);

            dialog.dismiss();
        });

    }
    private void showProjectOptions(Project project) {

        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_project_options, null);

        AlertDialog dialog = new AlertDialog.Builder(
                requireContext(),
                R.style.RaccoonDialogTheme
        )
                .setView(dialogView)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView optionComplete = dialogView.findViewById(R.id.option_complete);
        TextView optionInWork = dialogView.findViewById(R.id.option_in_work);
        TextView optionDelete = dialogView.findViewById(R.id.option_delete);

        optionComplete.setOnClickListener(v -> {
            project.type = 1;
            project.updatedAt = System.currentTimeMillis();
            viewModel.update(project);
            dialog.dismiss();
        });

        optionInWork.setOnClickListener(v -> {
            project.type = 0;
            project.updatedAt = System.currentTimeMillis();
            viewModel.update(project);
            dialog.dismiss();
        });

        optionDelete.setOnClickListener(v -> {
            viewModel.delete(project);
            dialog.dismiss();
        });
    }
}