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
import com.shawnlin.numberpicker.NumberPicker;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

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


itemCartBinding.quantityNumberpicker.setDisplayFocusable(true);
        itemCartBinding.quantityNumberpicker.setMin(Objects.requireNonNull(cart).getMinQuantity());
        itemCartBinding.quantityNumberpicker.setMax(cart.getMaxQuantity());
        float total_product_price=cart.getPrice()*cart.getQuantity();
        itemCartBinding.productPrice.setText("₹"+NumberFormat.getInstance().format(total_product_price));

        itemCartBinding.productName.setText(cart.getName());
        itemCartBinding.quantityNumberpicker.setValue(cart.getQuantity());
//        itemCartBinding.textQuantity.setText("quantity of "+cart.getName()+"/"+cart.getUnit());
        if (activity != null) {
            GlideApp.with(activity)
                    .load(cart.getProductImageUrl())
                    .into(itemCartBinding.productImage);
        }

        Cart finalCart = cart;

itemCartBinding.quantityNumberpicker.setValueChangedListener(new ValueChangedListener() {
    @Override
    public void valueChanged(int value, ActionEnum action) {
        firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document(finalCart.getProductId()).update("Quantity",value);
    }
});




    }
}
