package com.example.agrify.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.agrify.R;
import com.example.agrify.databinding.ActivityStoreDetailBinding;

public class storeDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStoreDetailBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_store_detail);
        bind.productDes.setMovementMethod(new ScrollingMovementMethod());

    }
}
