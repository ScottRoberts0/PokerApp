import java.util.Arrays;

public class Evaluator {
    public static int findWinner(Player[] players, Card[] board) {
        int winner = -1;
        int winningHandValue = -1;
        Card[][] playerMadeHands = new Card[players.length][7];

        for (int i = 0; i < playerMadeHands.length; i++) {
            playerMadeHands[i] = makeMadeHand(players[i], board);
        }

        /*for(int i = 0; i < playerMadeHands.length; i++) {
            System.out.println("Player " + players[i].getPlayerNum() + " made hand:");
            for(int j = 0; j < playerMadeHands[i].length; j++) {
                System.out.println(playerMadeHands[i][j]);
            }
            System.out.println();
        }*/

        for(int i = 0; i < players.length; i++) {
            if(handValue(players[i], board) > winningHandValue) {
                winningHandValue = handValue(players[i], board);
                winner = players[i].getPlayerNum();
                if(winningHandValue == handValue(players[i], board)) {
                    //if the winning hand values are equal (there is a tie) we need to do something
                }
            }
        }

        return winner;
    }

    public static int handValue(Player player, Card[] board) {
        Card[] possCards = new Card[7];
        int[] counter = new int[15];
        int[] suitCounter = new int[4];
        int[][] specialCounter = new int[4][15];

        createToolArrays(board, player, possCards, counter, suitCounter, specialCounter);

        if(detectRoyalFlush(specialCounter)) {
           return 9;
        } else if(detectStraightFlush(specialCounter)) {
            return 8;
        } else if(detectFour(counter)) {
            return 7;
        } else if(detectFullHouse(counter)) {
            return 6;
        } else if(detectFlush(suitCounter)) {
            return 5;
        } else if(detectStraight(counter)) {
            return 4;
        } else if(detectThree(counter)) {
            return 3;
        } else if(detectTwoPair(counter)) {
            return 2;
        } else if(detectPair(counter)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Card[] makeMadeHand(Player player, Card[] board) {
        Card[] madeHand = new Card[5];
        Card[] possCards = new Card[7];
        int[] counter = new int[15];
        int[] suitCounter = new int[4];
        int[][] specialCounter = new int[4][15];

        createToolArrays(board, player, possCards, counter, suitCounter, specialCounter);

        if(detectRoyalFlush(specialCounter)) {
            straightFlush(counter, suitCounter, possCards, madeHand);
        } else if(detectStraightFlush(specialCounter)) {
            straightFlush(counter, suitCounter, possCards, madeHand);
        } else if(detectFour(counter)) {
            four(counter, possCards, madeHand);
        } else if(detectFullHouse(counter)) {
            fullHouse(counter, possCards, madeHand);
        } else if(detectFlush(suitCounter)) {
            flush(suitCounter, possCards, madeHand);
        } else if(detectStraight(counter)) {
            straight(counter, possCards, madeHand);
        } else if(detectThree(counter)) {
            three(possCards, madeHand);
        } else if(detectTwoPair(counter)) {
            twoPair(possCards, madeHand);
        } else if(detectPair(counter)) {
            pair(possCards, madeHand);
        } else {
            highCard(possCards, madeHand);
        }

        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public static void createToolArrays(Card[] board, Player player, Card[] possCards, int[] counter, int[] suitCounter, int[][] specialCounter) {
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
                possCards[0] = player.getHand()[0];
            } else if(i == 1) {
                possCards[1] = player.getHand()[1];
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

    public static boolean detectRoyalFlush(int[][] specialCounter) {
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

    public static boolean detectStraightFlush(int[][] specialCounter) {
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

    public static void straightFlush(int[] counter, int[] suitCounter, Card[] possCards, Card[] madeHand) {
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

    public static boolean detectFour(int[] counter) {
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] == 4) {
                return true;
            }
        }
        return false;
    }

    public static void four(int[] counter, Card[] possCards, Card[] madeHand) {
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

    public static boolean detectFullHouse(int[] counter) {
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

    public static void fullHouse(int[] counter, Card[] possCards, Card[] madeHand) {
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

    public static boolean detectFlush(int[] suitCounter) {
        for (int i = 0; i < suitCounter.length; i++) {
            if (suitCounter[i] >= 5) {
                return true;
            }
        }
        return false;
    }

    public static void flush(int[] suitCounter, Card[] possCards, Card[] madeHand) {
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

    public static boolean detectStraight(int[] counter) {
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

    public static void straight(int[] counter, Card[] possCards, Card[] madeHand) {
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

    public static boolean detectThree(int[] counter) {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 3) {
                return true;
            }
        }
        return false;
    }

    public static void three(Card[] possCards, Card[] madeHand) {
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

    public static boolean detectTwoPair(int[] counter) {
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

    public static void twoPair(Card[] possCards, Card[] madeHand) {
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

    public static boolean detectPair(int[] counter) {
        for (int i = counter.length - 1; i > 1; i--) {
            if (counter[i] == 2) {
                return true;
            }
        }
        return false;
    }

    public static void pair(Card[] possCards, Card[] madeHand) {
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

    public static void highCard(Card[] possCards, Card[] madeHand) {
        for (int i = 0; i < madeHand.length; i++) {
            madeHand[i] = possCards[i];
        }
    }

    public static boolean containsCard(Card[] cards, Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null && cards[i].equals(card)) {
                return true;
            }
        }
        return false;
    }
}
