package com.example.agrify.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.activity.order.adapter.OrderAdapter;
import com.example.agrify.activity.order.adapter.OrderItemAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.order.model.OrderItem;
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
import java.util.Arrays;

import javax.annotation.Nullable;

import io.opencensus.tags.Tag;

public class OrderDetailActivity extends AppCompatActivity implements OrderItemAdapter.OnClickRateListener {
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

    String orderId;
    OrderItemAdapter orderItemAdapter;
    ActivityOrderDetailBinding binding;

    Order order;

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
        binding.downloadInvoice.setOnClickListener(v -> {
            DownloadInvoice();
        });


    }

    private void initCartRecycleView() {
        query = firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).collection("orderList");
        orderItemAdapter = new OrderItemAdapter(query, this, this) {
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
        binding.orderRecycleView.setAdapter(orderItemAdapter);
    }

    private void DownloadInvoice() {
        //TODO intilize order object with invoice
        //like this order.getOrderId();
        for (int i = 0; i < orderItemAdapter.getItemCount(); i++) {
            OrderItem orderItem = orderItemAdapter.getOrderItem(i);//object for product information
            //TODO intilize products with orderItem in for loop

        }
    }

    private void InitUi() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy");
        firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    {

                        order = snapshot.toObject(Order.class);
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
    protected void onStop() {
        super.onStop();
        if (orderItemAdapter != null) {
            orderItemAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (orderItemAdapter != null) {
            orderItemAdapter.startListening();
        }
    }

    @Override
    public void onClikedRating(DocumentSnapshot snapshot, OrderItem orderItem) {
        RatingDialogFragment fragment = new RatingDialogFragment(snapshot, orderItem);
        fragment.show(getSupportFragmentManager(), "OrderDetailActivity");

    }
}
