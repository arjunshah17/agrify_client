package com.example.agrify.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.validators.Validator;
import com.example.agrify.R;
import com.example.agrify.activity.Utils.internetConnectionUtils;
import com.example.agrify.activity.address.addressListActivity;
import com.example.agrify.activity.model.User;
import com.example.agrify.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.Continuation;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class editProfile extends AppCompatActivity {
    private static final int SELECTED_PIC = 1;

    ActivityEditProfileBinding bind;
    User user;
    FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    Boolean isUserFirstTime=false;
    AwesomeValidation validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            bind.ProfileLayout.setBackground(this.getDrawable(R.drawable.store_item_background));//set curve background
        }
        validator=new AwesomeValidation(BASIC);
        initializeValidators();
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        this.setSupportActionBar(bind.appBar);
if(getIntent().getExtras()!=null) {
    String firstFirstTime = getIntent().getStringExtra("LoginActivity");
    if (firstFirstTime.equals("sign_in_for_first_time")) {
        isUserFirstTime = true;
        bind.appBar.setNavigationIcon(null);
        bind.appBar.setTitle("save profile");
    }
}
        firebaseUser = firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        stateLoading(true);
        firebaseFirestore.disableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {//loading data from cache
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadData(); //lo
                firebaseFirestore.enableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    //loading data from network
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
            }
        });
        bind.addressButton.setOnClickListener(v -> {

            Intent intent = new Intent(editProfile.this, addressListActivity.class);
            startActivity(intent);
            Toast.makeText(editProfile.this, "Manage Address", Toast.LENGTH_LONG).show();
        });

        bind.userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(editProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(editProfile.this, "Permission granted", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(editProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                    }
                    BringImagePicker();


                } else {

                    BringImagePicker();

                }

            }

        });

        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(editProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadData() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user.setName(firebaseUser.getDisplayName());

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                       user=task.getResult().toObject(User.class);
                        user.setEmail(firebaseUser.getEmail());

                    }
                    if (firebaseUser.getPhotoUrl() != null) {


                        GlideApp.with(editProfile.this).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.add_photo).
                                into(bind.userProfilePhoto);
                    }
                    bind.setUser(user);

                } else {
                     if(!isUserFirstTime) {
                         Toasty.error(editProfile.this, task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                     }
                }
                stateLoading(false);
            }
        });
    }


    private void storeData(final User user) {
        UserProfileChangeRequest profileUpdates;
        if (isChanged && user.getProfilePhotoUrl() != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())

                    .setPhotoUri(Uri.parse(user.getProfilePhotoUrl()))
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

        firebaseFirestore.collection("Users").document(user_id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    Toasty.success(editProfile.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(editProfile.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {


                    Toasty.error(editProfile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

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
                Log.i("onactresultresult", "mainurl:" + mainImageURI.toString());

                bind.userProfilePhoto.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(editProfile.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add the function to perform here
        if(internetConnectionUtils.isInternetConnected(getApplicationContext())) {
            if (validator.validate()) {
                saveProfile();
            }
        }
        return (true);

    }


    void saveProfile() {

           if(user.getCartSellerId()==null)
           {
               user.setCartSellerId("");
           }
            user.setName(bind.name.getText().toString());
            user.setPhone(bind.phone.getText().toString());


            UserProfileChangeRequest profileUpdates;
            if (user.getName() != null && user.getPhone() != null) {
                stateLoading(true);
                if (isChanged) {


                    File newImageFile = new File(mainImageURI.getPath());
                    try {

                        compressedImageFile = new Compressor(editProfile.this)
                                .setMaxHeight(125)
                                .setMaxWidth(125)                                             //compressing image
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 40, baos);   //converting into bitmap
                    byte[] thumbData = baos.toByteArray();

                    final StorageReference ref = storageReference.child("profile_images").child(user_id + ".jpg");
                    UploadTask image_path = ref.putBytes(thumbData);                                                         //uploading image

                    Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        //getting image reference

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                user.setProfilePhotoUrl(downloadUri.toString());

                                storeData(user);
                                Log.i("image uploaded", user.getProfilePhotoUrl());

                            } else {
                                // Handle failures
                                Toast.makeText(editProfile.this, "error on uploading image", Toast.LENGTH_LONG).show();

                                // ...
                            }
                        }
                    });
                } else {
                    storeData(user);
                }


            }


    }
    private void initializeValidators()
    {

        validator.addValidation(this,bind.name.getId(), RegexTemplate.NOT_EMPTY,R.string.username_empty);
        validator.addValidation(this,bind.phone.getId(), Patterns.PHONE,R.string.phone_error);


    }
}
