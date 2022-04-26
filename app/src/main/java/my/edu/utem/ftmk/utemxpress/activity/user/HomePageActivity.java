package my.edu.utem.ftmk.utemxpress.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.craftman.cardform.CardForm;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.paypal.checkout.PayPalCheckout;
//import com.paypal.checkout.config.CheckoutConfig;
//import com.paypal.checkout.config.Environment;
//import com.paypal.checkout.config.SettingsConfig;
//import com.paypal.checkout.createorder.CurrencyCode;
//import com.paypal.checkout.createorder.UserAction;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.authentication.CreditCardActivity;
import my.edu.utem.ftmk.utemxpress.activity.authentication.SettingsActivity;
import my.edu.utem.ftmk.utemxpress.activity.authentication.TestCreditCardActivity;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterCartItem;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductCategories;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductSellerOffer;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductUser;
import my.edu.utem.ftmk.utemxpress.adapter.SliderAdapter;
import my.edu.utem.ftmk.utemxpress.model.Constant;
import my.edu.utem.ftmk.utemxpress.model.ModelCartItem;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


public class HomePageActivity extends AppCompatActivity implements LocationListener {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    private static final String PAYPAL_CLIENT_ID = "AR9qh8XBx5bIc2zClv1uHvg6FWPvkRLbY5QCm13j_DlFABMaYD2UUxlVRKWBvngXCD35sRaw8Mj09a7n";
    private View layout;
    private ImageButton backBtn, cartBtn, locateMeBtn, filterProductBtn,settingsBtn;
    private ImageView shopIv, profileIv;
    private TextView shopNameTv, phoneTv, emailTv, openCloseTv, deliveryFeeTv, cartCountTv, callNameTv,filterProductTv;
    private RecyclerView specialOfferRv, categoryProductRv, productRv;
    private LinearLayout browseGroceriesLl;
    private EditText inputLocationEt, searchProductEt;

    private String shopUid;
    private String myLatitude, myLongitude, myPhone;
    private String shopLatitude, shopLongitude;
    private String shopName, shopEmail, shopPhone, shopOpen;



    private ProgressDialog dialog;


    //product category

    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;


    //image slider
    SliderView sliderView;
    int[] images1 = {R.drawable.slider_poster4,
            R.drawable.slider_poster2,
            R.drawable.slider_poster3,
            R.drawable.poster_slider,
    };


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //progres dialog
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private ArrayList<ModelProduct> productList1;
    private AdapterProductSellerOffer adapterProductSellerOffer;
    private AdapterProductUser adapterProductUser;


    private AdapterProductCategories adapterProductCategories;

    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;

    //permission constant
    private static final int LOCATION_REQUEST_CODE = 100;

    //permissioms array
    private String[] locationPermissions;

    private LocationManager locationManager;

    private double recentLatitude, recentLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//        categoryProductRv = findViewById(R.id.categoryProductRv);


        //init ui view

