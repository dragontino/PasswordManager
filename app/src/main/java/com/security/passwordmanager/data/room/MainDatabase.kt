package com.security.passwordmanager.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.model.Website

@Database(
    entities = [Website::class, BankCard::class, Settings::class],
    version = 2,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun dataDao(): DataDao

    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        private val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL(
                "ALTER TABLE SettingsTable ADD COLUMN disablePullToRefresh INTEGER NOT NULL DEFAULT 0"
            )
        }

        fun getDatabase(context : Context) : MainDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        MainDatabase::class.java,
                        "PasswordBase",
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}