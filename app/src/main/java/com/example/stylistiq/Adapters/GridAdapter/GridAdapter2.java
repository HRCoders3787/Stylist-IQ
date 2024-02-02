package com.example.stylistiq.Adapters.GridAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.stylistiq.DashBoard.ui.closet.WardrobeOutfitSuggestions;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.R;

import java.util.ArrayList;

public class GridAdapter2 extends BaseAdapter {

    Context context;
    ArrayList<SuggestionModel> suggestionData;
    LayoutInflater inflater;
    //    String gridOpenFrom;
    String[] parseData = new String[7];

    public GridAdapter2(Context context, ArrayList<SuggestionModel> suggestionData) {
        this.context = context;
        this.suggestionData = suggestionData;

    }

    @Override
    public int getCount() {
        return suggestionData.size();
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

        ImageView imageView = convertView.findViewById(R.id.topClothes);
        ImageView imageView2 = convertView.findViewById(R.id.bottomClothes);

        TextView suggestionDate = convertView.findViewById(R.id.clothDate);

        CardView clothColor1 = convertView.findViewById(R.id.clothColor1);
        CardView clothColor2 = convertView.findViewById(R.id.clothColor2);

        Glide.with(context).load(suggestionData.get(position).getTopImg()).into(imageView);
        Glide.with(context).load(suggestionData.get(position).getBottomImg()).into(imageView2);
        suggestionDate.setText(suggestionData.get(position).getSuggestionDate());
        clothColor1.setCardBackgroundColor(suggestionData.get(position).getTopColor());
        clothColor2.setCardBackgroundColor(suggestionData.get(position).getBottomColor());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, WardrobeOutfitSuggestions.class);
//
//                parseData[0] = suggestionData.get(position).getTopImg();
//                parseData[1] = suggestionData.get(position).getBottomImg();
//                parseData[2] = suggestionData.get(position).getSuggestionDate();
//                parseData[3] = String.valueOf(suggestionData.get(position).getTopColor());
//                parseData[4] = String.valueOf(suggestionData.get(position).getBottomColor());
//
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("SuggestionData", parseData);
//                context.startActivity(intent);
            }
        });

        return convertView;
    }
}

//
