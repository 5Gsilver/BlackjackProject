package main;

import javax.swing.SwingUtilities;
import gui.WarningScreen;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                WarningScreen warningScreen = new WarningScreen();

                warningScreen.setVisible(true);

            }
        });
    }
}