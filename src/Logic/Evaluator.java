package Logic;
import java.util.Arrays;

import java.util.Arrays;
import java.util.ArrayList;

public class Evaluator {

    public static ArrayList<Player> findWinner(Card[] board, Pot pot) {
        ArrayList<Player> players = pot.getPlayersInPot();
        ArrayList<Player> winningPlayers = new ArrayList<>();
        boolean[] winners = new boolean[pot.getNumPlayersInPot()];
        Card[][] madeHands = new Card[pot.getNumPlayersInPot()][5];

        for (int i = 0; i < madeHands.length; i++) {
            if(pot.containsPlayer(players.get(i))) {
                madeHands[i] = players.get(i).makeMadeHand(board);
            }
        }

        int[] handValues = new int[pot.getNumPlayersInPot()];
        for (int i = 0; i < handValues.length; i++) {
            if(pot.containsPlayer(players.get(i))) {
                handValues[i] = players.get(i).getMadeHandValue();
            }
        }

        int winningHandValue = -1;
        int winnerCount = 1;

        for (int i = 0; i < handValues.length; i++) {
            if (handValues[i] > winningHandValue) {
                Arrays.fill(winners, false);
                winners[i] = true;
                winningHandValue = handValues[i];
                winnerCount = 1;
            } else if (handValues[i] == winningHandValue) {
                winners[i] = true;
                winnerCount++;
            }
        }

        for (int i = 0; i < handValues.length; i++) {
            if (!winners[i]) {
                handValues[i] = -1;
            }
        }

        if (winnerCount > 1) {
            if (winningHandValue == 8 || winningHandValue == 9) {
                winners = findWinnerStraightFlush(madeHands, handValues);
            } else if (winningHandValue == 7) {
                winners = findWinnerFour(madeHands, handValues);
            } else if (winningHandValue == 6) {
                winners = findWinnerFullHouse(madeHands, handValues);
            } else if (winningHandValue == 5) {
                winners = findWinnerFlush(madeHands, handValues);
            } else if (winningHandValue == 4) {
                winners = findWinnerStraight(madeHands, handValues);
            } else if (winningHandValue == 3) {
                winners = findWinnerThree(madeHands, handValues);
            } else if (winningHandValue == 2) {
                winners = findWinnerTwoPair(madeHands, handValues);
            } else if (winningHandValue == 1) {
                winners = findWinnerPair(madeHands, handValues);
            } else if (winningHandValue == 0) {
                winners = findWinnerHighCards(madeHands, 0, handValues, 0);
            }
        }

        for(int i = 0; i < winners.length; i++) {
            if(winners[i]) {
                winningPlayers.add(players.get(i));
            }
        }

        return winningPlayers;
    }

