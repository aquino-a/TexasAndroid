package com.aquino.texasandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class Load extends AppCompatActivity {

    public final static String USER_PREFERENCES = "1220099223454";
    private SharedPreferences mSharedPreferences;
    private TexasLoginManager texasLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        if(texasLoginManager.validToken())
            startMainPage();

        texasLoginManager.startLoginPage();
        finish();


    }

    private void startMainPage() {
            Intent intent = new Intent(this, );
            startActivity(intent);
            finish();

    }
}
