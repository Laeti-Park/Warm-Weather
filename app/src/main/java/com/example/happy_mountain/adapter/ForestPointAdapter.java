package com.example.happy_mountain.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happy_mountain.R;

public class ForestPointAdapter extends RecyclerView.Adapter<ForestPointAdapter.ViewHolder> {

    public ForestPointAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forest_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView pointLow;
        TextView pointRatherHigh;
        TextView pointHigh;
        TextView pointVeryHigh;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pointLow = itemView.findViewById(R.id.location);
            pointRatherHigh = itemView.findViewById(R.id.warningRate);
            pointHigh = itemView.findViewById(R.id.humidity);
            pointVeryHigh = itemView.findViewById(R.id.windSpeed);
        }
    }
}