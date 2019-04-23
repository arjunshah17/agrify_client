package com.example.agrify.activity.sellerProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.Utils.RatingUtils;
import com.example.agrify.activity.order.adapter.RatingAdapter;
import com.example.agrify.activity.order.model.Rating;
import com.example.agrify.activity.sellerProduct.adpater.SellerStoreAdpater;
import com.example.agrify.databinding.ActivitySellerProductStoreBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerProductStoreActivity extends AppCompatActivity implements SellerStoreAdpater.OnProductSelectedListener {
    ActivitySellerProductStoreBinding binding;
    Query query;
    FirebaseFirestore firebaseFirestore;
    SellerStoreAdpater adpater;
    String sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_product_store);
        if (getIntent().getStringExtra("sellerId") != null) {
            sellerId = getIntent().getStringExtra("sellerId");

        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        initRecycleView();


    }

    private void initRecycleView() {
        query = firebaseFirestore.collection("Sellers").document(sellerId).collection("productList");
        adpater = new SellerStoreAdpater(query, this, this) {

            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);
                productLoadingState(false);


            }
        };
        binding.productList.setLayoutManager(new LinearLayoutManager(this));
        binding.productList.setAdapter(adpater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (query != null) {
            adpater.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (query != null) {
            adpater.stopListening();
        }
    }

    @Override
    public void onProductSelected(String productId, String SellerId) {
        Intent intent = new Intent(getApplicationContext(), SellerProductActivity.class);

        intent.putExtra("seller_id", SellerId);
        intent.putExtra("product_id", productId);

        startActivity(intent);


    }

    private void productLoadingState(boolean state) {
        if (state) {
            binding.productList.setVisibility(View.INVISIBLE);
            binding.shimmerRecyclerView.showShimmerAdapter();
            binding.shimmerRecyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.productList.setVisibility(View.VISIBLE);
            binding.shimmerRecyclerView.hideShimmerAdapter();
            binding.shimmerRecyclerView.setVisibility(View.GONE);
        }
    }
}
