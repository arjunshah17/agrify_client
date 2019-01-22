package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.agrify.R;
import com.example.agrify.activity.GlideApp;
import com.example.agrify.activity.editProfile;
import com.example.agrify.activity.model.User;
import com.example.agrify.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment {

    User user;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseUser firebaseUser;
    public profileFragment() {
        // Required empty public constructor
    }

    private FragmentProfileBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile,
                container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();

        stateLoading(true);
        firebaseFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadData(); //lo
                firebaseFirestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
            }
        });





        bind.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), editProfile.class);
                startActivity(intent);
            }
        });
        return bind.getRoot();
    }

    private void loadData() {
        user = new User();

        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user.setName(firebaseUser.getDisplayName());
                user.setEmail(firebaseUser.getEmail());
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        user.setPhone(task.getResult().getString("phone"));


                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        GlideApp.with(getActivity()).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.add_photo).into(bind.userProfilePhoto);
                    }

                    bind.setUser(user);


                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }
                stateLoading(false);


            }
        });


    }

    private void stateLoading(boolean stateStatus) {
        if (stateStatus) {
            bind.ProfileLayout.setVisibility(View.GONE);
            bind.progressLoading.setVisibility(View.VISIBLE);
        } else {
            bind.ProfileLayout.setVisibility(View.VISIBLE);
            bind.progressLoading.setVisibility(View.GONE);
        }
    }

}


