package com.example.alarmclock_codsoft;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri ringtoneUri = Uri.parse(intent.getStringExtra("ringtone_uri"));

        if (action != null) {
            switch (action) {
                case "com.example.alarmclock_codsoft.ALARM_START":
                    startAlarm(context, ringtoneUri);
                    break;
                case "com.example.alarmclock_codsoft.ALARM_SNOOZE":
                    snoozeAlarm(context, ringtoneUri);
                    cancelNotification(context);
                    break;
                case "com.example.alarmclock_codsoft.ALARM_DISMISS":
                    stopAlarm(context);
                    cancelNotification(context);// Directly call stopAlarm() for dismiss action
                    break;
            }
        }
    }

    private void startAlarm(Context context, Uri ringtoneUri) {
        mediaPlayer = new MediaPlayer();


        try {
            pushNOT(context);
            mediaPlayer.setDataSource(context, ringtoneUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setVolume(100, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Vibrate the device
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{1000, 1000}, 0));
        }

        Toast.makeText(context, "Alarm is ringing!", Toast.LENGTH_LONG).show();
    }

    private void snoozeAlarm(Context context, Uri ringtoneUri) {
        // Stop media player and vibrator
        stopAlarm(context);

        // Schedule snooze alarm
        AlarmScheduler.scheduleSnoozeAlarm(context, ringtoneUri);
        Toast.makeText(context, "Alarm snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
    }

    public static void stopAlarm(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

//    public void pushNOT(Context context) {
//        String channelID = "CHANNEL_ID_NOTIFICATION";
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
//        builder.setSmallIcon(R.drawable.ic_nt); // Replace with your icon resource
//        builder.setContentTitle("Alarm");
//        builder.setContentText("Time to wake up!");
//        builder.setAutoCancel(true);
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        // Set up intent for notification click action
//        Intent intent = new Intent(context, MainActivity.class); // Replace with your activity class
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android S+
//        );
//        builder.setContentIntent(pendingIntent);
//
//        // Create notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Alarm Notification";
//            String description = "Notification for Alarm";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//
//        // Notify
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify(1, builder.build());
//    }
private void cancelNotification(Context context) {
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancel(1); // Cancel notification with ID 1
}
public void pushNOT(Context context) {
    String channelID = "CHANNEL_ID_NOTIFICATION";
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
    builder.setSmallIcon(R.drawable.ic_nt); // Replace with your icon resource
    builder.setContentTitle("Alarm");
    builder.setContentText("Time to wake up!");
    builder.setAutoCancel(true);
    builder.setPriority(NotificationCompat.PRIORITY_HIGH);

    // Set up intent for notification click action
    Intent intent = new Intent(context, MainActivity.class); // Replace with your activity class
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android S+
    );
    builder.setContentIntent(pendingIntent);

    // Action for snooze button
    Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
    snoozeIntent.setAction("com.example.alarmclock_codsoft.ALARM_SNOOZE");
    PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android S+
    );
    builder.addAction(R.drawable.ic_snooze, "Snooze (1 min)", snoozePendingIntent);

    // Action for dismiss button
    Intent dismissIntent = new Intent(context, AlarmReceiver.class);
    dismissIntent.setAction("com.example.alarmclock_codsoft.ALARM_DISMISS");
    PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android S+
    );
    builder.addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent);

    // Create notification channel for Android Oreo and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CharSequence name = "Alarm Notification";
        String description = "Notification for Alarm";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Notify
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        // Handle permission request if necessary
        return;
    }
    notificationManager.notify(1, builder.build());
}




}