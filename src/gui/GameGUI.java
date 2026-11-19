package gui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.net.URL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import game.BlackjackGame;

/** Casino-inspired blackjack table UI. Game rules are supplied by a controller. */
public class GameGUI extends JFrame {
    private BlackjackGame game;

    public void setGame(BlackjackGame game) {
        if (game == null) throw new IllegalArgumentException("게임 객체가 null입니다.");
        if (game.getPlayers().size() != playerCount) {
            throw new IllegalArgumentException("GUI와 게임의 플레이어 수가 다릅니다.");
        }
        this.game = game;
        bettingPlayerIndex = 0;
        moveToNextBettingPlayer();
        refreshGameState();
    }

    public GameGUI(BlackjackGame game) {
        this(game.getPlayers().size());
        setGame(game);
    }

    private static final int W = 1280, H = 820;
    private static final Color GOLD = new Color(225, 186, 99);
    private static final Color CREAM = new Color(255, 244, 214);
    private static final Color DARK = new Color(11, 18, 19);
    private final int playerCount;
    private int currentPlayerIndex = 0, round = 1, currentBet = 100;
    private boolean bettingFinished = false;
    private int bettingPlayerIndex = 0;
    private final List<JPanel> chipPanels = new ArrayList<JPanel>();
    private int[] visibleBets;
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
        visibleBets = new int[this.playerCount];
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
        dealerImageLabel.setBounds(470, 94, 340, 230);
        ImageIcon dealer = image("/dealer/dealer_idle.png", 340, 230);
        if (dealer != null) { dealerImageLabel.setIcon(dealer); dealerImageLabel.setText(""); }
        table.add(dealerImageLabel);

