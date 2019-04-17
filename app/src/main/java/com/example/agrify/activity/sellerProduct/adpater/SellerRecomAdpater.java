package com.example.agrify.activity.sellerProduct.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.sellerProduct.viewHolder.SellerRecomHolder;
import com.example.agrify.activity.viewHolder.StoreHolder;
import com.example.agrify.databinding.RecomProductItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class SellerRecomAdpater  extends FirestoreAdapter<SellerRecomHolder> {
    Activity activity;

    private OnProductSelectedListener listener;
    public interface OnProductSelectedListener {

        void onProductSelected(String productId,String SellerId);

    }

    public SellerRecomAdpater(Query query, Activity activity, OnProductSelectedListener listener) {
        super(query);
        this.activity = activity;

        this.listener = listener;
    }

    @NonNull
    @Override
    public SellerRecomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecomProductItemBinding binding= DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.recom_product_item, parent, false);
        return new SellerRecomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerRecomHolder holder, int position) {
        holder.bind(getSnapshot(position),listener,activity);
    }
}
