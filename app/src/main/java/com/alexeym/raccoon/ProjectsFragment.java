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

    private void showEditDialog(Project project)
    {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_project, null);
        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etAmount = dialogView.findViewById(R.id.et_amount);

        TextView btnSave = dialogView.findViewById(R.id.btn_save);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        TextView btnInWork = dialogView.findViewById(R.id.btn_status_in_work);
        TextView btnCompleted = dialogView.findViewById(R.id.btn_status_completed);

        // Заполняем текущими данными
        etTitle.setText(project.title);
        etAmount.setText(String.valueOf(project.amount));

        // Текущий статус (через массив, чтобы менять внутри lambda)
        final int[] currentStatus = { project.type };

        // Обновление визуального состояния статуса
        Runnable updateStatusUI = () -> {

            if (currentStatus[0] == 0) {

                btnInWork.setBackgroundResource(R.drawable.status_button_active);
                btnCompleted.setBackgroundResource(R.drawable.status_button_inactive);

                btnInWork.setTextColor(getResources().getColor(R.color.sc_accent));
                btnCompleted.setTextColor(getResources().getColor(R.color.sc_text_primary));

            } else {

                btnCompleted.setBackgroundResource(R.drawable.status_button_active);
                btnInWork.setBackgroundResource(R.drawable.status_button_inactive);

                btnCompleted.setTextColor(getResources().getColor(R.color.sc_accent_alt));
                btnInWork.setTextColor(getResources().getColor(R.color.sc_text_primary));
            }
        };

        updateStatusUI.run();

        btnInWork.setOnClickListener(v -> {
            currentStatus[0] = 0;
            updateStatusUI.run();
        });

        btnCompleted.setOnClickListener(v -> {
            currentStatus[0] = 1;
            updateStatusUI.run();
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.RaccoonDialogTheme)
                .setView(dialogView)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {

            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty()) return;

            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) return;

            project.title = title;
            project.amount = amount;
            project.type = currentStatus[0];
            project.updatedAt = System.currentTimeMillis();

            viewModel.update(project);

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

        TextView optionEdit = dialogView.findViewById(R.id.option_edit);
        TextView optionComplete = dialogView.findViewById(R.id.option_complete);
        TextView optionInWork = dialogView.findViewById(R.id.option_in_work);
        TextView optionDelete = dialogView.findViewById(R.id.option_delete);

        if (project.type == 1) {
            optionComplete.setVisibility(View.GONE);
        } else {
            optionInWork.setVisibility(View.GONE);
        }

        optionEdit.setOnClickListener(v -> {
            dialog.dismiss();
            showEditDialog(project);
        });


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