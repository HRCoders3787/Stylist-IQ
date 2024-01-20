package com.example.stylistiq.DashBoard.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stylistiq.Adapters.GridAdapter.GridAdapter;
import com.example.stylistiq.Adapters.GridAdapter.SuggestionAdapter;
import com.example.stylistiq.Adapters.RecyclerAdapter.RecyclerAdapter;
import com.example.stylistiq.DashBoard.ui.closet.Wardrobe;
import com.example.stylistiq.DashBoard.ui.closet.WardrobeOutfitSuggestions;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tempText;
    RecyclerView closet_recView;
    String City;
    private final String url = "https://api.openweathermap.org/data/2.5/weather/";
    private final String appid = "e53301e27efa0b66d05045d91b2742d3";
    ArrayList<String> weatherList = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    DecimalFormat df = new DecimalFormat("#.#");
    ArrayList<ClothesModel> allClothData;
    ArrayList<SuggestionModel> suggestionData;
    RecyclerAdapter recyclerAdapter;
    SuggestionAdapter suggestionAdapter;
    GridView suggestions_grid_view;
    FirebaseDatabase database;
    DatabaseReference reference;
    HashMap<String, String> userDetails;
    String _phone;
    String parseClothData[] = new String[6];
    SessionManager sessionManager;

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
            Intent intent = new Intent(getContext(), Wardrobe.class);
            startActivity(intent);
        });

        return view;
    }


    public void initialiseViews(View view) {
        tempText = view.findViewById(R.id.tempText);
        closet_recView = view.findViewById(R.id.closet_recView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        suggestions_grid_view = view.findViewById(R.id.suggestions_grid_view);


        sessionManager = new SessionManager(getContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        allClothData = new ArrayList<>();
        suggestionData = new ArrayList<>();
        getAllClothesImages();
        getSuggestionsImages();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        closet_recView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(allClothData, getContext());
        closet_recView.setAdapter(recyclerAdapter);

        suggestionAdapter = new SuggestionAdapter(getContext(), suggestionData);
        suggestions_grid_view.setAdapter(suggestionAdapter);


        getLastLocation();
    }


    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    assert addresses != null;
                                    City = addresses.get(0).getLocality();

                                    getWeatherDetails(City);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
//                                Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    public void getWeatherDetails(String city) {
        String tempUrl = "";
        tempUrl = url + "?q=" + city + "&appid=" + appid;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    tempText.setText(df.format(temp) + " °C");

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    public void getAllClothesImages() {
        reference.child("Closet").child(_phone).child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allClothData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot innerSnapShot : dataSnapshot.getChildren()) {
                                ClothesModel clothesModel = innerSnapShot.getValue(ClothesModel.class);
                                allClothData.add(clothesModel);
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        allClothData.clear();
                    }
                });
    }

    private void getSuggestionsImages() {
        reference.child("Suggestion")
                .child(_phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            suggestionData.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren())
                            {
                                SuggestionModel suggestionModel = snapshot1.getValue(SuggestionModel.class);
                                suggestionData.add(suggestionModel);
                            }
                            suggestionAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}