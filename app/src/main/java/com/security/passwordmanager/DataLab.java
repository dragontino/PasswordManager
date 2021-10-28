package com.security.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.security.passwordmanager.databases.DataCursorWrapper;
import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema;
import com.security.passwordmanager.databases.PasswordDBSchema.DataTable;

import java.util.UUID;

public class DataLab {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static DataLab sDataLab;

    public static DataLab get(Context context) {
        if (sDataLab == null)
            sDataLab = new DataLab(context);

        return sDataLab;
    }

    private DataLab(Context context) {
        this.mContext = context.getApplicationContext();
        mDatabase = new PasswordBaseHelper(mContext).getWritableDatabase();
    }


    public Data getData(UUID id) {
        DataCursorWrapper cursorWrapper = queryPasswords(
                DataTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        if (cursorWrapper.getCount() == 0)
            return null;

        cursorWrapper.moveToFirst();
        Data data = cursorWrapper.getData();
        cursorWrapper.close();

        return data;
    }



    private static ContentValues getContentValues(Data data) {
        ContentValues values = new ContentValues();
        values.put(DataTable.Cols.UUID, data.getId().toString());
        values.put(DataTable.Cols.URL, data.getAddress());
        values.put(DataTable.Cols.NAME, data.getName());
        values.put(DataTable.Cols.LOGIN, data.getLogin());
        values.put(DataTable.Cols.PASSWORD, data.getPassword());
        values.put(DataTable.Cols.COMMENT, data.getComment());

        return values;
    }


    private DataCursorWrapper queryPasswords(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DataTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DataCursorWrapper(cursor);
    }
}
