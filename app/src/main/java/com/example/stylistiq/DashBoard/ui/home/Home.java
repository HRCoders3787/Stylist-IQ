package com.example.stylistiq.DashBoard.ui.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.stylistiq.Adapters.SliderAdapter.SliderAdapter;
import com.example.stylistiq.DashBoard.ui.closet.Closet;
import com.example.stylistiq.R;
import com.google.android.material.slider.Slider;

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

    CardView cardView2;
    ViewFlipper imageSlider;
    ArrayList<String> arrayList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
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

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Closet closetFragment = new Closet();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, closetFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        for (int i = 0; i < arrayList.size(); i++) {
            showImage(arrayList.get(i));
        }

        return view;
    }

    public void showImage(String text) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.slider_items, null);
        TextView tempText = view.findViewById(R.id.tempText);
        tempText.setText(text);
        imageSlider.addView(view);
        imageSlider.setFlipInterval(2000);
        imageSlider.setAutoStart(true);
        imageSlider.setInAnimation(getContext(), android.R.anim.slide_in_left);
        imageSlider.setOutAnimation(getContext(), android.R.anim.slide_out_right);

    }

    public void initialiseViews(View view) {
        cardView2 = view.findViewById(R.id.cardView2);
        imageSlider = view.findViewById(R.id.imageSlider);
        arrayList.add("37 °C");
        arrayList.add("28 °C");

    }
}