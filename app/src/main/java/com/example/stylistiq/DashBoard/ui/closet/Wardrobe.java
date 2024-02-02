package com.example.stylistiq.DashBoard.ui.closet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.example.stylistiq.Adapters.GridAdapter.GridAdapter;
import com.example.stylistiq.DashBoard.suggestionAlgo.Algorithm;
import com.example.stylistiq.DashBoard.suggestionAlgo.DataBase;
import com.example.stylistiq.ImageUtils;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.OnDataLoadedListener;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.example.stylistiq.databinding.ActivityMainBinding;
import com.example.stylistiq.ml.ModelUnquant;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Wardrobe extends AppCompatActivity implements OnDataLoadedListener {

    String[] item = {"All", "Shirt", "Formal Pants", "Trouser", "Jeans", "TShirt"};
    String[] parseClothData = new String[6];


    AutoCompleteTextView category;
    ArrayAdapter<String> adapterItems;
    ActivityMainBinding binding;

    HashMap<String, String> userDetails;

    //Session Manager
    SessionManager sessionManager;
    ImageButton back_btn, menu_btn;
    GridView gridView;
    //    TextView notFound;
    ImageView trialImage;
    TextView trialText;
    String _phone;
    int imageSize = 224;

    //Firebase Storage
    StorageReference storageReference;

    //    Realtime database
    FirebaseDatabase database;
    DatabaseReference reference;
    GridAdapter gridAdapter;
    ArrayList<ClothesModel> allClothData;
    //    Algorithm suggestionAlgo = new Algorithm(getApplicationContext());
    List<String> topListItems = Arrays.asList("Shirt", "TShirt");
    List<String> bottomListItems = Arrays.asList("Jeans", "Trouser", "Formal Pants");

    Algorithm algorithm;
    ArrayList<Integer> bottomColor = new ArrayList<>();
    OnDataLoadedListener onDataLoadedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe);

        onDataLoadedListener = this;
        algorithm = new Algorithm(getApplicationContext());

        initialiseViews();
    }


    //FUNCTION TO GET IMAGES SELECTED FROM CAMERA OR GALLERY
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ImageClass = "";
        Uri uri = data.getData();
        Bitmap bitmap = ImageUtils.uriToBitmap(getApplicationContext(), uri);
        if (bitmap != null) {
            trialImage.setImageBitmap(bitmap);
            ImageClass = classifyImage(bitmap);
            Toast.makeText(getApplicationContext(), "Image class : " + ImageClass, Toast.LENGTH_SHORT).show();
            filenameCreator(uri, ImageClass);
        } else {
            Toast.makeText(getApplicationContext(), "Not able to upload", Toast.LENGTH_SHORT).show();
        }

    }

    //FUNCTION FOR GET CLASS OF IMAGE
    public String classifyImage(Bitmap image) {
        String imageClass = null;

        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"Shirt",
                    "TShirt",
                    "Jeans",
                    "Formal Pants",
                    "Trouser"};

            imageClass = classes[maxPos];

            // Releases model resources if no longer used.
            model.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        return imageClass;
    }

    //FUNCTION TO CREATE IMAGE FILENAME
    public void filenameCreator(Uri imageUri, String imageClass) {
        String filePrefix = "";
        String fileName = null;
        String clothID = null;
        filePrefix = getImagePrefix(imageClass);

        Random random = new Random();
        int number = random.nextInt(9000) + 1000;
        if (!filePrefix.isEmpty()) {
            if (filePrefix.equals("S")) {
                clothID = filePrefix + number;
                fileName = "Shirts/" + clothID;
            } else if (filePrefix.equals("TS")) {
                clothID = filePrefix + number;
                fileName = "TShirt/" + clothID;
            } else if (filePrefix.equals("J")) {
                clothID = filePrefix + number;
                fileName = "Jeans/" + clothID;
            } else if (filePrefix.equals("FP")) {
                clothID = filePrefix + number;
                fileName = "Formal Pants/" + clothID;
            } else if (filePrefix.equals("T")) {
                clothID = filePrefix + number;
                fileName = "Trouser/" + clothID;
            }
        } else {
            Toast.makeText(this, "file prefix is empty", Toast.LENGTH_SHORT).show();
        }
        firebaseStorageUpload(fileName, imageUri, clothID, imageClass);

    }

    //FUNCTION TO UPLOAD IMAGE TO FIREBASE STORAGE
    public void firebaseStorageUpload(String folderName, Uri imageUri, String clothID, String imageClass) {
        storageReference = FirebaseStorage.getInstance().getReference("clothes/images/" + folderName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                getClothColour(uri.toString(), clothID, imageClass);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //FUNCTION TO GET IMAGE-ID PREFIX VALUE
    public String getImagePrefix(String imageClass) {

        if (imageClass.equals("Shirt")) {
            return "S";
        } else if (imageClass.equals("TShirt")) {
            return "TS";
        } else if (imageClass.equals("Jeans")) {
            return "J";
        } else if (imageClass.equals("Formal Pants")) {
            return "FP";
        } else if (imageClass.equals("Trouser")) {
            return "T";
        }
        return null;
    }

    //FUNCTION TO INSERT VALUES TO REALTIME DATABASE
    public void insertRealtimeDatabase(String imageUrl, String clothID, String imageClass, int clothColour) {
        String UploadDate = getCurrentDate();

        ClothesModel clothesModel = new ClothesModel(clothID, imageUrl, clothColour, imageClass, UploadDate);
        reference.child("Closet").child(_phone)

                .child("Category").child(imageClass)
                .child(clothID)
                .setValue(clothesModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (topListItems.contains(imageClass)) {
                                getBottomColors(clothColour, imageUrl);

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Not Successfully realtime updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getBottomColors(int topColor, String topImage) {
        HashMap<Integer, String> bottomImage = new HashMap<>();
        reference.child("Closet")
                .child(_phone).child("Category")
                .child("Jeans")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ClothesModel clothesModel = dataSnapshot.getValue(ClothesModel.class);
                                bottomColor.add(clothesModel.getClothColour());
                                bottomImage.put(clothesModel.getClothColour(), clothesModel.getClotheImageUrl());
                            }
                            reference.child("Closet")
                                    .child(_phone).child("Category")
                                    .child("Trouser")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                    ClothesModel clothesModel = dataSnapshot1.getValue(ClothesModel.class);
                                                    bottomColor.add(clothesModel.getClothColour());
                                                    bottomImage.put(clothesModel.getClothColour(), clothesModel.getClotheImageUrl());
                                                }
                                                List<Integer> suggestedColors = algorithm.getColorSuggestions(topColor, bottomColor);
                                                DataBase dataBase = new DataBase
                                                        (getApplicationContext(), topColor, suggestedColors.get(0),
                                                                topImage, bottomImage.get(suggestedColors.get(0)));
                                                if (dataBase.insertToDatabase()) {
                                                    Toast.makeText(Wardrobe.this, "INSERTED SUGGESTIONS", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Wardrobe.this, "INSERTED SUGGESTIONS FAILED", Toast.LENGTH_SHORT).show();
                                                }
                                            }
//
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    //FUNCTION TO GET CURRENT DATE
    public String getCurrentDate() {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd, MMM yyyy");
        }
        String date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = currentDate.format(formatter);
        }

        return date;

    }

    //FUNCTION TO GET COLOUR OF IMAGE
    public void getClothColour(String imageUrl, String clothID, String imageClass) {
        Bitmap bitmap = ((BitmapDrawable) trialImage.getDrawable()).getBitmap();
        if (bitmap != null) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int secondDominantColor;
                    // Access the dominant color (or other colors) from the palette
                    Palette.Swatch secondDominantSwatch = getSecondDominantSwatch(palette);
                    secondDominantColor = secondDominantSwatch != null ? secondDominantSwatch.getRgb() : 0;

                    insertRealtimeDatabase(imageUrl, clothID, imageClass, secondDominantColor);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "bitmap empty", Toast.LENGTH_SHORT).show();
        }

    }

    private Palette.Swatch getSecondDominantSwatch(Palette palette) {
        // Get all the swatches from the palette
        List<Palette.Swatch> swatches = new ArrayList<>(palette.getSwatches());

        // Sort the swatches by population in descending order
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });

        // Return the second swatch if available
        return swatches.size() > 1 ? swatches.get(1) : null;
    }

    //FUNCTION TO INITIALISE VIEWS
    public void initialiseViews() {
        category = findViewById(R.id.category);
        gridView = findViewById(R.id.gridView);
        back_btn = findViewById(R.id.back_btn);
        menu_btn = findViewById(R.id.menu_btn);
        trialImage = findViewById(R.id.trialImage);

        back_btn.setOnClickListener(v -> finish());

        menu_btn.setOnClickListener(v -> showPopupMenu(v));

        sessionManager = new SessionManager(getApplicationContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        adapterItems = new ArrayAdapter<String>(getApplicationContext(), R.layout.drop_down_drawable, item);
        category.setAdapter(adapterItems);
        category.setOnItemClickListener((parent, view, position, id) -> {
            String dropDownValue = parent.getItemAtPosition(position).toString();
            dataReferenceCall(dropDownValue);
        });

        allClothData = new ArrayList<>();
        gridAdapter = new GridAdapter(getApplicationContext(), allClothData);
        gridView.setAdapter(gridAdapter);

        getAllClothesImages();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(Wardrobe.this, WardrobeOutfitSuggestions.class);
                intent.putExtra("clothData", setClothData_toArray(allClothData, position));
                startActivity(intent);

            }
        });
    }


    public String[] setClothData_toArray(ArrayList<ClothesModel> list, int pos) {
        parseClothData[0] = list.get(pos).getClothID();
        parseClothData[1] = list.get(pos).getClotheImageUrl();
        parseClothData[2] = list.get(pos).getClothType();
        parseClothData[3] = String.valueOf(list.get(pos).getClothColour());
        parseClothData[4] = list.get(pos).getUploadDate();
        parseClothData[5] = getColorName(list.get(pos).getClothColour());
        return parseClothData;
    }

    private String getColorName(int colorInt) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("RED", Color.RED);
        colorMap.put("GREEN", Color.GREEN);
        colorMap.put("BLUE", Color.BLUE);
        colorMap.put("YELLOW", Color.YELLOW);
        colorMap.put("CYAN", Color.CYAN);
        colorMap.put("MAGENTA", Color.MAGENTA);
        colorMap.put("BLACK", Color.BLACK);
        colorMap.put("WHITE", Color.WHITE);

        String colorName = null;

        for (Map.Entry<String, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() == colorInt) {
                colorName = entry.getKey();
                break;
            }
        }

        if (colorName == null) {
            int alpha = Color.alpha(colorInt);
            int red = Color.red(colorInt);
            int green = Color.green(colorInt);
            int blue = Color.blue(colorInt);

            String hexColor = String.format("#%02x%02x%02x", red, green, blue);

            colorName = hexColor;
        }

        return colorName;
    }

    //FUNCTION FOR MENU CLICK LISTENER
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.wardrobe_toolbar_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_add) {
                    ImagePicker.with(Wardrobe.this)
                            .crop()
                            .compress(100)
                            .maxResultSize(224, 224)
                            .start();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wardrobe_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle other menu items if needed
        return super.onOptionsItemSelected(item);
    }


    //FUNCTION TO GET THE CLOSET IMAGES FROM DATABASE
    public void dataReferenceCall(String imageClass) {

        if (imageClass.equals("All")) {
            allClothData.clear();
            getAllClothesImages();
        } else {
            allClothData.clear();
            reference.child("Closet").child(_phone).child("Category")
                    .child(imageClass)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                gridView.setVisibility(View.VISIBLE);

                                for (DataSnapshot closetSnapshot : snapshot.getChildren()) {

                                    ClothesModel clothesModel = closetSnapshot.getValue(ClothesModel.class);
                                    allClothData.add(clothesModel);
                                }

                                gridAdapter.notifyDataSetChanged();
                            } else {

                                gridView.setVisibility(View.INVISIBLE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }
    }


    //FUNCTION RETURNS ALL DATA FROM CLOSET FIREBASE TABLE.
    public void getAllClothesImages() {
        reference.child("Closet").child(_phone).child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gridView.setVisibility(View.VISIBLE);
                        allClothData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot innerSnapShot : dataSnapshot.getChildren()) {

                                ClothesModel clothesModel = innerSnapShot.getValue(ClothesModel.class);
                                allClothData.add(clothesModel);

                            }
                        }

                        gridAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        allClothData.clear();
                    }
                });
    }

    @Override
    public void onDataLoader(List<Integer> bottomColor) {
        Toast.makeText(this, "BOTTOM " + bottomColor.toString(), Toast.LENGTH_SHORT).show();
    }
}