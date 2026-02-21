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