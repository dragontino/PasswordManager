package com.security.passwordmanager.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.security.passwordmanager.Settings;
import com.security.passwordmanager.databases.PasswordDBSchema.SupportTable.Cols;

public class SupportCursorWrapper extends CursorWrapper {
    public SupportCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Settings getSettings() {
        String theme = getString(getColumnIndex(Cols.THEME));
        return new Settings(theme);
    }
}
