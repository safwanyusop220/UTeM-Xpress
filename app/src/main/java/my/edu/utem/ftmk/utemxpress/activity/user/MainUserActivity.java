package my.edu.utem.ftmk.utemxpress.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.authentication.LoginActivity;
import my.edu.utem.ftmk.utemxpress.activity.authentication.SettingsActivity;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterOrderUser;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductSeller;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterShop;
import my.edu.utem.ftmk.utemxpress.model.ModelOrderUser;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;
import my.edu.utem.ftmk.utemxpress.model.ModelShop;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, tabShopTv, tabOrderTv,phoneTv;
    private ImageButton logoutBtn,editProfileBtn, settingsBtn;
    private RelativeLayout shopRl, orderRl;
    private ImageView profileTv, nextIv;
    private RecyclerView shopRv,ordersRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> orderList;
    private AdapterOrderUser adapterOrderUser;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);




        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        tabShopTv = findViewById(R.id.tabShopTv);
        tabOrderTv = findViewById(R.id.tabOrderTv);
        nextIv = findViewById(R.id.nextIv);

        logoutBtn = findViewById(R.id.logoutBtn);
        profileTv = findViewById(R.id.profileTv);
        shopRl = findViewById(R.id.shopRl);
        shopRv = findViewById(R.id.shopRv);
        orderRl = findViewById(R.id.orderRl);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        ordersRv = findViewById(R.id.ordersRv);
        settingsBtn = findViewById(R.id.settingsBtn);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        chekUser();

        //at start show shop ui
        showOrderUI();
        showShopUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //go to login page
                makeMeOffline();
            }
        });


        tabShopTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show shops
                showShopUI();
            }
        });

        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show order
                showOrderUI();
            }
        });
//        editProfileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startActivity(new Intent(MainUserActivity.this, MainSellerActivity.class));
//            }
//        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
            }
        });

        //start setting screen
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, SettingsActivity.class));
            }
        });


    }

    private void showShopUI() {
        //show shop ui, hide order ui
        shopRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);

        tabShopTv.setTextColor(getResources().getColor(R.color.white));
        tabShopTv.setBackgroundResource(R.drawable.shape_btn_login_blue);
        tabOrderTv.setBackgroundResource(R.drawable.shape_btn_grey);



        tabOrderTv.setTextColor(getResources().getColor(R.color.grey));
//        tabOrderTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrderUI() {

        //show order ui, hide shop ui
        orderRl.setVisibility(View.VISIBLE);
        shopRl.setVisibility(View.GONE);

        tabOrderTv.setTextColor(getResources().getColor(R.color.white));
        tabOrderTv.setBackgroundResource(R.drawable.shape_btn_login_blue);
        tabShopTv.setBackgroundResource(R.drawable.shape_btn_grey);

        tabShopTv.setTextColor(getResources().getColor(R.color.grey));
//        tabShopTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void chekUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity((new Intent(MainUserActivity.this, LoginActivity.class)));
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
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();

                            String accountType = ""+ds.child("accountType").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String city = ""+ds.child("city").getValue();

                            nameTv.setText(name );
                            emailTv.setText(email);
                            phoneTv.setText("0"+phone);



                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_grey).into(profileTv);

                            }
                            catch (Exception e){
                                profileTv.setImageResource(R.drawable.ic_store_grey);

                            }
                            //load only those shops that in campus
                            loadOrders();
                            loadShops();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadOrders() {
        //init order list
        orderList = new ArrayList<>();

        //get Orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            //add to list
                                            orderList.add(modelOrderUser);
                                        }
                                        //setup adapter
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, orderList);
                                        //set recycle view
                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void loadShops() {

        //init list
        shopList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //clear list
                        shopList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            //city shops
                            String shopCity = ""+ds.child("address").getValue();
                            shopList.add(modelShop);

//
//                            if(shopCity.equals(myCity)){
//                                shopList.add(modelShop);
//                            }
//                            //if want to display all shops,skip the if statement//shoplist.add(modelShop)





                        }


                        //setup adapter
                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
                        shopRv.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}