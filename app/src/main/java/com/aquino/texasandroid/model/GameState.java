package com.aquino.texasandroid.model;

public class GameState {

    private  long userId,turnUserId,buttonUserId,roundWinner;
    private  int totalPot,amountToCall,minBet;
    private User[] users;
    private int[] cards;
    private String state;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTurnUserId() {
        return turnUserId;
    }

    public void setTurnUserId(long turnUserId) {
        this.turnUserId = turnUserId;
    }

    public long getButtonUserId() {
        return buttonUserId;
    }

    public void setButtonUserId(long buttonUserId) {
        this.buttonUserId = buttonUserId;
    }

    public long getRoundWinner() {
        return roundWinner;
    }

    public void setRoundWinner(long roundWinner) {
        this.roundWinner = roundWinner;
    }

    public int getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(int totalPot) {
        this.totalPot = totalPot;
    }

    public int getAmountToCall() {
        return amountToCall;
    }

    public void setAmountToCall(int amountToCall) {
        this.amountToCall = amountToCall;
    }

    public int getMinBet() {
        return minBet;
    }

    public void setMinBet(int minBet) {
        this.minBet = minBet;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public int[] getCards() {
        return cards;
    }

    public void setCards(int[] cards) {
        this.cards = cards;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
