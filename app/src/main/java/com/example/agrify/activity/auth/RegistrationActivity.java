
package com.example.agrify.activity.auth;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
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

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class RegistrationActivity extends AppCompatActivity {

ActivityRegistrationBinding binding;

    AwesomeValidation validator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil. setContentView(this,R.layout.activity_registration);
        validator=new AwesomeValidation(BASIC);
        initializeValidators();
        initializeGUI();
    }


    private void initializeValidators() {
        validator.addValidation(this,binding.inputName.getId(), RegexTemplate.NOT_EMPTY,R.string.username_empty);
        validator.addValidation(this,binding.inputEmail.getId(),android.util.Patterns.EMAIL_ADDRESS,R.string.fui_invalid_email_address);
        validator.addValidation(this,binding.inputMobile.getId(),RegexTemplate.TELEPHONE,R.string.phone_error);

validator.addValidation(this,binding.inputPassword.getId(),RegexTemplate.NOT_EMPTY,R.string.password_error);
validator.addValidation(this,binding.inputPassword.getId(),binding.inputReEnterPassword.getId(),R.string.password_match_error);
    }
    private void initializeGUI() {
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator.validate()) {



                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                }
            }
        });

    }

}
