package com.bibmovel.client.utils;

/**
 * Created by vinibrenobr11 on 11/09/2018 at 10:26
 */
public abstract class Values {

    private static final String PREFS_LOGIN = "LoginData";
    private static final String IS_LOGEED_VALUE_NAME = "IsLoggedIn";

    private static final String GENERAL_PREFS = "GeneralPrefs";
    private static final String FIRST_TIME_VALUE_NAME = "IsFirstTime";
    private static final String USER_LOGIN_VALUE_NAME = "UserLogin";

    private static final int PICKFILE_REQUEST_CODE = 5;
    private static final int RC_G_SIGN_IN = 10;

    public static String getPrefsLogin() {
        return PREFS_LOGIN;
    }

    public static String getIsLogeedValueName() {
        return IS_LOGEED_VALUE_NAME;
    }

    public static String getGeneralPrefs() {
        return GENERAL_PREFS;
    }

    public static String getFirstTimeValueName() {
        return FIRST_TIME_VALUE_NAME;
    }

    public static int getPickfileRequestCode() {
        return PICKFILE_REQUEST_CODE;
    }

    public static String getUserLoginValueName() {
        return USER_LOGIN_VALUE_NAME;
    }

    public static int getRcGSignIn() {
        return RC_G_SIGN_IN;
    }
}
