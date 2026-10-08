package com.lifeos.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            database = DatabaseProvider.getDatabase(this);

            // Force SQLite/Room to physically open the database.
            database.getOpenHelper().getWritableDatabase();

            Log.d("LifeOS_DB_TEST", "Room database opened successfully.");

            runOnUiThread(() -> {
                // Database initialization completed.
            });
        });
    }
}