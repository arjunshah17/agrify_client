package com.example.agrify.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.databinding.ActivityLoginBinding;
import com.github.paolorotolo.appintro.ProgressIndicatorController;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import es.dmoral.toasty.Toasty;
import kotlin.text.Regex;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class LoginActivity extends AppCompatActivity {
String TAG="LoginActivity";
    AwesomeValidation validator;
    GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil. setContentView(this,R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth=FirebaseAuth.getInstance();

validator=new AwesomeValidation(BASIC);
      initializeValidators();

        initializeGUI();
    }

    private void initializeValidators() {
        validator.addValidation(this,binding.inputEmail.getId(),android.util.Patterns.EMAIL_ADDRESS,R.string.fui_invalid_email_address);
        validator.addValidation(this,binding.inputPassword.getId(),RegexTemplate.NOT_EMPTY,R.string.password_error);
    }

    private void initializeGUI() {

        binding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PWresetActivity.class));
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator.validate()) {
                    showProgressDialog(true);
                    firebaseAuth.signInWithEmailAndPassword(binding.inputEmail.getText().toString(), binding.inputPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    showProgressDialog(false);
                                    if (task.isSuccessful()) {

                                        Toasty.success(LoginActivity.this, "login success fully", Toasty.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                    else
                                    {
                                        Toasty.error(LoginActivity.this,task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }



            }
        });
        binding.signinGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });



    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog(true);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.

                        }

                        // [START_EXCLUDE]
                      showProgressDialog(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    private void showProgressDialog(Boolean state) {


        if(state) {
            binding.progressLoading.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressLoading.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                
                // [END_EXCLUDE]
            }
        }
    }
}