package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Casino-inspired blackjack table UI. Game rules are supplied by a controller. */
public class GameGUI extends JFrame {
    private static final int W = 1280, H = 820;
    private static final Color GOLD = new Color(225, 186, 99);
    private static final Color CREAM = new Color(255, 244, 214);
    private static final Color DARK = new Color(11, 18, 19);
    private final int playerCount;
    private int currentPlayerIndex = 0, round = 1, currentBet = 100;
    private boolean bettingFinished = false;
    private final List<JPanel> playerCardPanels = new ArrayList<>();
    private final List<JLabel> playerScoreLabels = new ArrayList<>();
    private final List<JLabel> playerNameLabels = new ArrayList<>();
    private final List<JLabel> playerBetLabels = new ArrayList<>();
    private final List<JPanel> seatPanels = new ArrayList<>();
    private TablePanel table;
    private JPanel dealerCardPanel;
    private JLabel dealerScoreLabel, turnLabel, resultLabel, roundLabel, betLabel, dealerImageLabel;
    private JButton hitButton, standButton, doubleButton, betPlusButton, betMinusButton;
    private JButton betConfirmButton, nextRoundButton, restartButton;

    public GameGUI(int playerCount) {
        this.playerCount = Math.max(1, Math.min(7, playerCount));
        setTitle("BLACKJACK | LIVE TABLE");
        setSize(W, H);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeGUI();
        setVisible(true);
    }

