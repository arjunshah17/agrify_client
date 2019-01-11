package com.example.agrify.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.agrify.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            signIn();
        }
        else {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()
                        )).setIsSmartLockEnabled(false).setTheme(R.style.AppTheme_Launcher)
                        .build(),
                RC_SIGN_IN);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);


            // Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                // Sign in failed

                Log.e("result", "Sign-in error: ", response.getError());
            }
        }

    }

}
