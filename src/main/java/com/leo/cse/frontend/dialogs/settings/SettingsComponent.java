package com.leo.cse.frontend.dialogs.settings;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.Pager;
import com.leo.cse.frontend.ui.components.tabs.BottomTabsLayout;
import com.leo.cse.frontend.ui.layout.JContainer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.EmptyBorder;

public class SettingsComponent extends JContainer {
    private final static int PAGE_ID_APP = 0;
    private final static int PAGE_ID_ADVANCED = 1;

    private final Pager pager = new Pager();
    private final BottomTabsLayout bottomTabs = new BottomTabsLayout();

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private Callback callback;

    public SettingsComponent(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        init();
        showPage(PAGE_ID_APP);
    }

    private void init() {
        pager.setBorder(new EmptyBorder(16, 16, 16, 16));
        pager.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(pager, 0);

        bottomTabs.setPreferredSize(new Dimension(Integer.MAX_VALUE, 18));
        bottomTabs.setTabItemClickListener(this::onBottomTabItemClicked);
        add(bottomTabs, 0);

        initBottomTabs();
    }

    private void initBottomTabs() {
        final List<BottomTabsLayout.TabItem> items = new ArrayList<>();

        items.add(new BottomTabsLayout.TabItem(PAGE_ID_APP, null, "Application"));
        items.add(new BottomTabsLayout.TabItem(PAGE_ID_ADVANCED, null, "Advanced"));

        bottomTabs.setItems(items);
    }

    private void onBottomTabItemClicked(Component component, int tabId) {
        showPage(tabId);
    }

    private void showPage(final int tabId) {
        final Component page;

        switch (tabId) {
            case PAGE_ID_APP:
                page = new AppSettingsPage(resourcesManager, this::onThemeChanged);
                break;

            case PAGE_ID_ADVANCED:
                page = new AdvancedSettingsPage(profileManager, resourcesManager);
                break;

            default:
                throw new IllegalArgumentException(String.format("Tab id is incorrect: %d", tabId));
        }

        pager.setPage(page);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void onThemeChanged() {
        if (callback != null) {
            callback.onThemeChanged();
        }
        showPage(PAGE_ID_APP);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight());
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int availHeight = maxHeight - (insets.bottom + insets.top);

        if (bottomTabs.isVisible()) {
            final Dimension bottomTabsSize = measureChild(bottomTabs, availWidth, availHeight);
            availHeight -= bottomTabsSize.height;
        }

        measureChild(pager, availWidth, availHeight);

        setMeasuredDimensions(container.getWidth(), container.getHeight());
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int top = insets.top;
        int bottom = container.getHeight() - insets.bottom;

        if (bottomTabs.isVisible()) {
            final Dimension bottomTabsSize = getChildDimension(bottomTabs);

            bottomTabs.setBounds(
                    left,
                    bottom - bottomTabsSize.height,
                    bottomTabsSize.width,
                    bottomTabsSize.height
            );
        }

        final Dimension pagerSize = getChildDimension(pager);
        pager.setBounds(
                left,
                top,
                pagerSize.width,
                pagerSize.height
        );
    }

    public interface Callback {
        void onThemeChanged();
    }
}
