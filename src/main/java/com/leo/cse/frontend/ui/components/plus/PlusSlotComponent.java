package com.leo.cse.frontend.ui.components.plus;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignBottom;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.dto.PlusSlot;
import com.leo.cse.dto.Weapon;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.components.visual.ImageComponent;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.EmptyBorder;

public class PlusSlotComponent extends StackLayout {
    private final ImageComponent[] weaponIcons = new ImageComponent[7];
    private final HealthBarComponent healthBar;
    private final ImageComponent characterImage = new ImageComponent();
    private final TextLabel modificationDateLabel = new TextLabel();
    private final TextLabel locationLabel = new TextLabel();

    private Runnable clickListener;
    private boolean hovered = false;

    public PlusSlotComponent(GameResourcesManager resourcesManager) {
        super();
        healthBar = new HealthBarComponent(resourcesManager);
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(10, 10, 10, 10));

        final VerticalLayout content = new VerticalLayout();

        final HorizontalLayout weaponsContainer = new HorizontalLayout();
        weaponsContainer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 28));

        for (int i = 0; i < weaponIcons.length; i++) {
            final ImageComponent weaponIcon = new ImageComponent();
            weaponIcon.setPreferredSize(new Dimension(28, 28));
            weaponIcon.setScaleType(ImageComponent.ScaleType.CENTER_CROP);
            weaponIcon.setPlaceholderColor(ThemeData.getHoverColor());
            weaponIcon.setEnabled(false);

            weaponIcons[i] = weaponIcon;
            weaponsContainer.add(weaponIcon, leftMargin(i == 0 ? 0 : 5));
        }

        healthBar.setPreferredSize(new Dimension(128, 16));

        modificationDateLabel.setMinimumSize(new Dimension(0, 14));
        modificationDateLabel.setMaximumSize(new Dimension(184, 14));
        modificationDateLabel.setSingleLine(true);
        modificationDateLabel.setClickable(false);
        modificationDateLabel.setTextColor(ThemeData.getForegroundColor());
        modificationDateLabel.setFont(Resources.getFont());

        locationLabel.setMinimumSize(new Dimension(0, 14));
        locationLabel.setMaximumSize(new Dimension(184, 14));
        locationLabel.setSingleLine(true);
        locationLabel.setClickable(false);
        locationLabel.setTextColor(ThemeData.getForegroundColor());
        locationLabel.setFont(Resources.getFont());

        characterImage.setPreferredSize(new Dimension(32, 32));
        characterImage.setScaleType(ImageComponent.ScaleType.CENTER_CROP);
        characterImage.setPlaceholderColor(ThemeData.getHoverColor());
        characterImage.setEnabled(false);

        content.add(weaponsContainer);
        content.add(healthBar, topMargin(6));
        content.add(modificationDateLabel, topMargin(10));
        content.add(locationLabel, topMargin(2));

        add(content);
        add(characterImage, alignBottom(alignRight()));

        addMouseListener(new MouseEventsListener());
    }

    public void setOnClickListener(Runnable action) {
        clickListener = action;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (hovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void bind(PlusSlot plusSlot) {
        for (int i = 0; i < weaponIcons.length; i++) {
            final ImageComponent icon = weaponIcons[i];
            if (i < plusSlot.weapons.size()) {
                final Weapon weapon = plusSlot.weapons.get(i);
                icon.setImage(weapon.image);
                icon.setVisible(weapon.id != 0);
            } else {
                icon.setVisible(false);
            }
        }

        healthBar.setValues(plusSlot.hp, plusSlot.maxHp);

        modificationDateLabel.setText(plusSlot.modificationDate);
        locationLabel.setText(plusSlot.location);
        characterImage.setImage(plusSlot.character);
        characterImage.setVisible(true);
    }

    public void unbind(int position) {
        for (ImageComponent weaponIcon : weaponIcons) {
            weaponIcon.setVisible(false);
        }
        modificationDateLabel.setText(null);
        healthBar.setValues((short)0, (short)0);
        locationLabel.setText(String.format("Empty slot #%d", position + 1));
        characterImage.setVisible(false);
    }

    private class MouseEventsListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                clickListener.run();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = true;
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = false;
                repaint();
            }
        }
    }
}
