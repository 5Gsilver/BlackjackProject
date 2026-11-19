
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import game.BlackjackGame;
import model.Player;
import model.Dealer;
import model.Deck;

public class WarningScreen extends JFrame {

    private JCheckBox agreeCheckBox;
    private JButton startButton;

    public WarningScreen() {
        initializeUI();
        createWarningPanel();
        createButtonPanel();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Warning");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 70, 50));
        setResizable(false);
    }

    private void createWarningPanel() {

        JPanel warningPanel = new JPanel();

        warningPanel.setLayout(
                new BoxLayout(warningPanel, BoxLayout.Y_AXIS));

        warningPanel.setBackground(new Color(20, 70, 50));

        warningPanel.setBorder(
                BorderFactory.createEmptyBorder(80, 50, 30, 50));

        JLabel titleLabel =
                new JLabel("BLACKJACK WARNING");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 36));

        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel.setMaximumSize(
                new Dimension(700, 60));

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER);

        JLabel warningLabel = new JLabel(
                "<html><div style='text-align:center;'>"
                + "이 게임은 교육 목적으로만 제작되었습니다.<br><br>"
                + "실제 돈은 사용되지 않습니다.<br><br>"
                + "도박은 금전적 손실과 개인적인 피해를 초래할 수 있습니다.<br><br>"
                + "</div></html>");

        warningLabel.setFont(
                new Font("Arial", Font.PLAIN, 20));

        warningLabel.setForeground(Color.WHITE);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        warningLabel.setMaximumSize(
                new Dimension(700, 250));

        warningLabel.setHorizontalAlignment(
                SwingConstants.CENTER);

        warningPanel.add(titleLabel);
        warningPanel.add(Box.createVerticalStrut(50));
        warningPanel.add(warningLabel);

        add(warningPanel, BorderLayout.CENTER);
    }

    private void createButtonPanel() {

        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(
                new FlowLayout(FlowLayout.CENTER, 20, 20));

        buttonPanel.setBackground(new Color(20, 70, 50));

        agreeCheckBox =
                new JCheckBox("이 경고를 읽고 이해했습니다.");

        agreeCheckBox.setFont(
                new Font("맑은 고딕", Font.PLAIN, 16));

        agreeCheckBox.setForeground(Color.WHITE);
        agreeCheckBox.setBackground(new Color(20, 70, 50));

        startButton = new JButton("게임 시작");

        startButton.setFont(
                new Font("맑은 고딕", Font.BOLD, 20));

        startButton.setEnabled(false);

        agreeCheckBox.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        startButton.setEnabled(
                                agreeCheckBox.isSelected());
                    }
                });

        startButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        startGame();
                    }
                });

        buttonPanel.add(agreeCheckBox);
        buttonPanel.add(startButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startGame() {

        if (!agreeCheckBox.isSelected()) {

            JOptionPane.showMessageDialog(
                    this,
                    "경고 내용을 확인해주세요.");

            return;
        }

        SetupScreen setupScreen = new SetupScreen();

        setupScreen.setStartGameListener(
                new SetupScreen.StartGameListener() {

                    @Override
                    public void onStartGame(
                            ArrayList<SetupScreen.PlayerSetting> settings) {

                        ArrayList<Player> players =
                                new ArrayList<Player>();

                        for (int i = 0; i < settings.size(); i++) {

                            SetupScreen.PlayerSetting setting =
                                    settings.get(i);

                            Player player = new Player(
                                    setting.getName(),
                                    setting.getInitialChips());

                            players.add(player);

                            System.out.println(
                                    setting.getName() + " : "
                                    + setting.getInitialChips());
                        }

                        Dealer dealer = new Dealer();

                        Deck deck = new Deck(8);

                        BlackjackGame game =
                                new BlackjackGame(
                                        players,
                                        dealer,
                                        deck);

                        GameGUI gameGUI = new GameGUI(game);

                        gameGUI.setVisible(true);
                    }
                });

        setupScreen.setVisible(true);

        dispose();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new WarningScreen();
            }
        });
    }
}
