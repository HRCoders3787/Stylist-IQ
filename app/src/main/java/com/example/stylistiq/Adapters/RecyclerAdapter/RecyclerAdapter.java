package com.example.stylistiq.Adapters.RecyclerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Adapters.ViewPageAdapter;
import com.example.stylistiq.DashBoard.ui.closet.Wardrobe;
import com.example.stylistiq.DashBoard.ui.closet.WardrobeOutfitSuggestions;
import com.example.stylistiq.Models.ClosetModel;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    ArrayList<ClothesModel> allClothData;
    Context context;
    String[] parseClothData = new String[6];


    public RecyclerAdapter(ArrayList<ClothesModel> allClothData, Context context) {
        this.allClothData = allClothData;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.closet_imageset_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setRecyclerData(allClothData.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WardrobeOutfitSuggestions.class);
                if (!allClothData.isEmpty()) {
                    parseClothData[0] = allClothData.get(position).getClothID();
                    parseClothData[1] = allClothData.get(position).getClotheImageUrl();
                    parseClothData[2] = allClothData.get(position).getClothType();
                    parseClothData[3] = String.valueOf(allClothData.get(position).getClothColour());
                    parseClothData[4] = allClothData.get(position).getUploadDate();
                    parseClothData[5] = getColorName(allClothData.get(position).getClothColour());
                    intent.putExtra("clothData", parseClothData);
                    context.startActivity(intent);
                }

            }
        });
    }

    private String getColorName(int colorInt) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("RED", Color.RED);
        colorMap.put("GREEN", Color.GREEN);
        colorMap.put("BLUE", Color.BLUE);
        colorMap.put("YELLOW", Color.YELLOW);
        colorMap.put("CYAN", Color.CYAN);
        colorMap.put("MAGENTA", Color.MAGENTA);
        colorMap.put("BLACK", Color.BLACK);
        colorMap.put("WHITE", Color.WHITE);

        String colorName = null;

        for (Map.Entry<String, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() == colorInt) {
                colorName = entry.getKey();
                break;
            }
        }

        if (colorName == null) {
            int alpha = Color.alpha(colorInt);
            int red = Color.red(colorInt);
            int green = Color.green(colorInt);
            int blue = Color.blue(colorInt);

            String hexColor = String.format("#%02x%02x%02x", red, green, blue);

            colorName = hexColor;
        }

        return colorName;
    }

    @Override
    public int getItemCount() {
        return allClothData.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView clothes;
        TextView clothClass;
        TextView clothDate;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            clothes = itemView.findViewById(R.id.clothes);
            clothClass = itemView.findViewById(R.id.clothClass);
            clothDate = itemView.findViewById(R.id.clothDate);
        }

        void setRecyclerData(ClothesModel clothesModel) {
            Glide.with(context).load(clothesModel.getClotheImageUrl()).into(clothes);
            clothClass.setText(clothesModel.getClothType());
            clothDate.setText(clothesModel.getUploadDate());
        }

    }


}
