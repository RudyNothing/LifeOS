
package com.lifeos.app;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.JournalEntry;
import com.lifeos.app.viewmodel.JournalEntryViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    private JournalEntryViewModel journalViewModel;

    private LinearLayout layoutEntries;
    private TextView textEmpty;

    private final String[] moods = {
            "Happy", "Excited", "Grateful", "Calm",
            "Neutral", "Sad", "Stressed", "Angry"
    };

    private EditText editEntryTitle;
    private EditText editEntryContent;
    private TextView textSelectedDate;
    private TextView textSelectedMood;
    private CheckBox checkMemory;

    private Calendar selectedDate;

    private String selectedMood = "Neutral";

    private Dialog editorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_journal);

        applyWindowInsets();

        layoutEntries = findViewById(R.id.layoutJournalEntries);
        textEmpty = findViewById(R.id.textJournalEmpty);

        Button back = findViewById(R.id.buttonJournalBack);
        Button add = findViewById(R.id.buttonAddJournal);

        back.setOnClickListener(v -> finish());

        add.setBackgroundResource(R.drawable.bg_journal_primary);
        add.setTextColor(getColor(android.R.color.white));
        add.setAllCaps(false);

        add.setOnClickListener(v -> showEntryDialog(null));

        journalViewModel = new ViewModelProvider(this)
                .get(JournalEntryViewModel.class);

        journalViewModel.getAllEntries().observe(
                this,
                this::displayEntries
        );
    }

    private void applyWindowInsets() {

        View root = findViewById(R.id.journalRoot);

        final int left = root.getPaddingLeft();
        final int top = root.getPaddingTop();
        final int right = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {

            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    left + bars.left,
                    top + bars.top,
                    right + bars.right,
                    bottom + bars.bottom
            );

            return insets;
        });

        ViewCompat.requestApplyInsets(root);
    }

    // ---------------------------------------------------------
    // JOURNAL ENTRY LIST
    // ---------------------------------------------------------

    private void displayEntries(List<JournalEntry> entries) {

        layoutEntries.removeAllViews();

        if (entries == null || entries.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textEmpty.setVisibility(View.GONE);

        // Memories first.
        for (int i = entries.size() - 1; i >= 0; i--) {

            JournalEntry entry = entries.get(i);

            if (entry != null && entry.isMemory()) {
                addEntryCard(entry);
            }
        }

        // Ordinary journal entries next.
        for (int i = entries.size() - 1; i >= 0; i--) {

            JournalEntry entry = entries.get(i);

            if (entry != null && !entry.isMemory()) {
                addEntryCard(entry);
            }
        }
    }

    private void addEntryCard(JournalEntry entry) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackgroundResource(R.drawable.bg_lifeos_card);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.bottomMargin = dp(11);
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);

        title.setText(
                (entry.isMemory() ? "★  " : "")
                        + safeText(entry.getTitle())
        );

        title.setTextColor(getColor(R.color.lifeos_text));
        title.setTextSize(17);
        title.setTypeface(null, Typeface.BOLD);

        TextView metadata = new TextView(this);

        String date = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(new Date(entry.getDate()));

        String mood = entry.getMood() == null
                ? "No mood"
                : entry.getMood();

        metadata.setText(
                date + "  •  " + mood
                        + (entry.isMemory() ? "  •  Memory" : "  •  Journal")
        );

        metadata.setTextColor(getColor(R.color.journal_primary));
        metadata.setTextSize(12);
        metadata.setTypeface(null, Typeface.BOLD);
        metadata.setPadding(0, dp(6), 0, dp(9));

        TextView content = new TextView(this);
        content.setText(safeText(entry.getContent()));
        content.setTextColor(getColor(R.color.lifeos_text));
        content.setTextSize(14);
        content.setMaxLines(5);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button edit = actionButton("Edit", false);
        Button delete = actionButton("Delete", true);

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(0, dp(44), 1);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(44), 1);

        deleteParams.leftMargin = dp(8);

        actions.addView(edit, editParams);
        actions.addView(delete, deleteParams);

        edit.setOnClickListener(v -> showEntryDialog(entry));
        delete.setOnClickListener(v -> confirmDelete(entry));

        card.addView(title);
        card.addView(metadata);

        if (!safeText(entry.getContent()).trim().isEmpty()) {
            card.addView(content);
        }

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(-1, -2);

        actionsParams.topMargin = dp(14);

        card.addView(actions, actionsParams);

        layoutEntries.addView(card);
    }

    // ---------------------------------------------------------
    // CUSTOM JOURNAL EDITOR
    // ---------------------------------------------------------

    private void showEntryDialog(JournalEntry existingEntry) {

        boolean editing = existingEntry != null;

        selectedDate = Calendar.getInstance();

        selectedMood = "Neutral";

        if (editing) {

            selectedDate.setTimeInMillis(existingEntry.getDate());

            selectedMood = existingEntry.getMood() == null
                    ? "Neutral"
                    : existingEntry.getMood();

            if (!containsMood(selectedMood)) {
                selectedMood = "Neutral";
            }
        }

        editorDialog = new Dialog(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundResource(R.drawable.bg_dialog_surface);

        LinearLayout panel = dialogPanel();

        addEyebrow(
                panel,
                editing ? "REFLECT AND REVISIT" : "CAPTURE YOUR MOMENT"
        );

        TextView heading = new TextView(this);
        heading.setText(editing ? "Edit Entry" : "New Journal Entry");
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(23);
        heading.setTypeface(null, Typeface.BOLD);

        addWithTopMargin(panel, heading, 5);

        addFieldLabel(panel, "Entry title");

        editEntryTitle = new EditText(this);
        editEntryTitle.setHint("Give this moment a title");
        editEntryTitle.setSingleLine(true);
        editEntryTitle.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        styleInput(editEntryTitle);
        panel.addView(editEntryTitle);

        addFieldLabel(panel, "Your thoughts");

        editEntryContent = new EditText(this);
        editEntryContent.setHint("Write about your day...");
        editEntryContent.setMinLines(5);
        editEntryContent.setGravity(Gravity.TOP | Gravity.START);
        editEntryContent.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        styleInput(editEntryContent);
        panel.addView(editEntryContent);

        addFieldLabel(panel, "Entry date");

        TextView dateSelector = selectorControl();
        textSelectedDate = dateSelector;

        panel.addView(dateSelector);

        addFieldLabel(panel, "How are you feeling?");

        TextView moodSelector = selectorControl();
        textSelectedMood = moodSelector;

        panel.addView(moodSelector);

        CheckBox memory = new CheckBox(this);
        memory.setText("Save as an important memory");
        memory.setTextColor(getColor(R.color.lifeos_text));
        memory.setTextSize(14);
        memory.setButtonTintList(
                ColorStateList.valueOf(
                        getColor(R.color.journal_primary)
                )
        );

        checkMemory = memory;

        LinearLayout.LayoutParams memoryParams =
                new LinearLayout.LayoutParams(-1, -2);

        memoryParams.topMargin = dp(13);

        panel.addView(memory, memoryParams);

        Button save = new Button(this);
        save.setText(editing ? "Update Entry" : "Save Entry");
        save.setAllCaps(false);
        save.setTextColor(getColor(android.R.color.white));
        save.setBackgroundResource(R.drawable.bg_journal_primary);

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(-1, dp(49));

        saveParams.topMargin = dp(17);

        panel.addView(save, saveParams);

        Button cancel = new Button(this);
        cancel.setText("Cancel");
        cancel.setAllCaps(false);
        cancel.setTextColor(getColor(R.color.journal_button_text));
        cancel.setBackgroundResource(R.drawable.bg_journal_secondary);

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(-1, dp(47));

        cancelParams.topMargin = dp(8);

        panel.addView(cancel, cancelParams);

        scroll.addView(panel);

        editorDialog.setContentView(scroll);
        editorDialog.setCancelable(true);

        if (editing) {
            editEntryTitle.setText(existingEntry.getTitle());
            editEntryContent.setText(existingEntry.getContent());
            checkMemory.setChecked(existingEntry.isMemory());
        }

        updateDateLabel();
        updateMoodLabel();

        textSelectedDate.setOnClickListener(v ->
                showCustomDatePicker()
        );

        textSelectedMood.setOnClickListener(v ->
                showMoodDialog()
        );

        save.setOnClickListener(v ->
                saveEntry(existingEntry, editing)
        );

        cancel.setOnClickListener(v ->
                editorDialog.dismiss()
        );

        editorDialog.show();

        configureDialogWindow(editorDialog, 0.92f);
    }

    private void saveEntry(
            JournalEntry existingEntry,
            boolean editing
    ) {

        String entryTitle = editEntryTitle.getText()
                .toString().trim();

        String entryContent = editEntryContent.getText()
                .toString().trim();

        if (entryTitle.isEmpty()) {
            editEntryTitle.setError("Enter a title");
            editEntryTitle.requestFocus();
            return;
        }

        if (entryContent.isEmpty()) {
            editEntryContent.setError("Write something first");
            editEntryContent.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();

        if (editing && existingEntry != null) {

            existingEntry.setTitle(entryTitle);
            existingEntry.setContent(entryContent);
            existingEntry.setDate(selectedDate.getTimeInMillis());
            existingEntry.setMood(selectedMood);
            existingEntry.setMemory(checkMemory.isChecked());
            existingEntry.setUpdatedAt(now);

            journalViewModel.update(existingEntry);

            Toast.makeText(
                    this,
                    "Entry updated",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            JournalEntry entry = new JournalEntry(
                    entryTitle,
                    entryContent,
                    selectedDate.getTimeInMillis(),
                    selectedMood,
                    checkMemory.isChecked(),
                    now,
                    now
            );

            journalViewModel.insert(entry);

            Toast.makeText(
                    this,
                    "Entry saved",
                    Toast.LENGTH_SHORT
            ).show();
        }

        editorDialog.dismiss();
    }

    // ---------------------------------------------------------
    // CUSTOM DATE PICKER
    // ---------------------------------------------------------

    private void showCustomDatePicker() {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "CHOOSE YOUR MOMENT");
        addDialogHeading(panel, "Entry date");

        TextView description = new TextView(this);
        description.setText("Select the date you want to remember.");
        description.setTextColor(getColor(R.color.lifeos_muted));
        description.setTextSize(13);

        addWithTopMargin(panel, description, 4);

        DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(true);
        picker.setSpinnersShown(false);
        picker.setMaxDate(System.currentTimeMillis());

        picker.updateDate(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        LinearLayout.LayoutParams pickerParams =
                new LinearLayout.LayoutParams(-1, -2);

        pickerParams.topMargin = dp(10);

        panel.addView(picker, pickerParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button cancel = actionButton("Cancel", false);
        Button confirm = actionButton("Use date", false, true);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        actions.addView(cancel, buttonParams);

        LinearLayout.LayoutParams confirmParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        confirmParams.leftMargin = dp(8);

        actions.addView(confirm, confirmParams);

        addWithTopMargin(panel, actions, 15);

        cancel.setOnClickListener(v -> dialog.dismiss());

        confirm.setOnClickListener(v -> {

            selectedDate.set(
                    picker.getYear(),
                    picker.getMonth(),
                    picker.getDayOfMonth(),
                    12,
                    0,
                    0
            );

            selectedDate.set(Calendar.MILLISECOND, 0);

            updateDateLabel();

            dialog.dismiss();
        });

        showCustomDialog(dialog, panel, 0.94f);
    }

    private void updateDateLabel() {

        String date = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(selectedDate.getTime());

        textSelectedDate.setText("▦    " + date + "    ⌄");
    }

    // ---------------------------------------------------------
    // CUSTOM MOOD SELECTOR
    // ---------------------------------------------------------

    private void showMoodDialog() {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "CHECK IN WITH YOURSELF");
        addDialogHeading(panel, "How are you feeling?");

        String[] symbols = {
                "☺", "✦", "♥", "☼",
                "○", "☹", "◌", "!"
        };

        for (int i = 0; i < moods.length; i++) {

            String mood = moods[i];

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(12), dp(14), dp(12));
            row.setBackgroundResource(R.drawable.bg_selector_control);

            TextView symbol = new TextView(this);
            symbol.setText(symbols[i]);
            symbol.setTextSize(19);
            symbol.setTextColor(getColor(R.color.journal_primary));

            TextView label = new TextView(this);
            label.setText(mood);
            label.setTextSize(15);
            label.setTypeface(null, Typeface.BOLD);
            label.setTextColor(getColor(
                    mood.equals(selectedMood)
                            ? R.color.journal_primary
                            : R.color.lifeos_text
            ));

            LinearLayout.LayoutParams labelParams =
                    new LinearLayout.LayoutParams(0, -2, 1);

            labelParams.leftMargin = dp(12);

            TextView check = new TextView(this);
            check.setText(mood.equals(selectedMood) ? "✓" : "");
            check.setTextSize(18);
            check.setTypeface(null, Typeface.BOLD);
            check.setTextColor(getColor(R.color.journal_primary));

            row.addView(symbol);
            row.addView(label, labelParams);
            row.addView(check);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(-1, -2);

            rowParams.topMargin = dp(7);

            panel.addView(row, rowParams);

            row.setOnClickListener(v -> {

                selectedMood = mood;
                updateMoodLabel();

                dialog.dismiss();
            });
        }

        Button cancel = actionButton("Cancel", false);

        addWithTopMargin(panel, cancel, 13);

        cancel.setOnClickListener(v -> dialog.dismiss());

        showCustomDialog(dialog, panel, 0.90f);
    }

    private void updateMoodLabel() {

        textSelectedMood.setText(
                moodSymbol(selectedMood)
                        + "    " + selectedMood + "    ⌄"
        );
    }

    private String moodSymbol(String mood) {

        switch (mood) {
            case "Happy":
                return "☺";
            case "Excited":
                return "✦";
            case "Grateful":
                return "♥";
            case "Calm":
                return "☼";
            case "Sad":
                return "☹";
            case "Stressed":
                return "◌";
            case "Angry":
                return "!";
            default:
                return "○";
        }
    }

    private boolean containsMood(String mood) {

        for (String option : moods) {
            if (option.equals(mood)) {
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------
    // CUSTOM DELETE CONFIRMATION
    // ---------------------------------------------------------

    private void confirmDelete(JournalEntry entry) {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "JOURNAL & MEMORIES");
        addDialogHeading(panel, "Delete this entry?");

        TextView message = new TextView(this);

        message.setText(
                "“" + safeText(entry.getTitle())
                        + "” will be permanently removed."
        );

        message.setTextColor(getColor(R.color.lifeos_muted));
        message.setTextSize(14);

        addWithTopMargin(panel, message, 9);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button cancel = actionButton("Keep Entry", false);
        Button delete = actionButton("Delete", true);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        actions.addView(cancel, buttonParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        deleteParams.leftMargin = dp(8);

        actions.addView(delete, deleteParams);

        addWithTopMargin(panel, actions, 18);

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {

            journalViewModel.delete(entry);

            Toast.makeText(
                    this,
                    "Entry deleted",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
        });

        showCustomDialog(dialog, panel, 0.90f);
    }

    // ---------------------------------------------------------
    // CUSTOM UI HELPERS
    // ---------------------------------------------------------

    private LinearLayout dialogPanel() {

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(21), dp(21), dp(21), dp(20));
        panel.setBackgroundResource(R.drawable.bg_dialog_surface);

        return panel;
    }

    private void addEyebrow(LinearLayout panel, String text) {

        TextView eyebrow = new TextView(this);
        eyebrow.setText(text);
        eyebrow.setTextColor(getColor(R.color.journal_primary));
        eyebrow.setTextSize(10);
        eyebrow.setLetterSpacing(0.14f);
        eyebrow.setTypeface(null, Typeface.BOLD);

        panel.addView(eyebrow);
    }

    private void addDialogHeading(LinearLayout panel, String text) {

        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(22);
        heading.setTypeface(null, Typeface.BOLD);

        addWithTopMargin(panel, heading, 5);
    }

    private void addFieldLabel(LinearLayout panel, String text) {

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(getColor(R.color.lifeos_text));
        label.setTextSize(13);
        label.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.topMargin = dp(15);
        params.bottomMargin = dp(6);

        panel.addView(label, params);
    }

    private TextView selectorControl() {

        TextView selector = new TextView(this);

        selector.setBackgroundResource(R.drawable.bg_lifeos_input);
        selector.setTextColor(getColor(R.color.lifeos_text));
        selector.setTextSize(14);
        selector.setGravity(Gravity.CENTER_VERTICAL);
        selector.setPadding(dp(13), dp(13), dp(13), dp(13));
        selector.setMinHeight(dp(49));

        return selector;
    }

    private void styleInput(EditText input) {

        input.setBackgroundResource(R.drawable.bg_lifeos_input);
        input.setTextColor(getColor(R.color.lifeos_text));
        input.setHintTextColor(getColor(R.color.lifeos_muted));
        input.setTextSize(14);
        input.setPadding(dp(13), dp(12), dp(13), dp(12));
    }

    private Button actionButton(String text, boolean danger) {
        return actionButton(text, danger, false);
    }

    private Button actionButton(
            String text,
            boolean danger,
            boolean primary
    ) {

        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(12);
        button.setTypeface(null, Typeface.BOLD);

        if (danger) {

            button.setTextColor(getColor(R.color.finance_danger));
            button.setBackgroundResource(R.drawable.bg_finance_danger);

        } else if (primary) {

            button.setTextColor(getColor(android.R.color.white));
            button.setBackgroundResource(R.drawable.bg_journal_primary);

        } else {

            button.setTextColor(getColor(R.color.journal_button_text));
            button.setBackgroundResource(R.drawable.bg_journal_secondary);
        }

        return button;
    }

    private void showCustomDialog(
            Dialog dialog,
            View content,
            float widthFraction
    ) {

        dialog.setContentView(content);
        dialog.setCancelable(true);
        dialog.show();

        configureDialogWindow(dialog, widthFraction);
    }

    private void configureDialogWindow(
            Dialog dialog,
            float widthFraction
    ) {

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT)
        );

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = window.getAttributes();

        params.width = (int) (
                getResources().getDisplayMetrics().widthPixels
                        * widthFraction
        );

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.dimAmount = 0.62f;

        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }

    private void addWithTopMargin(
            LinearLayout parent,
            View child,
            int margin
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.topMargin = dp(margin);

        parent.addView(child, params);
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }
}
