
package com.lifeos.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "lifeos_event_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("eventId", 0);
        String eventTitle = intent.getStringExtra("eventTitle");

        if (eventTitle == null || eventTitle.trim().isEmpty()) {
            eventTitle = "Upcoming event";
        }

        createNotificationChannel(context);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("LifeOS reminder")
                        .setContentText(eventTitle)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Your event is coming up: " + eventTitle))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(context);

        // Android 13+ requires notification permission.
        if (Build.VERSION.SDK_INT < 33
                || context.checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            manager.notify(eventId, notification.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Reminders for events saved in LifeOS");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
