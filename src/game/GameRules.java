
package game;

import java.util.List;
import model.Card;

public class GameRules {

    public enum Result {
        WIN, LOSE, PUSH
    }

    public static int calculateScore(List<Card> hand) {

        int score = 0;
        int aces = 0;

        for (Card card : hand) {

            score += card.getValue();

            if (card.isAce()) {
                aces++;
            }
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public static boolean isBlackjack(List<Card> hand) {

        return hand != null
                && hand.size() == 2
                && calculateScore(hand) == 21;
    }

    public static boolean isBust(List<Card> hand) {
        return calculateScore(hand) > 21;
    }

    public static Result compareHands(
            List<Card> playerHand,
            List<Card> dealerHand) {

        int playerScore = calculateScore(playerHand);
        int dealerScore = calculateScore(dealerHand);

        if (playerScore > 21) {
            return Result.LOSE;
        }

        boolean playerBlackjack =
                isBlackjack(playerHand);

        boolean dealerBlackjack =
                isBlackjack(dealerHand);

        if (playerBlackjack && !dealerBlackjack) {
            return Result.WIN;
        }

        if (!playerBlackjack && dealerBlackjack) {
            return Result.LOSE;
        }

        if (dealerScore > 21) {
            return Result.WIN;
        }

        if (playerScore > dealerScore) {
            return Result.WIN;
        }

        if (playerScore < dealerScore) {
            return Result.LOSE;
        }

        return Result.PUSH;
    }

    public static boolean isPair(List<Card> hand) {

        return hand != null
                && hand.size() == 2
                && hand.get(0).getRank()
                        == hand.get(1).getRank();
    }

    public static boolean isPerfectPair(List<Card> hand) {

        return isPair(hand)
                && hand.get(0).getSuit()
                        == hand.get(1).getSuit();
    }
}
