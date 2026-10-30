
package com.lifeos.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.app.AlarmManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.Event;
import com.lifeos.app.viewmodel.EventViewModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private EditText editEventTitle;
    private EditText editEventDescription;
    private EditText editEventLocation;

    private Spinner spinnerEventCategory;
    private CheckBox checkEventAllDay;

    private TextView textEventStart;
    private TextView textEventEnd;
    private TextView textNoEvents;

    private Button buttonEventStart;
    private Button buttonEventEnd;
    private Button buttonSaveEvent;

    private LinearLayout eventListContainer;
    private EventViewModel eventViewModel;

    private long selectedStartTime = 0L;
    private long selectedEndTime = 0L;

    private Event editingEvent = null;

    private CheckBox checkEventReminder;
    private Button buttonEventReminder;
    private TextView textEventReminder;

    private long selectedReminderTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_schedule);

        applySystemBarInsets();
        initializeViews();
        initializeCategorySpinner();
        initializeViewModel();
        initializeListeners();
        updateDateButtonLabels();
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private void applySystemBarInsets() {
        View root = findViewById(R.id.scheduleRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    bars.left + dp(20),
                    bars.top + dp(8),
                    bars.right + dp(20),
                    bars.bottom + dp(20)
            );

            return insets;
        });

        ViewCompat.requestApplyInsets(root);
    }

    private void initializeViews() {
        editEventTitle = findViewById(R.id.editEventTitle);
        editEventDescription = findViewById(R.id.editEventDescription);
        editEventLocation = findViewById(R.id.editEventLocation);

        spinnerEventCategory = findViewById(R.id.spinnerEventCategory);
        checkEventAllDay = findViewById(R.id.checkEventAllDay);

        textEventStart = findViewById(R.id.textEventStart);
        textEventEnd = findViewById(R.id.textEventEnd);
        textNoEvents = findViewById(R.id.textNoEvents);

        buttonEventStart = findViewById(R.id.buttonEventStart);
        buttonEventEnd = findViewById(R.id.buttonEventEnd);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);

        checkEventReminder = findViewById(R.id.checkEventReminder);
        buttonEventReminder = findViewById(R.id.buttonEventReminder);
        textEventReminder = findViewById(R.id.textEventReminder);

        eventListContainer = findViewById(R.id.eventListContainer);
        findViewById(R.id.buttonCancelEventEdit).setOnClickListener(
                v -> clearForm()
        );
        findViewById(R.id.buttonScheduleBack).setOnClickListener(
                v -> finish()
        );
    }

    private void initializeCategorySpinner() {
        String[] categories = {
                "Personal", "College", "Work", "Appointment", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEventCategory.setAdapter(adapter);
    }

    private void initializeViewModel() {
        eventViewModel = new ViewModelProvider(this)
                .get(EventViewModel.class);

        eventViewModel.getAllEvents().observe(this, events -> {
            eventListContainer.removeAllViews();

            if (events == null || events.isEmpty()) {
                textNoEvents.setVisibility(View.VISIBLE);
                return;
            }

            textNoEvents.setVisibility(View.GONE);

            for (Event event : events) {
                addEventCard(event);
            }
        });
    }

    private void initializeListeners() {
        buttonEventStart.setOnClickListener(v -> pickDateTime(true));
        buttonEventEnd.setOnClickListener(v -> pickDateTime(false));
        buttonSaveEvent.setOnClickListener(v -> saveEvent());

        buttonEventReminder.setOnClickListener(v -> pickReminderDateTime());

        checkEventReminder.setOnCheckedChangeListener((button, checked) -> {
            buttonEventReminder.setEnabled(checked);

            if (!checked) {
                selectedReminderTime = 0L;
                textEventReminder.setText("No reminder set");
            }
        });

        buttonEventReminder.setEnabled(checkEventReminder.isChecked());

        checkEventAllDay.setOnCheckedChangeListener(
                (button, checked) -> updateDateButtonLabels()
        );
    }

    private void pickReminderDateTime() {
        Calendar calendar = Calendar.getInstance();

        if (selectedReminderTime > 0L) {
            calendar.setTimeInMillis(selectedReminderTime);
        }

        new DatePickerDialog(
                this,
                (picker, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    new TimePickerDialog(
                            this,
                            (timePicker, hour, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                selectedReminderTime = calendar.getTimeInMillis();

                                textEventReminder.setText(
                                        "Reminder: " +
                                                DateFormat.getDateTimeInstance(
                                                        DateFormat.MEDIUM,
                                                        DateFormat.SHORT,
                                                        Locale.getDefault()
                                                ).format(new Date(selectedReminderTime))
                                );
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
    private void updateDateButtonLabels() {
        boolean allDay = checkEventAllDay.isChecked();

        buttonEventStart.setText(
                allDay ? "Choose start date" : "Choose start date and time"
        );

        buttonEventEnd.setText(
                allDay ? "Choose end date" : "Choose end date and time"
        );
    }

    private void pickDateTime(boolean isStart) {
        Calendar calendar = Calendar.getInstance();

        long currentValue = isStart
                ? selectedStartTime : selectedEndTime;

        if (currentValue > 0L) {
            calendar.setTimeInMillis(currentValue);
        }

        new DatePickerDialog(
                this,
                (picker, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    if (checkEventAllDay.isChecked()) {
                        calendar.set(Calendar.HOUR_OF_DAY,
                                isStart ? 0 : 23);
                        calendar.set(Calendar.MINUTE,
                                isStart ? 0 : 59);
                        calendar.set(Calendar.SECOND,
                                isStart ? 0 : 59);
                        calendar.set(Calendar.MILLISECOND,
                                isStart ? 0 : 999);

                        applySelectedTime(isStart,
                                calendar.getTimeInMillis());
                    } else {
                        new TimePickerDialog(
                                this,
                                (timePicker, hour, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, 0);
                                    calendar.set(Calendar.MILLISECOND, 0);

                                    applySelectedTime(
                                            isStart,
                                            calendar.getTimeInMillis()
                                    );
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        ).show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void applySelectedTime(boolean isStart, long timestamp) {
        if (isStart) {
            selectedStartTime = timestamp;
            textEventStart.setText(
                    "Starts: " + formatDateTime(timestamp)
            );
        } else {
            selectedEndTime = timestamp;
            textEventEnd.setText(
                    "Ends: " + formatDateTime(timestamp)
            );
        }
    }

    private String formatDateTime(long timestamp) {
        if (checkEventAllDay.isChecked()) {
            DateFormat format = DateFormat.getDateInstance(
                    DateFormat.MEDIUM,
                    Locale.getDefault()
            );
            return format.format(new Date(timestamp));
        }

        DateFormat format = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
        );

        return format.format(new Date(timestamp));
    }

    private void saveEvent() {
        String title = editEventTitle.getText().toString().trim();

        if (title.isEmpty()) {
            editEventTitle.setError("Enter an event title");
            editEventTitle.requestFocus();
            return;
        }

        if (selectedStartTime == 0L || selectedEndTime == 0L) {
            Toast.makeText(this,
                    "Choose both a start and end date/time",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedEndTime < selectedStartTime) {
            Toast.makeText(this,
                    "End must be after start",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String description =
                editEventDescription.getText().toString().trim();

        String location =
                editEventLocation.getText().toString().trim();

        String category =
                spinnerEventCategory.getSelectedItem().toString();

        long now = System.currentTimeMillis();

        long reminderTime = 0L;

        if (checkEventReminder.isChecked()) {
            if (selectedReminderTime <= System.currentTimeMillis()) {
                Toast.makeText(
                        this,
                        "Choose a reminder time in the future",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (selectedReminderTime >= selectedStartTime) {
                Toast.makeText(
                        this,
                        "The reminder must be before the event starts",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            reminderTime = selectedReminderTime;
        }

        if (editingEvent == null) {
            Event event = new Event(
                    title,
                    description,
                    selectedStartTime,
                    selectedEndTime,
                    location,
                    checkEventAllDay.isChecked(),
                    reminderTime,
                    category,
                    now,
                    now
            );

            eventViewModel.insertEvent(event, savedEvent -> {
                if (savedEvent.getReminderDate() > 0) {
                    EventReminderScheduler.scheduleReminder(
                            ScheduleActivity.this,
                            savedEvent.getId(),
                            savedEvent.getTitle(),
                            savedEvent.getReminderDate()
                    );
                }
            });
            Toast.makeText(this, "Event saved",
                    Toast.LENGTH_SHORT).show();
        } else {
            editingEvent.setTitle(title);
            editingEvent.setDescription(description);
            editingEvent.setLocation(location);
            editingEvent.setCategory(category);
            editingEvent.setStartTime(selectedStartTime);
            editingEvent.setEndTime(selectedEndTime);
            editingEvent.setAllDay(checkEventAllDay.isChecked());
            editingEvent.setReminderDate(reminderTime);
            editingEvent.setUpdatedAt(now);

            // Cancel any previously scheduled reminder.
            EventReminderScheduler.cancelReminder(
                    ScheduleActivity.this,
                    editingEvent.getId()
            );

// Save the updated event.
            eventViewModel.update(editingEvent);

// Schedule the updated reminder, if enabled.
            if (editingEvent.getReminderDate() > System.currentTimeMillis()) {
                EventReminderScheduler.scheduleReminder(
                        ScheduleActivity.this,
                        editingEvent.getId(),
                        editingEvent.getTitle(),
                        editingEvent.getReminderDate()
                );
            }

            Toast.makeText(this, "Event updated",
                    Toast.LENGTH_SHORT).show();
        }

        clearForm();
    }

    private void clearForm() {
        editingEvent = null;

        editEventTitle.setText("");
        editEventDescription.setText("");
        editEventLocation.setText("");

        spinnerEventCategory.setSelection(0);
        checkEventAllDay.setChecked(false);

        selectedStartTime = 0L;
        selectedEndTime = 0L;

        selectedReminderTime = 0L;
        checkEventReminder.setChecked(false);
        textEventReminder.setText("No reminder set");
        buttonEventReminder.setEnabled(false);

        textEventStart.setText("No start time selected");
        textEventEnd.setText("No end time selected");

        buttonSaveEvent.setText("Save event");

        Button cancelButton = findViewById(R.id.buttonCancelEventEdit);
        if (cancelButton != null) {
            cancelButton.setVisibility(View.GONE);
        }

        updateDateButtonLabels();
    }

    private void beginEditing(Event event) {
        editingEvent = event;

        editEventTitle.setText(event.getTitle());
        editEventDescription.setText(event.getDescription());
        editEventLocation.setText(event.getLocation());

        selectedStartTime = event.getStartTime();
        selectedEndTime = event.getEndTime();

        checkEventAllDay.setChecked(event.isAllDay());

        selectCategory(event.getCategory());

        textEventStart.setText(
                "Starts: " + formatDateTime(selectedStartTime)
        );
        textEventEnd.setText(
                "Ends: " + formatDateTime(selectedEndTime)
        );

        selectedReminderTime = event.getReminderDate();

        checkEventReminder.setChecked(selectedReminderTime > 0L);

        if (selectedReminderTime > 0L) {
            textEventReminder.setText(
                    "Reminder: " + formatDateTime(selectedReminderTime)
            );
        } else {
            textEventReminder.setText("No reminder set");
        }
        buttonSaveEvent.setText("Save changes");

        Button cancelButton = findViewById(R.id.buttonCancelEventEdit);
        if (cancelButton != null) {
            cancelButton.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.scheduleRoot).requestFocus();
    }

    private void selectCategory(String category) {
        if (category == null) {
            spinnerEventCategory.setSelection(0);
            return;
        }

        for (int i = 0; i < spinnerEventCategory.getCount(); i++) {
            if (category.equals(spinnerEventCategory.getItemAtPosition(i))) {
                spinnerEventCategory.setSelection(i);
                return;
            }
        }

        spinnerEventCategory.setSelection(0);
    }

    private void addEventCard(Event event) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackgroundResource(R.drawable.bg_lifeos_card);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        params.bottomMargin = dp(12);
        card.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText(event.getTitle());
        title.setTextColor(getColor(R.color.lifeos_text));
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView details = new TextView(this);
        details.setTextColor(getColor(R.color.lifeos_muted));
        details.setTextSize(14);

        StringBuilder text = new StringBuilder();
        text.append(event.getCategory());

        if (event.isAllDay()) {
            text.append("\nAll day");
        }

        if (event.getLocation() != null
                && !event.getLocation().trim().isEmpty()) {
            text.append("\n").append(event.getLocation());
        }

        text.append("\nStarts: ").append(formatDateTime(event.getStartTime()));
        text.append("\nEnds: ").append(formatDateTime(event.getEndTime()));

        if (event.getDescription() != null
                && !event.getDescription().trim().isEmpty()) {
            text.append("\n").append(event.getDescription());
        }

        details.setText(text.toString());

        Button editButton = new Button(this);
        editButton.setText("Edit event");
        editButton.setOnClickListener(v -> beginEditing(event));

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete event");

        deleteButton.setOnClickListener(v -> {
            // Cancel the event's scheduled reminder first.
            EventReminderScheduler.cancelReminder(
                    ScheduleActivity.this,
                    event.getId()
            );

            // Delete the event from the database.
            eventViewModel.delete(event);

            // Clear the form if this event was being edited.
            if (editingEvent != null
                    && editingEvent.getId() == event.getId()) {
                clearForm();
            }

            Toast.makeText(this, "Event deleted",
                    Toast.LENGTH_SHORT).show();
        });


        card.addView(title);
        card.addView(details);
        card.addView(editButton);
        card.addView(deleteButton);

        eventListContainer.addView(card);
    }
}
