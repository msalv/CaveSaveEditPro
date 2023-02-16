package com.leo.cse.util;

import com.leo.cse.log.AppLogger;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

import javax.swing.JOptionPane;

public class LinksHelper {
    public static void browse(String url) {
        final boolean isBrowseSupported = Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);

        if (isBrowseSupported) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                copyUrl(url);
                AppLogger.error(String.format("Unable to browse to %s", url), e);
            }
        } else {
            copyUrl(url);
        }
    }

    private static void copyUrl(String url) {
        try {
            final StringSelection selection = new StringSelection(url);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            showLinkCopiedMessage(url);
        } catch (Exception e) {
            AppLogger.error(String.format("Unable to copy url: %s", url), e);
        }
    }

    private static void showLinkCopiedMessage(String url) {
        JOptionPane.showMessageDialog(
                null,
                String.format("Unable to browse to %s. Link copied to clipboard", url),
                "Link copied to clipboard",
                JOptionPane.WARNING_MESSAGE);
    }
}
