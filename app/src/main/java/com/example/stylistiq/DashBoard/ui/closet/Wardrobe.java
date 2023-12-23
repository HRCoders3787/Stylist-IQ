package com.example.stylistiq.DashBoard.ui.closet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.stylistiq.ImageUtils;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.example.stylistiq.databinding.ActivityMainBinding;
import com.example.stylistiq.ml.ModelUnquant;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Wardrobe extends AppCompatActivity {

    String[] item = {"All", "Shirt", "Formal Pants", "Trouser", "Jeans", "TShirt"};
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
    public static ArrayList<String> clothData, clothClassList, clothDateList;

    ArrayList<ClothesModel> allClothData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe);

        initialiseViews();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ImageClass = "";
        Uri uri = data.getData();
        Bitmap bitmap = ImageUtils.uriToBitmap(getApplicationContext(), uri);
        if (bitmap != null) {
            ImageClass = classifyImage(bitmap);
            Toast.makeText(getApplicationContext(), "Image class : " + ImageClass, Toast.LENGTH_SHORT).show();
            filenameCreator(uri, ImageClass);
            trialImage.setImageBitmap(bitmap);
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
        String filePrefix;
        String fileName = null;
        String clothID = null;
        filePrefix = getImagePrefix(imageClass);
        Random random = new Random();
        int number = random.nextInt(9000) + 1000;
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
        String imagePrefix = null;

        if (imageClass.equals("Shirt")) {
            imagePrefix = "S";
        } else if (imageClass.equals("TShirt")) {
            imagePrefix = "TS";
        } else if (imageClass.equals("Jeans")) {
            imagePrefix = "J";
        } else if (imageClass.equals("Formal pants")) {
            imagePrefix = "FP";
        } else if (imageClass.equals("Trouser")) {
            imagePrefix = "T";
        }

        return imagePrefix;

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
//
                        } else {
                            Toast.makeText(getApplicationContext(), "Not Successfully realtime updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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
//        notFound = findViewById(R.id.notFound);
        trialImage = findViewById(R.id.trialImage);
//        trialText = view.findViewById(R.id.trialText);
        //loadingAlert.startAlertDialog();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        sessionManager = new SessionManager(getApplicationContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        adapterItems = new ArrayAdapter<String>(getApplicationContext(), R.layout.drop_down_drawable, item);
        category.setAdapter(adapterItems);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dropDownValue = parent.getItemAtPosition(position).toString();
//                clothData.clear();
                dataReferenceCall(dropDownValue);
            }
        });


        clothData = new ArrayList<>();
        clothClassList = new ArrayList<>();
        clothDateList = new ArrayList<>();
        allClothData = new ArrayList<>();

//        gridAdapter = new GridAdapter(getApplicationContext(), clothData, clothClassList, clothDateList);
        gridAdapter = new GridAdapter(getApplicationContext(), allClothData);
        gridView.setAdapter(gridAdapter);

        getAllClothesImages();

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.wardrobe_toolbar_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_add) {
                    // Handle "Add" action
                    return true;
                } else if (item.getItemId() == R.id.menu_select) {
                    // Handle "Select" action
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
//        loadingAlert.startAlertDialog();
        if (imageClass.equals("All")) {
            getAllClothesImages();
        } else {
            allClothData.clear();
//            clothClassList.clear();
//            clothDateList.clear();

            reference.child("Closet").child(_phone).child("Category")
                    .child(imageClass)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                gridView.setVisibility(View.VISIBLE);
//                                notFound.setVisibility(View.INVISIBLE);
                                for (DataSnapshot closetSnapshot : snapshot.getChildren()) {
//                                    String _clotheImageUrl = closetSnapshot.child("clotheImageUrl").getValue(String.class);
//                                    String _clothClass = closetSnapshot.child("clothType").getValue(String.class);
//                                    String _uploadDate = closetSnapshot.child("uploadDate").getValue(String.class);

                                    ClothesModel clothesModel = closetSnapshot.getValue(ClothesModel.class);
                                    allClothData.add(clothesModel);


//                                    clothData.add(_clotheImageUrl);
//                                    clothClassList.add(_clothClass);
//                                    clothDateList.add(_uploadDate);
                                }
//                                LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(clothData);
//                                LinkedHashSet<String> linkedHashImageClass = new LinkedHashSet<>(clothClassList);
//                                LinkedHashSet<String> linkedHashDate = new LinkedHashSet<>(clothDateList);

//                                clothData.clear();
//                                clothClassList.clear();
//                                clothDateList.clear();
//                                clothData.addAll(linkedHashSet);
//                                clothClassList.addAll(linkedHashImageClass);
//                                clothDateList.addAll(linkedHashDate);
                                gridAdapter.notifyDataSetChanged();
//                            loadingAlert.closeAlertDialog();

                            } else {
//                            loadingAlert.closeAlertDialog();
                                gridView.setVisibility(View.INVISIBLE);
//                                notFound.setVisibility(View.VISIBLE);
//                                clothData.clear();
//                                clothClassList.clear();
//                                clothDateList.clear();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }
    }

    public void getAllClothesImages() {
//        clothData.clear();
//        allClothData.clear();
//        clothClassList.clear();
//        clothDateList.clear();
        reference.child("Closet").child(_phone).child("Category")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gridView.setVisibility(View.VISIBLE);
                        clothData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot innerSnapShot : dataSnapshot.getChildren()) {
//                                String _clotheImageUrl = innerSnapShot.child("clotheImageUrl").getValue(String.class);
//                                String _clothClass = innerSnapShot.child("clothType").getValue(String.class);
//                                String _uploadDate = innerSnapShot.child("uploadDate").getValue(String.class);

                                ClothesModel clothesModel = innerSnapShot.getValue(ClothesModel.class);
                                allClothData.add(clothesModel);
//                                clothData.add(_clotheImageUrl);
//                                clothClassList.add(_clothClass);
//                                clothDateList.add(_uploadDate);
                            }
                        }
//                        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(clothData);
                        //LinkedHashSet<String> linkedHashImageClass = new LinkedHashSet<>(clothClassList);
//                                LinkedHashSet<String> linkedHashDate = new LinkedHashSet<>(clothDateList);

//                        clothData.clear();
                        //clothClassList.clear();
                        //clothDateList.clear();
//                        clothData.addAll(linkedHashSet);

                        //clothClassList.addAll(linkedHashImageClass);
                        gridAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        allClothData.clear();
                    }
                });
    }

}