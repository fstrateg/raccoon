package com.alexeym.raccoon;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexeym.raccoon.data.Project;
import com.alexeym.raccoon.viewmodel.ExpensesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExpensesFragment extends Fragment {

    private ExpensesViewModel viewModel;
    private ExpenseAdapter adapter;

    public ExpensesFragment() {
        super(R.layout.fragment_expenses);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);
        adapter = new ExpenseAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getExpenses().observe(getViewLifecycleOwner(), list -> adapter.setExpenses(list));

        FloatingActionButton fab = view.findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(v -> showAddExpenseDialog());

        adapter.setOnExpenseLongClickListener(this::showExpenseOptions);
    }

    private void showAddExpenseDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_expense, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        TextView btnSave = dialogView.findViewById(R.id.btn_save);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Сумма должна быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Project expense = new Project();
            expense.title = title;
            expense.amount = amount;
            expense.type = -1; // Расход
            expense.createdAt = System.currentTimeMillis();
            expense.updatedAt = System.currentTimeMillis();

            viewModel.insert(expense);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showExpenseOptions(Project expense) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_expense_options, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.option_edit).setOnClickListener(v -> {
            dialog.dismiss();
            showEditExpenseDialog(expense);
        });

        dialogView.findViewById(R.id.option_delete).setOnClickListener(v -> {
            viewModel.delete(expense);
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showEditExpenseDialog(Project expense) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_expense, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        TextView btnSave = dialogView.findViewById(R.id.btn_save);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        etTitle.setText(expense.title);
        etAmount.setText(String.valueOf(expense.amount));

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            expense.title = title;
            expense.amount = amount;
            expense.updatedAt = System.currentTimeMillis();

            viewModel.update(expense);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }
}
