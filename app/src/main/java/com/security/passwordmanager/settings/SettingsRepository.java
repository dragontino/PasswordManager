package com.security.passwordmanager.settings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.security.passwordmanager.data.MainDatabase;

public class SettingsRepository {

    private final SettingsDao settingsDao;

    public SettingsRepository(@NonNull Context context) {
        settingsDao = MainDatabase.getDatabase(context).settingsDao();
    }

    public void updateTheme(@Support.ThemeDef String theme) {
        Support.Settings settings = new Support.Settings(theme);
        settingsDao.addSettings(settings);
    }

    public @Support.ThemeDef String getTheme() {
        Support.Settings settings = settingsDao.getSettings();
        if (settings == null)
            updateTheme(Support.SYSTEM_THEME);

        return settingsDao.getSettings().getTheme();
    }
}
