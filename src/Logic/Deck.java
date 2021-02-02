package Logic;
import java.util.ArrayList;
import java.util.Collections;

public class Deck extends ArrayList<Card> {

    public Deck() {
        super();
        reset();
        shuffle();
    }

    public Card drawCard() {
        Card card = this.get(0);
        this.remove(0);
        return card;
    }

    /**
     * This method will draw a specified card from the deck
     * @param value 1 for Aces, 11 for Jacks, 12 for Queens, 13 for Kings
     * @param suitValue 0 for Hearts, 1 for Diamonds, 2 for Clubs, 3 for Spades
     */
    public Card drawCard(int value, int suitValue) {
        Card card = new Card(value, suitValue);
        boolean changed = false;

        for(Card c : this) {
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

    public void reset() {
        this.removeAll(this);

        //populates a deck with all 52 cards
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 13; j++) {
                this.add(new Card(j + 1, i));
            }
        }
    }

    public void shuffle() {
        reset();
        Collections.shuffle(this);
    }

    public void printDeck() {
        for(Card card : this) {
            System.out.println(card);
        }

        System.out.println();
    }
}
