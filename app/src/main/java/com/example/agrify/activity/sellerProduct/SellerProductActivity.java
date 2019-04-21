package com.example.agrify.activity.sellerProduct;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.Utils.CartCounter;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.model.Store;
import com.example.agrify.activity.order.model.Order;
import com.example.agrify.activity.order.model.OrderItem;
import com.example.agrify.activity.order.orderActivity;
import com.example.agrify.activity.sellerProduct.adpater.SellerRecomAdpater;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.ActivitySellerProductBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.vincent.blurdialog.BlurDialog;
import com.vincent.blurdialog.listener.OnPositiveClick;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;
import it.sephiroth.android.library.numberpicker.NumberPicker;


public class SellerProductActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>, SellerRecomAdpater.OnProductSelectedListener {
    String seller_id;
    String product_id;
    String TAG = "SellerProductActivity";
    FirebaseFirestore firebaseFirestore;
    ActivitySellerProductBinding binding;
    SellerRecomAdpater adapter;
    Query query;
    WriteBatch cartBatch;
    WriteBatch clearBatch;
    FirebaseAuth auth;
    BlurDialog blurDialog;
    Seller seller;

    boolean isOutOfStock = false;
    String unit, name;
    private DocumentReference mSellerProductRef;
    private ListenerRegistration sellerProductListener;
    private ListenerRegistration productListener;
    private DocumentReference productRef;
    private GoogleMap map;
    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_product);
        if (getIntent().getStringExtra("seller_id") != null && getIntent().getStringExtra("product_id") != null) {
            product_id = getIntent().getStringExtra("product_id");
            seller_id = getIntent().getStringExtra("seller_id");
            Log.i("product_id", product_id);
            Log.i("seller_id", seller_id);
        }
        store = new Store();

        auth = FirebaseAuth.getInstance();

        seller = new Seller();
        setSupportActionBar(binding.appBar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        cartBatch = firebaseFirestore.batch();
        clearBatch = firebaseFirestore.batch();

        loadProductData(product_id, seller_id);
        initSlider(product_id, seller_id);
        //   binding.imageSlider.setIndicatorAnimation(SliderLayout.A); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setScrollTimeInSec(5);

        binding.appBar.setNavigationOnClickListener(v -> {
            onBackPressed();

        });
        initSellerRecomProductRecycleView();

        binding.addToCartButton.setOnClickListener(v -> {
            addToCart();
        });
        binding.quantityNumberpicker.setNumberPickerChangeListener(new NumberPicker.OnNumberPickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
                if (seller.getStock() > numberPicker.getProgress())
                    setOutOfStock(false);
                else setOutOfStock(true);

            }

            @Override
            public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) {

            }
        });
        binding.buyNow.setOnClickListener(v -> {
            if (isOutOfStock) {
                Toasty.error(getApplicationContext(), "some of products are not in stock,try to reduce quantity", Toasty.LENGTH_SHORT).show();
            } else {
                WriteBatch deleteBatch = firebaseFirestore.batch();
                firebaseFirestore.collection("Users").document(auth.getUid()).collection("tempOrderCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

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
    }

    private void createTempOrderCart() {
        OrderItem orderItem = new OrderItem();
        orderItem.setName(name);


    }


    private void addToCart() {
        final int[] Quantity = {0};
        Cart cart = new Cart();

        if (!(String.valueOf( binding.quantityNumberpicker.getProgress())).isEmpty()) {
            Quantity[0] = binding.quantityNumberpicker.getProgress();
            if (Quantity[0] >= seller.getMinQuantity() && Quantity[0] <= seller.getMaxQuantity()) {
                if (Quantity[0] <= seller.getStock()) {
                    cart.setSellerId(seller_id);
                    cart.setProductId(seller.getProductId());
                    cart.setSellerProductRef(seller.getSellerProductRef());
                    cart.setQuantity(Quantity[0]);
                    cart.setName(store.getName());
                    cart.setProductImageUrl(store.getProductImageUrl());
                    cart.setUnit(store.getUnit());
                    cart.setSellerProductRef(seller.getSellerProductRef());
                    cart.setPrice(seller.getPrice());
                    cart.setMaxQuantity(seller.getMaxQuantity());
                    cart.setMinQuantity(seller.getMinQuantity());


                    firebaseFirestore.collection("Users").document(auth.getUid()).get().addOnSuccessListener(snapshot -> {

                        String cartSellerId = snapshot.getString("cartSellerId");

                        assert cartSellerId != null;
                        if (cartSellerId.equals("") || cartSellerId == null || cartSellerId.equals(cart.getSellerId())) {


                            firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").get().addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                {
                                    boolean isAlreadyInCart=false;
                                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                                    {
                                        if(seller.getProductId().equals(documentSnapshot.getString("productId")))
                                        {
                                            isAlreadyInCart=true;
                                            break;
                                        }
                                    }
                                    if(!isAlreadyInCart)
                                    {
                                        insertItemToCart(cart);
                                    }
                                    else
                                    {
                                        Toasty.error(getApplicationContext(),name +" is already in cart").show();
                                    }
                                }

                            });


                        } else {
                            firebaseFirestore.collection("Sellers").document(cartSellerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot snapshot) {
                                    blurDialog = new BlurDialog();
                                    BlurDialog.Builder builder = new BlurDialog.Builder()
                                            .isCancelable(true).radius(10)

                                            .isOutsideCancelable(true).message("yout cart contain items from " + snapshot.getString("name") + " store.Do you want discard the selection and add items from " + seller.getName() + " store.")
                                            .positiveMsg("Yes")
                                            .negativeMsg("No").positiveClick(new OnPositiveClick() {
                                                @Override
                                                public void onClick() {
                                                    clearBatch = null;
                                                    clearBatch = firebaseFirestore.batch();
                                                    FirebaseFunctions mFunctions;
                                                    mFunctions = FirebaseFunctions.getInstance();

                                                    HashMap<String, Object> storeHash = new HashMap<>();
                                                    firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                for(QueryDocumentSnapshot doc:task.getResult())
                                                                {
                                                                    String productId=doc.getId();
                                                                    String sellerId=doc.getString("sellerId");
                                                                    DocumentReference Prodref=firebaseFirestore.collection("store").document(productId).collection("product_user_cart").document(auth.getUid());
                                                                    DocumentReference sellerProductRef=firebaseFirestore.collection("Sellers").document(sellerId).collection("cartItemUser").document(auth.getUid());
                                                                    clearBatch.delete(Prodref);
                                                                    clearBatch.delete(sellerProductRef);
                                                                }

                                                                        CollectionReference ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
                                                                        storeHash.put("path", ref.getPath());
                                                                        mFunctions.getHttpsCallable("recursiveDelete").call(storeHash).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentReference reference = firebaseFirestore.collection("Users").document(auth.getUid());
                                                                                    clearBatch.update(reference, "cartCounter", 0);
                                                                                    clearBatch.update(reference, "cartSellerId", "");
                                                                                    clearBatch.commit();
                                                                                    Toasty.info(getApplicationContext(), "cart is cleared", Toasty.LENGTH_SHORT).show();
                                                                                    insertItemToCart(cart);
                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });


                                                    blurDialog.dismiss();
                                                }
                                            }).negativeClick(() -> {
                                                blurDialog.dismiss();

                                            }).type(BlurDialog.TYPE_DOUBLE_OPTION)
                                            .createBuilder(SellerProductActivity.this);
                                    blurDialog.setBuilder(builder);
                                    blurDialog.show();
                                }
                            });


                        }


                    });


                } else {
                    Toasty.error(getApplicationContext(), "not enough stock is there,enter quantity less then" + seller.getStock(), Toasty.LENGTH_SHORT).show();
                }
            } else {
                binding.textQuantity.setError("quanity must be between " + seller.getMinQuantity() + " and " + seller.getMaxQuantity());
            }
        } else {
            binding.textQuantity.setError("enter currect quantity");
        }


    }

    private void insertItemToCart(Cart cart) {
        cartBatch = null;
        cartBatch = firebaseFirestore.batch();

        DocumentReference cartRef = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document(product_id);
        DocumentReference sellerCartRef=firebaseFirestore.document(seller.getSellerProductRef().getPath()).collection("cartItemUser").document(auth.getUid());
        cart.setUserCartProductRef(cartRef);
        cart.setSellerCartProductRef(sellerCartRef);
        cartBatch.set(cartRef, cart);
        DocumentReference userRef = firebaseFirestore.collection("Users").document(auth.getUid());
        DocumentReference product_user_cart=firebaseFirestore.collection("store").document(product_id).collection("product_user_cart").document(auth.getUid());
        HashMap<String,DocumentReference> product_user=new HashMap<>();
        product_user.put("userCartRef",cartRef);
        cartBatch.set(product_user_cart,product_user);
        cartBatch.set(sellerCartRef,product_user);

        cartBatch.update(userRef, "cartSellerId", seller_id);
        cartBatch.update(userRef, "cartCounter", FieldValue.increment(1));
        cartBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toasty.info(getApplicationContext(), name + " added successfully in cart", Toasty.LENGTH_SHORT).show();
                } else {
                    Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                }
            }
        });


    }

    void loadProductData(String product_id, String seller_id) {
        productListener = null;
        sellerProductListener = null;

        mSellerProductRef = firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id);
        productRef = firebaseFirestore.collection("store").document(product_id);
