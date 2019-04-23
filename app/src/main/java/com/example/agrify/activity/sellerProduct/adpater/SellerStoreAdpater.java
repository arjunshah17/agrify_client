package com.example.agrify.activity.sellerProduct.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.sellerProduct.viewHolder.SellerProductHolder;

import com.example.agrify.databinding.SellerProductItemBinding;
import com.google.firebase.firestore.Query;

public class SellerStoreAdpater extends FirestoreAdapter<SellerProductHolder> {
    Activity activity;

    private OnProductSelectedListener listener;
    public interface OnProductSelectedListener {

        void onProductSelected(String productId,String SellerId);

    }

    public SellerStoreAdpater(Query query, Activity activity, OnProductSelectedListener listener) {
        super(query);
        this.activity = activity;

        this.listener = listener;
    }

    @NonNull
    @Override
    public SellerProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SellerProductItemBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.seller_product_item, parent, false);
        return new SellerProductHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerProductHolder holder, int position) {
        holder.bind(getSnapshot(position),listener,activity);
    }
}
