package com.alexeym.raccoon;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alexeym.raccoon.data.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projects = new ArrayList<>();
    private OnProjectLongClickListener listener;

    public void setProjects(List<Project> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.tvTitle.setText(project.title);
        holder.tvAmount.setText(String.valueOf(project.amount));
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(project);
            }
            return true;
        });
        // Сбрасываем состояние ВСЕГДА
        holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.tvTitle.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.sc_text_primary)
        );

        // Применяем состояние для завершённых
        if (project.type == 1) {
            holder.tvTitle.setPaintFlags(
                    holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.tvTitle.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.sc_accent_alt)
            );
        }
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void setOnProjectLongClickListener(OnProjectLongClickListener listener) {
        this.listener = listener;
    }
    public interface OnProjectLongClickListener {
        void onLongClick(Project project);
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvAmount;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}