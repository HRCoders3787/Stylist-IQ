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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> clothData;
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
//        ShimmerFrameLayout shimmerFrameLayout;
//        LinearLayout dataLayout;
//
//        shimmerFrameLayout = convertView.findViewById(R.id.shimmer_view);
//        dataLayout = convertView.findViewById(R.id.data_view);
//
//        dataLayout.setVisibility(View.INVISIBLE);
//        shimmerFrameLayout.startShimmer();
//
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            dataLayout.setVisibility(View.VISIBLE);
//            shimmerFrameLayout.startShimmer();
//            shimmerFrameLayout.setVisibility(View.INVISIBLE);
//        }, 5000);

        Glide.with(context).load(clothData.get(position)).into(imageView);
        return convertView;
    }
}
