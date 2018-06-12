package com.aquino.texasandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasAssetManager;
import com.aquino.texasandroid.TexasLoginManager;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.User;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    public static final int LOGIN_REQUEST_CODE = 0;

    private Button mProfile, mGames;
    private TextView mUser;
    private TexasLoginManager texasLoginManager;
    private TexasRequestManager texasRequestManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TexasAssetManager.makeInstance(this);
        texasLoginManager = TexasLoginManager.getInstance(this);
        setupView(this);
        checkAuthorization();



    }
    @Override
    public void onStart() {
        super.onStart();
        checkAuthorization();
    }

    private void checkAuthorization() {
        if(texasLoginManager.validToken()) {
            texasRequestManager = TexasRequestManager.getInstance(texasLoginManager.getToken());
            setupUser();
        } else startLoginPage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == LOGIN_REQUEST_CODE) {
            if(resultCode != RESULT_OK)
                startLoginPage();
            else if (intent.getBooleanExtra(LoginActivity.SUCCESS_EXTRA,false)) {
                setupUser();
            }
        }
    }


    private void setupView(final Context packageContext) {
        mProfile = findViewById(R.id.profile_button);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(packageContext, ProfileActivity.class);
//                startActivity(intent);
            }
        });
        mGames = findViewById(R.id.games_button);
        mGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(packageContext, GamesActivity.class);
                startActivity(intent);
            }
        });

        mUser = findViewById(R.id.user_text);

    }
    private void setupUser() {
        try {
            User user = texasRequestManager.getUser();
            mUser.setText(String.format("Welcome, %s!",user.getUsername()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }



}
