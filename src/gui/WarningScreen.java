package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class WarningScreen extends JFrame {
	private JCheckBox agreeChekBox;
	private JButton startButton;
	
	public WarningScreen() {
		initializeUI();
		createWarningPanel();
		createButtonPanel();
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initializeUI() {
		setTitle("Blackjack - Warning");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(20, 70, 50));
		setResizable(false);
	}
	
	private void createWarningPanel() {
		JPanel warningPanel = new JPanel();
		warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.X_AXIS));
		warningPanel.setBackground(new Color(20, 70, 50));
		warningPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 30, 50));
		
		JLabel titleLabel = new JLabel("BLACKJACK WARNING");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		titleLabel.setForeground(Color.RED);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel warningLabel = new JLabel("<html><div style='text-align:center;'>"
				+ "이 게임은 교육 목적으로만 제작되었습니다.<br><br>"
				+ "싱제 돈은 사용되지 않습니다.<br><br>"
				+ "도박은 금전적 손실과 개인적인 피해를 초래할 수 있습니다.<br><br>"
				+ "책임감 있게 게임을 즐기세요.<br><br>"
				+ "</div></html>");
		
		warningLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		warningLabel.setForeground(Color.WHITE);
		warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		warningPanel.add(titleLabel);
		warningPanel.add(Box.createVerticalStrut(50));
		warningPanel.add(warningLabel);
		
		add(warningPanel, BorderLayout.CENTER);
	}
	
	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonPanel.setBackground(new Color(20, 70, 50));
		
		JCheckBox agreeCheckBox = new JCheckBox("이 경고를 읽고 이해했습니다.");
		agreeCheckBox.setForeground(Color.WHITE);
		agreeCheckBox.setBackground(new Color(20, 70, 50));
		
		startButton = new JButton("게임 시작");
		startButton.setFont(new Font("Arial", Font.BOLD, 18));
		startButton.setEnabled(false);
		
		agreeCheckBox.addActionListener(e -> {
			startButton.setEnabled(agreeCheckBox.isSelected());
		});
		startButton.addActionListener(e -> startGame());
		
		buttonPanel.add(agreeCheckBox);
		buttonPanel.add(startButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
}
	
	private void startGame() {
		dispose();
		
		// TODO: GameSetupScreen 구현 후 연결
		// new GameSetupScreen();
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new WarningScreen();
		}) ;
	}

}