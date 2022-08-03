package org.cis120.blackjack;

/**
 * CIS 120 HW09 - Blackjack
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Blackjack {
    static final String FILE_PATH = "files/save_data.csv";
    private static int numPlayers;
    private final Player p1;
    private final Player p2;
    private final Player p3;
    private final Player dealer;
    private final LinkedList<String> cards;
    private final Scanner reader;
    private final int dealerHandSum;
    private final int[][] chipsArr;
    private final Player[] playerArr;
    private final boolean save;
    private final String[] currCards;
    private int bustCount;
    private int currCardsCount;
    private boolean gameOver;
    private boolean saveSuccess;

    /**
     * Constructor sets up game state.
     */
    public Blackjack(BufferedReader br)  {
        Player[] playerArr1;
        try {
            IteratorAndParser.csvDataToVariables(br);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p1 = IteratorAndParser.getP1();
        p2 = IteratorAndParser.getP2();
        p3 = IteratorAndParser.getP3();
        dealer = IteratorAndParser.getDealer();
        reader = new Scanner(System.in);
        dealerHandSum = 0;
        playerArr1 = null;
        try {
            playerArr1 = IteratorAndParser.getPlayerArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        playerArr = new Player[3];
//        playerArr[0] = p1;
//        playerArr[1] = p1;
//        playerArr[2] = p1;
        playerArr = playerArr1;
        numPlayers = IteratorAndParser.getNumPlayers();
        cards = IteratorAndParser.getCards();
        chipsArr = IteratorAndParser.getChips();
        String tempSave = IteratorAndParser.getSave();
        save = tempSave.equals(" true");
        bustCount = 0;

        currCards = new String[20];
        currCardsCount = 0;
        gameOver = false;
        saveSuccess = true;
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     * <p>
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     * <p>
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("files/save_data.csv"));
            Blackjack bj = new org.cis120.blackjack.Blackjack(br);
            // play one round of blackjack
            bj.getSavedGame(true);
            bj.playRound(true);
            bj.placeBet(bj.p1, 100);
            bj.placeBet(bj.p2, 200);
            bj.placeBet(bj.p3, 50);
            bj.dealCards();
            bj.playHand(bj.p1, true);
            bj.playHand(bj.p1, false);
            bj.playHand(bj.p2, false);
            bj.playHand(bj.p3, true);
            bj.playHand(bj.p3, false);
            bj.dealerPlay();
            bj.results();
            bj.playRound(false);
            bj.endGame(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* -----------------------------
     * ----- GETTER FUNCTIONS ------
     * ----------------------------- */
    public static int getNumPlayers() {
        return numPlayers;
    }

    /* -----------------------------
     * ----- GAME SET UP -----------
     * ----------------------------- */
    public void getSavedGame(boolean resume) {
        if (save && resume) {
            for (int i = 0; i < numPlayers; i++) {
                Player player = playerArr[i];
                System.out.println("\nPlayer " + (i + 1) + "'s balance is: " + getBalance(player));
                printChips(player);
                playerArr[i].playAgain = true;
            }
        } else {
            startGame(true);
        }
    }

    public void startGame(boolean start) {
        if (start) {
            populateDeck();
            numPlayers = 3;
            playerArr[0] = p1;
            playerArr[1] = p2;
            playerArr[2] = p3;
            for (int i = 0; i < numPlayers; i++) {
                Player player = playerArr[i];
                placeChips();
                System.out.println("Player " + (i + 1) + "'s balance is: " + getBalance(player));
                playerArr[i].playAgain = true;
            }
        } else {
            System.out.println("exiting game. good bye.");
        }
    }

    public void placeChips() {
        for (int r = 0; r < chipsArr.length; r++) {
            for (int c = 0; c < chipsArr[0].length; c++) {
                if (r == 0) {
                    chipsArr[r][c] = 1;
                } else if (r == 1) {
                    chipsArr[r][c] = 2;
                } else if (r == 2) {
                    chipsArr[r][c] = 3;
                } else {
                    chipsArr[r][c] = 15;
                }
            }
        }
    }

    /* -----------------------------
     * ----- GAME EXECUTION --------
     * ----------------------------- */
    public void playRound(boolean play) {
        clearCurrCards();
        if (play) {
            bustCount = 0;
            for (int i = 0; i < numPlayers; i++) {
                Player player = playerArr[i];
                player.bust = false;
            }
            System.out.println("playRound [for testing]");

        }
    }

    public void clearCurrCards() {
        currCardsCount = 0;
        for (int i = 0; i < currCards.length; i++) {
            currCards[i] = "";
        }
    }

    public void placeBet(Player player, int bet) {
        if ((bet <= getBalance(player)) && bet >= 10 && (bet % 10 == 0)) {
            player.bet = bet;
            System.out.println(player.getName() + " bets: " + bet + " [for testing]");
        }
    }

    public void dealCards() {
        clearCurrCards();
        if (cards.size() < 2) {
            populateDeck();
        }
        dealer.setCard1(drawCard());
        dealer.setCard2(drawCard());
        dealer.setHandSum(cardToInt(dealer.getCard1()) + cardToInt(dealer.getCard2()));
        currCards[0] = 3 + "0000";
        currCards[1] = 3 + dealer.getCard2();
        currCardsCount += 2;

        for (int i = 0; i < numPlayers; i++) {
            Player player = playerArr[i];
            if (cards.size() < 2) {
                populateDeck();
            }
            player.setCard1(drawCard());
            player.setCard2(drawCard());
            player.setHandSum(cardToInt(player.getCard1()) + cardToInt(player.getCard2()));
            currCards[currCardsCount] = i + player.getCard1();
            currCardsCount++;
            currCards[currCardsCount] = i + player.getCard2();
            currCardsCount++;
        }
    }

    public void playHand(Player player, boolean hit) {
        System.out.println("-----------------------");
        System.out.println("--     " + player.name + "      --");
        System.out.println("-----------------------");
        System.out.println("your hand: \n \t" + getCardName(player.getCard1()) +
                "\n \t" + getCardName(player.getCard2()));
        System.out.println("the dealer's hand: \n \t" + getCardName(dealer.getCard1()));
        int i = 0;
        if (player == p1) {
            i = 0;
        }
        if (player == p2) {
            i = 1;
        }
        if (player == p3) {
            i = 2;
        }

        if (cards.size() < 1) {
            populateDeck();
        }
        if (hit) {
            String card = drawCard();
            currCards[currCardsCount] = i + card;
            currCardsCount++;
            player.setHandSum(player.getHandSum() + cardToInt(card));
            System.out.println("you draw: \n \t" + getCardName(card));
        } else {
            System.out.println("you stand");
        }
        if (player.getHandSum() > 21) {
            player.bust = true;
            System.out.println("\nyou bust. dealer wins");
            updateBalance(player, player.bet, false);
            bustCount++;
        }
    }

    public void dealerPlay() {
        currCards[0] = 3 + dealer.getCard1();
        if (bustCount != numPlayers) {
            if (numPlayers > 1) {
                System.out.println("-----------------------");
                System.out.println("--    The Dealer:    --");
                System.out.println("-----------------------");
            }
            System.out.println("\nthe dealer's hand: \n \t" + getCardName(dealer.getCard1()) +
                    "\n \t" + getCardName(dealer.getCard2()));
            dealerPlayHelper();
        }
    }

    public void dealerPlayHelper() {
        if (dealer.getHandSum() < 21) {
            if (cards.size() < 1) {
                populateDeck();
            }
            int numCards = cards.size();
            int deckTotal = 0;
            for (int i = 0; i < numCards; i++) {
                deckTotal += cardToInt(cards.get(i));
            }
            int expectedValue = deckTotal / numCards;

            if (dealer.getHandSum() + expectedValue < 22) {
                String card = drawCard();
                currCards[currCardsCount] = 3 + card;
                currCardsCount++;
                System.out.println("the dealer draws: \n \t" + getCardName(card));
                dealer.setHandSum(dealer.getHandSum() + cardToInt(card));
                dealerPlayHelper();
            }
        }
    }

    public String[] results() {
        String[] resultString = new String[3];
        for (int i = 0; i < numPlayers; i++) {
            Player player = playerArr[i];
            System.out.println("\n-----------------------");
            System.out.println("--     " + player.name + "      --");
            System.out.println("-----------------------");

            if (!player.bust) {
                String message = "";
                if (dealer.getHandSum() > 21) {
                    message = "dealer bust. you win!";
                    System.out.println(message);
                    updateBalance(player, player.bet, true);
                    resultString[i] = (message);
                } else if (player.getHandSum() > dealer.getHandSum()) {
                    message = "you win!";
                    System.out.println(message);
                    updateBalance(player, player.bet, true);
                    resultString[i] = (message);
                } else if (player.getHandSum() < dealer.getHandSum()) {
                    message = "dealer wins";
                    System.out.println(message);
                    updateBalance(player, player.bet, false);
                    resultString[i] = (message);
                } else {
                    message = "push!";
                    System.out.println(message);
                    resultString[i] = (message);
                }
            } else {
                String message = "you bust";
                System.out.println(message);
                resultString[i] = (message);
            }
            if (getBalance(player) <= 0) {
                System.out.println("your balance is 0. you lost this game");
            }
        }

        for (int i = 0; i < numPlayers; i++) {
            Player player = playerArr[i];
            if (getBalance(player) <= 0) {
                player.playAgain = false;
                numPlayers--;
                //updatePlayerArr(player);
                endGame(false);
            }
        }
        return (resultString);
    }

    /* -----------------------------
     * ----- CARD FUNCTIONS --------
     * ----------------------------- */
    public void populateDeck() {
        cards.clear();
        //card deck (1 = first deck, 2 = second deck)
        for (int i = 1; i <= 2; i++) {
            //card suit (1 = heart, 2 = club, 3 = spade, 4 = diamond)
            for (int j = 1; j <= 4; j++) {
                //card number (11=jack, 12=queen, 13=king)
                for (int k = 1; k <= 13; k++) {
                    String card = null;
                    if (k < 10) {
                        card = "0" + k + j + i;
                    } else {
                        card = "" + k + j + i;
                    }
                    cards.add(card);
                }
            }
        }
    }

    public String drawCard() {
        int max = cards.size();
        if (max == 0) {
            System.out.println("Out of cards!");
            return null;
        } else {
            int pick = (int) ((Math.random() * max));
            String card = cards.get(pick);
            cards.remove(pick);
            return card;
        }
    }

    public int cardToInt(String card) {
        if (card != null && !card.equals("")) {
            String cardNumString = card.substring(0, 2);
            int cardNum = Integer.parseInt(cardNumString);
            if (cardNum > 10) {
                cardNum = 10;
            }
            return cardNum;
        }
        return -1;
    }

    public String getCardName(String card) {
        if (card != null && !card.equals("")) {
            String cardNum = card.substring(0, 2);
            String suit = card.substring(2, 3);

            if (cardNum == "1" || Integer.parseInt(cardNum) > 10) {
                switch (cardNum) {
                    case "1":
                        cardNum = "ace";
                        break;
                    case "11":
                        cardNum = "jack";
                        break;
                    case "12":
                        cardNum = "queen";
                        break;
                    case "13":
                        cardNum = "king";
                        break;
                    default:
                        cardNum = null;
                        break;
                }
            } else if (Integer.parseInt(cardNum) < 10) {
                cardNum = cardNum.substring(1, 2);
            }

            switch (suit) {
                case "1":
                    suit = "hearts";
                    break;
                case "2":
                    suit = "clubs";
                    break;
                case "3":
                    suit = "spades";
                    break;
                case "4":
                    suit = "diamonds";
                    break;
                default:
                    suit = null;
                    break;
            }
            return (cardNum + " of " + suit);
        } else {
            return ("invalid card");
        }
    }

    /* -----------------------------
     * ----- CHIP FUNCTIONS -----
     * ----------------------------- */
    public int getBalance(Player player) {
        if (player == p1 || player == p2 || player == p3) {
            int col = 0;
            if (player == p2) {
                col = 1;
            }
            if (player == p3) {
                col = 2;
            }
            int balance = 0;
            for (int r = 0; r < chipsArr.length; r++) {
                for (int c = 0; c < chipsArr[0].length; c++) {
                    if (c == col) {
                        int multiplier = 0;
                        if (r == 0) {
                            multiplier = 500;
                        }
                        if (r == 1) {
                            multiplier = 100;
                        }
                        if (r == 2) {
                            multiplier = 50;
                        }
                        if (r == 3) {
                            multiplier = 10;
                        }
                        balance += (chipsArr[r][c]) * multiplier;
                    }
                }
            }
            return balance;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getChipCount(Player player, int chip) {
        if ((chip == 500 || chip == 100 || chip == 50 || chip == 10) &&
                (player == p1 || player == p2 || player == p3)) {
            int row = 0;
            if (chip == 100) {
                row = 1;
            }
            if (chip == 50) {
                row = 2;
            }
            if (chip == 10) {
                row = 3;
            }
            int col = 0;
            if (player == p2) {
                col = 1;
            }
            if (player == p3) {
                col = 2;
            }
            return chipsArr[row][col];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void updateBalance(Player player, int betAmount, boolean win) {
        if (player == p1 || player == p2 || player == p3) {
            int col = 0;
            if (player == p2) {
                col = 1;
            }
            if (player == p3) {
                col = 2;
            }
            if (win) {
                while (betAmount > 0) {
                    if (betAmount >= 500) {
                        chipsArr[0][col] += 1;
                        betAmount -= 500;
                    }
                    if (betAmount >= 100) {
                        chipsArr[1][col] += 1;
                        betAmount -= 100;
                    }
                    if (betAmount >= 50) {
                        chipsArr[2][col] += 1;
                        betAmount -= 50;
                    }
                    if (betAmount >= 10) {
                        chipsArr[3][col] += 1;
                        betAmount -= 10;
                    }
                }
            } else {
                int loopCount = 0;
                while (betAmount > 0) {
                    if (betAmount >= 500) {
                        if (chipsArr[0][col] > 0) {
                            chipsArr[0][col] -= 1;
                            betAmount -= 500;
                        }
                    }
                    if (betAmount >= 100) {
                        if (chipsArr[1][col] > 0) {
                            chipsArr[1][col] -= 1;
                            betAmount -= 100;
                        }
                    }
                    if (betAmount >= 50) {
                        if (chipsArr[2][col] > 0) {
                            chipsArr[2][col] -= 1;
                            betAmount -= 50;
                        }
                    }
                    if (betAmount >= 10) {
                        if (chipsArr[3][col] > 0) {
                            chipsArr[3][col] -= 1;
                            betAmount -= 10;
                        }
                    }
                    loopCount++;
                    if (loopCount > 100) {
                        if (betAmount < 500 && (chipsArr[0][col] > 0)) {
                            chipsArr[0][col] -= 1;
                            chipsArr[1][col] += 5;
                        } else if (betAmount < 100 && (chipsArr[1][col] > 0)) {
                            chipsArr[1][col] -= 1;
                            chipsArr[2][col] += 2;
                        } else if (betAmount < 50 && (chipsArr[2][col] > 0)) {
                            chipsArr[2][col] -= 1;
                            chipsArr[3][col] += 5;
                        }
                        loopCount = 0;
                    }
                }
            }
            System.out.println("your balance is: " + getBalance(player));
            printChips(player);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void printChips(Player player) {
        System.out.println("\nCHIPS:");
        System.out.println("\t500: " + getChipCount(player, 500));
        System.out.println("\t100: " + getChipCount(player, 100));
        System.out.println("\t50: " + getChipCount(player, 50));
        System.out.println("\t10: " + getChipCount(player, 10));
    }

    /* -----------------------------
     * ----- GAME OVER FUNCTIONS ---
     * ----------------------------- */
    public void endGame(boolean saveGame) {
        gameOver = true;
        System.out.println("\n");
        if (saveGame) {
            System.out.println("game over.\n" +
                    "thank you for playing blackjack! this game has been saved.\n" +
                    "to continue playing this game in the future just reopen this program!\n" +
                    "good bye.");
            IteratorAndParser.setSave(true);
        } else {
            System.out.println("game over.\n" +
                    "thank you for playing blackjack! good bye.");
            IteratorAndParser.setSave(false);
        }

        String players = IteratorAndParser.getEndingPlayerString(playerArr);
        String cards = IteratorAndParser.getEndingCards(this.cards);
        String chips = IteratorAndParser.getEndingChips(chipsArr);
        String save = IteratorAndParser.getSave();
        List<String> data = new LinkedList<>();
        data.add(players);
        data.add(cards);
        data.add(chips);
        data.add(save);
        boolean saveSuccess = IteratorAndParser.writeStringsToFile(data, FILE_PATH, false);
        if (!saveSuccess) {
            this.saveSuccess = false;
        }
    }

    public String[] getCurrCards() {
        return currCards;
    }

    public int getCurrCardsCount() {
        return currCardsCount;
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    public Player getP3() {
        return p3;
    }

    public Player getDealer() {
        return dealer;
    }

    public boolean getSave() {
        return save;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public boolean getSaveSuccess() {
        return saveSuccess;
    }

    public LinkedList<String> getCards() {
        return cards;
    }

}
