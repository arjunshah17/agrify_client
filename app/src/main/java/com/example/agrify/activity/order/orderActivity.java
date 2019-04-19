package com.example.agrify.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.address.AddressAdapter;
import com.example.agrify.activity.address.AddressListFragment;
import com.example.agrify.activity.address.model.Address;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.ActivityOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class orderActivity extends AppCompatActivity implements AddressAdapter.OnAddressSelectedListener {
    CartAdapter cartAdapter;
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    ActivityOrderBinding bind;
    AddressListFragment addressListFragment;
    Address orderAddress;
    final String TAG="orderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= DataBindingUtil.setContentView(this,R.layout.activity_order);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        initCartRecycleView();
        addressListFragment=new AddressListFragment(this::OnAddressSelected);
        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bind.orderAddressButton.setOnClickListener(v -> {

            addressListFragment.show(getSupportFragmentManager(),TAG);
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
//
    @Override
    public void OnAddressSelected(DocumentSnapshot snapshot) {
        addressListFragment.dismiss();
        orderAddress=snapshot.toObject(Address.class);
        bind.addressNameTv.setText(orderAddress.getName());
        bind.addressLocation.setText(orderAddress.getHouseNum()+orderAddress.getLocation());

    }
}
