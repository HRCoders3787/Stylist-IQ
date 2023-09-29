package com.example.stylistiq.Design;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class CustomPageTransformation implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float alpha = 1 - Math.abs(position);
        page.setAlpha(alpha);
    }
}
