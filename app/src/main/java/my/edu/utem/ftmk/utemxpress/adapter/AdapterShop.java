package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;
import my.edu.utem.ftmk.utemxpress.model.ModelShop;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    private Context context;
    public ArrayList<ModelShop> shopList;


    public AdapterShop(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // inflate layout row_shop
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        //get
        ModelShop modelShop = shopList.get(position);
        String accountType = modelShop.getAccountType();
        String city = modelShop.getCity();
        String street = modelShop.getStreet();
        String unit = modelShop.getUnit();
        String address = modelShop.getAddress();
        String deliveryFee = modelShop.getDeliveryFee();
        String email = modelShop.getEmail();
        String online = modelShop.getOnline();
        String name = modelShop.getName();
        String phone = modelShop.getPhone();
        final String uid = modelShop.getUid();
        String timeStamp = modelShop.getTimestamp();
        String shopOpen = modelShop.getShopOpen();
        String shopName = modelShop.getShopName();
        String profileImage = modelShop.getProfileImage();

        //set data
        holder.shopNameTv.setText("Shop Name : "+shopName);
        holder.phoneTv.setText("Phone Number : "+phone);
        holder.cityTv.setText("Location : "+address);
//        if (online.equals("true")){
//            holder.onlineIv.setVisibility(View.VISIBLE);
//        }
//        else {
//            holder.onlineIv.setVisibility(View.GONE);
//        }
        if(shopOpen.equals("true")){
            holder.shopClosedTv.setVisibility(View.GONE);
        }
        else {
            holder.shopClosedTv.setVisibility(View.VISIBLE);
        }

        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_grey).into(holder.shopIv);
        }
        catch (Exception e){
            holder.shopIv.setImageResource(R.drawable.ic_store_grey);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hanlde item click, show item details
                Intent intent = new Intent(context, HomePageActivity.class);
                intent.putExtra("shopUid", uid);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return  shopList.size(); //return number of record
    }

    //view holder
    class  HolderShop extends RecyclerView.ViewHolder{

        //Ui views of row_shop.xml
        private ImageView shopIv, onlineIv;
        private TextView shopClosedTv, shopNameTv, phoneTv,cityTv;
        private RatingBar ratingBar;


        public HolderShop(@NonNull View itemView) {
            super(itemView);

            //initalize uid views
            shopIv = itemView.findViewById(R.id.shopIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            shopClosedTv = itemView.findViewById(R.id.shopClosedTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            cityTv = itemView.findViewById(R.id.cityTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);



        }
    }
}
