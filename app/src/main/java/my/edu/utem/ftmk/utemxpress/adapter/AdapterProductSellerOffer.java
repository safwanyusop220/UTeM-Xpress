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

import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;
import my.edu.utem.ftmk.utemxpress.model.FilterProduct;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;
import my.edu.utem.ftmk.utemxpress.R;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductSellerOffer extends RecyclerView.Adapter<AdapterProductSellerOffer.HolderProductSellerOffer>{

    private Context context;
    public ArrayList<ModelProduct> productList, filterList1;


    public AdapterProductSellerOffer(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList1 =  productList;
    }

    @NonNull
    @Override
    public HolderProductSellerOffer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller_offer, parent,false);
        return new HolderProductSellerOffer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSellerOffer holder, int position) {
        //get data
        //get data
        ModelProduct modelProduct = productList.get(position);
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



        if(discountAvailable.equals("true")){
            //product on discount
            holder.titleTv.setText(title);
            //holder.quantityTv.setText("Quantity : "+quantity);
            holder.discountedPriceTv.setText("RM"+discountPrice);
//        holder.discountedPriceTv1.setText("RM"+discountPrice);
            holder.discountedPriceTv2.setText("Now -> RM"+discountPrice + ",");
            holder.originalPriceTv1.setText("Before -> RM"+originalPrice);
            holder.discountNoteTv.setText(discountNote);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
        }
        else if(discountAvailable.equals("false")){
            holder.titleTv.setText(title);
            //holder.quantityTv.setText("Quantity : "+quantity);
            holder.discountedPriceTv.setText("RM"+originalPrice);
//        holder.discountedPriceTv1.setText("RM"+discountPrice);
            holder.discountedPriceTv2.setText("Now -> RM"+originalPrice + ",");
            holder.originalPriceTv1.setText("Before -> RM"+originalPrice);
            holder.discountNoteTv.setText(discountNote);
            holder.discountNoteTv.setVisibility(View.INVISIBLE);

        }

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_aa_shopping_blue).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_aa_shopping_blue);

        }

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
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





        //set Data
//        holder.titleTv.setText(title);
//        //holder.quantityTv.setText("Quantity : "+quantity);
//        holder.discountedPriceTv.setText("RM"+discountPrice);
////        holder.discountedPriceTv1.setText("RM"+discountPrice);
//        holder.discountedPriceTv2.setText("RM"+discountPrice);
//        holder.originalPriceTv1.setText("RM"+originalPrice);


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
        TextView originalPriceTv1 = view.findViewById(R.id.originalPriceTv1);
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
                addToCart(productId,title,priceEach,totalPrice,quantity,image);

                dialog.dismiss();

            }
        });


    }




    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity, String image) {
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
        return productList.size();
    }



    class HolderProductSellerOffer extends RecyclerView.ViewHolder{


        private ImageView productIconIv;
        private TextView titleTv,originalPriceTv,eachpriceTv,nowTv,
                discountedPriceTv,discountedPriceTv1,discountedPriceTv2,commaTv,beforeTv,originalPriceTv1,discountNoteTv;
        private Button addToCartBtn;
        public HolderProductSellerOffer(@NonNull View itemView) {
            super(itemView);

            //init ui view
            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            discountedPriceTv1 = itemView.findViewById(R.id.discountedPriceTv1);
            discountedPriceTv2 = itemView.findViewById(R.id.discountedPriceTv2);
            originalPriceTv1 = itemView.findViewById(R.id.originalPriceTv1);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);





        }
    }





}
