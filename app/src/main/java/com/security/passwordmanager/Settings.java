package com.security.passwordmanager;

public class Settings {
    private String theme;

    public Settings() {
        this(Support.SYSTEM_THEME);
    }

    public Settings(String theme) {
        setTheme(theme);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(@Support.ThemeDef String theme) {
        if (!Support.checkTheme(theme))
            theme = Support.SYSTEM_THEME;
        this.theme = theme;
    }
}
