package com.leo.cse.frontend;

import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.res.GameResourcesManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class GameLauncher {
    private final GameResourcesManager resourcesManager;

    public GameLauncher(GameResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    public void launch() {
        if (resourcesManager.isCurrentModePlus()) {
            launchPlus();
        } else {
            launchDefault();
        }
    }

    private void launchDefault() {
        try {
            Runtime.getRuntime().exec("\"" + resourcesManager.getResourcesPath() + "\"");
        } catch (IOException e) {
            AppLogger.error("Launch fail: I/O error", e);
            JOptionPane.showMessageDialog(
                    null,
                    "Could not run game! The following exception occurred:\n" + e,
                    "Could not run game",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void launchPlus() {
        final String gamePath = resourcesManager.getPlusGamePath();
        final File bat = new File(gamePath + File.separator + "run.bat");
        if (bat.exists()) {
            launchRunBatPlus(bat);
        } else {
            final File steamLib = new File(gamePath + File.separator + "steam_api.dll");
            if (steamLib.exists() && Desktop.isDesktopSupported()) {
                launchSteamPlus();
            } else {
                launchExePlus(gamePath);
            }
        }
    }

    private void launchRunBatPlus(File bat) {
        final Runtime runtime = Runtime.getRuntime();

        // launch script
        AppLogger.trace("Attempting to run launch script at: " + bat.getAbsolutePath());

        try {
            runtime.exec(String.format("\"%s\"", bat.getAbsolutePath()));
        } catch (IOException e1) {
            AppLogger.error("Could not run launch script: " + bat.getAbsolutePath(), e1);
            JOptionPane.showMessageDialog(
                    null,
                    "Could not run game! The following exception occurred:\n" + e1,
                    "Could not run game",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void launchSteamPlus() {
        AppLogger.trace("Steamworks API detected, launching via Steam browser protocol");

        URI steamGameUri = null;
        try {
            steamGameUri = new URI("steam://run/200900");
        } catch (URISyntaxException e1) {
            AppLogger.error("Steam protocol fail: bad URI syntax", e1);
        }

        try {
            Desktop.getDesktop().browse(steamGameUri);
        } catch (IOException e) {
            AppLogger.trace("Steam protocol fail: I/O error");
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to launch game via Steam...",
                    "Well, this is awkward.",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void launchExePlus(String gamePath) {
        AppLogger.trace("Directly launching EXE: " + gamePath + File.separator + "CaveStory+.exe");

        final Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("\"" + gamePath + File.separator + "CaveStory+.exe\"");
        } catch (IOException e) {
            AppLogger.error("Direct launch fail: I/O error", e);
            JOptionPane.showMessageDialog(
                    null,
                    "Could not run game! The following exception occurred:\n" + e,
                    "Could not run game",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
