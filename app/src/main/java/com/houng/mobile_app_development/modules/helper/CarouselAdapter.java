package com.houng.mobile_app_development.modules.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.houng.mobile_app_development.R;

import java.util.ArrayList;
import java.util.List;
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private final List<Integer> imageResIds;

    public CarouselAdapter(List<Integer> imageResIds) {
        if (imageResIds == null) {
            throw new IllegalArgumentException("imageResIds cannot be null");
        }
        this.imageResIds = new ArrayList<>(imageResIds.size() * 1000); // Adjust the multiplier as needed
        int count = 0;
        int repetitions = 100000;
        while (count < repetitions) {
            this.imageResIds.addAll(imageResIds);
            count++;
        }

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageResId = imageResIds.get(position);
        holder.imageView.setImageResource(imageResId);
    }

    public int getItemCount() {
        return imageResIds.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
