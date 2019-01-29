package com.example.agrify.activity.viewHolder;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.databinding.ItemSellerBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class SellerHolder extends RecyclerView.ViewHolder {

    ItemSellerBinding binding;
    public SellerHolder(@NonNull ItemSellerBinding item) {
        super(item.getRoot());
        binding = item;
    }
    public void bind(final DocumentSnapshot snapshot, final Activity activity) {

        Seller seller = snapshot.toObject(Seller.class);
        Resources resources = itemView.getResources();

        binding.setSeller(seller);

        // Load image
        if (activity != null) {
            GlideApp.with(activity)
                    .load(seller.getProfilePhotoUrl())
                    .into(binding.profilePhoto);
        }
 final String phoneNumber=seller.getPhone();
        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                v.getContext().startActivity(intent);
            }
        });
    }



}
