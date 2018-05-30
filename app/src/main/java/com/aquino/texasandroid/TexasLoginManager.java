package com.aquino.texasandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.URLUtil;

import java.net.URI;
import java.net.URL;

public class TexasLoginManager {

    private static TexasLoginManager texasLoginManager;

    public final static String USER_PREFERENCES = "1220099223454";
    private SharedPreferences mSharedPreferences;
    private Context packageContext;

    private TexasLoginManager(){


    }

    private boolean checkPreferences() {
        mSharedPreferences = packageContext.getSharedPreferences(USER_PREFERENCES,packageContext.MODE_PRIVATE);
        return mSharedPreferences == null;

    }

    private void startLoginPage() {
    }

    public static TexasLoginManager getInstance(Context packageContext) {
        if(texasLoginManager == null)
            texasLoginManager = new TexasLoginManager();
        texasLoginManager.setPackageContext(packageContext);
        return texasLoginManager;
    }

    private Context getPackageContext() {
        return packageContext;
    }

    private void setPackageContext(Context packageContext) {
        this.packageContext = packageContext;
    }

    public boolean validToken() {
        if(!checkPreferences())
            return false;
        String token = mSharedPreferences.getString("token","none");
        if(token == null)
            return false;
        return checkToken(token);


    }

    public void createValidToken(String username, String password) {
        String path = packageContext.getResources().getString(R.string.token_check_path);
        String host = packageContext.getResources().getString(R.string.service_host);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        Uri.Builder().scheme("http").           


    }
}
