package com.lifeos.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.app.AlertDialog;

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

    private TextView textEventCategory;
    private CheckBox checkEventAllDay;

    private TextView textEventStart;
    private TextView textEventEnd;
    private TextView textEventReminder;

    private TextView buttonEventStart;
    private TextView buttonEventEnd;
    private TextView buttonEventReminder;

    private Button buttonSaveEvent;
    private Button buttonCancelEventEdit;

    private LinearLayout eventListContainer;
    private TextView textNoEvents;

    private EventViewModel eventViewModel;
    private Dialog eventDialog;

    private long selectedStartTime = 0L;
    private long selectedEndTime = 0L;
    private long selectedReminderTime = 0L;

    private Event editingEvent = null;

    private String selectedCategory = "Personal";

    private final String[] categories = {
            "Personal", "College", "Work", "Appointment", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_schedule);

        applySystemBarInsets();

        eventListContainer = findViewById(R.id.eventListContainer);
        textNoEvents = findViewById(R.id.textNoEvents);

        findViewById(R.id.buttonScheduleBack)
                .setOnClickListener(v -> finish());

        findViewById(R.id.buttonAddEvent)
                .setOnClickListener(v -> openEventDialog(null));

        initializeViewModel();
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

    // ---------------------------------------------------------
    // EVENT EDITOR POPUP
    // ---------------------------------------------------------

    private void openEventDialog(Event event) {

        editingEvent = event;

        selectedStartTime = event == null ? 0L : event.getStartTime();
        selectedEndTime = event == null ? 0L : event.getEndTime();
        selectedReminderTime = event == null ? 0L : event.getReminderDate();

        selectedCategory = event == null
                ? "Personal"
                : event.getCategory();

        eventDialog = new Dialog(this);
        eventDialog.setContentView(R.layout.dialog_event_editor);
        eventDialog.setCancelable(true);

        Window window = eventDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            );
        }

        editEventTitle = eventDialog.findViewById(R.id.editEventTitle);
        editEventDescription =
                eventDialog.findViewById(R.id.editEventDescription);
        editEventLocation =
                eventDialog.findViewById(R.id.editEventLocation);

        textEventCategory =
                eventDialog.findViewById(R.id.textEventCategory);

        checkEventAllDay =
                eventDialog.findViewById(R.id.checkEventAllDay);

        textEventStart =
                eventDialog.findViewById(R.id.textEventStart);
        textEventEnd =
                eventDialog.findViewById(R.id.textEventEnd);
        textEventReminder =
                eventDialog.findViewById(R.id.textEventReminder);

        buttonEventStart =
                eventDialog.findViewById(R.id.buttonEventStart);
        buttonEventEnd =
                eventDialog.findViewById(R.id.buttonEventEnd);
        buttonEventReminder =
                eventDialog.findViewById(R.id.buttonEventReminder);

        buttonSaveEvent =
                eventDialog.findViewById(R.id.buttonSaveEvent);
        buttonCancelEventEdit =
                eventDialog.findViewById(R.id.buttonCancelEventEdit);

        TextView heading =
                eventDialog.findViewById(R.id.textEventDialogTitle);

        if (event != null) {
            heading.setText("Edit event");
            buttonSaveEvent.setText("Save changes");

            editEventTitle.setText(event.getTitle());
            editEventDescription.setText(event.getDescription());
            editEventLocation.setText(event.getLocation());

            checkEventAllDay.setChecked(event.isAllDay());
        } else {
            heading.setText("Create an event");
            buttonSaveEvent.setText("Save event");

            checkEventAllDay.setChecked(false);
        }

        textEventCategory.setText(selectedCategory + "     ▾");

        updateDateButtonLabels();
        updateDateLabels();
        updateReminderLabel();

        boolean reminderEnabled = selectedReminderTime > 0L;

        CheckBox reminderCheck =
                eventDialog.findViewById(R.id.checkEventReminder);

        reminderCheck.setChecked(reminderEnabled);
        buttonEventReminder.setEnabled(reminderEnabled);
        buttonEventReminder.setAlpha(reminderEnabled ? 1f : 0.45f);

        textEventCategory.setOnClickListener(v -> showCategoryPicker());

        buttonEventStart.setOnClickListener(v -> pickDateTime(true));
        buttonEventEnd.setOnClickListener(v -> pickDateTime(false));

        buttonEventReminder.setOnClickListener(v -> {
            if (reminderCheck.isChecked()) {
                pickReminderDateTime();
            }
        });

        reminderCheck.setOnCheckedChangeListener((button, checked) -> {
            buttonEventReminder.setEnabled(checked);
            buttonEventReminder.setAlpha(checked ? 1f : 0.45f);

            if (!checked) {
                selectedReminderTime = 0L;
                updateReminderLabel();
            }
        });

        checkEventAllDay.setOnCheckedChangeListener(
                (button, checked) -> {
                    updateDateButtonLabels();
                    updateDateLabels();
                }
        );

        buttonSaveEvent.setOnClickListener(v -> saveEvent());

        buttonCancelEventEdit.setOnClickListener(v -> {
            eventDialog.dismiss();
            editingEvent = null;
        });

        eventDialog.show();

        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(28),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            window.setGravity(Gravity.CENTER);
        }
    }

    // ---------------------------------------------------------
    // CATEGORY PICKER
    // ---------------------------------------------------------

    private void showCategoryPicker() {

        final Dialog categoryDialog = new Dialog(this);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(22), dp(22), dp(22), dp(18));
        panel.setBackgroundResource(R.drawable.bg_dialog_surface);

        TextView heading = new TextView(this);
        heading.setText("Choose category");
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(21);
        heading.setTypeface(null, Typeface.BOLD);

        panel.addView(heading);

        for (String category : categories) {

            TextView option = new TextView(this);
            option.setText(
                    (category.equals(selectedCategory) ? "✓   " : "     ")
                            + category
            );

            option.setTextColor(
                    category.equals(selectedCategory)
                            ? getColor(R.color.lifeos_primary)
                            : getColor(R.color.lifeos_text)
            );

            option.setTextSize(15);
            option.setPadding(dp(12), dp(15), dp(12), dp(15));

            option.setOnClickListener(v -> {
                selectedCategory = category;
                textEventCategory.setText(category + "     ▾");
                categoryDialog.dismiss();
            });

            panel.addView(option);
        }

        categoryDialog.setContentView(panel);
        categoryDialog.setCancelable(true);

        Window window = categoryDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        categoryDialog.show();

        if (window != null) {
            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(60),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    // ---------------------------------------------------------
    // DATE AND TIME PICKERS
    // ---------------------------------------------------------

    private void updateDateButtonLabels() {
        if (checkEventAllDay == null) return;

        boolean allDay = checkEventAllDay.isChecked();

        buttonEventStart.setText(
                allDay ? "Choose start date" : "Choose start date and time"
        );

        buttonEventEnd.setText(
                allDay ? "Choose end date" : "Choose end date and time"
        );
    }

    private void updateDateLabels() {

        textEventStart.setText(
                selectedStartTime == 0L
                        ? "No start time selected"
                        : "Starts: " + formatDateTime(selectedStartTime)
        );

        textEventEnd.setText(
                selectedEndTime == 0L
                        ? "No end time selected"
                        : "Ends: " + formatDateTime(selectedEndTime)
        );
    }

    private void pickDateTime(boolean isStart) {

        Calendar calendar = Calendar.getInstance();

        long currentValue = isStart
                ? selectedStartTime
                : selectedEndTime;

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

                        applySelectedTime(
                                isStart,
                                calendar.getTimeInMillis()
                        );

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

                                selectedReminderTime =
                                        calendar.getTimeInMillis();

                                updateReminderLabel();
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

    private void applySelectedTime(boolean isStart, long timestamp) {

        if (isStart) {
            selectedStartTime = timestamp;
        } else {
            selectedEndTime = timestamp;
        }

        updateDateLabels();
    }

    private void updateReminderLabel() {

        if (selectedReminderTime <= 0L) {
            textEventReminder.setText("No reminder set");
            return;
        }

        textEventReminder.setText(
                "Reminder: " + DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM,
                        DateFormat.SHORT,
                        Locale.getDefault()
                ).format(new Date(selectedReminderTime))
        );
    }

    private String formatDateTime(long timestamp) {

        if (checkEventAllDay != null
                && checkEventAllDay.isChecked()) {

            return DateFormat.getDateInstance(
                    DateFormat.MEDIUM,
                    Locale.getDefault()
            ).format(new Date(timestamp));
        }

        return DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault()
        ).format(new Date(timestamp));
    }

    // ---------------------------------------------------------
    // SAVE EVENT
    // ---------------------------------------------------------

    private void saveEvent() {

        String title = editEventTitle.getText().toString().trim();

        if (title.isEmpty()) {
            editEventTitle.setError("Enter an event title");
            editEventTitle.requestFocus();
            return;
        }

        if (selectedStartTime == 0L || selectedEndTime == 0L) {
            Toast.makeText(
                    this,
                    "Choose both a start and end date/time",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (selectedEndTime < selectedStartTime) {
            Toast.makeText(
                    this,
                    "End must be after start",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String description =
                editEventDescription.getText().toString().trim();

        String location =
                editEventLocation.getText().toString().trim();

        long now = System.currentTimeMillis();

        long reminderTime = 0L;

        CheckBox reminderCheck =
                eventDialog.findViewById(R.id.checkEventReminder);

        if (reminderCheck.isChecked()) {

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
                    selectedCategory,
                    now,
                    now
            );

            eventViewModel.insertEvent(event, savedEvent -> {

                if (savedEvent.getReminderDate() > 0L) {
                    EventReminderScheduler.scheduleReminder(
                            ScheduleActivity.this,
                            savedEvent.getId(),
                            savedEvent.getTitle(),
                            savedEvent.getReminderDate()
                    );
                }
            });

            Toast.makeText(
                    this,
                    "Event saved",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            editingEvent.setTitle(title);
            editingEvent.setDescription(description);
            editingEvent.setLocation(location);
            editingEvent.setCategory(selectedCategory);
            editingEvent.setStartTime(selectedStartTime);
            editingEvent.setEndTime(selectedEndTime);
            editingEvent.setAllDay(checkEventAllDay.isChecked());
            editingEvent.setReminderDate(reminderTime);
            editingEvent.setUpdatedAt(now);

            EventReminderScheduler.cancelReminder(
                    this,
                    editingEvent.getId()
            );

            eventViewModel.update(editingEvent);

            if (reminderTime > System.currentTimeMillis()) {
                EventReminderScheduler.scheduleReminder(
                        this,
                        editingEvent.getId(),
                        editingEvent.getTitle(),
                        reminderTime
                );
            }

            Toast.makeText(
                    this,
                    "Event updated",
                    Toast.LENGTH_SHORT
            ).show();
        }

        eventDialog.dismiss();
        editingEvent = null;
    }

    // ---------------------------------------------------------
    // EVENT CARDS
    // ---------------------------------------------------------

    private void addEventCard(Event event) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(17), dp(17), dp(17), dp(17));
        card.setBackgroundResource(R.drawable.bg_lifeos_card);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(13);
        card.setLayoutParams(cardParams);

        TextView category = new TextView(this);
        category.setText(
                event.getCategory() == null
                        ? "EVENT"
                        : event.getCategory().toUpperCase(Locale.ROOT)
        );
        category.setTextColor(getColor(R.color.lifeos_primary));
        category.setTextSize(10);
        category.setTypeface(null, Typeface.BOLD);
        category.setLetterSpacing(0.12f);

        TextView title = new TextView(this);
        title.setText(event.getTitle());
        title.setTextColor(getColor(R.color.lifeos_text));
        title.setTextSize(19);
        title.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        titleParams.topMargin = dp(5);

        TextView details = new TextView(this);
        details.setTextColor(getColor(R.color.lifeos_muted));
        details.setTextSize(13);

        StringBuilder text = new StringBuilder();

        if (event.isAllDay()) {
            text.append("All-day event\n");
        }

        if (event.getLocation() != null
                && !event.getLocation().trim().isEmpty()) {
            text.append("Location: ")
                    .append(event.getLocation())
                    .append("\n");
        }

        text.append("Starts: ")
                .append(formatDateTimeForCard(
                        event.getStartTime(), event.isAllDay()
                ));

        text.append("\nEnds: ")
                .append(formatDateTimeForCard(
                        event.getEndTime(), event.isAllDay()
                ));

        if (event.getDescription() != null
                && !event.getDescription().trim().isEmpty()) {
            text.append("\n\n").append(event.getDescription());
        }

        if (event.getReminderDate() > 0L) {
            text.append("\n\nReminder set");
        }

        details.setText(text.toString());

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);

        TextView editButton = createCardAction("Edit", true);
        TextView deleteButton = createCardAction("Delete", false);

        editButton.setOnClickListener(v -> openEventDialog(event));
        deleteButton.setOnClickListener(v -> confirmDelete(event));

        actions.addView(editButton);
        actions.addView(deleteButton);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        detailsParams.topMargin = dp(10);

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        actionsParams.topMargin = dp(12);

        card.addView(category);
        card.addView(title, titleParams);
        card.addView(details, detailsParams);
        card.addView(actions, actionsParams);

        eventListContainer.addView(card);
    }

    private String formatDateTimeForCard(long timestamp, boolean allDay) {

        DateFormat format = allDay
                ? DateFormat.getDateInstance(
                DateFormat.MEDIUM, Locale.getDefault())
                : DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                Locale.getDefault());

        return format.format(new Date(timestamp));
    }

    private TextView createCardAction(String label, boolean primary) {

        TextView action = new TextView(this);

        action.setText(label);
        action.setTextSize(13);
        action.setTypeface(null, Typeface.BOLD);
        action.setGravity(Gravity.CENTER);
        action.setPadding(dp(15), dp(10), dp(15), dp(10));

        action.setTextColor(
                getColor(primary
                        ? R.color.lifeos_primary
                        : R.color.lifeos_muted)
        );

        return action;
    }

    // ---------------------------------------------------------
    // DELETE CONFIRMATION
    // ---------------------------------------------------------

    private void confirmDelete(Event event) {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(22), dp(22), dp(22), dp(18));
        panel.setBackgroundResource(R.drawable.bg_dialog_surface);

        TextView eyebrow = new TextView(this);
        eyebrow.setText("LIFEOS / CONFIRMATION");
        eyebrow.setTextColor(getColor(R.color.lifeos_primary));
        eyebrow.setTextSize(10);
        eyebrow.setTypeface(null, Typeface.BOLD);
        eyebrow.setLetterSpacing(0.13f);

        TextView heading = new TextView(this);
        heading.setText("Delete this event?");
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(22);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        headingParams.topMargin = dp(8);

        TextView message = new TextView(this);
        message.setText(
                "“" + event.getTitle()
                        + "” will be permanently removed from your schedule."
        );
        message.setTextColor(getColor(R.color.lifeos_muted));
        message.setTextSize(14);

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        messageParams.topMargin = dp(8);

        Button delete = new Button(this);
        delete.setText("Delete event");
        delete.setAllCaps(false);
        delete.setTextColor(0xFFFFFFFF);
        delete.setBackgroundResource(R.drawable.bg_profile_photo_button);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                );
        deleteParams.topMargin = dp(20);

        Button cancel = new Button(this);
        cancel.setText("Keep event");
        cancel.setAllCaps(false);
        cancel.setTextColor(getColor(R.color.lifeos_muted));

        panel.addView(eyebrow);
        panel.addView(heading, headingParams);
        panel.addView(message, messageParams);
        panel.addView(delete, deleteParams);
        panel.addView(cancel);

        dialog.setContentView(panel);
        dialog.setCancelable(true);

        delete.setOnClickListener(v -> {

            EventReminderScheduler.cancelReminder(
                    ScheduleActivity.this,
                    event.getId()
            );

            eventViewModel.delete(event);

            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Event deleted",
                    Toast.LENGTH_SHORT
            ).show();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(60),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }
}