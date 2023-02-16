package com.leo.cse.frontend.dialogs.niku;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.RootDialog;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.StringUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;

import javax.swing.JList;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NikuInfoDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(480, 500);
    public static final int TABLE_ROW_HEIGHT = 24;

    private final static int SACRED_GROUNDS_ID = 82;
    private final static int NIKU_COUNTER_ID = 22;

    public static final String SACRED_GROUNDS_FALLBACK = "Sacred Grounds";

    private final static String LEAD = "After the completion of the %s using the %s, " +
            "the time it took to complete the stage will be displayed on the title screen " +
            "(or on the save slot selection screen in Cave Story+).";

    private final static String[] DESCRIPTION = {
            "\nThe title screen changes based on the player's fastest completion of the %s.",
            "If beaten within a certain range, the character selector and music will change accordingly."
    };

    private final static String[] USAGE = {
            "\nCave Story",
            "You can place the file named '290.rec' in the game folder and that will make changes to the title screen.",
            "\nCave Story+",
            "Best time is stored in the save file itself. Check out Cave Story+ tab if you want to modify it."
    };

    private final static String[] JUKEBOX = {
            "Also there is a Jukebox Mode available in Cave Story+ if a player beats the Blood Stained Sanctuary in less than three minutes.",
            "'Jukebox' is a main menu mode in Cave Story+ that allows players to listen to all the tracks of Cave Story right on title screen."
    };

    private final static String[] TABLE_HEADER = { "Time Range",    "Character",    "Song" };
    private final static String[] TABLE_CURLY  = { "05:00 - 05:59", "Curly Brace",  "Running Hell" };
    private final static String[] TABLE_TOROKO = { "04:00 - 04:59", "Toroko",       "Toroko's Theme" };
    private final static String[] TABLE_KING   = { "03:00 - 03:59", "King",         "White Stone Wall" };
    private final static String[] TABLE_SUE    = { "00:01 - 02:59", "Sue Sakamoto", "Safety" };

    private final static String[][] TABLE = new String[][] {
            TABLE_HEADER, TABLE_CURLY, TABLE_TOROKO, TABLE_KING, TABLE_SUE
    };

    public NikuInfoDialog(
            Frame parentFrame,
            Component parentComponent,
            ProfileManager profileManager,
            GameResourcesManager resourcesManager) {
        super(parentFrame, String.format("About %s", profileManager.getCurrentMCI().getItemName(NIKU_COUNTER_ID)), true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(new NikuInfoComponent(this, profileManager, resourcesManager));

        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static class NikuInfoComponent extends VerticalLayout {
        private final NikuInfoDialog dialog;
        private final ProfileManager profileManager;
        private final GameResourcesManager resourcesManager;

        NikuInfoComponent(NikuInfoDialog dialog, ProfileManager profileManager, GameResourcesManager resourcesManager) {
            super();
            this.dialog = dialog;
            this.profileManager = profileManager;
            this.resourcesManager = resourcesManager;
            init();
        }

        private void init() {
            setBorder(new CompoundBorder(
                    new LineBorder(ThemeData.getForegroundColor()),
                    new EmptyBorder(16, 16, 16, 16)
            ));

            final MCI mci = profileManager.getCurrentMCI();
            final MapInfo mi = resourcesManager.hasResources()
                    ? resourcesManager.getResources().getMapInfo(SACRED_GROUNDS_ID)
                    : null;

            final String mapName = (mi == null || StringUtils.isNullOrEmpty(mi.getMapName()))
                    ? mci.getMapName(SACRED_GROUNDS_ID)
                    : mi.getMapName();

            final String trimmedMapName = (!StringUtils.isNullOrEmpty(mapName))
                    ? mapName.replaceAll("([\\s+-]+)?[A-Za-z]\\d+$", "")
                    : SACRED_GROUNDS_FALLBACK;

            final String lead = String.format(LEAD, trimmedMapName, mci.getItemName(NIKU_COUNTER_ID));
            addTextLabel(this, lead, ThemeData.getForegroundColor());

            final String description = String.format(DESCRIPTION[0], trimmedMapName);
            addTextLabel(this, description, ThemeData.getForegroundColor());
            addTextLabel(this, DESCRIPTION[1], ThemeData.getForegroundColor());

            add(initTable(), topMargin(12));

            for (int i = 0; i < USAGE.length; i++) {
                final Color textColor = (i % 2 == 0)
                        ? ThemeData.getTextColor()
                        : ThemeData.getForegroundColor();
                addTextLabel(this, USAGE[i], textColor);
            }

            add(initJukeboxNote(), topMargin(8));

            add(initFooter(), topMargin(12));
        }

        private Component initTable() {
            final JList<String[]> table = new JList<>();
            final LineBorder border = new LineBorder(ThemeData.getForegroundColor());
            table.setBorder(border);
            table.setCellRenderer(new BestTimeRow.CellRenderer());
            table.setFixedCellHeight(TABLE_ROW_HEIGHT);
            table.setMinimumSize(new Dimension(0, TABLE_ROW_HEIGHT * TABLE.length + border.getThickness() * 2));
            table.setBackground(ThemeData.getBackgroundColor());

            table.setListData(TABLE);

            return table;
        }

        private Component initJukeboxNote() {
            final VerticalLayout jukebox = new VerticalLayout();
            jukebox.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
            jukebox.setBorder(new EmptyBorder(8, 12, 12, 12));
            jukebox.setBackground(ThemeData.getHoverColor());

            addMultiLineTextLabels(jukebox, JUKEBOX, ThemeData.getForegroundColor());

            return jukebox;
        }

        private Component initFooter() {
            final HorizontalLayout footer = new HorizontalLayout();
            footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

            final TextButton okayButton = new TextButton();

            okayButton.setText("OK");
            okayButton.setPadding(6, 5, 6, 5);
            okayButton.setMinimumSize(new Dimension(52, 24));
            okayButton.setOnClickListener(dialog::dispatchClose);

            footer.add(okayButton, alignRight());

            return footer;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(ThemeData.getForegroundColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            super.paintComponent(g);
        }

        private static void addMultiLineTextLabels(Container container, String[] text, Color textColor) {
            for (String paragraph : text) {
                addTextLabel(container, paragraph, textColor);
            }
        }

        private static void addTextLabel(Container container, String paragraph, Color textColor) {
            final String trimmed = paragraph.trim();
            final TextLabel label = new TextLabel();
            label.setFont(Resources.getFont());
            label.setForeground(textColor);
            label.setText(trimmed);
            container.add(label, paragraph.startsWith("\n") ? topMargin(12) : topMargin(2));
        }
    }
}
