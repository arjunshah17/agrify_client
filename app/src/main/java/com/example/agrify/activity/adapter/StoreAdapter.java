package com.example.agrify.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;

import com.example.agrify.activity.model.Store;
import com.example.agrify.activity.viewHolder.StoreHolder;
import com.example.agrify.databinding.ItemStoreProductBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class StoreAdapter extends FirestoreAdapter<StoreHolder> {
    Activity activity;
    String TAG;
    public interface OnStoreSelectedListener {

        void onStoreSelected(DocumentSnapshot store,View SharedView);

    }
    private OnStoreSelectedListener mListener;

    public StoreAdapter(Query query ,OnStoreSelectedListener listener,Activity activity,String TAG) {
        super(query);
       mListener=listener;
        this.activity=activity;
        this.TAG=TAG;
    }
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemStoreProductBinding itemStoreProductBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_store_product, parent, false);


        return new StoreHolder(itemStoreProductBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener,activity,TAG);
    }
}
