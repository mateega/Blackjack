package org.cis120.blackjack;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * <p>
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * <p>
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    // Game constants
    public static final int BOARD_WIDTH = 300;
    public static final int BOARD_HEIGHT = 300;
    int hitCount = 0;
    int currPlayer = 1;
    int playersPlaid = 0;
    private final Blackjack ttt; // model for the game
    private final JLabel status; // current status text
    private int loc0Count = 0;
    private int loc1Count = 0;
    private int loc2Count = 0;
    private int loc3Count = 0;
    private final boolean[] hits = new boolean[4];
    private final String[] results = new String[3];
    private boolean showResults = false;
    private boolean betting = false;
    private boolean finalBet = false;
    private int currBet = 0;
    private int p1Bet = 0;
    private int p2Bet = 0;
    private int p3Bet = 0;
    private boolean displayPlayRound = true;
    private boolean displayTurn = false;


    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit) {
        resetHits();

        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Color tableColor = new Color(35, 91, 65);
        setBackground(tableColor);

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("files/save_data.csv"));
        } catch (FileNotFoundException e) {

        }

        ttt = new Blackjack(br); // initializes model for the game
        status = statusInit; // initializes the status JLabel

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                int yHigh = 425;
                int yLow = yHigh + 39;

                int chipAmount = 0;
                if (p.y >= yHigh && p.y <= yLow) {
                    if (p.x >= 30 && p.x <= 190) {
                        if (p.x >= 30 && p.x <= 70) { //width of 40
                            System.out.println("500");
                            chipAmount = 500;
                        } else if (p.x >= 71 && p.x <= 110) {
                            System.out.println("100");
                            chipAmount = 100;
                        } else if (p.x >= 111 && p.x <= 150) {
                            System.out.println("50");
                            chipAmount = 50;
                        } else if (p.x >= 151 && p.x <= 190) {
                            System.out.println("10");
                            chipAmount = 10;
                        }
                        p1Bet += chipAmount;
                    }
                    if (p.x >= 273 && p.x <= 433) {
                        if (p.x >= 273 && p.x <= 313) { //width of 40
                            System.out.println("500");
                            chipAmount = 500;
                        } else if (p.x >= 314 && p.x <= 353) {
                            System.out.println("100");
                            chipAmount = 100;
                        } else if (p.x >= 354 && p.x <= 393) {
                            System.out.println("50");
                            chipAmount = 50;
                        } else if (p.x >= 394 && p.x <= 433) {
                            System.out.println("10");
                            chipAmount = 10;
                        }
                        p2Bet += chipAmount;
                    }
                    if (p.x >= 510 && p.x <= 670) {
                        if (p.x >= 510 && p.x <= 550) { //width of 40
                            System.out.println("500");
                            chipAmount = 500;
                        } else if (p.x >= 551 && p.x <= 590) {
                            System.out.println("100");
                            chipAmount = 100;
                        } else if (p.x >= 591 && p.x <= 630) {
                            System.out.println("50");
                            chipAmount = 50;
                        } else if (p.x >= 631 && p.x <= 670) {
                            System.out.println("10");
                            chipAmount = 10;
                        }
                        p3Bet += chipAmount;
                    }
                }

                currBet += chipAmount;
                System.out.println("Current bet: " + currBet);

                updateStatus();
                repaint();
            }
        });


    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        status.setText("click 'play again' or 'end game'");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void playRound() {
        showResults = false;
        displayPlayRound = false;
        currBet = 0;
        ttt.playRound(true);
        betting = true;
        updateStatus();
        repaint();
    }

    public void endGame(boolean save) {
        ttt.playRound(false);
        ttt.endGame(save);
        repaint();
    }

    public void bet() {
        finalBet = false;
        //update for num players left (ask less if players are out)
        if (currPlayer == 1) {
            ttt.placeBet(ttt.getP1(), currBet);
            currPlayer = 2;
        } else if (currPlayer == 2) {
            ttt.placeBet(ttt.getP2(), currBet);
            currPlayer = 3;
        } else {
            ttt.placeBet(ttt.getP3(), currBet);
            currPlayer = 1;
            finalBet = true;
        }
        if (finalBet) {
            ttt.dealCards();
            displayTurn = true;
        }
        currBet = 0;
        updateStatus();
        repaint();
    }

    public void hit() {
        //hits[hitCount] = true;
        if (currPlayer == 1) {
            ttt.playHand(ttt.getP1(), true);

            if (ttt.getP1().bust) {
                currPlayer = 2;
                resetHits();
                playersPlaid++;
                if (Blackjack.getNumPlayers() == playersPlaid) {
                    ttt.dealerPlay();
                    results();
                    playersPlaid = 0;
                }
            }
        } else if (currPlayer == 2) {
            ttt.playHand(ttt.getP2(), true);
            if (ttt.getP2().bust) {
                currPlayer = 3;
                resetHits();
                playersPlaid++;
                if (Blackjack.getNumPlayers() == playersPlaid) {
                    ttt.dealerPlay();
                    results();
                    playersPlaid = 0;
                }
            }
        } else {
            ttt.playHand(ttt.getP3(), true);
            if (ttt.getP3().bust) {
                System.out.println("all bust 1");
                currPlayer = 1;
                resetHits();
                playersPlaid++;
                if (Blackjack.getNumPlayers() == playersPlaid) {
                    System.out.println("all bust 2");
                    ttt.dealerPlay();
                    results();
                    playersPlaid = 0;
                }
            }
        }
        hitCount++;
        System.out.println("hit [for testing]");
        updateStatus();
        repaint();
    }

    public void stand() {
        if (currPlayer == 1) {
            ttt.playHand(ttt.getP1(), false);
            currPlayer = 2;
        } else if (currPlayer == 2) {
            ttt.playHand(ttt.getP2(), false);
            currPlayer = 3;
        } else {
            ttt.playHand(ttt.getP3(), false);
            currPlayer = 1;
        }
        resetHits();
        playersPlaid++;
        System.out.println("stand [for testing]");

        if (Blackjack.getNumPlayers() == playersPlaid) {
            ttt.dealerPlay();
            results();
            playersPlaid = 0;
        }

        updateStatus();
        repaint();
    }

    public void resetHits() {
        hits[0] = false;
        hits[1] = false;
        hits[2] = false;
        hits[3] = false;
        hitCount = 0;
    }

    public void results() {
        displayTurn = false;
        betting = false;
        currBet = 0;
        p1Bet = 0;
        p2Bet = 0;
        p3Bet = 0;
        String[] resultsString = ttt.results();
        for (int i = 0; i < Blackjack.getNumPlayers(); i++) {
            results[i] = resultsString[i];
        }
        showResults = true;
        displayPlayRound = true;
        repaint();
    }


    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (displayPlayRound) {
            status.setText("click 'play again' or 'end game'");
        } else {
            if (currPlayer == 1) {
                if (betting && !displayTurn) {
                    status.setText("player 1's bet");
                } else {
                    status.setText("player 1 hit or stand");
                }
            } else if (currPlayer == 2) {
                if (betting && !displayTurn) {
                    status.setText("player 2's bet");
                } else {
                    status.setText("player 2 hit or stand");
                }
            } else {
                if (betting && !displayTurn) {
                    status.setText("player 3's bet");
                } else {
                    status.setText("player 3 hit or stand");
                }
            }
        }

    }

    /**
     * Draws the game board.
     * <p>
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoardLines(g);
        drawCards(g);
        drawResults(g);
        drawPlayerLabels(g);
        drawPlayerBalances(g);
        drawChips(g);
        drawChipAmounts(g);
        drawBet(g);
    }

    public void drawBet(Graphics g) {
        if (betting) {
            g.setColor(Color.white);
            int x = 190;
            int y = 245;
            if (p1Bet != 0) {
                g.drawString("Bet", x, y);
                g.drawString(Integer.toString(p1Bet), x, y + 30);
            }
            x = 420;
            if (p2Bet != 0) {
                g.drawString("Bet", x, y);
                g.drawString(Integer.toString(p2Bet), x, y + 30);
            }
            x = 660;
            if (p3Bet != 0) {
                g.drawString("Bet", x, y);
                g.drawString(Integer.toString(p3Bet), x, y + 30);
            }
        }
    }

    public void drawChips(Graphics g) {
        String imagePath = "files/chips.png";
        try {
            BufferedImage chipImg = ImageIO.read(new File(imagePath));
            System.out.println(chipImg.getWidth());
            System.out.println(chipImg.getHeight());
            int w = 160;
            int h = 39;
            int x = 30;
            int y = 425;
            g.drawImage(chipImg, x, y, w, h, null);
            x = 273;
            g.drawImage(chipImg, x, y, w, h, null);
            x = 510;
            g.drawImage(chipImg, x, y, w, h, null);

        } catch (IOException e) {
            System.out.println("IO exception");
        }
    }

    public void drawChipAmounts(Graphics g) {
        g.setColor(Color.white);
        int x = 48;
        int dx = 38;
        int y = 420;
        String string = Integer.toString(ttt.getChipCount(ttt.getP1(), 500));
        g.drawString(string, x, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP1(), 100));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP1(), 50));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP1(), 10));
        g.drawString(string, x += dx, y);

        x = 290;
        string = Integer.toString(ttt.getChipCount(ttt.getP2(), 500));
        g.drawString(string, x, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP2(), 100));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP2(), 50));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP2(), 10));
        g.drawString(string, x += dx, y);

        x = 528;
        string = Integer.toString(ttt.getChipCount(ttt.getP3(), 500));
        g.drawString(string, x, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP3(), 100));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP3(), 50));
        g.drawString(string, x += dx, y);
        string = Integer.toString(ttt.getChipCount(ttt.getP3(), 10));
        g.drawString(string, x += dx, y);

        g.setColor(Color.black);
    }

    public void drawResults(Graphics g) {
        if (showResults) {
            for (int i = 0; i < Blackjack.getNumPlayers(); i++) {
                int x;
                int y = 385;
                if (i == 0) {
                    x = 20;
                } else if (i == 1) {
                    x = 273;
                } else {
                    x = 506;
                }
                g.setColor(Color.white);
                g.drawString(results[i], x, y);
            }
            g.setColor(Color.black);
        }
        if (ttt.getGameOver()) {
            g.setColor(Color.white);
            g.drawString("GAME OVER", 320, 170);
            int p1Balance = ttt.getBalance(ttt.getP1());
            int p2Balance = ttt.getBalance(ttt.getP2());
            int p3Balance = ttt.getBalance(ttt.getP3());
            int x = 90;
            int y = 216;
            if (p1Balance != 0 && p1Balance >= p2Balance && p1Balance >= p3Balance) {
                g.drawString("you win!", x, y);
            } else {
                g.drawString("you lose", x, y);
            }
            x = 333;
            if (p2Balance != 0 && p2Balance >= p1Balance && p2Balance >= p3Balance) {
                g.drawString("you win!", x, y);
            } else {
                g.drawString("you lose", x, y);
            }
            x = 576;
            if (p3Balance != 0 && p3Balance >= p1Balance && p3Balance >= p2Balance) {
                g.drawString("you win!", x, y);
            } else {
                g.drawString("you lose", x, y);
            }
            g.setColor(Color.black);
        }
    }

    public void drawPlayerLabels(Graphics g) {
        g.setColor(Color.white);
        int x = 90;
        int y = 200;
        g.drawString("player 1", x, y);
        x = 333;
        g.drawString("player 2", x, y);
        x = 576;
        g.drawString("player 3", x, y);
        g.setColor(Color.black);
    }

    public void drawPlayerBalances(Graphics g) {
        g.setColor(Color.white);
        int x = 60;
        int y = 483;
        g.drawString("balance: " + ttt.getBalance(ttt.getP1()), x, y);
        x = 310;
        g.drawString("balance: " + ttt.getBalance(ttt.getP2()), x, y);
        x = 546;
        g.drawString("balance: " + ttt.getBalance(ttt.getP3()), x, y);

        g.setColor(Color.black);
    }


    public void drawCards(Graphics g) {
        String[] currCards = ttt.getCurrCards();
        int ccc = ttt.getCurrCardsCount();
        loc0Count = 0;
        loc1Count = 0;
        loc2Count = 0;
        loc3Count = 0;
        for (int i = 0; i < ccc; i++) {
            try {
                int location = Integer.valueOf(currCards[i].substring(0, 1));
                String card = currCards[i].substring(1, 4);
                drawCard(g, card, location);
                if (location == 0) {
                    loc0Count++;
                } else if (location == 1) {
                    loc1Count++;
                } else if (location == 2) {
                    loc2Count++;
                } else if (location == 3) {
                    loc3Count++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawCard(Graphics g, String card, int location) throws IOException {
        String imagePath = "files/cards/" + card + ".png";
        //THROW FILE NOT FOUND
        BufferedImage cardImg = ImageIO.read(new File(imagePath));
        int w = 500 / 6;
        int h = 686 / 6; //myPicture.getHeight()
        int x = 0;
        int y = 0;
        if (location == 0) {
            x = 20 + (loc0Count * 15);
            y = 225 + (loc0Count * 10);
        } else if (location == 1) {
            x = 263 + (loc1Count * 15);
            y = 225 + (loc1Count * 10);
        } else if (location == 2) {
            x = 496 + (loc2Count * 15);
            y = 225 + (loc2Count * 10);
        } else if (location == 3) {
            x = 340 + (loc3Count * 15);
            y = 10 + (loc3Count * 10);
        }

        g.drawImage(cardImg, x, y, w, h, null);
    }

    public void drawBoardLines(Graphics g) {
        // Draws board grid
        int h = 400;
        g.drawLine(233, 225, 233, h - 25);
        g.drawLine(466, 225, 466, h - 25);

        g.drawLine(0, h, 700, h);
        g.drawLine(233, h, 233, 500);
        g.drawLine(466, h, 466, 500);
    }


    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    public boolean getShowResults() {
        return showResults;
    }

    public boolean finalBet() {
        return finalBet;
    }

    public int getCurrBet() {
        return currBet;
    }

    public void setBetting(boolean betting) {
        this.betting = betting;
    }

    public boolean getSave() {
        return ttt.getSave();
    }

    public void getSavedGame(boolean resume) {
        ttt.getSavedGame(resume);
    }

    public boolean getSaveSuccess() {
        return ttt.getSaveSuccess();
    }

}
