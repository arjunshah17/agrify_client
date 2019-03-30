package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrify.R;
import com.example.agrify.activity.StoreDetailActivity;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.databinding.FragmentWishBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import spencerstudios.com.bungeelib.Bungee;

/**
 * A simple {@link Fragment} subclass.
 */

public class WishFragment extends Fragment implements StoreAdapter.OnStoreSelectedListener {
    private static final String TAG = "WishFragment";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FragmentWishBinding bind;
    private StoreAdapter mAdapter;

    public WishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_wish, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        initFirestore();
        initRecyclerView();

        return bind.getRoot();
    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("wishlist").document(firebaseAuth.getCurrentUser().getUid()).collection("wishlist");

    }

    private void initRecyclerView() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new StoreAdapter(mQuery, this, getActivity(), TAG) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);
                if (getItemCount() == 0) {
                    noProductFound(true);
                    productLoadingState(false);
                } else {
                    noProductFound(false);
                    productLoadingState(false);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        bind.storeRecycleView.setHasFixedSize(true);
        bind.storeRecycleView.setLayoutManager(gridLayoutManager);
        bind.storeRecycleView.setAdapter(mAdapter);
    }

    void noProductFound(boolean state) {
        if (state) {
            bind.storeRecycleView.setVisibility(View.GONE);
            bind.animationView.playAnimation();
            bind.animationLayout.setVisibility(View.VISIBLE);
        } else {
            bind.animationLayout.setVisibility(View.GONE);
            bind.storeRecycleView.setVisibility(View.VISIBLE);
            bind.animationView.cancelAnimation();
        }
    }

    @Override
    public void onStoreSelected(DocumentSnapshot store, View SharedView) {
        Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
        intent.putExtra(StoreDetailActivity.KEY_STORE_ID, store.getId());
        String transitionName = getString(R.string.store_product_transition);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), SharedView, transitionName);
        startActivity(intent, transitionActivityOptions.toBundle());
        Bungee.inAndOut(getActivity());
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

    void productLoadingState(boolean state) {
        if (state) {
            bind.storeRecycleView.setVisibility(View.GONE);
            bind.shimmerRecyclerView.showShimmerAdapter();
        } else {
            bind.storeRecycleView.setVisibility(View.VISIBLE);
            bind.shimmerRecyclerView.hideShimmerAdapter();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}

