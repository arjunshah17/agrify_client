package com.example.agrify.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import id.zelory.compressor.Compressor;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrify.R;
import com.example.agrify.activity.model.User;
import com.example.agrify.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;



public class editProfile extends AppCompatActivity {
    private static final int SELECTED_PIC = 1;

    ActivityEditProfileBinding bind;
    User user;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    FirebaseUser firebaseUser;
    private Uri mainImageURI = null;
    private boolean isChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        stateLoading(true);


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user.setName(firebaseUser.getDisplayName());
                user.setEmail(firebaseUser.getEmail());
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        user.setPhone(task.getResult().getString("phone"));


                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        Toast.makeText(editProfile.this, firebaseUser.getPhotoUrl().toString(), Toast.LENGTH_LONG).show();

                        GlideApp.with(editProfile.this).load(firebaseUser.getPhotoUrl().toString()).placeholder(R.drawable.ic_add_a_photo_black_24dp).
                                into(bind.userProfilePhoto);
                        } else {
                        Toast.makeText(editProfile.this, "null" + firebaseUser.getPhotoUrl(), Toast.LENGTH_LONG).show();
                    }

                        bind.setUser(user);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(editProfile.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
                stateLoading(false);
            }
        });


        bind.userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(editProfile.this, "Permission granted", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(editProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });

        bind.saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = new User();
                user.setName(bind.name.getText().toString());
                user.setPhone(bind.phone.getText().toString());

                if (user.getName() != null && user.getPhone() != null) {


                    storeData(user);
                }
            }
            
        });
        


    }

    private void storeData(final User user) {
        UserProfileChangeRequest profileUpdates;
        if (isChanged) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())

                    .setPhotoUri(Uri.parse(mainImageURI.toString()))
                    .build();
        } else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())


                    .build();

        }

        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    storeFirestoreData(user);
                } else {
                    Toast.makeText(editProfile.this, "error in storing image", Toast.LENGTH_LONG).show();
                }
            }
        });


        stateLoading(true);



    }

    private void storeFirestoreData(User user) {

        firebaseFirestore.collection("Users").document(user_id).set(user.toUserMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    Toast.makeText(editProfile.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(editProfile.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(editProfile.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(editProfile.this);
    }


    private void stateLoading(boolean stateStatus) {
        if (stateStatus) {
            bind.ProfileLayout.setVisibility(View.GONE);
            bind.progressLoading.setVisibility(View.VISIBLE);
        } else {
            bind.ProfileLayout.setVisibility(View.VISIBLE);
            bind.progressLoading.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                bind.userProfilePhoto.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }


    }
}