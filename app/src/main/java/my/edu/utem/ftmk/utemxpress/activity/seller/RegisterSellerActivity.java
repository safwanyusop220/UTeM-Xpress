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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;

public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {

    private View layout;
    private ImageView profileIv;
    private TextInputLayout nameEt,phoneEt,emailEt,passwordEt,confirmPasswordEt,shopNameEt;

    private EditText  deliveryFeeEt,
            addressEt, streetEt, unitEt;
    private Button registerBtn;
    private ImageButton backBtn;
    private Button gpsBtn;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission arrays
    private String[] locationPermission;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image Uri
    private Uri image_uri;


    //permissioms array
    private String[] locationPermissions;
    private LocationManager locationManager;

    private double latitude, longitude;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIV);
        nameEt = findViewById(R.id.nameEt);
        shopNameEt = findViewById(R.id.shopNameEt);
        phoneEt = findViewById(R.id.phoneEt);
//        deliveryFeeEt = findViewById(R.id.deliveryFeeEt);

//        streetEt = findViewById(R.id.streetEt);
//        unitEt = findViewById(R.id.unitEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        registerBtn = findViewById(R.id.registerBtn);
        gpsBtn = findViewById(R.id.gpsBtn);
        addressEt = findViewById(R.id.addressEt);



        //init permission array
        locationPermission = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        cameraPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        storagePermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //init permissions array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};



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

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register user
                inputData();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detect current location
                if (checkLocationPermission()) {
                    //already allowed
                    progressDialog = ProgressDialog.show(RegisterSellerActivity.this,"","Please wait ", true);
                    detectLocation();
                } else {
                    //not allowed, request
                    requestLocationPermission();
                }
            }
        });

    }

    private String fullName, shopName, phoneNumber, deliveryFee, address,
            street, unit, email, password, confirmPassword;

    private void inputData() {
        //input data
        fullName = nameEt.getEditText().getText().toString().trim();
        shopName = shopNameEt.getEditText().getText().toString().trim();
        phoneNumber = phoneEt.getEditText().getText().toString().trim();
//        deliveryFee = deliveryFeeEt.getText().toString().trim();
        address = addressEt.getText().toString().trim();
//        street = streetEt.getText().toString().trim();
//        unit = unitEt.getText().toString().trim();
        email = emailEt.getEditText().getText().toString().trim();
        password = passwordEt.getEditText().getText().toString().trim();
        confirmPassword = confirmPasswordEt.getEditText().getText().toString().trim();


        //validate data
        if (TextUtils.isEmpty(fullName)) {
            nameEt.setError("Insert Name...");
            Toast.makeText(this, "Insert Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(shopName)) {
            shopNameEt.setError("Insert ShopName...");
            Toast.makeText(this, "Insert Shop Name...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Click the Locate me button...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneEt.setError("Insert Phone Number...");
            Toast.makeText(this, "Insert Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (TextUtils.isEmpty(deliveryFee)) {
//            Toast.makeText(this, "Enter Delivery fee...", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");

        final String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            //save info without image

            //setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("email", "" + email);
            hashMap.put("name", "" + fullName);
            hashMap.put("shopName", "" + shopName);
            hashMap.put("phone", "" + phoneNumber);
//            hashMap.put("deliveryFee", "" + deliveryFee);
            hashMap.put("address", "" + address);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);

            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType","Seller");
            hashMap.put("online","true");
            hashMap.put("shopOpen", "true");
            hashMap.put("profileImage", "");

            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //db updated
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed Update db
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                            finish();
                        }
                    });
        } else {
            //save info with image

            //name and path of image

            String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                //setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" + firebaseAuth.getUid());
                                hashMap.put("email", "" + email);
                                hashMap.put("name", "" + fullName);
                                hashMap.put("shopName", "" + shopName);
                                hashMap.put("phone", "" + phoneNumber);
//                                hashMap.put("deliveryFee", "" + deliveryFee);
                                hashMap.put("address", "" + address);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "Seller");
                                hashMap.put("online", "true");
                                hashMap.put("shopOpen", "true");
                                hashMap.put("profileImage", "" + downloadImageUri);//url of uploaded image


                                //save to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //db updated
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed Update db
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                                                finish();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    });
        }
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


    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private void findAddress() {
        //find address, country, state, city
        Geocoder geocoder;

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);

            String address = addresses.get(0).getAddressLine(0);//complete address

            //set address
            ((EditText )findViewById(R.id.addressEt)).setText(address);
            progressDialog.dismiss();

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private  boolean checkLocationPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disabled
        Toast.makeText(this, "Please turn on location...", Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case LOCATION_REQUEST_CODE:{
//                if (grantResults.length>0){
//                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (locationAccepted){
//                        //permissiom allowes
//                        detectLocation();
//                    }
//                    else{
//                        //permission denied
//                        Toast.makeText(this, "Location permission is necessary...", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }




    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
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


    private void setImagePickCameraCode(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }


//    private void detectLocation() {
//
//    }


//    private boolean checkLocationPermission() {
//        boolean result = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) ==  (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }

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
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
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
            //break;
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

