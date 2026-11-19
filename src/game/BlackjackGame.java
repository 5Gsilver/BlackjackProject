package game;

import java.util.ArrayList;

import model.Dealer;
import model.Deck;
import model.Player;

public class BlackjackGame {

    public enum GameState {
        WAITING,
        BETTING,
        DEALING,
        PLAYER_TURN,
        DEALER_TURN,
        ROUND_END,
        GAME_OVER
    }

    public enum PlayerState {
        PLAYING,
        STAND,
        BUST,
        BLACKJACK,
        DOUBLE_DOWN
    }

    private ArrayList<Player> players;
    private Dealer dealer;
    private Deck deck;

    private GameState gameState;
    private int currentPlayerIndex;

    private ArrayList<PlayerState> playerStates;

    public BlackjackGame(
            ArrayList<Player> players,
            Dealer dealer,
            Deck deck) {

        if (players == null || dealer == null || deck == null) {
            throw new IllegalArgumentException(
                    "게임 객체가 올바르게 전달되지 않았습니다."
            );
        }

        if (players.isEmpty() || players.size() > 7) {
            throw new IllegalArgumentException(
                    "플레이어는 1명 이상 7명 이하여야 합니다."
            );
        }

        this.players = new ArrayList<>(players);
        this.dealer = dealer;
        this.deck = deck;

        this.gameState = GameState.WAITING;
        this.currentPlayerIndex = 0;

        this.playerStates = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            playerStates.add(PlayerState.PLAYING);
        }
    }

    public void startGame() {

        if (gameState != GameState.WAITING) {
            return;
        }

        gameState = GameState.BETTING;

        startRound();
    }

    public void startRound() {

        if (gameState != GameState.BETTING
                && gameState != GameState.ROUND_END) {
            return;
        }

        currentPlayerIndex = 0;

        playerStates.clear();

        for (Player player : players) {
            playerStates.add(PlayerState.PLAYING);
        }

        gameState = GameState.DEALING;

        dealInitialCards();
    }

    public void dealInitialCards() {

        if (gameState != GameState.DEALING) {
            return;
        }

        for (int i = 0; i < players.size(); i++) {

            Player player = players.get(i);

        }

        gameState = GameState.PLAYER_TURN;

        nextTurn();
    }

    public void hit() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        if (currentPlayerIndex >= players.size()) {
            return;
        }

        PlayerState state = playerStates.get(currentPlayerIndex);

        if (state != PlayerState.PLAYING) {
            return;
        }

        Player player = players.get(currentPlayerIndex);

    }

    public void stand() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        if (currentPlayerIndex >= players.size()) {
            return;
        }

        if (playerStates.get(currentPlayerIndex)
                != PlayerState.PLAYING) {
            return;
        }

        playerStates.set(
                currentPlayerIndex,
                PlayerState.STAND
        );

        nextTurn();
    }

    public void doubleDown() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        if (currentPlayerIndex >= players.size()) {
            return;
        }

        if (playerStates.get(currentPlayerIndex)
                != PlayerState.PLAYING) {
            return;
        }

        Player player = players.get(currentPlayerIndex);

        playerStates.set(
                currentPlayerIndex,
                PlayerState.DOUBLE_DOWN
        );

        nextTurn();
    }

    public void nextTurn() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        while (currentPlayerIndex < players.size()) {

            if (playerStates.get(currentPlayerIndex)
                    == PlayerState.PLAYING) {

                return;
            }

            currentPlayerIndex++;
        }

        dealerTurn();
    }

    public void dealerTurn() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        gameState = GameState.DEALER_TURN;

        boolean hasActivePlayer = false;

        for (PlayerState state : playerStates) {

            if (state != PlayerState.BUST) {
                hasActivePlayer = true;
                break;
            }
        }

        if (hasActivePlayer) {

        }

        finishRound();
    }

    public void finishRound() {

        if (gameState != GameState.DEALER_TURN) {
            return;
        }

        gameState = GameState.ROUND_END;
    }

    public void nextRound() {

        if (gameState != GameState.ROUND_END) {
            return;
        }

        boolean hasPlayer = false;

        for (Player player : players) {

            hasPlayer = true;

        }

        if (!hasPlayer) {
            gameState = GameState.GAME_OVER;
            return;
        }

        gameState = GameState.BETTING;

        startRound();
    }

    public void endGame() {

        gameState = GameState.GAME_OVER;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getCurrentPlayer() {

        if (currentPlayerIndex >= players.size()) {
            return null;
        }

        return players.get(currentPlayerIndex);
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Dealer getDealer() {
        return dealer;
    }
}