    private void initializeGUI() {
        table = new TablePanel();
        table.setLayout(null);
        setContentPane(table);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBounds(36, 14, 1208, 50);
        JLabel title = label("BLACKJACK  •  LIVE TABLE", 22, GOLD);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        header.add(title, BorderLayout.WEST);
        roundLabel = label("ROUND " + round, 18, CREAM);
        header.add(roundLabel, BorderLayout.EAST);
        table.add(header);

        dealerImageLabel = new JLabel("DEALER", SwingConstants.CENTER);
        dealerImageLabel.setForeground(CREAM);
        dealerImageLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        dealerImageLabel.setBounds(510, 58, 260, 205);
        ImageIcon dealer = image("/images/dealer/dealer_idle.png", 260, 205);
        if (dealer != null) { dealerImageLabel.setIcon(dealer); dealerImageLabel.setText(""); }
        table.add(dealerImageLabel);

        dealerScoreLabel = label("DEALER  •  ?", 17, GOLD);
        dealerScoreLabel.setBounds(520, 261, 240, 28);
        table.add(dealerScoreLabel);
        dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -18, 0));
        dealerCardPanel.setOpaque(false);
        dealerCardPanel.setBounds(420, 294, 440, 112);
        table.add(dealerCardPanel);

        createSeats();
        turnLabel = label("현재 차례  •  PLAYER 1", 17, CREAM);
        turnLabel.setBounds(300, 604, 680, 26);
        table.add(turnLabel);
        resultLabel = label("베팅 금액을 선택하고 BET 확정을 누르세요.", 15, GOLD);
        resultLabel.setBounds(240, 635, 800, 26);
        table.add(resultLabel);
        createControls();
        updateTurnDisplay();
        updateBetDisplay();
        setActionButtonsEnabled(false);
    }

    private void createSeats() {
        // Seven anchors follow the lower curve of the felt table.
        int[][] anchors = {{640, 510}, {430, 516}, {850, 516}, {265, 493},
                           {1015, 493}, {120, 455}, {1160, 455}};
        int[][] order = {{0}, {1,2}, {1,0,2}, {3,1,2,4},
                         {3,1,0,2,4}, {5,3,1,2,4,6}, {5,3,1,0,2,4,6}};
        for (int i = 0; i < playerCount; i++) {
            int[] xy = anchors[order[playerCount - 1][i]];
            JPanel seat = new JPanel();
            seat.setLayout(new BoxLayout(seat, BoxLayout.Y_AXIS));
            seat.setOpaque(false);
            seat.setBounds(xy[0] - 83, xy[1] - 42, 166, 126);
            JLabel name = label("PLAYER " + (i + 1), 14, CREAM);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel score = label("SCORE  0", 12, GOLD);
            score.setAlignmentX(Component.CENTER_ALIGNMENT);
            JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, -26, 0));
            cards.setOpaque(false);
            cards.setPreferredSize(new Dimension(166, 68));
            cards.setMaximumSize(new Dimension(166, 68));
            JLabel bet = label("BET  $0", 12, CREAM);
            bet.setAlignmentX(Component.CENTER_ALIGNMENT);
            seat.add(name); seat.add(score); seat.add(cards); seat.add(bet);
            table.add(seat);
            seatPanels.add(seat);
            playerNameLabels.add(name);
            playerScoreLabels.add(score);
            playerCardPanels.add(cards);
            playerBetLabels.add(bet);
        }
    }

    private void createControls() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 13));
        bar.setBackground(new Color(9, 18, 18));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GOLD));
        bar.setBounds(0, 679, W, 103);
        table.add(bar);
        hitButton = button("HIT", new Color(32, 112, 84));
        standButton = button("STAND", new Color(145, 59, 58));
        doubleButton = button("DOUBLE", new Color(61, 87, 139));
        betMinusButton = button("− 50", new Color(65, 64, 54));
        betPlusButton = button("+ 50", new Color(65, 64, 54));
        betConfirmButton = button("BET 확정", new Color(145, 110, 47));
        nextRoundButton = button("다음 라운드", new Color(91, 72, 43));
        restartButton = button("재시작", new Color(55, 59, 63));
        betLabel = label("BET $100", 15, GOLD);
        betLabel.setPreferredSize(new Dimension(106, 45));
        for (JComponent c : new JComponent[]{hitButton, standButton, doubleButton,
                betMinusButton, betLabel, betPlusButton, betConfirmButton,
                nextRoundButton, restartButton}) bar.add(c);
        nextRoundButton.setEnabled(false);
        hitButton.addActionListener(e -> showResult("HIT 선택 — 게임 컨트롤러 연결 필요"));
        standButton.addActionListener(e -> showResult("STAND 선택 — 게임 컨트롤러 연결 필요"));
        doubleButton.addActionListener(e -> showResult("DOUBLE 선택 — 게임 컨트롤러 연결 필요"));
        betPlusButton.addActionListener(e -> { currentBet += 50; updateBetDisplay(); });
        betMinusButton.addActionListener(e -> { currentBet = Math.max(50, currentBet - 50); updateBetDisplay(); });
        betConfirmButton.addActionListener(e -> {
            bettingFinished = true;
            playerBetLabels.get(currentPlayerIndex).setText("BET  $" + currentBet);
            showResult("베팅 확정  •  $" + currentBet);
            setActionButtonsEnabled(true);
            betPlusButton.setEnabled(false);
            betMinusButton.setEnabled(false);
            betConfirmButton.setEnabled(false);
        });
        nextRoundButton.addActionListener(e -> startNextRound());
        restartButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "게임을 재시작하시겠습니까?",
                    "재시작", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose(); new GameGUI(playerCount);
            }
        });
    }

    private JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }
    private JButton button(String text, Color background) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(background);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 184, 119), 1),
                new EmptyBorder(9, 9, 9, 9)));
        b.setPreferredSize(new Dimension(text.equals("다음 라운드") ? 110 : 94, 43));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private ImageIcon image(String path, int w, int h) {
        URL url = getClass().getResource(path);
        if (url == null) return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
    private JLabel card(String imageName) {
        JLabel l = label(imageName, 10, DARK);
        ImageIcon icon = image("/images/cards/" + imageName, 47, 66);
        if (icon != null) { l.setIcon(icon); l.setText(""); }
        else { l.setOpaque(true); l.setBackground(CREAM); l.setPreferredSize(new Dimension(47, 66)); }
        return l;
    }
    public void addDealerCard(String imageName) {
        dealerCardPanel.add(card(imageName));
        dealerCardPanel.revalidate(); dealerCardPanel.repaint();
    }
    public void addPlayerCard(int playerIndex, String imageName) {
        if (!valid(playerIndex)) return;
        JPanel p = playerCardPanels.get(playerIndex);
        p.add(card(imageName)); p.revalidate(); p.repaint();
    }
    public void updateDealerScore(int score) { dealerScoreLabel.setText("DEALER  •  " + score); }
    public void hideDealerScore() { dealerScoreLabel.setText("DEALER  •  ?"); }
    public void updatePlayerScore(int playerIndex, int score) {
        if (valid(playerIndex)) playerScoreLabels.get(playerIndex).setText("SCORE  " + score);
    }
    public void setCurrentPlayer(int playerIndex) {
        if (valid(playerIndex)) { currentPlayerIndex = playerIndex; updateTurnDisplay(); }
    }
    private void updateTurnDisplay() {
        turnLabel.setText("현재 차례  •  PLAYER " + (currentPlayerIndex + 1));
        for (int i = 0; i < playerCount; i++) {
            playerNameLabels.get(i).setForeground(i == currentPlayerIndex ? GOLD : CREAM);
            playerNameLabels.get(i).setText((i == currentPlayerIndex ? "◆ " : "") + "PLAYER " + (i + 1));
        }
    }
    public void showResult(String message) { resultLabel.setText(message); }
    public void finishRound(String message) {
        showResult(message); setActionButtonsEnabled(false); nextRoundButton.setEnabled(true);
    }
    private void startNextRound() {
        round++; currentPlayerIndex = 0; currentBet = 100; bettingFinished = false;
        roundLabel.setText("ROUND " + round);
        dealerCardPanel.removeAll(); dealerCardPanel.revalidate(); dealerCardPanel.repaint();
        for (int i = 0; i < playerCount; i++) {
            JPanel p = playerCardPanels.get(i);
            p.removeAll(); p.revalidate(); p.repaint();
            playerScoreLabels.get(i).setText("SCORE  0");
            playerBetLabels.get(i).setText("BET  $0");
        }
        hideDealerScore(); updateTurnDisplay(); updateBetDisplay();
        showResult("베팅 금액을 선택하고 BET 확정을 누르세요.");
        setActionButtonsEnabled(false);
        betPlusButton.setEnabled(true); betMinusButton.setEnabled(true);
        betConfirmButton.setEnabled(true); nextRoundButton.setEnabled(false);
    }
    public void setActionButtonsEnabled(boolean enabled) {
        hitButton.setEnabled(enabled); standButton.setEnabled(enabled); doubleButton.setEnabled(enabled);
    }
    private void updateBetDisplay() { betLabel.setText("BET $" + currentBet); }
    private boolean valid(int i) { return i >= 0 && i < playerCount; }

    private static class TablePanel extends JPanel {
        private Image background;
        TablePanel() {
            setBackground(DARK);
            URL url = GameGUI.class.getResource("/backgrounds/blackjack_table.png");
            if (url == null) url = GameGUI.class.getResource("/resource/backgrounds/blackjack_table.png");
            if (url != null) background = new ImageIcon(url).getImage();
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(26, 35, 34), 0, H, DARK));
            g.fillRect(0, 0, getWidth(), getHeight());
            if (background != null) {
                // Use the supplied table artwork at full opacity, not as a translucent overlay.
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                g.dispose();
                return;
            }
            // Fallback table drawing when the PNG is unavailable.
            Shape rim = new RoundRectangle2D.Double(32, 155, 1216, 510, 290, 290);
            g.setColor(new Color(7, 10, 11)); g.setStroke(new BasicStroke(16)); g.draw(rim);
            g.setPaint(new GradientPaint(0, 170, new Color(16, 102, 74), 0, 650, new Color(5, 55, 43)));
            g.fill(rim);
            g.setColor(new Color(208, 171, 91)); g.setStroke(new BasicStroke(4)); g.draw(rim);
            g.setColor(new Color(240, 213, 150, 85)); g.setStroke(new BasicStroke(2));
            g.draw(new Arc2D.Double(102, 240, 1076, 575, 0, 180, Arc2D.OPEN));
            g.dispose();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI(7));
    }
}
