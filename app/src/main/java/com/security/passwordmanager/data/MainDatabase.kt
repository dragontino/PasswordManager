package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.security.passwordmanager.settings.Settings
import com.security.passwordmanager.settings.SettingsDao

@Database(entities = [Website::class, BankCard::class, Settings::class], version = 2, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {

    abstract fun dataDao() : DataDao

    abstract fun settingsDao() : SettingsDao

    companion object {
        @Volatile
        private var INSTANCE : MainDatabase? = null

        private val MIGRATION_1_2 = Migration(1, 2) {
            for (property in arrayOf("isUsingBeautifulFont", "isShowingDataHints", "isUsingBottomView"))
                it.execSQL(
                    "ALTER TABLE SettingsTable ADD COLUMN $property INTEGER DEFAULT 0 NOT NULL")
        }

        fun getDatabase(context : Context) : MainDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        MainDatabase::class.java,
                        "PasswordBase"
                )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}