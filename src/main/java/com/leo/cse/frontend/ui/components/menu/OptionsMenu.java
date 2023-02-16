package com.leo.cse.frontend.ui.components.menu;

import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

public class OptionsMenu extends VerticalLayout {
    private OptionsItemClickListener clickListener;

    public OptionsMenu() {
        super();
        setBackground(ThemeData.getBackgroundColor());
    }

    public void setItems(List<Option> items) {
        removeAll();

        for (final Option option : items) {
            final OptionsMenuItem item = new OptionsMenuItem();
            item.setPreferredSize(new Dimension(Integer.MAX_VALUE, 22));

            item.setIcon(option.icon);
            item.setText(option.title);
            item.setHotKey(option.hotKey);
            item.setEnabled(option.isEnabled);

            item.setOnClickListener(() -> {
                if (clickListener != null) {
                    clickListener.onOptionsItemClicked(option.id);
                }
            });
            add(item);
        }
    }

    public void setItemClickListener(OptionsItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paint(g);

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        final int childCount = getComponentCount();
        for (int i = 0; i < childCount; ++i) {
            final Component child = getComponent(i);
            if (child.isVisible()) {
                g.setColor(ThemeData.getForegroundColor());
                g.drawLine(
                        child.getX(),
                        child.getY() + child.getHeight(),
                        child.getX() + child.getWidth(),
                        child.getY() + child.getHeight()
                );
            }
        }
    }

    public interface OptionsItemClickListener {
        void onOptionsItemClicked(int id);
    }

    public static class Option {
        public final int id;
        public final Image icon;
        public final String title;
        public final String hotKey;
        public final boolean isEnabled;

        public Option(int id, Image icon, String title, String hotKey, boolean isEnabled) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.hotKey = hotKey;
            this.isEnabled = isEnabled;
        }

        public Option(int id, Image icon, String title, String hotKey) {
            this(id, icon, title, hotKey, true);
        }

        public Option(int id, Image icon, String title) {
            this(id, icon, title, null, true);
        }

        public Option(int id, Image icon, String title, boolean isEnabled) {
            this(id, icon, title, null, isEnabled);
        }
    }
}