        backBtn = findViewById(R.id.backBtn);
        cartBtn = findViewById(R.id.cartBtn);
        callNameTv = findViewById(R.id.callNameTv);
        profileIv = findViewById(R.id.profileIv1);
        specialOfferRv = findViewById(R.id.specialOfferRv);
        productRv = findViewById(R.id.productRv);
        filterProductTv = findViewById(R.id.filterProductTv);
        settingsBtn = findViewById(R.id.settingsBtn);
//        categoryProductRv = findViewById(R.id.categoryProductRv);

//        shopIv = findViewById(R.id.shopIv);
//        shopNameTv = findViewById(R.id.shopNameTv);
//        phoneTv = findViewById(R.id.phoneTv);
//        emailTv = findViewById(R.id.emailTv);
//        openCloseTv = findViewById(R.id.openClosedTv);
//        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
//        browseGroceriesLl = findViewById(R.id.browseGroceriesLl);
        cartCountTv = findViewById(R.id.cartCountTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
//        reloadBtn = findViewById(R.id.reloadBtn);

        //slider
        sliderView = findViewById(R.id.shopSlider);

        SliderAdapter sliderAdapter = new SliderAdapter(images1);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();


        //init permissions array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};


        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //product category
        titles = new ArrayList<>();
        images = new ArrayList<>();

//        titles.add("Groceries");
//        titles.add("Drinks");
//        titles.add("Frozen");
//        titles.add("Health");
//        titles.add("Bakery");
//        titles.add("Household");
//
//        images.add(R.drawable.groceries_icon);
//        images.add(R.drawable.drinks_icon);
//        images.add(R.drawable.frozen_icon);
//        images.add(R.drawable.health_icon);
//        images.add(R.drawable.bakery_icon);
//        images.add(R.drawable.household1_icon);
//
//        adapterProductCategories = new AdapterProductCategories(this, titles, images);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
//        categoryProductRv.setLayoutManager(gridLayoutManager);
//        categoryProductRv.setAdapter(adapterProductCategories);
//
////        categoryProductRv.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                onBackPressed();
////            }
////        });
//        categoryProductRv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //go to reload page
//                startActivity(new Intent(HomePageActivity.this, SelectedCategoryProductActivity.class));
//
//            }
//        });

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterProductUser.getFilter().filter(s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(HomePageActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constant.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //get selected item
                                String selected = Constant.productCategory1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadShopProduct();
                                }
                                else {
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();

            }
        });
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick image
                startActivity(new Intent(HomePageActivity.this, ProfileEditUserActivity.class));
            }
        });




        //get uid shop
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();


        loadShopProductOffer();
        loadShopProduct();
        loadMyInfo();
        loadShopDetails();



        easyDB = EasyDB.init(this, "ITEMS_DB2") // TEST is the name of the DATABASE
                .setTableName("ITEMS_TABLE")  // You can ignore this line if you want
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Picture", new String[]{"text", "not null"}))
//                .addColumn(new Column("Item_PaymentState", new String[]{"text", "not null"}))
                .doneTableColumn();


        //each shop have its own product and order so if user add to cart and go back and open cart in different shop then cart should be different

        //so delete cart data whenever user open this activity
        deleteCartData();
        cartCount();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show cart dialog
                showCartDialog();

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, SettingsActivity.class));
            }
        });

//        reloadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //go to reload page
//                startActivity(new Intent(HomePageActivity.this, ReloadEwalletActivity.class));
//
//            }
//        });



    }

    private void loadShopProduct() {
        //init List
        productList1 = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot DataSnapshot) {
                        //clear list before editing items
                        productList1.clear();
                        for(DataSnapshot ds: DataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList1.add(modelProduct);
                        }
                        //setup data
                        adapterProductUser = new AdapterProductUser(HomePageActivity.this,productList1);
                        Collections.sort(productList1, ModelProduct.ModelProductNameAZComparator);
//                        adapterProductUser.notifyDataSetChanged();
                        //set adapter
                        productRv.setAdapter(adapterProductUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();

                            String profileImage = "" + ds.child("profileImage").getValue();
                            myLatitude = "" + ds.child("latitude").getValue();
                            myLongitude = "" + ds.child("longitude").getValue();

                            callNameTv.setText("Hi " + name);


                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_double_person_grey).into(profileIv);

                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_person_grey);

                            }
                            //load only those shops that in campus

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private  void payment(){

//        View view = LayoutInflater.from(this).inflate(R.layout.row_credit_card, null);
//        layout = view;

//        cardForm=(CardForm)findViewById(R.id.creditCard);
//        TextView textDes = (TextView)findViewById(R.id.payment_amount);
//        Button btnPay = (Button)findViewById(R.id.btn_pay);
//
//        textDes.setText("RM110");
//        btnPay.setText(String.format("Payer Name is:", textDes.getText()));



    }

    private void deleteCartData() {


        easyDB.deleteAllDataFromTable();//delete all record from cart

    }

    public void cartCount() {

        //keep it public so we can access in adapter
        //get cart count
        int count = easyDB.getAllData().getCount();
        if (count <= 0) {
            //no item
            cartCountTv.setVisibility(View.GONE);
        } else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText("" +count);
            //concamate with string, because we cant set integer to textview
        }


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
            addresses = geocoder.getFromLocation(recentLatitude,recentLongitude, 1);

            String address = addresses.get(0).getAddressLine(0);//complete address

            //set address
            ((TextView )layout.findViewById(R.id.inputLocationEt)).setText(address);
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
        recentLatitude = location.getLatitude();
        recentLongitude = location.getLongitude();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permissiom allowes
                        detectLocation();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this, "Location permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public double allTotalPrice = 0.00;
    public double sTotal = 0.00;
    public double deliveryFee = 2.00;
    //switch choice
