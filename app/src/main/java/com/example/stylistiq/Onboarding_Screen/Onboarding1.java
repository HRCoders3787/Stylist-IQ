package com.example.stylistiq.Onboarding_Screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stylistiq.Adapters.ViewPageAdapter;
import com.example.stylistiq.CustomPageTransformation;
import com.example.stylistiq.R;

public class Onboarding1 extends AppCompatActivity {

    public static ViewPager slideViewPager;
    LinearLayout indicatorLayout;
    ImageView[] dash;
    ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding1);

        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicatorLayout);

        viewPageAdapter = new ViewPageAdapter(Onboarding1.this);
        slideViewPager.setAdapter(viewPageAdapter);

        slideViewPager.setPageTransformer(true, new CustomPageTransformation());
        setUpIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);
    }

    public void setUpIndicator(int position) {
        dash = new ImageView[2];
        indicatorLayout.removeAllViews();

        for (int i = 0; i < dash.length; i++) {
            dash[i] = new ImageView(this);
            dash[i].setImageResource(R.drawable.not_active_slider);
            indicatorLayout.addView(dash[i]);
        }
        dash[position].setImageResource(R.drawable.active_slider);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}