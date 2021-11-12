package com.security.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;

import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema.SupportTable;
import com.security.passwordmanager.databases.SupportCursorWrapper;

public class Support {

    public static final String LIGHT_THEME = "LIGHT";
    public static final String DARK_THEME = "DARK";

    private final SQLiteDatabase mDatabase;
    private final Context mContext;
    private Settings mSettings;
    private int backgroundColor, fontColor, headerColor, layoutBackgroundColor;
    private @DrawableRes int backgroundRes, buttonRes;

    private static Support sSupport;

    public static boolean checkTheme(String theme) {
        return !theme.equals(LIGHT_THEME) && !theme.equals(DARK_THEME);
    }

    public static Support get(Context context) {
        if (sSupport == null)
            sSupport = new Support(context);

        return sSupport;
    }

    private Support(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PasswordBaseHelper(context).getWritableDatabase();
        mSettings = new Settings();

        SupportCursorWrapper cursor = querySupports(null, null);
        if (cursor.getCount() == 0)
            addSettings(mSettings);
        else {
            cursor.moveToFirst();
            mSettings = cursor.getSettings();
            cursor.close();
        }

        updateColors();
    }

    public String getTheme() {
        return mSettings.getTheme();
    }

    public void setTheme(String theme) {
        mSettings.setTheme(theme);
        updateColors();
        updateSettings(mSettings);
    }

    public boolean isLightTheme() {
        return getTheme().equals(LIGHT_THEME);
    }

    public void updateThemeInScreen(Window window, ActionBar actionBar) {
        window.getDecorView().setBackgroundColor(backgroundColor);
        window.setStatusBarColor(headerColor);
        actionBar.setBackgroundDrawable(new ColorDrawable(headerColor));
    }

    private void updateColors() {
        if (isLightTheme()) {
            backgroundColor = Color.WHITE;
            fontColor = Color.BLACK;
            headerColor = mContext.getColor(R.color.raspberry);
            layoutBackgroundColor = mContext.getColor(R.color.light_gray);
            backgroundRes = R.drawable.text_view_style;
            buttonRes = R.drawable.button_style;
        }
        else {
            backgroundColor = mContext.getColor(R.color.background_dark);
            fontColor = Color.WHITE;
            headerColor = mContext.getColor(R.color.header_dark);
            layoutBackgroundColor = mContext.getColor(R.color.gray);
            backgroundRes = R.drawable.text_view_dark_style;
            buttonRes = R.drawable.button_style_dark;
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public int getLayoutBackgroundColor() {
        return layoutBackgroundColor;
    }

    public @DrawableRes int getBackgroundRes() {
        return backgroundRes;
    }

    public @DrawableRes int getButtonRes() {
        return buttonRes;
    }

    public int getDarkerGrayColor() {
        return mContext.getColor(android.R.color.darker_gray);
    }




    private void addSettings(Settings settings) {
        ContentValues contentValues = getContentValues(settings);
        mDatabase.insert(SupportTable.NAME, null, contentValues);
    }

    private void updateSettings(Settings settings) {
        ContentValues contentValues = getContentValues(settings);
        mDatabase.update(
                SupportTable.NAME,
                contentValues,
                SupportTable.Cols.ID + " = ?",
                new String[]{"1"}
                );
    }

    private static ContentValues getContentValues(Settings settings) {
        ContentValues values = new ContentValues();
        values.put(SupportTable.Cols.THEME, settings.getTheme());
        return values;
    }

    private SupportCursorWrapper querySupports(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                SupportTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new SupportCursorWrapper(cursor);
    }
}
