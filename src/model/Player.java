package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Player {

    // 1. 플레이어 정보
    private final String name;
    private int chips;
    private int currentBet;
    private final ArrayList<Card> hand;

    // 2. 현재 라운드 상태
    private boolean standing;
    private boolean turnFinished;
    private String result;

    // 3. 생성자
    public Player(String name, int chips) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "플레이어 이름을 입력해야 합니다.");
        }

        if (chips < 0) {
            throw new IllegalArgumentException(
                    "초기 칩은 0 이상이어야 합니다.");
        }

        this.name = name;
        this.chips = chips;
        this.currentBet = 0;
        this.hand = new ArrayList<Card>();
        this.standing = false;
        this.turnFinished = false;
        this.result = "";
    }

    // 딜러 등 칩이 필요 없는 객체를 위한 생성자
    public Player(String name) {
        this(name, 0);
    }

    // 4. 정보 조회
    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    // 기존 GUI 코드의 getBet() 호출을 지원
    public int getBet() {
        return currentBet;
    }

    public String getResult() {
        return result;
    }

    public boolean isStanding() {
        return standing;
    }

    public boolean isTurnFinished() {
        return turnFinished;
    }

    // 5. 가상 칩 관리
    public void setChips(int chips) {
        if (chips < 0) {
            throw new IllegalArgumentException(
                    "칩은 0 이상이어야 합니다.");
        }

        if (currentBet > 0) {
            throw new IllegalStateException(
                    "베팅 중에는 보유 칩을 직접 변경할 수 없습니다.");
        }

        this.chips = chips;
    }

    public void addChips(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "추가할 칩은 0보다 커야 합니다.");
        }

        chips = Math.addExact(chips, amount);
    }

    public boolean removeChips(int amount) {
        if (amount <= 0 || amount > chips) {
            return false;
        }

        chips -= amount;
        return true;
    }

    // 6. 베팅
    public boolean canBet(int amount) {
        return amount > 0
                && chips >= amount
                && currentBet == 0
                && hand.isEmpty()
                && !turnFinished;
    }

    public boolean placeBet(int amount) {
        if (!canBet(amount)) {
            return false;
        }

        chips -= amount;
        currentBet = amount;
        return true;
    }

    // 기존 코드에서 bet()을 사용하는 경우를 위한 별칭
    public boolean bet(int amount) {
        return placeBet(amount);
    }

    // 7. 카드 관리
    public void addCard(Card card) {
        Objects.requireNonNull(
                card, "카드는 null일 수 없습니다.");

        hand.add(card);

        // 21점 도달 또는 버스트 시 턴 종료
        if (getScore() >= 21) {
            turnFinished = true;

            if (isBust()) {
                result = "BUST";
            }
        }
    }

    public void receiveCard(Card card) {
        addCard(card);
    }

    // 외부에서 원본 카드 목록을 수정하지 못하도록 복사본 반환
    public ArrayList<Card> getHand() {
        return new ArrayList<Card>(hand);
    }

    public List<Card> getCards() {
        return getHand();
    }

    public int getCardCount() {
        return hand.size();
    }

    public void clearHand() {
        if (currentBet > 0) {
            throw new IllegalStateException(
                    "베팅 정산 후 카드를 초기화해야 합니다.");
        }

        hand.clear();
        standing = false;
        turnFinished = false;
    }

    // 8. 점수 및 특수 상황 판정
    public int getScore() {
        return GameRules.calculateScore(hand);
    }

    public boolean isBlackjack() {
        return hand.size() == 2 && getScore() == 21;
    }

    public boolean isBust() {
        return getScore() > 21;
    }

    // 9. HIT
    public boolean canHit() {
        return hand.size() >= 2
                && !standing
                && !turnFinished
                && getScore() < 21;
    }

    public boolean hit(Card card) {
        if (card == null || !canHit()) {
            return false;
        }

        addCard(card);
        return true;
    }

    // 10. STAND
    public void stand() {
        if (hand.size() < 2 || turnFinished) {
            return;
        }

        standing = true;
        turnFinished = true;
    }

    // 11. DOUBLE
    public boolean canDouble() {
        return hand.size() == 2
                && currentBet > 0
                && chips >= currentBet
                && currentBet <= Integer.MAX_VALUE / 2
                && !standing
                && !turnFinished
                && getScore() < 21;
    }

    // 베팅을 2배로 늘리고 카드 한 장을 받은 뒤 종료
    public boolean doubleDown(Card card) {
        if (card == null || !canDouble()) {
            return false;
        }

        chips -= currentBet;
        currentBet *= 2;

        addCard(card);

        standing = true;
        turnFinished = true;
        return true;
    }

    // 12. 정산
    // 베팅 원금은 placeBet() 실행 시 이미 차감됨

    // 일반 승리: 1:1
    public void winBet() {
        if (currentBet <= 0) {
            return;
        }

        // 원금 + 1:1 수익
        long payout = (long) currentBet * 2;

        chips = Math.toIntExact((long) chips + payout);

        currentBet = 0;
        result = "WIN";
        turnFinished = true;
    }

    // 블랙잭 승리: 3:2
    public void blackjackWin() {
        if (currentBet <= 0) {
            return;
        }

        // 원금 + 3:2 수익
        // 홀수 단위 베팅의 0.5칩은 버림
        long payout = (long) currentBet * 5 / 2;

        chips = Math.toIntExact((long) chips + payout);

        currentBet = 0;
        result = "BLACKJACK";
        turnFinished = true;
    }

    // 패배
    public void loseBet() {
        if (currentBet <= 0) {
            return;
        }

        // 이미 베팅 시 칩을 차감했으므로 추가 차감 불필요
        currentBet = 0;

        result = isBust() ? "BUST" : "LOSE";
        turnFinished = true;
    }

    // 무승부: 원금 반환
    public void pushBet() {
        if (currentBet <= 0) {
            return;
        }

        chips = Math.toIntExact((long) chips + currentBet);

        currentBet = 0;
        result = "PUSH";
        turnFinished = true;
    }

    // 13. 상태 표시 및 턴 관리
    public void setResult(String result) {
        this.result = result == null ? "" : result;
    }

    public void finishTurn() {
        turnFinished = true;
    }

    // 14. 다음 라운드 초기화
    public void resetRound() {
        if (currentBet > 0) {
            throw new IllegalStateException(
                    "베팅 정산이 끝난 후 다음 라운드를 시작하세요.");
        }

        hand.clear();
        standing = false;
        turnFinished = false;
        result = "";
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", chips=" + chips +
                ", currentBet=" + currentBet +
                ", score=" + getScore() +
                ", standing=" + standing +
                ", result='" + result + '\'' +
                '}';
    }
}
