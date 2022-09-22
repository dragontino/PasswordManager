package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.security.passwordmanager.model.BankCard
import com.security.passwordmanager.model.Settings
import com.security.passwordmanager.model.Website
import com.security.passwordmanager.settings.SettingsDao

@Database(entities = [Website::class, BankCard::class, Settings::class], version = 4, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {

    abstract fun dataDao() : DataDao

    abstract fun settingsDao() : SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        private val MIGRATION_1_2 = Migration(1, 2) {
            for (property in arrayOf("isUsingBeautifulFont", "isShowingDataHints", "isUsingBottomView"))
                it.execSQL(
                    "ALTER TABLE SettingsTable ADD COLUMN $property INTEGER DEFAULT 0 NOT NULL")
        }

        private val MIGRATION_2_3 = Migration(2, 3) {
            it.execSQL("ALTER TABLE SettingsTable ADD COLUMN email TEXT NOT NULL DEFAULT ''")
            it.execSQL("ALTER TABLE WebsiteTable ADD COLUMN email TEXT NOT NULL DEFAULT ''")
            it.execSQL("ALTER TABLE BankTable ADD COLUMN email TEXT NOT NULL DEFAULT ''")
        }

        private val MIGRATION_3_4 = Migration(3, 4) {
            it.execSQL("ALTER TABLE BankTable ADD COLUMN bankCardName TEXT NOT NULL DEFAULT ''")
            it.execSQL("ALTER TABLE BankTable ADD COLUMN comment TEXT NOT NULL DEFAULT ''")
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}