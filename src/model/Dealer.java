
package model;

import java.util.ArrayList;
import java.util.List;

import game.GameRules;

public class Dealer extends Player {

    private boolean holeCardRevealed;

    public Dealer() {
        super("Dealer");
        holeCardRevealed = false;
    }

    public Card getOpenCard() {

        if (getHand().isEmpty()) {
            return null;
        }

        return getHand().get(0);
    }

    public Card getHiddenCard() {

        if (getHand().size() < 2) {
            return null;
        }

        return getHand().get(1);
    }

    public void revealHiddenCard() {
        holeCardRevealed = true;
    }

    public boolean isHoleCardRevealed() {
        return holeCardRevealed;
    }

    public List<Card> getVisibleCards() {

        if (holeCardRevealed) {
            return new ArrayList<Card>(getHand());
        }

        List<Card> visibleCards = new ArrayList<Card>();

        Card openCard = getOpenCard();

        if (openCard != null) {
            visibleCards.add(openCard);
        }

        return visibleCards;
    }

    @Override
    public int getScore() {
        return GameRules.calculateScore(getHand());
    }

    public boolean shouldHit() {
        return getScore() <= 16;
    }

    public void hit(Deck deck) {

        if (deck == null) {
            throw new IllegalArgumentException(
                    "덱은 null일 수 없습니다.");
        }

        Card card = deck.drawCard();

        addCard(card);
    }

    public void dealInitialCards(Deck deck) {

        clearHand();

        holeCardRevealed = false;

        hit(deck);
        hit(deck);
    }

    public void playTurn(Deck deck) {

        if (deck == null) {
            throw new IllegalArgumentException(
                    "덱은 null일 수 없습니다.");
        }

        revealHiddenCard();

        while (shouldHit()) {
            hit(deck);
        }
    }

    @Override
    public boolean isBust() {
        return GameRules.isBust(getHand());
    }

    @Override
    public boolean isBlackjack() {
        return GameRules.isBlackjack(getHand());
    }

    public void resetDealer() {

        clearHand();

        holeCardRevealed = false;
    }
}
