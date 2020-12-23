import java.util.Arrays;

public class Deck {
    private Card[][] deck;

    public Deck() {
        //populates a deck with all 52 cards
        deck = new Card[4][13];
        shuffle();
    }

    public Card drawCard() {
        Card card;
        Card[][] copy = Arrays.copyOf(deck, deck.length);

        int suitValue = (int) (Math.random() * 4);
        int value = (int) (Math.random() * 13);

        while (deck[suitValue][value] == null) {
            suitValue = (int) (Math.random() * 4);
            value = (int) (Math.random() * 13);
        }

        card = new Card(value + 1, suitValue);
        deck[suitValue][value] = null;

        return card;
    }

    public Card drawCard(String name, String suit) {
        /*this method will return the card regardless of whether it has been drawn or not,
         * which means it is best to use this BEFORE drawing any card randomly*/
        Card card;

        int value = 0;
        int suitValue = 0;

        if (name == "ace") {
            value = 1;
        } else if (name == "king") {
            value = 13;
        } else if (name == "queen") {
            value = 12;
        } else if (name == "jack") {
            value = 11;
        } else {
            value = Integer.parseInt(name);
        }

        if (suit == "diamonds") {
            suitValue = 0;
        } else if (suit == "hearts") {
            suitValue = 1;
        } else if (suit == "spades") {
            suitValue = 2;
        } else {
            suitValue = 3;
        }

        card = new Card(value, suitValue);

        deck[suitValue][value - 1] = null;
        return card;
    }

    public void printDeck() {
        for (int i = 0; i < deck.length; i++) {
            for (int j = 0; j < deck[i].length; j++) {
                System.out.println(deck[i][j]);
            }
            System.out.println();
        }
    }

    public void shuffle() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                deck[i][j] = new Card(j + 1, i);
            }
        }
    }
}
