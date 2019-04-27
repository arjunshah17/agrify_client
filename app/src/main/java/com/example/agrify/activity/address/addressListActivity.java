package com.example.agrify.activity.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.agrify.R;
import com.example.agrify.databinding.ActivityAddressListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class addressListActivity extends AppCompatActivity implements AddressAdapter.OnAddressSelectedListener {
    ActivityAddressListBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    Query query;
    static final String TAG="addressListActivity";
    AddressAdapter addressAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding= DataBindingUtil. setContentView(this, R.layout.activity_address_list);
     INIT();
     FireStoreInit();

    }

    private void FireStoreInit() {
        query = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("addressList");
        addressAdapter = new AddressAdapter(query, this,TAG,this) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);

                productLoadingState(false);
                if (getItemCount() == 0) {

                } else {

                }
            }

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    noProductFound(true);


                } else {
                    noProductFound(false);
                }

            }
        };
        binding.addressListRv.setLayoutManager(new LinearLayoutManager(this));
        binding.addressListRv.setAdapter(addressAdapter);


    }
    @Override
    public void onStop() {
        super.onStop();
        if (addressAdapter != null) {
            addressAdapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary

        // Start listening for Firestore updates
        if (addressAdapter!= null) {
            addressAdapter.startListening();
            productLoadingState(true);
        }
    }


    private void productLoadingState(boolean state) {
        if (state) {
            binding.addressListRv.setVisibility(View.GONE);
            binding.shimmerRecyclerView.showShimmerAdapter();

            binding.shimmerRecyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.addressListRv.setVisibility(View.VISIBLE);
            binding.shimmerRecyclerView.hideShimmerAdapter();
            binding.shimmerRecyclerView.setVisibility(View.GONE);
        }

    }


    private void INIT() {
        binding.addressButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),addressActivity.class));
        });
        binding.appBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        firebaseFirestore= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();


    }


    @Override
    public void OnAddressSelected(DocumentSnapshot snapshot) {

    }
    private void noProductFound(boolean state) {
        if (state) {
            binding.addressListRv.setVisibility(View.GONE);
          binding.shimmerRecyclerView.setVisibility(View.GONE);

            binding.animationView.playAnimation();
            binding.animationLayout.setVisibility(View.VISIBLE);
        } else {

            binding.animationLayout.setVisibility(View.GONE);
            binding.addressListRv.setVisibility(View.VISIBLE);
            binding.animationView.cancelAnimation();

        }
    }
}
