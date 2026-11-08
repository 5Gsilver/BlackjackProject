package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {

    // ==============================
    // 1. 화면 크기 및 기본 설정
    // ==============================
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    private JPanel mainPanel;
    private JPanel dealerCardPanel;
    private JPanel playerPanel;
    private JPanel buttonPanel;

    private JLabel dealerImageLabel;
    private JLabel dealerScoreLabel;
    private JLabel turnLabel;
    private JLabel resultLabel;
    private JLabel roundLabel;
    private JLabel betLabel;

    private JButton hitButton;
    private JButton standButton;
    private JButton doubleButton;
    private JButton betPlusButton;
    private JButton betMinusButton;
    private JButton betConfirmButton;
    private JButton nextRoundButton;
    private JButton restartButton;

    // ==============================
    // 2. 게임 상태 변수
    // ==============================
    private int playerCount;
    private int currentPlayerIndex = 0;
    private int round = 1;
    private int currentBet = 100;

    private boolean bettingFinished = false;

    private List<JPanel> playerCardPanels;
    private List<JLabel> playerScoreLabels;
    private List<JLabel> playerNameLabels;

    private static final Color GOLD =
            new Color(235, 195, 90);
    private static final Color DARK_GREEN =
            new Color(10, 75, 48);

    // ==============================
    // 3. 생성자
    // ==============================
    public GameGUI(int playerCount) {

        this.playerCount =
                Math.max(1, Math.min(7, playerCount));

        playerCardPanels = new ArrayList<>();
        playerScoreLabels = new ArrayList<>();
        playerNameLabels = new ArrayList<>();

        setTitle("BLACKJACK CASINO");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeGUI();

        setVisible(true);
    }

    // ==============================
    // 4. 메인 GUI 초기화
    // ==============================
    private void initializeGUI() {

        mainPanel = new BackgroundPanel(
                "/images/backgrounds/table.png"
        );

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        createTopPanel();
        createDealerPanel();
        createPlayerPanel();
        createButtonPanel();

        setContentPane(mainPanel);

        updateTurnDisplay();
        updateBetDisplay();
        setActionButtonsEnabled(false);
    }

    // ==============================
    // 5. 상단 정보 영역
    // ==============================
    private void createTopPanel() {

        JPanel topPanel = new JPanel(
                new GridLayout(3, 1, 0, 5)
        );
        topPanel.setOpaque(false);

        roundLabel = createLabel(
                "ROUND " + round, 24, GOLD
        );

        turnLabel = createLabel(
                "현재 차례: PLAYER 1",
                20, Color.WHITE
        );

        resultLabel = createLabel(
                "베팅을 진행해주세요.",
                18, Color.WHITE
        );

        topPanel.add(roundLabel);
        topPanel.add(turnLabel);
        topPanel.add(resultLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }

    // ==============================
    // 6. 딜러 영역
    // ==============================
    private void createDealerPanel() {

        JPanel dealerPanel = new JPanel();
        dealerPanel.setOpaque(false);
        dealerPanel.setLayout(
                new BoxLayout(dealerPanel, BoxLayout.Y_AXIS)
        );

        dealerImageLabel = new JLabel();
        dealerImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon dealerIcon = loadImage(
                "/images/dealer/dealer_idle.png",
                220, 180
        );

        if (dealerIcon != null) {
            dealerImageLabel.setIcon(dealerIcon);
        } else {
            dealerImageLabel.setText("DEALER");
            dealerImageLabel.setForeground(Color.WHITE);
        }

        dealerScoreLabel = createLabel(
                "DEALER : ?", 22, GOLD
        );
        dealerScoreLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        dealerCardPanel = new JPanel(new FlowLayout());
        dealerCardPanel.setOpaque(false);
        dealerCardPanel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        dealerPanel.add(dealerImageLabel);
        dealerPanel.add(dealerScoreLabel);
        dealerPanel.add(dealerCardPanel);

        mainPanel.add(dealerPanel, BorderLayout.CENTER);
    }

    // ==============================
    // 7. 플레이어 영역
    // ==============================
    private void createPlayerPanel() {

        playerPanel = new JPanel(
                new GridLayout(1, playerCount, 8, 0)
        );
        playerPanel.setOpaque(false);

        for (int i = 0; i < playerCount; i++) {

            JPanel playerBox = new JPanel();
            playerBox.setOpaque(false);
            playerBox.setLayout(
                    new BoxLayout(
                            playerBox,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel nameLabel = createLabel(
                    "PLAYER " + (i + 1),
                    17,
                    Color.WHITE
            );

            JLabel scoreLabel = createLabel(
                    "SCORE : 0",
                    15,
                    GOLD
            );

            JPanel cardPanel = new JPanel(
                    new FlowLayout(FlowLayout.CENTER, 2, 2)
            );
            cardPanel.setOpaque(false);
            cardPanel.setPreferredSize(
                    new Dimension(140, 105)
            );

            nameLabel.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );
            scoreLabel.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            playerBox.add(nameLabel);
            playerBox.add(scoreLabel);
            playerBox.add(cardPanel);

            playerNameLabels.add(nameLabel);
            playerScoreLabels.add(scoreLabel);
            playerCardPanels.add(cardPanel);

            playerPanel.add(playerBox);
        }

        JPanel bottomContainer = new JPanel(
                new BorderLayout(0, 15)
        );
        bottomContainer.setOpaque(false);

        bottomContainer.add(
                playerPanel, BorderLayout.CENTER
        );

        createButtonPanel();

        bottomContainer.add(
                buttonPanel, BorderLayout.SOUTH
        );

        mainPanel.add(
                bottomContainer, BorderLayout.SOUTH
        );
    }

    // ==============================
    // 8. 버튼 영역
    // ==============================
    private void createButtonPanel() {

        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(
                new FlowLayout(FlowLayout.CENTER, 8, 5)
        );

        hitButton = createButton("HIT");
        standButton = createButton("STAND");
        doubleButton = createButton("DOUBLE");

        betPlusButton = createButton("BET +");
        betMinusButton = createButton("BET -");
        betConfirmButton = createButton("BET 확정");

        nextRoundButton = createButton("다음 라운드");
        restartButton = createButton("재시작");

        betLabel = createLabel(
                "BET : $100", 16, GOLD
        );

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleButton);

        buttonPanel.add(betMinusButton);
        buttonPanel.add(betLabel);
        buttonPanel.add(betPlusButton);
        buttonPanel.add(betConfirmButton);

        buttonPanel.add(nextRoundButton);
        buttonPanel.add(restartButton);

        registerButtonEvents();
    }

    // ==============================
    // 9. 버튼 이벤트 연결
    // ==============================
    private void registerButtonEvents() {

        hitButton.addActionListener(e -> {
            resultLabel.setText("HIT 선택");
            // TODO: GameController.hit() 연결
        });

        standButton.addActionListener(e -> {
            resultLabel.setText("STAND 선택");
            // TODO: GameController.stand() 연결
        });

        doubleButton.addActionListener(e -> {
            resultLabel.setText("DOUBLE 선택");
            // TODO: GameController.doubleDown() 연결
        });

        betPlusButton.addActionListener(e -> {
            currentBet += 50;
            updateBetDisplay();
        });

        betMinusButton.addActionListener(e -> {
            currentBet = Math.max(50, currentBet - 50);
            updateBetDisplay();
        });

        betConfirmButton.addActionListener(e -> {
            bettingFinished = true;

            resultLabel.setText(
                    "베팅 확정 : $" + currentBet
            );

            setActionButtonsEnabled(true);

            betPlusButton.setEnabled(false);
            betMinusButton.setEnabled(false);
            betConfirmButton.setEnabled(false);

            // TODO: GameController.startRound() 연결
        });

        nextRoundButton.addActionListener(e -> {
            startNextRound();
        });

        restartButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "게임을 재시작하시겠습니까?",
                    "게임 재시작",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new GameGUI(playerCount);
            }
        });

        nextRoundButton.setEnabled(false);
    }

    // ==============================
    // 10. 카드 표시
    // ==============================
    public void addDealerCard(String imageName) {

        JLabel cardLabel = createCardLabel(imageName);

        dealerCardPanel.add(cardLabel);
        dealerCardPanel.revalidate();
        dealerCardPanel.repaint();
    }

    public void addPlayerCard(
            int playerIndex,
            String imageName
    ) {

        if (!isValidPlayerIndex(playerIndex)) {
            return;
        }

        JLabel cardLabel = createCardLabel(imageName);

        JPanel cardPanel =
                playerCardPanels.get(playerIndex);

        cardPanel.add(cardLabel);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private JLabel createCardLabel(String imageName) {

        JLabel label = new JLabel();

        ImageIcon icon = loadImage(
                "/images/cards/" + imageName,
                65, 95
        );

        if (icon != null) {
            label.setIcon(icon);
        } else {
            label.setText(imageName);
            label.setForeground(Color.WHITE);
        }

        return label;
    }

    // ==============================
    // 11. 점수 표시
    // ==============================
    public void updateDealerScore(int score) {
        dealerScoreLabel.setText(
                "DEALER : " + score
        );
    }

    public void hideDealerScore() {
        dealerScoreLabel.setText("DEALER : ?");
    }

    public void updatePlayerScore(
            int playerIndex,
            int score
    ) {

        if (!isValidPlayerIndex(playerIndex)) {
            return;
        }

        playerScoreLabels.get(playerIndex).setText(
                "SCORE : " + score
        );
    }

    // ==============================
    // 12. 현재 플레이어 표시
    // ==============================
    public void setCurrentPlayer(int playerIndex) {

        if (!isValidPlayerIndex(playerIndex)) {
            return;
        }

        currentPlayerIndex = playerIndex;
        updateTurnDisplay();
    }

    private void updateTurnDisplay() {

        turnLabel.setText(
                "현재 차례 : PLAYER "
                + (currentPlayerIndex + 1)
        );

        for (int i = 0; i < playerCount; i++) {

            if (i == currentPlayerIndex) {
                playerNameLabels.get(i).setForeground(
                        Color.YELLOW
                );
            } else {
                playerNameLabels.get(i).setForeground(
                        Color.WHITE
                );
            }
        }
    }

    // ==============================
    // 13. 승패 결과 표시
    // ==============================
    public void showResult(String message) {
        resultLabel.setText(message);
    }

    public void finishRound(String message) {

        showResult(message);

        setActionButtonsEnabled(false);
        nextRoundButton.setEnabled(true);
    }

    // ==============================
    // 14. 다음 라운드
    // ==============================
    private void startNextRound() {

        round++;
        currentPlayerIndex = 0;
        bettingFinished = false;
        currentBet = 100;

        dealerCardPanel.removeAll();

        for (JPanel panel : playerCardPanels) {
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
        }

        for (JLabel scoreLabel : playerScoreLabels) {
            scoreLabel.setText("SCORE : 0");
        }

        hideDealerScore();

        roundLabel.setText("ROUND " + round);
        resultLabel.setText("베팅을 진행해주세요.");

        updateTurnDisplay();
        updateBetDisplay();

        setActionButtonsEnabled(false);

        betPlusButton.setEnabled(true);
        betMinusButton.setEnabled(true);
        betConfirmButton.setEnabled(true);
        nextRoundButton.setEnabled(false);

        dealerCardPanel.revalidate();
        dealerCardPanel.repaint();
    }

    // ==============================
    // 15. 버튼 상태 변경
    // ==============================
    public void setActionButtonsEnabled(
            boolean enabled
    ) {
        hitButton.setEnabled(enabled);
        standButton.setEnabled(enabled);
        doubleButton.setEnabled(enabled);
    }

    private void updateBetDisplay() {
        betLabel.setText(
                "BET : $" + currentBet
        );
    }

    private boolean isValidPlayerIndex(int index) {
        return index >= 0 && index < playerCount;
    }

    // ==============================
    // 16. 공통 JLabel 생성
    // ==============================
    private JLabel createLabel(
            String text,
            int fontSize,
            Color color
    ) {

        JLabel label = new JLabel(
                text, SwingConstants.CENTER
        );

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        fontSize
                )
        );

        label.setForeground(color);

        return label;
    }

    // ==============================
    // 17. 공통 JButton 생성
    // ==============================
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(
                new Dimension(105, 38)
        );

        return button;
    }

    // ==============================
    // 18. 이미지 불러오기
    // ==============================
    private ImageIcon loadImage(
            String path,
            int width,
            int height
    ) {

        java.net.URL url =
                getClass().getResource(path);

        if (url == null) {
            System.out.println(
                    "이미지를 찾을 수 없습니다: " + path
            );
            return null;
        }

        ImageIcon original = new ImageIcon(url);

        Image scaled = original.getImage()
                .getScaledInstance(
                        width,
                        height,
                        Image.SCALE_SMOOTH
                );

        return new ImageIcon(scaled);
    }

    // ==============================
    // 19. 배경 이미지 JPanel
    // ==============================
    private class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel(String path) {

            java.net.URL url =
                    getClass().getResource(path);

            if (url != null) {
                backgroundImage =
                        new ImageIcon(url).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                g.drawImage(
                        backgroundImage,
                        0, 0,
                        getWidth(),
                        getHeight(),
                        this
                );
            } else {
                g.setColor(DARK_GREEN);
                g.fillRect(
                        0, 0,
                        getWidth(),
                        getHeight()
                );
            }
        }
    }

    // ==============================
    // 20. 단독 실행 테스트
    // ==============================
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new GameGUI(3);
        });
    }
}