//    public SwitchCompat switchChoice;
//    public EditText inputLocationEt;
//    public Button locateMeBtn;
    //need to access these views in adapter so making public
    public TextView sTotalTv, dFeeTv, allTotalPriceTv, TotalLabelTv;

    private void showCartDialog() {


        //init list
        cartItemList = new ArrayList<ModelCartItem>();

        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.row_dialog_cart, null);
        layout = view;

        //init views
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        TotalLabelTv = view.findViewById(R.id.TotalLabelTv);
        allTotalPriceTv = view.findViewById(R.id.allTotalPriceTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);
        Button paymentBtn = view.findViewById(R.id.paymentBtn);

        //switch choice
//        Switch switchChoice = view.findViewById(R.id.switchChoice1);
        RelativeLayout RlPickup = view.findViewById(R.id.RlPickup);
        TextView inputLocationEt = view.findViewById(R.id.inputLocationEt);
        Button locateMeBtn = view.findViewById(R.id.locateMeBtn);

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);
        shopNameTv.setText(shopName);


        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB2") // TEST is the name of the DATABASE
                .setTableName("ITEMS_TABLE")  // You can ignore this line if you want
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Picture", new String[]{"text", "not null"}))
//                .addColumn(new Column("Item_PaymentState", new String[]{"text", "not null"}))

                .doneTableColumn();

        //get all record from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);
            String productIcon = res.getString(7);
//            String paymentState = res.getString(8);
            Log.e(productIcon, "showCartDialog: ");
            String icon = "";
//            databaseReference.child("")


            allTotalPrice = allTotalPrice + Double.parseDouble(cost) + 2.00;
            sTotal = allTotalPrice - 2.00;

            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    "" + pId,
                    "" + name,
                    "" + price,
                    "" + cost,
                    "" + quantity,
                    "" + productIcon
//                    "" + paymentState
            );

            cartItemList.add(modelCartItem);

        }
        //setup adapter
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        //set to recycleview
        cartItemsRv.setAdapter(adapterCartItem);

        dFeeTv.setText("RM" + String.format("%.2f", deliveryFee));
        sTotalTv.setText("RM" + String.format("%.2f", sTotal));
//        allTotalPriceTv.setText("RM"+(allTotalPrice+ Double.parseDouble(deliveryFee.replace("RM",""))));
        allTotalPriceTv.setText("RM" + String.format("%.2f", allTotalPrice));

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //reset total price
        dialog.setOnCancelListener((dialogInterface -> {
            allTotalPrice = 0.00;
            sTotal = 0.00;

        }));
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//
//                allTotalPrice = 0.00;
//            }
//        });



        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    paymentBtn.setClickable(true);
