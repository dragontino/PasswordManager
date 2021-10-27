package com.security.passwordmanager;

public class Settings {
    private String theme;

    public Settings() {
        this(Support.LIGHT_THEME);
    }

    public Settings(String theme) {
        setTheme(theme);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        if (Support.checkTheme(theme))
            theme = Support.LIGHT_THEME;
        this.theme = theme;
    }
}
