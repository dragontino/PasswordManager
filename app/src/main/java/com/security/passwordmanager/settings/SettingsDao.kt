package com.security.passwordmanager.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    fun addSettings(settings: Settings)

    @Query("UPDATE SettingsTable SET theme = :theme WHERE email = :email")
    fun updateTheme(email: String, theme: String)

    @Query("UPDATE SettingsTable SET isUsingBeautifulFont = :usingBeautifulFont WHERE email = :email")
    fun updateUsingBeautifulFont(email: String, usingBeautifulFont: Boolean)

    @Query("UPDATE SettingsTable SET isShowingDataHints = :usingDataHints WHERE email = :email")
    fun updateDataHints(email: String, usingDataHints: Boolean)

    @Query("UPDATE SettingsTable SET isUsingBottomView = :usingBottomView WHERE email = :email")
    fun updateUsingBottomView(email: String, usingBottomView: Boolean)

    @Query("SELECT * FROM SettingsTable WHERE email = :email")
    fun getSettings(email: String): Settings?
}