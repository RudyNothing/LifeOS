
package com.lifeos.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null
                || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        // Keep the receiver alive while Room is queried in the background.
        final PendingResult pendingResult = goAsync();
        final Context appContext = context.getApplicationContext();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                AppDatabase database =
                        DatabaseProvider.getDatabase(appContext);

                List<Event> events =
                        database.eventDao().getAllEventsForRecovery();

                long now = System.currentTimeMillis();

                for (Event event : events) {
                    long reminderTime = event.getReminderDate();

                    if (event.getId() > 0 && reminderTime > now) {
                        EventReminderScheduler.scheduleReminder(
                                appContext,
                                event.getId(),
                                event.getTitle(),
                                reminderTime
                        );
                    }
                }
            } catch (Exception e) {
                android.util.Log.e(
                        "EventBootReceiver",
                        "Failed to restore event reminders",
                        e
                );
            } finally {
                pendingResult.finish();
                executor.shutdown();
            }
        });
    }
}
