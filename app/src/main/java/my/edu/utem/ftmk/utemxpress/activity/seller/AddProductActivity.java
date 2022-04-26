package my.edu.utem.ftmk.utemxpress.activity.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import my.edu.utem.ftmk.utemxpress.model.Constant;
import my.edu.utem.ftmk.utemxpress.R;

public class AddProductActivity extends AppCompatActivity {

    //ui views
    private ImageButton backBtn;
    private Button registerProductBtn;
    private ImageView productIconIv;
    private EditText titleEt, descriptionEt, quantityEt, priceEt, discountedPriceEt,
            discountedNoteEt;
    private TextView categoryTv;
    private SwitchCompat discountSwitch;


    //all permission
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //init ui view
        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        registerProductBtn = findViewById(R.id.registerProductBtn);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        discountedPriceEt = findViewById(R.id.discountedPriceEt);
        discountedNoteEt = findViewById(R.id.discountedNoteEt);
        categoryTv = findViewById(R.id.categoryTv);
        discountSwitch = findViewById(R.id.discountSwitch);

        //not checked
        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);


        firebaseAuth = FirebaseAuth.getInstance();

        //SETUP progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //initialize permission
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheked) {
                if(isCheked){
                    //checked
                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountedNoteEt.setVisibility(View.VISIBLE);

                }
                else{
                    //not checked
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);


                }
            }
        });

        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog
                showImagePickDialog();
            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick category
                categoryDialog();
            }
        });

        registerProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Flow
                //1) input
                //2) validate
                //3)add data to db
                inputData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String productTitle, productDescription, productCategory, productQuantity,
    originalPrice, discountPrice, discountNote;
    private boolean discountAvailable = false;
    private void inputData() {
        //input data
        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = quantityEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();

        //validate Data
        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "category is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable){
            discountPrice = discountedPriceEt.getText().toString().trim();
            discountNote = discountedNoteEt.getText().toString().trim()+"% OFF";
            if (TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "price is required...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            discountPrice = "0";
            discountNote = "";
        }
        addProduct();
    }

    private void addProduct() {

        //add to database
        progressDialog.setMessage("Adding Product");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();

        if(image_uri == null){
            //without image

            //setup data to upload
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productId", ""+timestamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription",""+productDescription);
            hashMap.put("productCategory",""+productCategory);
            hashMap.put("productQuantity",""+productQuantity);
            hashMap.put("productIcon","");//no image
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("uid",""+firebaseAuth.getUid());

            //add to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
        else {
            //with image

            //name and path of image to be uploaded
            String filePathAndName = "product_images/" + "" +timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploaded
                            Task<Uri> uriTask  = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                                Uri downloadImageUri = uriTask.getResult();

                                if(uriTask.isSuccessful()){
                                    //url of image received
                                    //setup data to upload
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("productId", ""+timestamp);
                                    hashMap.put("productTitle", ""+productTitle);
                                    hashMap.put("productDescription",""+productDescription);
                                    hashMap.put("productCategory",""+productCategory);
                                    hashMap.put("productQuantity",""+productQuantity);
                                    hashMap.put("productIcon",""+downloadImageUri);//url of uploaded
                                    hashMap.put("originalPrice",""+originalPrice);
                                    hashMap.put("discountPrice",""+discountPrice);
                                    hashMap.put("discountNote",""+discountNote);
                                    hashMap.put("discountAvailable",""+discountAvailable);
                                    hashMap.put("timestamp",""+timestamp);
                                    hashMap.put("uid",""+firebaseAuth.getUid());

                                    //add to db
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                    reference.child(firebaseAuth.getUid()).child("products").child(timestamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //added to db
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AddProductActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                                                    clearData();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //failed
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AddProductActivity.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }

    private void clearData() {
        //clear
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        discountedPriceEt.setText("");
        discountedNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_aa_shopping_blue);
        image_uri = null;
    }



    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constant.productCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                       String category = Constant.productCategory[which];


                       //set pick category
                       categoryTv.setText(category);
                    }
                })
                .show();
    }


    private void showImagePickDialog() {
        String[] options ={"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which==0) {
                            //camera clicked
                            if (checkStoragePermission()){
                                //permission granted
                                pickFromCamera();
                            }
                            else {
                                //permission not given
                                requestCameraPermission();
                            }

                        }
                        else {
                            //gallery Clicked
                            if (checkStoragePermission()) {
                                //permission granted
                                pickFromGallery();
                            }
                            else
                            {
                                //permission not given
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery(){
        //intent picture from gallery
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
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private  void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
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
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //handle image pick result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //IMAGE picked from gallery

                //save picked image uri
                image_uri = data.getData();

                //set image
                productIconIv.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //IMAGE PICK FROM CAMERA
                productIconIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}