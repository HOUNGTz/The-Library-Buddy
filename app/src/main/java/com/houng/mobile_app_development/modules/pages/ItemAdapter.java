package com.houng.mobile_app_development.modules.pages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.modules.model.Book_model;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final Context mContext;
    private static List<Book_model> itemList;
    public ItemAdapter(Context context, List<Book_model> itemList) {
        this.mContext = context;
        ItemAdapter.itemList = itemList;
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
        String isTapped = "";
        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            imageUrl = itemView.findViewById(R.id.itemImageView);
            imageUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // Check if item still exists
                        Intent intent = new Intent(v.getContext(), BookDetailsPage.class);
                        intent.putExtra("title", itemList.get(position).title);
                        intent.putExtra("description", itemList.get(position).des);
                        intent.putExtra("category", itemList.get(position).category);
                        intent.putExtra("image", itemList.get(position).image);
                        intent.putExtra("story", itemList.get(position).story);
                        intent.putExtra("rate", itemList.get(position).rate);
                        intent.putExtra("subtitle", itemList.get(position).subtitle);
                        intent.putExtra("isTapped", isTapped);
                        // You might want to pass other data as well
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
