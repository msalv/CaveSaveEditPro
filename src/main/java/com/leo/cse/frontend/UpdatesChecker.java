package com.leo.cse.frontend;

import static com.leo.cse.frontend.App.VERSION;

import com.leo.cse.util.async.IndeterminateAsyncTaskCallback;
import com.leo.cse.util.async.IndeterminateTask;
import com.leo.cse.log.AppLogger;

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UpdatesChecker {
    public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/msalv/CaveSaveEditPro/master/.version";
    public static final String DOWNLOAD_SITE = "https://github.com/msalv/CaveSaveEditPro/releases/";

    public static boolean shouldSkipCheck() {
        return Config.getBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
    }

    public static IndeterminateTask<?> check(boolean showUpToDate, IndeterminateAsyncTaskCallback<Version> callback) {
        final Task task = new Task(showUpToDate, callback);
        task.execute();
        return task;
    }

    private static class Result {
        private final Version version;
        private final String changelog;

        private Result(Version version, String changelog) {
            this.version = version;
            this.changelog = changelog;
        }
    }

    private static class Task extends IndeterminateTask<Result> {
        private final IndeterminateAsyncTaskCallback<Version> callback;
        private final boolean showUpToDate;

        public Task(boolean showUpToDate, IndeterminateAsyncTaskCallback<Version> callback) {
            this.callback = callback;
            this.showUpToDate = showUpToDate;
        }

        @Override
        protected void onPreExecute() {
            callback.onPreExecute();
        }

        @Override
        protected Result doInBackground() throws Exception {
            final URL url = new URL(UPDATE_CHECK_SITE);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                final Version version = new Version(reader.readLine());
                if (VERSION.compareTo(version) < 0) {
                    final StringBuilder sb = new StringBuilder();
                    while (reader.ready()) {
                        if (sb.length() != 0) {
                            sb.append('\n');
                        }
                        sb.append(reader.readLine());
                    }
                    return new Result(version, sb.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            final Version version = (result != null) ? result.version : VERSION;
            callback.onPostExecute(version);

            if (result == null || result.changelog.isEmpty()) {
                AppLogger.info("Update check successful: up to date");
                if (showUpToDate) {
                    JOptionPane.showMessageDialog(
                            null,
                            "You are using the most up to date version of CaveSaveEdit! Have fun!",
                            "Up to date!",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                try {
                    showChangeLog(result.version, result.changelog);
                } catch (URISyntaxException e) {
                    AppLogger.error("Update check failed: bad URI syntax", e);
                }
            }
        }

        @Override
        protected void onCancelled() {
            callback.onPostExecute(null);
        }

        private void showChangeLog(Version version, String changelog) throws URISyntaxException {
            final JPanel rootPanel = new JPanel();
            rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

            rootPanel.add(new JLabel(String.format("A new update is available: %s", version)));
            rootPanel.add(new JLabel("Changelog:"));

            // changelog start
            final JTextArea textArea = new JTextArea(changelog);
            textArea.setEditable(false);
            textArea.setFont(Resources.getFont());

            final JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            // changelog end

            rootPanel.add(scrollPane);
            rootPanel.add(new JLabel("Click \"Yes\" to go to the download site, click \"No\" to continue to the save editor."));

            final int result = JOptionPane.showConfirmDialog(
                    null,
                    rootPanel,
                    "New update!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                final URI siteUri = new URI(DOWNLOAD_SITE);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(siteUri);
                    } catch (IOException e) {
                        AppLogger.error("Browse to download site failed: I/O error", e);
                        JOptionPane.showMessageDialog(
                                null,
                                "Failed to browse to the download site...",
                                "Well, this is awkward.",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            String.format("Sadly, we can't browse to the download site for you on this platform. :(\nHead to\n%s\nto get the newest update!", siteUri),
                            "Operation not supported...",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
