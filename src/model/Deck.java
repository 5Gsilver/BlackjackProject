package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	
	//현재 슈에 들어있는 카드 목록
	private final List<Card> cards;
	
	//사용되는 덱의 개수
	private final int numberOfDecks;
	
	//다음에 분배할 카드의 위치
	private int currentIndex;
	
	//컷 카드 기준 (75%사용시 교환)
	private static final double CUT_CARD_RATIO = 0.75;
	
	//카드 생성자
	public Deck(int numberOfDecks) {
		if(numberOfDecks < 1 || numberOfDecks > 8) {
			throw new IllegalArgumentException("덱 개수는 1~8개 사이여야 합니다.");
		}
		
		this.numberOfDecks = numberOfDecks;
		this.cards = new ArrayList<>();
		
		reset();
	}
	
	//카드 덱 생성
	private void initializeDeck() {
		cards.clear();
		
		for(int i = 0; i< numberOfDecks; i++) {
			for (Card.Suit suit : Card.Suit.values()) {
				for(Card.Rank rank: Card.Rank.values()){
					cards.add(new Card(suit,rank));
				}
			}
		}
		
		currentIndex = 0;
	}
	
	//카드 섞기(빨간카드)
	public void shuffle() {
		Collections.shuffle(cards);
		currentIndex =0;
	}
	
	//카드 한장 분배
	public Card drawCard() {
		if(currentIndex >= cards.size()) {
			throw new IllegalStateException(
					"남아있는 카드가 없습니다"
			);
		}
		
		return cards.get(currentIndex++);
	}
	
	//남은 카드 수 
	public int getRemainingCards() {
		return cards.size() - currentIndex;
	}
	
	//사용한 카드 수 
	public int getUsedCards() {
		return currentIndex;
	}
	
	//전체 카드 수 
	public int getTotalCards() {
		return cards.size();
	}
	
	//첫 카드 기준 셔플 여부
	public boolean needsShuffle() {
		return currentIndex >= cards.size()* CUT_CARD_RATIO;
	}
	
	//새로운 덱 생성 및 셔플
	public void reset() {
		initializeDeck();
		shuffle();
	}
	
	public int getNumberOfDecks() {
		return numberOfDecks;
	}
}