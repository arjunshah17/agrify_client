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
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.HashMap;
import java.util.Map;

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
store=new Store();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        batch=firebaseFirestore.batch();
    }



    public void bind(final DocumentSnapshot snapshot,
                     final StoreAdapter.OnStoreSelectedListener listener, final Activity activity,String TAG) {

if(TAG.equals("StoreFragment")) {
     store = snapshot.toObject(Store.class);

}
else {

    firebaseFirestore.collection("store").document(snapshot.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            store=documentSnapshot.toObject(Store.class);
            store.setProductImageUrl(documentSnapshot.getString("ProductImageUrl"));
            store.setName("name");
            store.setDes("des");
            store.setDes("category");
        }
    });

}
        Resources resources = itemView.getResources();

        binding.setStore(store);

        // Load image
        if (activity != null) {
            GlideApp.with(activity)
                .load(store.getProductImageUrl())
                   .into(binding.productImage);
        }
        firebaseFirestore.collection("store").document(snapshot.getId()).collection("wishlist").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        binding.wiseButton.setChecked(true);
                    }
                }
            }
        });
        binding.wiseButton.setEventListener(new SparkEventListener() {


            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                Map<String,Boolean> status=new HashMap<>();
                status.put("status",true);
                Map<String,String> user_wish_info=new HashMap<>();
                user_wish_info.put("product_id",snapshot.getId());
                product_wish=  firebaseFirestore.collection("store").document(snapshot.getId()).collection("wishlist").document(auth.getCurrentUser().getUid());
                user_product_wish=firebaseFirestore.collection("wishlist").document(auth.getCurrentUser().getUid()).collection("wishlist").document(snapshot.getId());
                if (buttonState) {

                  batch.set(product_wish,status);

                 batch.set(user_product_wish,user_wish_info);
                 batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         Toasty.info(activity, store.getName() + " added in wish list", Toasty.LENGTH_SHORT).show();
                     }
                 });
                } else {

                          batch.delete(product_wish);
                          batch.delete(user_product_wish);


                           batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toasty.info(activity, store.getName() + " removed from wishlist", Toasty.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toasty.error(activity,task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
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