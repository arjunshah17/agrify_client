package com.example.agrify.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.viewHolder.SellerHolder;
import com.example.agrify.activity.viewHolder.StoreHolder;
import com.example.agrify.databinding.ItemSellerBinding;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.google.firebase.firestore.Query;

public class SellerAdapter extends FirestoreAdapter<SellerHolder> {
    Activity activity;
    public SellerAdapter(Query query,Activity activity) {
        super(query);
        this.activity=activity;

    }


    @NonNull
    @Override
    public SellerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSellerBinding itemSellerBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_seller, parent, false);


        return new SellerHolder(itemSellerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerHolder holder, int position) {
        holder.bind(getSnapshot(position),activity);
    }
}
