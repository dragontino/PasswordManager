package com.security.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.security.passwordmanager.databases.DataCursorWrapper;
import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema;
import com.security.passwordmanager.databases.PasswordDBSchema.DataTable;

import java.util.ArrayList;
import java.util.List;
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

    public void addData(Data data) {
        ContentValues values = getContentValues(data);

        mDatabase.insert(DataTable.NAME, null, values);
    }

    public void deleteData(UUID id) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
                );
    }

    public void deleteData(String address, String login) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.URL + " = ? and " + DataTable.Cols.LOGIN + " = ?",
                new String[]{address, login}
        );
    }


    public List<Data> getDataList() {
        DataCursorWrapper cursorWrapper = queryPasswords(null, null);

        List<Data> dataList = new ArrayList<>();

        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            dataList.add(cursorWrapper.getData());
            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();

        return dataList;
    }

    public List<Data> getAccountList(String url) {

        List<Data> accountList = new ArrayList<>();

        DataCursorWrapper cursorWrapper =
                queryPasswords(DataTable.Cols.URL + " = ?", new String[]{url});

        cursorWrapper.moveToFirst();

        while (!cursorWrapper.isAfterLast()) {
            accountList.add(cursorWrapper.getData());
            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();

        return accountList;
    }


    public Data getData(UUID id) {
        DataCursorWrapper cursorWrapper = queryPasswords(
                DataTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        if (cursorWrapper.getCount() == 0)
            return new Data();

        cursorWrapper.moveToFirst();
        Data data = cursorWrapper.getData();
        cursorWrapper.close();

        return data;
    }


    public void updateData(Data data) {
        String uuidString = data.getId().toString();

        if (queryPasswords(
                DataTable.Cols.UUID + " = ?",
                new String[]{uuidString})
                .getCount() == 0) {
            addData(data);
            return;
        }

        ContentValues values = getContentValues(data);

        mDatabase.update(
                DataTable.NAME,
                values,
                DataTable.Cols.UUID + " = ?",
                new String[]{uuidString}
                );
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
