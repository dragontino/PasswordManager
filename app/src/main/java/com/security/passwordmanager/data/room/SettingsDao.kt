package com.security.passwordmanager.data.room

import androidx.room.*
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSettings(settings: Settings)

    @Update(entity = Settings::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: Settings)

    @Query("UPDATE SettingsTable SET colorDesign = :colorDesign WHERE email = :email")
    suspend fun updateTheme(email: String, colorDesign: ColorDesign)

    @Query("SELECT * FROM SettingsTable WHERE email = :email")
    fun getSettings(email: String): Flow<Settings?>

    @Query("SELECT COUNT(*) FROM SettingsTable")
    suspend fun getCountOfRows(): Int
}