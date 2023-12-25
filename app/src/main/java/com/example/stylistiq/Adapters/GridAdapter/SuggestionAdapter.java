package com.example.stylistiq.Adapters.GridAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SuggestionAdapter extends BaseAdapter {

    Context context;

    ArrayList<ClothesModel> allClothData;
    LayoutInflater inflater;

    public SuggestionAdapter(Context context, ArrayList<ClothesModel> allClothData) {
        this.context = context;
        this.allClothData = allClothData;
    }

    @Override
    public int getCount() {
        return allClothData.size();
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
            convertView = inflater.inflate(R.layout.suggestion_imageset_cardview, null);
        }
        ImageView imageView = convertView.findViewById(R.id.clothes);
        TextView clothClass = convertView.findViewById(R.id.clothClass);
        TextView clothDate = convertView.findViewById(R.id.clothDate);

        CardView clothColor1 = convertView.findViewById(R.id.clothColor1);
//        CardView clothColor2 = convertView.findViewById(R.id.clothColor2);

        Glide.with(context).load(allClothData.get(position).getClotheImageUrl()).into(imageView);
        clothClass.setText(allClothData.get(position).getClothType());
        clothDate.setText(allClothData.get(position).getUploadDate());
        clothColor1.setCardBackgroundColor(allClothData.get(position).getClothColour());

        return convertView;
    }


}
