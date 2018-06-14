package com.aquino.texasandroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquino.texasandroid.R;
import com.aquino.texasandroid.activities.GameActivity;
import com.aquino.texasandroid.activities.GamesActivity;
import com.aquino.texasandroid.model.GameInfo;
import com.aquino.texasandroid.model.GameList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 6/13/2018.
 */

public class GameListFragment extends Fragment {

    private GameAdapter mGameAdapter;
    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_list,container,false);
        setupView(view);
        updateUI();

        return view;
    }

    private void updateUI() {
        //TODO addd list and addapter
        mGameAdapter = new GameAdapter();
        mRecyclerView.setAdapter(mGameAdapter);
    }

    private void setupView(View view) {
        mRecyclerView = view.findViewById(R.id.games_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public GameAdapter getGameAdapter() {
        return mGameAdapter;
    }

    private void joinGame(long gameId) {
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra(GamesActivity.GAME_ID_EXTRA, gameId);
        startActivity(intent);
    }

    private class GameHolder extends RecyclerView.ViewHolder {

        private TextView mIdTextView, mTitleTextView;
        private GameInfo mGameInfo;

        public GameHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(
                    R.layout.fragment_games_list_item,parent,false));
            itemView.setOnClickListener(v -> {
                joinGame(mGameInfo.getGameId());
            });
            mIdTextView = itemView.findViewById(R.id.game_id);
            mTitleTextView = itemView.findViewById(R.id.game_title);

        }

        public void bind(GameInfo info) {
            mGameInfo = info;
            mIdTextView.setText("Game Id: " +String.valueOf(info.getGameId()));
            mTitleTextView.setText("Title: " +info.getTitle());
        }

    }




    public class GameAdapter extends RecyclerView.Adapter<GameHolder> {

        private List<GameInfo> mGameInfoList;

        public GameAdapter() {
            mGameInfoList = new ArrayList<>();
        }

        @NonNull
        @Override
        public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new GameHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull GameHolder holder, int position) {
            GameInfo gameInfo = mGameInfoList.get(position);
            holder.bind(gameInfo);

        }

        @Override
        public int getItemCount() {
            return mGameInfoList.size();
        }

        public void setGameInfoList(List<GameInfo> gameInfoList) {
            mGameInfoList = gameInfoList;
        }
    }
}
