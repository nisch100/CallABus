package com.example.nisch100.call_a_bus;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    public static final String channelID = "mychannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+16096517382", null, "My bus from Senior Center to Mall leaves in 10 minutes", null, null);
    }

    public void createNotificationChannel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel n = new NotificationChannel(channelID, "Departure in 10 min", NotificationManager.IMPORTANCE_HIGH);
                n.setDescription("departure reminder");
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.createNotificationChannel(n);
                Notification.Builder nb = new Notification.Builder(context, channelID);
                nb.setContentText("Your bus from Senior Center to Mall leaves in 10 minutes.");
                nb.setContentTitle("CallABus");
                nb.setAutoCancel(true);
                nb.setSmallIcon(R.drawable.ic_launcher_background);
                nm.notify(1, nb.build());
            }
        } catch(Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
