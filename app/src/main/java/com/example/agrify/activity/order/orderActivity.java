package com.example.agrify.activity.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.activity.Utils.SwipeToDeleteCallBack;
import com.example.agrify.activity.Utils.cartUtils;
import com.example.agrify.activity.address.AddressAdapter;
import com.example.agrify.activity.address.AddressListFragment;
import com.example.agrify.activity.address.model.Address;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.model.User;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.activity.sellerProduct.CartActivity;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.ActivityOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.type.Date;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

public class orderActivity extends AppCompatActivity implements AddressAdapter.OnAddressSelectedListener, CartAdapter.OnOutOfStockListener, PaymentMethodSelectionFragment.RadioClickedListener {
    CartAdapter cartAdapter;
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    ActivityOrderBinding bind;
    AddressListFragment addressListFragment;
    Address orderAddress;
    final String TAG="orderActivity";
    float total_price=0;
    boolean isOutOfStock;
    boolean isCartActivity = false;
    boolean isAddressSelected=false;
    boolean isPaymentSelected = false;
    boolean isGooglePay = true;
    CollectionReference Total_amount_ref;
    PaymentMethodSelectionFragment paymentMethodSelectionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind= DataBindingUtil.setContentView(this,R.layout.activity_order);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        if (getIntent().getStringExtra("activity") != null) {
            if (getIntent().getStringExtra("activity").equals(CartActivity.class.getName())) {
                isCartActivity = true;
            }
        }
        paymentMethodSelectionFragment = new PaymentMethodSelectionFragment(this::isGooglePay);

        Total_amount_ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart");
order=new Order();
seller =new Seller();
user=new User();
address=new Address();

        initCartRecycleView();
        enableSwipeToDelete();

