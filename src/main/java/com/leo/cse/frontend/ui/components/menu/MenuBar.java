package com.leo.cse.frontend.ui.components.menu;

import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.decorations.VerticalSeparatorDecoration;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class MenuBar extends HorizontalLayout {
    private MenuItemClickListener clickListener;
    private MenuItemHoverListener hoverListener;

    private final VerticalSeparatorDecoration separatorsDecoration = new VerticalSeparatorDecoration();

    public MenuBar() {
        super();
    }

    public void setItems(String[] items) {
        removeAll();

        for (int i = 0; i < items.length; ++i) {
            final MenuBarItem item = new MenuBarItem();
            item.setText(items[i]);

            final int id = i;
            item.setOnClickListener(() -> {
                if (clickListener != null) {
                    clickListener.onMenuItemClicked(item, id);
                }
            });
            item.setOnHoverListener(() -> {
                if (hoverListener != null) {
                    hoverListener.onMenuItemHovered(item, id);
                }
            });

            item.setPreferredSize(new Dimension(66, Integer.MAX_VALUE));
            add(item);
        }
    }

    public void setItemClickListener(MenuItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItemHoverListener(MenuItemHoverListener hoverListener) {
        this.hoverListener = hoverListener;
    }

    @Override
    public void setEnabled(boolean b) {
        if (isEnabled() == b) {
            return;
        }

        super.setEnabled(b);

        for (int i = 0; i < getComponentCount(); ++i) {
            getComponent(i).setEnabled(b);
        }

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        separatorsDecoration.paint(g, this);
    }

    public interface MenuItemClickListener {
        void onMenuItemClicked(Component target, int id);
    }

    public interface MenuItemHoverListener {
        void onMenuItemHovered(Component target, int id);
    }
}
