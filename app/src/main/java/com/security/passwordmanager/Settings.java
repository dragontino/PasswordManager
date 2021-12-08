package com.security.passwordmanager;

public class Settings {
    private String theme;

    public Settings() {
        this(Support.SYSTEM_THEME);
    }

    public Settings(String theme) {
        setTheme(theme);
    }

    public @Support.ThemeDef
    String getTheme() {
        return theme;
    }

    public void setTheme(@Support.ThemeDef String theme) {
        this.theme = theme;
    }
}
