package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.HomePageActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.MainUserActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.ReloadEwalletActivity;
import my.edu.utem.ftmk.utemxpress.activity.user.SelectedCategoryProductActivity;
import my.edu.utem.ftmk.utemxpress.model.Constant;

public class AdapterProductCategories extends  RecyclerView.Adapter<AdapterProductCategories.viewHolder>{

    List<String> titles;
    List<Integer> images;

    Context context;
    LayoutInflater inflater;

    public AdapterProductCategories(Context ctx, List<String>titles, List<Integer>images){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        View view = inflater.inflate(R.layout.row_product_category,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.titles.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //hanlde item click, show item details
//                startActivity(new Intent(HomePageActivity.this, ReloadEwalletActivity.class));
//
//
//            }
//        });

//        reloadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //go to reload page
//                startActivity(new Intent(HomePageActivity.this, ReloadEwalletActivity.class));
//
//            }
//        });



    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder{
        TextView titles;
        ImageView gridIcon;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            titles = itemView.findViewById(R.id.titleTv);
            gridIcon = itemView.findViewById(R.id.productIconIv);



        }
    }


}
