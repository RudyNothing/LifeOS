
package com.lifeos.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private TextView textPendingTaskCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_dashboard);

        View root = findViewById(R.id.dashboardRoot);

        final int initialLeft = root.getPaddingLeft();
        final int initialTop = root.getPaddingTop();
        final int initialRight = root.getPaddingRight();
        final int initialBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    initialLeft + insets.left,
                    initialTop + insets.top,
                    initialRight + insets.right,
                    initialBottom + insets.bottom
            );

            return windowInsets;
        });

        ViewCompat.requestApplyInsets(root);

        textPendingTaskCount =
                findViewById(R.id.textPendingTaskCount);

        taskViewModel = new ViewModelProvider(this)
                .get(TaskViewModel.class);

        taskViewModel.getPendingTasks().observe(this, tasks -> {
            int count = tasks == null ? 0 : tasks.size();
            textPendingTaskCount.setText(String.valueOf(count));
        });

        Button buttonOpenTasks = findViewById(R.id.buttonOpenTasks);
        buttonOpenTasks.setOnClickListener(v ->
                startActivity(new Intent(this, TaskActivity.class))
        );

        Button buttonOpenSchedule = findViewById(R.id.buttonOpenSchedule);

        buttonOpenSchedule.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleActivity.class))
        );

        Button buttonOpenFinance = findViewById(R.id.buttonOpenFinance);

        buttonOpenFinance.setOnClickListener(v ->
                startActivity(new Intent(this, FinanceActivity.class))
        );
    }
}
