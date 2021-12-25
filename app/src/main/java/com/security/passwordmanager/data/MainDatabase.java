package com.security.passwordmanager.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.security.passwordmanager.settings.SettingsDao;
import com.security.passwordmanager.settings.Support;

import kotlin.jvm.Volatile;

@Database(entities = {Website.class, BankCard.class, Support.Settings.class}, version = 2, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    @Volatile
    private static MainDatabase INSTANCE = null;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE SettingsTable (id INTEGER PRIMARY KEY NOT NULL, theme TEXT)");
        }
    };

    public static MainDatabase getDatabase(Context context) {
        MainDatabase temp = INSTANCE;

        if (temp != null)
            return temp;

        synchronized (MainDatabase.class) {
            MainDatabase instance = Room
                    .databaseBuilder(context, MainDatabase.class, "PasswordBase")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();

            INSTANCE = instance;
            return instance;
        }
    }

    public abstract DataDao websiteDao();

    public abstract SettingsDao settingsDao();
}
