package com.security.passwordmanager.settings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SettingsDao {

    @Insert(entity = Support.Settings.class, onConflict = OnConflictStrategy.REPLACE)
    void addSettings(Support.Settings settings);

    @Query("SELECT * FROM SettingsTable")
    Support.Settings getSettings();
}
