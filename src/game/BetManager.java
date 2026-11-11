package game;
import java.util.HashMap;
import java.util.Map;

/**
 * 블랙잭 게임의 가상 칩과 베팅 내역을 관리합니다.
 * 이 클래스의 칩 잔액을 유일한 정산 기준으로 사용해야 합니다.
 */
public class BetManager {

    public static final int MAX_PLAYERS = 7;

    public enum NormalResult {
        WIN, BLACKJACK, PUSH, LOSE
    }

    public enum PairResult {
        NONE, PAIR, PERFECT_PAIR
    }

    //=====================================
    // 1. 플레이어별 베팅 상태
    //=====================================

    private static class BetState {

        long chips;
        long mainBet;
        long pairBet;
        long insuranceBet;

        boolean doubled;
        boolean mainSettled;
        boolean pairSettled;
        boolean insuranceSettled;

        BetState(long startingChips) {
            chips = startingChips;
        }

        void resetRound() {
            mainBet = 0;
            pairBet = 0;
            insuranceBet = 0;

            doubled = false;
            mainSettled = false;
            pairSettled = false;
            insuranceSettled = false;
        }
    }

    private final Map<String, BetState> players =
            new HashMap<String, BetState>();

    private boolean bettingOpen = true;

    //=====================================
    // 2. 플레이어 등록 및 칩 조회
    //=====================================

    public synchronized void registerPlayer(
            String playerId, long startingChips) {

        String id = checkPlayerId(playerId);

        if (!bettingOpen) {
            throw new IllegalStateException(
                    "베팅 진행 중에는 플레이어를 추가할 수 없습니다.");
        }

        if (startingChips < 0) {
            throw new IllegalArgumentException(
                    "초기 칩은 0 이상이어야 합니다.");
        }

        if (players.containsKey(id)) {
            throw new IllegalArgumentException(
                    "이미 등록된 플레이어입니다: " + id);
        }

        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException(
                    "플레이어는 최대 7명까지 등록할 수 있습니다.");
        }

