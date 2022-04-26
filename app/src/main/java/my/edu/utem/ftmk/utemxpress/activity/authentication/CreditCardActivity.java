package my.edu.utem.ftmk.utemxpress.activity.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;

public class CreditCardActivity extends AppCompatActivity {
    private CardForm cardForm;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        cardForm=(CardForm)findViewById(R.id.creditCard);
        

        TextView textDes = (TextView)findViewById(R.id.payment_amount);
        Button btnPay = (Button)findViewById(R.id.btn_pay);

        textDes.setText("");
        btnPay.setText(String.format("Confirm Payment"));

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
//                Toast.makeText(CreditCardActivity.this, "Name :"
//                        +card.getName()+"Last 4 digits"+card.getLast4(), Toast.LENGTH_SHORT).show();

                progressDialog = ProgressDialog.show(CreditCardActivity.this,"","payment is successfully processed", true);


//                Toast.makeText(CreditCardActivity.this, "payment is successfully processed", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 3000);
            }
        });



    }

}