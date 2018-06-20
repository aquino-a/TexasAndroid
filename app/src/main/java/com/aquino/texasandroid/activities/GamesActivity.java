package com.aquino.texasandroid.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.fragments.GameListFragment;
import com.aquino.texasandroid.model.GameInfo;
import com.aquino.texasandroid.model.GameList;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class GamesActivity extends AppCompatActivity {

    //private LinearLayout mListLayout;
    private Button mNewGame, mRefresh;
    private TextView mActiveGames;
    private TexasRequestManager texasRequestManager;
    private Fragment mFragment;
    private List<GameInfo> mGameInfoList;

    public static final String GAME_ID_EXTRA =
            "com.aquino.texasandroid.activities.GamesActivity.game_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        texasRequestManager = TexasRequestManager.getSetupInstance();

        if(texasRequestManager == null) {}

        setupView(this);
        setupFragment();

        //populateList();

    }


    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if(mFragment == null) {
            mFragment = new GameListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,mFragment)
                    .commit();
        }
    }

    private void setList(Fragment fragment) {
        GameListFragment glf = (GameListFragment) fragment;
        GameListFragment.GameAdapter ga = glf.getGameAdapter();
        ga.setGameInfoList(mGameInfoList);
        ga.notifyDataSetChanged();
    }

    private void setupList() {
        try {
            mGameInfoList = texasRequestManager.getGameList().getList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setupView(GamesActivity gamesActivity) {
        mActiveGames = findViewById(R.id.active_games_text);
        mNewGame = findViewById(R.id.new_game_button);
        mRefresh = findViewById(R.id.show_games_button);

        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                     long gameId = texasRequestManager.createNewGame();
                     refreshList();
                    //joinGame(gameId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mRefresh.setOnClickListener(v -> {
            refreshList();
        });
        //mListLayout = findViewById(R.id.games_list);


    }

    private void refreshList() {
        setupList();
        setList(mFragment);
    }

    private void joinGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GAME_ID_EXTRA, gameId);
        startActivity(intent);
    }

//    private class OpenGameClickListener implements View.OnClickListener {
//
//        private long gameId;
//
//        OpenGameClickListener(long gameId) {
//            this.gameId = gameId;
//        }
//
//        @Override
//        public void onClick(View v) {
//            joinGame(gameId);
//
//        }
//    }
}
