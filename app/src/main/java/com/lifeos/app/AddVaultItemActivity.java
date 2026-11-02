package com.lifeos.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.VaultItem;
import com.lifeos.app.security.VaultEncryption;
import com.lifeos.app.viewmodel.VaultItemViewModel;

public class AddVaultItemActivity extends AppCompatActivity {

    private EditText editTitle, editUsername, editPassword, editNotes;
    private Spinner spinnerCategory;
    private TextView textHeading;

    private VaultItemViewModel vaultViewModel;

    private VaultItem editingItem;
    private int itemId = -1;

    private final String[] categories = {
            "Personal", "Finance", "Social", "Work", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vault_item);

        editTitle = findViewById(R.id.editVaultTitle);
        editUsername = findViewById(R.id.editVaultUsername);
        editPassword = findViewById(R.id.editVaultPassword);
        editNotes = findViewById(R.id.editVaultNotes);

        spinnerCategory = findViewById(R.id.spinnerVaultCategory);
        textHeading = findViewById(R.id.textVaultFormHeading);

        Button buttonSave = findViewById(R.id.buttonSaveVaultItem);
        Button buttonCancel = findViewById(R.id.buttonCancelVaultItem);

        vaultViewModel = new ViewModelProvider(this)
                .get(VaultItemViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(adapter);

        itemId = getIntent().getIntExtra("vault_item_id", -1);

        if (itemId != -1) {
            loadItemForEditing();
        }

        buttonSave.setOnClickListener(v -> saveItem());

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadItemForEditing() {

        textHeading.setText("Edit Secure Record");
        findViewById(R.id.buttonSaveVaultItem);

        vaultViewModel.getItemById(itemId).observe(this, item -> {

            if (item == null) {
                return;
            }

            editingItem = item;

            editTitle.setText(item.getTitle());
            editUsername.setText(item.getUsername());

            try {
                editPassword.setText(
                        VaultEncryption.decrypt(
                                item.getEncryptedPassword()
                        )
                );

                editNotes.setText(
                        VaultEncryption.decrypt(
                                item.getEncryptedNotes()
                        )
                );

            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Unable to decrypt this record.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
                return;
            }

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(item.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        });
    }

    private void saveItem() {

        String title = editTitle.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();
        String notes = editNotes.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty()) {
            editTitle.setError("Enter a title");
            editTitle.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Enter a password");
            editPassword.requestFocus();
            return;
        }

        try {

            String encryptedPassword =
                    VaultEncryption.encrypt(password);

            String encryptedNotes =
                    VaultEncryption.encrypt(notes);

            long currentTime = System.currentTimeMillis();

            if (editingItem == null) {

                VaultItem item = new VaultItem(
                        title,
                        username,
                        encryptedPassword,
                        encryptedNotes,
                        category,
                        currentTime,
                        currentTime
                );

                vaultViewModel.insert(item);

            } else {

                editingItem.setTitle(title);
                editingItem.setUsername(username);
                editingItem.setEncryptedPassword(encryptedPassword);
                editingItem.setEncryptedNotes(encryptedNotes);
                editingItem.setCategory(category);
                editingItem.setUpdatedAt(currentTime);

                vaultViewModel.update(editingItem);
            }

            Toast.makeText(
                    this,
                    "Secure record saved.",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Encryption failed. Record was not saved.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