    public static Card[] makeMadeHand(Card[] possCards) {
        Card[] madeHand = new Card[5];
        int[] counter = new int[15];
        int[] suitCounter = new int[4];
        int[][] specialCounter = new int[4][15];

        createToolArrays(possCards, counter, suitCounter, specialCounter);

        if (detectRoyalFlush(specialCounter)) {
            straightFlush(counter, suitCounter, possCards, madeHand);
        } else if (detectStraightFlush(specialCounter)) {
            straightFlush(counter, suitCounter, possCards, madeHand);
        } else if (detectFour(counter)) {
            four(counter, possCards, madeHand);
        } else if (detectFullHouse(counter)) {
            fullHouse(counter, possCards, madeHand);
        } else if (detectFlush(suitCounter)) {
            flush(suitCounter, possCards, madeHand);
        } else if (detectStraight(counter)) {
            straight(counter, possCards, madeHand);
        } else if (detectThree(counter)) {
            three(possCards, madeHand);
        } else if (detectTwoPair(counter)) {
            twoPair(possCards, madeHand);
        } else if (detectPair(counter)) {
            pair(possCards, madeHand);
        } else {
            highCard(possCards, madeHand);
        }

        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public static String getMadeHandName(Card[] madeHand) {
        int[] counter = new int[15];
        int[] suitCounter = new int[4];
        int[][] specialCounter = new int[4][15];

        createToolArrays(madeHand, counter, suitCounter, specialCounter);

        if (detectRoyalFlush(specialCounter)) {
            return "ROYAL FLUSH";
        } else if (detectStraightFlush(specialCounter)) {
            return "STRAIGHT FLUSH";
        } else if (detectFour(counter)) {
            return "FOUR OF A KIND";
        } else if (detectFullHouse(counter)) {
            return "FULL HOUSE";
        } else if (detectFlush(suitCounter)) {
            return "FLUSH";
        } else if (detectStraight(counter)) {
            return "STRAIGHT";
        } else if (detectThree(counter)) {
            return "THREE OF A KIND";
        } else if (detectTwoPair(counter)) {
            return "TWO PAIR";
        } else if (detectPair(counter)) {
            return "PAIR";
        } else {
            return "HIGH CARD";
        }
    }

    public static int getMadeHandValue(Card[] madeHand) {
        int[] counter = new int[15];
        int[] suitCounter = new int[4];
        int[][] specialCounter = new int[4][15];

        createToolArrays(madeHand, counter, suitCounter, specialCounter);

        if (detectRoyalFlush(specialCounter)) {
            return 9;
        } else if (detectStraightFlush(specialCounter)) {
            return 8;
        } else if (detectFour(counter)) {
            return 7;
        } else if (detectFullHouse(counter)) {
            return 6;
        } else if (detectFlush(suitCounter)) {
            return 5;
        } else if (detectStraight(counter)) {
            return 4;
        } else if (detectThree(counter)) {
            return 3;
        } else if (detectTwoPair(counter)) {
            return 2;
        } else if (detectPair(counter)) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void createToolArrays(Card[] possCards, int[] counter, int[] suitCounter, int[][] specialCounter) {
        //reset all these arrays
        Arrays.fill(counter, 0);
        Arrays.fill(suitCounter, 0);
        for (int i = 0; i < suitCounter.length; i++) {
            Arrays.fill(specialCounter[i], 0);
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



    private static boolean detectRoyalFlush(int[][] specialCounter) {
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

    private static boolean detectStraightFlush(int[][] specialCounter) {
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

    private static void straightFlush(int[] counter, int[] suitCounter, Card[] possCards, Card[] madeHand) {
        straight(counter, possCards, madeHand);

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

    private static boolean[] findWinnerStraightFlush(Card[][] madeHands, int[] handValues) {
        int highStraightValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 8 || handValues[i] == 9) {
                if (madeHands[i][0].getValue() > highStraightValue) {
                    highStraightValue = madeHands[i][0].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highStraightValue) {
                    winners[i] = true;
                }
            }
        }

        return winners;
    }



    private static boolean detectFour(int[] counter) {
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] == 4) {
                return true;
            }
        }
        return false;
    }

    private static void four(int[] counter, Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerFour(Card[][] madeHands, int[] handValues) {
        boolean[] winners = findWinnerHighCards(madeHands, 4, handValues, 7);
        return winners;
    }



    private static boolean detectFullHouse(int[] counter) {
        int pairCount = 0;
        int threeCount = 0;
        for (int i = counter.length - 1; i > 1; i--) {
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

    private static void fullHouse(int[] counter, Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerFullHouse(Card[][] madeHands, int[] handValues) {
        int highThreeValue = -1;
        int highTwoValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 6) {
                if (madeHands[i][0].getValue() > highThreeValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        //excludes full houses that are lower than the new highThreeValue from future searches
                        if (j != i && madeHands[j][0].getValue() < madeHands[i][0].getValue()) {
                            handValues[j] = -1;
                        }
                    }
                    highThreeValue = madeHands[i][0].getValue();
                    highTwoValue = madeHands[i][3].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highThreeValue && madeHands[i][3].getValue() > highTwoValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        //excludes full houses that are lower than the new highTwoValue from future searches
                        if (j != i && madeHands[j][3].getValue() < madeHands[i][3].getValue()) {
                            handValues[j] = -1;
                        }
                        highTwoValue = madeHands[i][3].getValue();
                        Arrays.fill(winners, false);
                        winners[i] = true;
                    }
                } else if (madeHands[i][0].getValue() == highThreeValue && madeHands[i][3].getValue() == highTwoValue) {
                    winners[i] = true;
                }
            }
        }

        return winners;
    }



    private static boolean detectFlush(int[] suitCounter) {
        for (int i = 0; i < suitCounter.length; i++) {
            if (suitCounter[i] >= 5) {
                return true;
            }
        }
        return false;
    }

    private static void flush(int[] suitCounter, Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerFlush(Card[][] madeHands, int[] handValues) {
        boolean[] winners = findWinnerHighCards(madeHands, 0, handValues, 5);
        return winners;
    }



    private static boolean detectStraight(int[] counter) {
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

    private static void straight(int[] counter, Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerStraight(Card[][] madeHands, int[] handValues) {
        //the only relevant card in determining who wins out of two straights is the highest card,
        //therefore this method looks at the highest card in each made hand that is a straight
        int highStraightValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 4) {
                if (madeHands[i][0].getValue() > highStraightValue) {
                    highStraightValue = madeHands[i][0].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highStraightValue) {
                    winners[i] = true;
                }
            }
        }

        return winners;
    }



    private static boolean detectThree(int[] counter) {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 3) {
                return true;
            }
        }
        return false;
    }

    private static void three(Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerThree(Card[][] madeHands, int[] handValues) {
        int highThreeValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 3) {
                if (madeHands[i][0].getValue() > highThreeValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        //need to exclude hands that have a highThreeValue of the old one from future searches
                        if (j != i && madeHands[j][0].getValue() < madeHands[i][2].getValue()) {
                            handValues[j] = -1;
                        }
                    }
                    highThreeValue = madeHands[i][0].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highThreeValue) {
                    winners = findWinnerHighCards(madeHands, 3, handValues, 3);
                }
            }
        }

