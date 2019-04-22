package com.example.agrify.activity.order.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.order.model.Rating;
import com.example.agrify.activity.order.viewHolder.OrderHolder;
import com.example.agrify.activity.order.viewHolder.RatingHolder;
import com.example.agrify.databinding.ItemRatingBinding;
import com.example.agrify.databinding.OrderListItemBinding;
import com.google.firebase.firestore.Query;

public class RatingAdapter extends FirestoreAdapter<RatingHolder> {
    Activity activity;

    public RatingAdapter(Query query, Activity activity) {
        super(query);
        this.activity = activity;
    }

    @NonNull
    @Override
    public RatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRatingBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_rating, parent, false);


        return new RatingHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingHolder holder, int position) {
        holder.bind(getSnapshot(position), activity);
    }

    public Rating getRating(int pos) {
        Rating rating = getSnapshot(pos).toObject(Rating.class);
        return rating;
    }
}
