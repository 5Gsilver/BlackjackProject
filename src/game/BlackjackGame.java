
package game;

import java.util.ArrayList;
import model.Card;
import model.Dealer;
import model.Deck;
import model.Player;

public class BlackjackGame {

    public enum GameState {
        WAITING, BETTING, DEALING, PLAYER_TURN,
        DEALER_TURN, ROUND_END, GAME_OVER
    }

    public enum PlayerState {
        PLAYING, STAND, BUST, BLACKJACK, DOUBLE_DOWN
    }

    private final ArrayList<Player> players;
    private final Dealer dealer;
    private final Deck deck;
    private final ArrayList<PlayerState> playerStates;

    private GameState gameState = GameState.WAITING;
    private int currentPlayerIndex;
    private int round = 1;

    public BlackjackGame(
            ArrayList<Player> players,
            Dealer dealer,
            Deck deck) {

        if (players == null || players.isEmpty()
                || players.size() > 7
                || dealer == null || deck == null) {

            throw new IllegalArgumentException(
                    "플레이어(1~7명), 딜러, 덱을 확인하세요.");
        }

        this.players = new ArrayList<Player>(players);
        this.dealer = dealer;
        this.deck = deck;

        playerStates = new ArrayList<PlayerState>();

        for (Player player : players) {

            if (player == null) {
                throw new IllegalArgumentException(
                        "플레이어가 null입니다.");
            }

            playerStates.add(PlayerState.PLAYING);
        }
    }

    public boolean placeBet(int playerIndex, int amount) {

        if ((gameState != GameState.WAITING
                && gameState != GameState.BETTING)
                || playerIndex < 0
                || playerIndex >= players.size()) {
            return false;
        }

        return players.get(playerIndex).placeBet(amount);
    }

    public void startGame() {

        if (gameState != GameState.WAITING
                && gameState != GameState.BETTING) {
            return;
        }

        for (Player player : players) {

            if (player.getChips() + player.getCurrentBet() > 0
                    && player.getCurrentBet() == 0) {

                throw new IllegalStateException(
                        "모든 참가자의 베팅을 확정해야 합니다.");
            }
        }

        gameState = GameState.BETTING;
        startRound();
    }

    public void startRound() {

        if (gameState != GameState.BETTING) {
            return;
        }

        if (deck.needsShuffle()
                || deck.getRemainingCards() < 30) {
            deck.reset();
        }

        dealer.resetDealer();
        currentPlayerIndex = 0;

        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).getCurrentBet() > 0) {
                playerStates.set(i, PlayerState.PLAYING);
            } else {
                playerStates.set(i, PlayerState.STAND);
            }
        }

        gameState = GameState.DEALING;
        dealInitialCards();
    }

    public void dealInitialCards() {

        if (gameState != GameState.DEALING) {
            return;
        }

        for (int pass = 0; pass < 2; pass++) {

            for (int i = 0; i < players.size(); i++) {

                if (players.get(i).getCurrentBet() > 0) {
                    players.get(i).addCard(deck.drawCard());
                }
            }

            dealer.addCard(deck.drawCard());
        }

        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).getCurrentBet() > 0
                    && players.get(i).isBlackjack()) {

                playerStates.set(i, PlayerState.BLACKJACK);
            }
        }

        gameState = GameState.PLAYER_TURN;

        if (dealer.isBlackjack()) {
            dealer.revealHiddenCard();
            finishRound();
        } else {
            nextTurn();
        }
    }

    private boolean canPlay() {

        return gameState == GameState.PLAYER_TURN
                && currentPlayerIndex >= 0
                && currentPlayerIndex < players.size()
                && playerStates.get(currentPlayerIndex)
                        == PlayerState.PLAYING;
    }

    public void hit() {

        if (!canPlay()) {
            return;
        }

        Player player = players.get(currentPlayerIndex);

        if (!player.hit(deck.drawCard())) {
            return;
        }

        if (player.isBust()) {

            playerStates.set(
                    currentPlayerIndex,
                    PlayerState.BUST);

        } else if (player.getScore() == 21) {

            playerStates.set(
                    currentPlayerIndex,
                    PlayerState.STAND);
        }

        if (playerStates.get(currentPlayerIndex)
                != PlayerState.PLAYING) {
            nextTurn();
        }
    }

    public void stand() {

        if (!canPlay()) {
            return;
        }

        players.get(currentPlayerIndex).stand();

        playerStates.set(
                currentPlayerIndex,
                PlayerState.STAND);

        nextTurn();
    }

    public void doubleDown() {

        if (!canPlay()) {
            return;
        }

        Player player = players.get(currentPlayerIndex);

        if (!player.canDouble()) {
            return;
        }

        player.doubleDown(deck.drawCard());

        if (player.isBust()) {

            playerStates.set(
                    currentPlayerIndex,
                    PlayerState.BUST);

        } else {

            playerStates.set(
                    currentPlayerIndex,
                    PlayerState.DOUBLE_DOWN);
        }

        nextTurn();
    }

    public void nextTurn() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        while (currentPlayerIndex < players.size()
                && playerStates.get(currentPlayerIndex)
                        != PlayerState.PLAYING) {

            currentPlayerIndex++;
        }

        if (currentPlayerIndex == players.size()) {
            dealerTurn();
        }
    }

    public void dealerTurn() {

        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        gameState = GameState.DEALER_TURN;

        dealer.revealHiddenCard();

        boolean active = false;

        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).getCurrentBet() > 0
                    && !players.get(i).isBust()) {

                active = true;
            }
        }

        if (active) {
            dealer.playTurn(deck);
        }

        finishRound();
    }

    public void finishRound() {

        if (gameState != GameState.DEALER_TURN
                && gameState != GameState.PLAYER_TURN) {
            return;
        }

        dealer.revealHiddenCard();

        for (Player player : players) {

            if (player.getCurrentBet() == 0) {
                continue;
            }

            GameRules.Result result =
                    GameRules.compareHands(
                            player.getHand(),
                            dealer.getHand());

            if (result == GameRules.Result.PUSH) {

                player.pushBet();

            } else if (result == GameRules.Result.LOSE) {

                player.loseBet();

            } else if (player.isBlackjack()) {

                player.blackjackWin();

            } else {

                player.winBet();
            }
        }

        gameState = GameState.ROUND_END;
    }

    public void nextRound() {

        if (gameState != GameState.ROUND_END) {
            return;
        }

        boolean canContinue = false;

        for (Player player : players) {

            if (player.getChips() > 0) {
                canContinue = true;
            }

            player.resetRound();
        }

        dealer.resetDealer();
        round++;

        if (canContinue) {
            gameState = GameState.BETTING;
        } else {
            gameState = GameState.GAME_OVER;
        }
    }

    public void endGame() {
        gameState = GameState.GAME_OVER;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRound() {
        return round;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {

        if (currentPlayerIndex >= players.size()) {
            return null;
        }

        return players.get(currentPlayerIndex);
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }

    public Dealer getDealer() {
        return dealer;
    }

    public ArrayList<Card> getPlayerHand(int index) {
        return players.get(index).getHand();
    }

    public ArrayList<Card> getDealerHand() {
        return dealer.getHand();
    }

    public int getPlayerScore(int index) {
        return players.get(index).getScore();
    }

    public int getDealerScore() {
        return dealer.getScore();
    }

    public PlayerState getPlayerState(int index) {
        return playerStates.get(index);
    }

    public int getBet(int index) {
        return players.get(index).getCurrentBet();
    }
}
