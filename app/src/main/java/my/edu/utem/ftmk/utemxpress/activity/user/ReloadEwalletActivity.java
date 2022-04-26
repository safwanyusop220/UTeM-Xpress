package my.edu.utem.ftmk.utemxpress.activity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterTopupValue;
import my.edu.utem.ftmk.utemxpress.model.ModelTopupValue;

public class ReloadEwalletActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView cardTv,bankingTv,price100,price50,price30,price10;
    private EditText topupEt;
    private Button topupBtn,valueButton11;
    public TextView eWalletBalance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_ewallet);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        backBtn = findViewById(R.id.backBtn);
        eWalletBalance = findViewById(R.id.eWalletBalance);
        cardTv = findViewById(R.id.cardTv);
        bankingTv = findViewById(R.id.bankingTv);


        RecyclerView topupValueRv = findViewById(R.id.topupValueRv);
        topupValueRv.setLayoutManager(new LinearLayoutManager(this));

        ModelTopupValue[] modelTopupValues = new ModelTopupValue[]{
                new ModelTopupValue("100.00"),
                new ModelTopupValue("50.00"),
                new ModelTopupValue("30.00"),
                new ModelTopupValue("10.00"),
                new ModelTopupValue("5.00"),


        };

        AdapterTopupValue adapterTopupValue = new AdapterTopupValue(modelTopupValues, this);
        topupValueRv.setAdapter(adapterTopupValue);



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        topupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int number1 = Integer.parseInt(topupEt.getText().toString());
//                int balance = 0;
//                int sum = balance + number1;
//                eWalletBalance.setText("Rm"+String.valueOf(sum));
//            }
//        });


    }






}