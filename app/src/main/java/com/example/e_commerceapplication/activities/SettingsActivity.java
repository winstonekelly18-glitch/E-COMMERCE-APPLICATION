package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.utils.PreferenceManager;
import com.example.e_commerceapplication.utils.ThemeManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefManager = new PreferenceManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSettings);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        RadioGroup group = findViewById(R.id.rgThemeMode);

        MaterialSwitch switchBiometric = findViewById(R.id.switchBiometric);
        if (switchBiometric != null) {
            switchBiometric.setChecked(prefManager.isBiometricEnabled());
            switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefManager.setBiometricEnabled(isChecked);
            });
        }

        if (group != null) {
            // Tick the saved option
            switch (ThemeManager.getSavedMode(this)) {
                case ThemeManager.LIGHT:  group.check(R.id.rbThemeLight);  break;
                case ThemeManager.DARK:   group.check(R.id.rbThemeDark);   break;
                default:                  group.check(R.id.rbThemeSystem);
            }

            // Save + apply when the user picks one
            group.setOnCheckedChangeListener((g, checkedId) -> {
                if (checkedId == R.id.rbThemeLight) {
                    ThemeManager.setMode(this, ThemeManager.LIGHT);
                } else if (checkedId == R.id.rbThemeDark) {
                    ThemeManager.setMode(this, ThemeManager.DARK);
                } else {
                    ThemeManager.setMode(this, ThemeManager.SYSTEM);
                }
            });
        }
    }
}