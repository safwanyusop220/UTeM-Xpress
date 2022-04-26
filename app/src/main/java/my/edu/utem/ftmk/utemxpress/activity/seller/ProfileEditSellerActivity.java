package my.edu.utem.ftmk.utemxpress.activity.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.authentication.LoginActivity;

public class ProfileEditSellerActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv;
    private EditText emailEt,nameEt,addressEt,phoneEt,shopNameEt;
    private Button updateBtn;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    private String[] cameraPermissions;
    private String[] storagePermissions;
    private String[] locationPermissions;

    private Uri image_uri;

    //progress dialog
    private ProgressDialog progressDialog;

    //firebase auth
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_seller);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIV);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        shopNameEt = findViewById(R.id.shopNameEt);
//        addressEt = findViewById(R.id.addressEt);
        updateBtn = findViewById(R.id.updateBtn);
        emailEt = findViewById(R.id.emailEt);


        //INIT permision array
//        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick image
                showImagePickDialog();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update
                inputData();
            }
        });






    }

    private String email,name,shopName,phone;
    private void inputData() {
        //input data
        name = nameEt.getText().toString().trim();
        shopName = shopNameEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();

        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("Updating Profile");
        progressDialog.show();

        if(image_uri == null){
            //update without image

            //setup
            HashMap<String, Object>hashMap = new HashMap<>();
            hashMap.put("name",""+name);
            hashMap.put("shopName",""+shopName);
            hashMap.put("phone",""+phone);
            hashMap.put("phone",""+email);

            //update to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //updated
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditSellerActivity.this, "Profile Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to update
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditSellerActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            //update with image

            //upload image first
            String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();
            //get storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded, get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //image url received

                                //setup
                                HashMap<String, Object>hashMap = new HashMap<>();
                                hashMap.put("name",""+name);
                                hashMap.put("shopName",""+shopName);
                                hashMap.put("phone",""+phone);
                                hashMap.put("phone",""+email);
                                hashMap.put("profileImage",""+downloadImageUri);


                                //update to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //updated
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditSellerActivity.this, "Profile Updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed to update
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditSellerActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileEditSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info, and set to views
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String name = ""+ds.child("name").getValue();
                            String shopName = ""+ds.child("shopName").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String deliveryFee = ""+ds.child("deliveryFee").getValue();
                            String city = ""+ds.child("city").getValue();
                            String Street = ""+ds.child("street").getValue();
                            String unit = ""+ds.child("unit").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String online = ""+ds.child("online").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String shopOpen = ""+ds.child("shopOpen").getValue();
                            String uid = ""+ds.child("uid").getValue();

                            nameEt.setText(" "+name);
                            phoneEt.setText(" "+phone);
                            shopNameEt.setText(" "+shopName);
                            emailEt.setText(" "+email);

//                            if(shopOpen.equals("true")){
//                                shopOpenSwitch.setChecked(true);
//                            }
//                            else{
//                                shopOpenSwitch.setChecked(false);
//
//                            }

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_grey).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_grey);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImagePickDialog() {
        //option to show dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //handle clicks
                        if(which==0) {
                            //camera clicks
                            if(checkCameraPermission()){
                                //camera permission allowed
                                pickFromCamera();
                            }
                            else {
                                //not allowed
                                requestCameraPermission();
                            }
                        }
                        else{
                            //gallery clicked
                            if(checkStoragePermission()){
                                //storage permission allowed
                                pickFromGallery();
                            }
                            else{
                                //not allowed
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }


    private  void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void setImagePickCameraCode(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }


    private void detectLocation() {

    }


    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==  (PackageManager.PERMISSION_GRANTED);
        return result;
    }

//    private void requestLocationPermission() {
//        ActivityCompat.requestPermissions(this,
//                locationPermission, LOCATION_REQUEST_CODE);
//    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this
                ,Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);


        return result && result1;
    }


    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions
                , CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //both permission given
                        pickFromCamera();
                    }
                    else {
                        //both or one no allowed
                        Toast.makeText(this, "Camera & Storage permission are required...", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            case  STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission granted
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(this, "Storage permission are required...", Toast.LENGTH_SHORT).show();

                    }

                }
            }


            case LOCATION_REQUEST_CODE:

            {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //permission allowed
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location permission is necessary...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //IMAGE picked from gallery

                //save picked image uri
                image_uri = data.getData();

                //set image
                profileIv.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //IMAGE PICK FROM CAMERA
                profileIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}