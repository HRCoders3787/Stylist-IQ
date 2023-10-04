package com.example.stylistiq.Onboarding_Screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.stylistiq.Adapters.ViewPageAdapter;
import com.example.stylistiq.Authentication_Panel.Authentication;
import com.example.stylistiq.Design.CustomPageTransformation;
import com.example.stylistiq.Models.OnboardingItem;
import com.example.stylistiq.R;

import java.util.ArrayList;
import java.util.List;

public class Onboarding1 extends AppCompatActivity {

    public static ViewPager2 slideViewPager;
    ConstraintLayout OnBoardingParent;
    LinearLayout indicatorLayout;
    ImageView[] dash;
    ViewPageAdapter viewPageAdapter;
    CardView sliderBtn;
    TextView indBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding1);

        OnBoardingParent = findViewById(R.id.OnBoardingParent);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        slideViewPager = findViewById(R.id.slideViewPager);
        sliderBtn = findViewById(R.id.sliderBtn);
        indBtn = findViewById(R.id.indBtn);

        setupOnboardingItem();
        slideViewPager.setAdapter(viewPageAdapter);
        slideViewPager.setPageTransformer(new CustomPageTransformation());
        setupOnboardingIndicator();
        setCurrentOnboardingIndicator(0);

        slideViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        sliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideViewPager.getCurrentItem() + 1 < viewPageAdapter.getItemCount()) {
                    slideViewPager.setCurrentItem(slideViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), Authentication.class));
                    finish();
                }
            }
        });
    }

    private void setupOnboardingItem() {
        List<OnboardingItem> onboardingItemList = new ArrayList<>();
        OnboardingItem itemPage1 = new OnboardingItem();
        itemPage1.setHeader("Discover Your Style with Stylist IQ");
        itemPage1.setDescription("Discover your unique style with our AI- powered recommendations.");
        onboardingItemList.add(itemPage1);
        OnboardingItem itemPage2 = new OnboardingItem();
        itemPage2.setHeader("Perfect Outfits, Every Day");
        itemPage2.setDescription("Say goodbye to wardrobe dilemmas. StylistIQ creates perfect outfits for every occasion.");

        onboardingItemList.add(itemPage2);

        viewPageAdapter = new ViewPageAdapter(onboardingItemList, getApplicationContext());
    }

    private void setupOnboardingIndicator() {
        ImageView[] indicator = new ImageView[viewPageAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicator.length; i++) {
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(
                    ContextCompat.getDrawable(
                            getApplicationContext(),
                            R.drawable.onboarding_inactive_dash));
            indicator[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicator[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                getApplicationContext(),
                                R.drawable.onboarding_active_dash));
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                                getApplicationContext(),
                                R.drawable.onboarding_inactive_dash));
            }
        }
        if (position == viewPageAdapter.getItemCount() - 1) {
            indBtn.setText("Get started!");
            OnBoardingParent.setBackgroundResource(R.drawable.onboarding_background2);
        } else {
            indBtn.setText("Next up!");
            OnBoardingParent.setBackgroundResource(R.drawable.onboarding_background);
        }
    }


}