package model;

import java.util.Objects;

public class Card {

    public enum Suit {
        SPADE, HEART, DIAMOND, CLUB
    }

    public enum Rank {
        ACE(11, "A"),
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        JACK(10, "J"),
        QUEEN(10, "Q"),
        KING(10, "K");

        private final int value;
        private final String symbol;

        Rank(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }

        public int getValue() {
            return value;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = Objects.requireNonNull(
                suit, "카드 무늬는 null일 수 없습니다.");
        this.rank = Objects.requireNonNull(
                rank, "카드 숫자는 null일 수 없습니다.");
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    public String getImageFileName() {
        String letter = "";

        if (suit == Suit.SPADE) {
            letter = "S";
        } else if (suit == Suit.HEART) {
            letter = "H";
        } else if (suit == Suit.DIAMOND) {
            letter = "D";
        } else {
            letter = "C";
        }

        return rank.getSymbol() + letter + ".png";
    }

    @Override
    public String toString() {
        return rank.getSymbol() + " of " + suit.name();
    }
}