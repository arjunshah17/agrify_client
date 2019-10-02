package com.example.agrify.activity.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

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
import com.example.agrify.databinding.ActivityPwresetBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;
import spencerstudios.com.bungeelib.Bungee;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class PWresetActivity extends AppCompatActivity {
    AwesomeValidation validator;
ActivityPwresetBinding binding;
    private FirebaseAuth firebaseAuth;
    Animation animation;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pwreset);
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
        validator.addValidation(this,binding.emailEditText.getId(),android.util.Patterns.EMAIL_ADDRESS,R.string.fui_invalid_email_address);

    }

    private void initializeGUI() {
        binding.tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PWresetActivity.this, LoginActivity.class));
                Bungee.slideUp(PWresetActivity.this);
            }
        });
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator.validate()) {
                        showProgressDialog(true);
                    firebaseAuth.sendPasswordResetEmail(binding.emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgressDialog(false);
                            if(task.isSuccessful())
                            {
                                Toasty.success(PWresetActivity.this,"password reset send to"+binding.emailEditText.getText().toString(),Toasty.LENGTH_SHORT).show();
                                startActivity(new Intent(PWresetActivity.this, LoginActivity.class));
                                Bungee.slideUp(PWresetActivity.this);
                            }
                            else {
                                Toasty.error(PWresetActivity.this,task.getException().getLocalizedMessage(),Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
     void showProgressDialog(Boolean state) {


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
