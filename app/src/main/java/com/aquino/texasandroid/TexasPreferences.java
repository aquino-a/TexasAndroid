package com.aquino.texasandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.logging.Logger;

public class TexasPreferences {

    private final static String USER_PREFERENCES = "1220099223454";
    private volatile SharedPreferences mSharedPreferences;


    private static TexasPreferences preferences;

    public static TexasPreferences getInstance(Context packageContext) {
        if(preferences == null)
            preferences = new TexasPreferences(packageContext);
        return preferences;
    }
    private TexasPreferences(Context packageContext) {
        mSharedPreferences = packageContext.getSharedPreferences(USER_PREFERENCES,packageContext.MODE_PRIVATE);
    }
    private boolean checkPreferences() {
        return mSharedPreferences == null;

    }

     public void saveToken(String token) {
        Log.d(getClass().getName(),token);
        if(checkPreferences())
            Log.i(this.getClass().getName(),"Preferences were null.");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token",token);
        editor.apply();
        Log.i(this.getClass().getName(),"Added new token");
    }

    public String loadToken() throws NullPointerException {
        String result =  mSharedPreferences.getString("token",null);
        if(result == null)
            throw new NullPointerException("There is no token");
        Log.i(this.getClass().getName(), "Successfully loaded token");
        return result;
    }

//

}
