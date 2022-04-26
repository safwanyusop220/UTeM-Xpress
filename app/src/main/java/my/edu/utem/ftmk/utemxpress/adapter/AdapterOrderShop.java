package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import my.edu.utem.ftmk.utemxpress.model.FilterOrderShop;
import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.seller.OrderDetailsSellerActivity;
import my.edu.utem.ftmk.utemxpress.model.ModelOrderShop;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> implements Filterable {


    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList, filterList;
    private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller, parent, false);

        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {

        //get data at position
        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);
        String orderId = modelOrderShop.getOrderId();
        String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        String orderTo = modelOrderShop.getOrderTo();

        //load user/buyer info
        loadUserInfo(modelOrderShop, holder);

        //set data to view
        holder.amountTv.setText("RM"+ orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order ID: "+orderId);

        //change color status
        if(orderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.blue02_login));
        }
        else if (orderStatus.equals("Completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (orderStatus.equals("Cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.red));
        }


        //Convert time
        //addd here
        // convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.orderDateTv.setText(formatedDate);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open order details
                Intent intent = new Intent(context, OrderDetailsSellerActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderBy", orderBy);
                context.startActivity(intent);
            }
        });



    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, HolderOrderShop holder) {

        //tp load email of the user/buyer: modelOrderShop.getOrderBy() containts uid of that user/buyer
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        holder.emailTv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            //init filter
            filter = new FilterOrderShop(this, filterList);
        }

        return filter;
    }

    //view holder class for row_order_seller.xml
    class HolderOrderShop extends RecyclerView.ViewHolder{


        //ui views of row_order_seller.xml
        private TextView orderIdTv, orderDateTv, emailTv, amountTv, statusTv;

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            //init ui views
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);

        }
    }


}
