package com.example.agrify.activity.sellerProduct.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.sellerProduct.viewHolder.cartHolder;
import com.example.agrify.activity.viewHolder.StoreHolder;
import com.example.agrify.databinding.CartItemBinding;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.google.firebase.firestore.Query;

public class CartAdapter extends FirestoreAdapter<cartHolder> {
    Activity activity;

    public CartAdapter(Query query, Activity activity) {
        super(query);
        this.activity=activity;
    }

    @NonNull
    @Override
    public cartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CartItemBinding itemCartBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_item, parent, false);


        return new cartHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull cartHolder holder, int position) {
        holder.bind(getSnapshot(position),activity);

    }
}
