package com.aquino.texasandroid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Move {

    private String moveType;
    private int bet;

    public Move(String moveType, int bet) {
        this.moveType = moveType;
        this.bet = bet;
    }

    public Move() { }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
