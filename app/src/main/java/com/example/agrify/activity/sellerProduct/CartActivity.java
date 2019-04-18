package com.example.agrify.activity.sellerProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DownloadManager;
import android.media.MediaDrm;
import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CartActivity extends AppCompatActivity {
ActivityCartBinding bind;
CartAdapter cartAdapter;
Query query;
FirebaseFirestore firebaseFirestore;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= DataBindingUtil.setContentView(this,R.layout.activity_cart);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        initCartRecycleView();

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
