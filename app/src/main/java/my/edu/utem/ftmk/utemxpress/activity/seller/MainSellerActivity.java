package my.edu.utem.ftmk.utemxpress.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import my.edu.utem.ftmk.utemxpress.activity.authentication.LoginActivity;
import my.edu.utem.ftmk.utemxpress.activity.authentication.SettingsActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.MainUserActivity;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterOrderShop;
import my.edu.utem.ftmk.utemxpress.model.Constant;
import my.edu.utem.ftmk.utemxpress.model.ModelOrderShop;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;
import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductSeller;

public class MainSellerActivity extends AppCompatActivity {


    private TextView filterProductTv,nameTv, shopNameTv, emailTv, tabProductsTv, tabOrdersTv,
                        filteredOrdersTv;
    private EditText searchProductEt;
    private ImageButton logoutBtn, addProductBtn,filterProductBtn,
    editProfileBtn, filteredOrdersBtn, settingsBtn;
    private ImageView profileTv;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productsRv, ordersRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);

        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filterProductTv = findViewById(R.id.filterProductTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        shopNameTv = findViewById(R.id.shopNameTv);

        addProductBtn = findViewById(R.id.addProductBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileTv = findViewById(R.id.profileTV);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        productsRv = findViewById(R.id.productsRv);

        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        filteredOrdersBtn = findViewById(R.id.filteredOrdersBtn);
        ordersRv = findViewById(R.id.ordersRv);
        settingsBtn = findViewById(R.id.settingsBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        chekUser();
        loadAllProducts();
        showProductsUI();
        loadAllOrders();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterProductSeller.getFilter().filter(s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //go to login page
                //show delete cnmfirm dialog
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                builder.setTitle("Delete")
//                        .setMessage("Are you sure you want to Logout  ?")
//                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //delete
//                                progressDialog = ProgressDialog.show(MainSellerActivity.this,"","Logging Out Your Account", true);
//
//
//                                new Handler().postDelayed(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        makeMeOffline();
//                                    }
//                                }, 3000);
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //cancel, dismiss
//                                dialog.dismiss();
//
//                            }
//                        })
//                        .show();

//                progressDialog = ProgressDialog.show(MainSellerActivity.this,"","Logging Out Your Account", true);
//                progressDialog.setMessage("Logging Out Your Account");
//                progressDialog.show();
                makeMeOffline();

//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        makeMeOffline();
//                    }
//                }, 2000);
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                makeMeOffline();
            startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class ));

            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load product
                showProductsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load product
                showOrdersUI();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
            }
        });


        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constant.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                            //get selected item
                                String selected = Constant.productCategory1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadAllProducts();
                                }
                                else {
                                    loadFilteredProduct(selected);
                                }
                            }
                        })
                .show();
            }
        });

        filteredOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //options to display in dialog
                String[] options = {"All", "In Progress", "Completed", "Cancelled"};
                //set dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //handle item clicks
                                if (which == 0){
                                    //All clicked
                                    filteredOrdersTv.setText("Showing All Orders ");
                                    adapterOrderShop.getFilter().filter("");//show all orders

                                }
                                else {
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("Showing " +optionClicked+ " Orders ");
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });

        //start setting screen
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this, SettingsActivity.class));
            }
        });



    }

    private void loadAllOrders() {
        //init array list
        orderShopArrayList = new ArrayList<>();

        //load orders of shop
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding new data in it
                        orderShopArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            //add to list
                            orderShopArrayList.add(modelOrderShop);
                        }
                        //setup adapter
                        adapterOrderShop = new AdapterOrderShop(MainSellerActivity.this, orderShopArrayList);
                        //set adapter to recycelview
                        ordersRv.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilteredProduct(String selected) {
        productList = new ArrayList<>();
        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before get reset list
                        productList.clear();
                        for (DataSnapshot ds:dataSnapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();
                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

//                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
//                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts(){
        productList = new ArrayList<>();
        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before get reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void showProductsUI() {
        //show order ui and hide products ui
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect05_corner_radius_white);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showOrdersUI() {
        //show products ui and hide order ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));



        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect05_corner_radius_white);


    }




    private void makeMeOffline() {
        //after loggin in, make user online

        progressDialog.setMessage("Logging Out ...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "true");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        chekUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void chekUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity((new Intent(MainSellerActivity.this, LoginActivity.class)));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            //data from db
                            String name = ""+ds.child("name").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String shopName = ""+ds.child("shopName").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();

                            //set data to ui
                            nameTv.setText(name + "");
                            shopNameTv.setText(shopName);
                            emailTv.setText(email);



                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_grey).into(profileTv);

                            }
                            catch (Exception e){
                                profileTv.setImageResource(R.drawable.ic_store_grey);

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
