package com.example.stylistiq.Adapters;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.stylistiq.R;

public class ViewPageAdapter extends PagerAdapter {

    Context context;
    int[] images = {
            R.drawable.onboarding_background,
            R.drawable.onboarding_background2
    };

    int[] heading = {
            R.string.slider_layout_header1,
            R.string.slider_layout_header2
    };

    int[] description = {
            R.string.slider_layout_content1,
            R.string.slider_layout_content2
    };

    int[] btnText = {
            R.string.slider_layout_btn_text1,
            R.string.slider_layout_btn_text2
    };

    public ViewPageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        TextView sliderHeaderText = (TextView) view.findViewById(R.id.header_title);
        TextView sliderContentText = (TextView) view.findViewById(R.id.content);
        TextView sliderButtonText = (TextView) view.findViewById(R.id.indBtn);
        ViewPager slideViewPager = (ViewPager) view.findViewById(R.id.slideViewPager);

        LinearLayout linearBackground = (LinearLayout) view.findViewById(R.id.linearBackground);
        sliderHeaderText.setText(heading[position]);
        sliderContentText.setText(description[position]);
        sliderButtonText.setText(btnText[position]);

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), images[position], null);

        linearBackground.setBackground(drawable);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
