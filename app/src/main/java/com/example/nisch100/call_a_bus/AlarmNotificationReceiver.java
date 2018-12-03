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
        createNotificationChannel(context, intent);
        SmsManager smsManager = SmsManager.getDefault();
        Log.i("here", "" + intent.getExtras().keySet().size());
        if (intent.getExtras().getString("rel1") != null) {
            smsManager.sendTextMessage(intent.getExtras().getString("rel1"), null, "My bus from " + intent.getExtras().getString("pickup") + " to " + intent.getExtras().getString("dropoff") + " leaves in " + intent.getExtras().getString("time") + " minutes.", null, null);
        }
        if (intent.getExtras().getString("rel2") != null) {
            smsManager.sendTextMessage(intent.getExtras().getString("rel2"), null, "My bus from " + intent.getExtras().getString("pickup") + " to " + intent.getExtras().getString("dropoff") + " leaves in " + intent.getExtras().getString("time") + " minutes.", null, null);
        }
        if (intent.getExtras().getString("rel3") != null) {
            smsManager.sendTextMessage(intent.getExtras().getString("rel3"), null, "My bus from " + intent.getExtras().getString("pickup") + " to " + intent.getExtras().getString("dropoff") + " leaves in " + intent.getExtras().getString("time") + " minutes.", null, null);
        }

    }

    public void createNotificationChannel(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel n = new NotificationChannel(channelID, "Departure soon", NotificationManager.IMPORTANCE_HIGH);
                n.setDescription("departure reminder");
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.createNotificationChannel(n);
                Notification.Builder nb = new Notification.Builder(context, channelID);
                nb.setContentText("Your bus from " + intent.getExtras().getString("pickup") + " to " + intent.getExtras().getString("dropoff") + " leaves in " + intent.getExtras().getString("time") + " minutes.");
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
