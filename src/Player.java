package src;

import java.util.ArrayList;

public class Player {

    private static final String[] suits = {"S", "C", "H", "D"};

    private ArrayList<Card> hand;
    private int playerNum;
    private String holeCards;
    private String handRankName;
    private int handValue;
    private ArrayList<Integer> bestCards;

    public Player(int playerNum) {
        hand = new ArrayList<>();
        this.playerNum = playerNum;
        handValue = 0;
        holeCards = "";
        handRankName = "None";
        bestCards = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            bestCards.add(-1);
        }
    }

    /**
     * See instructions in Schoology
     */
    public void showdown() {
        int handRank = 10;
        while (handRankName.equals("None")) {
            switch (handRank) {
                case 10:
                    if (checkRoyalFlush()) {
                        handRankName = "Royal Flush";
                    }
                    break;
                case 9:
                    if (checkStraightFlush()) {
                        handRankName = "Straight Flush";
                    }
                    break;
                case 8:
                    if (checkFourOfAKind()) {
                        handRankName = "Four of a Kind";
                    }
                    break;
                case 7:
                    if (checkFullHouse()) {
                        handRankName = "Full House";
                    }
                    break;
                case 6:
                    if (checkFlush()) {
                        handRankName = "Flush";
                    }
                    break;
                case 5:
                    if (checkStraight()) {
                        handRankName = "Straight";
                    }
                    break;
                case 4:
                    if (checkThreeOfAKind()) {
                        handRankName = "Three of a Kind";
                    }
                    break;
                case 3:
                    if (checkTwoPair()) {
                        handRankName = "Two Pair";
                    }
                    break;
                case 2:
                    if (checkPair()) {
                        handRankName = "Pair";
                    }
                    break;
                case 1:
                    makeHighCardHand();
                    handRankName = "High Card";
                    break;
            }
            handRank--;
        }
        setHandValue(handRank);
        makeBestHand();
    }

    private void makeBestHand() {
        //Keep highest values + whatever is in the bestCards list
        int keep = 5 - bestCards.size();
        int deleted = 0;
        for (int i = 0, j = 0; i < hand.size(); i++) {
            if (j < bestCards.size() && bestCards.get(j) == i + deleted) {
                j++;
            }
            else if (keep > 0) {
                keep--;
            }
            else {
                hand.remove(i);
                i--;
                deleted++;
            }
        }
    }

    private void makeHighCardHand() {
        resizeBestCards(5);
        for (int i = 0; i < 5; i++) {
            bestCards.set(i, i);
        }
    }

    private int findThreeInFullHouse() {
        if (hand.get(bestCards.get(0)).getValue() == hand.get(bestCards.get(2)).getValue()) {
            return 0;
        }
        return 2;
    }

    private void setHandValue(int handRank) {
        /*Hand Rank value is set using a 6 digit base 15 number
        The first digit is the rank of the hand, second is the most important
        card's value, third is second, etc.*/

        //**First digit**

        handValue += handRank * Math.pow(15, 5);

        //**Special cases**

        //Royal flush needs no further changes
        if (handRank == 9) {
            return;
        }

        //Flush and high card include each of the 5 cards
        if (handRank == 0 || handRank == 5) {
            int j = 4;
            for (int i : bestCards) {
                handValue += hand.get(i).getValue() * Math.pow(15,j);
                j--;
            }
            return;
        }


        //**Digits involving kickers**

        //Full house has specific needs
        if (handRank == 6) {
            int tIndex = findThreeInFullHouse();
            handValue += Math.pow(15, 4) * hand.get(bestCards.get(tIndex)).getValue();
            if (tIndex == 2) {
                handValue += Math.pow(15,3) * hand.get(bestCards.get(0)).getValue();
            }
            else {
                handValue += Math.pow(15,3) * hand.get(bestCards.get(3)).getValue();
            }
            return;
        }
        else {
            handValue += Math.pow(15, 4) * hand.get(bestCards.get(0)).getValue();
            //Straights only need the first value
            if (handRank == 8 || handRank == 4) {
                return;
            }
            else if (handRank == 2) {
                handValue += Math.pow(15, 3) * hand.get(bestCards.get(2)).getValue();
            }
        }

        //**Digits not involving kickers
        int mult = 5 - bestCards.size() - 1;
        int index = 0; //position in hand arrayList
        int i = 0; //position in bestCards arrayList
        while (index < hand.size() && mult >= 0) {
            if (i >= bestCards.size() || index != bestCards.get(i)) {
                handValue += Math.pow(15, mult)*hand.get(index).getValue();
                mult--;
            }
            else if (i < bestCards.size()) {
                i++;
            }
            index++;
        }
    }

    private boolean checkRoyalFlush() {
        findStraightFlush();
        return checkStraightFlush() && hand.get(bestCards.get(0)).getValue() == 14;
    }

    private boolean checkStraightFlush() {
        return bestCards.get(0) != -1;
    }

    private boolean checkPair() {
        findNumOfAKind(2);
        return bestCards.get(0) != -1;
    }

    private boolean checkTwoPair() {
        findNumOfAKind(2);
        findNumOfAKindExcluding(2);
        return bestCards.get(0) != -1;
    }

    private boolean checkStraight() {
        resizeBestCards(5);
        findStraightInHand();
        return bestCards.get(0) != -1;
    }

    private boolean checkFlush() {
        findFlush();
        resizeBestCards(5);
        return bestCards.get(0) != -1;
    }

    private boolean checkFourOfAKind() {
        findNumOfAKind(4);
        return bestCards.get(0) != -1;
    }

    private boolean checkThreeOfAKind() {
        findNumOfAKind(3);
        return bestCards.get(0) != -1;
    }

    private boolean checkFullHouse() {
        findNumOfAKind(3);
        findNumOfAKindExcluding(2);
        return bestCards.get(0) != -1;
    }

    private void findNumOfAKind(int num) {
        resizeBestCards(num);
        int count = 0;
        for (int i = 0; i < hand.size() - num + 1; i++) {
            bestCards.set(0, i);
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getValue() == hand.get(j).getValue()) {
                    bestCards.set(++count, j);
                }
                if (count == num - 1) {
                    return;
                }
            }
            count = 0;
        }
        bestCards.set(0, -1);
    }

    //Finds cards that repeat "num" times excluding those already in the bestCards list
    private void findNumOfAKindExcluding (int num) {
        if (bestCards.get(0) == -1) {
            return;
        }
        int count = 0;
        for (int i = 0, k = 0; i < hand.size() - num + 1; i++) {
            if (k < bestCards.size() && i == bestCards.get(k)) {
                k++;
            }
            else {
                //Find card that repeats num times
                int value = hand.get(i).getValue();
                for (int j = i + 1; j < hand.size(); j++) {
                    if (value == hand.get(j).getValue()) {
                        count++;
                    }
                }

                //If count is reached or exceeded, add indexes to array
                if (count >= num - 1) {
                    for (int j = 0; j < hand.size() && bestCards.size() < 5; j++) {
                        if (hand.get(j).getValue() == value) {
                            int q = 0;
                            while (q < bestCards.size() && j > bestCards.get(q)) {
                                q++;
                            }
                            bestCards.add(q, j);
                        }
                    }
                    return;
                }
                count = 0;
            }
        }
        bestCards.set(0, -1);
    }

    private void resizeBestCards(int length) {
        while (bestCards.size() < length) {
            bestCards.add(-1);
        }
        while (bestCards.size() > length) {
            bestCards.remove(bestCards.size()-1);
        }
    }

    private void findStraightFlush() {
        findFlush();
        findStraightInBestCards();
    }

    private void findFlush() {
        //Find most common suit and frequency of most common suit
        int maxSuit = 0;
        String mostCommonSuit = "";
        for (String suit : suits) {
            int count = 0;
            for (Card c : hand) {
                if (c.getSuit().equals(suit)) {
                    count++;
                }
            }
            if (count > maxSuit && count >= 5) {
                maxSuit = count;
                mostCommonSuit = suit;
            }
        }

        //If a suit with 5 or more cards is not found, return w/o modification
        if (mostCommonSuit.length() == 0) return;

        //Add slots to bestCards if more than 5 cards belong to one suit
        resizeBestCards(maxSuit);

        //Replace -1s with indexes of cards from the suit
        for (int i = 0, j = 0; i < hand.size(); i++) {
            if (hand.get(i).getSuit().equals(mostCommonSuit)) {
                bestCards.set(j++, i);
            }
        }
    }

    private void findStraightInBestCards() {
        //Return array of -1s if there's no flush
        if (bestCards.get(0) == -1) {
            return;
        }

        //Finds start of straight
        int index = -1;
        for (int i = 0; i < bestCards.size() - 4; i++) {
            int value = hand.get(bestCards.get(i)).getValue();
            for (int j = i + 1; j < i + 5; j++) {
                if (hand.get(bestCards.get(j)).getValue() == value - 1) {
                    value--;
                }
                else {
                    break;
                }
                if (j - i == 4) {
                    index = i;
                }
            }
            if (index != -1) {
                break;
            }
        }

        //If no straight is found, return
        if (index < 0) {
            bestCards.set(0, -1);
            return;
        }

        //Remove all cards not part of the straight
        while (index != 0) {
            bestCards.remove(0);
            index--;
        }
        while (bestCards.size() != 5) {
            bestCards.remove(bestCards.size()-1);
        }
    }

    private void findStraightInHand() {
        for (int i = 0; i < hand.size() - 4; i++) {
            int value = hand.get(i).getValue();
            bestCards.set(0, i);
            int index = 1;
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(j).getValue() == value - 1) {
                    value--;
                    bestCards.set(index++, j);
                }
                if (index == 5) {
                    return;
                }
            }
        }
        bestCards.set(0, -1);
    }

    public void addCard(Card card) {
        // inserts cards into hand in sorted order
        int i = 0;
        while (i < hand.size() && hand.get(i).compareTo(card) > 0) {
            i++;
        }
        hand.add(i, card);

        // save string output of the private (hole) cards (first 2 cards dealt).
        // This is needed for ouput in the toString() method.
        if (hand.size() == 2) {
            holeCards = hand.toString();
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getHandValue() {
        return handValue;
    }

    public String getName() {
        return String.format("Player%3d", playerNum);
    }

    public String getHandRankName() {
        return handRankName;
    }

    @Override
    public String toString() {
        return String.format("%-8s = %-7d  %-10s  %-24s  %s",
                getName(), handValue, holeCards, hand, handRankName);
    }
}