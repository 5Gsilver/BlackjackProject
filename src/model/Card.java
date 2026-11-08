package model;
import java.util.Objects;

public class Card {

    // 1. 카드 무늬
    public enum Suit {
        SPADE, HEART, DIAMOND, CLUB
    }

    // 2. 카드 숫자
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

    // 3. 카드 객체의 필드
    private final Suit suit;
    private final Rank rank;

    // 4. 생성자
    public Card(Suit suit, Rank rank) {
        this.suit = Objects.requireNonNull(
            suit, "카드 무늬는 null일 수 없습니다."
        );
        this.rank = Objects.requireNonNull(
            rank, "카드 숫자는 null일 수 없습니다."
        );
    }

    // 5. 카드 무늬 반환
    public Suit getSuit() {
        return suit;
    }

    // 6. 카드 숫자 반환
    public Rank getRank() {
        return rank;
    }

    // 7. 카드 점수 반환
    public int getValue() {
        return rank.getValue();
    }

    // 8. 에이스 여부 확인
    public boolean isAce() {
        return rank == Rank.ACE;
    }

    // 9. 카드 이미지 파일명 반환
    public String getImageFileName() {
        return rank.getSymbol() + "_"
                + suit.name().toLowerCase() + ".png";
    }

    // 10. 카드 정보 문자열 반환
    @Override
    public String toString() {
        return rank.getSymbol() + " of " + suit.name();
    }
}
