package org.cis120.blackjack;

/**
 * CIS 120 HW09 - Blackjack
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class RunBlackjack implements Runnable {
    public void run() {
        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Blackjack");
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        // Button panel
        final JPanel button_panel = new JPanel();
        frame.add(button_panel, BorderLayout.SOUTH);
        button_panel.add(status);

        JButton betB = new JButton("bet");
        button_panel.add(betB);

        JButton hitB = new JButton("hit");
        button_panel.add(hitB);

        JButton standB = new JButton("stand");
        button_panel.add(standB);

        JButton playAgainB = new JButton("play again");

        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);


        // Game board
        final GameBoard board = new GameBoard(status);
        frame.add(board, BorderLayout.CENTER);

        // end game button
        final JButton endGame = new JButton("end game");
        endGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] options = {"yes", "no"};
                int x = JOptionPane.showOptionDialog(frame,
                        "would you like to save this game for later continuation?",
                        "Save Game",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0]);
                board.endGame(x == 0);
                if (!board.getSaveSuccess()) {
                    JOptionPane.showMessageDialog(frame, "game save failed.");
                }
                System.exit(0);
            }
        });

        // instructions button
        JButton instructionsB = new JButton("instructions");
        instructionsB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "OBJECTIVE \neach player attempts to beat the dealer by " +
                                "getting a score as close to 21 as possible, without " +
                                "going over 21" +
                                "\n\nSCORING \nyour score is the total value of your " +
                                "current cards. Faces cards are worth 10, *aces are worth " +
                                "1*, and any other card is worth its pip value" +
                                "\n\nBETTING \nbefore the deal begins, each player places " +
                                "a bet, in chips. bets must be made in multiples of 10. " +
                                "the minimum bet is 10" +
                                "\n\nDEALING \nafter all bets are made, the dealer deals " +
                                "two face up cards to each player and one face up card " +
                                "and one face down card to themself" +
                                "\n\nTHE PLAY \nthe left most player (Player 1) goes " +
                                "first and must decide whether to 'hit' (ask for another " +
                                "card) or 'stand' (not ask for another card). \na player " +
                                "may stand on the two cards originally dealt to them or " +
                                "they may ask the dealer for additional cards, one at a " +
                                "time, until deciding to stand on the total \n(if it is " +
                                "21 or under). if a player 'busts' (their score is over " +
                                "21), they lose and the dealer collects their bet. the " +
                                "dealer then turns to the next player (the \nplayer to " +
                                "the right) and serves them in the same manner." +
                                "\n\nTHE DEALER'S PLAY \nwhen the dealer has served " +
                                "every player, the dealers face-down card is turned up. " +
                                "the dealer will then hit or stand themself, until they " +
                                "reach \na desirable score or they bust (causing all " +
                                "players who didn't bust themself to win)." +
                                "\n\nGAME EDITS \nthere is no doubling down or slitting " +
                                "in this game. aces are always worth 1. the dealer acts " +
                                "in a way best for them, meaning they don't have to " +
                                "\nstand on scores 17 and up or hit on scores less than " +
                                "17." +
                                "\n\nSETTLEMENT \nif a player goes bust, they lose their " +
                                "bet, even if the dealer goes bust as well. if the dealer " +
                                "goes over 21, the dealer pays each player (who haven't " +
                                "bust) \nthe amount of their bet. if the dealer stands at " +
                                "21 or less, the dealer pays the bet of any player having " +
                                "a higher score (not exceeding 21) and collects the bet " +
                                "\nof any player having a lower score. if there is a " +
                                "'push' (a player having the same total as the dealer), " +
                                "the player neither loses or gains any chips." +
                                "\n\nNUMBER OF PLAYERS AND HOW TO WIN \nthere is always 3 " +
                                "players. players play using the same keyboard and mouse. " +
                                "once one player loses (has no chips left), the game ends, " +
                                "\nwith the player with the highest score as the winner." +
                                "\n\nHOW TO PLAY \nto start each round of blackjack, click " +
                                "the 'play again' button. players can end the game after " +
                                "the settlement period by clicking the 'end game' " +
                                "\nbutton. players then place their bets by clicking the " +
                                "chips on the screen when it is their turn (noted in the " +
                                "bottom left of the screen [e.g., 'player 1's \nbet']). " +
                                "when a player is done betting, they click the 'bet' " +
                                "button, allowing the next players to bet. once all bets " +
                                "have been placed, cards are dealt to each player. \neach " +
                                "player then hits (potentially multiple times) or stands " +
                                "on their hand with the 'hit' and 'stand' buttons. once " +
                                "all players have stood, the dealer plays and \nthe " +
                                "players are notified if they won or lost. the players' " +
                                "chip counts are updated accordingly. the players then " +
                                "have the option to play another round ('play \nagain' " +
                                "button) or end the game ('end game' button).");
            }
        });
        control_panel.add(instructionsB);
        control_panel.add(playAgainB);
        control_panel.add(endGame);

        // play again button
        playAgainB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.playRound();
                betB.setEnabled(false);
                hitB.setEnabled(false);
                standB.setEnabled(false);
                playAgainB.setEnabled(false);
                endGame.setEnabled(false);
            }
        });

        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (board.getCurrBet() != 0) {
                    betB.setEnabled(true);
                    board.setBetting(true);
                } else {
                    betB.setEnabled(false);
                }
            }
        });


        // bet button
        betB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.bet();
                if (board.finalBet()) {
                    betB.setEnabled(false);
                    hitB.setEnabled(true);
                    standB.setEnabled(true);
                    playAgainB.setEnabled(false);
                } else {
                    betB.setEnabled(false);
                    hitB.setEnabled(false);
                    standB.setEnabled(false);
                    playAgainB.setEnabled(false);
                }
            }
        });

        // hit button
        hitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.hit();
                if (board.getShowResults()) {
                    betB.setEnabled(false);
                    hitB.setEnabled(false);
                    standB.setEnabled(false);
                    playAgainB.setEnabled(true);
                    endGame.setEnabled(true);
                }
            }
        });

        // stand button
        standB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.stand();
                if (board.getShowResults()) {
                    betB.setEnabled(false);
                    hitB.setEnabled(false);
                    standB.setEnabled(false);
                    playAgainB.setEnabled(true);
                    endGame.setEnabled(true);
                }
            }
        });


        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setVisible(true);

        // Start the game
        board.reset();

        //FINAL BOARD??
        System.out.println(board.getSave());
        if (board.getSave()) {
            String[] options = {"yes", "no"};
            int x = JOptionPane.showOptionDialog(frame,
                    "would you like to resume your previous saved game?",
                    "Resume Previous Game",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            board.getSavedGame(x == 0);
        } else {
            board.getSavedGame(false);
        }

        betB.setEnabled(false);
        hitB.setEnabled(false);
        standB.setEnabled(false);
        playAgainB.setEnabled(true);
    }
}