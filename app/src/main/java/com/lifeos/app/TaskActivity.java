package com.lifeos.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private EditText editTaskTitle, editTaskDescription;
    private TextView categorySelector, prioritySelector;
    private TextView textTaskCount, textSelectedDueDate, textEmptyTasks;
    private Button buttonSelectDueDate;
    private LinearLayout taskListContainer;
    private long selectedDueDate = 0L;

    private String selectedCategory = "College";
    private String selectedPriority = "Medium";

    private final String[] categories = {"College", "Personal", "Private"};
    private final String[] priorities = {"Low", "Medium", "High"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        applySystemBarInsets();
        initializeViews();
        initializeSelectors();
        initializeTaskViewModel();
        initializeAddTaskButton();
    }

    private void applySystemBarInsets() {
        View root = findViewById(R.id.main);
        if (root == null) return;

        final int left = root.getPaddingLeft();
        final int top = root.getPaddingTop();
        final int right = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(left + insets.left, top + insets.top,
                    right + insets.right, bottom + insets.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void initializeViews() {
        editTaskTitle = findViewById(R.id.editTaskTitle);
        editTaskDescription = findViewById(R.id.editTaskDescription);
        categorySelector = findViewById(R.id.spinnerTaskCategory);
        prioritySelector = findViewById(R.id.spinnerTaskPriority);
        textTaskCount = findViewById(R.id.textTaskCount);
        textSelectedDueDate = findViewById(R.id.textSelectedDueDate);
        textEmptyTasks = findViewById(R.id.textEmptyTasks);
        buttonSelectDueDate = findViewById(R.id.buttonSelectDueDate);
        taskListContainer = findViewById(R.id.taskListContainer);

        categorySelector.setOnClickListener(v ->
                showChoiceDialog("Choose category", categories, selectedCategory, true));
        prioritySelector.setOnClickListener(v ->
                showChoiceDialog("Set task priority", priorities, selectedPriority, false));
        buttonSelectDueDate.setOnClickListener(v -> showCustomDatePicker());

        TextView back = findViewById(R.id.buttonBackToDashboard);
        back.setOnClickListener(v -> finish());
    }

    private void initializeSelectors() {
        updateSelectorLabels();
    }

    private void updateSelectorLabels() {
        categorySelector.setText(categoryLabel(selectedCategory) + "     ⌄");

        prioritySelector.setText(priorityLabel(selectedPriority));

        categorySelector.setCompoundDrawablesWithIntrinsicBounds(
                categoryIcon(selectedCategory),
                null,
                null,
                null
        );

        categorySelector.setCompoundDrawablePadding(dp(12));

        prioritySelector.setCompoundDrawablesWithIntrinsicBounds(
                priorityDot(selectedPriority),
                null,
                null,
                null
        );

        prioritySelector.setCompoundDrawablePadding(dp(12));
    }

    private String categoryLabel(String value) {
        if ("College".equals(value)) return "▣   College";
        if ("Personal".equals(value)) return "✦   Personal";
        return "◇   Private";
    }

    private String priorityLabel(String value) {
        return value + " priority";
    }

    private android.graphics.drawable.Drawable categoryIcon(String value) {
        int color = "College".equals(value) ? 0xFF4D91E8
                : "Personal".equals(value) ? 0xFF9A6CE8 : 0xFFE16C87;
        return makeDot(color);
    }

    private android.graphics.drawable.Drawable priorityDot(String value) {
        int color = "High".equals(value) ? 0xFFE66B75
                : "Medium".equals(value) ? 0xFFE5A344 : 0xFF35B58A;
        return makeDot(color);
    }

    private android.graphics.drawable.Drawable makeDot(int color) {
        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(color);
        dot.setSize(dp(10), dp(10));
        return dot;
    }

    private void showChoiceDialog(String title, String[] options,
                                  String selected, boolean isCategory) {
        Dialog dialog = new Dialog(this);
        LinearLayout panel = dialogPanel();

        addEyebrow(panel, isCategory ? "ORGANISE YOUR TASK" : "SET YOUR FOCUS");
        addText(panel, title, 23, R.color.lifeos_text, true, dp(5));

        for (String option : options) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dp(14), dp(14), dp(14), dp(14));
            row.setBackgroundResource("".equals(option) ? R.drawable.bg_dialog_surface
                    : R.drawable.bg_selector_control);

            TextView marker = new TextView(this);
            marker.setText(isCategory
                    ? ("College".equals(option) ? "▣" : "Personal".equals(option) ? "✦" : "◇")
                    : ("High".equals(option) ? "●" : "Medium".equals(option) ? "●" : "●"));
            marker.setTextSize(17);
            int markerColor = isCategory
                    ? ("College".equals(option) ? 0xFF4D91E8 : "Personal".equals(option)
                    ? 0xFF9A6CE8 : 0xFFE16C87)
                    : ("High".equals(option) ? 0xFFE66B75 : "Medium".equals(option)
                    ? 0xFFE5A344 : 0xFF35B58A);
            marker.setTextColor(markerColor);

            TextView label = new TextView(this);
            label.setText(option);
            label.setTextColor(getColor(R.color.lifeos_text));
            label.setTextSize(15);
            label.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.leftMargin = dp(12);
            row.addView(marker);
            row.addView(label, lp);

            TextView state = new TextView(this);
            state.setText(option.equals(selected) ? "✓" : "");
            state.setTextColor(getColor(R.color.lifeos_primary));
            state.setTextSize(18);
            state.setTypeface(null, Typeface.BOLD);
            row.addView(state);

            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
            rp.topMargin = dp(9);
            panel.addView(row, rp);

            row.setOnClickListener(v -> {
                if (isCategory) selectedCategory = option;
                else selectedPriority = option;
                updateSelectorLabels();
                dialog.dismiss();
            });
        }

        addDialogFooter(panel, dialog);
        showDialog(dialog, panel);
    }

    private void showCustomDatePicker() {
        Dialog dialog = new Dialog(this);
        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "PLAN AHEAD");
        addText(panel, "Choose due date", 23, R.color.lifeos_text, true, dp(5));
        addText(panel, "Give this task a target to work towards.", 13,
                R.color.lifeos_muted, false, dp(3));

        Calendar calendar = Calendar.getInstance();
        if (selectedDueDate > 0L) calendar.setTimeInMillis(selectedDueDate);

        DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(true);
        picker.setSpinnersShown(false);
        picker.setMinDate(System.currentTimeMillis() - 1000L);
        picker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        LinearLayout.LayoutParams pickerParams =
                new LinearLayout.LayoutParams(-1, -2);
        pickerParams.topMargin = dp(10);
        panel.addView(picker, pickerParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button clear = actionButton("Clear", false);
        Button confirm = actionButton("Use this date", true);

        actions.addView(clear, new LinearLayout.LayoutParams(0, dp(48), 1));
        LinearLayout.LayoutParams confirmParams =
                new LinearLayout.LayoutParams(0, dp(48), 1);
        confirmParams.leftMargin = dp(9);
        actions.addView(confirm, confirmParams);

        LinearLayout.LayoutParams actionParams =
                new LinearLayout.LayoutParams(-1, -2);
        actionParams.topMargin = dp(15);
        panel.addView(actions, actionParams);

        clear.setOnClickListener(v -> {
            selectedDueDate = 0L;
            updateDueDateDisplay();
            dialog.dismiss();
        });

        confirm.setOnClickListener(v -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth(),
                    12, 0, 0);
            chosen.set(Calendar.MILLISECOND, 0);
            selectedDueDate = chosen.getTimeInMillis();
            updateDueDateDisplay();
            dialog.dismiss();
        });

        showDialog(dialog, panel);
    }

    private void updateDueDateDisplay() {
        if (selectedDueDate == 0L) {
            textSelectedDueDate.setText("No due date selected");
            buttonSelectDueDate.setText("+ Add date");
            return;
        }
        DateFormat format = DateFormat.getDateInstance(
                DateFormat.MEDIUM, Locale.getDefault());
        textSelectedDueDate.setText(format.format(new Date(selectedDueDate)));
        buttonSelectDueDate.setText("Change date");
    }

    private void initializeTaskViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getNormalTasks().observe(this, tasks -> {
            taskListContainer.removeAllViews();
            int count = tasks == null ? 0 : tasks.size();
            textTaskCount.setText(count + (count == 1 ? " task" : " tasks"));
            if (tasks == null || tasks.isEmpty()) {
                textEmptyTasks.setVisibility(View.VISIBLE);
                return;
            }
            textEmptyTasks.setVisibility(View.GONE);
            for (Task task : tasks) addTaskToList(task);
        });
    }

    private void initializeAddTaskButton() {
        Button add = findViewById(R.id.buttonAddTask);
        add.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = editTaskTitle.getText().toString().trim();
        String description = editTaskDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTaskTitle.setError("Enter a task title");
            editTaskTitle.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();
        Task task = new Task(title, description, selectedPriority,
                "PENDING", selectedCategory, selectedDueDate, 0L,
                now, now, 0L);

        taskViewModel.insert(task);
        String savedCategory = selectedCategory;
        editTaskTitle.setText("");
        editTaskDescription.setText("");
        selectedCategory = "College";
        selectedPriority = "Medium";
        selectedDueDate = 0L;
        updateSelectorLabels();
        updateDueDateDisplay();

        Toast.makeText(this,
                "Private".equals(savedCategory) ? "Private task saved" : "Task added",
                Toast.LENGTH_SHORT).show();
    }

    private void addTaskToList(Task task) {
        int pad = dp(16);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(pad, pad, pad, pad);
        card.setBackgroundResource(R.drawable.bg_lifeos_card);

        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.bottomMargin = dp(12);
        card.setLayoutParams(cp);

        boolean completed = "COMPLETED".equalsIgnoreCase(task.getStatus());

        TextView meta = new TextView(this);
        meta.setText(task.getCategory().toUpperCase(Locale.ROOT) + "  •  "
                + task.getPriority().toUpperCase(Locale.ROOT));
        meta.setTextColor(getColor(R.color.lifeos_primary));
        meta.setTextSize(10);
        meta.setLetterSpacing(0.1f);
        meta.setTypeface(null, Typeface.BOLD);

        TextView title = new TextView(this);
        title.setText(task.getTitle());
        title.setTextColor(getColor(R.color.lifeos_text));
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0, dp(7), 0, dp(5));

        TextView details = new TextView(this);
        details.setTextColor(getColor(R.color.lifeos_muted));
        details.setTextSize(13);
        String detail = task.getDescription() == null ? "" : task.getDescription().trim();
        if (task.getDueDate() > 0L) {
            DateFormat f = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            detail += (detail.isEmpty() ? "" : "\n") + "Due " + f.format(new Date(task.getDueDate()));
        }
        details.setText(detail);
        details.setVisibility(detail.isEmpty() ? View.GONE : View.VISIBLE);

        TextView status = new TextView(this);
        status.setText(completed ? "✓  COMPLETED" : "●  IN PROGRESS");
        status.setTextColor(getColor(R.color.lifeos_muted));
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);
        status.setPadding(0, dp(12), 0, dp(9));

        Button complete = actionButton(completed ? "Mark as pending" : "Complete task", true);
        complete.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            task.setStatus(completed ? "PENDING" : "COMPLETED");
            task.setUpdatedAt(now);
            task.setCompletedAt(completed ? 0L : now);
            taskViewModel.update(task);
        });

        Button delete = actionButton("Delete task", false);
        delete.setOnClickListener(v -> showDeleteTaskDialog(task));

        card.addView(meta);
        card.addView(title);
        card.addView(details);
        card.addView(status);
        card.addView(complete);
        LinearLayout.LayoutParams delp = new LinearLayout.LayoutParams(-1, dp(46));
        delp.topMargin = dp(8);
        card.addView(delete, delp);
        taskListContainer.addView(card);
    }


    private void showDeleteTaskDialog(Task task) {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "TASK MANAGER");

        addText(
                panel,
                "Delete this task?",
                23,
                R.color.lifeos_text,
                true,
                dp(7)
        );

        addText(
                panel,
                "\"" + task.getTitle() + "\" will be permanently removed.",
                14,
                R.color.lifeos_muted,
                false,
                dp(12)
        );

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);

        Button keepButton = createDeleteDialogButton(
                "Keep task",
                0xFFF7E8EF,
                0xFF633449
        );

        Button deleteButton = createDeleteDialogButton(
                "Delete",
                0xFFFBE9EA,
                0xFFD45B65
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(0, dp(50), 1);

        actions.addView(keepButton, buttonParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(50), 1);

        deleteParams.leftMargin = dp(9);

        actions.addView(deleteButton, deleteParams);

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(-1, -2);

        actionsParams.topMargin = dp(20);

        panel.addView(actions, actionsParams);

        keepButton.setOnClickListener(v -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {
            taskViewModel.delete(task);
            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Task deleted",
                    Toast.LENGTH_SHORT
            ).show();
        });

        showDialog(dialog, panel);
    }

    private Button createDeleteDialogButton(
            String text,
            int backgroundColor,
            int textColor
    ) {

        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setTypeface(null, Typeface.BOLD);

        button.setTextColor(textColor);

        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);

        button.setPadding(dp(8), 0, dp(8), 0);
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(false);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setColor(backgroundColor);
        background.setCornerRadius(dp(16));
        background.setStroke(dp(1), textColor);

        button.setBackground(background);
        button.setBackgroundTintList(null);
        button.setElevation(0);

        return button;
    }

    private LinearLayout dialogPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(21), dp(21), dp(21), dp(21));
        panel.setBackgroundResource(R.drawable.bg_dialog_surface);
        return panel;
    }

    private void addEyebrow(LinearLayout panel, String text) {
        TextView eyebrow = new TextView(this);
        eyebrow.setText(text);
        eyebrow.setTextColor(getColor(R.color.lifeos_primary));
        eyebrow.setTextSize(10);
        eyebrow.setLetterSpacing(0.15f);
        eyebrow.setTypeface(null, Typeface.BOLD);
        panel.addView(eyebrow);
    }

    private void addText(LinearLayout panel, String text, int size,
                         int color, boolean bold, int topMargin) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(color));
        view.setTextSize(size);
        if (bold) view.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = topMargin;
        panel.addView(view, lp);
    }

    private void addDialogFooter(LinearLayout panel, Dialog dialog) {
        Button close = actionButton("Cancel", false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(46));
        lp.topMargin = dp(14);
        panel.addView(close, lp);
        close.setOnClickListener(v -> dialog.dismiss());
    }

    private Button actionButton(String text, boolean primary) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextColor(getColor(primary ? android.R.color.white : R.color.lifeos_text));
        button.setBackgroundResource(primary
                ? R.drawable.bg_button_primary : R.drawable.bg_selector_control);
        return button;
    }

    private void showDialog(Dialog dialog, View content) {
        dialog.setContentView(content);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90f);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.dimAmount = 0.62f;
            window.setAttributes(params);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
