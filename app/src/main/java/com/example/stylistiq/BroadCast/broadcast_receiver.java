package com.example.stylistiq.BroadCast;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.stylistiq.R;
import com.example.stylistiq.Schedule.Schedule_Clothes;

public class broadcast_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent repeating_intent = new Intent(context, Schedule_Clothes.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Drawable drawable = context.getResources().getDrawable(R.drawable.app_full_logo);
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, repeating_intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_full_logo)
                .setColor(ContextCompat.getColor(context, R.color.weather_col))
                .setContentTitle("Reminder")
                .setContentText("Cloth wearing reminder to be wore today")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(200, builder.build());
    }
}
