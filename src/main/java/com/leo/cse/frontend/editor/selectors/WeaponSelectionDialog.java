package com.leo.cse.frontend.editor.selectors;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.Weapon;
import com.leo.cse.dto.factory.WeaponsFactory;
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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class WeaponSelectionDialog {
    private final static int ITEMS_COUNT = 7;

    private final Weapon initialSelection;
    private final int position;

    private final ProfileManager profileManager;
    private final WeaponsFactory factory;

    public WeaponSelectionDialog(Weapon initialSelection, int position, ProfileManager profileManager, WeaponsFactory factory) {
        this.initialSelection = new Weapon(initialSelection.id, initialSelection.title, initialSelection.image);
        this.position = position;
        this.profileManager = profileManager;
        this.factory = factory;
    }

    public void select() {
        final List<Weapon> selections = getSelections();

        if (selections.isEmpty()) {
            return;
        }

        final Weapon selection = Dialogs.showSelectionDialog(
                "Select a weapon",
                selections.toArray(new Weapon[0]),
                initialSelection,
                new ListItemRenderer()
        );

        if (selection == null || selection.equals(initialSelection)) {
            return;
        }

        profileManager.setField(ProfileFields.FIELD_WEAPON_IDS, position, selection.id);
    }

    private List<Weapon> getSelections() {
        final List<Weapon> items = factory.createAll();

        final Set<Integer> unavailable = new HashSet<>();
        for (int i = 0; i < ITEMS_COUNT; ++i) {
            final int itemId = profileManager.getIntField(ProfileFields.FIELD_WEAPON_IDS, i);
            if (itemId != 0 && itemId != initialSelection.id) {
                unavailable.add(itemId);
            }
        }

        final List<Weapon> available = new ArrayList<>();
        for (Weapon item : items) {
            if (!unavailable.contains(item.id)) {
                available.add(item);
            }
        }

        return available;
    }

    private static class ListItemRenderer extends HorizontalLayout implements ListCellRenderer<Weapon> {
        private final ImageComponent imageComponent = new ImageComponent();
        private final TextLabel textLabel = new TextLabel();
        private MouseListener mouseListener;

        private boolean isSelected = false;
        private boolean isHovered = false;
        private int hoveredIndex = -1;

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
        public Component getListCellRendererComponent(JList<? extends Weapon> list, Weapon value, int index, boolean isSelected, boolean cellHasFocus) {
            initMouseListenerIfNeeded(list);
            imageComponent.setImage(value.image);
            textLabel.setText(value.toString());
            setSelected(isSelected);
            setHovered(index == hoveredIndex);
            return this;
        }

        private void initMouseListenerIfNeeded(JList<? extends Weapon> list) {
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
        private final JList<? extends Weapon> list;
        private final ListItemRenderer cellRenderer;

        private final Point point = new Point();

        private MouseListener(JList<? extends Weapon> list, ListItemRenderer cellRenderer) {
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
