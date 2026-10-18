package com.nordhub.aguiachallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nordhub.aguiachallenge.R;
import com.nordhub.aguiachallenge.models.Idea;

import java.util.List;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder> {

    private final List<Idea> ideas;

    public IdeaAdapter(List<Idea> ideas) {
        this.ideas = ideas;
    }

    @NonNull
    @Override
    public IdeaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idea, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeaViewHolder holder, int position) {
        Idea idea = ideas.get(position);
        holder.tvTitle.setText(value(idea.getTitulo()));
        holder.tvDescription.setText(value(idea.getDescricao()));
        holder.tvStatus.setText("Status: " + value(idea.getStatus()));
    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "Sem informação" : text;
    }

    static class IdeaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvStatus;

        IdeaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvIdeaTitle);
            tvDescription = itemView.findViewById(R.id.tvIdeaDescription);
            tvStatus = itemView.findViewById(R.id.tvIdeaStatus);
        }
    }
}
