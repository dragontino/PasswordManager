package com.security.passwordmanager.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.security.passwordmanager.Data;
import com.security.passwordmanager.databases.PasswordDBSchema.DataTable.Cols;

import java.util.UUID;

public class DataCursorWrapper extends CursorWrapper {

    public DataCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Data getData() {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String url = getString(getColumnIndex(Cols.URL));
        String name = getString(getColumnIndex(Cols.NAME));
        String login = getString(getColumnIndex(Cols.LOGIN));
        String password = getString(getColumnIndex(Cols.PASSWORD));
        String comment = getString(getColumnIndex(Cols.COMMENT));

        Data data = new Data(UUID.fromString(uuidString));
        data.setAddress(url);
        data.setName(name);
        data.setLogin(login);
        data.setPassword(password);
        data.setComment(comment);

        return data;
    }
}
