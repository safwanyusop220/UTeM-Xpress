package my.edu.utem.ftmk.utemxpress.activity.user;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import my.edu.utem.ftmk.utemxpress.R;

public class ShoppingList extends AppCompatActivity {

    private ImageView logoIv ;
    private ImageButton logoutBtn,bookSlotBtn;
    private TextView greetingTv, hellotv,text1,text2,iconSwapVerticaTv,
            specialOfferTv,paymentAtDoorTv;
    private LinearLayout browserGroceriesTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


    }
}
