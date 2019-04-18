package com.example.agrify.activity.sellerProduct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DownloadManager;
import android.media.MediaDrm;
import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;

import javax.annotation.Nullable;

public class CartActivity extends AppCompatActivity {
ActivityCartBinding bind;
CartAdapter cartAdapter;
Query query;
FirebaseFirestore firebaseFirestore;
FirebaseAuth auth;
CollectionReference Total_amount_ref;
    private ListenerRegistration cartItemListener;
float total_price=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= DataBindingUtil.setContentView(this,R.layout.activity_cart);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        initCartRecycleView();
        Total_amount_ref=firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
        Total_amount_ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                  QuerySnapshot snapshot= task.getResult();
                  getTotalAmount(snapshot);

                }
            }
        });
         Total_amount_ref.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                total_price=0;
                getTotalAmount(queryDocumentSnapshots);
            }
        });


    }


    private void initCartRecycleView() {
        query=firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
        cartAdapter=new CartAdapter(query,this){
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);
                productLoadingState(false);

                if (getItemCount() == 0) {
                    noProductFound(true);
                } else {
                    noProductFound(false);
                }


            }

            @Override
            protected void onDocumentModified(DocumentChange change) {
                super.onDocumentModified(change);
            }
        };


        bind.cartRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bind.cartRecycleView.setAdapter(cartAdapter);
    }
    void getTotalAmount(QuerySnapshot queryDocumentSnapshots)
    {


        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
        {
            Cart cart=doc.toObject(Cart.class);






                    total_price=total_price+cart.getPrice()*cart.getQuantity();




        }

        bind.totalPrice.setText("₹"+ NumberFormat.getInstance().format(total_price));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(cartAdapter!=null)
        {
            cartAdapter.startListening();
            productLoadingState(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(cartAdapter!=null)
        {
            cartAdapter.stopListening();
        }
    }

    private void productLoadingState(boolean state) {

        if(state)
        {
            //TODO add code for turning on shrimmer
        }
        else
        {
            //TODO add code for turning off shrimmer
        }

    }


    void noProductFound(boolean state) {
        if (state) {
            bind.mainLayout.setVisibility(View.GONE);


            bind.animationView.playAnimation();
            bind.animationLayout.setVisibility(View.VISIBLE);
        } else {
            bind.animationLayout.setVisibility(View.GONE);
            bind.mainLayout.setVisibility(View.VISIBLE);
            bind.animationView.cancelAnimation();
        }
    }
}
