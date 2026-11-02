
package com.lifeos.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.Profile;
import com.lifeos.app.viewmodel.ProfileViewModel;
import com.lifeos.app.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "lifeos_appearance";
    private static final String KEY_MODE = "theme_mode";

    private TaskViewModel taskViewModel;
    private ProfileViewModel profileViewModel;

    private TextView textPendingTaskCount;
    private ImageView imageDashboardProfile;

    private boolean onboardingOpened = false;
    private boolean dashboardAnimated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applySavedTheme();
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_dashboard);

        applyDashboardInsets();

        // Task Manager count
        textPendingTaskCount = findViewById(
                R.id.textPendingTaskCount);

        taskViewModel = new ViewModelProvider(this)
                .get(TaskViewModel.class);

        taskViewModel.getPendingTasks().observe(this, tasks -> {
            int count = tasks == null ? 0 : tasks.size();

            textPendingTaskCount.setText(String.valueOf(count));
        });

        // Task Manager
        findViewById(R.id.buttonOpenTasks).setOnClickListener(v ->
                open(TaskActivity.class));

        findViewById(R.id.cardTasks).setOnClickListener(v ->
                open(TaskActivity.class));

        // Schedule Manager
        findViewById(R.id.cardSchedule).setOnClickListener(v ->
                open(ScheduleActivity.class));

        // Finance Manager
        findViewById(R.id.cardFinance).setOnClickListener(v ->
                open(FinanceActivity.class));

        // Knowledge Hub
        findViewById(R.id.cardKnowledge).setOnClickListener(v ->
                open(KnowledgeHubActivity.class));

        // Journal & Memories
        findViewById(R.id.cardJournal).setOnClickListener(v ->
                open(JournalActivity.class));

        // Privacy Corner
        findViewById(R.id.cardPrivacy).setOnClickListener(v ->
                open(PrivacyActivity.class));

        // Appearance Studio
        findViewById(R.id.buttonAppearance).setOnClickListener(v ->
                showAppearancePicker());

        // Profile avatar
        imageDashboardProfile = findViewById(
                R.id.imageDashboardProfile);

        findViewById(R.id.dashboardProfileButton)
                .setOnClickListener(v -> open(ProfileActivity.class));

        // First-launch profile onboarding
        setupProfileObserver();
    }

    private void applyDashboardInsets() {

        View root = findViewById(R.id.dashboardRoot);

        int initialLeft = root.getPaddingLeft();
        int initialTop = root.getPaddingTop();
        int initialRight = root.getPaddingRight();
        int initialBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root,
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

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

    private void setupProfileObserver() {

        profileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        profileViewModel.getProfileById(1).observe(this, profile -> {

            if (profile == null) {

                // No saved profile: launch onboarding once.
                if (!onboardingOpened) {

                    onboardingOpened = true;

                    findViewById(R.id.dashboardRoot)
                            .setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(
                            MainActivity.this,
                            ProfileActivity.class);

                    intent.putExtra("EXTRA_ONBOARDING", true);

                    startActivity(intent);
                }

            } else {

                // A profile exists: show the dashboard.
                findViewById(R.id.dashboardRoot)
                        .setVisibility(View.VISIBLE);

                loadDashboardProfile(profile);

                // Animate only once per MainActivity instance.
                if (!dashboardAnimated) {
                    dashboardAnimated = true;
                    animateDashboard();
                }
            }
        });
    }

    private void loadDashboardProfile(Profile profile) {

        String imageUri = profile.getProfileImage();

        if (imageUri != null && !imageUri.trim().isEmpty()) {

            try {
                imageDashboardProfile.setImageURI(
                        Uri.parse(imageUri));

                if (imageDashboardProfile.getDrawable() == null) {
                    showDefaultDashboardAvatar();
                }

            } catch (Exception e) {
                showDefaultDashboardAvatar();
            }

        } else {
            showDefaultDashboardAvatar();
        }
    }

    private void showDefaultDashboardAvatar() {

        imageDashboardProfile.setImageResource(
                R.drawable.ic_profile_silhouette);
    }

    private void open(Class<?> activity) {

        startActivity(new Intent(this, activity));

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }

    private void animateDashboard() {

        int[] ids = {
                R.id.buttonAppearance,
                R.id.textPendingTaskCount,
                R.id.cardSchedule,
                R.id.cardFinance,
                R.id.cardKnowledge,
                R.id.cardJournal,
                R.id.cardPrivacy,
                R.id.cardTasks
        };

        for (int i = 0; i < ids.length; i++) {

            View view = findViewById(ids[i]);

            if (view == null) continue;

            view.setAlpha(0f);
            view.setTranslationY(24f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(i * 65L)
                    .setDuration(420)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void showAppearancePicker() {

        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        android.widget.LinearLayout panel =
                new android.widget.LinearLayout(this);

        panel.setOrientation(
                android.widget.LinearLayout.VERTICAL);

        int pad = (int) (22 *
                getResources().getDisplayMetrics().density);

        panel.setPadding(pad, pad, pad, pad);
        panel.setBackgroundResource(
                R.drawable.bg_dialog_surface);

        TextView eyebrow = new TextView(this);
        eyebrow.setText("LIFEOS / PERSONALISE");
        eyebrow.setTextColor(getColor(R.color.lifeos_primary));
        eyebrow.setTextSize(10);
        eyebrow.setLetterSpacing(0.16f);
        eyebrow.setTypeface(null,
                android.graphics.Typeface.BOLD);

        panel.addView(eyebrow);

        TextView heading = new TextView(this);
        heading.setText("Appearance Studio");
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTextSize(25);
        heading.setTypeface(null,
                android.graphics.Typeface.BOLD);

        android.widget.LinearLayout.LayoutParams hp =
                new android.widget.LinearLayout.LayoutParams(
                        -1, -2);

        hp.topMargin = (int) (7 *
                getResources().getDisplayMetrics().density);

        panel.addView(heading, hp);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Choose the look that feels like you.");

        subtitle.setTextColor(getColor(R.color.lifeos_muted));
        subtitle.setTextSize(14);

        android.widget.LinearLayout.LayoutParams sp =
                new android.widget.LinearLayout.LayoutParams(
                        -1, -2);

        sp.topMargin = (int) (4 *
                getResources().getDisplayMetrics().density);

        panel.addView(subtitle, sp);

        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        int current = prefs.getInt(
                KEY_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        android.widget.RadioGroup group =
                new android.widget.RadioGroup(this);

        group.setOrientation(
                android.widget.RadioGroup.VERTICAL);

        String[] labels = {
                "Follow device settings",
                "Light mode",
                "Dark mode"
        };

        int[] modes = {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.MODE_NIGHT_YES
        };

        int selectedId = -1;

        for (int i = 0; i < labels.length; i++) {

            android.widget.RadioButton option =
                    new android.widget.RadioButton(this);

            option.setId(View.generateViewId());
            option.setText(labels[i]);
            option.setTextColor(getColor(R.color.lifeos_text));
            option.setTextSize(15);

            option.setButtonTintList(
                    android.content.res.ColorStateList.valueOf(
                            getColor(R.color.lifeos_primary)));

            option.setPadding(4, pad / 3, 4, pad / 3);
            option.setTag(modes[i]);

            group.addView(
                    option,
                    new android.widget.RadioGroup.LayoutParams(
                            -1, -2));

            if (modes[i] == current) {
                selectedId = option.getId();
            }
        }

        if (selectedId != -1) {
            group.check(selectedId);
        }

        android.widget.LinearLayout.LayoutParams gp =
                new android.widget.LinearLayout.LayoutParams(
                        -1, -2);

        gp.topMargin = pad / 2;
        panel.addView(group, gp);

        TextView hint = new TextView(this);
        hint.setText(
                "Your choice is saved automatically.");

        hint.setTextColor(getColor(R.color.lifeos_muted));
        hint.setTextSize(12);

        panel.addView(hint);

        android.widget.Button done =
                new android.widget.Button(this);

        done.setText("Done");
        done.setAllCaps(false);
        done.setTextColor(android.graphics.Color.WHITE);

        done.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        getColor(R.color.lifeos_primary)));

        android.widget.LinearLayout.LayoutParams dp =
                new android.widget.LinearLayout.LayoutParams(
                        -1, -2);

        dp.topMargin = pad / 2;
        panel.addView(done, dp);

        done.setOnClickListener(v -> {

            int checked = group.getCheckedRadioButtonId();

            if (checked != -1) {

                android.widget.RadioButton choice =
                        group.findViewById(checked);

                int mode = (Integer) choice.getTag();

                prefs.edit()
                        .putInt(KEY_MODE, mode)
                        .apply();

                AppCompatDelegate.setDefaultNightMode(mode);
            }

            dialog.dismiss();
        });

        dialog.setContentView(panel);

        android.view.Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent);

            window.setLayout(
                    (int) (getResources()
                            .getDisplayMetrics().widthPixels * 0.90f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);

            dialog.getWindow().setLayout(
                    (int) (getResources()
                            .getDisplayMetrics().widthPixels * 0.90f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void applySavedTheme() {

        SharedPreferences preferences =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        int mode = preferences.getInt(
                KEY_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
