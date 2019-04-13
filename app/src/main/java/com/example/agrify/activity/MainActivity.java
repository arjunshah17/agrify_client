package com.example.agrify.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.agrify.R;
import com.example.agrify.activity.ChatBot.ChatFragment;
import com.example.agrify.activity.fragments.StoreFragment;
import com.example.agrify.activity.fragments.WishFragment;
import com.example.agrify.activity.fragments.profileFragment;
import com.example.agrify.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    StoreFragment store;
    ActivityMainBinding bind;
    profileFragment profile;
    Fragment LoadedFragment;
    WishFragment wish;
    ChatFragment chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use the MenuItem given by this library and not the default one.
        //First parameter is the title of the menu item and then the second parameter is the image which will be the background of the menu item.

        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        store = new StoreFragment();
        profile = new profileFragment();
        wish = new WishFragment();
        chat = new ChatFragment();
        //default load Store fragment
        LoadedFragment = store;
        loadFragment(store);

        bind.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.storeItem:
                        LoadedFragment = store;
                        break;

                    case R.id.wishlistItem:
                        LoadedFragment = wish;
                        break;

                    case R.id.chatItem:
                        LoadedFragment = chat;
                        break;

                    case R.id.notificationItem:

                        break;
                    case R.id.aboutItem:
                        LoadedFragment = profile;
                        break;
                }

                return loadFragment(LoadedFragment);
            }
        });

    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();

                    }
                }).setNegativeButton("No", null).show();

    }
}
