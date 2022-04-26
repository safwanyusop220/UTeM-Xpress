package my.edu.utem.ftmk.utemxpress.activity.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.seller.MainSellerActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.MainUserActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make full screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);




        firebaseAuth = FirebaseAuth.getInstance();
        System.out.println("onCreate");
        //start login activity after 2sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //User not logged in start Login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    System.out.println("User >> " + user.getEmail());
                    //user is logger in, check user
                    checkUserType();
                }

            }
        }, 1000);



    }
    //bottom navigation


    private void checkUserType() {
            //if user seller, start seller main screen
            //if user is buyer, start user main screen

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child((firebaseAuth.getUid()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String accountType = "" + dataSnapshot.child("accountType").getValue();
                            System.out.println("accountType >> " + accountType);
                            if (accountType.equals("Seller")) {
                                //user is seller
                                startActivity(new Intent(MainActivity.this, MainSellerActivity.class));
                                finish();
                            } else {
                                //user is buyer
                                startActivity(new Intent(MainActivity.this, MainUserActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
    }