package com.leo.cse.frontend.editor;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.CaveSaveEdit;
import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.actions.OptionsActionsBinder;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.frontend.options.OptionsMenuItemsBuilderFactory;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.Pager;
import com.leo.cse.frontend.ui.components.menu.MenuBar;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;
import com.leo.cse.frontend.ui.components.tabs.BottomTabsLayout;
import com.leo.cse.frontend.dialogs.ResourcesLoaderDialog;
import com.leo.cse.frontend.editor.pages.CSPlusPage;
import com.leo.cse.frontend.editor.pages.FlagsPage;
import com.leo.cse.frontend.editor.pages.GeneralPage;
import com.leo.cse.frontend.editor.pages.InventoryPage;
import com.leo.cse.frontend.editor.pages.MapFlagsPage;
import com.leo.cse.frontend.editor.pages.VariablesPage;
import com.leo.cse.frontend.editor.pages.WarpsPage;
import com.leo.cse.frontend.editor.pages.WelcomePage;
import com.leo.cse.frontend.ui.layout.ComponentsLayoutManager;
import com.leo.cse.frontend.ui.layout.ValidateRootPanel;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SaveEditorPanel extends ValidateRootPanel implements
        ProfileStateChangeListener,
        OnGameResourcesLoadingStateChangeListener {

    private final static String[] MENU_ITEMS = {"File", "Edit", "Tools", "Help"};

    private final Frame parentFrame;
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private final SaveEditorController controller;

    private final MenuBar menuBar = new MenuBar();
    private final Pager pager = new Pager();
    private final BottomTabsLayout bottomTabs = new BottomTabsLayout();
    private final Overlay overlay = new Overlay();

    private int currentMenuItemId = -1;
    private int currentTabId = Ids.TAB_WELCOME_PAGE;

    public SaveEditorPanel(Frame parentFrame, ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        setLayout(new LayoutManager());
        this.parentFrame = parentFrame;
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.controller = new SaveEditorController(profileManager, resourcesManager);

        onCreateComponent();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideOverlay();
            }
        });

        new OptionsActionsBinder(profileManager, resourcesManager)
                .bind(this, this::onOptionItemSelected);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        profileManager.addListener(this);
        resourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        resourcesManager.removeListener(this);
        super.removeNotify();
    }

    private void onCreateComponent() {
        menuBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 18));

        menuBar.setItems(MENU_ITEMS);
        menuBar.setItemClickListener(this::onMenuBarItemClicked);
        menuBar.setItemHoverListener(this::onMenuBarItemHovered);

        add(menuBar, 0);

        pager.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(pager, 0);

        bottomTabs.setPreferredSize(new Dimension(Integer.MAX_VALUE, 18));
        bottomTabs.setTabItemClickListener(this::onBottomTabItemClicked);
        bottomTabs.setVisible(false);
        add(bottomTabs, 0);

        if (profileManager.hasProfile()) {
            showBottomTabs();
        } else {
            showWelcomePage();
        }

        overlay.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        overlay.setVisible(false);
        overlay.setOnClickListener(this::hideOverlay);

        add(overlay, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // paint background manually so it could be changed in runtime
        g.setColor(ThemeData.getBackgroundColor());
        g.fillRect(0, 0, getWidth(), getHeight());

        // paint borders around pager where needed
        g.setColor(ThemeData.getForegroundColor());
        final int height = bottomTabs.isVisible() ? pager.getHeight() + 1 : pager.getHeight();
        g.drawRect(pager.getX(), pager.getY() - 1, pager.getWidth() - 1, height);
    }

    private void onBottomTabItemClicked(Component component, int tabId) {
        final Component page;

        switch (tabId) {
            case Ids.TAB_GENERAL:
                page = new GeneralPage(profileManager, resourcesManager, () -> {
                    controller.loadResources(this);
                });
                break;
            case Ids.TAB_INVENTORY:
                page = new InventoryPage(profileManager, resourcesManager);
                break;
            case Ids.TAB_WARPS:
                page = new WarpsPage(profileManager, resourcesManager);
                break;
            case Ids.TAB_FLAGS:
                page = new FlagsPage(profileManager, resourcesManager);
                break;
            case Ids.TAB_MAP_FLAGS:
                page = new MapFlagsPage(profileManager, resourcesManager);
                break;
            case Ids.TAB_VARS:
                page = new VariablesPage(profileManager);
                break;
            case Ids.TAB_PLUS:
                page = new CSPlusPage(profileManager, resourcesManager);
                break;
            default:
                throw new IllegalArgumentException(String.format("Tab id is incorrect: %d", tabId));
        }

        currentTabId = tabId;
        pager.setPage(page);
    }

    private void onMenuBarItemClicked(Component anchor, int menuId) {
        overlay.removeAll();

        if (currentMenuItemId == menuId) {
            hideOverlay();
            return;
        }

        final OptionsMenu optionsMenu = new OptionsMenu();
        optionsMenu.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));

        final List<OptionsMenu.Option> options = new OptionsMenuItemsBuilderFactory(profileManager,
                resourcesManager, currentTabId)
                .create(menuId)
                .build();

        currentMenuItemId = menuId;

        optionsMenu.setItems(options);
        optionsMenu.setItemClickListener(this::onOptionItemSelected);

        final LayoutConstraints lc = new LayoutConstraints();
        lc.topMargin = -1;
        lc.leftMargin = Math.max(0, anchor.getX() - 1);

        overlay.add(optionsMenu, lc);

        showOverlay();
    }

    private void onMenuBarItemHovered(Component anchor, int menuId) {
        if (currentMenuItemId != -1 && currentMenuItemId != menuId) {
            onMenuBarItemClicked(anchor, menuId); // show another options menu
        }
    }

    private void onOptionItemSelected(int optionId) {
        switch (optionId) {
            case Ids.OPTION_FILE_NEW:
                controller.createFileDefault(this);
                break;

            case Ids.OPTION_FILE_NEW_PLUS:
                controller.createFilePlus(this);
                break;

            case Ids.OPTION_FILE_OPEN:
                controller.openFile(this);
                break;

            case Ids.OPTION_SLOT_CHANGE:
                onSelectPlusSlot();
                break;

            case Ids.OPTION_FILE_CLOSE:
                controller.closeFile(this);
                break;

            case Ids.OPTION_RESOURCES_LOAD:
                controller.loadResources(this);
                break;

            case Ids.OPTION_RESOURCES_UNLOAD:
                controller.clearResources();
                break;

            case Ids.OPTION_GAME_RUN:
                controller.launchGame();
                break;

            case Ids.OPTION_FILE_SAVE:
                controller.saveProfile(this);
                break;

            case Ids.OPTION_FILE_SAVE_AS:
                controller.saveProfileAs(this);
                break;

            case Ids.OPTION_SETTINGS:
                controller.openSettings(parentFrame, this);
                changeTheme();
                break;

            case Ids.OPTION_EXIT:
                CaveSaveEdit.exit();
                break;

            case Ids.OPTION_EDIT_UNDO:
                profileManager.undo();
                break;

            case Ids.OPTION_EDIT_REDO:
                profileManager.redo();
                break;

            case Ids.OPTION_TOOLS_290_REC:
                controller.showNikuEditorDialog(parentFrame, this);
                break;

            case Ids.OPTION_TOOLS_SAVE_POINTS:
                controller.selectSavePoint();
                break;

            case Ids.OPTION_HELP_WELCOME_PAGE:
                if (currentTabId != Ids.TAB_WELCOME_PAGE) {
                    showWelcomePage();
                    hideBottomTabs();
                } else {
                    onBottomTabItemClicked(null, Ids.TAB_GENERAL);
                    showBottomTabs();
                }
                break;

            case Ids.OPTION_HELP_CHECK_UPDATES:
                controller.checkUpdates();
                break;

            case Ids.OPTION_HELP_ABOUT:
                controller.showAboutDialog(parentFrame, this);
                break;

            default:
                AppLogger.info("Option clicked:" + optionId);
                break;
        }

        hideOverlay();
    }

    private void showOverlay() {
        overlay.setVisible(true);
        overlay.repaint();
    }

    private void hideOverlay() {
        currentMenuItemId = -1;
        overlay.setVisible(false);
    }

    private void showWelcomePage() {
        currentTabId = Ids.TAB_WELCOME_PAGE;
        pager.setPage(new WelcomePage(profileManager, resourcesManager, controller, () -> {
            onOptionItemSelected(Ids.OPTION_HELP_WELCOME_PAGE);
        }));
    }

    private void showBottomTabs() {
        final List<BottomTabsLayout.TabItem> items = new ArrayList<>();

        final MCI mci = profileManager.getCurrentMCI();
        final boolean plus = profileManager.isCurrentProfilePlus();
        final boolean var = !plus && mci.hasSpecial("VarHack");

        items.add(new BottomTabsLayout.TabItem(Ids.TAB_GENERAL, Resources.getTabIcon(Ids.ICON_TAB_GENERAL), "General"));
        items.add(new BottomTabsLayout.TabItem(Ids.TAB_INVENTORY, Resources.getTabIcon(Ids.ICON_TAB_INVENTORY), "Inventory"));
        items.add(new BottomTabsLayout.TabItem(Ids.TAB_WARPS, Resources.getTabIcon(Ids.ICON_TAB_WARPS), "Warps"));
        items.add(new BottomTabsLayout.TabItem(Ids.TAB_FLAGS, Resources.getTabIcon(Ids.ICON_TAB_FLAGS), "Flags"));
        items.add(new BottomTabsLayout.TabItem(Ids.TAB_MAP_FLAGS, Resources.getTabIcon(Ids.ICON_TAB_MAP_FLAGS), "Map Flags"));

        if (var) {
            items.add(new BottomTabsLayout.TabItem(Ids.TAB_VARS, Resources.getTabIcon(Ids.ICON_TAB_VARS), "Variables"));
        } else if (plus) {
            items.add(new BottomTabsLayout.TabItem(Ids.TAB_PLUS, Resources.getTabIcon(Ids.ICON_TAB_PLUS), "Cave Story+"));
        }

        bottomTabs.setItems(items);
        bottomTabs.setVisible(true);
    }

    private void hideBottomTabs() {
        bottomTabs.setVisible(false);
    }

    private void onSelectPlusSlot() {
        overlay.removeAll();
        showOverlay();
        controller.selectSaveSlot(parentFrame,this);
        hideOverlay();
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.BEGIN) {
            ResourcesLoaderDialog.show(parentFrame, this, resourcesManager);
        }
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (ProfileStateEvent.LOADED == event) {
            if (profileManager.isCurrentProfilePlus()) {
                onSelectPlusSlot();
            }
        }

        final Boolean slotChangedPayload = (ProfileStateEvent.SLOT_CHANGED == event)
                ? (payload instanceof Boolean) ? (Boolean) payload : null
                : null;

        final boolean isSlotChanged = Boolean.TRUE.equals(slotChangedPayload);
        final boolean isSlotDeleted = Boolean.FALSE.equals(slotChangedPayload);

        if (ProfileStateEvent.LOADED == event || ProfileStateEvent.MCI_CHANGED == event || isSlotChanged) {
            onBottomTabItemClicked(null, Ids.TAB_GENERAL);
            showBottomTabs();
        } else if (ProfileStateEvent.UNLOADED == event || isSlotDeleted) {
            showWelcomePage();
            hideBottomTabs();
        }
    }

    public void changeTheme() {
        if (currentTabId != Ids.TAB_WELCOME_PAGE) {
            onBottomTabItemClicked(null, currentTabId);
        } else {
            showWelcomePage();
        }
        repaint();
    }

    private class LayoutManager extends ComponentsLayoutManager {
        @Override
        protected void onMeasure(Container container, int maxWidth, int maxHeight) {
            final Insets insets = container.getInsets();
            int availWidth = maxWidth - (insets.right + insets.left);
            int totalHeight = maxHeight - (insets.bottom + insets.top);
            int availHeight = totalHeight;

            final Dimension menuSize = measureChild(menuBar, availWidth, availHeight);
            availHeight -= menuSize.height;

            if (bottomTabs.isVisible()) {
                final Dimension bottomTabsSize = measureChild(bottomTabs, availWidth, availHeight);
                availHeight -= bottomTabsSize.height;
            }

            measureChild(pager, availWidth, availHeight);

            if (overlay.isVisible()) {
                measureChild(overlay, availWidth, totalHeight - menuSize.height);
            }

            setMeasuredDimensions(container.getWidth(), container.getHeight());
        }

        @Override
        protected void onLayout(Container container) {
            final Insets insets = container.getInsets();
            int left = insets.left;
            int top = insets.top;
            int bottom = container.getHeight() - insets.bottom;

            final Dimension menuSize = getChildDimension(menuBar);

            menuBar.setBounds(
                    left,
                    top,
                    menuSize.width,
                    menuSize.height
            );

            top += menuSize.height;

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

            if (overlay.isVisible()) {
                final Dimension overlaySize = getChildDimension(overlay);
                overlay.setBounds(
                        left,
                        menuSize.height,
                        overlaySize.width,
                        overlaySize.height
                );
            }
        }
    }
}
