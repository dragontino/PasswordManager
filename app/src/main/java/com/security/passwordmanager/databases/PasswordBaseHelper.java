package com.security.passwordmanager.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.security.passwordmanager.databases.PasswordDBSchema.DataTable;
import com.security.passwordmanager.databases.PasswordDBSchema.SupportTable;

public class PasswordBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "passwordBase.db";

    public PasswordBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SupportTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                SupportTable.Cols.THEME + " text)"
        );

        db.execSQL("create table " + DataTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                DataTable.Cols.UUID + " integer, " +
                DataTable.Cols.URL + " text, " +
                DataTable.Cols.NAME_ADDRESS + " text, " +
                DataTable.Cols.NAME_ACCOUNT + " text, " +
                DataTable.Cols.LOGIN + " text, " +
                DataTable.Cols.PASSWORD + " text, " +
                DataTable.Cols.COMMENT + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
