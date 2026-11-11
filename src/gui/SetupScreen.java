package gui;

GameSetupScreen setupScreen = new GameSetupScreen();

setupScreen.setStartGameListener(
        new GameSetupScreen.StartGameListener() {

    public void onStartGame(
            ArrayList<GameSetupScreen.PlayerSetting> settings) {

        ArrayList<Player> players = new ArrayList<Player>();

        for (int i = 0; i < settings.size(); i++) {
            GameSetupScreen.PlayerSetting setting =
                    settings.get(i);

            Player player = new Player(
                    setting.getName(),
                    setting.getInitialChips());

            players.add(player);
        }

        GameGUI gameGUI = new GameGUI(players);
        gameGUI.setVisible(true);
    }
});

setupScreen.setVisible(true);
dispose();