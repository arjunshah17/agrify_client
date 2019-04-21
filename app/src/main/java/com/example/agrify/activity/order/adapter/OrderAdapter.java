package com.example.agrify.activity.order.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.order.viewHolder.OrderHolder;
import com.example.agrify.activity.sellerProduct.viewHolder.cartHolder;
import com.example.agrify.databinding.OrderItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class OrderAdapter extends FirestoreAdapter<OrderHolder> {
    Activity activity;
    OnOrderItemClickListener listener;

    public OrderAdapter(Query query, Activity activity, OnOrderItemClickListener listener) {
        super(query);
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.order_item, parent, false);


        return new OrderHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        holder.bind(getSnapshot(position), activity, listener);
    }

    public interface OnOrderItemClickListener {

        void onOrderItemClicked(DocumentSnapshot snapshot);

    }
}
