package com.security.passwordmanager.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.presentation.model.enums.Themes
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSettings(settings: Settings)

    @Query("UPDATE SettingsTable SET theme = :theme WHERE email = :email")
    suspend fun updateTheme(email: String, theme: Themes)

    @Query("UPDATE SettingsTable SET isUsingBeautifulFont = :usingBeautifulFont WHERE email = :email")
    suspend fun updateUsingBeautifulFont(email: String, usingBeautifulFont: Boolean)

    @Query("UPDATE SettingsTable SET isUsingAutofill = :autofill WHERE email = :email")
    suspend fun updateAutofill(email: String, autofill: Boolean)

    @Query("UPDATE SettingsTable SET usingDynamicColor = :useDynamicColor WHERE email = :email")
    suspend fun updateUsingDynamicColor(email: String, useDynamicColor: Boolean)

    @Query("SELECT * FROM SettingsTable WHERE email = :email")
    fun getSettings(email: String): Flow<Settings?>

    @Query("SELECT COUNT(*) FROM SettingsTable")
    suspend fun getCountOfRows(): Int
}