package com.leo.cse.frontend.ui.components.tabs;

import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.layout.HorizontalSpreadLayout;
import com.leo.cse.util.ColorUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

public class BottomTabsLayout extends HorizontalSpreadLayout {
    private TabItemClickListener itemClickListener;
    private int currentTab = 0;

    public BottomTabsLayout() {
        super();
    }

    public void setTabItemClickListener(TabItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItems(List<TabItem> tabs) {
        removeAll();
        currentTab = 0;

        final int tabsSize = tabs.size();
        for (int i = 0; i < tabsSize; i++) {
            final int index = i;
            TabItem tabItem = tabs.get(i);
            final BottomTab tab = new BottomTab();

            tab.setIcon(tabItem.icon);
            tab.setText(tabItem.title);
            tab.setOnClickListener(() -> {
                if (itemClickListener != null) {
                    itemClickListener.onTabItemClicked(tab, tabItem.id);
                }
                currentTab = index;
                repaint();
            });

            tab.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            add(tab);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        final int childCount = getComponentCount();
        int visibleCount = 0;

        for (int i = 0; i < childCount; ++i) {
            visibleCount++;
        }

        int j = 0;
        for (int i = 0; i < childCount; ++i) {
            final Component child = getComponent(i);
            if (child.isVisible() && ++j < visibleCount) {
                g.setColor(ThemeData.getForegroundColor());
                g.drawLine(
                        child.getX() + child.getWidth(),
                        child.getY(),
                        child.getX() + child.getWidth(),
                        child.getY() + child.getHeight()
                );
            }
            if (child.isVisible() && currentTab == i) {
                paintCurrentTabTopLine(g, child);
            }
        }
    }

    private void paintCurrentTabTopLine(Graphics g, Component child) {
        final boolean isTabHovered = (child instanceof BottomTab && ((BottomTab) child).isHovered());
        if (isTabHovered) {
            g.setColor(ColorUtils.blendOpaque(ThemeData.getBackgroundColor(), ThemeData.getHoverColor()));
        } else {
            g.setColor(ThemeData.getBackgroundColor());
        }
        g.drawLine(
                child.getX() + 1,
                child.getY(),
                child.getX() + child.getWidth() - 1,
                child.getY()
        );
    }

    public interface TabItemClickListener {
        void onTabItemClicked(Component target, int id);
    }

    public static class TabItem {
        private final int id;
        private final Image icon;
        private final String title;

        public TabItem(int id, Image icon, String title) {
            this.id = id;
            this.icon = icon;
            this.title = title;
        }
    }
}
