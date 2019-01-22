package com.example.agrify.activity.viewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.databinding.ItemStoreProductBinding;


public class StoreHolder extends RecyclerView.ViewHolder {
    public ItemStoreProductBinding binding;

    public StoreHolder(@NonNull ItemStoreProductBinding item) {
        super(item.getRoot());
        binding = item;
    }
}