package org.cis120.blackjack;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorAndParser implements Iterator<String> {
    //-- parser
    private static String playerS;
    private static String cardS;
    private static String chipS;
    private static String saveS;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player dealer;
    private static LinkedList<String> cards;
    private static int[][] chipsArr;
    private static int numPlayers;
    private final BufferedReader myReader;
    private String line;
    private String nextLine;
    private boolean hasNextBool;
    private boolean ioException;
    private int nextCount;
    private boolean singleLine;
    private final Player[] playerArr;


    public IteratorAndParser(BufferedReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        singleLine = false;
        myReader = reader;
        line = instantiateLine();
        nextLine = instantiateNextLine();
        ioException = false;
        nextCount = 0;


        playerS = null;
        cardS = null;
        chipS = null;
        saveS = null;

        p1 = new Player("Player 1");
        p2 = new Player("Player 2");
        p3 = new Player("Player 3");
        playerArr = new Player[3];
        playerArr[0] = p1;
        playerArr[1] = p2;
        playerArr[2] = p3;
        dealer = new Player("Dealer");
        cards = new LinkedList<>();
        chipsArr = new int[4][3];
        numPlayers = 0;
    }

    public static void csvDataToVariables(BufferedReader br) throws Exception {
        String playerString = "";
        String cardString = "";
        String chipString = "";
        String saveString = "";
        try {
            IteratorAndParser myFli = new IteratorAndParser(br);
            String line = myFli.next();
            if (line != null) {
                playerString = line;
            }
            line = myFli.next();
            if (line != null) {
                cardString = line;
            }
            line = myFli.next();
            if (line != null) {
                chipString = line;
            }
            line = myFli.next();
            if (line != null) {
                saveString = line;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        playerS = playerString;
        cardS = cardString;
        chipS = chipString;
        saveS = saveString;
        setCards();
        setChips();
    }

    public static boolean writeStringsToFile(
            List<String> stringsToWrite, String filePath,
            boolean append
    ) {
        File file = Paths.get(filePath).toFile();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
            for (String string : stringsToWrite) {
                bw.write(string);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("game save failed.");
            return false;
        }
        return true;
    }

    /* -----------------------------
     * ----- GETTER FUNCTIONS ------
     * ----------------------------- */
    public static Player getP1() {
        return p1;
    }

    public static Player getP2() {
        return p2;
    }

    public static Player getP3() {
        return p3;
    }

    public static Player getDealer() {
        return dealer;
    }

    public static int getNumPlayers() {
        return numPlayers;
    }

    public static LinkedList<String> getCards() {
        return cards;
    }

    public static int[][] getChips() {
        return chipsArr;
    }

    public static String getSave() {
        return saveS;
    }

    public static void setSave(boolean save) {
        if (save) {
            saveS = " true";
        } else {
            saveS = " false";
        }
    }

    public static Player[] getPlayerArray() throws Exception {
        Player[] playerArr = new Player[3];
        String pS = playerS.trim(); // player string
        int l = pS.length(); // player string length
        if (l > 0) {
            // 3 players
            if (l == 8) {
                playerArr[0] = p1;
                playerArr[1] = p2;
                playerArr[2] = p3;
                numPlayers = 3;
            } else if (l == 5) { // 2 players
                String first = pS.substring(0, 2);
                String second = pS.substring(3);
                if (first.equals("p1")) {
                    playerArr[0] = p1;
                } else if (first.equals("p2")) {
                    playerArr[0] = p2;
                } else if (first.equals("p3")) {
                    playerArr[0] = p3;
                } else {
                    playerArr = null;
                }
                if (second.equals("p1")) {
                    playerArr[1] = p1;
                } else if (second.equals("p2")) {
                    playerArr[1] = p2;
                } else if (second.equals("p3")) {
                    playerArr[1] = p3;
                } else {
                    playerArr = null;
                }
                numPlayers = 2;
            } else if (l == 2) { // 1 player
                if (pS.equals("p1")) {
                    playerArr[0] = p1;
                } else if (pS.equals("p2")) {
                    playerArr[0] = p2;
                } else if (pS.equals("p3")) {
                    playerArr[0] = p3;
                } else {
                    playerArr = null;
                }
                numPlayers = 1;
            } else {
                throw new Exception("invalid player csv intake");
            }
        }
        return playerArr;
    }

    public static String getEndingPlayerString(Player[] playerArr) {
        String outputString = "";
        numPlayers = Blackjack.getNumPlayers();
        for (int i = 0; i < numPlayers; i++) {
            Player p = playerArr[i];
            if (p.playAgain) {
                if (p == p1) {
                    outputString = outputString + " p1";
                } else if (p == p2) {
                    outputString = outputString + " p2";
                } else {
                    outputString = outputString + " p3";
                }
            }
        }
        return outputString;
    }

    public static String getEndingCards(LinkedList<String> cards) {
        String outputString = "";
        for (String c : cards) {
            outputString = outputString + " " + c;
        }
        return outputString;
    }

    public static String getEndingChips(int[][] chipsArr) {
        String outputString = "";
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 4; r++) {
                String chip = Integer.toString(chipsArr[r][c]);
                outputString = outputString + " " + chip;
            }
        }
        return outputString;
    }

    /* -----------------------------
     * ----- SETTER FUNCTIONS ------
     * ----------------------------- */
    public static void setCards() {
        String cS = cardS.trim();
        while (cS.length() > 0) {
            String currCard = cS.substring(0, 4);
            cards.add(currCard);
            cS = cS.substring(4).trim();
        }
    }

    public static void setChips() throws Exception {
        String[] csa = chipS.trim().split(" ");
        if (csa.length == 12) {
            int i = 0;
            for (int c = 0; c < 3; c++) {
                for (int r = 0; r < 4; r++) {
                    int currChip = Integer.parseInt(csa[i]);
                    chipsArr[r][c] = currChip;
                    i++;
                }
            }
        } else {
            throw new Exception("invalid player csv intake");
        }
    }

    @Override
    public boolean hasNext() {
        if (singleLine) {
            return nextCount == 0;
        } else {
            if (ioException || nextLine == null) {
                hasNextBool = false;
                return false;
            }
            return (line != null);
        }
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        } else {
            nextCount++;
            try {
                if (nextCount == 1) {
                    return line;
                } else {
                    line = nextLine;
                    nextLine = myReader.readLine();
                    if (nextLine == null) {
                        try {
                            myReader.close();
                        } catch (IOException ex) {
                            System.out.println("myReader.close() failed");
                            throw new NoSuchElementException();
                        }
                    }
                    return line;
                }

            } catch (IOException e) {
                ioException = true;
                line = nextLine;
                nextLine = null;
                try {
                    myReader.close();
                } catch (IOException ex) {
                    System.out.println("myReader.close() failed");
                    throw new NoSuchElementException();
                }
                return line;
            }
        }
    }

    public String instantiateLine() {
        try {
            line = myReader.readLine();
        } catch (IOException e) {
            ioException = true;
        }
        return line;
    }

    public String instantiateNextLine() {
        try {
            nextLine = myReader.readLine();
            if (nextLine == null) {
                singleLine = true;
            }
        } catch (IOException e) {
            ioException = true;
            nextLine = null;
            singleLine = true;
        }

        return nextLine;
    }
}