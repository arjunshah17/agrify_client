package com.example.agrify.activity.sellerProduct;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.activity.Utils.SwipeToDeleteCallBack;
import com.example.agrify.activity.Utils.cartUtils;
import com.example.agrify.activity.order.orderActivity;
import com.example.agrify.activity.sellerProduct.adpater.CartAdapter;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnOutOfStockListener {
    ActivityCartBinding bind;
    CartAdapter cartAdapter;
    Query query;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    CollectionReference Total_amount_ref;
    boolean isOutOfStock;
    WriteBatch deleteBatch;
    float total_price = 0;
    ArrayList<String> productIdList = new ArrayList();
    private ListenerRegistration cartItemListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        initCartRecycleView();
        deleteBatch = firebaseFirestore.batch();
        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Total_amount_ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");


        Total_amount_ref.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                total_price = 0;
                getTotalAmount(queryDocumentSnapshots);
            }
        });
        bind.checkoutButton.setOnClickListener(v -> {

            if (isOutOfStock) {
                Toasty.error(getApplicationContext(), "some of products are not in stock,try to reduce quantity", Toasty.LENGTH_SHORT).show();
            } else {
                progressLoading(true);
                firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressLoading(true);
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            DocumentReference SellerProductref = firebaseFirestore.collection("Sellers").document(doc.getString("sellerId")).collection("productList").document(doc.getString("productId")).collection("tempOrderCart").document(auth.getUid());
                            DocumentReference UserProductRef = firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").document(doc.getString("productId"));
                            deleteBatch.delete(SellerProductref);
                            deleteBatch.delete(UserProductRef);
                        }
                        deleteBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    createTempOrderCart();
                                } else {
                                    Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });


            }

        });
        enableSwipeToDelete();
        bind.cartEmpty.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        });
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallBack swipeToDeleteCallback = new SwipeToDeleteCallBack(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();

                Cart cart = cartAdapter.getCart(position);
                cartUtils.deletecartItem(cart.getProductId(), cart.getSellerId(), getApplicationContext());
                cartAdapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(bind.cartRecycleView);
    }


    private void createTempOrderCart() {


        total_price = 0;
        WriteBatch orderBatch = firebaseFirestore.batch();

        DocumentReference UserRef = firebaseFirestore.collection("Users").document(auth.getUid());
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @androidx.annotation.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot user = transaction.get(UserRef);


                orderBatch.update(UserRef, "tempOrderSellerId", user.getString("cartSellerId"));
                for (int i = 0; i < cartAdapter.getItemCount(); i++) {
                    Cart cartItem = cartAdapter.getCart(i);
                    DocumentReference tempOrderCart = firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").document(cartItem.getProductId());
                    DocumentReference sellerTempOrderCart = firebaseFirestore.document(cartItem.getSellerProductRef().getPath()).collection("tempOrderCart").document(auth.getUid());
                    HashMap<String, DocumentReference> sellerIdHash = new HashMap<>();
                    sellerIdHash.put("tempOrderCartId", tempOrderCart);
                    orderBatch.set(tempOrderCart, cartItem);
                    orderBatch.set(sellerTempOrderCart, sellerIdHash);

                }

                orderBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressLoading(false);
                        Intent intent;
                        if (task.isSuccessful()) {
                            intent = new Intent(getApplicationContext(), orderActivity.class);
                            intent.putExtra("activity", CartActivity.class.getName());
                            startActivity(intent);
                        } else
                            Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                    }
                });
                return null;
            }


        });


    }

    private void initCartRecycleView() {
        query = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
        cartAdapter = new CartAdapter(query, this, this) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);
                productLoadingState(false);


                if (getItemCount() == 0) {
                    noProductFound(true);
                } else {
                    noProductFound(false);
                }


            }

            @Override
            protected void onDocumentModified(DocumentChange change) {
                super.onDocumentModified(change);
            }
        };


        bind.cartRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bind.cartRecycleView.setAdapter(cartAdapter);
    }

    void getTotalAmount(QuerySnapshot queryDocumentSnapshots) {


        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
            Cart cart = doc.toObject(Cart.class);


            total_price = total_price + cart.getPrice() * cart.getQuantity();


        }

        bind.totalPrice.setText("₹" + NumberFormat.getInstance().format(total_price));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (cartAdapter != null) {
            cartAdapter.startListening();
            productLoadingState(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cartAdapter != null) {
            cartAdapter.stopListening();
        }
    }

    private void productLoadingState(boolean state) {

        if (state) {
            bind.cartRecycleView.setVisibility(View.INVISIBLE);
            bind.shimmerRecyclerView.showShimmerAdapter();
            bind.shimmerRecyclerView.setVisibility(View.VISIBLE);
        } else {
            bind.cartRecycleView.setVisibility(View.VISIBLE);
            bind.shimmerRecyclerView.hideShimmerAdapter();
            bind.shimmerRecyclerView.setVisibility(View.GONE);
        }

    }

    void noProductFound(boolean state) {
        if (state) {
            bind.mainLayout.setVisibility(View.GONE);
            bind.animationView.playAnimation();
            bind.animationLayout.setVisibility(View.VISIBLE);
        } else {
            bind.animationLayout.setVisibility(View.GONE);
            bind.mainLayout.setVisibility(View.VISIBLE);
            bind.animationView.cancelAnimation();
        }
    }

    @Override
    public void onOutofStock(boolean status, String product_id) {


        if (productIdList.contains(product_id)) {
            if (status) {
                productIdList.remove(product_id);
            }
        } else {
            if (!status) {
                productIdList.add(product_id);
            }

        }

        isOutOfStock = productIdList.size() != cartAdapter.getItemCount();

    }

    void progressLoading(boolean state) {
        if (state) {
            bind.progressBarLayout.progressBarLoader.setVisibility(View.VISIBLE);
            bind.wholeLayout.setVisibility(View.GONE);
        } else {
            bind.progressBarLayout.progressBarLoader.setVisibility(View.GONE);
            bind.wholeLayout.setVisibility(View.VISIBLE);
        }

    }

}
