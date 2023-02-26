package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.dto.Weapon;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.compound.CompoundButton;
import com.leo.cse.frontend.ui.components.compound.OnCheckedStateChangedListener;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.components.visual.ImageComponent;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.HorizontalSpreadLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class WeaponComponent extends VerticalLayout implements CompoundButton {
    private TextLabel titleLabel;
    private ImageComponent image;
    private TextButton levelButton;
    private TextButton expButton;
    private TextButton ammoButton;
    private TextButton maxAmmoButton;

    private boolean isChecked = false;
    private final Border checkedImageBorder = BorderFactory.createLineBorder(ThemeData.getAccentColor(), 2);
    private OnCheckedStateChangedListener checkChangedListener;
    private ComponentController controller;

    private final DecimalFormat numberFormatter = new DecimalFormat();
    private Weapon weapon;

    public WeaponComponent() {
        super();
        setBorder(new EmptyBorder(0, 9, 8, 9));
        initComponent();
    }

    private void initComponent() {
        titleLabel = new TextLabel();

        titleLabel.setPadding(6, 2, 6, 3);
        titleLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        titleLabel.setFont(Resources.getFont());
        titleLabel.setForeground(ThemeData.getForegroundColor());
        titleLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        titleLabel.setSingleLine(true);

        add(titleLabel);

        final HorizontalLayout horizontalLayout = new HorizontalLayout();

        image = new ImageComponent();
        image.setScaleType(ImageComponent.ScaleType.CENTER_CROP);
        image.setPlaceholderColor(ThemeData.getHoverColor());
        image.setPreferredSize(new Dimension(28, 28));
        image.setOnClickListener(() -> {
            setChecked(!isChecked, true);
        });
        horizontalLayout.add(image);

        final TextButton setButton = new TextButton();
        setButton.setText("Set");
        setButton.setPadding(0, 0, 0, 0);
        setButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 28));
        setButton.setOnClickListener(() -> {
            if (controller != null) {
                controller.onSetButtonClick(weapon);
            }
        });
        horizontalLayout.add(setButton, leftMargin(9));

        add(horizontalLayout, topMargin(7));

        final TextLabel levelLabel = new TextLabel();
        levelLabel.setFont(Resources.getFont());
        levelLabel.setForeground(ThemeData.getForegroundColor());
        levelLabel.setSingleLine(true);
        levelLabel.setText("Level:");

        levelButton = new TextButton();
        levelButton.setGravity(Gravity.RIGHT);
        levelButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        levelButton.setOnClickListener(() -> {
            if (controller != null) {
                controller.onLevelButtonClick(weapon);
            }
        });

        add(levelLabel, topMargin(6));
        add(levelButton, topMargin(2));

        final TextLabel expLabel = new TextLabel();
        expLabel.setFont(Resources.getFont());
        expLabel.setForeground(ThemeData.getForegroundColor());
        expLabel.setSingleLine(true);
        expLabel.setText("Extra XP:");

        expButton = new TextButton();
        expButton.setGravity(Gravity.RIGHT);
        expButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        expButton.setOnClickListener(() -> {
            if (controller != null) {
                controller.onExpButtonClick(weapon);
            }
        });

        add(expLabel, topMargin(2));
        add(expButton, topMargin(2));

        final TextLabel ammoLabel = new TextLabel();
        ammoLabel.setFont(Resources.getFont());
        ammoLabel.setForeground(ThemeData.getForegroundColor());
        ammoLabel.setSingleLine(true);
        ammoLabel.setText("Ammo / Max:");

        final HorizontalSpreadLayout ammoLayout = new HorizontalSpreadLayout();
        ammoLayout.setGap(4);

        ammoButton = new TextButton();
        ammoButton.setGravity(Gravity.RIGHT);
        ammoButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        ammoButton.setOnClickListener(() -> {
            if (controller != null) {
                controller.onAmmoButtonClick(weapon);
            }
        });

        maxAmmoButton = new TextButton();
        maxAmmoButton.setGravity(Gravity.RIGHT);
        maxAmmoButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        maxAmmoButton.setOnClickListener(() -> {
            if (controller != null) {
                controller.onMaxAmmoButtonClick(weapon);
            }
        });

        ammoLayout.add(ammoButton);
        ammoLayout.add(maxAmmoButton);

        add(ammoLabel, topMargin(2));
        add(ammoLayout, topMargin(2));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        final int y = titleLabel.getY() + titleLabel.getHeight() - 1;
        g.drawLine(0, y, getWidth() - 1, y);

        if (isChecked()) {
            final int bx = image.getParent().getX() + image.getX();
            final int by = image.getParent().getY() + image.getY();
            checkedImageBorder.paintBorder(image, g, bx, by, image.getWidth(), image.getHeight());
        }
    }

    @Override
    public void setChecked(boolean isChecked) {
        setChecked(isChecked, false);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    private void setChecked(boolean isChecked, boolean notify) {
        if (notify) {
            if (!isChecked()) {
                setCheckedInternal(isChecked, true);
            }
        } else {
            setCheckedInternal(isChecked, false);
        }
    }

    private void setCheckedInternal(boolean isChecked, boolean notify) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            if (notify && checkChangedListener != null) {
                checkChangedListener.onCheckedStateChanged(this, isChecked);
            }
            repaint();
        }
    }

    @Override
    public void setOnCheckedStateListener(OnCheckedStateChangedListener listener) {
        this.checkChangedListener = listener;
    }

    public void setComponentController(ComponentController controller) {
        this.controller = controller;
    }

    public void bind(Weapon weapon) {
        this.weapon = weapon;
        titleLabel.setText(String.format("%d - %s", weapon.id, weapon.title));
        image.setImage(weapon.image);
        levelButton.setText(numberFormatter.format(weapon.level));
        expButton.setText(numberFormatter.format(weapon.exp));
        ammoButton.setText(numberFormatter.format(weapon.currentAmmo));
        maxAmmoButton.setText(numberFormatter.format(weapon.maxAmmo));
    }

    public interface ComponentController {
        void onSetButtonClick(Weapon weapon);
        void onLevelButtonClick(Weapon weapon);
        void onExpButtonClick(Weapon weapon);
        void onAmmoButtonClick(Weapon weapon);
        void onMaxAmmoButtonClick(Weapon weapon);
    }
}
