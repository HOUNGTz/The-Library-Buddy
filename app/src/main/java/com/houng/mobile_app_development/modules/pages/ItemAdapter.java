package com.houng.mobile_app_development.modules.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.model.Book_model;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context mContext;
    private List<Book_model> itemList;

    public ItemAdapter(Context context, List<Book_model> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adaptor, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Book_model item = itemList.get(position);
        holder.nameTextView.setText(item.title);
        holder.descriptionTextView.setText(item.des);
        Glide.with(mContext)
                .load(item.image)
                .into(holder.imageUrl);
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public ImageView imageUrl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            imageUrl = itemView.findViewById(R.id.itemImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
