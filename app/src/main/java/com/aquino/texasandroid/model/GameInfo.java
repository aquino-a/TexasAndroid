package com.aquino.texasandroid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInfo {

    private String title;
    private long gameId;

    public GameInfo(String title, long gameId) {
        this.title = title;
        this.gameId = gameId;
    }

    public GameInfo() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
