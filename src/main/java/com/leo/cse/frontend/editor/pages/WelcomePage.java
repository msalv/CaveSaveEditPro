package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.center;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topLeftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topRightMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.editor.SaveEditorController;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.RecentFileComponent;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.log.AppLogger;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

public class WelcomePage extends StackLayout implements OnGameResourcesLoadingStateChangeListener {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private final SaveEditorController controller;
    private final Callback callback;

    private final TextLabel backButton = new TextLabel();
    private final RecentFileComponent recentProfileBlock = new RecentFileComponent();
    private final RecentFileComponent recentResourcesBlock = new RecentFileComponent();

    public WelcomePage(ProfileManager profileManager,
                       GameResourcesManager resourcesManager,
                       SaveEditorController controller,
                       Callback callback) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.controller = controller;
        this.callback = callback;
        initPage();
        bind();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        resourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        resourcesManager.removeListener(this);
        super.removeNotify();
    }

    private void initPage() {
        add(initBackButton(), alignRight(topRightMargin(18, 58)));
        add(initContent(), center());
    }

    private Component initBackButton() {
        backButton.setFont(Resources.getFont());
        backButton.setTextColor(ThemeData.getTextColor());
        backButton.setText("\u2B05 Return to Editor");
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setOnClickListener(callback::onBackButtonClicked);

        return backButton;
    }

    private Component initContent() {
        final VerticalLayout layout = new VerticalLayout();

        final TextLabel lead = new TextLabel();
        lead.setFont(Resources.getFontPixelLarge().deriveFont(Font.PLAIN, 40.0f));
        lead.setTextColor(ThemeData.getTextColor());
        lead.setGravity(Gravity.CENTER_HORIZONTAL);
        lead.setText("Welcome to CaveSaveEdit Pro");

        final TextLabel subtitle = new TextLabel();
        subtitle.setMaximumSize(new Dimension(344, Integer.MAX_VALUE));
        subtitle.setFont(Resources.getFont().deriveFont(Font.PLAIN, 32.0f));
        subtitle.setTextColor(ThemeData.getTextColor());
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitle.setText("Save files editor for Cave Story and Cave Story+");

        layout.add(lead);
        layout.add(subtitle, centerHorizontal(topMargin(8)));

        layout.add(initColumns(), centerHorizontal(topMargin(34)));

        return layout;
    }

    private Component initColumns() {
        final HorizontalLayout columns = new HorizontalLayout();

        columns.add(initGettingStartedColumn());
        columns.add(initRecentFilesColumn(), leftMargin(40));

        return columns;
    }

    private Component initGettingStartedColumn() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));

        final TextLabel title = new TextLabel();
        title.setFont(Resources.getFontPixelLarge().deriveFont(Font.PLAIN, 20.0f));
        title.setTextColor(ThemeData.getTextColor());
        title.setText("Getting Started");

        layout.add(title);

        initProfileNotes(layout);
        initResourcesNotes(layout);

        return layout;
    }

    private static void initProfileNotes(VerticalLayout layout) {
        final TextLabel profileTitle = new TextLabel();
        profileTitle.setFont(Resources.getFont());
        profileTitle.setTextColor(ThemeData.getTextColor());
        profileTitle.setText("1. Open Profile.dat");

        layout.add(profileTitle, topMargin(16));

        final TextLabel profileParagraph0 = new TextLabel();
        profileParagraph0.setFont(Resources.getFont());
        profileParagraph0.setTextColor(ThemeData.getForegroundColor());
        profileParagraph0.setText("Save game file is usually located in the same directory as your Cave Story executable file. It is called \u2018Profile.dat\u2019");

        layout.add(profileParagraph0, topMargin(8));

        final TextLabel profileParagraph1 = new TextLabel();
        profileParagraph1.setFont(Resources.getFont());
        profileParagraph1.setTextColor(ThemeData.getForegroundColor());
        profileParagraph1.setText("If you have a Steam version of Cave Story+ installed " +
                "then open Steam and go to your Library. Right-click the game and select " +
                "\u2018Properties\u2019 from the context menu. In the \u2018Properties\u2019 window, go to the " +
                "\u2018Local Files\u2019 tab and click \u2018Browse Local Files\u2019.");

        layout.add(profileParagraph1, topMargin(12));
    }

    private static void initResourcesNotes(VerticalLayout layout) {
        final TextLabel profileTitle = new TextLabel();
        profileTitle.setFont(Resources.getFont());
        profileTitle.setTextColor(ThemeData.getTextColor());
        profileTitle.setText("2. Load Game Resources (optional)");

        layout.add(profileTitle, topMargin(32));

        final TextLabel resParagraph0 = new TextLabel();
        resParagraph0.setFont(Resources.getFont());
        resParagraph0.setTextColor(ThemeData.getForegroundColor());
        resParagraph0.setText("Also you can load resources in order to see game sprites and maps.");

        layout.add(resParagraph0, topMargin(8));

        final TextLabel resParagraph1 = new TextLabel();
        resParagraph1.setFont(Resources.getFont());
        resParagraph1.setTextColor(ThemeData.getForegroundColor());
        resParagraph1.setText("Select one of these files according to your version of the game:");

        layout.add(resParagraph1, topMargin(12));

        final String[] files = {
                "\u2022 Cave Story (freeware) - Doukutsu.exe",
                "\u2022 Cave Story+ - stage.tbl",
                "\u2022 NXEngine/NXEngine-evo - stage.dat",
                "\u2022 Doukutsu-rs/CSE2E - mrmap.bin"
        };

        for (String file : files) {
            final TextLabel resParagraph = new TextLabel();
            resParagraph.setFont(Resources.getFont());
            resParagraph.setTextColor(ThemeData.getForegroundColor());
            resParagraph.setText(file);
            layout.add(resParagraph, topLeftMargin(2, 6));
        }

        final TextLabel resParagraph2 = new TextLabel();
        resParagraph2.setFont(Resources.getFont());
        resParagraph2.setTextColor(ThemeData.getForegroundColor());
        resParagraph2.setText("One of these files should be located in the game folder or in the \u2018data\u2019 subfolder.");

        layout.add(resParagraph2, topMargin(12));
    }

    private Component initRecentFilesColumn() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMinimumSize(new Dimension(370, 0));

        layout.add(initRecentProfileBlock(), topMargin(50));
        layout.add(initRecentResourcesBlock(), topMargin(40));

        return layout;
    }

    private Component initRecentProfileBlock() {
        recentProfileBlock.setBorder(new EmptyBorder(12, 12, 12, 12));
        recentProfileBlock.setPreferredSize(new Dimension(370, 91));

        recentProfileBlock.setButtonText("Open...");
        recentProfileBlock.setButtonClickListener(() -> {
            controller.openFile(this);
        });

        recentProfileBlock.setPathClickListener(() -> {
            final String path = Config.get(Config.KEY_LAST_PROFILE, "");
            final File file = new File(path);
            if (file.exists()) {
                try {
                    profileManager.loadProfile(file, null);
                } catch (Exception e) {
                    AppLogger.error("Unable to load recent profile: " + file, e);
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to load recent profile:\n" + file,
                        "File not found",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return recentProfileBlock;
    }

    private Component initRecentResourcesBlock() {
        recentResourcesBlock.setBorder(new EmptyBorder(12, 12, 12, 12));
        recentResourcesBlock.setPreferredSize(new Dimension(370, 91));

        recentResourcesBlock.setButtonText("Load...");
        recentResourcesBlock.setButtonClickListener(() -> {
            controller.loadResources(this);
        });

        recentResourcesBlock.setPathClickListener(() -> {
            final String path = Config.get(Config.KEY_LAST_MOD, "");
            final File file = new File(path);
            if (file.exists()) {
                try {
                    resourcesManager.load(file, null);
                } catch (Exception e) {
                    AppLogger.error("Unable to load recent resources: " + file, e);
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to load recent resources:\n" + file,
                        "File not found",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return recentResourcesBlock;
    }

    private void bind() {
        backButton.setVisible(profileManager.hasProfile());

        final String recentProfilePath = Config.get(Config.KEY_LAST_PROFILE, "");
        recentProfileBlock.setRecentFilePath(recentProfilePath);

        if (!profileManager.hasProfile()) {
            recentProfileBlock.setStateText("There is no Profile.dat opened yet");
            recentProfileBlock.setTitle("Recent Profile:");
            recentProfileBlock.setStateColor(ThemeData.getForegroundColor());
        } else {
            recentProfileBlock.setStateText("Profile.dat has been opened");
            recentProfileBlock.setTitle("Current:");
            recentProfileBlock.setStateColor(ThemeData.getAccentColor());
        }

        if (!resourcesManager.hasResources()) {
            recentResourcesBlock.setStateText("There are no game resources loaded yet");
            recentResourcesBlock.setTitle("Recent:");
            recentResourcesBlock.setStateColor(ThemeData.getForegroundColor());
        } else {
            recentResourcesBlock.setStateText("Resources has been loaded");
            recentResourcesBlock.setTitle("Current:");
            recentResourcesBlock.setStateColor(ThemeData.getAccentColor());
        }

        final String recentResourcesPath = Config.get(Config.KEY_LAST_MOD, "");
        recentResourcesBlock.setRecentFilePath(recentResourcesPath);
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        super.onMeasure(container, maxWidth, maxHeight);

        final Insets insets = container.getInsets();
        final int availWidth = maxWidth - (insets.right + insets.left);
        final int availHeight = maxHeight - (insets.bottom + insets.top);
        setMeasuredDimensions(availWidth, availHeight);
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.DONE || state == GameResourcesLoadingState.NONE) {
            bind();
        }
    }

    public interface Callback {
        void onBackButtonClicked();
    }
}
