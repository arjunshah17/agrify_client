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

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        batch=firebaseFirestore.batch();
    }



    public void bind(final DocumentSnapshot snapshot,
                     final StoreAdapter.OnStoreSelectedListener listener, final Activity activity,String TAG)
    {

        store=new Store();

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


if(TAG.equals("StoreFragment")) {
     store = snapshot.toObject(Store.class);
    binding.setStore(store);


    // Load image
    if (activity != null) {
        GlideApp.with(activity)
                .load(store.getProductImageUrl())
                .into(binding.productImage);
    }
}
else if(TAG.equals("wishList"))  {

    DocumentReference documentReference= firebaseFirestore.collection("store").document(snapshot.getId());
           documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   store = documentSnapshot.toObject(Store.class);
                   try {
                       binding.setStore(store);


                       // Load image

                       if (store.getProductImageUrl() != null) {
                           if (activity != null) {
                               GlideApp.with(activity)
                                       .load(store.getProductImageUrl())
                                       .into(binding.productImage);

                           }
                       }
                   }
                   catch (NullPointerException e)
                   {
                       Log.e("nullPointer",e.getLocalizedMessage().toString());
                   }
               }
           });
documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
    @Override                //updates on change
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("event", "Listen failed.", e);
            return;
        }

        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                ? "Local" : "Server";

        if (snapshot != null && snapshot.exists()) {

            Log.d("event", source + " data: " + snapshot.getData());
            store=  documentSnapshot.toObject(Store.class);
            binding.setStore(store);




            // Load image
            try {
                if (store.getProductImageUrl() != null) {
                    if (activity != null) {
                        GlideApp.with(activity)
                                .load(store.getProductImageUrl())
                                .into(binding.productImage);
                    }
                }
            }
            catch (NullPointerException exx)
            {


        }


}
    }
});

}
else if(TAG.equals("SellerProductActivity"))
{
   firebaseFirestore.collection("store").document(snapshot.getString("productId")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
       @Override
       public void onSuccess(DocumentSnapshot snapshot) {
           store=snapshot.toObject(Store.class);
           binding.setStore(store);


           // Load image
           if (activity != null) {
               GlideApp.with(activity)
                       .load(store.getProductImageUrl())
                       .into(binding.productImage);
           }

       }
   });



}
        Resources resources = itemView.getResources();



        binding.wiseButton.setEventListener(new SparkEventListener() {


            @Override
            public void onEvent(ImageView button, boolean buttonState) {

                final Map<String,Boolean> status=new HashMap<>();
                status.put("status",true);
                final Map<String,String> user_wish_info=new HashMap<>();
                user_wish_info.put("product_id",snapshot.getId());
                product_wish=  firebaseFirestore.collection("store").document(snapshot.getId()).collection("wishlist").document(auth.getCurrentUser().getUid());
                user_product_wish=firebaseFirestore.collection("wishlist").document(auth.getCurrentUser().getUid()).collection("wishlist").document(snapshot.getId());
                if (buttonState) {

                    firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                        @androidx.annotation.Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            transaction.set(product_wish,status);
                            transaction.set(user_product_wish,user_wish_info);
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.info(activity, store.getName() + " added in wish list", Toasty.LENGTH_SHORT).show();

                            binding.wiseButton.setChecked(true);
                        }
                    });
                } else {


                       firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                           @androidx.annotation.Nullable
                           @Override
                           public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                               transaction.delete(product_wish);
                               transaction.delete(user_product_wish);
                               return null;
                           }
                       }).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toasty.info(activity, store.getName() + " removed from wish list", Toasty.LENGTH_SHORT).show();

                               binding.wiseButton.setChecked(false);
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