package my.edu.utem.ftmk.utemxpress.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import my.edu.utem.ftmk.utemxpress.model.FilterProductUser;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct>productsList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);

        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        //get data
        ModelProduct modelProduct = productsList.get(position);
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountPrice = modelProduct.getDiscountPrice();
        String discountNote = modelProduct.getDiscountNote();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String productIcon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();


        //holder.discountedNoteTv.setText(discountNote);
//        holder.originalPriceTv.setText("RM"+originalPrice);
//        if(discountAvailable.equals("true")){
//            //product on discount
//            holder.discountedPriceTv.setVisibility(View.VISIBLE);
//            //holder.discountedNoteTv.setVisibility(View.VISIBLE);
//            //holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);//ADD strike to orignal price
//        }
//        else{
//            //product not discount
//            holder.discountedPriceTv.setVisibility(View.GONE);
//            //holder.discountedNoteTv.setVisibility(View.GONE);
//            //holder.originalPriceTv.setPaintFlags(0);
//        }

        //set data
        holder.productNameTv.setText(title);
                if(discountAvailable.equals("true")){
            //product on discount
                    holder.discountedPriceTv.setText("rm"+discountPrice);
                    holder.discountedPriceTv1.setText("rm"+discountPrice);

        }
        else{
            //product not discount
                    holder.discountedPriceTv.setText("rm"+originalPrice);
                    holder.discountedPriceTv1.setText("rm"+originalPrice);
        }



        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_aa_shopping_blue).into(holder.productImageIv);
        }
        catch (Exception e){
            holder.productImageIv.setImageResource(R.drawable.ic_aa_shopping_blue);

        }

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add product details
                showQuantityDialog(modelProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hanlde item click, show item details
            }
        });


    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate dialog
        View view = LayoutInflater.from(context).inflate(R.layout.row_dialog_quantity, null);
        //init layout view

        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
//        TextView pQuantity = view.findViewById(R.id.pQuantityTv);
//        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);
        TextView discountedPriceTv = view.findViewById(R.id.discountedPriceTv);


        //get data from model product
        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
//        String productQuantity = modelProduct.getProductQuantity();
//        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();



        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            //if product have discount
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike through on original price
        }
        else {
            //product dont have discount
            discountedNoteTv.setVisibility(View.GONE);
            discountedPriceTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("RM", ""));
        finalCost = Double.parseDouble(price.replaceAll("Rm", ""));
        quantity = 1;

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_grey).into(productIv);

        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_grey);
        }
        titleTv.setText(""+title);
//        pQuantity.setText("Quantity : "+productQuantity);
//        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("RM"+modelProduct.getOriginalPrice());
        discountedPriceTv.setText("RM"+modelProduct.getDiscountPrice());
        finalPriceTv.setText("RM"+String.format("%.2f", finalCost));

        AlertDialog dialog = builder.create();
        dialog.show();

        //increase quantity
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText("RM"+String.format("%.2f",finalCost));
                quantityTv.setText(""+quantity);

            }
        });

        //decrease quantity
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost = finalCost - cost;
                quantity--;

                finalPriceTv.setText("RM"+String.format("%.2f",finalCost));
                quantityTv.setText(""+quantity);

            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("RM", "");
                String quantity = quantityTv.getText().toString().trim();

                //add to db
                addBtn(productId,title,priceEach,totalPrice,quantity,image);

                dialog.dismiss();

            }
        });


    }

    private int itemId = 1;
    private void addBtn(String productId, String title, String priceEach, String price, String quantity, String image) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB2") // TEST is the name of the DATABASE
                .setTableName("ITEMS_TABLE")  // You can ignore this line if you want
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Picture", new String[]{"text", "not null"}))
//                .addColumn(new Column("Item_PaymentState", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .addData("Item_Picture", image)
//                .addData("Item_PaymentState", paymentState)

                .doneDataAdding();

        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();

        //update cart count
        ((HomePageActivity)context).cartCount();




    }





    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null ){
            filter = new FilterProductUser(this, filterList);
        }

        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        //ui views
        private ImageView productImageIv;
        private TextView productNameTv, discountedPriceTv, discountedPriceTv1;
        private Button addBtn;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            //init ui views
            productImageIv = itemView.findViewById(R.id.productImageIv);
            productNameTv = itemView.findViewById(R.id.productNameTv);
            discountedPriceTv1 = itemView.findViewById(R.id.discountedPriceTv1);
            addBtn = itemView.findViewById(R.id.addBtn);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);

        }
    }
}
