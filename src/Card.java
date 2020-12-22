public class Card {
    private int value;
    private int suitValue;
    private String suit;
    private String name;

    public Card(int value, int suitValue) {
        this.value = value;
        this.suitValue = suitValue;

        //if value is outside of normal card range, set it to ace or king
        if (value < 1) {
            this.value = 1;
        } else if (value > 13) {
            this.value = 13;
        }

        //name cards based on values
        if (value == 1) {
            this.name = "Ace";
        } else if (value == 11) {
            this.name = "Jack";
        } else if (value == 12) {
            this.name = "Queen";
        } else if (value == 13) {
            this.name = "King";
        } else {
            this.name = Integer.toString(value);
        }

        //set suits based on suit values
        if (suitValue == 0 || suitValue < 0) {
            this.suit = "Diamonds";
        } else if (suitValue == 1) {
            this.suit = "Hearts";
        } else if (suitValue == 2) {
            this.suit = "Spades";
        } else {
            this.suit = "Clubs";
        }
    }

    public boolean equals(Card card) {
        if (getValue() == card.getValue() && getSuitValue() == card.getSuitValue()) {
            return true;
        }
        return false;
    }

    public String toString() {
        return name + " of " + suit;
    }

    public int getValue() {
        //need aces to be valued highest
        if (this.value == 1) {
            this.value = 14;
        }
        return value;
    }

    public int getSuitValue() {
        return suitValue;
    }
}