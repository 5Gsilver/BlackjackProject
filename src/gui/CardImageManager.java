package gui;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;


import javax.swing.ImageIcon;

import model.Card;

public class CardImageManager {
	
	//===============================
	// 1. 이미지 경로 및 기본 크기
	//===============================
	
	private static final String CARD_PATH = "resources/cards/";
	
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGTH = 120;
	
	//===============================
	// 2. 이미지 캐시
	//===============================
	
	private HashMap<String, ImageIcon> imageCache;
	
	//===============================
	// 3. 생성자
	//===============================
	
	public CardImageManager() {
		imageCache = new HashMap<String, ImageIcon>();
	}
	
	//===============================
	// 4. 카드 앞면 이미지 반환
	//===============================
	
	public ImageIcon getCardImage(Card card) {
		
		return getCardImage(card, DEFAULT_WIDTH, DEFAULT_HEIGTH);
	}
	
	public ImageIcon getCardImage(Card card, int width, int heigth) {
		
		if(card == null) {
			System.out.println("카드 캑체가 없습니다.");
			return null;
		}
		
		String suit = card.getSuit().name();
		String rank = card.getRank().getSymbol();
		
		String fileName = suit + "_" + rank + ".png";
		
		return loadImage(fileName, width, heigth);
	}
	
	//===============================
	// 5. 카드 뒷면 이미지 반환
	//===============================
	
	public ImageIcon getBackImage() {
		
		return getBackImage(DEFAULT_WIDTH, DEFAULT_HEIGTH);
		
	}
	
	public ImageIcon getBackImage(int width, int heigth) {
		
		return loadImage("back.png", width, heigth);
	}
	
	//===============================
	// 6. 이미지 불러외 및 캐싱
	//===============================
	
	private ImageIcon loadImage(String fileName, int width, int heigth) {
		
		if(width <= 0 || heigth <= 0) {
			System.out.println("이미지 크기가 올바르지 않습니다.");
			return null;
		}
		
		String key = fileName + "_" + width + "_" +heigth;
		
		// 이미 저장된 이미지가 있다면 재사용
		if(imageCache.containsKey(key)) {
			return imageCache.get(key);
		}
		
		String fullPath = CARD_PATH + fileName;
		
		File file = new File(fullPath);
		
		// 이미지 파일 존재 여부 확인
		if(!file.isFile()) {
			System.out.println("이미지 파일이 없습니다: " + fullPath);
			return null;
		}
		
		ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
		
		// 이미지가 정상적으로 로딩되었는지 확인
		if(originalIcon.getIconWidth() <= 0 || originalIcon.getIconHeight() <= 0) {
			
			System.out.println("이미지를 불러올 수 없습니다.");
			return null;
		}
		
		ImageIcon resizedIcon = resizeImage(originalIcon, width, heigth);
		
		// 크기 조절된 이미지 캐시에 저장
		imageCache.put(key, resizedIcon);
		
		return resizedIcon;
	}
	
	//===============================
	// 7. 이미지 크기 조절
	//===============================
	
	public ImageIcon resizeImage(ImageIcon icon, int width, int heigth) {
		
		if(icon == null || width <= 0 || heigth <= 0) {
			return null;
		}
		
		Image image = icon.getImage();
		
		Image resizedImage = image.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
		
		return new ImageIcon(resizedImage);
	}
	
	//===============================
	// 8. 캐시 초기화
	//===============================
	
	public void clearCache() {
		
		imageCache.clear();
	}
	
	//===============================
	// 9. 캐시 이미지 개수 확인
	//===============================
	
	public int getCacheSize() {
		return imageCache.size();
	}

}