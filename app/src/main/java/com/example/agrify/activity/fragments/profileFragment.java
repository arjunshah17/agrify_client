package com.example.agrify.activity.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.agrify.R;
import com.example.agrify.activity.editProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment {


    public profileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_profile,
                container, false);
        Button button = (Button) rootView.findViewById(R.id.EditButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editprofileDetails();
            }
        });
        return rootView;
    }

    public void editprofileDetails() {
        Intent intent = new Intent(getActivity(), editProfile.class);
        startActivity(intent);
    }
}


