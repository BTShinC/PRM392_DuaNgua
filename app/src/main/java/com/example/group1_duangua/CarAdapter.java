package com.example.group1_duangua;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private int[] carImages;

    public CarAdapter(int[] carImages) {
        this.carImages = carImages;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return new CarViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        holder.imageView.setImageResource(carImages[position % carImages.length]);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // cho chạy vòng lặp vô tận
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CarViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}

