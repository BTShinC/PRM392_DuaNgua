package com.example.group1_duangua;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<BetHistory> historyList;

    public HistoryAdapter(List<BetHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        BetHistory bet = historyList.get(position);
        holder.text1.setText("Xe: " + bet.getCarName() + " | " + bet.getAmount() + " coin");
        holder.text2.setText((bet.isWin() ? "✔ Thắng" : "✖ Thua") + " | " + bet.getTime());
        holder.text2.setTextColor(bet.isWin() ? 0xFF00FF00 : 0xFFFF4444); // xanh/đỏ
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}