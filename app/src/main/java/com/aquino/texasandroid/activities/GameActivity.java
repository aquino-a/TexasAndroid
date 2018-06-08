package com.aquino.texasandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.model.GameState;
import com.aquino.texasandroid.model.Move;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {


    private LinearLayout mUserListContainer;
    private Button mFold, mBet;
    private TextView mTitle;
    private ImageView mCardOne, mCardTwo;

    private TexasRequestManager texasRequestManager;
    private long gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameId = getIntent().getLongExtra(GamesActivity.GAME_ID_EXTRA, -1);

        if(gameId == -1)
            finish();

        texasRequestManager = TexasRequestManager.getSetupInstance();

        setupView();
        joinGame();

    }

    private void setupView() {
        mUserListContainer = findViewById(R.id.user_list_layout);
        mFold = findViewById(R.id.fold_button);
        mBet = findViewById(R.id.bet_button);
        mTitle = findViewById(R.id.game_title_text);
        mCardOne = findViewById(R.id.card_one_image);
        mCardTwo = findViewById(R.id.card_two_image);

        mFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO contents for click action
            }
        });

        mBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO contents
            }
        });
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
            return null;
        }
    }


}
