package com.alexeym.raccoon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.viewmodel.ProjectViewModel;

public class DashboardFragment extends Fragment {

    private ProjectViewModel viewModel;
    private TextView tvBalance;
    private Button btnAddTest;

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBalance = view.findViewById(R.id.tv_balance);
        btnAddTest = view.findViewById(R.id.btn_add_test);

        viewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance == null) balance = 0;
            tvBalance.setText("Balance: " + balance);
        });

        btnAddTest.setOnClickListener(v -> {
            Project test = new Project();
            test.title = "Test income";
            test.amount = 1000;
            test.type = 1;
            test.createdAt = System.currentTimeMillis();
            test.updatedAt = System.currentTimeMillis();

            viewModel.insert(test);
        });
    }
}