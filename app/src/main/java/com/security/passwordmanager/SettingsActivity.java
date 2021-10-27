package com.security.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "settingsActivity";

    private SwitchCompat switchTheme;
    private Support support;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        support = Support.get(this);
        switchTheme = findViewById(R.id.switchTheme);

        switchTheme.setChecked(!support.isLightTheme());
        updateTheme();


        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) support.setTheme(Support.DARK_THEME);
            else support.setTheme(Support.LIGHT_THEME);
            updateTheme();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                setTitle(R.string.title_activity_settings);
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateTheme() {
        support.updateThemeInScreen(getWindow(), getSupportActionBar());
        switchTheme.setTextColor(support.getFontColor());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}