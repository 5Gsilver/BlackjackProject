package model;


import java.util.ArrayList;
import java.util.List;

import game.GameRules;

/**
 * 딜러 주요 기능
 * 1. 딜러 카드 관리
 * 2. 공개 카드 및 비공개 카드 관리
 * 3. 딜러 점수 계산
 * 4. 16점 이하 HIT
 * 5. 17점 이상 STAND
 * 6. 자동 카드 배분
 *
 * Player 클래스를 상속받아 구현한다.
 */
public class Dealer extends Player {

    // 딜러의 비공개 카드 공개 여부
    private boolean holeCardRevealed;

    public Dealer() {
        super("Dealer");
        holeCardRevealed = false;
    }

    /**
     * 딜러의 첫 번째 공개 카드를 반환한다.
     *
     * @return 공개 카드, 카드가 없으면 null이지만 null 은 반환하지 않음.
     */
    public Card getOpenCard() {

        if (getHand().isEmpty()) {
            return null;
        }

        return getHand().get(0);
    }

    /**
     * 딜러의 두 번째 비공개 카드를 반환한다 두번째 카드는 뒷면을 반환함.
     * @return 비공개 카드, 카드가 없으면 null
     */
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

    /**
     * 현재 상태 카드 목록 반환
     * 비공개 상태에서는 첫 번째 카드만 반환한다.
     */
    public List<Card> getVisibleCards() {

        if (holeCardRevealed) {
            return new ArrayList<>(getHand());
        }

        List<Card> visibleCards = new ArrayList<>();

        Card openCard = getOpenCard();

        if (openCard != null) {
            visibleCards.add(openCard);
        }

        return visibleCards;
    }

    //딜러 점수 계산
    public int getScore() {
        return GameRules.calculateScore(getHand());
    }
    
    //딜러 HIT STAND 판단 
    public boolean shouldHit() {
        return getScore() <= 16;
    }

    //카드가 필요시 덱에서 카드를 받아옴
    public void hit(Deck deck) {

        if (deck == null) {
            throw new IllegalArgumentException(
                "덱은 비어있을수 없습니다.."
            );
        }

        Card card = deck.dealCard();

        if (card != null) {
            addCard(card);
        }
    }
    
    public void dealInitialCards(Deck deck) {

        resetHand();
        holeCardRevealed = false;

        hit(deck);
        hit(deck);
    }

    public void playTurn(Deck deck) {

        if (deck == null) {
            throw new IllegalArgumentException(
                "Deck cannot be null."
            );
        }

        revealHiddenCard();

        while (shouldHit()) {

            int previousCardCount = getHand().size();

            hit(deck);

            // 덱에 카드가 없으면 반복 종료
            if (getHand().size() == previousCardCount) {
                break;
            }
        }
    }

    //딜러 버스트 확인
    public boolean isBust() {
        return GameRules.isBust(getHand());
    }

    //딜러 블랙잭 확인
    public boolean isBlackjack() {
        return GameRules.isBlackjack(getHand());
    }


    //게임 종료 후 딜러 초기화
    public void resetDealer() {

        resetHand();
        holeCardRevealed = false;
    }
}
