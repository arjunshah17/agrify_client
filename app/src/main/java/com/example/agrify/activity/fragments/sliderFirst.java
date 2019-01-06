package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.databinding.FragmentSliderFirstBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class sliderFirst extends Fragment {

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;
    public sliderFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentSliderFirstBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slider_first, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = binding.name.getText().toString();

             storeFireStoreData(userName);
            }

        });

        return binding.getRoot();
    }

    private void storeFireStoreData(String userName) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", userName);


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
Toast.makeText(getContext(),"success",Toast.LENGTH_LONG).show();


                } else {

                    String error = task.getException().getMessage();


                }


            }
        });
    }
}
