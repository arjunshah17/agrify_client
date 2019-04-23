package com.example.agrify.activity.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.agrify.R;
import com.example.agrify.databinding.AddressListFragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AddressListFragment extends BottomSheetDialogFragment {
    AddressAdapter addressAdapter;
    AddressListFragmentBinding binding;
    String TAG = "AddressListFragment";
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FirebaseAuth auth;
    AddressAdapter.OnAddressSelectedListener listener;

    public AddressListFragment(AddressAdapter.OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    @Override
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

        binding = DataBindingUtil.inflate(inflater, R.layout.address_list_fragment, container,
                false);
        mFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // get the views and attach the listener
        initFirestore();
        initRecyclerView();
        binding.addressButton.setOnClickListener(v->{
            startActivity(new Intent(getActivity(),addressActivity.class));
        });
        return binding.getRoot();

    }

    private void initFirestore() {

        mQuery = mFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("addressList");

    }

    private void initRecyclerView() {


        addressAdapter = new AddressAdapter(mQuery, getActivity(),TAG,listener) {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                super.onEvent(documentSnapshots, e);
                productLoadingState(false);

                if (getItemCount() == 0) {

                } else {

                }
            }

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    noProductFound(true);


                } else {
                    noProductFound(false);
                }

            }


        };

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.addressListRv.setHasFixedSize(true);
        binding.addressListRv.setLayoutManager(mLayoutManager);


        binding.addressListRv.setAdapter(addressAdapter);


    }



    private void noProductFound(boolean state) {
        if (state) {
            binding.addressListRv.setVisibility(View.GONE);


            binding.animationView.playAnimation();
            binding.animationLayout.setVisibility(View.VISIBLE);
        } else {
            binding.animationLayout.setVisibility(View.GONE);
            binding.addressListRv.setVisibility(View.VISIBLE);
            binding.animationView.cancelAnimation();
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary

        // Start listening for Firestore updates
        if (addressAdapter != null) {

            productLoadingState(true);
            addressAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (addressAdapter != null) {
            addressAdapter.stopListening();
        }
    }

    void productLoadingState(boolean state) {
        if (state) {
            binding.addressListRv.setVisibility(View.INVISIBLE);
            binding.shimmerRecyclerView.showShimmerAdapter();
            binding.shimmerRecyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.addressListRv.setVisibility(View.VISIBLE);
            binding.shimmerRecyclerView.hideShimmerAdapter();
            binding.shimmerRecyclerView.setVisibility(View.GONE);
        }
    }

}
