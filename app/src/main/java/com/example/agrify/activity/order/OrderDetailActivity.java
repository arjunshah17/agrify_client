package com.example.agrify.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.order.adapter.OrderAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.ActivityOrderDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.annotation.Nullable;

public class OrderDetailActivity extends AppCompatActivity implements CartAdapter.OnOutOfStockListener {
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

    String orderId;
    CartAdapter cartAdapter;
    ActivityOrderDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        binding.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getStringExtra("orderId") != null) {
            orderId = getIntent().getStringExtra("orderId");
        }
        InitUi();
        initCartRecycleView();


    }

    private void initCartRecycleView() {
        query = firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).collection("orderList");
        cartAdapter = new CartAdapter(query, this, this) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);


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


        binding.orderRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.orderRecycleView.setAdapter(cartAdapter);
    }

    private void InitUi() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy");
        firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    {

                        Order order = snapshot.toObject(Order.class);
                        binding.orderId.setText(order.getOrderId());
                        binding.orderDate.setText(simpleDateFormat.format(order.getTimestamp().toDate()));
                        binding.orderStatus.setText(order.getOrderStatus());
                        binding.addressNameTv.setText(order.getUserAddressname());
                        binding.addressLocation.setText(order.getUserHouseNum() + order.getUserLocation());
                        binding.totalAmount.setText(NumberFormat.getInstance().format(order.getTotalAmount()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    private void noProductFound(boolean b) {
    }

    @Override
    public void onOutofStock(boolean status, String product_id) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cartAdapter != null) {
            cartAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cartAdapter != null) {
            cartAdapter.startListening();
        }
    }
}
