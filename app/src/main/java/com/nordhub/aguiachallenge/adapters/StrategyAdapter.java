package com.nordhub.aguiachallenge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nordhub.aguiachallenge.R;
import com.nordhub.aguiachallenge.models.Strategy;

import java.util.List;

public class StrategyAdapter extends RecyclerView.Adapter<StrategyAdapter.StrategyViewHolder> {

    private final List<Strategy> strategies;

    public StrategyAdapter(List<Strategy> strategies) {
        this.strategies = strategies;
    }

    @NonNull
    @Override
    public StrategyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new StrategyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StrategyViewHolder holder, int position) {
        Strategy strategy = strategies.get(position);
        holder.title.setText(value(strategy.getTitulo()));
        holder.description.setText(value(strategy.getDescricao()));
    }

    @Override
    public int getItemCount() {
        return strategies.size();
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "Sem informação" : text;
    }

    static class StrategyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;

        StrategyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            description = itemView.findViewById(android.R.id.text2);
        }
    }
}
