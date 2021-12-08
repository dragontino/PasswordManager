package com.security.passwordmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.security.passwordmanager.databases.DataCursorWrapper;
import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema.DataTable;

import java.util.ArrayList;
import java.util.List;

public class DataLab {

    private static final String COPY_LABEL = "dataLab_copy_label";

    private final SQLiteDatabase mDatabase;
    private final Cryptographer mCryptographer;
    private final Context mContext;

    private static DataLab sDataLab;

    public static DataLab get(Context context) {
        if (sDataLab == null)
            sDataLab = new DataLab(context);

        return sDataLab;
    }

    private DataLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PasswordBaseHelper(mContext).getWritableDatabase();
        mCryptographer = new Cryptographer(mContext);
    }

    public void addData(Data data) {
        ContentValues values = getContentValues(data.encrypt(mCryptographer));

        mDatabase.insert(DataTable.NAME, null, values);
    }

    //Удаляет только 1 запись в бд
    public void deleteData(Data data) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.URL + " = ? and " + DataTable.Cols.LOGIN + " = ?",
                new String[]{ data.getAddress(), mCryptographer.encrypt(data.getLogin()) }
                );
    }

    //удаляет несколько записей в бд (по url)
    public void deleteData(String address) {
        mDatabase.delete(
                DataTable.NAME,
                DataTable.Cols.URL + " = ?",
                new String[]{address}
        );
    }


    public List<Data> getDataList() {
        DataCursorWrapper cursorWrapper = queryPasswords(null, null, DataTable.Cols.NAME_WEBSITE);

        List<Data> dataList = new ArrayList<>();

        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            Data data = cursorWrapper.getData().decrypt(mCryptographer);
            if (notContains(dataList, data))
                dataList.add(data);
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


    public List<Data> searchData(String query) {
        if (query == null || query.length() == 0)
            return getDataList();

        List<Data> searchList = new ArrayList<>();

        String search = getStringForSearch(DataTable.Cols.NAME_WEBSITE, query) + " OR " +
                getStringForSearch(DataTable.Cols.URL, query);

        DataCursorWrapper cursorWrapper = queryPasswords(
                search, null, DataTable.Cols.NAME_WEBSITE);

        boolean needCheck = false;

        if (cursorWrapper.getCount() == 0) {
            cursorWrapper = queryPasswords(null, null);
            needCheck = true;
        }

        checkAndAdd(cursorWrapper, query, searchList, needCheck);

        return searchList;
    }


    public void copyData(Data data) {
        String dataText = data.toString(mContext, true);
        copyText(dataText);
    }


    public void copyAccountList(List<Data> accountList) {
        StringBuilder builder = new StringBuilder(
                accountList.get(0).toString(mContext, true));

        for (int i = 1, accountListSize = accountList.size(); i < accountListSize; i++) {
            Data d = accountList.get(i);
            builder.append("\n").append(d.toString(mContext, false));
        }

        copyText(builder.toString());
    }

    public void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(COPY_LABEL, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, R.string.clipText, Toast.LENGTH_SHORT).show();
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

    private boolean notContains(List<Data> dataList, Data data) {
        for (Data d : dataList) {
            if (data.getAddress().equals(d.getAddress()))
                return false;
        }
        return true;
    }

    private String getStringForSearch(String columnName, String query) {
        return columnName + " LIKE" + "'%" + query + "%'";
    }

    private boolean compare(String first, String second) {
        return first.toLowerCase().contains(second.toLowerCase());
    }

    private void checkAndAdd(DataCursorWrapper cursorWrapper, String query, List<Data> list, boolean needCheck) {
        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            Data data = cursorWrapper.getData().decrypt(mCryptographer);

            if (needCheck && notContains(list, data) &&
                    (compare(data.getNameAccount(), query) || compare(data.getLogin(), query)) ||
            !needCheck && notContains(list, data))

                list.add(data);

            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();
    }


    private static ContentValues getContentValues(Data data) {
        ContentValues values = new ContentValues();
        values.put(DataTable.Cols.UUID, data.getId().toString());
        values.put(DataTable.Cols.URL, data.getAddress());
        values.put(DataTable.Cols.NAME_WEBSITE, data.getNameWebsite());
        values.put(DataTable.Cols.NAME_ACCOUNT, data.getNameAccount());
        values.put(DataTable.Cols.LOGIN, data.getLogin());
        values.put(DataTable.Cols.PASSWORD, data.getPassword());
        values.put(DataTable.Cols.COMMENT, data.getComment());

        return values;
    }


    private DataCursorWrapper queryPasswords(String whereClause, String[] whereArgs) {
        return queryPasswords(whereClause, whereArgs, null);
    }

    private DataCursorWrapper queryPasswords(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                DataTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                orderBy
        );

        return new DataCursorWrapper(cursor);
    }
}
