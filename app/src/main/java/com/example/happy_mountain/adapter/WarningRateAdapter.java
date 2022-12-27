package com.example.happy_mountain.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happy_mountain.R;
import com.example.happy_mountain.item.WarningRateItem;

import java.util.List;

public class WarningRateAdapter extends RecyclerView.Adapter<WarningRateAdapter.ViewHolder> {
    private List<WarningRateItem> warningRateItems;

    public WarningRateAdapter(List<WarningRateItem> item) {
        this.warningRateItems = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning_rate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(warningRateItems.get(position));
    }

    @Override
    public int getItemCount() {
        return warningRateItems.size();
    }

    public void setWarningRateItem(List<WarningRateItem> warningRateItems) {
        this.warningRateItems = warningRateItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateView;
        TextView locationView;
        TextView warningRateView;
        TextView humidityView;
        TextView windSpeedView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateView_f);
            locationView = itemView.findViewById(R.id.locationView_f);
            warningRateView = itemView.findViewById(R.id.warningRateView_f);
            humidityView = itemView.findViewById(R.id.humidityView_f);
            windSpeedView = itemView.findViewById(R.id.windSpeed_f);
        }

        public void bind(WarningRateItem warningRateItem) {
            dateView.setText(warningRateItem.getTime());
            locationView.setText(warningRateItem.getLocation());
            warningRateView.setText(warningRateItem.getWarningRate());
            humidityView.setText(warningRateItem.getHumidity());
            windSpeedView.setText(warningRateItem.getWindSpeed());
        }
    }
}