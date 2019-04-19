package com.example.agrify.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.ActivityOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class orderActivity extends AppCompatActivity {
    CartAdapter cartAdapter;
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    ActivityOrderBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= DataBindingUtil.setContentView(this,R.layout.activity_order);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        initCartRecycleView();
        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    private void initCartRecycleView() {
        query=firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
        cartAdapter=new CartAdapter(query,this);


        bind.orderRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bind.orderRecycleView.setAdapter(cartAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(cartAdapter!=null) {
            cartAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(cartAdapter!=null) {
            cartAdapter.startListening();
        }
    }
}
