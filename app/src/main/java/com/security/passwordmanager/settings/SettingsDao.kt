package com.security.passwordmanager.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    fun addSettings(settings: Settings)

    @Query("SELECT * FROM SettingsTable")
    fun getSettings(): Settings?
}