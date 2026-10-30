package com.lifeos.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.Task;
import com.lifeos.app.viewmodel.TaskViewModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;

    private EditText editTaskTitle;
    private EditText editTaskDescription;

    private Spinner spinnerTaskCategory;
    private Spinner spinnerTaskPriority;

    private TextView textTaskCount;
    private TextView textSelectedDueDate;
    private TextView textEmptyTasks;

    private Button buttonSelectDueDate;
    private LinearLayout taskListContainer;

    private long selectedDueDate = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        applySystemBarInsets();
        initializeViews();
        initializeCategorySpinner();
        initializePrioritySpinner();
        initializeTaskViewModel();
        initializeAddTaskButton();
    }

    private void applySystemBarInsets() {
        View root = findViewById(R.id.main);

        if (root == null) {
            return;
        }

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
    }

    private void initializeViews() {
        editTaskTitle = findViewById(R.id.editTaskTitle);
        editTaskDescription = findViewById(R.id.editTaskDescription);

        spinnerTaskCategory = findViewById(R.id.spinnerTaskCategory);
        spinnerTaskPriority = findViewById(R.id.spinnerTaskPriority);

        textTaskCount = findViewById(R.id.textTaskCount);
        textSelectedDueDate = findViewById(R.id.textSelectedDueDate);
        textEmptyTasks = findViewById(R.id.textEmptyTasks);

        buttonSelectDueDate = findViewById(R.id.buttonSelectDueDate);
        taskListContainer = findViewById(R.id.taskListContainer);

        buttonSelectDueDate.setOnClickListener(v -> showDatePicker());

        Button buttonBack = findViewById(R.id.buttonBackToDashboard);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void initializeCategorySpinner() {
        String[] categories = {"College", "Personal", "Private"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTaskCategory.setAdapter(adapter);
        spinnerTaskCategory.setSelection(0);
    }

    private void initializePrioritySpinner() {
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                priorities
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTaskPriority.setAdapter(adapter);
        spinnerTaskPriority.setSelection(1);
    }

    private void initializeTaskViewModel() {
        taskViewModel = new ViewModelProvider(this)
                .get(TaskViewModel.class);

        taskViewModel.getNormalTasks().observe(this, tasks -> {
            taskListContainer.removeAllViews();

            int count = tasks == null ? 0 : tasks.size();
            textTaskCount.setText(
                    count + (count == 1 ? " task" : " tasks")
            );

            if (tasks == null || tasks.isEmpty()) {
                textEmptyTasks.setVisibility(View.VISIBLE);
                return;
            }

            textEmptyTasks.setVisibility(View.GONE);

            for (Task task : tasks) {
                addTaskToList(task);
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (selectedDueDate > 0L) {
            calendar.setTimeInMillis(selectedDueDate);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 12, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    selectedDueDate = selected.getTimeInMillis();
                    updateDueDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.setButton(
                DatePickerDialog.BUTTON_NEUTRAL,
                "Clear date",
                (dateDialog, which) -> {
                    selectedDueDate = 0L;
                    updateDueDateDisplay();
                }
        );

        dialog.show();
    }

    private void updateDueDateDisplay() {
        if (selectedDueDate == 0L) {
            textSelectedDueDate.setText("No due date");
            buttonSelectDueDate.setText("+ Date");
            return;
        }

        DateFormat format = DateFormat.getDateInstance(
                DateFormat.MEDIUM,
                Locale.getDefault()
        );

        textSelectedDueDate.setText(
                format.format(new Date(selectedDueDate))
        );
        buttonSelectDueDate.setText("Change");
    }

    private void initializeAddTaskButton() {
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = editTaskTitle.getText().toString().trim();
        String description =
                editTaskDescription.getText().toString().trim();

        if (spinnerTaskCategory.getSelectedItem() == null
                || spinnerTaskPriority.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a category and priority",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String category =
                spinnerTaskCategory.getSelectedItem().toString();
        String priority =
                spinnerTaskPriority.getSelectedItem().toString();

        if (title.isEmpty()) {
            editTaskTitle.setError("Enter a task title");
            editTaskTitle.requestFocus();
            return;
        }

        long currentTime = System.currentTimeMillis();

        Task task = new Task(
                title,
                description,
                priority,
                "PENDING",
                category,
                selectedDueDate,
                0L,
                currentTime,
                currentTime,
                0L
        );

        taskViewModel.insert(task);

        editTaskTitle.setText("");
        editTaskDescription.setText("");
        spinnerTaskCategory.setSelection(0);
        spinnerTaskPriority.setSelection(1);

        selectedDueDate = 0L;
        updateDueDateDisplay();

        Toast.makeText(
                this,
                category.equals("Private")
                        ? "Private task saved"
                        : "Task added",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void addTaskToList(Task task) {
        LinearLayout taskCard = new LinearLayout(this);
        taskCard.setOrientation(LinearLayout.VERTICAL);
        taskCard.setBackgroundResource(R.drawable.bg_lifeos_card);

        int padding = (int) (16 * getResources()
                .getDisplayMetrics().density);

        taskCard.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = padding / 2;
        taskCard.setLayoutParams(cardParams);

        TextView titleView = new TextView(this);
        titleView.setText(task.getTitle());
        titleView.setTextSize(18);
        titleView.setTextColor(getColor(R.color.lifeos_text));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView detailsView = new TextView(this);
        detailsView.setTextColor(getColor(R.color.lifeos_muted));
        detailsView.setTextSize(14);

        String details = task.getCategory()
                + "  •  " + task.getPriority();

        if (task.getDescription() != null
                && !task.getDescription().trim().isEmpty()) {
            details += "\n" + task.getDescription();
        }

        if (task.getDueDate() > 0L) {
            DateFormat format = DateFormat.getDateInstance(
                    DateFormat.MEDIUM,
                    Locale.getDefault()
            );
            details += "\nDue " +
                    format.format(new Date(task.getDueDate()));
        }

        detailsView.setText(details);

        TextView statusView = new TextView(this);
        statusView.setTextColor(getColor(R.color.lifeos_muted));
        statusView.setTextSize(12);
        statusView.setText("Status: " + task.getStatus());

        Button completeButton = new Button(this);
        boolean completed =
                "COMPLETED".equalsIgnoreCase(task.getStatus());

        completeButton.setText(
                completed ? "Mark pending" : "Mark completed"
        );

        completeButton.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();

            task.setStatus(completed ? "PENDING" : "COMPLETED");
            task.setUpdatedAt(currentTime);
            task.setCompletedAt(completed ? 0L : currentTime);

            taskViewModel.update(task);
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> taskViewModel.delete(task));

        taskCard.addView(titleView);
        taskCard.addView(detailsView);
        taskCard.addView(statusView);
        taskCard.addView(completeButton);
        taskCard.addView(deleteButton);

        taskListContainer.addView(taskCard);
    }
}