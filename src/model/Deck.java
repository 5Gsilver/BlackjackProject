package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;
    private final int numberOfDecks;
    private int currentIndex;

    private static final double CUT_CARD_RATIO = 0.75;

    public Deck(int numberOfDecks) {

        if (numberOfDecks < 1 || numberOfDecks > 8) {
            throw new IllegalArgumentException(
                    "덱 개수는 1~8개 사이여야 합니다.");
        }

        this.numberOfDecks = numberOfDecks;
        this.cards = new ArrayList<Card>();

        reset();
    }

    private void initializeDeck() {

        cards.clear();

        for (int i = 0; i < numberOfDecks; i++) {

            for (Card.Suit suit : Card.Suit.values()) {

                for (Card.Rank rank : Card.Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
        }

        currentIndex = 0;
    }

    public void shuffle() {
        Collections.shuffle(cards);
        currentIndex = 0;
    }

    public Card drawCard() {

        if (currentIndex >= cards.size()) {
            throw new IllegalStateException(
                    "남아있는 카드가 없습니다");
        }

        return cards.get(currentIndex++);
    }

    public int getRemainingCards() {
        return cards.size() - currentIndex;
    }

    public int getUsedCards() {
        return currentIndex;
    }

    public int getTotalCards() {
        return cards.size();
    }

    public boolean needsShuffle() {
        return currentIndex >= cards.size() * CUT_CARD_RATIO;
    }

    public void reset() {
        initializeDeck();
        shuffle();
    }

    public int getNumberOfDecks() {
        return numberOfDecks;
    }
}