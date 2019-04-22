package com.example.agrify.activity.order.viewHolder;

import android.app.Activity;
import android.view.View;


import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.order.adapter.OrderAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.databinding.OrderItemBinding;
import com.example.agrify.databinding.OrderListItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class OrderHolder extends RecyclerView.ViewHolder {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy");
    OrderListItemBinding binding;
    Order order;

    public OrderHolder(OrderListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(DocumentSnapshot snapshot, Activity activity, OrderAdapter.OnOrderItemClickListener listener) {
        order = new Order();
        try {
            order = snapshot.toObject(Order.class);
            binding.orderId.setText("Order id:" + order.getOrderId());
            binding.orderDate.setText("Date:" + simpleDateFormat.format(order.getTimestamp().toDate()));
            binding.orderStatus.setText("status:" + order.getOrderStatus());
            binding.totalPrice.setText("₹" + NumberFormat.getInstance().format(order.getTotalAmount()));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOrderItemClicked(snapshot);
            }
        });
    }
}
