package com.example.stylistiq.Adapters.GridAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.stylistiq.R;

import java.util.ArrayList;

public class WeatherSuggestionAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> clothUrls;
    ArrayList<String> clothClassList;
    LayoutInflater inflater;

    public WeatherSuggestionAdapter(Context context, ArrayList<String> clothUrls, ArrayList<String> clothClassList) {
        this.context = context;
        this.clothUrls = clothUrls;
        this.clothClassList = clothClassList;
    }


    @Override
    public int getCount() {
        return 0;
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
        if (convertView == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.closet_imageset_cardview, null);
        }
        ImageView imageView = convertView.findViewById(R.id.clothes);
        TextView clothClass = convertView.findViewById(R.id.clothClass);
        TextView clothDate = convertView.findViewById(R.id.clothDate);

        Glide.with(context).load(clothUrls.get(position)).into(imageView);
        clothClass.setText(clothClassList.get(position));
        return convertView;
    }
}
