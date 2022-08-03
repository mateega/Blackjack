package org.cis120.blackjack;

public class Player {
    String name;
    int bet;
    boolean bust;
    boolean playAgain;
    private int userHandSum;
    private String card1;
    private String card2;

    public Player(String playerName) {
        userHandSum = 0;
        name = playerName;
        bet = 0;
        bust = false;
        playAgain = false;
        card1 = "";
        card2 = "";
    }

    /* -----------------------------
     * -------- GETTERS ------------
     * ----------------------------- */
    public String getCard1() {
        return card1;
    }

    /* -----------------------------
     * -------- SETTERS ------------
     * ----------------------------- */
    public void setCard1(String card) {
        card1 = card;
    }

    public String getCard2() {
        return card2;
    }

    public void setCard2(String card) {
        card2 = card;
    }

    public int getHandSum() {
        return userHandSum;
    }

    public void setHandSum(int sum) {
        userHandSum = sum;
    }

    public String getName() {
        return name;
    }

    public int getBet() {
        return bet;
    }
}
