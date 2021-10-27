package com.security.passwordmanager.databases;

public class PasswordDBSchema {

    public static final class SupportTable {
        public static final String NAME = "SupportTable";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String THEME = "Theme";
        }
    }

    public static final class DataTable {
        public static final String NAME = "Data";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String LOGIN = "Login";
            public static final String PASSWORD = "Password";
        }
    }

}
