package com.example.agrify.activity.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.agrify.R;
import com.example.agrify.activity.Utils.internetConnectionUtils;
import com.example.agrify.activity.address.model.Address;
import com.example.agrify.databinding.ActivityAddressBinding;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import es.dmoral.toasty.Toasty;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class addressActivity extends AppCompatActivity {

    int PLACE_PICKER_REQUEST = 1;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    ActivityAddressBinding bind;
    Place place;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    Address address;
    String AddressRef;
    boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_address);


        bind = DataBindingUtil.setContentView(this, R.layout.activity_address);

address=new Address();
        mAwesomeValidation.addValidation(this, bind.addressNameTv.getId(), RegexTemplate.NOT_EMPTY, R.string.empty);
        mAwesomeValidation.addValidation(this, bind.houseFlatTv.getId(), RegexTemplate.NOT_EMPTY, R.string.empty);
        mAwesomeValidation.addValidation(this, bind.locationTv.getId(), RegexTemplate.NOT_EMPTY, R.string.empty);
        if (getIntent().getStringExtra("addressRef") != null) {
            AddressRef = getIntent().getStringExtra("addressRef");
            isEdited = true;
        }
        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              onBackPressed();
            }
        });
        firebaseFirestore= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser= auth.getCurrentUser();
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bind.saveAddress.setOnClickListener(v-> {
            if(mAwesomeValidation.validate()) {
                if (internetConnectionUtils.isInternetConnected(getApplicationContext())) {
                    if (place != null) {
                        dataLoading(true);
                        address.setGeoLocation(new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
                        address.setName(bind.addressNameTv.getText().toString());
                        address.setHouseNum(bind.houseFlatTv.getText().toString());
                        address.setLocation(bind.locationTv.getText().toString());
                        DocumentReference addRef;
                        if (!isEdited) {
                            addRef = firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("addressList").document();
                        } else {
                            addRef = firebaseFirestore.document(AddressRef);
                        }
                        addRef.set(address).addOnCompleteListener(task -> {
                            dataLoading(false);
                            if (task.isSuccessful()) {
                                Toasty.info(getApplicationContext(), "uploaded address in server").show();
                                onBackPressed();

                            } else {
                                Toasty.error(getApplicationContext(), task.getException().getLocalizedMessage()).show();
                            }
                        });
                    }

                }
            }
            else
            {
                Toasty.error(getApplicationContext(), "enter information properly ",Toasty.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                place = PlacePicker.getPlace(data, this);
                String Name = String.format("%s", place.getName());
                Toasty.info(getApplicationContext(), place.getLatLng().toString(), Toasty.LENGTH_SHORT).show();
                bind.houseFlatTv.setText(Name);
                String Address = String.format("%s", place.getAddress(), "\n" + place.getLatLng().latitude, "\n" + place.getLatLng().longitude);
                bind.locationTv.setText(place.getAddress());
            }
        }
    }
    void dataLoading(boolean state)
    {
        if(state)
        {
            bind.loaderLayout.setVisibility(View.GONE);
            bind.animationLayout.setVisibility(View.VISIBLE);
            bind.animationViewText.playAnimation();
            bind.animationView.playAnimation();

        }
        else {
            bind.animationLayout.setVisibility(View.GONE);
            bind.loaderLayout.setVisibility(View.VISIBLE);
            bind.animationView.cancelAnimation();
            bind.animationViewText.cancelAnimation();
        }
    }
}
