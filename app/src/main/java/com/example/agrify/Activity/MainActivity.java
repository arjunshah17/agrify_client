package com.example.agrify.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.agrify.Activity.fragments.profileFragment;
import com.example.agrify.Activity.fragments.storeFragment;
import com.example.agrify.R;
import com.example.agrify.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        loadFragment(new storeFragment());      //default load Store fragment

        bind.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.storeItem:
                        fragment = new storeFragment();
                        break;

                    case R.id.wishlistItem:

                        break;

                    case R.id.chatItem:

                        break;

                    case R.id.notificationItem:

                        break;
                    case R.id.aboutItem:
                        fragment = new profileFragment();
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
}
