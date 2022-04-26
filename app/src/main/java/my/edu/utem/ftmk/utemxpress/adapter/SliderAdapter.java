package my.edu.utem.ftmk.utemxpress.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;

import com.smarteist.autoimageslider.SliderViewAdapter;

import my.edu.utem.ftmk.utemxpress.R;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    int[] images1;

    public SliderAdapter(int[] images) {

        this.images1 = images;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {

        viewHolder.imageView.setImageResource(images1[position]);

    }

    @Override
    public int getCount() {
        return images1.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);

        }
    }
}