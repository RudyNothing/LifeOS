
package com.lifeos.app;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.VaultItem;
import com.lifeos.app.security.VaultEncryption;
import com.lifeos.app.security.VaultPinManager;
import com.lifeos.app.viewmodel.VaultItemViewModel;

import java.util.List;

public class PrivacyActivity extends AppCompatActivity {

    private EditText editPin, editConfirmPin;
    private TextView textPinHeading, textPinDescription, textPinError;
    private TextView textVaultEmpty;

    private LinearLayout layoutPin, layoutVault, layoutVaultItems;

    private Button buttonUnlockVault;

    private VaultPinManager pinManager;
    private VaultItemViewModel vaultViewModel;

    private boolean isSettingUpPin = false;

    private final String[] categories = {
            "Personal", "Finance", "Social", "Work", "Other"
    };

    private int selectedCategory = 0;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        pinManager = new VaultPinManager(this);

        vaultViewModel = new ViewModelProvider(this)
                .get(VaultItemViewModel.class);

        editPin = findViewById(R.id.editPin);
        editConfirmPin = findViewById(R.id.editConfirmPin);

        textPinHeading = findViewById(R.id.textPinHeading);
        textPinDescription = findViewById(R.id.textPinDescription);
        textPinError = findViewById(R.id.textPinError);
        textVaultEmpty = findViewById(R.id.textVaultEmpty);

        layoutPin = findViewById(R.id.layoutPin);
        layoutVault = findViewById(R.id.layoutVault);
        layoutVaultItems = findViewById(R.id.layoutVaultItems);

        buttonUnlockVault = findViewById(R.id.buttonUnlockVault);

        Button buttonBack = findViewById(R.id.buttonPrivacyBack);
        Button buttonAdd = findViewById(R.id.buttonAddVaultItem);

        styleExistingButton(buttonUnlockVault, true);
        styleExistingButton(buttonBack, false);
        styleExistingButton(buttonAdd, true);

        buttonBack.setOnClickListener(v -> finish());

        buttonUnlockVault.setOnClickListener(v -> handlePinAction());

        buttonAdd.setOnClickListener(v -> showRecordEditor(null));

        vaultViewModel.getAllItems().observe(this, items -> {
            if (items != null) {
                displayVaultItems(items);
            }
        });