        return winners;
    }



    private static boolean detectTwoPair(int[] counter) {
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

    private static void twoPair(Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerTwoPair(Card[][] madeHands, int[] handValues) {
        int highFirstPairValue = -1;
        int highSecondPairValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 2) {
                if (madeHands[i][0].getValue() > highFirstPairValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        if (j != i && madeHands[j][0].getValue() < madeHands[i][0].getValue()) {
                            handValues[j] = -1;
                        }
                    }
                    highFirstPairValue = madeHands[i][0].getValue();
                    highSecondPairValue = madeHands[i][2].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highFirstPairValue && madeHands[i][2].getValue() > highSecondPairValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        if (j != i && madeHands[j][0].getValue() < madeHands[i][2].getValue()) {
                            handValues[j] = -1;
                        }
                    }
                    highSecondPairValue = madeHands[i][2].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highFirstPairValue && madeHands[i][2].getValue() == highSecondPairValue) {
                    winners = findWinnerHighCards(madeHands, 4, handValues, 2);
                }
            }
        }

        for (int x : handValues) {
            System.out.println(x);
        }

        return winners;
    }



    private static boolean detectPair(int[] counter) {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 2) {
                return true;
            }
        }
        return false;
    }

    private static void pair(Card[] possCards, Card[] madeHand) {
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

    private static boolean[] findWinnerPair(Card[][] madeHands, int[] handValues) {
        int highPairValue = -1;
        boolean[] winners = new boolean[madeHands.length];

        for (int i = 0; i < madeHands.length; i++) {
            if (handValues[i] == 1) {
                if (madeHands[i][0].getValue() > highPairValue) {
                    for (int j = 0; j < madeHands.length; j++) {
                        if (j != i && madeHands[j][0].getValue() < madeHands[i][0].getValue()) {
                            handValues[j] = -1;
                        }
                    }
                    highPairValue = madeHands[i][0].getValue();
                    Arrays.fill(winners, false);
                    winners[i] = true;
                } else if (madeHands[i][0].getValue() == highPairValue) {
                    winners = findWinnerHighCards(madeHands, 2, handValues, 1);
                }
            }
        }

        return winners;
    }



    private static void highCard(Card[] possCards, Card[] madeHand) {
        for (int i = 0; i < madeHand.length; i++) {
            madeHand[i] = possCards[i];
        }
    }

    /**
     * Finds the winner based on high card value starting at the value specified (should be after the relevant cards to hand type).
     *
     * @param madeHands  The list of hands to look through.
     * @param cardLevel  The starting index (should be after the relevant cards to hand type).
     * @param handValues The list of hand values that corresponds 1-to-1 to madeHands.
     * @param handValue  The value of the hand type we are looking at.
     * @return Returns an array of booleans that corresponds 1-to-1 to the list of players, where true indicates a winner and false a loser.
     */
    private static boolean[] findWinnerHighCards(Card[][] madeHands, int cardLevel, int[] handValues, int handValue) {
        boolean[] winners = new boolean[madeHands.length];
        int highCardValue = -1;
        int winnerCount = 0;

        for (int i = cardLevel; i < 5; i++) {
            highCardValue = -1;
            winnerCount = 0;

            for (int j = 0; j < madeHands.length; j++) {
                if (handValues[j] == handValue) {
                    if (madeHands[j][i].getValue() > highCardValue) {
                        highCardValue = madeHands[j][i].getValue();
                        Arrays.fill(winners, false);
                        winners[j] = true;
                    } else if (madeHands[j][i].getValue() == highCardValue) {
                        winners[j] = true;
                    }
                }
            }

            for (boolean winner : winners) {
                if (winner) {
                    winnerCount++;
                }
            }

            if (winnerCount == 1) {
                break;
            }
        }

        return winners;
    }

    private static boolean containsCard(Card[] cards, Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null && cards[i].equals(card)) {
                return true;
            }
        }
        return false;
    }
}