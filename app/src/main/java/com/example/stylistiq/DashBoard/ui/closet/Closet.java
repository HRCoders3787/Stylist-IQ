package com.example.stylistiq.DashBoard.ui.closet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylistiq.Adapters.GridAdapter.GridAdapter;
import com.example.stylistiq.Dialogs.LoadingAlert;
import com.example.stylistiq.ImageUtils;
import com.example.stylistiq.Login.Login;
import com.example.stylistiq.Models.ClosetModel;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.example.stylistiq.databinding.ActivityMainBinding;


import com.example.stylistiq.ml.ModelUnquant;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Closet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Closet extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String[] item = {"All", "Shirt", "Formal Pants", "Trouser", "Jeans", "TShirt"};
    AutoCompleteTextView category;
    ArrayAdapter<String> adapterItems;
    ActivityMainBinding binding;


    HashMap<String, String> userDetails;

    //Session Manager
    SessionManager sessionManager;
    FloatingActionButton addImageBtn;
    GridView gridView;
    TextView notFound;
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
    public static ArrayList<String> clothData;

    //LoadingAlert loadingAlert = new LoadingAlert(getActivity());

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
        initialiseViews(view);
        initialiseViews(view);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Closet.this)
                        .crop()
                        .compress(100)
                        .maxResultSize(224, 224)
                        .start();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ImageClass = "";
        Uri uri = data.getData();
        Bitmap bitmap = ImageUtils.uriToBitmap(getContext(), uri);
        if (bitmap != null) {
            ImageClass = classifyImage(bitmap);
            Toast.makeText(getContext(), "Image class : " + ImageClass, Toast.LENGTH_SHORT).show();
            filenameCreator(uri, ImageClass);
            trialImage.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "Not able to upload", Toast.LENGTH_SHORT).show();
        }

    }

    //FUNCTION FOR GET CLASS OF IMAGE
    public String classifyImage(Bitmap image) {
        String imageClass = null;

        try {
            ModelUnquant model = ModelUnquant.newInstance(getContext());
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
            Toast.makeText(getContext(), "Error : " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                                insertRealtimeDatabase(uri.toString(), clothID, imageClass);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
    public void insertRealtimeDatabase(String imageUrl, String clothID, String imageClass) {
        //String _fullName = userDetails.get(SessionManager.KEY_PHONENUMBER);

        //ClosetModel closetModel = new ClosetModel(_fullName, _phone);
        ClothesModel clothesModel = new ClothesModel(clothID, imageUrl);

        reference.child("Closet").child(_phone)
                .child("Category").child(imageClass)
                .child(clothID)
                .setValue(clothesModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Successfully realtime updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Not Successfully realtime updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //FUNCTION TO INITIALISE VIEWS
    public void initialiseViews(View view) {
        category = view.findViewById(R.id.category);
        gridView = view.findViewById(R.id.gridView);
        addImageBtn = view.findViewById(R.id.addImageBtn);
        notFound = view.findViewById(R.id.notFound);
//        trialImage = view.findViewById(R.id.trialImage);
//        trialText = view.findViewById(R.id.trialText);
        //loadingAlert.startAlertDialog();

        sessionManager = new SessionManager(getContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        adapterItems = new ArrayAdapter<String>(getContext(), R.layout.drop_down_drawable, item);
        category.setAdapter(adapterItems);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dropDownValue = parent.getItemAtPosition(position).toString();
                clothData.clear();
                dataReferenceCall(dropDownValue);
            }
        });


        clothData = new ArrayList<>();

//        Toast.makeText(getContext(), "from activity : " + clothData.size(), Toast.LENGTH_SHORT).show();
        gridAdapter = new GridAdapter(getContext(), clothData);
        gridView.setAdapter(gridAdapter);

        dataReferenceCall("TShirt");

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }

    public void dataReferenceCall(String imageClass) {
//        loadingAlert.startAlertDialog();
        reference.child("Closet").child(_phone).child("Category")
                .child(imageClass)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            gridView.setVisibility(View.VISIBLE);
                            notFound.setVisibility(View.INVISIBLE);
                            for (DataSnapshot closetSnapshot : snapshot.getChildren()) {
                                String _clotheImageUrl = closetSnapshot.child("clotheImageUrl").getValue(String.class);
                                clothData.add(_clotheImageUrl);
                            }
                            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(clothData);
                            clothData.clear();
                            clothData.addAll(linkedHashSet);
                            gridAdapter.notifyDataSetChanged();
//                            loadingAlert.closeAlertDialog();

                        } else {
//                            loadingAlert.closeAlertDialog();
                            gridView.setVisibility(View.INVISIBLE);
                            clothData.clear();
                            notFound.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                });
    }


}