productLoading(true);
        productListener = productRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                productLoader(snapshot);

            }
        });
        sellerProductListener = mSellerProductRef.addSnapshotListener(this);
    }

    private void initSellerRecomProductRecycleView() {
        query = firebaseFirestore.collection("Sellers").document(seller_id).collection("productList");
        adapter = new SellerRecomAdpater(query, this, this);
        binding.sellerRecomRv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.sellerRecomRv.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }

    private void initSlider(String product_id, String seller_id) {
        binding.imageSlider.clearSliderViews();
        firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id).collection("productImage").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                DefaultSliderView sliderView = new DefaultSliderView(getApplicationContext());
                                sliderView.setImageUrl(document.getString("url"));
                                sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                binding.imageSlider.addSliderView(sliderView);

                            }


                        } else {

                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }

    private void productLoader(DocumentSnapshot snapshot) {
         store = snapshot.toObject(Store.class);
        binding.productName.setText(store.getName());
        unit = store.getUnit();
        name = store.getName();

    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            return;
        }
        productLoading(false);
        Toasty.info(getApplicationContext(), "checkout again some data is changed", Toasty.LENGTH_SHORT).show();
        sellerProductLoader(snapshot);


    }

    private void sellerProductLoader(DocumentSnapshot snapshot) {
        seller = snapshot.toObject(Seller.class);
        binding.quantityNumberpicker.setMaxValue(seller.getMaxQuantity());
        binding.quantityNumberpicker.setMinValue(seller.getMinQuantity());
        binding.minQuantity.setText("min quantity:" + seller.getMinQuantity() + "/" + unit);
        binding.maxQuantity.setText("mxn quantity:" + seller.getMaxQuantity() + "/" + unit);
        binding.textInfo.setText(seller.getInfo());
        binding.appBar.setTitle(seller.getName());
        binding.sellerPrice.setText(seller.getPrice() + "/" + unit);
        binding.textQuantity.setText("enter quantity of " + name + " in " + unit);
        if (seller.getStock() > binding.quantityNumberpicker.getProgress())
            setOutOfStock(false);
        else setOutOfStock(true);




    }


    @Override
    public void onProductSelected(String productId, String SellerId) {

        mSellerProductRef = firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id);
        productRef = firebaseFirestore.collection("store").document(product_id);

        loadProductData(product_id, seller_id);
        initSlider(product_id, seller_id);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        final MenuItem add_to_cart = menu.findItem(R.id.addToCartButton);
        LayerDrawable icon = (LayerDrawable) add_to_cart.getIcon();
        CartCounter.loadCounter(icon, getApplicationContext());
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add the function to perform here


        startActivity(new Intent(getApplicationContext(), CartActivity.class));
        return (true);

    }

    void productLoading(boolean state)
    {
        if(state)
        {
            binding.progressBarLayout.progressBarLoader.setVisibility(View.VISIBLE);
            binding.mainLayout.setVisibility(View.GONE);
        }
        else
        {
            binding.progressBarLayout.progressBarLoader.setVisibility(View.GONE);
            binding.mainLayout.setVisibility(View.VISIBLE);
        }
    }

    void setOutOfStock(boolean state) {
        if (state) {
            isOutOfStock = true;
            binding.outOfStock.setVisibility(View.VISIBLE);
        } else {
            isOutOfStock = false;
            binding.outOfStock.setVisibility(View.GONE);
        }
    }
}
