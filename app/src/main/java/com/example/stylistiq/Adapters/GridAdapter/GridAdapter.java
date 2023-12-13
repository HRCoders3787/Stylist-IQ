package com.example.stylistiq.Adapters.GridAdapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> clothData;
    ArrayList<String> clothClassList = new ArrayList<>(Arrays.asList("Shirt", "T-Shirt", "Jeans", "Shirt", "trouser", "T-Shirt"));
    ArrayList<String> clothDateList = new ArrayList<>(Arrays.asList(
            "July 2" + "," + " 2023",
            "Nov 3" + "," + " 2023",
            "June 2" + "," + " 2023",
            "Aug 4" + "," + " 2023",
            "Aug 4" + "," + " 2023",
            "Feb 12" + "," + " 2023"));
    LayoutInflater inflater;

    public GridAdapter(Context context, ArrayList<String> clothData) {
        this.context = context;
        this.clothData = clothData;
    }

    @Override
    public int getCount() {
        return clothData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.closet_imageset_cardview, null);
        }
        ImageView imageView = convertView.findViewById(R.id.clothes);
        TextView clothClass = convertView.findViewById(R.id.clothClass);
        TextView clothDate = convertView.findViewById(R.id.clothDate);

        Glide.with(context).load(clothData.get(position)).into(imageView);
        clothClass.setText(clothClassList.get(position));
        clothDate.setText(clothDateList.get(position));

        return convertView;
    }
}
