package com.aquino.texasandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.GameState;
import com.aquino.texasandroid.model.Move;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {

    private TexasRequestManager texasRequestManager;
    private long gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameId = getIntent().getLongExtra(GamesActivity.GAME_ID_EXTRA, -1);

        texasRequestManager = TexasRequestManager.getSetupInstance();

        //setupView();
        joinGame();

    }

    private void joinGame() {
        try {
            texasRequestManager.room("join",gameId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leaveGame() {
        try {
            texasRequestManager.room("leave",gameId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameState startGame() {
        try {
            return texasRequestManager.sendMove(new Move("START",0),gameId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private GameState sendPlay(int bet) {
        try {
            return texasRequestManager.sendMove(new Move("BET", bet ),gameId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private GameState ping() {
        try {
            return texasRequestManager.pingServer(gameId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