        if (pinManager.isPinSet()) {
            showPinEntry();
        } else {
            showPinSetup();
        }
    }

    private void styleExistingButton(Button button, boolean primary) {
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(dp(18), dp(12), dp(18), dp(12));
        button.setBackground(createBackground(
                primary ? 0xFFC64F68 : 0xFFFBE8ED,
                primary ? 0xFFC64F68 : 0xFFF0D5DC,
                14
        ));
        button.setTextColor(primary ? 0xFFFFFFFF : 0xFF96364D);
        button.setStateListAnimator(null);
    }

    private GradientDrawable createBackground(
            int fillColor, int strokeColor, int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private TextView createText(String value, int size, int color, boolean bold) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        return text;
    }

    private Button createActionButton(String label, boolean primary) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(dp(14), dp(8), dp(14), dp(8));
        button.setBackground(createBackground(
                primary ? 0xFFC64F68 : 0xFFFBE8ED,
                primary ? 0xFFC64F68 : 0xFFF0D5DC,
                12
        ));
        button.setTextColor(primary ? 0xFFFFFFFF : 0xFF96364D);
        button.setStateListAnimator(null);
        return button;
    }

    private EditText createInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextSize(14);
        input.setSingleLine(false);
        input.setInputType(inputType);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        input.setBackground(createBackground(
                0xFFFFF8F9,
                0xFFF0D5DC,
                12
        ));
        input.setTextColor(0xFF211B32);
        input.setHintTextColor(0xFF817B91);
        return input;
    }

    private void showPinSetup() {
        isSettingUpPin = true;

        textPinHeading.setText("Create Vault PIN");
        textPinDescription.setText(
                "Choose a 4–6 digit PIN to protect your private records."
        );

        editConfirmPin.setVisibility(View.VISIBLE);
        buttonUnlockVault.setText("Create PIN");

        textPinError.setVisibility(View.GONE);

        layoutPin.setVisibility(View.VISIBLE);
        layoutVault.setVisibility(View.GONE);
    }

    private void showPinEntry() {
        isSettingUpPin = false;

        textPinHeading.setText("Unlock Secure Vault");
        textPinDescription.setText(
                "Enter your PIN to access your private records."
        );

        editConfirmPin.setVisibility(View.GONE);
        buttonUnlockVault.setText("Unlock Vault");

        textPinError.setVisibility(View.GONE);

        layoutPin.setVisibility(View.VISIBLE);
        layoutVault.setVisibility(View.GONE);
    }

    private void handlePinAction() {
        String pin = editPin.getText().toString().trim();

        if (!pin.matches("\\d{4,6}")) {
            showError("PIN must contain 4 to 6 digits.");
            return;
        }

        if (isSettingUpPin) {

            String confirmPin =
                    editConfirmPin.getText().toString().trim();

            if (!pin.equals(confirmPin)) {
                showError("PINs do not match.");
                return;
            }

            if (pinManager.setPin(pin)) {

                Toast.makeText(
                        this,
                        "Vault PIN created successfully.",
                        Toast.LENGTH_SHORT
                ).show();

                editPin.setText("");
                editConfirmPin.setText("");

                showPinEntry();

            } else {
                showError("Unable to create PIN. Try again.");
            }

        } else {

            if (pinManager.verifyPin(pin)) {

                editPin.setText("");
                textPinError.setVisibility(View.GONE);

                layoutPin.setVisibility(View.GONE);
                layoutVault.setVisibility(View.VISIBLE);

            } else {
                showError("Incorrect PIN. Please try again.");
            }
        }
    }

    private void displayVaultItems(List<VaultItem> items) {

        layoutVaultItems.removeAllViews();

        if (items.isEmpty()) {
            textVaultEmpty.setVisibility(View.VISIBLE);
            return;
        }

        textVaultEmpty.setVisibility(View.GONE);

        for (VaultItem item : items) {

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(18), dp(16), dp(18), dp(16));
            card.setBackground(createBackground(
                    0xFFFFF8F9,
                    0xFFF0D5DC,
                    16
            ));

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, dp(7), 0, dp(12));
            card.setLayoutParams(cardParams);

            TextView title = createText(
                    item.getTitle(),
                    17,
                    0xFF211B32,
                    true
            );

            TextView details = createText(
                    item.getCategory() + "  •  " + item.getUsername(),
                    12,
                    0xFF817B91,
                    false
            );

            details.setPadding(0, dp(5), 0, dp(12));

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setGravity(Gravity.CENTER_VERTICAL);

            Button viewButton = createActionButton("View", false);
            Button editButton = createActionButton("Edit", false);
            Button deleteButton = createActionButton("Delete", false);

            addAction(actions, viewButton);
            addAction(actions, editButton);
            addAction(actions, deleteButton);

            card.addView(title);
            card.addView(details);
            card.addView(actions);

            viewButton.setOnClickListener(v -> viewRecord(item));
            editButton.setOnClickListener(v -> showRecordEditor(item));
            deleteButton.setOnClickListener(v -> confirmDelete(item));

            layoutVaultItems.addView(card);
        }
    }

    private void addAction(LinearLayout parent, Button button) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(44),
                        1
                );

        params.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(button, params);
    }

    private void showRecordEditor(VaultItem existingItem) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(createRecordEditor(dialog, existingItem));

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private View createRecordEditor(Dialog dialog, VaultItem existingItem) {

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(22), dp(22), dp(22), dp(20));
        container.setBackground(createBackground(
                0xFFFFFFFF,
                0xFFF0D5DC,
                22
        ));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView eyebrow = createText(
                "SECURE VAULT",
                11,
                0xFFC64F68,
                true
        );

        TextView heading = createText(
                existingItem == null
                        ? "Add Secure Record"
                        : "Edit Secure Record",
                22,
                0xFF211B32,
                true
        );

        TextView subtitle = createText(
                "Your sensitive information is stored in encrypted form.",
                12,
                0xFF817B91,
                false
        );

        subtitle.setPadding(0, dp(5), 0, dp(18));

        EditText title = createInput(
                "Record title *",
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        EditText username = createInput(
                "Username / Email",
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        EditText password = createInput(
                "Password *",
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        EditText notes = createInput(
                "Private notes",
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        notes.setMinLines(3);
        notes.setGravity(Gravity.TOP);

        TextView categoryLabel = createText(
                "CATEGORY",
                11,
                0xFF817B91,
                true
        );

        TextView categoryValue = createText(
                categories[selectedCategory],
                14,
                0xFF211B32,
                true
        );

        categoryValue.setPadding(dp(14), dp(14), dp(14), dp(14));
        categoryValue.setBackground(createBackground(
                0xFFFFF8F9,
                0xFFF0D5DC,
                12
        ));

        categoryValue.setOnClickListener(v -> {
            int currentSelection = selectedCategory;

            new AlertDialog.Builder(this)
                    .setTitle("Choose category")
                    .setSingleChoiceItems(
                            categories,
                            currentSelection,
                            (categoryDialog, which) -> {
                                selectedCategory = which;
                                categoryValue.setText(categories[which]);
                                categoryDialog.dismiss();
                            }
                    )
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        addEditorField(content, "TITLE", title);
        addEditorField(content, "USERNAME / EMAIL", username);
        addEditorField(content, "PASSWORD", password);
        addEditorField(content, "PRIVATE NOTES", notes);

        content.addView(categoryLabel);
        content.addView(categoryValue);

        LinearLayout.LayoutParams categoryParams =
                (LinearLayout.LayoutParams) categoryValue.getLayoutParams();

        if (categoryParams != null) {
            categoryParams.topMargin = dp(8);
            categoryValue.setLayoutParams(categoryParams);
        }

        scrollView.addView(content);

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(390)
                );

        container.addView(eyebrow);
        container.addView(heading);
        container.addView(subtitle);
        container.addView(scrollView, scrollParams);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        buttons.setPadding(0, dp(18), 0, 0);

        Button cancel = createActionButton("Cancel", false);
        Button save = createActionButton("Save Record", true);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1
                );

        buttonParams.setMargins(dp(3), 0, dp(3), 0);

        buttons.addView(cancel, buttonParams);
        buttons.addView(save, buttonParams);

        container.addView(buttons);

        if (existingItem != null) {

            title.setText(existingItem.getTitle());
            username.setText(existingItem.getUsername());

            try {
                password.setText(
                        VaultEncryption.decrypt(
                                existingItem.getEncryptedPassword()
                        )
                );

                notes.setText(
                        VaultEncryption.decrypt(
                                existingItem.getEncryptedNotes()
                        )
                );

            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Unable to decrypt this record.",
                        Toast.LENGTH_LONG
                ).show();

                dialog.dismiss();
                return container;
            }

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(existingItem.getCategory())) {
                    selectedCategory = i;
                    categoryValue.setText(categories[i]);
                    break;
                }
            }

        } else {
            selectedCategory = 0;
        }

        cancel.setOnClickListener(v -> dialog.dismiss());

        save.setOnClickListener(v -> {

            String recordTitle = title.getText().toString().trim();
            String recordUsername = username.getText().toString().trim();
            String recordPassword = password.getText().toString();
            String recordNotes = notes.getText().toString();

            if (recordTitle.isEmpty()) {
                title.setError("Enter a title");
                title.requestFocus();
                return;
            }

            if (recordPassword.isEmpty()) {
                password.setError("Enter a password");
                password.requestFocus();
                return;
            }

            try {
                String encryptedPassword =
                        VaultEncryption.encrypt(recordPassword);

                String encryptedNotes =
                        VaultEncryption.encrypt(recordNotes);

                long currentTime = System.currentTimeMillis();

                if (existingItem == null) {

                    VaultItem item = new VaultItem(
                            recordTitle,
                            recordUsername,
                            encryptedPassword,
                            encryptedNotes,
                            categories[selectedCategory],
                            currentTime,
                            currentTime
                    );

                    vaultViewModel.insert(item);

                } else {

                    existingItem.setTitle(recordTitle);
                    existingItem.setUsername(recordUsername);
                    existingItem.setEncryptedPassword(encryptedPassword);
                    existingItem.setEncryptedNotes(encryptedNotes);
                    existingItem.setCategory(categories[selectedCategory]);
                    existingItem.setUpdatedAt(currentTime);

                    vaultViewModel.update(existingItem);
                }

                Toast.makeText(
                        this,
                        "Secure record saved.",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();

            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Encryption failed. Record was not saved.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        return container;
    }

    private void addEditorField(
            LinearLayout parent,
            String label,
            EditText input
    ) {
        TextView fieldLabel = createText(
                label,
                11,
                0xFF817B91,
                true
        );

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        labelParams.topMargin = dp(10);
        labelParams.bottomMargin = dp(6);

        parent.addView(fieldLabel, labelParams);

        parent.addView(input, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    private void viewRecord(VaultItem item) {

        try {
            String password = VaultEncryption.decrypt(
                    item.getEncryptedPassword()
            );

            String notes = VaultEncryption.decrypt(
                    item.getEncryptedNotes()
            );

            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(dp(20), dp(12), dp(20), dp(8));

            TextView username = createText(
                    "USERNAME\n" + item.getUsername(),
                    14,
                    0xFF211B32,
                    false
            );

            TextView passwordText = createText(
                    "PASSWORD\n" + password,
                    14,
                    0xFF211B32,
                    false
            );

            TextView notesText = createText(
                    "PRIVATE NOTES\n" + (notes.isEmpty() ? "No notes" : notes),
                    14,
                    0xFF211B32,
                    false
            );

            username.setPadding(0, dp(8), 0, dp(12));
            passwordText.setPadding(0, dp(8), 0, dp(12));
            notesText.setPadding(0, dp(8), 0, dp(8));

            content.addView(username);
            content.addView(passwordText);
            content.addView(notesText);

            new AlertDialog.Builder(this)
                    .setTitle(item.getTitle())
                    .setView(content)
                    .setPositiveButton("Close", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to decrypt this record.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    private void confirmDelete(VaultItem item) {

        Dialog dialog = new Dialog(this);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(22), dp(22), dp(22), dp(20));
        panel.setBackgroundResource(R.drawable.bg_dialog_surface);

        TextView eyebrow = createText(
                "SECURE VAULT",
                11,
                0xFFC64F68,
                true
        );

        TextView heading = createText(
                "Delete this record?",
                23,
                getColor(R.color.lifeos_text),
                true
        );

        heading.setPadding(0, dp(7), 0, 0);

        TextView message = createText(
                "\"" + item.getTitle() + "\" will be permanently removed.",
                14,
                getColor(R.color.lifeos_muted),
                false
        );

        message.setPadding(0, dp(12), 0, 0);

        panel.addView(eyebrow);
        panel.addView(heading);
        panel.addView(message);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);

        Button cancelButton = createDeleteDialogButton(
                "Keep Record",
                0xFFF7E8EF,
                0xFF633449
        );

        Button deleteButton = createDeleteDialogButton(
                "Delete",
                0xFFFBE9EA,
                0xFFD45B65
        );

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(0, dp(50), 1);

        actions.addView(cancelButton, cancelParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(50), 1);

        deleteParams.leftMargin = dp(9);

        actions.addView(deleteButton, deleteParams);

        LinearLayout.LayoutParams actionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        actionParams.topMargin = dp(20);

        panel.addView(actions, actionParams);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {

            vaultViewModel.delete(item);

            Toast.makeText(
                    this,
                    "Secure record deleted.",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
        });

        dialog.setContentView(panel);
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.90f),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            WindowManager.LayoutParams params =
                    dialog.getWindow().getAttributes();

            params.dimAmount = 0.62f;
            dialog.getWindow().addFlags(
                    android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            dialog.getWindow().setAttributes(params);
        }
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


    private void showError(String message) {
        textPinError.setText(message);
        textPinError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (layoutVault != null) {
            layoutVault.setVisibility(View.GONE);
            layoutPin.setVisibility(View.VISIBLE);

            editPin.setText("");

            if (pinManager != null && pinManager.isPinSet()) {
                showPinEntry();
            }
        }
    }
}
