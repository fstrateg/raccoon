package com.alexeym.raccoon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexeym.raccoon.data.Project;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Project> expenses = new ArrayList<>();
    private OnExpenseLongClickListener listener;

    public void setExpenses(List<Project> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Project expense = expenses.get(position);
        holder.tvTitle.setText(expense.title);
        holder.tvAmount.setText(String.valueOf(expense.amount));
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(expense);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setOnExpenseLongClickListener(OnExpenseLongClickListener listener) {
        this.listener = listener;
    }

    public interface OnExpenseLongClickListener {
        void onLongClick(Project expense);
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
