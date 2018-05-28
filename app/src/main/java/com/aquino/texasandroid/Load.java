package com.aquino.texasandroid;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Load extends AppCompatActivity {

    public final static String USER_PREFERENCES = "1220099223454";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        mSharedPreferences = getSharedPreferences(USER_PREFERENCES,MODE_PRIVATE);
        if(mSharedPreferences == null)
            startLoginPage();

    }

    private void startLoginPage() {

    }
}
