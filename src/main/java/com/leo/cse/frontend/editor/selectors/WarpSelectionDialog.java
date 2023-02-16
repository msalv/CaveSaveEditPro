package com.leo.cse.frontend.editor.selectors;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.WarpSlot;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class WarpSelectionDialog {
    private final ProfileManager profileManager;
    private final WarpListItem initialSelection;
    private final int position;
    private final GameResourcesManager resourcesManager;

    public WarpSelectionDialog(WarpSlot initialSlot, int position, ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.initialSelection = new WarpListItem(initialSlot.id, initialSlot.name, initialSlot.image);
        this.position = position;
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public void select() {
        final List<WarpListItem> slots = getWarps();

        if (slots.isEmpty()) {
            return;
        }

        final WarpListItem selected = Dialogs.showSelectionDialog("Select a warp",
                slots.toArray(new WarpListItem[0]),
                initialSelection,
                new ListItemRenderer());

        if (selected == null || selected.id == initialSelection.id) {
            return;
        }

        profileManager.setField(ProfileFields.FIELD_WARP_IDS, position, selected.id);
    }

    private List<WarpListItem> getWarps() {
        final List<WarpListItem> result = new ArrayList<>();

        final String[] slots = profileManager.getCurrentMCI().getWarpNames();

        for (int id = 0; id < slots.length; ++id) {
            final Image image = (id != 0 && resourcesManager.hasResources())
                    ? resourcesManager.getResources().getWarpSlotImage(id)
                    : null;

            result.add(new WarpListItem(id, slots[id], image));
        }

        return result;
    }

    private static class WarpListItem {
        final int id;
        final String name;
        final Image image;

        private WarpListItem(int id, String name, Image image) {
            this.id = id;
            this.name = name;
            this.image = image;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WarpListItem that = (WarpListItem) o;

            if (id != that.id) return false;
            if (!Objects.equals(name, that.name)) return false;
            return Objects.equals(image, that.image);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (image != null ? image.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d - %s", id, name);
        }
    }

    private static class ListItemRenderer extends HorizontalLayout implements ListCellRenderer<WarpListItem> {
        private final ImageComponent imageComponent = new ImageComponent();
        private final TextLabel textLabel = new TextLabel();
        private MouseListener mouseListener;

        private boolean isSelected = false;
        private boolean isHovered = false;
        private int hoveredIndex = -1;

        ListItemRenderer() {
            imageComponent.setPlaceholderColor(ThemeData.getHoverColor());
            imageComponent.setPreferredSize(new Dimension(64, 32));
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
        public Component getListCellRendererComponent(JList<? extends WarpListItem> list, WarpListItem value, int index, boolean isSelected, boolean cellHasFocus) {
            initMouseListenerIfNeeded(list);
            imageComponent.setImage(value.image);
            textLabel.setText(value.toString());
            setSelected(isSelected);
            setHovered(index == hoveredIndex);
            return this;
        }

        private void initMouseListenerIfNeeded(JList<? extends WarpListItem> list) {
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
        private final JList<? extends WarpListItem> list;
        private final ListItemRenderer cellRenderer;

        private final Point point = new Point();

        private MouseListener(JList<? extends WarpListItem> list, ListItemRenderer cellRenderer) {
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
}
