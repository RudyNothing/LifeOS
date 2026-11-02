
package com.lifeos.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.Profile;
import com.lifeos.app.viewmodel.ProfileViewModel;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int PROFILE_ID = 1;

    private ProfileViewModel profileViewModel;

    private EditText editName, editEmail;
//    private TextView textCurrency;
    private TextView textInitials, textUpdated;
    private ImageView imageProfile;

    private String selectedCurrency = "INR (₹)";
    private String selectedImageUri = null;

    private boolean profileLoaded = false;
    private boolean onboardingMode = false;
    private long profileCreatedAt = 0L;

//    private final String[] currencies = {
//            "INR (₹)", "USD ($)", "EUR (€)", "GBP (£)",
//            "AED (د.إ)", "JPY (¥)", "CAD ($)", "AUD ($)"
//    };

    private ActivityResultLauncher<String[]> imagePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_profile);

        onboardingMode = getIntent().getBooleanExtra(
                "EXTRA_ONBOARDING", false);

        applyInsets();
        bindViews();

        if (onboardingMode) {
            findViewById(R.id.buttonProfileBack).setVisibility(View.GONE);

            findViewById(R.id.buttonSaveProfile).setEnabled(false);
            ((android.widget.Button) findViewById(
                    R.id.buttonSaveProfile)).setText("Get Started");
        }
        if (onboardingMode) {

            findViewById(R.id.buttonProfileBack)
                    .setVisibility(View.GONE);

            ((TextView) findViewById(R.id.textProfileHeading))
                    .setText("WELCOME TO\nLIFEOS.");

            ((TextView) findViewById(R.id.textProfileSubtitle))
                    .setText("Let's set up your personal space. " + "You can change these details anytime.");

            ((TextView) findViewById(R.id.textProfileIdentity))
                    .setText("Your LifeOS starts here");

            ((android.widget.Button) findViewById(
                    R.id.buttonSaveProfile))
                    .setText("Get Started   →");
        }

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException ignored) {
                            // The selected image may still be available
                            // during the current session.
                        }

                        selectedImageUri = uri.toString();
                        showSelectedImage(uri);
                    }
                }
        );

        findViewById(R.id.buttonProfileBack)
                .setOnClickListener(v -> finish());

        findViewById(R.id.buttonChangePhoto)
                .setOnClickListener(v -> openImagePicker());

        findViewById(R.id.profileAvatarArea)
                .setOnClickListener(v -> openImagePicker());

//        findViewById(R.id.rowCurrency)
//                .setOnClickListener(v -> showCurrencyPicker());

        findViewById(R.id.buttonSaveProfile)
                .setOnClickListener(v -> saveProfile());

        profileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        profileViewModel.getProfileById(PROFILE_ID)
                .observe(this, this::showProfile);
    }

    private void applyInsets() {
        View root = findViewById(R.id.profileRoot);

        int left = root.getPaddingLeft();
        int top = root.getPaddingTop();
        int right = root.getPaddingRight();
        int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars());

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

    private void bindViews() {
        editName = findViewById(R.id.editProfileName);
        editEmail = findViewById(R.id.editProfileEmail);
//        textCurrency = findViewById(R.id.textProfileCurrency);
        textInitials = findViewById(R.id.textProfileInitials);
        textUpdated = findViewById(R.id.textProfileUpdated);
        imageProfile = findViewById(R.id.imageProfile);
    }

    private void openImagePicker() {
        imagePicker.launch(new String[]{"image/*"});
    }

    private void showSelectedImage(Uri uri) {
        imageProfile.setImageURI(uri);
        imageProfile.setVisibility(View.VISIBLE);
        textInitials.setVisibility(View.GONE);
    }

    private void showProfile(Profile profile) {

        profileLoaded = profile != null;

        if (profile == null) {
            profileCreatedAt = 0L;
//            selectedCurrency = "INR (₹)";
            selectedImageUri = null;

//            textCurrency.setText(selectedCurrency);
            textUpdated.setText(
                    "Your profile is stored only on this device.");

            showDefaultAvatar();

            if (onboardingMode) {
                findViewById(R.id.buttonSaveProfile).setEnabled(true);
            }

            return;
        }

        profileCreatedAt = profile.getCreatedAt();

        editName.setText(
                profile.getName() == null ? "" : profile.getName());

        editEmail.setText(
                profile.getEmail() == null ? "" : profile.getEmail());

//        selectedCurrency = profile.getCurrency() == null
//                || profile.getCurrency().trim().isEmpty()
//                ? "INR (₹)"
//                : profile.getCurrency();

//        textCurrency.setText(selectedCurrency);

        selectedImageUri = profile.getProfileImage();

        if (selectedImageUri != null
                && !selectedImageUri.trim().isEmpty()) {

            try {
                showSelectedImage(Uri.parse(selectedImageUri));
            } catch (Exception ignored) {
                showDefaultAvatar();
            }

        } else {
            showDefaultAvatar();
        }

        if (profile.getUpdatedAt() > 0) {
            String updated = DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT
            ).format(new Date(profile.getUpdatedAt()));

            textUpdated.setText("Last updated " + updated);

        } else {
            textUpdated.setText(
                    "Your profile is stored only on this device.");
        }
    }

    private void showDefaultAvatar() {
        imageProfile.setImageResource(
                R.drawable.ic_profile_silhouette);

        imageProfile.setVisibility(View.VISIBLE);
        textInitials.setVisibility(View.GONE);
    }

//    private void showCurrencyPicker() {
//
//        int checked = 0;

//        for (int i = 0; i < currencies.length; i++) {
//            if (currencies[i].equals(selectedCurrency)) {
//                checked = i;
//                break;
//            }
//        }

//        new AlertDialog.Builder(this)
//                .setTitle("Choose display currency")
//                .setSingleChoiceItems(currencies, checked,
//                        (dialog, which) -> {
//                            selectedCurrency = currencies[which];
//                            textCurrency.setText(selectedCurrency);
//                            dialog.dismiss();
//                        })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }

    private void saveProfile() {

        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Enter your name");
            editName.requestFocus();
            return;
        }

        if (!email.isEmpty()
                && !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {

            editEmail.setError("Enter a valid email address");
            editEmail.requestFocus();
            return;
        }

        long now = System.currentTimeMillis();

        Profile profile = new Profile(
                PROFILE_ID,
                name,
                email,
                selectedImageUri,
                selectedCurrency,
                profileLoaded ? profileCreatedAt : now,
                now
        );

        if (profileLoaded) {
            profileViewModel.update(profile);
        } else {
            profileViewModel.insert(profile);
            profileLoaded = true;
            profileCreatedAt = now;
        }

        Toast.makeText(
                this,
                "Profile saved",
                Toast.LENGTH_SHORT
        ).show();

        if (onboardingMode) {
            finish();
            return;
        }

        textUpdated.setText("Saved on this device");

        if (selectedImageUri != null
                && !selectedImageUri.isEmpty()) {
            showSelectedImage(Uri.parse(selectedImageUri));
        } else {
            showDefaultAvatar();
        }
    }
}
