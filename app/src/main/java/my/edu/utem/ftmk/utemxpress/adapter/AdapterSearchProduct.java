package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.model.ModelProduct;

public class AdapterSearchProduct  extends RecyclerView.Adapter<AdapterSearchProduct.HolderSearchProduct>{


    private Context context;
    public ArrayList<ModelProduct> productList;


    public AdapterSearchProduct(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public HolderSearchProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);

        return new HolderSearchProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearchProduct holder, int position) {

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


        //set Data
        holder.productNameTv.setText(title);
        //holder.quantityTv.setText("Quantity : "+quantity);
        holder.discountedPriceTv.setText("RM"+discountPrice);
        holder.discountedPriceTv1.setText("RM"+discountPrice);
//        holder.discountedPriceTv2.setText("RM"+discountPrice);

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_aa_shopping_blue).into(holder.productImageIv);
        }
        catch (Exception e){
            holder.productImageIv.setImageResource(R.drawable.ic_aa_shopping_blue);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hanlde item click, show item details
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class HolderSearchProduct extends RecyclerView.ViewHolder{

        private ImageView productImageIv;
        private TextView productNameTv,originalPriceTv,eachpriceTv,nowTv,
                discountedPriceTv,discountedPriceTv1,discountedPriceTv2,commaTv,beforeTv,originalPriceTv1;
        private Button addBtn;
        public HolderSearchProduct(@NonNull View itemView) {
            super(itemView);

            productImageIv = itemView.findViewById(R.id.productIconIv);
            productNameTv = itemView.findViewById(R.id.productNameTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            discountedPriceTv1 = itemView.findViewById(R.id.discountedPriceTv1);
            addBtn = itemView.findViewById(R.id.addBtn);



        }
    }
}
