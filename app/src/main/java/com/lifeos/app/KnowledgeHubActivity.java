
package com.lifeos.app;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.Note;
import com.lifeos.app.viewmodel.NoteViewModel;

import java.util.List;

public class KnowledgeHubActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    private LinearLayout layoutNotes;
    private TextView textNotesEmpty;

    private final String[] categories = {
            "General", "Study", "Work",
            "Ideas", "Projects", "Personal"
    };

    private EditText editNoteTitle;
    private EditText editNoteContent;
    private TextView textSelectedCategory;
    private CheckBox checkPinned;

    private String selectedCategory = "General";

    private Dialog editorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_knowledge_hub);

        applyWindowInsets();

        layoutNotes = findViewById(R.id.layoutNotes);
        textNotesEmpty = findViewById(R.id.textNotesEmpty);

        Button back = findViewById(R.id.buttonKnowledgeBack);
        Button add = findViewById(R.id.buttonAddNote);

        back.setOnClickListener(v -> finish());

        add.setBackgroundResource(R.drawable.bg_knowledge_primary);
        add.setTextColor(getColor(android.R.color.white));
        add.setAllCaps(false);

        add.setOnClickListener(v -> showNoteDialog(null));

        noteViewModel = new ViewModelProvider(this)
                .get(NoteViewModel.class);

        noteViewModel.getAllNotes().observe(this, this::displayNotes);
    }

    private void applyWindowInsets() {

        View root = findViewById(R.id.knowledgeRoot);

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
    // NOTES LIST
    // ---------------------------------------------------------

    private void displayNotes(List<Note> notes) {

        layoutNotes.removeAllViews();

        if (notes == null || notes.isEmpty()) {
            textNotesEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textNotesEmpty.setVisibility(View.GONE);

        // Pinned notes appear first.
        for (int i = 0; i < notes.size(); i++) {

            Note note = notes.get(i);

            if (note != null && note.isPinned()) {
                addNoteCard(note);
            }
        }

        // Unpinned notes appear below.
        for (int i = notes.size() - 1; i >= 0; i--) {

            Note note = notes.get(i);

            if (note != null && !note.isPinned()) {
                addNoteCard(note);
            }
        }
    }

    private void addNoteCard(Note note) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackgroundResource(R.drawable.bg_lifeos_card);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.bottomMargin = dp(11);
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);

        String titleText = (note.isPinned() ? "★  " : "")
                + safeText(note.getTitle());

        title.setText(titleText);
        title.setTextColor(themedTextColor());
        title.setTextSize(17);
        title.setTypeface(null, Typeface.BOLD);

        TextView category = new TextView(this);
        category.setText(safeText(note.getCategory()));
        category.setTextColor(isDarkMode()
                ? 0xFFFFC66D
                : getColor(R.color.knowledge_primary));
        category.setTextSize(12);
        category.setTypeface(null, Typeface.BOLD);
        category.setPadding(0, dp(6), 0, dp(9));

        TextView content = new TextView(this);
        content.setText(safeText(note.getContent()));
        content.setTextColor(themedTextColor());
        content.setTextSize(14);
        content.setMaxLines(4);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);

        Button edit = actionButton("Edit", false);
        Button pin = actionButton(
                note.isPinned() ? "Unpin" : "Pin",
                false
        );
        Button delete = actionButton("Delete", true);

        LinearLayout.LayoutParams actionParams =
                new LinearLayout.LayoutParams(0, dp(44), 1);

        LinearLayout.LayoutParams secondParams =
                new LinearLayout.LayoutParams(0, dp(44), 1);

        secondParams.leftMargin = dp(6);

        LinearLayout.LayoutParams thirdParams =
                new LinearLayout.LayoutParams(0, dp(44), 1);

        thirdParams.leftMargin = dp(6);

        actions.addView(edit, actionParams);
        actions.addView(pin, secondParams);
        actions.addView(delete, thirdParams);

        edit.setOnClickListener(v -> showNoteDialog(note));

        pin.setOnClickListener(v -> {

            note.setPinned(!note.isPinned());
            note.setUpdatedAt(System.currentTimeMillis());

            noteViewModel.update(note);

            Toast.makeText(
                    this,
                    note.isPinned() ? "Note pinned" : "Note unpinned",
                    Toast.LENGTH_SHORT
            ).show();
        });

        delete.setOnClickListener(v -> confirmDelete(note));

        card.addView(title);
        card.addView(category);

        if (!safeText(note.getContent()).trim().isEmpty()) {
            card.addView(content);
        }

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(-1, -2);

        actionsParams.topMargin = dp(14);

        card.addView(actions, actionsParams);

        layoutNotes.addView(card);
    }

    // ---------------------------------------------------------
    // CUSTOM NOTE EDITOR
    // ---------------------------------------------------------

    private void showNoteDialog(Note existingNote) {

        boolean editing = existingNote != null;

        selectedCategory = editing
                ? safeText(existingNote.getCategory())
                : "General";

        if (!containsCategory(selectedCategory)) {
            selectedCategory = "General";
        }

        editorDialog = new Dialog(this);
        editorDialog.setContentView(createNoteEditor(editing));
        editorDialog.setCancelable(true);

        Window window = editorDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT)
            );

            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            );
        }

        TextView heading = editorDialog.findViewById(
                R.id.textKnowledgeDialogTitle
        );

        TextView eyebrow = editorDialog.findViewById(
                R.id.textKnowledgeDialogEyebrow
        );

        editNoteTitle = editorDialog.findViewById(
                R.id.editKnowledgeNoteTitle
        );

        editNoteContent = editorDialog.findViewById(
                R.id.editKnowledgeNoteContent
        );

        textSelectedCategory = editorDialog.findViewById(
                R.id.textKnowledgeCategory
        );

        checkPinned = editorDialog.findViewById(
                R.id.checkKnowledgePinned
        );

        Button save = editorDialog.findViewById(
                R.id.buttonKnowledgeSave
        );

        Button cancel = editorDialog.findViewById(
                R.id.buttonKnowledgeCancel
        );

        heading.setText(editing ? "Edit Note" : "Create Note");

        eyebrow.setText(
                editing ? "REFINE YOUR KNOWLEDGE" : "CAPTURE AN IDEA"
        );

        save.setText(editing ? "Update Note" : "Save Note");

        save.setBackgroundResource(R.drawable.bg_knowledge_primary);
        save.setTextColor(getColor(android.R.color.white));
        save.setAllCaps(false);

        cancel.setBackgroundResource(R.drawable.bg_knowledge_secondary);
        cancel.setTextColor(getColor(R.color.knowledge_button_text));
        cancel.setAllCaps(false);
        if (editing) {

            editNoteTitle.setText(existingNote.getTitle());
            editNoteContent.setText(existingNote.getContent());
            checkPinned.setChecked(existingNote.isPinned());

        } else {

            checkPinned.setChecked(false);
        }

        checkPinned.setButtonTintList(
                ColorStateList.valueOf(
                        getColor(R.color.knowledge_primary)
                )
        );

        updateCategoryLabel();

        textSelectedCategory.setOnClickListener(v ->
                showCategoryDialog()
        );

        save.setOnClickListener(v ->
                saveNote(existingNote, editing)
        );

        cancel.setOnClickListener(v ->
                editorDialog.dismiss()
        );

        editorDialog.show();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT)
            );

            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(32),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            window.setGravity(Gravity.CENTER);
        }
    }

    private View createNoteEditor(boolean editing) {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundResource(R.drawable.bg_dialog_surface);

        LinearLayout panel = dialogPanel();

        TextView eyebrow = new TextView(this);
        eyebrow.setId(R.id.textKnowledgeDialogEyebrow);
        eyebrow.setText(editing
                ? "REFINE YOUR KNOWLEDGE"
                : "CAPTURE AN IDEA");
        styleEyebrow(eyebrow);

        TextView heading = new TextView(this);
        heading.setId(R.id.textKnowledgeDialogTitle);
        heading.setText(editing ? "Edit Note" : "Create Note");
        heading.setTextColor(themedTextColor());
        heading.setTextSize(23);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(-1, -2);

        headingParams.topMargin = dp(5);

        panel.addView(eyebrow);
        panel.addView(heading, headingParams);

        addFieldLabel(panel, "Note title");

        EditText title = new EditText(this);
        title.setId(R.id.editKnowledgeNoteTitle);
        title.setSingleLine(true);
        title.setHint("Give your note a title");
        title.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(title);

        panel.addView(title);

        addFieldLabel(panel, "Category");

        TextView category = new TextView(this);
        category.setId(R.id.textKnowledgeCategory);
        category.setBackgroundResource(R.drawable.bg_lifeos_input);
        category.setTextColor(themedTextColor());
        category.setTextSize(14);
        category.setGravity(Gravity.CENTER_VERTICAL);
        category.setPadding(dp(13), dp(13), dp(13), dp(13));
        category.setMinHeight(dp(49));

        panel.addView(category);

        addFieldLabel(panel, "Your note");

        EditText content = new EditText(this);
        content.setId(R.id.editKnowledgeNoteContent);
        content.setHint("Write your thoughts, ideas or notes...");
        content.setMinLines(5);
        content.setGravity(Gravity.TOP | Gravity.START);
        content.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(content);

        panel.addView(content);

        CheckBox pinned = new CheckBox(this);
        pinned.setId(R.id.checkKnowledgePinned);
        pinned.setText("Pin this note");
        pinned.setTextColor(themedTextColor());
        pinned.setTextSize(14);
        pinned.setButtonTintList(
                ColorStateList.valueOf(
                        getColor(R.color.knowledge_primary)
                )
        );

        LinearLayout.LayoutParams pinnedParams =
                new LinearLayout.LayoutParams(-1, -2);

        pinnedParams.topMargin = dp(12);

        panel.addView(pinned, pinnedParams);

        Button save = new Button(this);
        save.setId(R.id.buttonKnowledgeSave);
        save.setText(editing ? "Update Note" : "Save Note");
        save.setAllCaps(false);
        save.setTextColor(getColor(android.R.color.white));
        save.setBackgroundResource(R.drawable.bg_knowledge_primary);

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(-1, dp(49));

        saveParams.topMargin = dp(17);

        panel.addView(save, saveParams);

        Button cancel = new Button(this);
        cancel.setId(R.id.buttonKnowledgeCancel);
        cancel.setText("Cancel");
        cancel.setAllCaps(false);
        cancel.setTextColor(getColor(R.color.knowledge_button_text));
        cancel.setBackgroundResource(R.drawable.bg_knowledge_secondary);

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(-1, dp(47));

        cancelParams.topMargin = dp(7);

        panel.addView(cancel, cancelParams);

        scroll.addView(panel);

        return scroll;
    }

    private void saveNote(Note existingNote, boolean editing) {

        String noteTitle = editNoteTitle.getText().toString().trim();
        String noteContent = editNoteContent.getText().toString().trim();

        if (noteTitle.isEmpty()) {
            editNoteTitle.setError("Enter a title");
            editNoteTitle.requestFocus();
            return;
        }

        if (noteContent.isEmpty()) {
            editNoteContent.setError("Enter note content");
            editNoteContent.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();

        if (editing && existingNote != null) {

            existingNote.setTitle(noteTitle);
            existingNote.setContent(noteContent);
            existingNote.setCategory(selectedCategory);
            existingNote.setPinned(checkPinned.isChecked());
            existingNote.setUpdatedAt(now);

            noteViewModel.update(existingNote);

            Toast.makeText(
                    this,
                    "Note updated",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Note note = new Note(
                    noteTitle,
                    noteContent,
                    selectedCategory,
                    checkPinned.isChecked(),
                    now,
                    now
            );

            noteViewModel.insert(note);

            Toast.makeText(
                    this,
                    "Note saved",
                    Toast.LENGTH_SHORT
            ).show();
        }

        editorDialog.dismiss();
    }

    // ---------------------------------------------------------
    // CUSTOM CATEGORY SELECTOR
    // ---------------------------------------------------------

    private void showCategoryDialog() {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "ORGANISE YOUR NOTES");
        addDialogHeading(panel, "Choose a category");

        for (String option : categories) {

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(13), dp(14), dp(13));
            row.setBackgroundResource(
                    R.drawable.bg_selector_control
            );

            TextView marker = new TextView(this);
            marker.setText(categorySymbol(option));
            marker.setTextSize(17);
            marker.setTextColor(getColor(R.color.knowledge_primary));

            TextView label = new TextView(this);
            label.setText(option);
            label.setTextSize(15);
            label.setTextColor(getColor(
                    option.equals(selectedCategory)
                            ? R.color.knowledge_primary
                            : R.color.lifeos_text
            ));
            label.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams labelParams =
                    new LinearLayout.LayoutParams(0, -2, 1);

            labelParams.leftMargin = dp(12);

            TextView check = new TextView(this);
            check.setText(option.equals(selectedCategory) ? "✓" : "");
            check.setTextColor(getColor(R.color.knowledge_primary));
            check.setTextSize(18);
            check.setTypeface(null, Typeface.BOLD);

            row.addView(marker);
            row.addView(label, labelParams);
            row.addView(check);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(-1, -2);

            rowParams.topMargin = dp(8);

            panel.addView(row, rowParams);

            row.setOnClickListener(v -> {

                selectedCategory = option;
                updateCategoryLabel();

                dialog.dismiss();
            });
        }

        Button cancel = actionButton("Cancel", false, false);

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(-1, dp(46));

        cancelParams.topMargin = dp(14);

        panel.addView(cancel, cancelParams);

        cancel.setOnClickListener(v -> dialog.dismiss());

        showCustomDialog(dialog, panel);
    }

    private void updateCategoryLabel() {

        if (textSelectedCategory != null) {
            textSelectedCategory.setText(
                    categorySymbol(selectedCategory)
                            + "    " + selectedCategory + "    ⌄"
            );
        }
    }

    private String categorySymbol(String category) {

        switch (category) {
            case "Study":
                return "▤";
            case "Work":
                return "▣";
            case "Ideas":
                return "✦";
            case "Projects":
                return "◇";
            case "Personal":
                return "♡";
            default:
                return "○";
        }
    }

    private boolean containsCategory(String value) {

        for (String category : categories) {
            if (category.equals(value)) {
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------
    // CUSTOM DELETE CONFIRMATION
    // ---------------------------------------------------------

    private void confirmDelete(Note note) {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = dialogPanel();

        addEyebrow(panel, "KNOWLEDGE HUB");
        addDialogHeading(panel, "Delete this note?");

        TextView message = new TextView(this);

        message.setText(
                "“" + safeText(note.getTitle())
                        + "” will be permanently removed."
        );

        message.setTextColor(themedMutedColor());
        message.setTextSize(14);

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(-1, -2);

        messageParams.topMargin = dp(10);

        panel.addView(message, messageParams);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button cancel = actionButton("Keep Note", false, false);
        Button delete = actionButton("Delete", true, true);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        buttons.addView(cancel, buttonParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(47), 1);

        deleteParams.leftMargin = dp(9);

        buttons.addView(delete, deleteParams);

        LinearLayout.LayoutParams buttonsParams =
                new LinearLayout.LayoutParams(-1, -2);

        buttonsParams.topMargin = dp(19);

        panel.addView(buttons, buttonsParams);

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {

            noteViewModel.delete(note);

            Toast.makeText(
                    this,
                    "Note deleted",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
        });

        showCustomDialog(dialog, panel);
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
        styleEyebrow(eyebrow);

        panel.addView(eyebrow);
    }

    private void styleEyebrow(TextView view) {

        view.setTextColor(getColor(R.color.knowledge_primary));
        view.setTextSize(10);
        view.setLetterSpacing(0.14f);
        view.setTypeface(null, Typeface.BOLD);
    }

    private void addDialogHeading(LinearLayout panel, String text) {

        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(22);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.topMargin = dp(5);

        panel.addView(heading, params);
    }

    private void addFieldLabel(LinearLayout panel, String text) {

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(themedTextColor());
        label.setTextSize(13);
        label.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.topMargin = dp(15);
        params.bottomMargin = dp(6);

        panel.addView(label, params);
    }

    private void styleInput(EditText input) {

        input.setBackgroundResource(R.drawable.bg_lifeos_input);
        input.setTextColor(themedTextColor());
        input.setHintTextColor(themedMutedColor());
        input.setTextSize(14);
        input.setPadding(dp(13), dp(12), dp(13), dp(12));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        input.setLayoutParams(params);
    }

    private Button actionButton(String text, boolean danger) {
        return actionButton(text, danger, false);
    }

    private Button actionButton(
            String text,
            boolean danger,
            boolean filled
    ) {
        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(12);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(dp(8), dp(6), dp(8), dp(6));
        button.setStateListAnimator(null);
        button.setMinHeight(dp(44));
        button.setMinimumHeight(dp(44));

        GradientDrawable background = new GradientDrawable();

        background.setCornerRadius(dp(13));

        if (isDarkMode()) {

            if (danger) {
                background.setColor(0xFF38232B);
                background.setStroke(dp(1), 0xFF965365);
                button.setTextColor(0xFFFFA0AE);

            } else if (filled) {
                background.setColor(0xFF7655E8);
                background.setStroke(dp(1), 0xFF7655E8);
                button.setTextColor(Color.WHITE);

            } else {
                background.setColor(0xFF292437);
                background.setStroke(dp(1), 0xFF65547F);
                button.setTextColor(0xFFD8C5FF);
            }

        } else {

            if (danger) {
                background.setColor(0xFFFFE9EC);
                background.setStroke(dp(1), 0xFFD65C65);
                button.setTextColor(0xFFBE4654);

            } else if (filled) {
                background.setColor(getColor(R.color.knowledge_primary));
                background.setStroke(dp(1),
                        getColor(R.color.knowledge_primary));
                button.setTextColor(Color.WHITE);

            } else {
                background.setColor(0xFFF0EAFE);
                background.setStroke(dp(1), 0xFFD8C9F5);
                button.setTextColor(0xFF6946B8);
            }
        }

        button.setBackground(background);

        return button;
    }

    private void showCustomDialog(Dialog dialog, View content) {

        dialog.setContentView(content);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT)
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = (int) (
                    getResources().getDisplayMetrics().widthPixels * 0.90f
            );

            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.dimAmount = 0.62f;

            window.setAttributes(params);
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private boolean isDarkMode() {
        int nightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private int themedTextColor() {
        return getColor(R.color.lifeos_text);
    }

    private int themedMutedColor() {
        return getColor(R.color.lifeos_muted);
    }
}
