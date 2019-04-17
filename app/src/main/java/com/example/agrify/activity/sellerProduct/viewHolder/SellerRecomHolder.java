package com.example.agrify.activity.sellerProduct.viewHolder;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.model.Store;
import com.example.agrify.activity.sellerProduct.adpater.SellerRecomAdpater;
import com.example.agrify.databinding.RecomProductItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerRecomHolder extends RecyclerView.ViewHolder {
    FirebaseFirestore firebaseFirestore;
    Store store;
    RecomProductItemBinding binding;
    public SellerRecomHolder(@NonNull RecomProductItemBinding binding) {
        super(binding.getRoot());
        this.binding=binding;
        firebaseFirestore=FirebaseFirestore.getInstance();
        store=new Store();

    }

    public void bind(DocumentSnapshot sellerSnapshot, SellerRecomAdpater.OnProductSelectedListener listener, Activity activity) {

        String productId=sellerSnapshot.getString("productId");
        String sellerId=sellerSnapshot.getId();

        firebaseFirestore.collection("store").document(sellerSnapshot.getString("productId")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                store=snapshot.toObject(Store.class);
                binding.productName.setText(store.getName());
                binding.productPrice.setText("₹"+sellerSnapshot.getDouble("price").toString()+"/"+store.getUnit());

                // Load image
                if (activity != null) {
                    GlideApp.with(activity)
                            .load(store.getProductImageUrl())
                            .into(binding.productImage);
                }

            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductSelected(productId,sellerId);
            }
        });


    }
}
