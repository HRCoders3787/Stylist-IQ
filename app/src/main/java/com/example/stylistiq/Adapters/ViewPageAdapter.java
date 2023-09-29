package com.example.stylistiq.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stylistiq.Models.OnboardingItem;
import com.example.stylistiq.R;

import java.util.List;

public class ViewPageAdapter extends RecyclerView.Adapter<ViewPageAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> onboardingItemList;
    private static Context context;

    public ViewPageAdapter(List<OnboardingItem> onboardingItemList, Context context) {
        this.onboardingItemList = onboardingItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {

        holder.setOnboardingData(onboardingItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItemList.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private TextView header_title;
        private TextView content;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            header_title = itemView.findViewById(R.id.header_title);
            content = itemView.findViewById(R.id.content);
        }

        void setOnboardingData(OnboardingItem onboardingItem) {
            Toast.makeText(context, "" + onboardingItem.getHeader(), Toast.LENGTH_SHORT).show();
            header_title.setText(onboardingItem.getHeader());
            content.setText(onboardingItem.getDescription());
        }
    }

}
