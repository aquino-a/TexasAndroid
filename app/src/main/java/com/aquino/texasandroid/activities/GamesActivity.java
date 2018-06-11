package com.aquino.texasandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.GameInfo;
import com.aquino.texasandroid.model.GameList;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class GamesActivity extends AppCompatActivity {

    private LinearLayout mListLayout;
    private Button mNewGame;
    private TextView mActiveGames;
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

    private void addButtons(GameList gameList) {
        mActiveGames.setText(String.format("Active Games: %d", gameList.getSize()));
        LayoutParams lparams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for(GameInfo info : gameList.getList()) {
            TextView game = new TextView(this);
            game.setLayoutParams(lparams);
            game.setText(
                    String.format("Game ID: %d Title: %s", info.getGameId(),info.getTitle()));
            game.setOnClickListener(new OpenGameClickListener(info.getGameId()));
            mListLayout.addView(game);
        }

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
        mActiveGames = findViewById(R.id.active_games_text);

    }

    private void joinGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
        startActivity(intent);
    }

    private class OpenGameClickListener implements View.OnClickListener {

        private long gameId;

        OpenGameClickListener(long gameId) {
            this.gameId = gameId;
        }

        @Override
        public void onClick(View v) {
            joinGame(gameId);

        }
    }
}
