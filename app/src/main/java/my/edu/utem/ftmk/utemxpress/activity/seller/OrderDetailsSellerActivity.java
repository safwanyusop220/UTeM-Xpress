package my.edu.utem.ftmk.utemxpress.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.OrderDetailsUsersActivity;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterOrderedItem;
import my.edu.utem.ftmk.utemxpress.model.Constant;
import my.edu.utem.ftmk.utemxpress.model.ModelOrderedItem;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    //ui views
    private ImageButton backBtn, editBtn, mapBtn;
    private TextView orderIdTv, dateTv, orderStatusTv, emailTv,
            phoneTv, totalItemsTv, amountTv, addressTv;
    private RecyclerView itemsRv;


    String orderId, orderBy;

    String sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        //init ui views
        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        mapBtn = findViewById(R.id.mapBtn);
        addressTv = findViewById(R.id.addressTv);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        itemsRv = findViewById(R.id.itemsRv);



        //get data from intent
        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");

        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadBuyerInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edit order status; in progress, complete, cancelled
                editOrderStatusDialog();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to map for delivery
                openMap();
            }
        });

    }

    private void openMap(){
        //saddr means source addres
        //daddr means destination address
        String address = "https://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }



    private void editOrderStatusDialog() {
        //options to display
        final String[] options = {"In Progress", "Completed", "Cancelled"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //hndle item click
                        String selectedOptions = options[i];
                        editOrderStatus(selectedOptions);
                    }
                })
                .show();


    }

    private void editOrderStatus(final String selectedOption) {
        //setup data to put in firebase db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+selectedOption);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    String message =  "Order is Now "+selectedOption;
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(OrderDetailsSellerActivity.this, message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(orderId, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



    }

    private void loadOrderDetails() {
        //load detailed info of this order, based on order id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get order info
                        String orderBy = ""+snapshot.child("orderBy").getValue();
                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        String orderId = ""+snapshot.child("orderId").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTime = ""+snapshot.child("orderTime").getValue();
                        String orderTo = ""+snapshot.child("orderTo").getValue();
                        String recentLatitude = ""+snapshot.child("recentLatitude").getValue();
                        String recentLongitude = ""+snapshot.child("recentLongitude").getValue();

//                        String orderCost = ""+snapshot.child("orderCost").getValue();
//                        String orderCost = ""+snapshot.child("orderCost").getValue();
//                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        destinationLatitude = ""+snapshot.child("recentLatitude").getValue();
                        destinationLongitude = ""+snapshot.child("recentLongitude").getValue();
                        // convert timestamp to proper format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();


                        if(orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.blue02_login));

                        }
                        else if(orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.green));

                        }
                        else if(orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));

                        }

                        //set Data
                        orderIdTv.setText(": "+orderId);
                        orderStatusTv.setText(": "+orderStatus);
                        amountTv.setText(": "+"RM"+orderCost);
                        dateTv.setText(": "+formatedDate);
                        //dateeee

                        findAddress(recentLatitude, recentLongitude); //find delivery


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findAddress(String recentLatitude, String recentLongitude) {
        double lat = Double.parseDouble(recentLatitude);
        double lon = Double.parseDouble(recentLongitude);


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            //complete
            String address = addresses.get(0).getAddressLine(0);
            addressTv.setText(": "+address);

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sourceLatitude = ""+snapshot.child("latitude").getValue();
                        sourceLongitude = ""+snapshot.child("longitude").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBuyerInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get buyer info
//                        destinationLatitude = ""+snapshot.child("recentLatitude").getValue();
//                        destinationLongitude = ""+snapshot.child("recentLongitude").getValue();
                        String email = ""+snapshot.child("email").getValue();
                        String phone = ""+snapshot.child("phone").getValue();

                        //set info
                        emailTv.setText(": "+email);
                        phoneTv.setText(": 0"+phone);

//                        addressTv.setText();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderedItems(){
        //load the product item of order

        //init list
        orderedItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            //add to list
                            orderedItemArrayList.add(modelOrderedItem);
                        }
                        //setup adapter
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSellerActivity.this, orderedItemArrayList);
                        //set Adapter to our recycleView
                        itemsRv.setAdapter(adapterOrderedItem);

                        //set total number of item/product in order
                        totalItemsTv.setText(": "+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private  void prepareNotificationMessage(String orderId, String message){
        //when seller change order status InProgress/Canceled/completed, send notification to buyer


        //prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/=" + Constant.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order "+ orderId;
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        //prepare json (what to send and where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",orderBy);
            notificationBodyJo.put("sellerUid", firebaseAuth.getUid());
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

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Toast.makeText(OrderDetailsSellerActivity.this, "successful get noti", Toast.LENGTH_SHORT).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(OrderDetailsSellerActivity.this, "failed get noti", Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                //put required header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key="+ Constant.FCM_KEY);
                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "key=AAAACXxplBw:APA91bHczkLrXcL_mKxUTRl-1vE16dYWBqEwFRhuqVqGWfsle1JUSkOmP6sddx5If6_TPfgMm6AFRqWmU78MkucqxF79EbAtsbniaQlQZi72gl1CdRaMP0StOng6H95_SE7N_Jf-6Af4");



                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


}