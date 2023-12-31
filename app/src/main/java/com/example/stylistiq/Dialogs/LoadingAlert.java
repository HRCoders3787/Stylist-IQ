package com.example.stylistiq.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.example.stylistiq.DashBoard.ui.closet.Closet;
import com.example.stylistiq.R;
import com.example.stylistiq.Signup.SignUp_1;

public class LoadingAlert {

    private Activity activity;
    private AlertDialog dialog;

    private Context context;

    public LoadingAlert(Activity myActivity) {
        activity = myActivity;
    }


//    public LoadingAlert(Closet closet) {
//    }

    public void startAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_loading_layout, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }



    public void closeAlertDialog() {
        dialog.dismiss();
    }
}
