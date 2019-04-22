package com.example.agrify.activity.order.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.activity.order.viewHolder.OrderHolder;
import com.example.agrify.activity.order.viewHolder.OrderItemHolder;
import com.example.agrify.databinding.OrderItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class OrderItemAdapter extends FirestoreAdapter<OrderItemHolder> {
    Activity activity;
    OnClickRateListener listener;

    public OrderItemAdapter(Query query, Activity activity, OnClickRateListener listener) {
        super(query);
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        OrderItemBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.order_item, parent, false);


        return new OrderItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        holder.bind(getSnapshot(position), activity, listener);

    }

    public interface OnClickRateListener {
        void onClikedRating(DocumentSnapshot snapshot, OrderItem orderItem);
    }
}
