package com.example.agrify.activity.address;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;


import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.databinding.AddressItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class AddressAdapter extends FirestoreAdapter<AddressViewHolder> {
   public interface OnAddressSelectedListener{
        void OnAddressSelected(DocumentSnapshot snapshot);
    }
    Activity activity;
    String TAG;
    public OnAddressSelectedListener onAddressSelectedListener;
    public AddressAdapter(Query query, Activity activity, String TAG, OnAddressSelectedListener listener) {
        super(query);
        this.activity=activity;
        this.TAG=TAG;
        onAddressSelectedListener=listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddressItemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.address_item, parent, false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
     holder.bind(getSnapshot(position),activity,TAG,onAddressSelectedListener);
    }
}
