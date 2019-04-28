package com.example.agrify.activity.viewHolder;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.activity.model.Store;
import com.example.agrify.databinding.CartItemBinding;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.varunest.sparkbutton.SparkEventListener;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;


public class StoreHolder extends RecyclerView.ViewHolder {
    public ItemStoreProductBinding binding;
FirebaseAuth auth;
FirebaseFirestore firebaseFirestore;
DocumentReference product_wish;
DocumentReference user_product_wish;
WriteBatch batch;
    Store store;

    public StoreHolder(@NonNull ItemStoreProductBinding item) {
        super(item.getRoot());
        binding = item;


    }



    public void bind(final DocumentSnapshot snapshot,
                     final StoreAdapter.OnStoreSelectedListener listener, final Activity activity,String TAG)
    {
        store = new Store();
        auth = FirebaseAuth.getInstance();

        if (TAG.equals("StoreFragment")) {
            store = snapshot.toObject(Store.class);


            binding.setStore(store);
            if(store.getPrice()==0)
            {
                binding.price.setText("no seller is selling");
            }
            else {
                binding.price.setText("start from ₹" + NumberFormat.getInstance().format(store.getPrice()) + "/" + store.getUnit());
            }
            // Load image
            if (activity != null) {
                GlideApp.with(activity)
                        .load(store.getProductImageUrl())
                        .into(binding.productImage);
            }

        }
        // Click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                  View SharedView=  binding.productImage;

                    listener.onStoreSelected(snapshot,SharedView);
                }
            }
        });

    }
}