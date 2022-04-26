package my.edu.utem.ftmk.utemxpress.adapter;

import android.annotation.SuppressLint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;
import my.edu.utem.ftmk.utemxpress.model.ModelCartItem;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_cart.xml
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);

        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {
        //get data
        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String productIcon = modelCartItem.getProductIcon();
        String getpId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText("Total: "+"RM"+cost);
        holder.itemQuantityTv.setText("[Quantity- "+quantity+"]");
        holder.itemPriceEachTv.setText("RM"+price+"/each");

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_store_grey).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_store_grey);
        }


        //handle remove click listener
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //will create table if not exist, but in that case will must exist
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB2") // TEST is the name of the DATABASE
                        .setTableName("ITEMS_TABLE")  // You can ignore this line if you want
                        .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Picture", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context,"Removed from cart...", Toast.LENGTH_SHORT).show();

                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double deliveryFee = 2.00;


                double tx = Double.parseDouble((((HomePageActivity)context).allTotalPriceTv.getText().toString().trim().replace("RM", "")));
                double totalPrice = tx - Double.parseDouble(cost.replace("RM", ""));
//                double deliveryFee= Double.parseDouble((((HomePageActivity)context).deliveryFee.replace("RM", "")));
//                double deliveryFee= Double.parseDouble((((HomePageActivity)context).deliveryFee.replace("RM", "")));
                double sTotalPrice = Double.parseDouble(String.format("%.2f", totalPrice))- Double.parseDouble(String.format("%.2f",deliveryFee));
//                double sTotalPrice = Double.parseDouble(String.format("Rm.2f", totalPrice))-2;


                ((HomePageActivity)context).allTotalPrice=0.00;
                ((HomePageActivity)context).sTotalTv.setText("RM"+String.format("%.2f",sTotalPrice));
                ((HomePageActivity)context).allTotalPriceTv.setText("RM"+String.format("%.2f",Double.parseDouble(String.format("%.2f", totalPrice))));



                //after removing item from cart, update cart count
                ((HomePageActivity)context).cartCount();


            }

        });




    }

    @Override
    public int getItemCount() {
        return cartItems.size(); //return number or order
    }

    class HolderCartItem extends RecyclerView.ViewHolder{


        //ui view of row cart
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv,
                        itemQuantityTv, itemRemoveTv;
        private ImageView productIconIv;


        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            //init views
            productIconIv = itemView.findViewById(R.id.productIconIv);
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);

        }
    }


}
