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

        texasLoginManager.createValidToken();


    }

    private void startMainPage() {
            Intent intent = new Intent(this, );
            startActivity(intent);
            finish();
        }
    }
/*
    private void checkToken() throws IOException {
        String path = getResources().getString(R.string.token_check_path);

        URI uri = new Uri.Builder()



        HttpURLConnection con = (HttpURLConnection) url.openConnection();



    }

    private void startLoginPage() {
        Intent intent = new Intent(this ,Login.class);
        startActivity(intent);

    } */
}
