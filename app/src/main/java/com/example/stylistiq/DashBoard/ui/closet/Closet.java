package com.example.stylistiq.DashBoard.ui.closet;

import static android.content.Context.ALARM_SERVICE;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Adapters.GridAdapter.ClothSelectionAdapter;
import com.example.stylistiq.Adapters.GridAdapter.SuggestionAdapter;
import com.example.stylistiq.BroadCast.broadcast_receiver;
import com.example.stylistiq.Models.ClosetModel;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.Models.ScheduleModel;
import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Closet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Closet extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private PopupWindow popupWindow;
    ImageView topImage, bottomImage;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Toolbar toolbar;
    private ArrayList<SuggestionModel> suggestionData;
    private ArrayList<ClothesModel> allDataList;
    private ClothSelectionAdapter selectionAdapter;
    FirebaseDatabase database;
    DatabaseReference reference;
    String _phone;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    GridView suggestionGridView, ClosetGridView;
    SuggestionAdapter suggestionAdapter;
    FloatingActionButton setReminder;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    EditText occasionName;

    String topImageUri, bottomImageUri, selectionFrom, getScheduleDate;
    int topImageColor, bottomImageColor;


    public Closet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Closet.
     */
    // TODO: Rename and change types and number of parameters
    public static Closet newInstance(String param1, String param2) {
        Closet fragment = new Closet();
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
        View view = inflater.inflate(R.layout.fragment_closet, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {

        dateButton = view.findViewById(R.id.datePickerButton);
        Button upload_top = view.findViewById(R.id.upload_top);
        Button upload_bottom = view.findViewById(R.id.upload_bottom);
        suggestionGridView = view.findViewById(R.id.suggestionGridView);
        topImage = view.findViewById(R.id.topImage);
        bottomImage = view.findViewById(R.id.bottomImage);
        setReminder = view.findViewById(R.id.setReminder);
        occasionName = view.findViewById(R.id.occasionName);

        sessionManager = new SessionManager(getContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        notification_cannel();
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(getContext(), broadcast_receiver.class), PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        getScheduleDate = getTodaysDate();

        dateButton.setText(getScheduleDate);
        initDatePicker();
        dateButton.setOnClickListener(v -> datePickerDialog.show());
        allDataList = new ArrayList<>();
        suggestionData = new ArrayList<>();

        getSuggestionsImages();

        upload_top.setOnClickListener(v -> {
            showPopupWindow(v, "top");
            selectionFrom = "Wardrobe";
        });

        upload_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupWindow(v, "bottom");
            }
        });

        suggestionAdapter = new SuggestionAdapter(getContext(), suggestionData);
        suggestionGridView.setAdapter(suggestionAdapter);

        suggestionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectionFrom = "Suggestion";
                Toast.makeText(getContext(), "POSITION : " + position, Toast.LENGTH_SHORT).show();
            }
        });
        setReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (occasionName.getText().length() > 0) {
                    if (topImage.getDrawable() != null && bottomImage.getDrawable() != null) {
                        insertScheduleData();
                        scheduleClothing(10 * 1000);
                    }
                } else {
                    Toast.makeText(getContext(), "Please provide data!..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertScheduleData() {
        String SCH_ID = createScheduleID();
        ScheduleModel scheduleModel = new ScheduleModel(SCH_ID, occasionName.getText().toString(), getScheduleDate, topImageUri, bottomImageUri, topImageColor, bottomImageColor, selectionFrom);
        reference.child("Schedule").child(_phone)
                .child(SCH_ID)
                .setValue(scheduleModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            occasionName.setText("");
                            topImage.setImageDrawable(null);
                            bottomImage.setImageDrawable(null);
                            Toast.makeText(getContext(), "Successfully Schedule Reminder", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Not able to Schedule Reminder!..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String createScheduleID() {
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        String SC_HID = "SH" + randomNumber;
        return SC_HID;
    }

    private void notification_cannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Reminder";
            String description = "Reminder Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notification", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleClothing(long interval) {

        long triggerAtMillis = System.currentTimeMillis() + interval;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }


    }

    public void cancel_notification_alarm() {
        alarmManager.cancel(pendingIntent);
    }

    private void showPopupWindow(View v, String Category) {

        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.upperwear_popup, null);

        // Create the PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set background color to transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        // Show the popup window at the center of the anchor view
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // Set up UI elements in the popup
        TextView upper_bottom_txt = popupView.findViewById(R.id.upper_bottom_txt);
        ImageButton close_btn = popupView.findViewById(R.id.close_btn);
        ClosetGridView = popupView.findViewById(R.id.ClosetGridView);


        if (Category.equals("top")) {
            SingleCategoryData("top");

        } else {
            SingleCategoryData("bottom");
        }


        // Set text and click listener
        upper_bottom_txt.setText("This is a popup window!");
        close_btn.setOnClickListener(v1 -> {
            // Close the popup window

            popupWindow.dismiss();
        });

        // Show the popup window
        popupWindow.showAsDropDown(v);
        selectionAdapter = new ClothSelectionAdapter(getContext(), allDataList);
        ClosetGridView.setAdapter(selectionAdapter);

        ClosetGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ImageClass = allDataList.get(position).getClothType();
                if (ImageClass.equals("Shirt") || ImageClass.equals("TShirt")) {
                    Glide.with(getContext()).load(allDataList.get(position).getClotheImageUrl()).into(topImage);
                    topImageUri = allDataList.get(position).getClotheImageUrl();
                    topImageColor = allDataList.get(position).getClothColour();
                } else {
                    Glide.with(getContext()).load(allDataList.get(position).getClotheImageUrl()).into(bottomImage);
                    bottomImageUri = allDataList.get(position).getClotheImageUrl();
                    bottomImageColor = allDataList.get(position).getClothColour();
                }
                popupWindow.dismiss();
            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(requireContext(), style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }


    private void SingleCategoryData(String category) {

        if (category.equals("top")) {
            reference.child("Closet")
                    .child(_phone).child("Category")
                    .child("Shirt")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                allDataList.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    ClothesModel clothesModel = snapshot1.getValue(ClothesModel.class);
                                    allDataList.add(clothesModel);
                                }

                                reference.child("Closet")
                                        .child(_phone).child("Category")
                                        .child("TShirt")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        ClothesModel clothesModel = snapshot1.getValue(ClothesModel.class);
                                                        allDataList.add(clothesModel);
                                                    }
                                                    selectionAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Failed closet extraction", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {

            reference.child("Closet")
                    .child(_phone).child("Category")
                    .child("Jeans")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                allDataList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    ClothesModel clothesModel = dataSnapshot.getValue(ClothesModel.class);
                                    allDataList.add(clothesModel);

                                }
                                reference.child("Closet")
                                        .child(_phone).child("Category")
                                        .child("Trouser")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        ClothesModel clothesModel = dataSnapshot.getValue(ClothesModel.class);
                                                        allDataList.add(clothesModel);
                                                    }
                                                    selectionAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            } else {
                                Toast.makeText(getContext(), "SNAPSHOT EMPTY", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }

    private void getSuggestionsImages() {
        reference.child("Suggestion")
                .child(_phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            suggestionData.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
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





