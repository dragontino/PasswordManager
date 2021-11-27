package com.security.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringDef;
import androidx.appcompat.app.ActionBar;

import com.google.android.gms.common.util.ArrayUtils;
import com.security.passwordmanager.databases.PasswordBaseHelper;
import com.security.passwordmanager.databases.PasswordDBSchema.SupportTable;
import com.security.passwordmanager.databases.SupportCursorWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Support {

    @StringDef({LIGHT_THEME, DARK_THEME, SYSTEM_THEME, AUTO_THEME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeDef {}

    public static final String LIGHT_THEME = "LIGHT";
    public static final String DARK_THEME = "DARK";
    public static final String SYSTEM_THEME = "SYSTEM";
    public static final String AUTO_THEME = "AUTO";

    private static final String[] THEMES = new String[]
            {LIGHT_THEME, DARK_THEME, SYSTEM_THEME, AUTO_THEME};

    private final SQLiteDatabase mDatabase;
    private final Context mContext;
    private Settings mSettings;
    private @ColorInt int backgroundColor, fontColor, headerColor, layoutBackgroundColor;
    private @DrawableRes int backgroundRes, buttonRes;

    private static Support sSupport;

    public static boolean checkTheme(String theme) {
        return ArrayUtils.contains(THEMES, theme);
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

        SupportCursorWrapper cursor = querySupports();
        if (cursor.getCount() == 0)
            addSettings(mSettings);
        else {
            cursor.moveToFirst();
            mSettings = cursor.getSettings();
            cursor.close();
        }
        updateColors();
    }

    public @ThemeDef String getTheme() {
        return mSettings.getTheme();
    }

    public void setTheme(@ThemeDef String theme) {
        mSettings.setTheme(theme);
        updateColors();
        updateSettings(mSettings);
    }

    public int getIndexTheme() {
        return Arrays.asList(THEMES).indexOf(getTheme());
    }

    public boolean isLightTheme() {
        String theme = getTheme();
        switch (theme) {
            case DARK_THEME:
                return false;
            case SYSTEM_THEME:
                int currentNightMode = mContext.getResources().getConfiguration()
                        .uiMode & Configuration.UI_MODE_NIGHT_MASK;

                return currentNightMode == Configuration.UI_MODE_NIGHT_NO;
            case AUTO_THEME:
                Date date = new Date(System.currentTimeMillis());
                DateFormat format = new SimpleDateFormat("HH", Locale.getDefault());
                int hours = Integer.parseInt(format.format(date));
                return hours >= 7 && hours <= 22;
            default:
                return true;
        }
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

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @ColorInt
    public int getFontColor() {
        return fontColor;
    }

    @ColorInt
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

    private SupportCursorWrapper querySupports() {
        Cursor cursor = mDatabase.query(
                SupportTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new SupportCursorWrapper(cursor);
    }
}
