package com.example.agrify.activity.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agrify.R;
import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.model.Store;
import com.example.agrify.databinding.FragmentStoreBinding;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class storeFragment extends Fragment {
    FragmentStoreBinding bind;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Query mQuery;
    private static final int LIMIT = 5;
    public storeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bind= DataBindingUtil.inflate(inflater,R.layout.fragment_store,container,false);

        init();
        getProductList();
          bind.storeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
              @Override
              public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                  super.onScrolled(recyclerView, dx, dy);
                  if(!recyclerView.canScrollVertically(1))
                  {
                      Toast.makeText(getActivity(),"reached buttom",Toast.LENGTH_SHORT).show();
                     // getProductList();
                      
                  }
              }
          });



        return bind.getRoot();
    }
    private void init() {
        linearLayoutManager = new LinearLayoutManager(getActivity());

        bind.storeRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();


    }

    private void getProductList() {
        mQuery = db.collection("store")
                .orderBy("name", Query.Direction.DESCENDING)
                .limit(LIMIT);
        FirestoreRecyclerOptions<Store> storeResponse = new FirestoreRecyclerOptions.Builder<Store>()
                .setQuery(mQuery, Store.class)
                .build();



        adapter = new FirestoreRecyclerAdapter<Store, StoreHolder>(storeResponse) {


            @NonNull
            @Override
            public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                ItemStoreProductBinding itemStoreProductBinding=DataBindingUtil.
                        inflate(LayoutInflater.from(parent.getContext()),R.layout.item_store_product,parent,false);


                return new StoreHolder(itemStoreProductBinding);
            }

            @Override
            protected void onBindViewHolder(@NonNull StoreHolder storeHolder, int i, @NonNull Store store) {
                     storeHolder.binding.setStore(store);
                GlideApp.with(getActivity())
                        .load(store.getProductImageUrl())
                        .into(storeHolder.binding.productImage);
                storeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     Toast.makeText(getActivity(),"clicked",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };




        adapter.notifyDataSetChanged();
        bind.storeRecyclerView.setAdapter(adapter);
    }


    public class StoreHolder extends RecyclerView.ViewHolder
    {
        private final ItemStoreProductBinding binding;
        public StoreHolder(@NonNull ItemStoreProductBinding item) {
            super(item.getRoot());
            binding=item;
        }
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
