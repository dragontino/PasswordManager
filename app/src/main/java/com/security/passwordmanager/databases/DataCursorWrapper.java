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

    //зашифрованные данные
    public Data getData() {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String url = getString(getColumnIndex(Cols.URL));
        String nameAddress = getString(getColumnIndex(Cols.NAME_ADDRESS));
        String nameAccount = getString(getColumnIndex(Cols.NAME_ACCOUNT));
        String login = getString(getColumnIndex(Cols.LOGIN));
        String password = getString(getColumnIndex(Cols.PASSWORD));
        String comment = getString(getColumnIndex(Cols.COMMENT));

        return new Data(UUID.fromString(uuidString))
                .setAddress(url)
                .setNameWebsite(nameAddress)
                .setNameAccount(nameAccount)
                .setLogin(login)
                .setPassword(password)
                .setComment(comment);
    }
}
