package com.example.agrify.activity.sellerProduct.viewHolder;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.sellerProduct.CartActivity;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.CartItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

import javax.annotation.Nullable;

import it.sephiroth.android.library.numberpicker.NumberPicker;

import static java.text.NumberFormat.getInstance;

public class cartHolder extends RecyclerView.ViewHolder {
    CartItemBinding itemCartBinding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    Float price;


    public cartHolder(CartItemBinding itemCartBinding) {
        super(itemCartBinding.getRoot());
       this.itemCartBinding=itemCartBinding;
       firebaseFirestore=FirebaseFirestore.getInstance();
       auth=FirebaseAuth.getInstance();

    }
    Cart cart;
    public void bind(DocumentSnapshot snapshot, Activity activity, CartAdapter.OnOutOfStockListener listener) {
        cart = new Cart();
        cart = snapshot.toObject(Cart.class);

        firebaseFirestore.document(cart.getSellerProductRef().getPath()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


                itemCartBinding.quantityNumberpicker.setMinValue(Objects.requireNonNull(cart).getMinQuantity());
                itemCartBinding.quantityNumberpicker.setMaxValue(cart.getMaxQuantity());
                float total_product_price = cart.getPrice() * cart.getQuantity();
                itemCartBinding.productPrice.setText("₹" + getInstance().format(total_product_price));

                itemCartBinding.productName.setText(cart.getName());

                itemCartBinding.quantityNumberpicker.setValue(cart.getQuantity());
//        itemCartBinding.textQuantity.setText("quantity of "+cart.getName()+"/"+cart.getUnit());
                if (activity != null) {
                    try {


                        GlideApp.with(activity)
                                .load(cart.getProductImageUrl())
                                .into(itemCartBinding.productImage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                int stock = 0;
                boolean avalibity=false;
                try {


                    stock = documentSnapshot.getDouble("stock").intValue();
                    if (cart.getQuantity() <= stock) {
                        itemCartBinding.outOfStock.setVisibility(View.GONE);
                        listener.onOutofStock(false, cart.getProductId());
                    } else {
                        itemCartBinding.outOfStock.setVisibility(View.VISIBLE);
                        listener.onOutofStock(true, cart.getProductId());
                    }
                    avalibity=documentSnapshot.getBoolean("avalibity");
                    if(avalibity)
                    {
                        itemCartBinding.avalibityTextview.setVisibility(View.GONE);
                        listener.onChangeAvalibity(false, cart.getProductId());
                    }
                    else
                    {
                        itemCartBinding.avalibityTextview.setVisibility(View.VISIBLE);
                        listener.onChangeAvalibity(true, cart.getProductId());
                    }

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }


                Cart finalCart = cart;


                itemCartBinding.quantityNumberpicker.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                        if (activity.getClass().getName().equals(CartActivity.class.getName())) {
                            firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document(finalCart.getProductId()).update("quantity", newVal);
                        } else {
                            firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").document(finalCart.getProductId()).update("quantity", newVal);
                        }
                    }
                });

            }
        });
    }

}
