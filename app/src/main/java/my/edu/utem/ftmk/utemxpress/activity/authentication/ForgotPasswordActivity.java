package my.edu.utem.ftmk.utemxpress.activity.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import my.edu.utem.ftmk.utemxpress.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private Button recoverBtn;
    private TextInputLayout emailEt;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    backBtn = findViewById(R.id.backBtn);
    emailEt = findViewById(R.id.emailEt);
    recoverBtn =findViewById(R.id.recoverBtn);

    firebaseAuth = FirebaseAuth.getInstance();
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please wait");
    progressDialog.setCanceledOnTouchOutside(false);

    backBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            onBackPressed();
        }
    });
    recoverBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            recoverPassword();
        }
    });


    }

    private String email;
    private void recoverPassword() {
        email = emailEt.getEditText().getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Sending instruction to reset password...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //instruction sent
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "password reset instruction sent to your email...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed sending instruction
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}