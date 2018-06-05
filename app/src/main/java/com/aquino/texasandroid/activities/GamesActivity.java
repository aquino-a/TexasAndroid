package com.aquino.texasandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class GamesActivity extends AppCompatActivity {

    private LinearLayout mListLayout;
    private Button mNewGame;
    private TexasRequestManager texasRequestManager;

    public static final String GAME_ID_EXTRA =
            "com.aquino.texasandroid.activities.GamesActivity.game_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        texasRequestManager = TexasRequestManager.getSetupInstance();

        //TODO do something if no requestmanager, but maybe redundant
        if(texasRequestManager == null) {}

        setupView(this);
        populateList();






    }

    private void populateList() {
        //send request
        try {
            addButtons(texasRequestManager.getGameList());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addButtons(JSONArray gameList) {
        //add buttons
    }

    private void setupView(GamesActivity gamesActivity) {
        mNewGame = findViewById(R.id.games_button);
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                     long gameId = texasRequestManager.createNewGame();
                    joinGame(gameId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mListLayout = findViewById(R.id.games_list);

    }

    private void joinGame(long gameId) {
        //TODO join game
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
        startActivity(intent);
    }
}
