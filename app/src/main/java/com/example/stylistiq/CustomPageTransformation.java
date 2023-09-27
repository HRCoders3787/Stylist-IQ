package com.example.stylistiq;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class CustomPageTransformation implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float alpha = 1 - Math.abs(position);
        page.setAlpha(alpha);
    }
}
