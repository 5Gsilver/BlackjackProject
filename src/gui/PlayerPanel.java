package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlayerPanel extends JPanel {
	//1.플 레이어 정보
	
	private String playerName;
	private int chips;
	private int betAmount;
	private int score;
	private boolean currentTurn;
	
	//2.GUI컴포넌트
	
	private JLabel nameLabel, chipsLabel, betLabel, scoreLabel, resultLabel;
	private JPanel cardPanel, infoPanel;
	private ArrayList<JLabel> cardLabels;

	//3. 카드 크기 및 색상
	
	private static final int CARD_WIDTH = 65;
	private static final int CARD_HEIGHT = 95;
	
	private static final Color GOLD = new Color(255, 185, 0);
	private static final Color WHITE = new Color(255, 255, 255);

	//4.생성자
	
	private PlayerPanel(String playerName, int chips) {
		this.playerName = playerName;
		this.chips = chips;
		
		betAmount = 0;
		score = 0;
		currentTurn = false;
		
		cardLabels = new ArrayList<JLabel>();
		initializeUI();
	}
	
	//5.GUI초기화
	
	private void initializeUI() {
		setOpaque(false);
		
		setLayout(new BorderLayout(0,5));
		
		setPreferredSize(new Dimension(210, 240));
		
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
	
	//카드 표시
	cardPanel = new JPanel();
	
	cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, -15, 0));
	
	cardPanel.setOpaque(false);
	
	//카드 점수 표시
	scoreLabel = new JLabel("");

	scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
	
	scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
	
	//플레이어 정보
	infoPanel = new JPanel();
	
	infoPanel.setOpaque(false);
	
	infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
	
	//이름
	 nameLabel = createLabel(playerName, 15, GOLD);
	
	//보유 칩
	chipsLabel = createLabel("Chips: " + chips, 13, WHITE);
	
	//배팅 액 
	betLabel = createLabel("Bet: " + betAmount, 13, WHITE);
	
	//결과
	resultLabel = createLabel("", 13, GOLD);
	
	infoPanel.add(nameLabel);
	infoPanel.add(chipsLabel);
	infoPanel.add(betLabel);
	infoPanel.add(resultLabel);
	
	//카드 및 점수 영역
	JPanel centerPanel = new JPanel(new BorderLayout());
	
	centerPanel.setOpaque(false);
	
	centerPanel.add(cardPanel, BorderLayout.CENTER);
	centerPanel.add(scoreLabel, BorderLayout.SOUTH);
	
	//전체 배치
	add(centerPanel, BorderLayout.CENTER);
	add(infoPanel, BorderLayout.SOUTH);
	}
	//6.JLablel 생성 메서드
	
	private JLabel createLabel(String text, int fontSize, Color color) {
		JLabel label = new JLabel(text);
		
		label.setFont(new Font("Arial", Font.BOLD, fontSize));
		
		label.setAlignmentX(CENTER_ALIGNMENT);
		
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		return label;
	}
	
	//7. 카드 불러오기
	private ImageIcon loadCardImage(String fileName) {
		String path = "cards/" + fileName + ".png";
		
		URL url = getClass().getResource(path);
		
		if (url == null) {
			System.err.println("카드 이미지 없음 :" + path);
			
			BufferedImage fallback = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	
			Graphics2D g = fallback.createGraphics();
			
			g.setColor(Color.WHITE);
			
			g.fillRoundRect(0, 0, CARD_WIDTH -1 , CARD_HEIGHT - 1, 8, 8);
			
			g.setColor(Color.RED);
			
			g.drawString("이미지가 없습니다.", 3, 45);
			
			g.dispose();
			
			return new ImageIcon(fallback);
		}
		
		//이미지 크기 조절
		ImageIcon original = new ImageIcon(url);
		
		Image scaled = original.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
		
		return new ImageIcon(scaled);
	}
	
	//8.카드 추가 
	public void addCard(String cardFileName) {
		JLabel cardLabel = new JLabel(loadCardImage(cardFileName));
		cardLabels.add(cardLabel);
		
		cardPanel.add(cardLabel);
		
		cardPanel.revalidate();
		cardPanel.repaint();
	}
	
	public void clearCards() {
		cardLabels.clear();
		
		cardPanel.removeAll();
		
		score = 0;
		
		scoreLabel.setText(" ");
		
		cardPanel.revalidate();
		cardPanel.repaint();
	}
	
	//10. 카드 점수 변경
	
	public void setScore(int score) {
		this.score = score;
		
		scoreLabel.setText("SCORE:" + score);
	}
	
	//11. 보유 칩 변경

	public void setChips(int chips) {
		this.chips = chips;
		
		chipsLabel.setText("BALANCE: " + chips);
	}
	
	//12. 배팅 금액 변경
	
	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
		
		betLabel.setText("BET: " + betAmount);
	}
	
	//13. 현재 플레이어 차례

	public void setCurrentTurn(boolean currentTurn) {
		this.currentTurn = currentTurn;
		
		if (currentTurn) {
			setBorder(BorderFactory.createLineBorder(GOLD, 2,true));
			nameLabel.setForeground(GOLD.YELLOW);
		} else {
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			nameLabel.setForeground(GOLD);
		}
		
		repaint();
	}
	
	//14. 결과 표시
	
	public void showResult(String result) {
		if(result == null) {
			resultLabel.setText(" ");
			return;
		} 
		switch (result.toUpperCase()) {
		case "WIN" :
			resultLabel.setText("YOU WIN");
			resultLabel.setForeground(new Color(80,255,130));
			break;
		case "LOSE" :
			resultLabel.setText("YOU LOSE");
			resultLabel.setForeground(Color.RED);
			break;
			
		case "PUSH" :
			resultLabel.setText("PUSH");
			resultLabel.setForeground(Color.YELLOW);
			break;
		case "BLACKJACK" :
			resultLabel.setText("BLACKJACK");
			resultLabel.setForeground(GOLD);
			break;
		default :
			resultLabel.setText(result);
			resultLabel.setForeground(Color.WHITE);
			break;
		}
	}
	
	//15.다음 라운드 초기화
	
	public void resetRound() {
		clearCards();
		
		betAmount = 0;
		betLabel.setText("BET: " + betAmount);	
		resultLabel.setText(" ");
		resultLabel.setForeground(GOLD);
		setCurrentTurn(false);
	}
	
	//16.Getter 메서드
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getChips() {
		return chips;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean isCurrentTurn() {
		return currentTurn;
	}
}