package com.example.agrify.activity.viewHolder;


import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.Utils.RatingUtils;
import com.example.agrify.activity.adapter.SellerAdapter;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.order.model.Rating;
import com.example.agrify.databinding.ItemSellerBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SellerHolder extends RecyclerView.ViewHolder {
    public FirebaseFirestore db;
    public FirebaseAuth auth;
    Seller seller;
    ItemSellerBinding binding;
    private String phoneNumber;
    public SellerHolder(@NonNull ItemSellerBinding item) {
        super(item.getRoot());
        binding = item;
        seller = new Seller();
    }
    public void bind(final DocumentSnapshot snapshot, final Activity activity, final SellerAdapter.OnSellerSelectedListener listener) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        seller = snapshot.toObject(Seller.class);
        binding.setSeller(seller);
        binding.price.setText("₹" + NumberFormat.getInstance().format(seller.getPrice()));
        phoneNumber = seller.getPhone();
        // Load image
        if (activity != null) {
            GlideApp.with(activity)
                    .load(seller.getProfilePhotoUrl())
                    .into(binding.profilePhoto);
        }

        db.document(seller.getSellerProductRef().getPath()).collection("ratingList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    try {
                        ArrayList<Rating> ratings = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            ratings.add(documentSnapshot.toObject(Rating.class));
                        }
                        binding.Rating.setRating((float) RatingUtils.getAverageRating(ratings));
                        binding.NumRatings.setText("(" + ratings.size() + ")");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        Resources resources = itemView.getResources();


//        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + phoneNumber));
//                v.getContext().startActivity(intent);
//            }
//        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    View SharedView = binding.userName;

                    listener.onSellerSelected(snapshot, SharedView);
                }
            }
        });


    }


}
