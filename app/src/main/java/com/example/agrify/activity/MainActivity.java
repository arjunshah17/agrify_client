package com.example.agrify.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.agrify.R;
import com.example.agrify.activity.fragments.StoreFragment;
import com.example.agrify.activity.fragments.profileFragment;
import com.example.agrify.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    StoreFragment store;
    profileFragment profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        store = new StoreFragment();
        profile = new profileFragment();
        loadFragment(store);      //default load Store fragment

        bind.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;


                switch (menuItem.getItemId()) {
                    case R.id.storeItem:
                        fragment = store;
                        break;

                    case R.id.wishlistItem:

                        break;

                    case R.id.chatItem:

                        break;

                    case R.id.notificationItem:

                        break;
                    case R.id.aboutItem:
                        fragment = profile;
                        break;
                }

                return loadFragment(fragment);
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

    public static class editProfile extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

    }


}
