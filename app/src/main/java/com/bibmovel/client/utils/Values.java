package com.bibmovel.client.utils;

import android.os.Environment;

import com.bibmovel.client.BuildConfig;

/**
 * Created by vinibrenobr11 on 11/09/2018 at 10:26
 */
public abstract class Values {

    public abstract static class Preferences {
        public static final String PREFS_LOGIN = "LoginData";
        public static final String IS_LOGEED_VALUE_NAME = "IsLoggedIn";
        public static final String USER_LOGIN_VALUE_NAME = "UserLogin";
        public static final String USER_EMAIL_VALUE_NAME = "UserEmail";
    }

    public abstract static class Codes {
        public static final int PICKFILE_REQUEST_CODE = 5;
        public static final int RC_G_SIGN_IN = 10;
    }

    public abstract static class Notification {
        public static final String CHANNEL_DOWNLOAD_ID = BuildConfig.APPLICATION_ID + ".DOWNLOAD";
        public static final String CHANNEL_DOWNLOAD_NAME = "Downloads";
    }

    public abstract static class Path {
        public static final String DOWNLOAD_BOOKS = Environment.DIRECTORY_DOWNLOADS + "/BibMovel/";
        public static final String UPDATES_PATH = "Updates/";
        public static final String BOOKS_PATH = "Books/";
    }
}
