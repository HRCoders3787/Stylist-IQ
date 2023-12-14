package com.example.stylistiq.DashBoard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.stylistiq.DashBoard.ui.closet.Closet;
import com.example.stylistiq.DashBoard.ui.weather.Weather;
import com.example.stylistiq.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //    CardView cardView2;
    MaterialButton see_all_btn, weather_btn;
//    ViewFlipper imageSlider;
    ArrayList<String> arrayList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialiseViews(view);

//        Hooks
        see_all_btn = view.findViewById(R.id.see_all_btn);
        weather_btn = view.findViewById(R.id.weather_btn);

        see_all_btn.setOnClickListener(v -> {
            Closet closetFragment = new Closet();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, closetFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

//        for (int i = 0; i < arrayList.size(); i++) {
//            showImage(arrayList.get(i));
//        }

        return view;
    }

//    public void showImage(String text) {
//
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.slider_items, null);
//        TextView tempText = view.findViewById(R.id.tempText);
//        tempText.setText(text);
//        imageSlider.addView(view);
//        imageSlider.setFlipInterval(2000);
//        imageSlider.setAutoStart(true);
//        imageSlider.setInAnimation(getContext(), android.R.anim.slide_in_left);
//        imageSlider.setOutAnimation(getContext(), android.R.anim.slide_out_right);
//
//    }

    public void initialiseViews(View view) {
//        cardView2 = view.findViewById(R.id.cardView2);
        arrayList.add("37 °C");
        arrayList.add("28 °C");

    }
}