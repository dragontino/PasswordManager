package com.security.passwordmanager.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    fun addSettings(settings: Settings)

    //TODO добавить where
    @Query("UPDATE SettingsTable SET theme = :theme")
    fun updateTheme(theme: String)

    @Query("UPDATE SettingsTable SET isUsingBeautifulFont = :usingBeautifulFont")
    fun updateUsingBeautifulFont(usingBeautifulFont: Boolean)

    @Query("UPDATE SettingsTable SET isShowingDataHints = :usingDataHints")
    fun updateDataHints(usingDataHints: Boolean)

    @Query("UPDATE SettingsTable SET isUsingBottomView = :usingBottomView")
    fun updateUsingBottomView(usingBottomView: Boolean)

    @Query("SELECT * FROM SettingsTable")
    fun getSettings(): Settings?
}