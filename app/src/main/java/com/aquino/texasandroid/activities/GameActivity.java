package com.aquino.texasandroid.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.TexasAssetManager;
import com.aquino.texasandroid.TexasRequestManager;
import com.aquino.texasandroid.fragments.BetFragment;
import com.aquino.texasandroid.model.GameState;
import com.aquino.texasandroid.model.Move;
import com.aquino.texasandroid.model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity
        implements BetFragment.BetListener {


    private LinearLayout mUserListContainer;
    private Button mFold, mBet,mCall;
    private TextView mGameInfo, mTitle, mPot, mCallAmount,mMinimumBet,mGameState;
    private ImageView mCardOne, mCardTwo;
    private Timer timer;
    private RefreshPage refresher;
    private Runnable refresh;
    private GameState lastState;

    private TexasRequestManager texasRequestManager;
    private long gameId;

    private static final List<String> FACE =Collections.unmodifiableList(
            Arrays.asList("","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT",
                    "NINE","TEN","JACK","QUEEN","KING","ACE"));

    private static final List<String> SUIT = Collections.unmodifiableList(
            Arrays.asList("","CLUB","DIAMOND","HEART","SPADE"));

    private static final int BET_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupView();

        if(savedInstanceState != null) {
            gameId = savedInstanceState.getLong("game_id");
        } else {
            gameId = getIntent().getLongExtra(GamesActivity.GAME_ID_EXTRA, -1);
            if(gameId == -1)
                finish();
            joinGame();
        }

        setupRefresh();
    }

    private void setupRefresh() {
        timer = new Timer();
        refresh = () -> {
            refresher = new RefreshPage();
            refresher.execute();
        };
        startTimer();
    }

    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runPingInterval();
            }
        },0,5000);
    }

    private void runPingInterval() {
        this.runOnUiThread(refresh);
    }

    private void setupView() {
        texasRequestManager = TexasRequestManager.getSetupInstance();

        mUserListContainer = findViewById(R.id.user_list_layout);
        mFold = findViewById(R.id.fold_button);
        mBet = findViewById(R.id.bet_button);
        mCall = findViewById(R.id.call_button);
        mTitle = findViewById(R.id.game_title_text);
        mCardOne = findViewById(R.id.card_one_image);
        mCardTwo = findViewById(R.id.card_two_image);
        mTitle = findViewById(R.id.game_title_text);

        mGameInfo = findViewById(R.id.game_info);

//        mPot = findViewById(R.id.total_pot);
//        mCallAmount = findViewById(R.id.amount_to_call);
//        mMinimumBet = findViewById(R.id.minimum_bet);
//        mGameState = findViewById(R.id.game_state);

        mFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    texasRequestManager.fold(gameId);
                    turnOver();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                openBetFragment();
                //TODO contents, start bet fragment?
            }
        });

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call
                try {
                    texasRequestManager.sendMove(
                            new Move("BET", lastState.getAmountToCall()),gameId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        disableButtons();

    }

    private void openBetFragment() {
        //TODO change to dialog fragment
        BetFragment fragment = new BetFragment();
        fragment.setArguments(makeBetBundle());
        fragment.setBetListener(this);
        fragment.show(getSupportFragmentManager(),"bet_amount");
    }

    private Bundle makeBetBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("min", lastState.getMinBet());
        bundle.putInt("call",lastState.getAmountToCall());
        return bundle;
    }


    private void turnOver() {
        disableButtons();
        startTimer();
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

    @Override
    public void onFinish(int amount) {
        //TODO implement after bet select
        try {
            texasRequestManager.sendMove(new Move("BET",amount),gameId);
            turnOver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
        //TODO on cancel
        enableButtons();
    }


    private class RefreshPage extends AsyncTask<Void, Void,GameState> {

        @Override
        protected GameState doInBackground(Void ... voids) {
            return ping();
        }

        @Override
        protected void onPostExecute(GameState state) {
            lastState = state;
            refreshPage(state);
            //TODO setup refresh
            //TODO setup bet layout etc
        }
    }

    private void refreshPage(GameState state) {
        if(state.getTurnUserId() == state.getUserId()) {
            enableButtons();
            stopRefresh();
            showYourTurnToast();
        }
        //user list
        addUsers(state.getUsers());

        //info
        setInfo(state);

        //set cards
        setCards(state);


    }

    private void setCards(GameState state) {
        if(state.getState().equals("NOROUND") || state.getState().equals("ENDROUND")) {
            mCardOne.setImageResource(R.drawable.back_aqua);
            mCardTwo.setImageResource(R.drawable.back_aqua);
        } else {
            int[] cards = state.getCards();
            mCardOne.setImageResource(getResources().getIdentifier(cardToString(cards[0]),"drawable",getPackageName()));
            mCardTwo.setImageResource(getResources().getIdentifier(cardToString(cards[1]),"drawable",getPackageName()));
        }


    }

    private void showYourTurnToast() {
        Toast.makeText(this,
                "It's your turn!"
                ,Toast.LENGTH_SHORT).show();
    }

    private void stopRefresh() {
        timer.cancel();

    }

    private void setInfo(GameState state) {
        mGameInfo.setText(String.format(
                "Pot: %d%nAmount to call: %d%nMinimum Bet: %d%nState: %s",
                state.getTotalPot(),state.getAmountToCall(),state.getMinBet(),state.getState()));
    }

    private void addUsers(User[] users) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mUserListContainer.removeAllViews();
        for(User user : users) {
            TextView userView= new TextView(this);
            userView.setLayoutParams(params);
            userView.setText(
                    String.format("%s%nMoney: %d ", user.getUsername(),user.getMoney()));
            userView.setOnClickListener(new ShowUserInfo(user));

            mUserListContainer.addView(userView);
        }

    }

    private void disableButtons() {
        mBet.setEnabled(false);
        mFold.setEnabled(false);
        mCall.setEnabled(false);
    }

    private void enableButtons() {
        mFold.setEnabled(true);
        mBet.setEnabled(true);
        mCall.setEnabled(true);
    }

    private static final String cardToString(int num) {
        if(num < 100 || num > 413 || num % 100 > 13)
            throw new IllegalArgumentException("Number isn't in the range");
        int suit = num/100;
        int face = num % 100;
        StringBuilder  sb = new StringBuilder();
        sb.append(SUIT.get(suit));
        sb.append(FACE.get(face));
        return sb.toString();
    }

    private static class ShowUserInfo implements View.OnClickListener {
        private User user;
        ShowUserInfo(User user) {
            this.user = user;
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(),
                    String.format("ID: %s%nUsername: %s%nMoney: %d%nEmail: %s",
                    user.getId(),user.getUsername(),user.getMoney(),user.getEmail())
                    ,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putLong("game_id",gameId);
    }


}
