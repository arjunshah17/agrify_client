package com.example.agrify.activity.order;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.agrify.R;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.activity.order.model.Rating;
import com.example.agrify.databinding.RatingDialogBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import es.dmoral.toasty.Toasty;

public class RatingDialogFragment extends DialogFragment {
    RatingDialogBinding binding;
    DocumentSnapshot snapshot;
    OrderItem orderItem;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    WriteBatch batch;

    public RatingDialogFragment(DocumentSnapshot snapshot, OrderItem orderItem) {
        this.snapshot = snapshot;
        this.orderItem = orderItem;
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        batch = firebaseFirestore.batch();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.rating_dialog, container, false);
        WriteBatch batch = firebaseFirestore.batch();
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRating();
            }
        });
        binding.buttonCancel.setOnClickListener(v -> {
            dismiss();
        });
        return binding.getRoot();

    }

    private void uploadRating() {
        DocumentReference ratingRef = firebaseFirestore.document(orderItem.getSellerProductRef().getPath()).collection("ratingList").document();
        if (binding.ratingBar.getRating() == 0) {
            Toasty.error(getContext(), "enter rating", Toasty.LENGTH_SHORT).show();
        } else {
            Rating rating = new Rating();
            rating.setRating(binding.ratingBar.getRating());
            rating.setReviewText(binding.reviewText.getText().toString());
            rating.setUserId(auth.getUid());
            batch.set(ratingRef, rating);
            batch.update(ratingRef, "timestamp", FieldValue.serverTimestamp());
            batch.update(snapshot.getReference(), "rating", binding.ratingBar.getRating());

            batch.update(snapshot.getReference(), "reviewed", Boolean.valueOf("true"));
            batch.update(orderItem.getSellerProductRef(), "ratingCount", FieldValue.increment(1));
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toasty.success(getContext(), "successfully updated rating", Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(getContext(), task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });

        }

    }
}