        if (!isAddressSelected) {
            bind.addressLayout.setVisibility(View.GONE);
        }
        Total_amount_ref.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                total_price=0;
                getTotalAmount(queryDocumentSnapshots);
            }
        });
        addressListFragment=new AddressListFragment(this::OnAddressSelected);
        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bind.orderAddressButton.setOnClickListener(v -> {

            addressListFragment.show(getSupportFragmentManager(),TAG);
        });
        bind.placeOrderButton.setOnClickListener(v -> {

            if (cartAdapter.getItemCount() != 0) {

                if (isAddressSelected) {
                    if (isOutOfStock) {
                        Toasty.error(getApplicationContext(), "some of products are out of stock ,try to reduce quantity", Toasty.LENGTH_SHORT).show();
                    } else {

                        placeOrder();
                    }
                } else {
                    Toasty.error(getApplicationContext(), "no items in order", Toasty.LENGTH_SHORT).show();

                }
            } else {
                Toasty.info(getApplicationContext(), "select address ", Toasty.LENGTH_SHORT).show();
            }



        });
        bind.paymentMethodButton.setOnClickListener(v -> {
            paymentMethodSelectionFragment.show(getSupportFragmentManager(), "orderActivity");

        });
    }

    private void makeOnlinePayment() {


    }

    User user;
    Order order;
    Seller seller;
    Address address;

    private void placeOrder() {
loadingState(true);
        WriteBatch orderBatch=firebaseFirestore.batch();
        DocumentReference userRef=firebaseFirestore.collection("Users").document(auth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot UserShot) {
                      user =UserShot.toObject(User.class);
                      firebaseFirestore.collection("Sellers").document(user.getTempOrderSellerId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                          @Override
                          public void onSuccess(DocumentSnapshot Sellershot) {
                              seller =Sellershot.toObject(Seller.class);
                              order.setUserId(UserShot.getId());

                              order.setUserAddressname(orderAddress.getName());
                              order.setUserHouseNum(orderAddress.getHouseNum());
                              order.setUserLocation(orderAddress.getLocation());
                              order.setSellerId(Sellershot.getId());
                              order.setOrderStatus("pendding");
                              order.setTotalAmount(total_price);

                              order.setPaymentMode("cash on delivery");





                                       String orderId=UUID.randomUUID().toString();
                                       order.setOrderId(String.valueOf(Timestamp.now().getSeconds()));
                              order.setTimestamp(null);
                                       DocumentReference UserOrderRef=firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId);
                                       DocumentReference sellerOrderRef=firebaseFirestore.collection("Sellers").document(order.getSellerId()).collection("orderList").document(orderId);
                                       orderBatch.set(UserOrderRef,order);
                                       orderBatch.set(sellerOrderRef,order);
                              orderBatch.update(UserOrderRef, "timestamp", FieldValue.serverTimestamp());
                              orderBatch.update(sellerOrderRef, "timestamp", FieldValue.serverTimestamp());


                                       for(int i=0;i<cartAdapter.getItemCount();i++) {
                                           Cart cartItem = cartAdapter.getCart(i);
                                           OrderItem orderItem = new OrderItem();
                                           orderItem.setName(cartItem.getName());
                                           orderItem.setReviewed(false);
                                           orderItem.setPrice(cartItem.getPrice());
                                           orderItem.setProductId(cartItem.getProductId());
                                           orderItem.setSellerId(cartItem.getSellerId());
                                           orderItem.setSellerProductRef(cartItem.getSellerProductRef());
                                           orderItem.setProductImageUrl(cartItem.getProductImageUrl());
                                           orderItem.setUnit(cartItem.getUnit());
                                           orderItem.setProductImageUrl(cartItem.getProductImageUrl());
                                           orderItem.setQuantity(cartItem.getQuantity());
                                           DocumentReference userOrderItem=firebaseFirestore.collection("Users").document(auth.getUid()).collection("orderList").document(orderId).collection("orderList").document(cartItem.getProductId());
                                           DocumentReference sellerOrderItem=firebaseFirestore.collection("Sellers").document(order.getSellerId()).collection("orderList").document(orderId).collection("orderList").document(cartItem.getProductId());

                                           firebaseFirestore.runTransaction(new Transaction.Function<Integer>() {
                                               @androidx.annotation.Nullable
                                               @Override
                                               public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                   DocumentSnapshot doc = transaction.get(cartItem.getSellerProductRef());
                                                   int stock = doc.getDouble("stock").intValue();


                                                   stock = stock - cartItem.getQuantity();
                                                   cartItem.getSellerProductRef().update("stock", "stock", stock);


                                                   return stock;
                                               }

                                           });



                                           orderBatch.set(userOrderItem, orderItem);
                                           orderBatch.set(sellerOrderItem, orderItem);

                                       }


                              orderBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                           loadingState(true);
                                           if(task.isSuccessful())
                                           {
                                               cartUtils.clearAllCart(getApplicationContext());
                                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                           }
                                           else
                                           {
                                               Toasty.error(getApplicationContext(),task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
                                           }

                                           }
                                       });





                          }
                      });

            }
        });

    }

    private void loadingState(boolean state) {
        if(state)
        {
            bind.mainLayout.setVisibility(View.GONE);
            bind.animationLayout.setVisibility(View.VISIBLE);
            bind.animationViewText.playAnimation();
            bind.animationView.playAnimation();

        }
        else {
            bind.animationLayout.setVisibility(View.GONE);
            bind.mainLayout.setVisibility(View.VISIBLE);
            bind.animationView.cancelAnimation();
            bind.animationViewText.cancelAnimation();
        }
    }

    private void initCartRecycleView() {
        query=firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart");
        cartAdapter=new CartAdapter(query,this,this);
        bind.orderRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bind.orderRecycleView.setAdapter(cartAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(cartAdapter!=null) {
            cartAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(cartAdapter!=null) {
            cartAdapter.startListening();
        }
    }
//
    @Override
    public void OnAddressSelected(DocumentSnapshot snapshot) {
        addressListFragment.dismiss();
        orderAddress=snapshot.toObject(Address.class);
        bind.addressNameTv.setText(orderAddress.getName());
        bind.addressLocation.setText(orderAddress.getHouseNum()+orderAddress.getLocation());
        isAddressSelected=true;
        bind.addressLayout.setVisibility(View.VISIBLE);

    }

    void getTotalAmount(QuerySnapshot queryDocumentSnapshots)
    {


        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
        {
            Cart cart=doc.toObject(Cart.class);






            total_price=total_price+cart.getPrice()*cart.getQuantity();




        }

        bind.totalPrice.setText("₹"+ NumberFormat.getInstance().format(total_price));
    }

    ArrayList<String> productIdList=new ArrayList();
    @Override
    public void onOutofStock(boolean status, String product_id) {
        if(productIdList.contains(product_id)) {
            if(status) {
                productIdList.remove(product_id);
            }
        } else {
            if(!status) {
                productIdList.add(product_id);
            }

        }

        if(productIdList.size()==cartAdapter.getItemCount()) {
            isOutOfStock=false;
        } else {
            isOutOfStock=true;
        }

    }

    @Override
    public void isGooglePay(boolean mode) {
        paymentMethodSelectionFragment.dismiss();
        isPaymentSelected = true;
        if (mode) {
            isGooglePay = true;
            bind.paymentMethodIcon.setImageResource(R.drawable.googleg_standard_color_18);
            bind.paymentMethodText.setText("google pay");
        } else {
            isGooglePay = false;
            bind.paymentMethodIcon.setImageResource(R.drawable.ic_cash);
            bind.paymentMethodText.setText("cash on delivery");
        }

    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallBack swipeToDeleteCallback = new SwipeToDeleteCallBack(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();

                Cart cart = cartAdapter.getCart(position);
                cartUtils.deleteorderItem(cart.getProductId(), cart.getSellerId(), getApplicationContext());
                cartAdapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(bind.orderRecycleView);
    }

}