        players.put(id, new BetState(startingChips));
    }

    public synchronized long getChips(String playerId) {
        return stateOf(playerId).chips;
    }

    public synchronized long getMainBet(String playerId) {
        return stateOf(playerId).mainBet;
    }

    public synchronized long getPairBet(String playerId) {
        return stateOf(playerId).pairBet;
    }

    public synchronized long getInsuranceBet(String playerId) {
        return stateOf(playerId).insuranceBet;
    }

    public synchronized boolean isDoubled(String playerId) {
        return stateOf(playerId).doubled;
    }

    //=====================================
    // 3. 기본 베팅 가능 여부 확인
    //=====================================

    public synchronized boolean canBet(
            String playerId, long amount) {

        BetState state = stateOf(playerId);

        return bettingOpen
                && amount > 0
                && state.mainBet == 0
                && !state.mainSettled
                && state.chips >= amount;
    }

    //=====================================
    // 4. 기본 베팅 금액 차감
    //=====================================

    public synchronized void placeBet(
            String playerId, long amount) {

        if (!canBet(playerId, amount)) {
            throw new IllegalArgumentException(
                    "기본 베팅이 불가능합니다.");
        }

        BetState state = stateOf(playerId);

        state.chips -= amount;
        state.mainBet = amount;
    }

    //=====================================
    // 5. 페어 사이드 베팅
    //=====================================

    public synchronized void placePairBet(
            String playerId, long amount) {

        BetState state = stateOf(playerId);

        if (!bettingOpen
                || state.mainBet == 0
                || state.pairBet != 0
                || amount <= 0
                || state.chips < amount) {

            throw new IllegalArgumentException(
                    "페어 베팅이 불가능합니다.");
        }

        state.chips -= amount;
        state.pairBet = amount;
    }

    //=====================================
    // 6. 초기 베팅 마감
    //=====================================

    public synchronized void closeBetting() {

        if (!bettingOpen) {
            throw new IllegalStateException(
                    "이미 베팅이 마감되었습니다.");
        }

        bettingOpen = false;
    }

    //=====================================
    // 7. DOUBLE DOWN
    //=====================================

    public synchronized void doubleBet(String playerId) {

        BetState state = stateOf(playerId);

        if (bettingOpen
                || state.mainBet == 0
                || state.mainSettled
                || state.doubled
                || state.chips < state.mainBet) {

            throw new IllegalStateException(
                    "더블다운이 불가능합니다.");
        }

        long doubledAmount =
                Math.multiplyExact(state.mainBet, 2L);

        state.chips -= state.mainBet;
        state.mainBet = doubledAmount;
        state.doubled = true;
    }

    //=====================================
    // 8. 일반 베팅 정산
    //=====================================

    public synchronized long settleNormalBet(
            String playerId, NormalResult result) {

        BetState state = stateOf(playerId);

        if (bettingOpen
                || state.mainBet == 0
                || state.mainSettled
                || result == null) {

            throw new IllegalStateException(
                    "기본 베팅을 정산할 수 없습니다.");
        }

        if (result == NormalResult.BLACKJACK
                && state.doubled) {

            throw new IllegalArgumentException(
                    "더블다운한 패에는 블랙잭 배당을 적용하지 않습니다.");
        }

        long payout = 0;

        if (result == NormalResult.WIN) {

            // 일반 승리: 원금 포함 2배
            payout = Math.multiplyExact(
                    state.mainBet, 2L);

        } else if (result == NormalResult.BLACKJACK) {

            // 블랙잭: 원금 포함 2.5배
            // 홀수 베팅의 0.5칩은 버림
            payout = Math.addExact(
                    Math.multiplyExact(state.mainBet, 2L),
                    state.mainBet / 2L);

        } else if (result == NormalResult.PUSH) {

            // 무승부: 원금 반환
            payout = state.mainBet;
        }

        // LOSE이면 반환 금액 0
        long newChips =
                Math.addExact(state.chips, payout);

        state.chips = newChips;
        state.mainSettled = true;

        return payout;
    }

    //=====================================
    // 9. 페어 베팅 정산
    //=====================================

    public synchronized long settlePairBet(
            String playerId, PairResult result) {

        BetState state = stateOf(playerId);

        if (bettingOpen || result == null) {
            throw new IllegalStateException(
                    "페어 베팅을 정산할 수 없습니다.");
        }

        if (state.pairBet == 0) {
            return 0;
        }

        if (state.pairSettled) {
            throw new IllegalStateException(
                    "이미 정산한 페어 베팅입니다.");
        }

        long payout = 0;

        if (result == PairResult.PAIR) {

            // 일반 페어 6:1
            payout = Math.multiplyExact(
                    state.pairBet, 7L);

        } else if (result == PairResult.PERFECT_PAIR) {

            // 퍼펙트 페어 25:1
            payout = Math.multiplyExact(
                    state.pairBet, 26L);
        }

        long newChips =
                Math.addExact(state.chips, payout);

        state.chips = newChips;
        state.pairSettled = true;

        return payout;
    }

    //=====================================
    // 10. 보험 베팅
    //=====================================

    public synchronized void placeInsurance(
            String playerId,
            long amount,
            boolean dealerShowsAce) {

        BetState state = stateOf(playerId);

        if (bettingOpen
                || !dealerShowsAce
                || state.mainBet == 0
                || state.doubled
                || state.mainSettled
                || state.insuranceBet != 0
                || state.insuranceSettled
                || amount <= 0
                || amount > state.mainBet / 2L
                || state.chips < amount) {

            throw new IllegalArgumentException(
                    "보험 베팅이 불가능합니다.");
        }

        state.chips -= amount;
        state.insuranceBet = amount;
    }

    //=====================================
    // 11. 보험 베팅 정산
    //=====================================

    public synchronized long settleInsurance(
            String playerId,
            boolean dealerHasBlackjack) {

        BetState state = stateOf(playerId);

        if (bettingOpen) {
            throw new IllegalStateException(
                    "베팅을 마감한 후 보험을 정산해야 합니다.");
        }

        if (state.insuranceBet == 0) {
            return 0;
        }

        if (state.insuranceSettled) {
            throw new IllegalStateException(
                    "이미 정산한 보험 베팅입니다.");
        }

        long payout = dealerHasBlackjack
                ? Math.multiplyExact(
                        state.insuranceBet, 3L)
                : 0;

        long newChips =
                Math.addExact(state.chips, payout);

        state.chips = newChips;
        state.insuranceSettled = true;

        return payout;
    }

    //=====================================
    // 12. 다음 라운드 초기화
    //=====================================

    public synchronized void startNewRound() {

        for (BetState state : players.values()) {

            if ((state.mainBet > 0 && !state.mainSettled)
                    || (state.pairBet > 0 && !state.pairSettled)
                    || (state.insuranceBet > 0
                            && !state.insuranceSettled)) {

                throw new IllegalStateException(
                        "정산되지 않은 베팅이 남아 있습니다.");
            }
        }

        for (BetState state : players.values()) {
            state.resetRound();
        }

        bettingOpen = true;
    }

    //=====================================
    // 13. 내부 검증 메서드
    //=====================================

    private String checkPlayerId(String playerId) {

        if (playerId == null
                || playerId.trim().length() == 0) {

            throw new IllegalArgumentException(
                    "플레이어 ID가 비어 있습니다.");
        }

        return playerId.trim();
    }

    private BetState stateOf(String playerId) {

        String id = checkPlayerId(playerId);
        BetState state = players.get(id);

        if (state == null) {
            throw new IllegalArgumentException(
                    "등록되지 않은 플레이어입니다: " + id);
        }

        return state;
    }
}