//                    checkoutBtn.setClickable(false);


                String strLocation = inputLocationEt.getText().toString();

                if(TextUtils.isEmpty(strLocation)) {
                    inputLocationEt.setError("Click LOCATE ME button below...");
                    Toast.makeText(HomePageActivity.this, "please enter your address in your profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (inputLocationEt.equals("")|| myLatitude.equals("nulll")||myLongitude.equals("")|| myLongitude.equals("nulll")){
//                    Toast.makeText(HomePageActivity.this, "please enter your address in your profile before placing order...", Toast.LENGTH_SHORT).show();
//                    return;//dont go futher
//                }

//                if (myPhone.equals("")|| myPhone.equals("nulll")){
//                    Toast.makeText(HomePageActivity.this, "please enter your phone number before placing order...", Toast.LENGTH_SHORT).show();
//                    return;//dont go futher
//                }

                if (cartItemList.size() == 0){
                    //cart list empty
                    Toast.makeText(HomePageActivity.this,"no item in cart", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(HomePageActivity.this, CreditCardActivity.class));


            }
        });


        //place order
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                paymentBtn.setClickable(false);
//                checkoutBtn.setClickable(true);
//                first validate address
//                startActivity(new Intent(HomePageActivity.this, CreditCardActivity.class));

                        // str will be trimmed text
                        // Do your work here


//                if (inputLocationEt.matches("")|| inputLocationEt.equals("nulll")){
//                    Toast.makeText(HomePageActivity.this, "please enter your address  before placing order...", Toast.LENGTH_SHORT).show();
//                    return;//dont go futher
//                }

                String strLocation = inputLocationEt.getText().toString();

                if(TextUtils.isEmpty(strLocation)) {
                    inputLocationEt.setError("Click LOCATE ME button below...");
                    Toast.makeText(HomePageActivity.this, "please enter your address in your profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (inputLocationEt.equals("")|| myLatitude.equals("nulll")||myLongitude.equals("")|| myLongitude.equals("nulll")){
//                    Toast.makeText(HomePageActivity.this, "please enter your address in your profile before placing order...", Toast.LENGTH_SHORT).show();
//                    return;//dont go futher
//                }

//                if (myPhone.equals("")|| myPhone.equals("nulll")){
//                    Toast.makeText(HomePageActivity.this, "please enter your phone number before placing order...", Toast.LENGTH_SHORT).show();
//                    return;//dont go futher
//                }

                if (cartItemList.size() == 0){
                    //cart list empty
                    Toast.makeText(HomePageActivity.this,"no item in cart", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitOrder();




            }
        });


//        switchChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    inputLocationEt.setVisibility(View.VISIBLE);
//                    locateMeBtn.setVisibility(View.VISIBLE);
//                    RlPickup.setVisibility(View.GONE);
//
//                } else {
//                    inputLocationEt.setVisibility(View.GONE);
//                    locateMeBtn.setVisibility(View.GONE);
//                    RlPickup.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });

        locateMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputLocationEt.setError(null);

                //detect current location
                if (checkLocationPermission()) {

                    //already allowed
                    progressDialog = ProgressDialog.show(HomePageActivity.this,"","Please wait ", true);
                    detectLocation();



                } else {
                    //not allowed, request
                    requestLocationPermission();
                }
            }
        });


    }

    private void submitOrder() {


//        payment();


        //show progress dialog
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//
//            }
//        }, 3000);
//        progressDialog = ProgressDialog.show(HomePageActivity.this,"","Placing order confirmation", true);
        progressDialog.setMessage("Placing order confirmation");
        progressDialog.show();



        //for order id and order time
        String timestamp = "" + System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("RM", "");//remove rm is contains


//        //paypal
//        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(cost),
//                "RM",
//                "UTeM Xpress Application",
//                PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
//        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
//




        //add latitude longitude to user to each order|delete previous order from firebase or add manually to them


        //setup order data
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress");//in progress/complete/canceled
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);
        hashMap.put("recentLatitude", "" + recentLatitude);
        hashMap.put("recentLongitude", "" + recentLongitude);
        hashMap.put("paymentState", "Approved");
