package com.example.stylistiq.DashBoard.ui.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stylistiq.Adapters.GridAdapter.GridAdapter;
import com.example.stylistiq.Adapters.GridAdapter.SuggestionAdapter;
import com.example.stylistiq.DashBoard.suggestionAlgo.Algorithm;
import com.example.stylistiq.DashBoard.suggestionAlgo.DataBase;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Weather#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Weather extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String City;
    String _phone;
    private final String url = "https://api.openweathermap.org/data/2.5/weather/";
    private final String appid = "e53301e27efa0b66d05045d91b2742d3";
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    DecimalFormat df = new DecimalFormat("#.#");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView cloudTxt, humidityTxt, addressTxt, tempTxt;
    GridView weather_suggestions_grid_view;

    FirebaseDatabase database;
    DatabaseReference reference;
    GridAdapter gridAdapter;
    ArrayList<SuggestionModel> suggestionData;
    SuggestionAdapter suggestionAdapter;
    HashMap<String, String> userDetails;
    SessionManager sessionManager;


    public Weather() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Weather.
     */
    // TODO: Rename and change types and number of parameters
    public static Weather newInstance(String param1, String param2) {
        Weather fragment = new Weather();
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
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        initialiseViews(view);
        return view;
    }

    public void initialiseViews(View view) {
        cloudTxt = view.findViewById(R.id.cloud);
        tempTxt = view.findViewById(R.id.tempTxt);
        humidityTxt = view.findViewById(R.id.humidity);
        addressTxt = view.findViewById(R.id.address);
//        debugText = view.findViewById(R.id.debugText);
        weather_suggestions_grid_view = view.findViewById(R.id.weather_suggestions_grid_view);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLastLocation();


        sessionManager = new SessionManager(getContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        suggestionData = new ArrayList<>();
        getAllSuggestionClothesImages();

        suggestionAdapter = new SuggestionAdapter(getContext(), suggestionData);
        weather_suggestions_grid_view.setAdapter(suggestionAdapter);

    }


    private boolean alreadyPresentSuggestion() {
        boolean flag = false;
        reference.child("Suggestion").child(_phone)
                .child("Top").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return flag;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    City = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    addressTxt.setText(City + ", " + state);
                                    getWeatherDetails(City);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }


    public void getWeatherDetails(String city) {
        String tempUrl = "";
        tempUrl = url + "?q=" + city + "&appid=" + appid;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
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
                    tempTxt.setText(df.format(temp) + " °C");
                    humidityTxt.setText("Humidity: " + String.valueOf(humidity) + "%");
                    cloudTxt.setText("Cloudy: " + String.valueOf(clouds) + "%");


                } catch (Exception ex) {
                    System.out.println("FROM EXCEPTION " + ex.getMessage().toString());
//                    Toast.makeText(getContext(), "FROM EXCEPTION " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "FROM ERROR RESP " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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


    public void getAllSuggestionClothesImages() {
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