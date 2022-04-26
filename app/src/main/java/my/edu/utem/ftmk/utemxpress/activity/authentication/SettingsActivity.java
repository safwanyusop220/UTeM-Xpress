package my.edu.utem.ftmk.utemxpress.activity.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.model.Constant;

public class SettingsActivity extends AppCompatActivity {

    //ui views
    private SwitchCompat fcmSwitch;
    private TextView notificationStatusTv;
    private ImageButton backBtn;

    private  static final String enabledMessage = "Notifications are enabled";
    private  static final String disabledMessage = "Notifications are disabled";

    private boolean isChecked = false;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //init ui views
        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);
        backBtn = findViewById(R.id.backBtn);


        firebaseAuth = FirebaseAuth.getInstance();

        //init shared preference
        sp = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);

        //check last selected option true/false
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);

        if (isChecked){
            //was enable
            notificationStatusTv.setText(enabledMessage);
        }
        else {
            //was disable
            notificationStatusTv.setText(disabledMessage);
        }

        //handle click go back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //add Switch check change listener to enable disable notification
        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //checked, enable notification
                    subscribeToTopic();
                }
                else {
                    //unchecked, disable notification
                    unSubscribeToTopic();
                }
            }
        });

    }

    private  void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //subcribe succesfully
                        //save settings
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",true);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this, ""+enabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(enabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private  void unSubscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",false);
                        spEditor.apply();

                        Toast.makeText(SettingsActivity.this, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


}