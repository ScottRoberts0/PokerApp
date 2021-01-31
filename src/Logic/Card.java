package Logic;

public class Card {
    private int value;
    private int suitValue;
    private String suit;
    private String name;

    public Card(int value, int suitValue) {
        if(value < 1 || value > 14) {
            //IllegalArgumentException is a runtime exception, therefore not required to be handled by compiler
            throw new IllegalArgumentException("value must be an integer from 1 to 13");
        } else {
            this.value = value;
        }

        if(suitValue < 0 || suitValue > 3) {
            throw new IllegalArgumentException("suitValue must be an integer from 0 to 3");
        } else {
            this.suitValue = suitValue;
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
            this.suit = "Hearts";
        } else if (suitValue == 1) {
            this.suit = "Diamonds";
        } else if (suitValue == 2) {
            this.suit = "Clubs";
        } else {
            this.suit = "Spades";
        }
    }

    public boolean equals(Card card) {
        if (getValue() == card.getValue() && getSuitValue() == card.getSuitValue()) {
            return true;
        }
        return false;
    }

    public String getShortName() {
        //checks to see if it is a 10, in which case gives Tsuit, if not, just the first char of the name
        return (name.equals("10") ? "T" : name.charAt(0)) + "" + Character.toLowerCase(suit.charAt(0));
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