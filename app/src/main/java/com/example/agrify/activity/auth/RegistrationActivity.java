
package com.example.agrify.activity.auth;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.agrify.R;
import com.example.agrify.activity.MainActivity;
import com.example.agrify.databinding.ActivityRegistrationBinding;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class RegistrationActivity extends AppCompatActivity {

ActivityRegistrationBinding binding;

    AwesomeValidation validator;
    private FirebaseAuth firebaseAuth;
    private Animation animation;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        validator = new AwesomeValidation(BASIC);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(binding.bgHeader);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        animation = AnimationUtils.loadAnimation(this,R.animator.uptodowndiagonal);
        binding.rlayout.setAnimation(animation);
        initializeValidators();
        initializeGUI();
    }

    private void initializeValidators() {

        validator.addValidation(this,binding.inputEmail.getId(),android.util.Patterns.EMAIL_ADDRESS,R.string.fui_invalid_email_address);

               String passRegex="^(?=\\S+$).{6,}";
validator.addValidation(this,binding.inputPassword.getId(),passRegex,R.string.password_error);
validator.addValidation(this,binding.inputPassword.getId(),binding.inputReEnterPassword.getId(),R.string.password_match_error);
    }
    private void initializeGUI() {
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator.validate()) {
                showProgressDialog(true);
         firebaseAuth.createUserWithEmailAndPassword(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                showProgressDialog(false);
                 if(task.isSuccessful())
                 {
                     Toasty.success(RegistrationActivity.this,"new account created",Toasty.LENGTH_SHORT).show();
                     startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                     Bungee.inAndOut(RegistrationActivity.this);
                 }
                 else
                 {
                     Toasty.error(RegistrationActivity.this,task.getException().getLocalizedMessage()).show();
                 }
             }
         });



                }
            }
        });
        binding.linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
    }

    private void showProgressDialog(Boolean state) {


        if(state) {
            binding.progressLoading.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressLoading.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
