package com.security.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.security.passwordmanager.databases.DataCursorWrapper;
import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema.DataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataLab {

    private final SQLiteDatabase mDatabase;
    private final Cryptographer mCryptographer;

    private static DataLab sDataLab;

    public static DataLab get(Context context) {
        if (sDataLab == null)
            sDataLab = new DataLab(context);

        return sDataLab;
    }

    private DataLab(Context context) {
        Context mContext = context.getApplicationContext();
        mDatabase = new PasswordBaseHelper(mContext).getWritableDatabase();
        mCryptographer = new Cryptographer(mContext);
    }

    public void addData(Data data) {
        ContentValues values = getContentValues(data.encrypt(mCryptographer));

        mDatabase.insert(DataTable.NAME, null, values);
    }

    public void deleteData(Data data) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.URL + " = ?",
                new String[]{data.getAddress()}
                );
    }

    public void deleteData(String address, String login) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.URL + " = ? and " + DataTable.Cols.LOGIN + " = ?",
                new String[]{address, mCryptographer.decrypt(login)}
        );
    }


    public List<Data> getDataList() {
        DataCursorWrapper cursorWrapper = queryPasswords(null, null);

        List<Data> dataList = new ArrayList<>();

        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            Data data = cursorWrapper.getData().decrypt(mCryptographer);
            if (!contains(dataList, data))
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
            accountList.add(cursorWrapper.getData().decrypt(mCryptographer));
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
        Data data = cursorWrapper.getData().decrypt(mCryptographer);
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

        ContentValues values = getContentValues(data.encrypt(mCryptographer));

        mDatabase.update(
                DataTable.NAME,
                values,
                DataTable.Cols.UUID + " = ?",
                new String[]{uuidString}
                );
    }


    private boolean contains(List<Data> dataList, Data data) {
        for (Data d : dataList) {
            if (data.equals(d))
                return true;
        }
        return false;
    }



    private static ContentValues getContentValues(Data data) {
        ContentValues values = new ContentValues();
        values.put(DataTable.Cols.UUID, data.getId().toString());
        values.put(DataTable.Cols.URL, data.getAddress());
        values.put(DataTable.Cols.NAME_ADDRESS, data.getNameWebsite());
        values.put(DataTable.Cols.NAME_ACCOUNT, data.getNameAccount());
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