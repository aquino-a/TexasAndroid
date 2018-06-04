package com.aquino.texasandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasLoginManager;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.User;

public class MainActivity extends AppCompatActivity {


    private Button mProfile, mGames;
    private TextView mUser;
    private TexasLoginManager texasLoginManager;
    private TexasRequestManager texasRequestManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!texasLoginManager.validToken())
            texasLoginManager.startLoginPage(this);

        texasLoginManager = TexasLoginManager.getInstance(this);
        texasRequestManager = TexasRequestManager.getInstance(texasLoginManager.getToken(),this);

        setupView();
        setupUser();




    }


    private void setupView() {
        mProfile = findViewById(R.id.profile_button);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        mGames = findViewById(R.id.games_button);
        mGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(this, GamesActivity,class);
                startActivity(intent);
            }
        });

        mUser = findViewById(R.id.user_text);

    }
    private void setupUser() {
        User user = texasRequestManager.getUser();



    }

}