        dealerScoreLabel = label("DEALER  •  ?", 17, GOLD);
        dealerScoreLabel.setBounds(520, 322, 240, 28);
        table.add(dealerScoreLabel);
        dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, -18, 0));
        dealerCardPanel.setOpaque(false);
        dealerCardPanel.setBounds(420, 405, 440, 112);
        table.add(dealerCardPanel);

        createSeats();
        turnLabel = label("현재 차례  •  PLAYER 1", 17, CREAM);
        turnLabel.setBounds(380, 34, 520, 28);
        table.add(turnLabel);
        resultLabel = label("베팅 금액을 선택하고 BET 확정을 누르세요.", 15, GOLD);
        resultLabel.setBounds(310, 65, 660, 24);
        table.add(resultLabel);
        createControls();
        updateTurnDisplay();
        updateBetDisplay();
        setActionButtonsEnabled(false);
        betConfirmButton.setEnabled(false);
    }

    private void createSeats() {
        int[][] anchors = {
            {145, 558}, {280, 615}, {445, 648},
            {640, 648}, {820, 648}, {980, 615}, {1120, 558}
        };
        int[][] order = {
            {3}, {2, 4}, {1, 3, 5}, {1, 2, 4, 5},
            {1, 2, 3, 4, 5}, {0, 1, 2, 4, 5, 6},
            {0, 1, 2, 3, 4, 5, 6}
        };
        for (int i = 0; i < playerCount; i++) {
            int[] xy = anchors[order[playerCount - 1][i]];
            JPanel seat = new JPanel();
            seat.setLayout(new BoxLayout(seat, BoxLayout.Y_AXIS));
            seat.setOpaque(false);
            seat.setBounds(xy[0] - 83, xy[1] - 166, 166, 126);
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

            JPanel chips = new JPanel(null);
            chips.setOpaque(false);
            chips.setBounds(xy[0] - 70,xy[1]-50,140,76);
            table.add(chips);
            chipPanels.add(chips);
        }
    }

    private void updateChipStack(int playerIndex, int amount) {
        JPanel chips = chipPanels.get(playerIndex);
        chips.removeAll();
        if (amount <= 0) {
            chips.repaint();
            return;
        }
        int[] denominations = {500, 100, 25, 10, 5, 1};
        int remainder = amount;
        int shown = 0;
        for (int denomination : denominations) {
            int count = remainder / denomination;
            remainder %= denomination;
            if (count == 0) continue;
            int displayCount = Math.min(count, 5);
            for (int j = 0; j < displayCount; j++) {
                if (shown >= 12) break;
                JLabel chip = new JLabel();
                ImageIcon icon = image("/chips/chip_" + denomination + ".png", 58, 58);
                if (icon != null) {
                    chip.setIcon(icon);
                } else {
                    chip.setOpaque(true);
                    chip.setBackground(GOLD);
                    chip.setText("" + denomination);
                    chip.setHorizontalAlignment(SwingConstants.CENTER);
                }
                int column = shown / 4;
                int level = shown % 4;
                chip.setBounds(8 + column * 31, 17 - level * 6, 58, 58);
                chips.add(chip, 0);
                shown++;
            }
        }
        chips.revalidate();
        chips.repaint();
    }

    private boolean isBettingPhase() {
        if (game == null) return false;
        BlackjackGame.GameState state = game.getGameState();
        return state == BlackjackGame.GameState.WAITING
                || state == BlackjackGame.GameState.BETTING;
    }

    private void moveToNextBettingPlayer() {
        if (game == null) return;
        ArrayList<model.Player> players = game.getPlayers();
        while (bettingPlayerIndex < playerCount) {
            model.Player player = players.get(bettingPlayerIndex);
            if (player.getChips() > 0 && player.getCurrentBet() == 0) break;
            bettingPlayerIndex++;
        }
        if (bettingPlayerIndex < playerCount) {
            model.Player player = players.get(bettingPlayerIndex);
            currentBet = Math.min(100, player.getChips());
            updateBetDisplay();
        }
    }

    private void confirmCurrentBet() {
        if (!isBettingPhase() || bettingPlayerIndex >= playerCount) return;
        model.Player player = game.getPlayers().get(bettingPlayerIndex);
        if (currentBet < 1 || currentBet > player.getChips()) {
            showResult(player.getName() + "의 베팅액을 확인하세요.");
            return;
        }
        if (!game.placeBet(bettingPlayerIndex, currentBet)) {
            showResult("베팅 실패: " + player.getName());
            return;
        }
        visibleBets[bettingPlayerIndex] = player.getCurrentBet();
        updateChipStack(bettingPlayerIndex, visibleBets[bettingPlayerIndex]);
        bettingPlayerIndex++;
        moveToNextBettingPlayer();
        if (bettingPlayerIndex >= playerCount) {
            try {
                game.startGame();
                bettingFinished = true;
            } catch (IllegalStateException ex) {
                showResult(ex.getMessage());
            }
        }
        refreshGameState();
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
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!checkPlayerTurn()) return;
                game.hit();
                refreshGameState();
            }
        });
        standButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!checkPlayerTurn()) return;
                game.stand();
                refreshGameState();
            }
        });
        doubleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!checkPlayerTurn()) return;
                if (!game.getCurrentPlayer().canDouble()) {
                    showResult("DOUBLE은 첫 두 장의 카드와 추가 베팅 칩이 필요합니다.");
                    return;
                }
                game.doubleDown();
                refreshGameState();
            }
        });
        betPlusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isBettingPhase() || bettingPlayerIndex >= playerCount) return;
                int chips = game.getPlayers().get(bettingPlayerIndex).getChips();
                currentBet = Math.min(chips, currentBet + 50);
                updateBetDisplay();
            }
        });
        betMinusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isBettingPhase()) return;
                currentBet = Math.max(1, currentBet - 50);
                updateBetDisplay();
            }
        });
        betConfirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmCurrentBet();
            }
        });
        nextRoundButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (game == null || game.getGameState() != BlackjackGame.GameState.ROUND_END) {
                    showResult("아직 다음 라운드로 진행할 수 없습니다.");
                    return;
                }
                game.nextRound();
                clearRoundView();
                bettingPlayerIndex = 0;
                moveToNextBettingPlayer();
                refreshGameState();
                showResult(game.getGameState() == BlackjackGame.GameState.BETTING
                        ? "베팅 금액을 선택하고 BET 확정을 누르세요."
                        : "게임 종료: 남은 칩이 없습니다.");
            }
        });
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new WarningScreen();
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
    private static Image loadResource(String path) {
        URL url = GameGUI.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        }
        String relative = path.startsWith("/") ? path.substring(1) : path;
        String[] candidates = {
            "resources/" + relative,
            "resource/" + relative,
            relative,
            "../resources/" + relative
        };
        for (String candidate : candidates) {
            File file = new File(candidate);
            if (file.isFile()) {
                return new ImageIcon(file.getAbsolutePath()).getImage();
            }
        }
        System.err.println("이미지를 찾을 수 없습니다: " + path);
        return null;
    }

    private ImageIcon image(String path, int w, int h) {
        Image source = loadResource(path);
        if (source == null) return null;
        return new ImageIcon(source.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
    private JLabel card(String imageName) {
        JLabel l = label(imageName, 10, DARK);
        ImageIcon icon = image("/cards/" + imageName, 47, 66);
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
    private boolean checkPlayerTurn() {
        if (game == null) {
            showResult("게임 객체가 연결되지 않았습니다.");
            return false;
        }
        if (game.getGameState() != BlackjackGame.GameState.PLAYER_TURN) {
            showResult("현재 플레이어 턴이 아닙니다.");
            return false;
        }
        return true;
    }

    public void refreshGameState() {
        if (game == null) {
            setActionButtonsEnabled(false);
            betConfirmButton.setEnabled(false);
            betPlusButton.setEnabled(false);
            betMinusButton.setEnabled(false);
            return;
        }
        BlackjackGame.GameState state = game.getGameState();
        ArrayList<model.Player> players = game.getPlayers();
        for (int i = 0; i < playerCount; i++) {
            model.Player player = players.get(i);
            playerNameLabels.get(i).setText(
                    isBettingPhase() && i == bettingPlayerIndex ? "◆ " + player.getName() : player.getName());
            playerScoreLabels.get(i).setText("SCORE " + player.getScore()
                    + "  |  CHIPS " + player.getChips());
            if (player.getCurrentBet() > 0) visibleBets[i] = player.getCurrentBet();
            updateChipStack(i, visibleBets[i]);
            playerBetLabels.get(i).setText("BET " + visibleBets[i]
                    + (state == BlackjackGame.GameState.ROUND_END
                            ? "  " + player.getResult() : ""));
            JPanel panel = playerCardPanels.get(i);
            panel.removeAll();
            for (model.Card card : game.getPlayerHand(i)) {
                panel.add(card(card.getImageFileName()));
            }
            panel.revalidate();
            panel.repaint();
        }
        dealerCardPanel.removeAll();
        ArrayList<model.Card> dealerCards = game.getDealerHand();
        boolean reveal = game.getDealer().isHoleCardRevealed()
                || state == BlackjackGame.GameState.ROUND_END;
        for (int i = 0; i < dealerCards.size(); i++) {
            dealerCardPanel.add(card(i == 1 && !reveal
                    ? "BACK.png" : dealerCards.get(i).getImageFileName()));
        }
        dealerCardPanel.revalidate();
        dealerCardPanel.repaint();
        if (reveal) updateDealerScore(game.getDealerScore());
        else if (dealerCards.size() > 0) {
            dealerScoreLabel.setText("DEALER  •  "
                    + game.getDealer().getOpenCard().getValue() + " + ?");
        } else hideDealerScore();
        boolean betting = state == BlackjackGame.GameState.WAITING
                || state == BlackjackGame.GameState.BETTING;
        betPlusButton.setEnabled(betting && bettingPlayerIndex < playerCount);
        betMinusButton.setEnabled(betting && bettingPlayerIndex < playerCount);
        betConfirmButton.setEnabled(betting && bettingPlayerIndex < playerCount);
        boolean turn = state == BlackjackGame.GameState.PLAYER_TURN;
        setActionButtonsEnabled(turn);
        int index = game.getCurrentPlayerIndex();
        if (turn && valid(index)) {
            setCurrentPlayer(index);
            doubleButton.setEnabled(players.get(index).canDouble());
            turnLabel.setText("현재 차례  •  " + players.get(index).getName());
        } else if (state == BlackjackGame.GameState.ROUND_END) {
            turnLabel.setText("라운드 종료");
        } else if (betting) {
            turnLabel.setText(bettingPlayerIndex < playerCount
                    ? "베팅 차례  •  " + players.get(bettingPlayerIndex).getName() : "베팅 완료");
        } else {
            turnLabel.setText("게임 종료");
        }
        nextRoundButton.setEnabled(state == BlackjackGame.GameState.ROUND_END);
        if (state == BlackjackGame.GameState.ROUND_END) {
            StringBuilder summary = new StringBuilder();
            for (model.Player player : players) {
                if (summary.length() > 0) summary.append("   |   ");
                summary.append(player.getName()).append(": ").append(player.getResult());
            }
            showResult(summary.toString());
        } else if (turn) {
            showResult(players.get(index).getName() + "의 차례입니다.");
        }
    }

    private void clearRoundView() {
        round++;
        currentBet = 100;
        bettingFinished = false;
        visibleBets = new int[playerCount];
        for (int i = 0; i < chipPanels.size(); i++) updateChipStack(i, 0);
        roundLabel.setText("ROUND " + round);
        dealerCardPanel.removeAll();
        dealerCardPanel.revalidate();
        dealerCardPanel.repaint();
        for (int i = 0; i < playerCount; i++) {
            JPanel panel = playerCardPanels.get(i);
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
            playerScoreLabels.get(i).setText("SCORE  0");
            playerBetLabels.get(i).setText("BET  $0");
        }
        hideDealerScore();
        updateBetDisplay();
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
            background = loadResource("/backgrounds/blackjack_table.png");
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
}
