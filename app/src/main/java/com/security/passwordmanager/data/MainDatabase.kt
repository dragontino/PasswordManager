package com.security.passwordmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.security.passwordmanager.settings.Settings
import com.security.passwordmanager.settings.SettingsDao

@Database(entities = [Website::class, BankCard::class, Settings::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {

    abstract fun dataDao() : DataDao

    abstract fun settingsDao() : SettingsDao

    companion object {
        @Volatile
        private var INSTANCE : MainDatabase? = null

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
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}