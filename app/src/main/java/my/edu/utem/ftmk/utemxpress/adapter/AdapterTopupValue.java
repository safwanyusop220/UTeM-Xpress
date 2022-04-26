package my.edu.utem.ftmk.utemxpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import my.edu.utem.ftmk.utemxpress.R;
import my.edu.utem.ftmk.utemxpress.activity.user.ReloadEwalletActivity;
import my.edu.utem.ftmk.utemxpress.model.ModelTopupValue;

public class AdapterTopupValue extends RecyclerView.Adapter<AdapterTopupValue.ViewHolder>{

    ModelTopupValue[] modelTopupValues;
    Context context;


    public AdapterTopupValue(ModelTopupValue[] modelTopupValues, ReloadEwalletActivity activity) {
        this.modelTopupValues = modelTopupValues;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_topup_value,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelTopupValue modelTopupValue = modelTopupValues[position];
//        holder.valueTv.setText(modelTopupValue.getValueTv());
        holder.valueTv.setText("    RM"+modelTopupValue.getValueTv());

        holder.valueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add product details


            }
        });
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, modelTopupValue.getValueTv(), Toast.LENGTH_SHORT).show();

            }
        });

    }



    @Override
    public int getItemCount() {
        return modelTopupValues.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       private TextView valueTv;
        private Button valueButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueTv = itemView.findViewById(R.id.valueTv);
            valueButton = itemView.findViewById(R.id.valueButton);
        }
    }
}
