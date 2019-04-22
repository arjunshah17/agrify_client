package com.example.agrify.activity.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrify.R;
import com.example.agrify.activity.order.adapter.OrderAdapter;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.databinding.FragmentOrderListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    OrderAdapter orderAdapter;
    FragmentOrderListBinding bind;


    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_order_list, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        initCartRecycleView();
        return bind.getRoot();
    }

    private void initCartRecycleView() {
        query = firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").orderBy("timestamp", Query.Direction.DESCENDING);
        orderAdapter = new OrderAdapter(query, getActivity(), this) {
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


        bind.orderRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bind.orderRecycleView.setAdapter(orderAdapter);
    }

    private void noProductFound(boolean b) {
    }

    private void productLoadingState(boolean b) {
    }


    @Override
    public void onOrderItemClicked(DocumentSnapshot snapshot) {
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        intent.putExtra("orderId", snapshot.getId());
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (orderAdapter != null) {
            orderAdapter.startListening();
            productLoadingState(true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (orderAdapter != null) {
            orderAdapter.stopListening();
        }
    }
}
