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

        if(detectThree()) {
            three();
            madeHandName = "THREE OF A KIND";
        } else if(detectTwoPair()) {
            twoPair();
            madeHandName = "TWO PAIR";
        } else if(detectPair()) {
            pair();
            madeHandName = "PAIR";
        } else {
            highCard();
        }
        return Arrays.copyOf(madeHand, madeHand.length);
    }
    public void printMadeHand() {
        System.out.println("Player " + playerNum + " made hand: " + madeHandName);
        for(int i = 0; i < madeHand.length; i++) {
            System.out.println(madeHand[i]);
        }
        System.out.println();
    }
    public String getMadeHandName() {
        return madeHandName;
    }

    public boolean detectThree() {
        for(int i = 0; i < counter.length; i++) {
            if(counter[i] == 3) {
                return true;
            }
        }
        return false;
    }
    public void three() {
        //grabs the highest set of 3 and puts in the array threeHand
        for(int i = 0; i < possCards.length - 2; i++) {
            if(possCards[i].getValue() == possCards[i + 1].getValue() &&
                    possCards[i + 1].getValue() == possCards[i + 2].getValue()) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                madeHand[2] = possCards[i + 2];
                break;
            }
        }

        //finishes populating the hand with the two highest remaining cards
        int count = 3;
        for(int i = 0; i < possCards.length; i++) {
            if(count < 5 && !containsCard(madeHand, possCards[i])) {
                madeHand[count] = possCards[i];
                count++;
            }
        }
    }
    public boolean detectTwoPair() {
        int count = 0;
        for(int i = 0; i < counter.length - 2; i++) {
            if(counter[i] == 2) {
                count++;
                if(count == 2) {
                    return true;
                }
            }
        }
        return false;
    }
    public void twoPair() {
        //pulls the first pair out of the sorted possCards array
        for(int i = 0; i < possCards.length - 1; i++) {
            if(possCards[i].getValue() == possCards[i + 1].getValue() &&
                    !containsCard(madeHand, possCards[i]) &&
                    !containsCard(madeHand, possCards[i + 1])) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                break;
            }
        }

        //pulls the second pair out of the sorted possCards array
        for(int i = 0; i < possCards.length - 1; i++) {
            if(possCards[i].getValue() == possCards[i + 1].getValue() &&
                    !containsCard(madeHand, possCards[i]) &&
                    !containsCard(madeHand, possCards[i + 1])) {
                madeHand[2] = possCards[i];
                madeHand[3] = possCards[i + 1];
                break;
            }
        }

        //uses the highest card left in possCards as the kicker
        for(int i = 0; i < possCards.length; i++) {
            if(!containsCard(madeHand, possCards[i])) {
                madeHand[4] = possCards[i];
                break;
            }
        }
    }
    public boolean detectPair() {
        for(int i = 0; i < counter.length; i++) {
            if(counter[i] == 2) {
                return true;
            }
        }
        return false;
    }
    public void pair() {
        for(int i = 0; i < possCards.length - 1; i++) {
            if(possCards[i].getValue() == possCards[i + 1].getValue()) {
                madeHand[0] = possCards[i];
                madeHand[1] = possCards[i + 1];
                break;
            }
        }

        int count = 2;
        for(int i = 0; i < possCards.length; i++) {
            if(!containsCard(madeHand, possCards[i])) {
                madeHand[count] = possCards[i];
                count++;
                if(count == 5) {
                    break;
                }
            }
        }
    }
    public void highCard() {
        for(int i = 0; i < madeHand.length; i++) {
            madeHand[i] = possCards[i];
        }
        this.madeHandName = "HIGH CARD";
    }

    public boolean containsCard(Card[] cards, Card card) {
        for(int i = 0; i < cards.length; i++) {
            if(cards[i] != null && cards[i].equals(card)) {
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
        for(int i = 0; i < suitCounter.length; i++) {
            Arrays.fill(specialCounter[i], 0);
        }

        //populates possCards
        //only call this method after the river has been dealt
        for(int i = 0; i < possCards.length; i++) {
            if(i == 0) {
                possCards[0] = hand[0];
            } else if(i == 1) {
                possCards[1] = hand[1];
            } else {
                if(board[i - 2] != null) {
                    possCards[i] = board[i - 2];
                }
            }
        }

        //sorts possCards from highest val to lowest val
        for(int i = 0; i < possCards.length; i++) {
            for(int j = 0; j < possCards.length; j++) {
                if(j != i && possCards[i].getValue() > possCards[j].getValue()) {
                    Card temp = possCards[i];
                    possCards[i] = possCards[j];
                    possCards[j] = temp;
                }
            }
        }

        for(int i = 0; i < possCards.length; i++) {
            if(possCards[i].getValue() == 14) {
                counter[1]++;
            }
            counter[possCards[i].getValue()]++;
        }

        for(int i = 0; i < possCards.length; i++) {
            suitCounter[possCards[i].getSuitValue()]++;
        }

        for(int i = 0; i < possCards.length; i++) {
            if(possCards[i].getValue() == 14) {
                specialCounter[possCards[i].getSuitValue()][1]++;
            }
            specialCounter[possCards[i].getSuitValue()][possCards[i].getValue()]++;
        }
    }
    public void printPossCards() {
        System.out.println("Player " + playerNum + " possible cards:");
        for(int i = 0; i < possCards.length; i ++) {
            if(possCards[i] != null) {
                System.out.println(possCards[i]);
            }
        }
        System.out.println(threeValue());
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
    public void printHand() {
        System.out.println("Player " + playerNum + " hand:");
        for(int i = 0; i < hand.length; i++) {
            System.out.println(hand[i]);
        }
        System.out.println();
    }

    public int compareHands(Player playerToCompare) {
        if(handValue() > playerToCompare.handValue()) {
            return 1;
        } else if(playerToCompare.handValue() > handValue()) {
            return -1;
        } else {
            if (madeHandName.equals("THREE")) {
                if(threeValue() > playerToCompare.threeValue()) {
                    return 1;
                } else if(playerToCompare.threeValue() > threeValue()) {
                    return -1;
                } else {
                    if(highCardValue(3, playerToCompare.makeMadeHand()) == 1) {
                        return 1;
                    } else if(highCardValue(3, playerToCompare.makeMadeHand()) == -1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else if(madeHandName.equals("TWO PAIR")) {
                if(twoPairValue() > playerToCompare.twoPairValue()) {
                    return 1;
                } else if(playerToCompare.twoPairValue() > twoPairValue()) {
                    return -1;
                } else {
                    if(twoPairSecondPairValue() > playerToCompare.twoPairSecondPairValue()) {
                        return 1;
                    } else if(playerToCompare.twoPairSecondPairValue()> twoPairSecondPairValue()) {
                        return -1;
                    } else {
                        if(highCardValue(4, playerToCompare.makeMadeHand()) == 1) {
                            return 1;
                        } else if(highCardValue(4, playerToCompare.makeMadeHand()) == -1) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            } else if(madeHandName.equals("PAIR")) {
                if(pairValue() > playerToCompare.pairValue()) {
                    return 1;
                } else if(playerToCompare.pairValue() > pairValue()) {
                    return -1;
                } else {
                    if(highCardValue(2, playerToCompare.makeMadeHand()) == 1) {
                        return 1;
                    } else if(highCardValue(2, playerToCompare.makeMadeHand()) == -1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else {
                if(highCardValue(0, playerToCompare.makeMadeHand()) == 1) {
                    return 1;
                } else if(highCardValue(0, playerToCompare.makeMadeHand()) == -1) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
    public int handValue() {
        if(madeHandName.equals("THREE OF A KIND")) {
            return 3;
        } else if(madeHandName == "TWO PAIR") {
            return 2;
        } else if(madeHandName == "PAIR") {
            return 1;
        } else {
            return 0;
        }
    }
    public int threeValue() {
        return madeHand[0].getValue();
    }
    public int twoPairValue() {
        return madeHand[0].getValue();
    }
    public int twoPairSecondPairValue() {
        return madeHand[2].getValue();
    }
    public int pairValue() {
        return madeHand[0].getValue();
    }
    public int highCardValue(int startingVal, Card[] toCompare) {
        for(int i = startingVal; i < 5; i++) {
            if(madeHand[i].getValue() > toCompare[i].getValue()) {
                return 1;
            } else if(toCompare[i].getValue() > madeHand[i].getValue()) {
                return -1;
            }
        }
        return 0;
    }

    //for testing purposes only, delete when done:
    public int[] getCounter() {
        return Arrays.copyOf(counter, counter.length);
    }
}
