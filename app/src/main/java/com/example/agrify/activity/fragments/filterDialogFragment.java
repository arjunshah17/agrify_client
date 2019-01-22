package com.example.agrify.activity.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.agrify.R;
import com.example.agrify.activity.Filters;
import com.example.agrify.databinding.FragmentFilterDialogBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class filterDialogFragment extends DialogFragment {
    FragmentFilterDialogBinding binding;

    public filterDialogFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_dialog, container, false);
        return binding.getRoot();
    }


    interface FilterListener {

        void onFilter(Filters filters);
    }


}
