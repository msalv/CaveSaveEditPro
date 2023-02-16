package com.leo.cse.frontend.dialogs.settings;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.util.async.IndeterminateTask;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.backend.mci.MCIFactory;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.Dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AdvancedSettingsPage extends VerticalLayout {
    private final static String RESOURCES_SCHEME = "resources://";

    private final TextLabel nameLabel = new TextLabel();
    private final TextLabel authorLabel = new TextLabel();
    private final TextLabel specialsLabel = new TextLabel();
    private final TextLabel infoLabel = new TextLabel();
    private final TextButton exportButton = new TextButton();
    private final TextButton loadButton = new TextButton();

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public AdvancedSettingsPage(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        init();
        bind();
    }

    private void init() {
        setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        nameLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 16));
        nameLabel.setGravity(Gravity.CENTER_VERTICAL);
        nameLabel.setFont(Resources.getFont());
        nameLabel.setTextColor(ThemeData.getForegroundColor());
        nameLabel.setSingleLine(true);

        authorLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 16));
        authorLabel.setGravity(Gravity.CENTER_VERTICAL);
        authorLabel.setFont(Resources.getFont());
        authorLabel.setTextColor(ThemeData.getForegroundColor());

        specialsLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 16));
        specialsLabel.setGravity(Gravity.CENTER_VERTICAL);
        specialsLabel.setFont(Resources.getFont());
        specialsLabel.setTextColor(ThemeData.getForegroundColor());
        specialsLabel.setSingleLine(true);

        infoLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 16));
        infoLabel.setGravity(Gravity.CENTER_VERTICAL);
        infoLabel.setFont(Resources.getFont());
        infoLabel.setTextColor(ThemeData.getForegroundColor());

        infoLabel.setText("\u00B0MCI (Mod Compatibility Information) system defines " +
                "game related values that may vary from one edition to another " +
                "(i.e. map names in CS and CS+)");

        add(nameLabel);
        add(authorLabel, topMargin(2));
        add(specialsLabel, topMargin(2));
        add(infoLabel, topMargin(7));
        add(initFooter(), topMargin(7));
    }

    private Component initFooter() {
        exportButton.setText("Export Current MCI");
        exportButton.setPadding(6, 5, 6, 5);
        exportButton.setMinimumSize(new Dimension(116, 19));
        exportButton.setOnClickListener(this::onExportButtonClicked);

        loadButton.setText("Load Custom MCI file");
        loadButton.setPadding(6, 5, 6, 5);
        loadButton.setMinimumSize(new Dimension(116, 19));
        loadButton.setOnClickListener(this::onLoadButtonClicked);

        final HorizontalLayout footer = new HorizontalLayout();
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        footer.add(loadButton, alignRight());
        footer.add(exportButton, alignRight(rightMargin(6)));

        return footer;
    }

    private void onExportButtonClicked() {
        final MCI mci = profileManager.getCurrentMCI();
        final String filePath = mci.getFilePath();

        final File file = Dialogs.openFileChooser(this,
                "Export MCI file",
                new FileNameExtensionFilter("MCI system JSON files", "json"),
                new File(Config.get(Config.KEY_LAST_PROFILE, System.getProperty("user.dir"))),
                false,
                true);

        if (file == null) {
            return;
        }

        if (file.exists()) {
            final int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to overwrite this file?",
                    "Overwrite confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        new ExportTask(filePath, file).execute();
    }

    private void onLoadButtonClicked() {
        final File file = Dialogs.openFileChooser(
                null,
                "Open MCI file",
                new FileNameExtensionFilter("MCI Files", "json"),
                new File(Config.get(Config.KEY_LAST_MCI_FILE, System.getProperty("user.dir"))),
                false,
                false);

        if (file == null || !file.exists()) {
            return;
        }

        new LoadTask(file).execute();
    }

    private void bind() {
        final MCI mci = profileManager.getCurrentMCI();

        nameLabel.setText(String.format("Current MCI\u00B0 for %s", mci.getName()));
        authorLabel.setText(String.format("By %s", mci.getAuthor()));
        specialsLabel.setText(String.format("Special Support: %s", mci.getSpecials()));
    }

    private class ExportTask extends IndeterminateTask<Boolean> {
        private final String filePath;
        private final File file;

        public ExportTask(String filePath, File file) {
            this.filePath = filePath;
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            exportButton.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            exportButton.setEnabled(true);

            if (!success) {
                JOptionPane.showMessageDialog(
                        AdvancedSettingsPage.this,
                        "An error occurred while exporting the MCI file",
                        "Could not export MCI file",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            final InputStream is;

            if (filePath.startsWith(RESOURCES_SCHEME)) {
                final String fileName = filePath.substring(RESOURCES_SCHEME.length());
                is = MCI.class.getResourceAsStream("/" + fileName);
            } else {
                is = new FileInputStream(filePath);
            }

            if (is == null) {
                return false;
            }

            try {
                exportMCIFile(is, file);
            } catch (IOException e) {
                AppLogger.error("Unable to export file", e);
                return false;
            } finally {
                is.close();
            }

            AppLogger.info("File exported: " + filePath);

            return true;
        }

        private void exportMCIFile(InputStream is, File dest) throws IOException {
            try (final OutputStream out = new FileOutputStream(dest)) {
                final byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        }
    }

    private class LoadTask extends IndeterminateTask<Boolean> {
        private final File file;

        public LoadTask(File file) {
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            loadButton.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground() {
            try {
                profileManager.setCurrentMCI(MCIFactory.fromFile(file));
            } catch (Exception e) {
                AppLogger.error("Unable to load file", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            loadButton.setEnabled(true);

            if (success) {
                profileManager.notifyMCIChanged();
                resourcesManager.reload();
                Config.set(Config.KEY_LAST_MCI_FILE, file.getAbsolutePath());
                bind();
            } else {
                JOptionPane.showMessageDialog(
                        AdvancedSettingsPage.this,
                        "An error occurred while loading the MCI file",
                        "Could not load MCI file",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
