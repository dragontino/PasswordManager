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
        DataCursorWrapper cursorWrapper = queryPasswords(null, null);

        List<Data> dataList = new ArrayList<>();

        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            Data data = cursorWrapper.getData().decrypt(mCryptographer);
            if (!contains(dataList, data))
                addSortedValue(dataList, data);
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

    //добавляет новое значение в массив (массив уже отсортирован)
    private void addSortedValue(List<Data> list, Data value) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).compareTo(value) > 0) {
                list.add(i, value);
                return;
            }
        list.add(value);
    }

    private boolean contains(List<Data> dataList, Data data) {
        for (Data d : dataList) {
            if (data.getAddress().equals(d.getAddress()))
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
