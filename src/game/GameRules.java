package game;

import java.util.List;
import com.sun.net.httpserver.Authenticator.Result;

import model.Card;

public class GameRules {
	
	//게임 결과를 나타내는 열거형 자료
	public enum Result{
		WIN, LOSE , PUSH;
	}
	
	// 1.카드 점수 합산
	public static int calculateScore(List<Card> hand) {
		int score = 0;
		int aceCount = 0;
		
		for (Card card : hand) {
			score += card.getValue();
			
			if (card.getRank().equals("A")) {
				aceCount++;
			}
		}
		
		//만약 21 점 초과시 A를 11점에서 1점으로 변경
		while (score > 21 && aceCount > 0) {
			score -= 10;
			aceCount--;
		}
		
		return score;
	}
	
	//2. 블랙젝 판정 (21이 나올시)
	public static boolean isBlackjack(List<Card> hand) {
		return hand.size() == 2 && calculateScore(hand) == 21;
	}
	
	//3. 버스트 판정 (21이 넘으면 버스트)
	public static boolean isBust(List<Card> hand) {
		return calculateScore(hand) > 21; 
	}
	
	//4. 승패 판정
	public static Result compareHands(
			List<Card> playerHand,
			List<Card> dealerHand) {
		
		int playerScore = calculateScore(playerHand);
		int dealerScore = calculateScore(dealerHand);
		
		// 플레이어 버스트시
		if (playerScore > 21) {
			return Result.LOSE;
		}
		
		//딜러 버스트시
		if (dealerScore > 21){
			return Result.WIN;
		}
		
		//블랙잭 우선 판정
		if (playerBlackjack && !dealerBlackjack) {
			return Result.WIN;
		}
		
		if (!playerBlackjack && dealerBlackjack) {
			return Result.LOSE;
		}
		
		//일반 점수 비교
		if(playerScore > dealerScore) {
			return Result.WIN;
		} else if(playerScore < dealerScore) {
			return Result.LOSE;
		} else {
			return Result.PUSH;
		}
	}
	
	//5. 일반 페어 판정
	public static boolean isPair(List<Card> hand) {
		if(hand.size() !=2) {
			return false;
		}
		
		Card first = hand.get(0);
		Card second = hand.get(1);
		
		return first.getRank().equals(second.getRank());
	}
	
	//6. 퍼팩트 페어 판정
	public static boolean isPerfectPair(List<Card> hand) {
		if(!isPair(hand)) {
			return false;
		}
		
		Card first = hand.get(0);
		Card second = hand.get(1);
		
		return first.getSuit().equals(second.getSuit());
	}
	}