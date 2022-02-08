package com.security.passwordmanager.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringDef;
import androidx.appcompat.app.ActionBar;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.security.passwordmanager.Pair;
import com.security.passwordmanager.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Support {

    @StringDef({LIGHT_THEME, DARK_THEME, SYSTEM_THEME, AUTO_THEME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeDef {}

    public static final String LIGHT_THEME = "LIGHT";
    public static final String DARK_THEME = "DARK";
    public static final String SYSTEM_THEME = "SYSTEM";
    public static final String AUTO_THEME = "AUTO";

    private static final String APP_PREFERENCES = "my_settings";
    private static final String APP_PREFERENCES_START_HOURS = "start_hours";
    private static final String APP_PREFERENCES_START_MINUTES = "start_minutes";
    private static final String APP_PREFERENCES_END_HOURS = "end_hours";
    private static final String APP_PREFERENCES_END_MINUTES = "end_minutes";
    private static final String APP_PREFERENCES_IS_PASSWORD_REMEMBERED = "is_password_remembered";

    private static final String[] THEMES = new String[]
            {LIGHT_THEME, DARK_THEME, SYSTEM_THEME, AUTO_THEME};

    private static Calendar START_TIME, END_TIME;


    private final SettingsRepository mSettingsRepository;
    private final Context mContext;
    private final SharedPreferences mPreferences;

    private @ColorInt int backgroundColor, fontColor, headerColor, layoutBackgroundColor;
    private @DrawableRes int backgroundRes, buttonRes;

    private static Support sSupport;

    public static Support getInstance(Context context) {
        if (sSupport == null)
            sSupport = new Support(context);

        return sSupport;
    }

    private Support(Context context) {
        mContext = context.getApplicationContext();
        mSettingsRepository = new SettingsRepository(mContext);
        mPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        START_TIME = getDateFromPreferences(
                APP_PREFERENCES_START_HOURS, 7, APP_PREFERENCES_START_MINUTES);
        END_TIME = getDateFromPreferences(
                APP_PREFERENCES_END_HOURS, 23, APP_PREFERENCES_END_MINUTES);

        updateColors();
    }

    public @ThemeDef String getTheme() {
        return mSettingsRepository.getTheme();
    }

    public void setTheme(@ThemeDef String theme) {
        mSettingsRepository.updateTheme(theme);
        updateColors();
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
                return date.after(START_TIME.getTime()) && date.before(END_TIME.getTime()) ||
                        date.equals(START_TIME.getTime());
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



    private Calendar getDateFromPreferences(
            String key_hours, int defHours, String key_minutes) {

        int hours = mPreferences.getInt(key_hours, defHours);
        int minutes = mPreferences.getInt(key_minutes, 0);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        return calendar;
    }

    private void setDateToPreferences(String key_hours, String key_minutes, Calendar date) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key_hours, date.get(Calendar.HOUR_OF_DAY));
        editor.putInt(key_minutes, date.get(Calendar.MINUTE));
        editor.apply();
    }


    public void setStartTimeForAutoTheme(Calendar startTime) {
        START_TIME = startTime;
        setDateToPreferences(APP_PREFERENCES_START_HOURS, APP_PREFERENCES_START_MINUTES, startTime);
        updateColors();
    }

    public void setEndTimeForAutoTheme(Calendar endTime) {
        END_TIME = endTime;
        setDateToPreferences(APP_PREFERENCES_END_HOURS, APP_PREFERENCES_END_MINUTES, endTime);
        updateColors();
    }

    public Pair<Calendar, Calendar> getTimesForAutoTheme() {
        return new Pair<>(START_TIME, END_TIME);
    }


    public void setPasswordIsRemembered(boolean isRemembered) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED, isRemembered);
        editor.apply();
    }

    public boolean isPasswordRemembered() {
        return mPreferences.getBoolean(APP_PREFERENCES_IS_PASSWORD_REMEMBERED, false);
    }


    @Entity(tableName = "SettingsTable")
    public static class Settings {

        @PrimaryKey
        public int id;
        public String theme;

        @Ignore
        public Settings(@ThemeDef String theme) {
            this(1, theme);
        }

        public Settings(int id, @ThemeDef String theme) {
            setId(id);
            setTheme(theme);
        }

        @ThemeDef
        public String getTheme() {
            return theme;
        }

        public void setTheme(@ThemeDef String theme) {
            this.theme = theme;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}