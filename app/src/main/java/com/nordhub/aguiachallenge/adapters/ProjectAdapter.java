package com.nordhub.aguiachallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nordhub.aguiachallenge.R;
import com.nordhub.aguiachallenge.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private final List<Project> projects;

    public ProjectAdapter(List<Project> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.tvName.setText(value(project.getNome()));
        holder.tvStatus.setText("Status: " + value(project.getStatus()));
        holder.tvValues.setText("Investimento: R$ " + value(project.getInvestimento()) + " | Retorno: R$ " + value(project.getRetorno()));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "0" : text;
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvStatus;
        final TextView tvValues;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProjectName);
            tvStatus = itemView.findViewById(R.id.tvProjectStatus);
            tvValues = itemView.findViewById(R.id.tvProjectValues);
        }
    }
}
