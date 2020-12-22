import java.util.Arrays;

public class Player {
    private int playerNum;
    private Deck deck;
    private Card[] hand;
    private Card[] possCards;
    private Card[] madeHand;
    private int[] counter = new int[15];
    private int[] suitCounter = new int[4];
    private int[][] specialCounter = new int[4][15];
    private String madeHandName;

    public Player(int playerNum, Deck deck) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.deck = deck;
    }

    public Card[] makeMadeHand() {
        Arrays.fill(madeHand, null);

        if (detectRoyalFlush()) {
            straightFlush();
            madeHandName = "ROYAL FLUSH";
        } else if (detectStraightFlush()) {
            straightFlush();
            madeHandName = "STRAIGHT FLUSH";
        } else if (detectFour()) {
            four();
            madeHandName = "FOUR OF A KIND";
        } else if (detectFullHouse()) {
            fullHouse();
            madeHandName = "FULL HOUSE";
        } else if (detectFlush()) {
            flush();
            madeHandName = "FLUSH";
        } else if (detectStraight()) {
            straight();
            madeHandName = "STRAIGHT";
        } else if (detectThree()) {
            three();
            madeHandName = "THREE OF A KIND";
        } else if (detectTwoPair()) {
            twoPair();
            madeHandName = "TWO PAIR";
        } else if (detectPair()) {
            pair();
            madeHandName = "PAIR";
        } else {
            highCard();
            madeHandName = "HIGH CARD";
        }
        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public void printMadeHand() {
        System.out.println("Player " + playerNum + " made hand: " + madeHandName);
        for (int i = 0; i < madeHand.length; i++) {
            System.out.println(madeHand[i]);
        }
        System.out.println();
    }

    public String getMadeHandName() {
        return madeHandName;
    }

    public boolean detectRoyalFlush() {
        for (int i = 0; i < specialCounter.length; i++) {
            if (specialCounter[i][14] == 1 &&
                    specialCounter[i][13] == 1 &&
                    specialCounter[i][12] == 1 &&
                    specialCounter[i][11] == 1 &&
                    specialCounter[i][10] == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean detectStraightFlush() {
        //specialCounter is a 2d array of ints with where a 1 maps the suit and value of a card
        int count = 0;
        for (int i = 0; i < specialCounter.length; i++) {
            for (int j = specialCounter[i].length - 1; j > 0; j--) {
                if (specialCounter[i][j] >= 1 && specialCounter[i][j - 1] >= 1) {
                    count++;
                } else {
                    count = 0;
                }

                if (count >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public void straightFlush() {
        straight();

        int suitValue = 0;
        int suitArraySize = 0;
        for (int i = 0; i < suitCounter.length; i++) {
            if (suitCounter[i] >= 5) {
                suitValue = i;
                suitArraySize = suitCounter[i];
            }
        }

        Card[] updatedPossCards = new Card[suitArraySize];

        int count = 0;
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getSuitValue() == suitValue) {
                updatedPossCards[count] = possCards[i];
                count++;
                if (count == updatedPossCards.length) {
                    break;
                }
            }
        }

        count = 0;
        for (int i = 0; i < updatedPossCards.length - 1; i++) {
            if (updatedPossCards[i].getValue() - 1 == updatedPossCards[i + 1].getValue()) {
                madeHand[count] = updatedPossCards[i];
                madeHand[count + 1] = updatedPossCards[i + 1];
                count++;
                if (count >= 4) {
                    break;
                }
            }
        }
    }

    public boolean detectFour() {
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] == 4) {
                return true;
            }
        }
        return false;
    }

    public void four() {
        int fourValue = -1;

        for (int i = 0; i < counter.length; i++) {
            if (counter[i] == 4) {
                fourValue = i;
            }
        }

        int count = 0;
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() == fourValue) {
                madeHand[count] = possCards[i];
                count++;
                if (count >= 4) {
                    break;
                }
            }
        }

        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() != fourValue) {
                madeHand[count] = possCards[i];
                break;
            }
        }
    }

    public boolean detectFullHouse() {
        int pairCount = 0;
        int threeCount = 0;
        for (int i = counter.length - 2; i > 1; i--) {
            if (counter[i] == 2) {
                pairCount++;
            } else if (counter[i] == 3) {
                threeCount++;
            }
        }

        if ((pairCount >= 1 && threeCount >= 1) || threeCount == 2) {
            return true;
        }
        return false;
    }

    public void fullHouse() {
        int threeValue = -1;
        int twoValue = -1;

        //looks for the value of the set of three
        for (int i = counter.length - 1; i > 0; i--) {
            if (counter[i] == 3) {
                threeValue = i;
                break;
            }
        }

        //looks for the value of the set of two
        for (int i = counter.length - 1; i > 0; i--) {
            if (counter[i] == 2) {
                twoValue = i;
                break;
            }
        }

        //deals with an edge case where there are two sets of three in possCards
        if (twoValue == -1) {
            for (int i = counter.length - 1; i > 0; i--) {
                if (counter[i] == 3 && i != threeValue) {
                    twoValue = i;
                    break;
                }
            }
        }

        //begins populating the hand array with the set of 3
        int count = 0;
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() == threeValue) {
                madeHand[count] = possCards[i];
                count++;
                if (count == 3) {
                    break;
                }
            }
        }

        //finishes populating the hand array with the set of 2
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() == twoValue) {
                madeHand[count] = possCards[i];
                count++;
                if (count == 5) {
                    break;
                }
            }
        }
    }

    public boolean detectFlush() {
        for (int i = 0; i < suitCounter.length; i++) {
            if (suitCounter[i] >= 5) {
                return true;
            }
        }
        return false;
    }

    public void flush() {
        //finds what suit value the flush is
        int suitValue = -1;
        for (int i = 0; i < suitCounter.length; i++) {
            if (suitCounter[i] >= 5) {
                suitValue = i;
            }
        }

        //populates the flush array from possCards based on suit value
        int count = 0;
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getSuitValue() == suitValue) {
                madeHand[count] = possCards[i];
                count++;
                if (count >= 5) {
                    break;
                }
            }
        }
    }

    public boolean detectStraight() {
        int count = 0;
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] >= 1 && counter[i - 1] >= 1) {
                count++;
            } else {
                count = 0;
            }

            if (count >= 4) {
                return true;
            }
        }
        return false;
    }

    public void straight() {
        int[] index = new int[5];

        //creates an index of which values are involved in the straight
        int count = 0;
        for (int i = counter.length - 1; i > 0; i--) {
            if (counter[i] >= 1 && counter[i - 1] >= 1) {
                index[count] = i;
                if (count == 3) {
                    count++;
                    index[count] = i - 1;
                }
                count++;
            } else {
                count = 0;
                for (int j = 0; j < index.length; j++) {
                    index[j] = 0;
                }
            }

            if (count >= 4) {
                break;
            }
        }

        //populates the straight hand array with the cards in the straight based on the index
        for (int i = 0; i < madeHand.length; i++) {
            for (int j = 0; j < possCards.length; j++) {
                if (possCards[j].getValue() == index[i]) {
                    madeHand[i] = possCards[j];
                    break;
                }
            }
        }

        //in the case that it is a 5 high straight, ace will not be added to the bottom with the above array.
        //this just adds it manually
        if (madeHand[4] == null) {
            madeHand[4] = possCards[0];
        }
    }

    public boolean detectThree() {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 3) {
                return true;
            }
        }
        return false;
    }

    public void three() {
        //grabs the highest set of 3 and puts in the array threeHand
        for (int i = 0; i < possCards.length - 2; i++) {
            if (possCards[i].getValue() == possCards[i + 1].getValue() &&
                    possCards[i + 1].getValue() == possCards[i + 2].getValue()) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                madeHand[2] = possCards[i + 2];
                break;
            }
        }

        //finishes populating the hand with the two highest remaining cards
        int count = 3;
        for (int i = 0; i < possCards.length; i++) {
            if (count < 5 && !containsCard(madeHand, possCards[i])) {
                madeHand[count] = possCards[i];
                count++;
            }
        }
    }

    public boolean detectTwoPair() {
        int count = 0;
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 2) {
                count++;
                if (count == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public void twoPair() {
        //pulls the first pair out of the sorted possCards array
        for (int i = 0; i < possCards.length - 1; i++) {
            if (possCards[i].getValue() == possCards[i + 1].getValue() &&
                    !containsCard(madeHand, possCards[i]) &&
                    !containsCard(madeHand, possCards[i + 1])) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                break;
            }
        }

        //pulls the second pair out of the sorted possCards array
        for (int i = 0; i < possCards.length - 1; i++) {
            if (possCards[i].getValue() == possCards[i + 1].getValue() &&
                    !containsCard(madeHand, possCards[i]) &&
                    !containsCard(madeHand, possCards[i + 1])) {
                madeHand[2] = possCards[i];
                madeHand[3] = possCards[i + 1];
                break;
            }
        }

        //uses the highest card left in possCards as the kicker
        for (int i = 0; i < possCards.length; i++) {
            if (!containsCard(madeHand, possCards[i])) {
                madeHand[4] = possCards[i];
                break;
            }
        }
    }

    public boolean detectPair() {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 2) {
                return true;
            }
        }
        return false;
    }

    public void pair() {
        for (int i = 0; i < possCards.length - 1; i++) {
            if (possCards[i].getValue() == possCards[i + 1].getValue()) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                break;
            }
        }

        int count = 2;
        for (int i = 0; i < possCards.length; i++) {
            if (!containsCard(madeHand, possCards[i])) {
                madeHand[count] = possCards[i];
                count++;
                if (count == 5) {
                    break;
                }
            }
        }
    }

    public void highCard() {
        for (int i = 0; i < madeHand.length; i++) {
            madeHand[i] = possCards[i];
        }
    }

    public boolean containsCard(Card[] cards, Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null && cards[i].equals(card)) {
                return true;
            }
        }
        return false;
    }

    public void createToolArrays(Card[] board) {
        //reset all these arrays
        Arrays.fill(possCards, null);
        Arrays.fill(counter, 0);
        Arrays.fill(suitCounter, 0);
        for (int i = 0; i < suitCounter.length; i++) {
            Arrays.fill(specialCounter[i], 0);
        }

        //populates possCards
        //only call this method after the river has been dealt
        for (int i = 0; i < possCards.length; i++) {
            if (i == 0) {
                possCards[0] = hand[0];
            } else if (i == 1) {
                possCards[1] = hand[1];
            } else {
                if (board[i - 2] != null) {
                    possCards[i] = board[i - 2];
                }
            }
        }

        //sorts possCards from highest val to lowest val
        for (int i = 0; i < possCards.length; i++) {
            for (int j = 0; j < possCards.length; j++) {
                if (j != i && possCards[i].getValue() > possCards[j].getValue()) {
                    Card temp = possCards[i];
                    possCards[i] = possCards[j];
                    possCards[j] = temp;
                }
            }
        }

        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() == 14) {
                counter[1]++;
            }
            counter[possCards[i].getValue()]++;
        }

        for (int i = 0; i < possCards.length; i++) {
            suitCounter[possCards[i].getSuitValue()]++;
        }

        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i].getValue() == 14) {
                specialCounter[possCards[i].getSuitValue()][1]++;
            }
            specialCounter[possCards[i].getSuitValue()][possCards[i].getValue()]++;
        }
    }

    public void printPossCards() {
        System.out.println("Player " + playerNum + " possible cards:");
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i] != null) {
                System.out.println(possCards[i]);
            }
        }
        System.out.println();
    }

    public void drawHand() {
        hand[0] = deck.drawCard();
        hand[1] = deck.drawCard();
    }

    public void drawHand(String value1, String suit1, String value2, String suit2) {
        hand[0] = deck.drawCard(value1, suit1);
        hand[1] = deck.drawCard(value2, suit2);
    }

    public Card[] getHand() {
        return Arrays.copyOf(hand, hand.length);
    }

    public void printHand() {
        System.out.println("Player " + playerNum + " hand:");
        for (int i = 0; i < hand.length; i++) {
            System.out.println(hand[i]);
        }
        System.out.println();
    }

    public int compareHands(Player playerToCompare) {
        if (handValue() > playerToCompare.handValue()) {
            return 1;
        } else if (playerToCompare.handValue() > handValue()) {
            return -1;
        } else {
            if (madeHandName.equals("STRAIGHT FLUSH")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (madeHandName.equals("FOUR OF A KIND")) {
                if (lastValue() > playerToCompare.lastValue()) {
                    return 1;
                } else if (playerToCompare.lastValue() > lastValue()) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (madeHandName.equals("FULL HOUSE")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    if (lastValue() > playerToCompare.lastValue()) {
                        return 1;
                    } else if (playerToCompare.lastValue() > lastValue()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else if (madeHandName.equals("FLUSH")) {
                if (highCardValue(0, playerToCompare.makeMadeHand()) == 1) {
                    return 1;
                } else if (highCardValue(0, playerToCompare.makeMadeHand()) == -1) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (madeHandName.equals("STRAIGHT")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (madeHandName.equals("THREE")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    if (highCardValue(3, playerToCompare.makeMadeHand()) == 1) {
                        return 1;
                    } else if (highCardValue(3, playerToCompare.makeMadeHand()) == -1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else if (madeHandName.equals("TWO PAIR")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    if (thirdValue() > playerToCompare.thirdValue()) {
                        return 1;
                    } else if (playerToCompare.thirdValue() > thirdValue()) {
                        return -1;
                    } else {
                        if (highCardValue(4, playerToCompare.makeMadeHand()) == 1) {
                            return 1;
                        } else if (highCardValue(4, playerToCompare.makeMadeHand()) == -1) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            } else if (madeHandName.equals("PAIR")) {
                if (firstValue() > playerToCompare.firstValue()) {
                    return 1;
                } else if (playerToCompare.firstValue() > firstValue()) {
                    return -1;
                } else {
                    if (highCardValue(2, playerToCompare.makeMadeHand()) == 1) {
                        return 1;
                    } else if (highCardValue(2, playerToCompare.makeMadeHand()) == -1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else {
                if (highCardValue(0, playerToCompare.makeMadeHand()) == 1) {
                    return 1;
                } else if (highCardValue(0, playerToCompare.makeMadeHand()) == -1) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public int handValue() {
        if (madeHandName.equals("ROYAL FLUSH")) {
            return 9;
        } else if (madeHandName.equals("STRAIGHT FLUSH")) {
            return 8;
        } else if (madeHandName.equals("FOUR OF A KIND")) {
            return 7;
        } else if (madeHandName.equals("FULL HOUSE")) {
            return 6;
        } else if (madeHandName.equals("FLUSH")) {
            return 5;
        } else if (madeHandName.equals("STRAIGHT")) {
            return 4;
        } else if (madeHandName.equals("THREE OF A KIND")) {
            return 3;
        } else if (madeHandName.equals("TWO PAIR")) {
            return 2;
        } else if (madeHandName.equals("PAIR")) {
            return 1;
        } else {
            return 0;
        }
    }

    public int firstValue() {
        return madeHand[0].getValue();
    }

    public int thirdValue() {
        return madeHand[2].getValue();
    }

    public int lastValue() {
        return madeHand[4].getValue();
    }

    public int highCardValue(int startingVal, Card[] toCompare) {
        for (int i = startingVal; i < 5; i++) {
            if (madeHand[i].getValue() > toCompare[i].getValue()) {
                return 1;
            } else if (toCompare[i].getValue() > madeHand[i].getValue()) {
                return -1;
            }
        }
        return 0;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    //for testing purposes only, delete when done:
    public int[] getCounter() {
        return Arrays.copyOf(counter, counter.length);
    }

    public int[] getSuitCounter() {
        return Arrays.copyOf(suitCounter, suitCounter.length);
    }
}
