package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.StoreDetailActivity;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.activity.listener.NavigationIconClickListener;
import com.example.agrify.databinding.FragmentStoreBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements StoreAdapter.OnStoreSelectedListener {
    private FragmentStoreBinding bind;
    public FirebaseFirestore mFirestore;
    private Query mQuery;
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;
    private StoreAdapter mAdapter;
    private NavigationIconClickListener navigationIconClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_store, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bind.productGrid.setBackground(getContext().getDrawable(R.drawable.shr_product_grid_background_shape));
        }
        setUpToolbar();
        initFirestore();
        initRecyclerView();
        return bind.getRoot();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("store").orderBy("name", Query.Direction.ASCENDING);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new StoreAdapter(mQuery, this, getActivity()) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        bind.storeRecycleView.setHasFixedSize(true);
        bind.storeRecycleView.setLayoutManager(gridLayoutManager);
        bind.storeRecycleView.setAdapter(mAdapter);

        //TODO call category listener here
        categoryButtonListener();
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


    private void setUpToolbar() {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(bind.appBar);
        }
        navigationIconClickListener = new NavigationIconClickListener(getContext(), bind.productGrid, new AccelerateDecelerateInterpolator(), getContext().getDrawable(R.drawable.shr_menu), getContext().getDrawable(R.drawable.shr_close_menu));

        bind.appBar.setNavigationOnClickListener(navigationIconClickListener);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    void categoryButtonListener() {

        bind.friutsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        // TODO add above code for all buttons
        bind.vegetablesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.seedsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.flowersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.grainsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.pulsesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.cerealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
        bind.dairyProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationIconClickListener.closeMenu();
            }
        });
    }
}