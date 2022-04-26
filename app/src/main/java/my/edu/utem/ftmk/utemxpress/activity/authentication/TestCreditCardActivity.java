package my.edu.utem.ftmk.utemxpress.activity.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import my.edu.utem.ftmk.utemxpress.R;

public class TestCreditCardActivity extends AppCompatActivity {

    private EditText cardNumEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_credit_card);

        cardNumEt = findViewById(R.id.cardNumEt);



    }


    public class CreditCardFormattingTextWatcher implements TextWatcher {
        private EditText cardNumEt;
        boolean isDelete;

        public CreditCardFormattingTextWatcher(EditText cardNumEt) {
            this.cardNumEt = cardNumEt;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (before == 0)
                isDelete = false;
            else
                isDelete = true;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}