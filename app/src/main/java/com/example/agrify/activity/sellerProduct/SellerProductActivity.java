package com.example.agrify.activity.sellerProduct;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrify.R;
import com.example.agrify.activity.CartActivity;
import com.example.agrify.activity.Utils.CartCounter;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.model.Store;
import com.example.agrify.activity.sellerProduct.adpater.SellerRecomAdpater;
import com.example.agrify.activity.sellerProduct.model.Cart;
import com.example.agrify.databinding.ActivitySellerProductBinding;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
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
import com.google.firebase.firestore.model.value.FieldValueOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.vincent.blurdialog.BlurDialog;
import com.vincent.blurdialog.listener.OnPositiveClick;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;


public class SellerProductActivity extends AppCompatActivity  implements EventListener<DocumentSnapshot> , SellerRecomAdpater.OnProductSelectedListener {
    String seller_id;
    String product_id;
    String TAG="SellerProductActivity";
    FirebaseFirestore firebaseFirestore;
    private DocumentReference mSellerProductRef;
    ActivitySellerProductBinding binding;
    SellerRecomAdpater adapter;
    Query query;
    WriteBatch cartBatch;
    WriteBatch clearBatch;
    FirebaseAuth auth;

    private ListenerRegistration sellerProductListener;
    private ListenerRegistration productListener;
    private DocumentReference productRef;
    private GoogleMap map;
    BlurDialog blurDialog;

    Seller seller;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_product);
        if (getIntent().getStringExtra("seller_id") != null && getIntent().getStringExtra("product_id") != null) {
            product_id = getIntent().getStringExtra("product_id");
            seller_id = getIntent().getStringExtra("seller_id");
            Log.i("product_id", product_id);
            Log.i("seller_id", seller_id);
        }

        auth=FirebaseAuth.getInstance();

        seller=new Seller();
        setSupportActionBar(binding.appBar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        cartBatch=firebaseFirestore.batch();
        clearBatch=firebaseFirestore.batch();

        loadProductData(product_id,seller_id);
initSlider(product_id,seller_id);
        //   binding.imageSlider.setIndicatorAnimation(SliderLayout.A); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setScrollTimeInSec(5);

        binding.appBar.setNavigationOnClickListener(v->{
            onBackPressed();

        });
     initSellerRecomProductRecycleView();

binding.addToCartButton.setOnClickListener(v->{
    addToCart();
});

    }

    private void addToCart() {
      int   Quantity=0;
        Cart cart =new Cart();

                if(!binding.quantityEditText.getText().toString().isEmpty())
                {
                    Quantity= Integer.parseInt(
                            Objects.requireNonNull(binding.quantityEditText.getText()).toString());
                    if( Quantity >= seller.getMinQuantity() && Quantity <=seller.getMaxQuantity())
                    {
                        if(Quantity <= seller.getStock())

                        {  cart.setSellerId(seller_id);
                           cart.setProductId(seller.getProductId());
                           cart.setSellerProductRef(seller.getSellerProductRef());
                           cart.setQuantity(Quantity);

                            firebaseFirestore.collection("Users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot snapshot) {
                                    String cartSellerId=snapshot.getString("cartSellerId");

                                    if(cartSellerId==null || cartSellerId.equals(cart.getSellerId()))
                                    {

                                            insertItemToCart(cart);

                                    }
                                    else
                                    {
                                        firebaseFirestore.collection("Sellers").document(cartSellerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                blurDialog=new BlurDialog();
                                                BlurDialog.Builder builder = new BlurDialog.Builder()
                                                        .isCancelable(true).radius(10)

                                                        .isOutsideCancelable(true).message("yout cart contain items from "+snapshot.getString("name")+" store.Do you want discard the selection and add items from "+seller.getName()+" store.")
                                                        .positiveMsg("Yes")
                                                        .negativeMsg("No").positiveClick(new OnPositiveClick() {
                                                            @Override
                                                            public void onClick() {
                                                                clearBatch=null;
                                                                clearBatch=firebaseFirestore.batch();
                                                                FirebaseFunctions mFunctions;
                                                                mFunctions = FirebaseFunctions.getInstance();
                                                                HashMap<String, Object> storeHash = new HashMap<>();

                                                                CollectionReference ref = firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList");
                                                                storeHash.put("path", ref.getPath());
                                                                mFunctions.getHttpsCallable("recursiveDelete").call(storeHash).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                                        if(task.isSuccessful()) {
                                                                            DocumentReference reference = firebaseFirestore.collection("Users").document(auth.getUid());
                                                                            clearBatch.update(reference, "cartCounter", 0);
                                                                            clearBatch.update(reference,"cartSellerId",null);
                                                                            clearBatch.commit();
                                                                            Toasty.info(getApplicationContext(), "cart is cleared", Toasty.LENGTH_SHORT).show();
                                                                            insertItemToCart(cart);
                                                                        }
                                                                    }
                                                                });

                                                                blurDialog.dismiss();
                                                            }
                                                        }) .negativeClick(() -> {
                                                            blurDialog.dismiss();

                                                        }) .type(BlurDialog.TYPE_DOUBLE_OPTION)
                                                        .createBuilder(SellerProductActivity.this);
                                                blurDialog.setBuilder(builder);
                                                blurDialog.show();
                                            }
                                        });



                                    }


                                }
                            });


                        }
                        else
                        {
                            Toasty.error(getApplicationContext(),"not enough stock is there,enter quantity less then"+seller.getStock(),Toasty.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        binding.quantityEditText.setError("quanity must be between "+seller.getMinQuantity() +" and "+seller.getMaxQuantity());
                    }
                }
                else
                {
                    binding.quantityEditText.setError("enter currect quantity");
                }





    }

    private void insertItemToCart(Cart cart) {
              cartBatch=null;
              cartBatch=firebaseFirestore.batch();

        DocumentReference cartRef=firebaseFirestore.collection("Users").document(auth.getUid()).collection("cartItemList").document();
        cartBatch.set(cartRef,cart);
        DocumentReference userRef=firebaseFirestore.collection("Users").document(auth.getUid());

        cartBatch.update(userRef,"cartSellerId",seller_id);
        cartBatch.update(userRef,"cartCounter",FieldValue.increment(1));
      cartBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful())
              {

                  Toasty.info(getApplicationContext(),name+"added successfully in cart",Toasty.LENGTH_SHORT).show();
              }
              else
              {
                  Toasty.error(getApplicationContext(),task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
              }
          }
      });


    }

    void  loadProductData(String product_id,String seller_id)
   {
      productListener=null;
      sellerProductListener=null;

       mSellerProductRef=firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id);
       productRef=firebaseFirestore.collection("store").document(product_id);

       productListener=productRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
               productLoader(snapshot);

           }
       });
       sellerProductListener=mSellerProductRef.addSnapshotListener(this);
   }

    private void initSellerRecomProductRecycleView() {
        query=firebaseFirestore.collection("Sellers").document(seller_id).collection("productList");
        adapter =new SellerRecomAdpater(query,this,this);
        binding.sellerRecomRv.setLayoutManager(new GridLayoutManager(this,3));
        binding.sellerRecomRv.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        {
            adapter.stopListening();
        }

    }


    private void initSlider(String product_id,String seller_id) {
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
        if(adapter!=null)
        {
            adapter.startListening();
        }


    }
