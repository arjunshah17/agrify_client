package com.example.agrify.activity.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.Filters;
import com.example.agrify.activity.adapter.StoreAdapter;
import com.example.agrify.activity.model.Store;
import com.example.agrify.databinding.FragmentStoreBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {
    FragmentStoreBinding bind;
    private FirebaseFirestore db;
    int LIMIT = 50;
    LinearLayoutManager linearLayoutManager;
    private Query mQuery;
    filterDialogFragment dialogFragment;
    private StoreAdapter adapter;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_store,container,false);
        dialogFragment = new filterDialogFragment();

        init();
        getProductList();
        bind.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = db.collection("store")
                        .orderBy("name", Query.Direction.ASCENDING)
                        .limit(3);
                FirestoreRecyclerOptions<Store> options = new FirestoreRecyclerOptions.Builder<Store>()
                        .setQuery(mQuery, Store.class)
                        .build();
                ;
                setNewOption(options);

                Toast.makeText(getActivity(), "hello", Toast.LENGTH_LONG).show();


            }
        });

        return bind.getRoot();
    }

    private void getProductList() {
        mQuery = db.collection("store")
                .orderBy("name", Query.Direction.DESCENDING)
                .limit(LIMIT);
        FirestoreRecyclerOptions<Store> storeResponse = new FirestoreRecyclerOptions.Builder<Store>()
                .setQuery(mQuery, Store.class)
                .build();

        adapter = new StoreAdapter(storeResponse, getActivity());


        adapter.notifyDataSetChanged();
        bind.storeRecyclerView.setAdapter(adapter);
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(getActivity());

        bind.storeRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();


    }

    void setNewOption(final FirestoreRecyclerOptions<Store> options) {
        if (adapter != null) {
            adapter.stopListening();
        }

        adapter.notifyDataSetChanged();
        bind.storeRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    interface FilterListener {

        void onFilter(Filters filters);

    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }




}