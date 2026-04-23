package com.alexeym.raccoon;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.viewmodel.ProjectViewModel;

public class DashboardFragment extends Fragment {

    private static final String PREFS_NAME = "raccoon_prefs";
    private static final String KEY_GOAL = "goal";
    private Integer lastBalance = 0;
    private ProjectViewModel viewModel;
    private TextView tvBalance;
    private TextView tvGoal;
    private TextView tvPercent;
    private ProgressBar progressBar;

    private final NumberFormat numberFormat =
            NumberFormat.getInstance(new Locale("ru", "RU"));

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }
    private int getGoal() {
        return requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_GOAL, 10000); // default 10k
    }

    private void saveGoal(int goal) {
        requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_GOAL, goal)
                .apply();
    }

    private void render(int balance) {
        int goal = getGoal();

        String formattedBalance = numberFormat.format(balance);
        String formattedGoal = numberFormat.format(goal);

        tvBalance.setText(formattedBalance);
        tvGoal.setText("/ " + formattedGoal);

        int percent = (int) ((balance * 100.0f) / goal);
        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;

        tvPercent.setText(percent + "%");
        progressBar.setProgress(percent);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBalance = view.findViewById(R.id.tv_balance);

        viewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        tvGoal = view.findViewById(R.id.tv_goal);
        tvPercent = view.findViewById(R.id.tv_percent);
        progressBar = view.findViewById(R.id.progress_bar);
        TextView btnEditGoal = view.findViewById(R.id.btn_edit_goal);

        btnEditGoal.setOnClickListener(v -> showGoalDialog());

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance == null) balance = 0;
            lastBalance = balance;
            render(balance);
        });
    }
    private void showGoalDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_goal, null);

        EditText etGoal = dialogView.findViewById(R.id.et_goal);
        TextView btnSave = dialogView.findViewById(R.id.btn_save);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btnFinalize = dialogView.findViewById(R.id.btn_finalize);

        etGoal.setText(String.valueOf(getGoal()));

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.RaccoonDialogTheme)
                .setView(dialogView)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String value = etGoal.getText().toString().trim();
            if (value.isEmpty()) return;

            int newGoal = Integer.parseInt(value);
            if (newGoal <= 0) return; // защита от нуля

            saveGoal(newGoal);

            // Вот оно: цель изменилась, вручную перерисовываем по последнему балансу
            render(lastBalance == null ? 0 : lastBalance);

            dialog.dismiss();
        });

        btnFinalize.setOnClickListener(v -> {

            View confirmView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

            AlertDialog confirmDialog = new AlertDialog.Builder(requireContext(), R.style.RaccoonDialogTheme)
                    .setView(confirmView)
                    .create();

            confirmDialog.show();

            if (confirmDialog.getWindow() != null) {
                confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView btnYes = confirmView.findViewById(R.id.btn_yes);
            TextView btnNo = confirmView.findViewById(R.id.btn_no);

            btnNo.setOnClickListener(v1 -> confirmDialog.dismiss());

            btnYes.setOnClickListener(v1 -> {

                int balance = lastBalance == null ? 0 : lastBalance;

                viewModel.clearCompletedAndExpenses();

                Toast.makeText(requireContext(),
                        "Итог: " + NumberFormat.getInstance().format(balance) + " енотов",
                        Toast.LENGTH_LONG).show();

                confirmDialog.dismiss();
                dialog.dismiss();
            });
        });
    }

}