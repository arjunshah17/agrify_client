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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;



public class editProfile extends AppCompatActivity {
    private static final int SELECTED_PIC = 1;
    ImageView imageView;
    ActivityEditProfileBinding bind;
    User user;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        stateLoading(true);
        bind.progressLoading.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        user.setName(task.getResult().getString("name"));
                        user.setPhone(task.getResult().getString("phone"));
                        user.setUserProfilePhoto(task.getResult().getString("userProfilePhoto"));
                        user.setEmail(firebaseAuth.getCurrentUser().getEmail());

                        if (user.getUserProfilePhoto()!=null) {
                            Toast.makeText(editProfile.this,user.getUserProfilePhoto(),Toast.LENGTH_LONG).show();
                            mainImageURI = Uri.parse(user.getUserProfilePhoto());

                            RequestOptions placeholderRequest = new RequestOptions();


                            Glide.with(bind.userProfilePhoto.getContext()).load(user.getUserProfilePhoto()).into(bind.userProfilePhoto);
                        }
                        bind.setUser(user);
                    }
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

                        Toast.makeText(editProfile.this, "Permission Denied", Toast.LENGTH_LONG).show();
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
                final User user = bind.getUser();
                if (isChanged) {
                    File newImageFile = new File(mainImageURI.getPath());
                    try {

                        compressedImageFile = new Compressor(editProfile.this)
                                .setMaxHeight(125)
                                .setMaxWidth(125)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumbData = baos.toByteArray();
                    UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                    image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                storeFirestore(task,user);

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(editProfile.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                               

                            }
                        }
                    });

                }
            }
            
        });
        


    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, User user) {
        Uri download_uri;
        if(task != null) {

            download_uri = task.getResult().getUploadSessionUri();

        } else {

            download_uri = mainImageURI;

        }
        user.setUserProfilePhoto(download_uri.toString());
        firebaseFirestore.collection("Users").document(user_id).set(user.toUserMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){

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

    public void changeprofile(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_PIC);
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