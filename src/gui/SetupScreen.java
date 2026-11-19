package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/** 블랙잭 시작 전에 참가 인원, 이름, 가상 칩을 설정하는 화면. */
public final class SetupScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final int MAX_PLAYERS = 7;
    public static final int MIN_CHIPS = 1;
    public static final int MAX_CHIPS = 1000000;
    public static final int DEFAULT_CHIPS = 1000;

    private static final Color BACKGROUND = new Color(16, 41, 35);
    private static final Color PANEL_COLOR = new Color(25, 62, 49);
    private static final Color GOLD = new Color(232, 194, 111);
    private static final Color WHITE = new Color(245, 245, 240);

    /** 다른 클래스가 GameGUI를 실행하도록 연결하는 창구. */
    public interface StartGameListener {
        void onStartGame(ArrayList<PlayerSetting> settings);
    }

    /** 한 플레이어의 시작 설정을 보관하는 자료형. */
    public static class PlayerSetting {
        private final String name;
        private final int initialChips;

        public PlayerSetting(String name, int initialChips) {
            this.name = name;
            this.initialChips = initialChips;
        }

        public String getName() {
            return name;
        }

        public int getInitialChips() {
            return initialChips;
        }
    }

    private JComboBox<Integer> playerCountBox;
    private JTextField[] nameFields;
    private JTextField[] chipFields;
    private JPanel[] playerRows;
    private JPanel rowsPanel;
    private transient StartGameListener startGameListener;

    public SetupScreen() {
        setTitle("BLACKJACK - 게임 설정");
        setSize(760, 630);
        setMinimumSize(new Dimension(640, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        nameFields = new JTextField[MAX_PLAYERS];
        chipFields = new JTextField[MAX_PLAYERS];
        playerRows = new JPanel[MAX_PLAYERS];

        createScreen();
        updatePlayerRows();
    }

    public SetupScreen(StartGameListener listener) {
        this();
        this.startGameListener = listener;
    }

    public void setStartGameListener(StartGameListener listener) {
        this.startGameListener = listener;
    }

    private void createScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("BLACKJACK");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel infoLabel = new JLabel(
                "실제 금전 거래가 없는 교육용 게임입니다.");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setForeground(WHITE);
        infoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(infoLabel);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel countPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        countPanel.setBackground(BACKGROUND);

        JLabel countLabel = new JLabel("참가 인원 (1~7명)");
        countLabel.setForeground(WHITE);
        countLabel.setFont(
                new Font("SansSerif", Font.BOLD, 15));
        countPanel.add(countLabel);

        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7};
        playerCountBox = new JComboBox<Integer>(numbers);
        playerCountBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updatePlayerRows();
            }
        });

        countPanel.add(playerCountBox);
        topPanel.add(countPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(
                new BorderLayout(0, 12));
        centerPanel.setBackground(PANEL_COLOR);
        centerPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD, 1),
                        new EmptyBorder(16, 16, 16, 16)));

        JPanel headings = new JPanel(
                new GridLayout(1, 3, 12, 0));
        headings.setBackground(PANEL_COLOR);

        headings.add(createHeading("번호"));
        headings.add(createHeading("플레이어 이름"));
        headings.add(createHeading("시작 가상 칩"));
        centerPanel.add(headings, BorderLayout.NORTH);

        rowsPanel = new JPanel();
        rowsPanel.setLayout(
                new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(PANEL_COLOR);

        for (int i = 0; i < MAX_PLAYERS; i++) {
            JPanel row = new JPanel(
                    new GridLayout(1, 3, 12, 0));
            row.setBackground(PANEL_COLOR);
            row.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 40));

            JLabel playerLabel = new JLabel(
                    "플레이어 " + (i + 1));
            playerLabel.setForeground(WHITE);
            playerLabel.setHorizontalAlignment(
                    SwingConstants.CENTER);
            row.add(playerLabel);

            nameFields[i] = new JTextField(
                    "플레이어 " + (i + 1));
            row.add(nameFields[i]);

            chipFields[i] = new JTextField(
                    String.valueOf(DEFAULT_CHIPS));
            row.add(chipFields[i]);

            playerRows[i] = row;
        }

        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND);

        JLabel guideLabel = new JLabel(
                "각 플레이어의 시작 칩: 1~1,000,000 (가상 칩)");
        guideLabel.setForeground(WHITE);
        bottomPanel.add(guideLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(BACKGROUND);

        JButton resetButton = new JButton("초기화");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                resetSettings();
            }
        });
        buttonPanel.add(resetButton);

        JButton exitButton = new JButton("닫기");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        buttonPanel.add(exitButton);

        JButton startButton = new JButton("게임 시작");
        startButton.setBackground(GOLD);
        startButton.setForeground(Color.BLACK);
        startButton.setFont(
                new Font("SansSerif", Font.BOLD, 15));

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                startGame();
            }
        });
        buttonPanel.add(startButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createHeading(String title) {
        JLabel label = new JLabel(
                title, SwingConstants.CENTER);
        label.setForeground(GOLD);
        label.setFont(
                new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private void updatePlayerRows() {
        if (rowsPanel == null) {
            return;
        }

        int count = ((Integer)
                playerCountBox.getSelectedItem()).intValue();

        rowsPanel.removeAll();

        for (int i = 0; i < count; i++) {
            rowsPanel.add(playerRows[i]);
            rowsPanel.add(Box.createVerticalStrut(8));
        }

        rowsPanel.revalidate();
        rowsPanel.repaint();
    }

    private void resetSettings() {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            nameFields[i].setText("플레이어 " + (i + 1));
            chipFields[i].setText(
                    String.valueOf(DEFAULT_CHIPS));
        }

        playerCountBox.setSelectedIndex(0);
        updatePlayerRows();
    }

    private void startGame() {
        int count = ((Integer)
                playerCountBox.getSelectedItem()).intValue();

        String[] names = new String[count];
        String[] chips = new String[count];

        for (int i = 0; i < count; i++) {
            names[i] = nameFields[i].getText();
            chips[i] = chipFields[i].getText();
        }

        ArrayList<PlayerSetting> settings;

        try {
            settings = validateSettings(names, chips, count);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "설정 오류",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (startGameListener == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "게임 실행 연결이 등록되지 않았습니다.\n"
                    + "WarningScreen에서 StartGameListener를 연결해 주세요.",
                    "연결 필요",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            startGameListener.onStartGame(settings);
            dispose();
        } catch (RuntimeException exception) {
            exception.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "게임 화면을 여는 중 오류가 발생했습니다: "
                    + exception.getMessage(),
                    "실행 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 입력 검사 기능을 화면과 분리하여 단독 테스트할 수 있게 함. */
    public static ArrayList<PlayerSetting> validateSettings(
            String[] names, String[] chipTexts, int count) {

        if (names == null || chipTexts == null
                || count < 1 || count > MAX_PLAYERS
                || names.length < count
                || chipTexts.length < count) {

            throw new IllegalArgumentException(
                    "플레이어 수 또는 입력 자료가 올바르지 않습니다.");
        }

        ArrayList<PlayerSetting> result =
                new ArrayList<PlayerSetting>();

        for (int i = 0; i < count; i++) {
            if (names[i] == null || chipTexts[i] == null) {
                throw new IllegalArgumentException(
                        (i + 1) + "번 플레이어의 입력값이 없습니다.");
            }

            String name = names[i].trim();

            if (name.length() == 0 || name.length() > 20) {
                throw new IllegalArgumentException(
                        (i + 1)
                        + "번 플레이어의 이름은 1~20자여야 합니다.");
            }

            for (int j = 0; j < result.size(); j++) {
                if (result.get(j).getName()
                        .equalsIgnoreCase(name)) {

                    throw new IllegalArgumentException(
                            "플레이어 이름은 중복될 수 없습니다: "
                            + name);
                }
            }

            int chips;

            try {
                chips = Integer.parseInt(
                        chipTexts[i].trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        (i + 1)
                        + "번 플레이어의 칩은 정수로 입력해 주세요.");
            }

            if (chips < MIN_CHIPS || chips > MAX_CHIPS) {
                throw new IllegalArgumentException(
                        (i + 1)
                        + "번 플레이어의 칩은 1~1,000,000이어야 합니다.");
            }

            result.add(new PlayerSetting(name, chips));
        }

        return result;
    }

    /** 설정 화면만 단독으로 실행할 때 사용. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SetupScreen screen =
                        new SetupScreen();
                screen.setVisible(true);
            }
        });
    }
}