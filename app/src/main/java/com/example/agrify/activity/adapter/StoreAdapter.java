package com.example.agrify.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.model.Store;
import com.example.agrify.activity.viewHolder.StoreHolder;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class StoreAdapter extends FirestoreRecyclerAdapter<Store, StoreHolder> {
    Activity activity;
    List<Store> itemList;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StoreAdapter(@NonNull FirestoreRecyclerOptions<Store> options, Activity activity) {

        super(options);
        this.activity = activity;


    }


    @Override
    protected void onBindViewHolder(@NonNull final StoreHolder storeHolder, int i, @NonNull Store store) {
        storeHolder.binding.setStore(store);
        GlideApp.with(activity)
                .load(store.getProductImageUrl())
                .into(storeHolder.binding.productImage);
        storeHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "clicked", Toast.LENGTH_SHORT).show();


            }
        });
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoreProductBinding itemStoreProductBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_store_product, parent, false);


        return new StoreHolder(itemStoreProductBinding);

    }

}
