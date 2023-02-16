package com.leo.cse.frontend.dialogs.niku;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.AppEventQueue;
import com.leo.cse.frontend.Ids;
import com.leo.cse.frontend.editor.Overlay;
import com.leo.cse.frontend.options.niku.NikuOptionsMenuItemsBuilderFactory;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.DurationPickerComponent;
import com.leo.cse.frontend.ui.components.menu.MenuBar;
import com.leo.cse.frontend.ui.components.menu.OptionsMenu;
import com.leo.cse.frontend.ui.layout.RootDialog;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;
import com.leo.cse.util.MathUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.util.List;

public class NikuEditDialog extends RootDialog implements AppEventQueue.DraggableComponent {
    private final static Dimension CONTENT_SIZE = new Dimension(210, 262);
    private final static Dimension MENU_SIZE = new Dimension(Integer.MAX_VALUE, 18);
    private final static int OPTIONS_MENU_MAX_WIDTH = 160;

    private final static String[] MENU_ITEMS = { "File", "Edit", "Help" };
    private final static int[] MENU_IDS = { Ids.MENU_290_REC_FILE, Ids.MENU_290_REC_EDIT, Ids.MENU_290_REC_HELP };

    private final MenuBar menuBar = new MenuBar();
    private final DurationPickerComponent durationComponent = new DurationPickerComponent();
    private final Overlay overlay = new Overlay();
    private int currentMenuItemId = -1;

    private final NikuEditController controller = new NikuEditController();
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public NikuEditDialog(
            Frame parentFrame,
            Component parentComponent,
            ProfileManager profileManager,
            GameResourcesManager resourcesManager) {
        super(parentFrame, "290.rec Editor", true);

        setMinimumSize(CONTENT_SIZE);
        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(initContentComponent());

        durationComponent.setDuration(0L);
        durationComponent.setPositiveButtonText("Cancel");
        durationComponent.setCallback(this::dispatchClose);

        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;

        pack();
    }

    private Component initContentComponent() {
        final StackLayout stackLayout = new StackLayout();

        final VerticalLayout verticalLayout = new VerticalLayoutWithBorder();
        verticalLayout.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        menuBar.setItems(MENU_ITEMS);
        menuBar.setItemClickListener(this::onMenuBarItemClicked);
        menuBar.setItemHoverListener(this::onMenuBarItemHovered);
        menuBar.setPreferredSize(MENU_SIZE);

        verticalLayout.add(menuBar);
        verticalLayout.add(durationComponent);

        overlay.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        overlay.setVisible(false);
        overlay.setOnClickListener(this::hideOverlay);

        stackLayout.add(verticalLayout, 0);
        stackLayout.add(overlay, topMargin(MENU_SIZE.height), 0);

        return stackLayout;
    }

    private void showOverlay() {
        overlay.setVisible(true);
        overlay.repaint();
    }

    private void hideOverlay() {
        currentMenuItemId = -1;
        overlay.setVisible(false);
    }

    private void onMenuBarItemClicked(Component anchor, int menuId) {
        overlay.removeAll();

        if (currentMenuItemId == menuId) {
            hideOverlay();
            return;
        }

        final int availWidth = MathUtils.coerceIn(CONTENT_SIZE.width - anchor.getX() - 8, 0, OPTIONS_MENU_MAX_WIDTH);
        final int maxWidth = availWidth < OPTIONS_MENU_MAX_WIDTH / 2
                ? OPTIONS_MENU_MAX_WIDTH
                : availWidth;

        final OptionsMenu optionsMenu = new OptionsMenu();
        optionsMenu.setMaximumSize(new Dimension(maxWidth, CONTENT_SIZE.height));

        final List<OptionsMenu.Option> options = new NikuOptionsMenuItemsBuilderFactory()
                .create(MENU_IDS[menuId])
                .build();

        currentMenuItemId = menuId;

        optionsMenu.setItems(options);
        optionsMenu.setItemClickListener(this::onOptionItemSelected);

        final LayoutConstraints lc = new LayoutConstraints();
        lc.topMargin  = -1;
        lc.leftMargin = availWidth < OPTIONS_MENU_MAX_WIDTH / 2
                ? anchor.getX() + anchor.getWidth() - maxWidth
                : Math.max(0, anchor.getX() - 1);

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
            case Ids.OPTION_290_REC_FILE_NEW:
                controller.unload(durationComponent::setDuration);
                break;

            case Ids.OPTION_290_REC_FILE_OPEN:
                controller.open(durationComponent::setDuration);
                break;

            case Ids.OPTION_290_REC_FILE_SAVE:
                controller.save(durationComponent.getDuration());
                break;

            case Ids.OPTION_290_REC_FILE_SAVE_AS:
                controller.saveAs(durationComponent.getDuration());
                break;

            case Ids.OPTION_290_REC_FILE_CLOSE:
                dispatchClose();
                break;

            case Ids.OPTION_290_REC_EDIT_SELECT_TITLE_SCREEN:
                final long initial = durationComponent.getDuration();
                final long selection = new BestTimeSelectionDialog(initial, profileManager, resourcesManager).select();
                if (initial != selection) {
                    durationComponent.setDuration(selection);
                }
                break;

            case Ids.OPTION_290_REC_HELP_ABOUT:
                final NikuInfoDialog dialog = new NikuInfoDialog(null, this, profileManager, resourcesManager);
                dialog.setVisible(true);
                break;
        }

        hideOverlay();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static class VerticalLayoutWithBorder extends VerticalLayout {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(ThemeData.getForegroundColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
