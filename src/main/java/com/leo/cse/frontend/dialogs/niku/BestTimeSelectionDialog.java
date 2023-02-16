package com.leo.cse.frontend.dialogs.niku;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.format.DurationFormatter;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.components.visual.ImageComponent;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.util.Dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class BestTimeSelectionDialog {
    private final static long[] TIMES = {
                 0, // Quote  (0:00)
            10_740, // Sue    (2:59)
            14_340, // King   (3:59)
            17_940, // Toroko (4:59)
            21_540  // Curly  (5:59)
    };

    private final static int[] SONG_IDS = {
            0x18, // Cave Story
            0x02, // Safety
            0x29, // King's Theme
            0x28, // Toroko's Theme
            0x24  // Running Hell
    };

    private final static Point[] CHARACTERS = {
            new Point(0, 32),
            new Point(0, 32),
            new Point(64 * 7, 64 + 32),
            new Point(64 * 2, 64 * 2 + 32),
            new Point(0, 64 * 3 + 32)
    };

    private final long initialSelection;

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public BestTimeSelectionDialog(long initialSelection, ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.initialSelection = initialSelection;
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public long select() {
        final List<BestTime> selections = new ArrayList<>();
        final BestTime initial = getSelections(selections);

        if (selections.isEmpty()) {
            return initialSelection;
        }

        final BestTime selection = Dialogs.showSelectionDialog(
                "Select a title screen",
                selections.toArray(new BestTime[0]),
                initial,
                new ListItemRenderer()
        );

        if (selection == null || selection.time == initialSelection) {
            return initialSelection;
        }

        return selection.time;
    }

    private BestTime getSelections(List<BestTime> items) {
        BestTime initial = null;

        for (int i = 0; i < TIMES.length; i++) {
            final String song = profileManager.getCurrentMCI().getSongName(SONG_IDS[i]);
            final Image image = getCharacterImage(i);
            final long time = (initial == null && initialSelection > 0 && initialSelection <= TIMES[i])
                    ? initialSelection
                    : TIMES[i];

            final BestTime item = new BestTime(time, song, image);
            items.add(item);

            if (time > 0 && time == initialSelection) {
                initial = item;
            }
        }

        return initial;
    }

    private Image getCharacterImage(int position) {
        if (!resourcesManager.hasResources()) {
            return null;
        }

        final Point point = CHARACTERS[position];
        final Rectangle bounds = new Rectangle(point.x, point.y, 32, 32);

        if (position == 0) {
            return resourcesManager.getResources().getCharacterImage(bounds);
        }

        return resourcesManager.getResources().getNpcReguImage(bounds);
    }

    private static class ListItemRenderer extends HorizontalLayout implements ListCellRenderer<BestTime> {
        private final ImageComponent imageComponent = new ImageComponent();
        private final TextLabel textLabel = new TextLabel();
        private MouseListener mouseListener;

        private boolean isSelected = false;
        private boolean isHovered = false;
        private int hoveredIndex = -1;

        private final DurationFormatter formatter = new DurationFormatter();
        private final StringBuilder stringBuilder = new StringBuilder();

        ListItemRenderer() {
            imageComponent.setPlaceholderColor(ThemeData.getHoverColor());
            imageComponent.setPreferredSize(new Dimension(32, 32));
            add(imageComponent);

            textLabel.setFont(Resources.getFont());
            textLabel.setSingleLine(true);
            textLabel.setGravity(Gravity.CENTER_VERTICAL);
            textLabel.setMinimumSize(new Dimension(0, 32));
            add(textLabel, leftMargin(16));

            setBorder(new EmptyBorder(8, 16, 8, 16));
            setPreferredSize(new Dimension(256, 48));

            setOpaque(true);
        }

        void setSelected(boolean isSelected) {
            if (this.isSelected != isSelected) {
                this.isSelected = isSelected;
                repaint();
            }
        }

        void setHovered(boolean isHovered) {
            if (this.isHovered != isHovered) {
                this.isHovered = isHovered;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (isHovered) {
                g.setColor(ThemeData.getHoverColor());
                g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }

            if (isSelected) {
                g.setColor(ThemeData.getHoverColor());
                g.fillRect(1, 1, getWidth() - 3, getHeight() - 3);

                g.setColor(ThemeData.getForegroundColor());
                g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            }
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends BestTime> list, BestTime value, int index, boolean isSelected, boolean cellHasFocus) {
            initMouseListenerIfNeeded(list);
            imageComponent.setImage(value.characterImage);

            stringBuilder.setLength(0);
            stringBuilder.append(formatter.format(value.time))
                    .append(" - ")
                    .append(value.song);

            textLabel.setText(stringBuilder.toString());

            setSelected(isSelected);
            setHovered(index == hoveredIndex);
            return this;
        }

        private void initMouseListenerIfNeeded(JList<? extends BestTime> list) {
            if (this.mouseListener == null) {
                final MouseListener mouseListener = new MouseListener(list, this);
                list.addMouseListener(mouseListener);
                list.addMouseMotionListener(mouseListener);
                this.mouseListener = mouseListener;
            }
        }

        public boolean setHoveredIndex(int index) {
            if (hoveredIndex != index) {
                hoveredIndex = index;
                return true;
            }
            return false;
        }

        public boolean clearHover() {
            return setHoveredIndex(-1);
        }
    }

    private static class MouseListener extends MouseAdapter {
        private final JList<? extends BestTime> list;
        private final ListItemRenderer cellRenderer;

        private final Point point = new Point();

        private MouseListener(JList<? extends BestTime> list, ListItemRenderer cellRenderer) {
            this.list = list;
            this.cellRenderer = cellRenderer;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            point.setLocation(e.getX(), e.getY());

            final int index = list.locationToIndex(point);

            if (cellRenderer.setHoveredIndex(index)) {
                list.repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (cellRenderer.clearHover()) {
                list.repaint();
            }
        }
    }

    private static class BestTime {
        public final long time;
        public final String song;
        public final Image characterImage;

        private BestTime(long time, String song, Image characterImage) {
            this.time = time;
            this.song = song;
            this.characterImage = characterImage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BestTime bestTime = (BestTime) o;

            if (time != bestTime.time) return false;
            if (!Objects.equals(song, bestTime.song)) return false;
            return Objects.equals(characterImage, bestTime.characterImage);
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(time);
            result = 31 * result + (song != null ? song.hashCode() : 0);
            result = 31 * result + (characterImage != null ? characterImage.hashCode() : 0);
            return result;
        }
    }
}
