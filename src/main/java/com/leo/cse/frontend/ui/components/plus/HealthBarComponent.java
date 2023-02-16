package com.leo.cse.frontend.ui.components.plus;

import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.GameResourcesManager;

import java.awt.Graphics;

import javax.swing.JComponent;

public class HealthBarComponent extends JComponent {
    private static final int MAX_VALUE = 99; // max visible value

    private final GameResourcesManager resourcesManager;

    private short hp = 0;
    private short maxHp = 0;
    private boolean hasResources = false;

    public HealthBarComponent(GameResourcesManager resourcesManager) {
        super();
        this.resourcesManager = resourcesManager;
    }

    public void setValues(short hp, short maxHp) {
        boolean changed = false;

        final boolean hasResources = resourcesManager.hasResources();
        if (this.hasResources != hasResources) {
            this.hasResources = hasResources;
            changed = true;
        }

        if (this.hp != hp) {
            this.hp = (hp > MAX_VALUE) ? MAX_VALUE : hp;
            changed = true;
        }
        if (this.maxHp != maxHp) {
            this.maxHp = (maxHp > MAX_VALUE) ? MAX_VALUE : maxHp;
            changed = true;
        }
        if (changed) {
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hp <= 0) {
            return;
        }

        if (resourcesManager.hasResources()) {
            final GameResources resources = resourcesManager.getResources();
            g.drawImage(resources.getHealthBarImage(), 0, 0, null);

            if (maxHp > 0) {
                g.drawImage(resources.getHealthBarFillImage(),
                        48,
                        2,
                        (int) (78 * (hp / (float) maxHp)),
                        10,
                        null);
            }

            final int firstDigit = hp / 10;
            if (firstDigit != 0) {
                g.drawImage(resources.getNumberImage(firstDigit), 16, 0, null);
            }
            g.drawImage(resources.getNumberImage(hp % 10), 32, 0, null);
        }
    }
}
