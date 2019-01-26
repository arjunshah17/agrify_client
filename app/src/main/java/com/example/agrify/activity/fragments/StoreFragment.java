package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.Filters;
import com.example.agrify.activity.StoreDetailActivity;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.activity.model.Store;

import com.example.agrify.databinding.FragmentStoreBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements StoreAdapter.OnStoreSelectedListener {
    FragmentStoreBinding bind;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;
    private StoreAdapter mAdapter;



    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_store,container,false);

            getActivity().setActionBar(bind.appBar);

        initFirestore();
        initRecyclerView();



        return bind.getRoot();
    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("store").orderBy("name", Query.Direction.ASCENDING);
    }
    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new StoreAdapter(mQuery, this,getActivity())
        {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.

            }


        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        bind.storeRecycleView.setHasFixedSize(true);
        bind.storeRecycleView.setLayoutManager(gridLayoutManager);

       bind.storeRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void onStoreSelected(DocumentSnapshot store) {
        Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
        intent.putExtra(StoreDetailActivity.KEY_STORE_ID, store.getId());

        startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }
}