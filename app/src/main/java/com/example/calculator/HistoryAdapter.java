package com.example.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<String[]> historyList;

    public HistoryAdapter(List<String[]> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] item = historyList.get(position);
        // item[0] is the Equation, item[1] is the Result
        holder.tvEquation.setText(item[0]);
        holder.tvResult.setText(item[1]);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEquation, tvResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquation = itemView.findViewById(R.id.hiEquation);
            tvResult = itemView.findViewById(R.id.hiResult);
        }
    }
}