package com.example.agrify.activity.sellerProduct.viewHolder;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.CartItemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

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
    public void bind(DocumentSnapshot snapshot, Activity activity) {
         cart=new Cart();
        cart=snapshot.toObject(Cart.class);



        itemCartBinding.quantityNumberpicker.setMinValue(cart.getMinQuantity());
        itemCartBinding.quantityNumberpicker.setMaxValue(cart.getMaxQuantity());
        float total_product_price=price*cart.getQuantity();
        itemCartBinding.productPrice.setText("₹"+NumberFormat.getInstance().format(total_product_price));

        itemCartBinding.productName.setText(cart.getName());
        itemCartBinding.quantityNumberpicker.setProgress(cart.getQuantity());
        itemCartBinding.textQuantity.setText("quantity of "+cart.getName()+"/"+cart.getUnit());
        if (activity != null) {
            GlideApp.with(activity)
                    .load(cart.getProductImageUrl())
                    .into(itemCartBinding.productImage);
        }

        Cart finalCart = cart;
        itemCartBinding.quantityNumberpicker.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document(finalCart.getProductId()).update("Quantity",numberPicker.getProgress());
//                float total_product_price=price*numberPicker.getProgress();
//                itemCartBinding.productPrice.setText("₹"+NumberFormat.getInstance().format(total_product_price));
            }

            @Override
            public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) {

            }
        });




    }
}
