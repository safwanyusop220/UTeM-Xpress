package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.model.ModelCartItem;
import my.edu.utem.ftmk.utemxpress.model.ModelOrderedItem;

public class AdapterOrderedItem extends  RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemArrayList) {
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);
        return  new HolderOrderedItem(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        //get data at position
        ModelOrderedItem modelOrderedItem = orderedItemArrayList.get(position);
        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();


        //set Data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("RM"+price);
        holder.itemPriceTv.setText("RM"+cost);
        holder.itemQuantityTv.setText("["+ quantity +"]");

    }

    @Override
    public int getItemCount() {
        return  orderedItemArrayList.size();
    }


    //view holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //view of row_ordereditem.xml
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv;


        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            //init view
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);

        }
    }


}
