package com.example.agrify.activity.sellerProduct.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.adapter.FirestoreAdapter;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.activity.sellerProduct.viewHolder.cartHolder;
import com.example.agrify.databinding.CartItemBinding;
import com.google.firebase.firestore.Query;

public class CartAdapter extends FirestoreAdapter<cartHolder> {
    Activity activity;
    public interface OnOutOfStockListener {

        void onOutofStock(boolean status, String product_id);

    }
    private OnOutOfStockListener Listener;
    public CartAdapter(Query query, Activity activity, OnOutOfStockListener Listener) {
        super(query);
        this.activity=activity;
        this.Listener=Listener;
    }

    @NonNull
    @Override
    public cartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CartItemBinding itemCartBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_item, parent, false);


        return new cartHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull cartHolder holder, int position) {
        holder.bind(getSnapshot(position),activity,Listener);

    }
   public Cart getCart(int pos)
   {
       return getSnapshot(pos).toObject(Cart.class);
   }
}
