package Logic;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        //populates a deck with all 52 cards
        deck = new ArrayList<>(52);
        shuffle();
    }

    public Card drawCard() {
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }

    /**
     * This method will draw a specified card from the deck
     * @param value 1 for Aces, 11 for Jacks, 12 for Queens, 13 for Kings
     * @param suitValue 0 for Hearts, 1 for Diamonds, 2 for Clubs, 3 for Spades
     * @return
     */
    public Card drawCard(int value, int suitValue) {
        Card card = new Card(value, suitValue);
        boolean changed = false;

        for(Card c : deck) {
            if(card.equals(c)) {
                card = c;
                changed = true;
                break;
            }
        }

        if(changed) {
            return card;
        } else {
            throw new NullPointerException("Card not in deck");
        }
    }

    public void shuffle() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 13; j++) {
                deck.add(new Card(j + 1, i));
            }
        }

        Collections.shuffle(deck);
    }

    public void printDeck() {
        for(Card card : deck) {
            System.out.println(card);
        }

        System.out.println();
    }
}
