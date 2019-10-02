package com.example.agrify.activity.order.viewHolder;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.agrify.activity.order.adapter.OrderItemAdapter;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.databinding.OrderItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;

public class OrderItemHolder extends RecyclerView.ViewHolder {
    OrderItemBinding binding;
    OrderItem orderItem = new OrderItem();

    public OrderItemHolder(OrderItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(DocumentSnapshot snapshot, Activity activity, OrderItemAdapter.OnClickRateListener listener) {
        try {
            orderItem = snapshot.toObject(OrderItem.class);
            binding.productName.setText(orderItem.getName());
            binding.productPrice.setText("₹" + NumberFormat.getInstance().format(orderItem.getPrice()));
            binding.quantityTextView.setText(String.valueOf(orderItem.getQuantity()));
            binding.unit.setText(orderItem.getUnit());
            if (orderItem.isReviewed()) {
                binding.writeReviewButton.setVisibility(View.GONE);
                binding.ratingBar.setRating(orderItem.getRating());
                binding.ratingBar.setVisibility(View.VISIBLE);
            } else {
                binding.writeReviewButton.setVisibility(View.VISIBLE);
                binding.ratingBar.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (activity != null) {
            Glide.with(activity)
                    .load(orderItem.getProductImageUrl())
                    .into(binding.productImage);
        }
        binding.writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClikedRating(snapshot, orderItem);
            }
        });


    }

}
