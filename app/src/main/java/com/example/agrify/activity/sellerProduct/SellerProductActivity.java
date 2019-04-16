package com.example.agrify.activity.sellerProduct;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.model.Seller;
import com.example.agrify.activity.model.Store;
import com.example.agrify.databinding.ActivitySellerProductBinding;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.DefaultSliderView;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;


public class SellerProductActivity extends AppCompatActivity  implements EventListener<DocumentSnapshot> {
    String seller_id;
    String product_id;
    FirebaseFirestore firebaseFirestore;
    private DocumentReference mSellerProductRef;
    ActivitySellerProductBinding binding;

    private ListenerRegistration sellerProductListener;
    private ListenerRegistration productListener;
    private DocumentReference productRef;
    private GoogleMap map;
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


setSupportActionBar(binding.appBar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mSellerProductRef=firebaseFirestore.collection("store").document(product_id).collection("sellerList").document(seller_id);
        productRef=firebaseFirestore.collection("store").document(product_id);

        //   binding.imageSlider.setIndicatorAnimation(SliderLayout.A); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setScrollTimeInSec(5);
        initSlider();
        binding.appBar.setNavigationOnClickListener(v->{
            onBackPressed();

        });



    }

    private void initSlider() {

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
        productListener=productRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                productLoader(snapshot);

            }
        });
        sellerProductListener=mSellerProductRef.addSnapshotListener(this);

    }
String unit;
    private void productLoader(DocumentSnapshot snapshot) {
        Store store=snapshot.toObject(Store.class);
        binding.productName.setText(store.getName());
        unit=store.getUnit();
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
        Seller seller=snapshot.toObject(Seller.class);
binding.minQuantity.setText("min quantity:"+String.valueOf(seller.getMinQuantity())+"/"+unit);
binding.textInfo.setText(seller.getInfo());
binding.appBar.setTitle(seller.getName());
        binding.sellerPrice.setText(String.valueOf(seller.getPrice())+"/"+unit);


    }


}