//        hashMap.put("address", "" + address);

        //add to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //order info added now add order item
                        for (int i = 0; i < cartItemList.size(); i++) {

                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();
                            String name = cartItemList.get(i).getName();
                            String productIcon = cartItemList.get(i).getProductIcon();
                            Log.e(productIcon, "onSuccess: " );


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);
                            hashMap1.put("productIcon", productIcon);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(HomePageActivity.this, "Order Successfully Place", Toast.LENGTH_SHORT).show();


                        prepareNotificationMessage(timestamp);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 4000);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //faild placing order
                        progressDialog.dismiss();
                        Toast.makeText(HomePageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PAYPAL_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirmation != null) {
//                    try {
//                        String paymentDetail = confirmation.toJSONObject().toString(4);
//                        JSONObject jsonObject = new JSONObject(paymentDetail);
//
//                        jsonObject.getJSONObject("response").getString("state");
//
//                        Toast.makeText(this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
//                        finish();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//            }
//            else if(resultCode == Activity.RESULT_CANCELED)
//                Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
//            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
//                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
//        }
//    }


//    private void loadMyInfo() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            String name = ""+ds.child("name").getValue();
//                            String email = ""+ds.child("email").getValue();
//                            myPhone = ""+ds.child("phone").getValue();
//
//                            String accountType = ""+ds.child("accountType").getValue();
//                            String profileImage = ""+ds.child("profileImage").getValue();
//                            String city = ""+ds.child("city").getValue();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//    }

    private void loadShopProductOffer() {
        productList = new ArrayList<>();
        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("products")
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
                        adapterProductSellerOffer = new AdapterProductSellerOffer(HomePageActivity.this, productList);
                        //set adapter
                        Collections.sort(productList, ModelProduct.ModelProductNameZAComparator);
                        adapterProductSellerOffer.notifyDataSetChanged();
                        specialOfferRv.setAdapter(adapterProductSellerOffer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get shop data

                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                 shopEmail = ""+dataSnapshot.child("shopEmail").getValue();
                 shopPhone = ""+dataSnapshot.child("shopPhone").getValue();
                String deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                String profileImage= ""+dataSnapshot.child("profileImage").getValue();
                shopLatitude = ""+dataSnapshot.child("latitude").getValue();
                shopLongitude = ""+dataSnapshot.child("longitude").getValue();

                //set Data
//                shopNameTv.setText(shopName);
//                emailTv.setText(shopEmail);
//                deliveryFeeTv.setText(deliveryFee);
                //phoneTv.setText(ShopPhone);

//                if(shopOpen.equals("true")){
//                    openCloseTv.setText("open");
//                }
//                else {
//                    openCloseTv.setText("closed");
//                }
//                try {
//                    Picasso.get().load(profileImage).into(shopIv);
//                }
//                catch (Exception e){
//
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void loadAllProducts(){
    }

    private  void prepareNotificationMessage(String orderId){
        //when user places order, send notification to seller

        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" +Constant.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order "+orderId;
        String NOTIFICATION_MESSAGE = "Congratulations...! you have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        //prepare json (what to send and where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid", shopUid);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);


        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        sendFcmNotification(notificationJo,orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, final String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(" user success", "getHeaders: " );
                //after sending fcm start order details activity
                //after place order open order details page
                //open order details, we need to keys there, orderId,orderTO
                Intent intent = new Intent(HomePageActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);//now get these value through intent on orderDetailsUsersActivity

//                Toast.makeText(HomePageActivity.this, "successful get noti", Toast.LENGTH_SHORT).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(" user fail", "getHeaders: " );

                //if failed sending fcm
                Intent intent = new Intent(HomePageActivity.this, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);//now get these value through intent on orderDetailsUsersActivity

//                Toast.makeText(HomePageActivity.this, "fail to get noti", Toast.LENGTH_SHORT).show();


            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Log.e(" header side", "getHeaders: " );
                //put required header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key="+ Constant.FCM_KEY);



//                return super.getHeaders();
                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


//    @Override
//    public void onLocationChanged(@NonNull List<Location> locations) {
//
//    }

//    @Override
//    public void onFlushComplete(int requestCode) {
//
//    }


}