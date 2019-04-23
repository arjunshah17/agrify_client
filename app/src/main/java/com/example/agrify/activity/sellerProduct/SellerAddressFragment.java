package com.example.agrify.activity.sellerProduct;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.activity.address.model.Address;
import com.example.agrify.databinding.AddressBottomFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class SellerAddressFragment extends BottomSheetDialogFragment {

    DocumentReference addRef;
    AddressBottomFragmentBinding binding;

    SellerAddressFragment(DocumentReference addRef) {
        this.addRef = addRef;
    }

    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.address_bottom_fragment, container,
                false);
        addressLoading(true);
        addRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    addressLoading(false);
                    Address address = task.getResult().toObject(Address.class);
                    binding.addressNameTv.setText(address.getName());
                    binding.addressLocation.setText(address.getHouseNum() + address.getLocation());
                    binding.addressCard.setOnClickListener(v -> {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("geo:" + address.getGeoLocation().getLatitude() + "," + address.getGeoLocation().getLongitude()));
                        try {


                            startActivity(intent);
                        } catch (Exception ex
                        ) {
                            ex.printStackTrace();
                        }

                    });
                }
            }
        });

        return binding.getRoot();
    }

    private void addressLoading(boolean state) {
        if (state) {
            binding.addressCard.setVisibility(View.GONE);

            binding.animationLayout.setVisibility(View.VISIBLE);
            binding.animationViewText.playAnimation();
            binding.animationLayout.setVisibility(View.VISIBLE);
        } else {
            binding.animationLayout.setVisibility(View.GONE);
            binding.addressCard.setVisibility(View.VISIBLE);
            binding.animationViewText.cancelAnimation();
        }
    }
}