package com.alexeym.raccoon;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.viewmodel.ProjectViewModel;

public class DashboardFragment extends Fragment {

    private static final int GOAL = 10000;
    private ProjectViewModel viewModel;
    private TextView tvBalance;

    private final NumberFormat numberFormat =
            NumberFormat.getInstance(new Locale("ru", "RU"));

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBalance = view.findViewById(R.id.tv_balance);

        viewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        TextView tvGoal = view.findViewById(R.id.tv_goal);
        TextView tvPercent = view.findViewById(R.id.tv_percent);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);



        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {

            if (balance == null) balance = 0;

            String formattedBalance = numberFormat.format(balance);
            String formattedGoal = numberFormat.format(GOAL);
            tvGoal.setText("/ " + formattedGoal);
            tvBalance.setText(String.valueOf(formattedBalance));

            int percent = (int) ((balance * 100.0f) / GOAL);

            if (percent > 100) percent = 100;
            if (percent < 0) percent = 0;

            tvPercent.setText(percent + "%");
            progressBar.setProgress(percent);
        });
    }
}