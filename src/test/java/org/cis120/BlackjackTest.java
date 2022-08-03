package org.cis120;

import org.cis120.blackjack.Blackjack;
import org.cis120.blackjack.IteratorAndParser;
import org.cis120.blackjack.Player;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class BlackjackTest {

    BufferedReader br;
    Blackjack bj;

    public BlackjackTest() throws Exception {
        br = new BufferedReader(new FileReader("files/save_data.csv"));
        bj = new Blackjack(br);
    }

    @Test
    public void testPlaceChips() throws Exception {
        bj.getSavedGame(false);
        assertEquals(1, bj.getChipCount(bj.getP1(), 500));
        assertEquals(2, bj.getChipCount(bj.getP2(), 100));
        assertEquals(3, bj.getChipCount(bj.getP3(), 50));
        assertEquals(15, bj.getChipCount(bj.getP1(), 10));
    }

    @Test
    public void testClearCards() {
        bj.getSavedGame(false);
        bj.playRound(true);
        assertEquals(0, bj.getCurrCardsCount());
        String[] currCards = bj.getCurrCards();
        for (String card : currCards) {
            assertEquals(0, card.length());
        }
    }

    @Test
    public void testPlaceBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        assertEquals(100, bj.getP1().getBet());
    }

    @Test
    public void testPlaceBetInvalidLowBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 9);
        assertEquals(0, bj.getP1().getBet());
    }

    @Test
    public void testPlaceBetInvalidHighBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 1100);
        assertEquals(0, bj.getP1().getBet());
    }

    @Test
    public void testPlaceBetInvalidNotDivisibleBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 13);
        assertEquals(0, bj.getP1().getBet());
    }

    @Test
    public void testDealCards() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();

        assertNotEquals("", bj.getDealer().getCard1());
        assertNotEquals("", bj.getDealer().getCard2());
        assertNotEquals("", bj.getP1().getCard1());
        assertNotEquals("", bj.getP1().getCard2());
        assertNotEquals("", bj.getP2().getCard1());
        assertNotEquals("", bj.getP2().getCard2());
        assertNotEquals("", bj.getP3().getCard1());
        assertNotEquals("", bj.getP3().getCard2());
        String[] currCards = bj.getCurrCards();
        assertEquals("30000", currCards[0]);
        for (int i = 0; i < 8; i++) {
            assertNotEquals(0, currCards[i].length());
        }
        assertEquals(8, bj.getCurrCardsCount());
    }

    @Test
    public void testPlayHandAllStand() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        int hs1 = bj.getP1().getHandSum();
        int hs2 = bj.getP2().getHandSum();
        int hs3 = bj.getP3().getHandSum();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        assertEquals(hs1, bj.getP1().getHandSum());
        assertEquals(hs2, bj.getP2().getHandSum());
        assertEquals(hs3, bj.getP3().getHandSum());
        assertEquals(8, bj.getCurrCardsCount());
    }

    @Test
    public void testPlayHandOneHit() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        int hs1 = bj.getP1().getHandSum();
        int hs2 = bj.getP2().getHandSum();
        int hs3 = bj.getP3().getHandSum();
        bj.playHand(bj.getP1(), true);
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        assertNotEquals(hs1, bj.getP1().getHandSum());
        assertEquals(hs2, bj.getP2().getHandSum());
        assertEquals(hs3, bj.getP3().getHandSum());
        assertEquals(9, bj.getCurrCardsCount());
    }

    @Test
    public void testPlayHandMultipleHit() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        int hs1 = bj.getP1().getHandSum();
        int hs2 = bj.getP2().getHandSum();
        int hs3 = bj.getP3().getHandSum();
        bj.playHand(bj.getP1(), true);
        bj.playHand(bj.getP1(), true);
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        assertNotEquals(hs1, bj.getP1().getHandSum());
        assertEquals(hs2, bj.getP2().getHandSum());
        assertEquals(hs3, bj.getP3().getHandSum());
        assertEquals(10, bj.getCurrCardsCount());
    }

    @Test
    public void testPlayHandBust() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        int hs1 = bj.getP1().getHandSum();
        int hs2 = bj.getP2().getHandSum();
        int hs3 = bj.getP3().getHandSum();
        int balanceBefore = bj.getBalance(bj.getP1());
        bj.getP1().setHandSum(22);
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        assertNotEquals(hs1, bj.getP1().getHandSum());
        assertEquals(hs2, bj.getP2().getHandSum());
        assertEquals(hs3, bj.getP3().getHandSum());
        assertTrue(balanceBefore > bj.getBalance(bj.getP1()));
    }

    @Test
    public void testDealerPlayUpdatesCurrCard() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        bj.dealerPlay();
        String[] currCards = bj.getCurrCards();
        assertNotEquals("30000", currCards[0]);
    }

    @Test
    public void testResultsDealerBust() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        int balance1Before = bj.getBalance(bj.getP1());
        int balance2Before = bj.getBalance(bj.getP2());
        int balance3Before = bj.getBalance(bj.getP3());
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        bj.dealerPlay();
        bj.getDealer().setHandSum(22);
        bj.results();
        assertTrue(balance1Before < bj.getBalance(bj.getP1()));
        assertTrue(balance2Before < bj.getBalance(bj.getP2()));
        assertTrue(balance3Before < bj.getBalance(bj.getP3()));
    }

    @Test
    public void testResultsPlayerWin() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        int balance1Before = bj.getBalance(bj.getP1());
        int balance2Before = bj.getBalance(bj.getP2());
        int balance3Before = bj.getBalance(bj.getP3());
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        bj.getDealer().setHandSum(1);
        bj.results();
        assertTrue(balance1Before < bj.getBalance(bj.getP1()));
        assertTrue(balance2Before < bj.getBalance(bj.getP2()));
        assertTrue(balance3Before < bj.getBalance(bj.getP3()));
    }

    @Test
    public void testResultsDealerWin() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        int balance1Before = bj.getBalance(bj.getP1());
        int balance2Before = bj.getBalance(bj.getP2());
        int balance3Before = bj.getBalance(bj.getP3());
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        bj.getDealer().setHandSum(21);
        bj.results();
        assertTrue(balance1Before > bj.getBalance(bj.getP1()));
        assertTrue(balance2Before > bj.getBalance(bj.getP2()));
        assertTrue(balance3Before > bj.getBalance(bj.getP3()));
    }

    @Test
    public void testResultsPlayerPush() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        int balance1Before = bj.getBalance(bj.getP1());
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.getDealer().setHandSum(bj.getP1().getHandSum());
        bj.results();
        assertEquals(balance1Before, bj.getBalance(bj.getP1()));
    }

    @Test
    public void testPopulateDeck() {
        bj.populateDeck();
        assertEquals(104, bj.getCards().size());
    }

    @Test
    public void testDrawCard() {
        assertNotEquals(null, bj.drawCard());
    }

    @Test
    public void testCardToInt() {
        assertEquals(6, bj.cardToInt("0621"));
    }

    @Test
    public void testCardToIntFaceCard() {
        assertEquals(10, bj.cardToInt("1221"));
    }

    @Test
    public void testCardToIntNull() {
        assertEquals(-1, bj.cardToInt(null));
    }

    @Test
    public void testCardToIntEmpty() {
        assertEquals(-1, bj.cardToInt(""));
    }

    @Test
    public void testGetCardName() {
        assertTrue(bj.getCardName("0621").equals("6 of clubs"));
    }

    @Test
    public void testGetCardNameFaceCard() {
        assertTrue(bj.getCardName("1231").equals("queen of spades"));
    }

    @Test
    public void testGetCardNameNull() {
        assertEquals("invalid card", bj.getCardName(null));
    }

    @Test
    public void testGetCardNameEmpty() {
        assertEquals("invalid card", bj.getCardName(""));
    }

    @Test
    public void testGetBalanceBeforeBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        assertEquals(1000, bj.getBalance(bj.getP1()));
        assertEquals(1000, bj.getBalance(bj.getP2()));
        assertEquals(1000, bj.getBalance(bj.getP3()));
    }

    @Test
    public void testGetBalanceAfterBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 200);
        bj.placeBet(bj.getP3(), 50);
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        bj.getDealer().setHandSum(1);
        bj.results();
        assertEquals(1100, bj.getBalance(bj.getP1()));
        assertEquals(1200, bj.getBalance(bj.getP2()));
        assertEquals(1050, bj.getBalance(bj.getP3()));
    }

    @Test
    public void testGetBalanceInvalidPlayer() {
        Player p4 = new Player("player 4");
        assertThrows(IllegalArgumentException.class, () -> {
            bj.getBalance(p4);
        });
    }

    @Test
    public void testGetChipCountBeforeBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        assertEquals(1, bj.getChipCount(bj.getP1(), 500));
        assertEquals(2, bj.getChipCount(bj.getP1(), 100));
        assertEquals(3, bj.getChipCount(bj.getP1(), 50));
        assertEquals(15, bj.getChipCount(bj.getP1(), 10));
    }

    @Test
    public void testGetChipCountAfterBet() {
        bj.getSavedGame(false);
        bj.playRound(true);
        bj.placeBet(bj.getP1(), 100);
        bj.placeBet(bj.getP2(), 510);
        bj.placeBet(bj.getP3(), 20);
        bj.dealCards();
        bj.playHand(bj.getP1(), false);
        bj.playHand(bj.getP2(), false);
        bj.playHand(bj.getP3(), false);
        bj.getDealer().setHandSum(1);
        bj.results();
        assertEquals(1, bj.getChipCount(bj.getP1(), 500));
        assertEquals(3, bj.getChipCount(bj.getP1(), 100));
        assertEquals(3, bj.getChipCount(bj.getP1(), 50));
        assertEquals(15, bj.getChipCount(bj.getP1(), 10));

        assertEquals(2, bj.getChipCount(bj.getP2(), 500));
        assertEquals(2, bj.getChipCount(bj.getP2(), 100));
        assertEquals(3, bj.getChipCount(bj.getP2(), 50));
        assertEquals(16, bj.getChipCount(bj.getP2(), 10));

        assertEquals(1, bj.getChipCount(bj.getP3(), 500));
        assertEquals(2, bj.getChipCount(bj.getP3(), 100));
        assertEquals(3, bj.getChipCount(bj.getP3(), 50));
        assertEquals(17, bj.getChipCount(bj.getP3(), 10));
    }

    @Test
    public void testGetChipCountInvalidPlayer() {
        Player p4 = new Player("player 4");
        assertThrows(IllegalArgumentException.class, () -> {
            bj.getChipCount(p4, 100);
        });
    }

    @Test
    public void testGetChipCountInvalidChip() {
        Player p4 = new Player("player 4");
        assertThrows(IllegalArgumentException.class, () -> {
            bj.getChipCount(bj.getP1(), 1000);
        });
    }

    @Test
    public void testUpdateBalanceWin() {
        int balanceBefore = bj.getBalance(bj.getP1());
        bj.updateBalance(bj.getP1(), 100, true);
        assertEquals(balanceBefore + 100, bj.getBalance(bj.getP1()));
    }

    @Test
    public void testUpdateBalanceLose() {
        int balanceBefore = bj.getBalance(bj.getP1());
        bj.updateBalance(bj.getP1(), 100, false);
        assertEquals(balanceBefore - 100, bj.getBalance(bj.getP1()));
    }

    @Test
    public void testUpdateBalanceInvalidPlayer() {
        Player p4 = new Player("player 4");
        assertThrows(IllegalArgumentException.class, () -> {
            bj.updateBalance(p4, 100, false);
        });
    }

    @Test
    public void testEndGameChipsSaved() throws Exception {
        bj.getSavedGame(false);
        bj.updateBalance(bj.getP2(), 100, true);
        bj.updateBalance(bj.getP2(), 500, true);
        bj.updateBalance(bj.getP2(), 50, false);
        bj.endGame(true);
        BufferedReader br = new BufferedReader(new FileReader("files/save_data.csv"));
        IteratorAndParser.csvDataToVariables(br);
        int[][] chipArr = IteratorAndParser.getChips();
        assertEquals(1, chipArr[0][0]);
        assertEquals(2, chipArr[1][0]);
        assertEquals(3, chipArr[2][0]);
        assertEquals(15, chipArr[3][0]);
        assertEquals(2, chipArr[0][1]);
        assertEquals(3, chipArr[1][1]);
        assertEquals(2, chipArr[2][1]);
        assertEquals(15, chipArr[3][1]);
    }

    @Test
    public void testEndGameTrueBooleanSaved() throws Exception {
        bj.getSavedGame(false);
        bj.endGame(true);
        BufferedReader br = new BufferedReader(new FileReader("files/save_data.csv"));
        IteratorAndParser.csvDataToVariables(br);
        assertEquals(" true", IteratorAndParser.getSave());
    }

    @Test
    public void testEndGameFalseBooleanSaved() throws Exception {
        bj.getSavedGame(false);
        bj.endGame(false);
        BufferedReader br = new BufferedReader(new FileReader("files/save_data.csv"));
        IteratorAndParser.csvDataToVariables(br);
        assertEquals(" false", IteratorAndParser.getSave());
    }

    @Test
    public void testGetSavedGameResume() throws Exception {
        bj.getSavedGame(false);
        bj.updateBalance(bj.getP2(), 100, true);
        bj.updateBalance(bj.getP2(), 500, true);
        bj.updateBalance(bj.getP2(), 50, false);
        bj.drawCard();
        int sizeBefore = bj.getCards().size();
        bj.endGame(true);

        bj.getSavedGame(true);
        assertEquals(1000, bj.getBalance(bj.getP1()));
        assertEquals(1550, bj.getBalance(bj.getP2()));
        assertEquals(1000, bj.getBalance(bj.getP3()));
        assertEquals(sizeBefore, bj.getCards().size());
    }

    @Test
    public void testGetSavedGameDontResume() {
        bj.getSavedGame(false);
        bj.updateBalance(bj.getP2(), 100, true);
        bj.updateBalance(bj.getP2(), 500, true);
        bj.updateBalance(bj.getP2(), 50, false);
        bj.drawCard();
        int sizeBefore = bj.getCards().size();
        bj.endGame(true);

        bj.getSavedGame(false);
        assertEquals(1000, bj.getBalance(bj.getP1()));
        assertEquals(1000, bj.getBalance(bj.getP2()));
        assertEquals(1000, bj.getBalance(bj.getP3()));
        assertNotEquals(sizeBefore, bj.getCards().size());
    }

    @Test
    public void testCsvDataToVariables() throws Exception {
        String words = " p1 p2 p3\n"
                + " 0111 0211 0311 0411 0511 0611 0711\n" +
                " 1 2 3 15 2 3 2 15 1 2 3 15\n" +
                " true";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        bj.getSavedGame(false);
        bj.endGame(true);
        IteratorAndParser.csvDataToVariables(br);
        IteratorAndParser.getPlayerArray();
        assertEquals(3, IteratorAndParser.getNumPlayers());
        assertEquals(7, IteratorAndParser.getCards().size());
        int[][] chips = IteratorAndParser.getChips();
        int sum = 0;
        for (int r = 0; r < chips.length; r++) {
            for (int c = 0; c < chips[0].length; c++) {
                sum += chips[r][c];
            }
        }
        assertEquals(64, sum);
        assertEquals(" true", IteratorAndParser.getSave());
    }

    @Test
    public void testHasNextAndNext() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        IteratorAndParser iap = new IteratorAndParser(br);
        assertTrue(iap.hasNext());
        assertEquals("0, The end should come here.", iap.next());
        assertTrue(iap.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", iap.next());
        assertFalse(iap.hasNext());
    }

    @Test
    public void testNoNext() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        IteratorAndParser iap = new IteratorAndParser(br);
        assertTrue(iap.hasNext());
        assertEquals("0, The end should come here.", iap.next());
        assertTrue(iap.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", iap.next());
        assertFalse(iap.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            iap.next();
        });
    }

    @Test
    public void testNextEmptyFile() {
        String words = "\n";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        IteratorAndParser iap = new IteratorAndParser(br);
        assertTrue(iap.hasNext());
        assertEquals("", iap.next());
        assertFalse(iap.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            iap.next();
        });
    }

    @Test
    public void testWriteStringsToFileAppend() throws IOException {
        List<String> strings = new LinkedList<String>();
        strings.add("Hi, this is string 1");
        strings.add("Hello, this is string 2");

        StringReader sr = new StringReader(
                "The end should come here."
        );
        BufferedReader br = new BufferedReader(sr);
        IteratorAndParser tb = new IteratorAndParser(br);
        String filePath = "files/test_file.csv";
        IteratorAndParser.writeStringsToFile(strings, filePath, true);
        BufferedReader br2 = new BufferedReader(new FileReader(filePath));
        String currLine = br2.readLine();
        assertEquals("this is a test", currLine);
        currLine = br2.readLine();
        assertEquals("Hi, this is string 1", currLine);
        currLine = br2.readLine();
        assertEquals("Hello, this is string 2", currLine);
        currLine = br2.readLine();
        assertNull(currLine);
        List<String> resetFileStrings = new LinkedList<String>();
        resetFileStrings.add("this is a test");
        IteratorAndParser.writeStringsToFile(resetFileStrings, filePath, false);
    }


}
