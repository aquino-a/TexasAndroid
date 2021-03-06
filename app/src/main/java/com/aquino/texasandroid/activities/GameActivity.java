package com.aquino.texasandroid.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Button mFold, mBet, mCall;
    private TextView mGameInfo, mTimeRemaining, mTitle, mPot, mCallAmount, mMinimumBet, mGameState;
    private ImageView mCardOne, mCardTwo, mFlopOne, mFlopTwo, mFlopThree, mTurn, mRiver;
    private Timer timer;
    private RefreshPage refresher;
    private Runnable refresh;
    private GameState lastState;
    private String cardBackColor = "back_red";

    private BetFragment fragment;
    private CountDownTimer turnTimer;

    private TexasRequestManager texasRequestManager;
    private long gameId;

    private static final List<String> FACE = Collections.unmodifiableList(
            Arrays.asList("", "two", "three", "four", "five", "six", "seven", "eight",
                    "nine", "ten", "jack", "queen", "king", "ace"));

    private static final List<String> SUIT = Collections.unmodifiableList(
            Arrays.asList("", "club", "diamond", "heart", "spade"));

    private static final int BET_REQUEST_CODE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.start_game:
                try {
                    texasRequestManager.startGame(gameId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    notEnoughPlayersToast();
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.start_game);
        item.setEnabled(false);
        item.getIcon().setAlpha(130);
        //starting game is automatic for now
//        if (lastState != null && (lastState.getState().equals("NOROUND"))) {
//            item.setEnabled(true);
//            item.getIcon().setAlpha(255);
//        } else {
//            // disabled
//            item.setEnabled(false);
//            item.getIcon().setAlpha(130);
//        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupView();

        if (savedInstanceState != null) {
            gameId = savedInstanceState.getLong("game_id");
        } else {
            gameId = getIntent().getLongExtra(GamesActivity.GAME_ID_EXTRA, -1);
            if (gameId == -1)
                finish();
            joinGame();
        }

        startTimer();
    }

    private void startTimer() {
        timer = new Timer();

        refresh = () -> {
            refresher = new RefreshPage();
            refresher.execute();
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runPingInterval();
            }
        }, 0, 2000);
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
        mFlopOne = findViewById(R.id.flop_one_image);
        mFlopTwo = findViewById(R.id.flop_two_image);
        mFlopThree = findViewById(R.id.flop_three_image);
        mTurn = findViewById(R.id.turn_image);
        mRiver = findViewById(R.id.river_image);
        mTitle = findViewById(R.id.game_title_text);

        mGameInfo = findViewById(R.id.game_info);
        mTimeRemaining = findViewById(R.id.time_left);

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
            }
        });

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendPlay(lastState.getAmountToCall());
                    turnOver();
            }
        });
        disableButtons();

    }

    private void openBetFragment() {
        fragment = new BetFragment();
        fragment.setArguments(makeBetBundle());
        fragment.setBetListener(this);
        fragment.show(getSupportFragmentManager(), "bet_amount");
    }

    private Bundle makeBetBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("min", lastState.getMinBet());
        bundle.putInt("call", lastState.getAmountToCall());
        return bundle;
    }


    private void turnOver() {
        mTimeRemaining.setText("Not your turn");
        turnTimer.cancel();
        disableButtons();
        startTimer();
    }

    private void joinGame() {
        try {
            texasRequestManager.room("join", gameId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leaveGame() {
        try {
            texasRequestManager.room("leave", gameId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameState startGame() {
        try {
            return texasRequestManager.sendMove(new Move("START", 0), gameId);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private GameState sendPlay(int bet) {
        try {
            GameState state = ping();
            if(!state.getState().equals("NOROUND"))
                return texasRequestManager.sendMove(new Move("BET", bet), gameId);
            return null;
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
                sendPlay(amount);
                turnOver();
    }

    @Override
    public void onCancel() {
        enableButtons();
    }


    private class RefreshPage extends AsyncTask<Void, Void, GameState> {

        @Override
        protected GameState doInBackground(Void... voids) {
            return ping();
        }

        @Override
        protected void onPostExecute(GameState state) {
            lastState = state;
            refreshPage(state);
        }
    }

    private void refreshPage(GameState state) {
        if (autoStart(state)) {
            startGame();
        }

        if(state.getState().equals("ENDROUND")){
            if(isWinner(state)) {
                showToast("You won!");
            } else showToast("You lost!");
        }
        if (checkTurn(state)) {
            enableButtons();
            stopRefresh();
            showYourTurnToast();
            timeTurn();
        }
        //user list
        addUsers(state);

        //info
        setInfo(state);

        //set cards
        setCards(state);


    }

    private void showToast(String str) {
        Toast toast =
        Toast.makeText(this,
                str
                , Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL,0,-300);
        toast.show();
    }

    private boolean isWinner(GameState state) {
        return state.getUserId() == state.getRoundWinner();
    }


    private boolean autoStart(GameState state) {
        if (!state.getState().equals("NOROUND"))
            return false;
        if (state.getUsers().length < 2)
            return false;
        if (!(state.getUsers()[0].getId() == state.getUserId()))
            return false;
        return true;
    }

    private boolean checkTurn(GameState state) {
        if (state.getState().equals("NOROUND"))
            return false;
        if (state.getCards()[0] == 0 || state.getCards()[1] == 0)
            return false;
        return state.getUserId() == state.getTurnUserId();
    }

    private void timeTurn() {
        turnTimer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeRemaining.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (fragment != null && fragment.isVisible())
                    fragment.dismiss();
                try {
                    texasRequestManager.fold(gameId);
                    turnOver();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void setCards(GameState state) {
        String gameState = state.getState();
        int[] cards = state.getCards();
        mCardOne.setImageResource(getResources().getIdentifier(cardToString(cards[0]), "drawable", getPackageName()));
        mCardTwo.setImageResource(getResources().getIdentifier(cardToString(cards[1]), "drawable", getPackageName()));
        mFlopOne.setImageResource(getResources().getIdentifier(cardToString(cards[2]), "drawable", getPackageName()));
        mFlopTwo.setImageResource(getResources().getIdentifier(cardToString(cards[3]), "drawable", getPackageName()));
        mFlopThree.setImageResource(getResources().getIdentifier(cardToString(cards[4]), "drawable", getPackageName()));
        mTurn.setImageResource(getResources().getIdentifier(cardToString(cards[5]), "drawable", getPackageName()));
        mRiver.setImageResource(getResources().getIdentifier(cardToString(cards[6]), "drawable", getPackageName()));

    }

    private void notEnoughPlayersToast() {
        Toast.makeText(this,
                "Not enough players to start"
                , Toast.LENGTH_SHORT).show();
    }

    private void showYourTurnToast() {
        showToast("It's your turn!");
    }

    private void stopRefresh() {
        timer.cancel();

    }

    private void setInfo(GameState state) {
        mGameInfo.setText(String.format(
                "Pot: %d%nAmount to call: %d%nMinimum Bet: %d%nState: %s",
                state.getTotalPot(), state.getAmountToCall(), state.getMinBet(), state.getState()));
    }

    private void addUsers(GameState state) {
        User[] users = state.getUsers();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mUserListContainer.removeAllViews();
        for (User user : users) {
            TextView userView = new TextView(this);
            userView.setLayoutParams(params);
            userView.setText(
                    String.format("%s%nMoney: %d ", user.getUsername(), user.getMoney()));
            userView.setOnClickListener(new ShowUserInfo(user));
            if(state.getTurnUserId() == user.getId())
                userView.setBackgroundColor(Color.YELLOW);
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

    private final String cardToString(int num) {
        if (num == 0)
            return cardBackColor;
        if (num < 101 || num > 413 || num % 100 > 13)
            throw new IllegalArgumentException("Number isn't in the range");
        int suit = num / 100;
        int face = num % 100;
        StringBuilder sb = new StringBuilder();
        sb.append(SUIT.get(suit));
        sb.append(FACE.get(face));
        String result = sb.toString();
        Log.d(getLocalClassName(),"Processed card: "+ result);
        return result;
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
                            user.getId(), user.getUsername(), user.getMoney(), user.getEmail())
                    , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putLong("game_id", gameId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            texasRequestManager.room("leave", gameId);
            if (turnTimer != null)
                turnTimer.cancel();
            if (timer != null)
                timer.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
