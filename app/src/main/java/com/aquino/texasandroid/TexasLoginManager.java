package com.aquino.texasandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class TexasLoginManager {

    private static TexasLoginManager texasLoginManager;

    public final static String USER_PREFERENCES = "1220099223454";
    private SharedPreferences mSharedPreferences;
    private Context packageContext;

    private TexasLoginManager(){
        checkPreferences();

    }

    private void checkPreferences() {
        mSharedPreferences = mSharedPreferences = packageContext.getSharedPreferences(USER_PREFERENCES,packageContext.MODE_PRIVATE);
        if(mSharedPreferences == null)
            startLoginPage();
    }

    private void startLoginPage() {
    }

    public static TexasLoginManager getInstance(Context packageContext) {
        if(texasLoginManager == null)
            texasLoginManager = new TexasLoginManager();
        texasLoginManager.setPackageContext(packageContext);
        return texasLoginManager;
    }

    public String getToken() {

        return "";
    }

    private Context getPackageContext() {
        return packageContext;
    }

    private void setPackageContext(Context packageContext) {
        this.packageContext = packageContext;
    }
}