String unit,name;
    private void productLoader(DocumentSnapshot snapshot) {
        Store store=snapshot.toObject(Store.class);
        binding.productName.setText(store.getName());
        unit=store.getUnit();
        name=store.getName();

    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if(e!=null)
        {
            return;
        }
        Toasty.info(getApplicationContext(),"checkout again some data is changed",Toasty.LENGTH_SHORT).show();
        sellerProductLoader(snapshot);


    }

    private void sellerProductLoader(DocumentSnapshot snapshot) {
        seller=snapshot.toObject(Seller.class);
binding.minQuantity.setText("min quantity:"+String.valueOf(seller.getMinQuantity())+"/"+unit);
        binding.maxQuantity.setText("mxn quantity:"+String.valueOf(seller.getMaxQuantity())+"/"+unit);
binding.textInfo.setText(seller.getInfo());
binding.appBar.setTitle(seller.getName());
        binding.sellerPrice.setText(String.valueOf(seller.getPrice())+"/"+unit);
        binding.quantityTextLayout.setHint("enter quantity of "+name+" in "+unit);





    }




    @Override
    public void onProductSelected(String productId, String SellerId) {

        mSellerProductRef=firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id);
        productRef=firebaseFirestore.collection("store").document(product_id);

   loadProductData(product_id,seller_id);
   initSlider(product_id,seller_id);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        final  MenuItem add_to_cart = menu.findItem(R.id.addToCartButton);
        LayerDrawable icon = (LayerDrawable) add_to_cart.getIcon();
        CartCounter.loadCounter(icon,getApplicationContext());
        return super.onCreateOptionsMenu(menu);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add the function to perform here


        startActivity(new Intent(getApplicationContext(), CartActivity.class));
        return (true);

    }
}
