package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.center;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.dto.factory.WarpSlotsFactory;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.components.WarpSlotComponent;
import com.leo.cse.frontend.editor.selectors.WarpLocationSelectionDialog;
import com.leo.cse.frontend.editor.selectors.WarpSelectionDialog;
import com.leo.cse.frontend.ui.layout.GridLayout;
import com.leo.cse.frontend.ui.layout.StackLayout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class WarpsPage extends StackLayout implements ProfileStateChangeListener, OnGameResourcesLoadingStateChangeListener {
    private static final int SLOTS_NUM = 7;
    private final List<WarpSlotComponent> components = new ArrayList<>();

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    private final WarpSlotsFactory warpSlotFactory;

    public WarpsPage(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.warpSlotFactory = new WarpSlotsFactory(profileManager, resourcesManager);
        initPage();
        bind();
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

    private void initPage() {
        final GridLayout grid = new GridLayout();
        grid.setHorizontalGap(16);
        grid.setVerticalGap(16);
        grid.setSpanCount(3);
        grid.setMaximumSize(new Dimension(542, Integer.MAX_VALUE));

        for (int i = 0; i < SLOTS_NUM; i++) {
            final WarpSlotComponent comp = new WarpSlotComponent();

            final int position = i;
            comp.setOnClickListener((warpSlot) -> {
                new WarpSelectionDialog(
                        warpSlot,
                        position,
                        profileManager,
                        resourcesManager).select();
            });

            comp.setOnLocationButtonClickListener((warpSlot) -> {
                new WarpLocationSelectionDialog(warpSlot, position, profileManager).select();
            });

            components.add(comp);
            grid.add(comp);
        }

        add(grid, center());
    }

    private void bind() {
        final int size = components.size();
        for (int i = 0; i < size; i++) {
            final WarpSlotComponent comp = components.get(i);
            comp.setLabelText(String.format("Warp Slot %d", i + 1));
            comp.bind(warpSlotFactory.create(i));
        }
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        super.onMeasure(container, maxWidth, maxHeight);

        final Insets insets = container.getInsets();
        final int availWidth = maxWidth - (insets.right + insets.left);
        final int availHeight = maxHeight - (insets.bottom + insets.top);
        setMeasuredDimensions(availWidth, availHeight);
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.MODIFIED || event == ProfileStateEvent.SLOT_CHANGED) {
            bind();
        }
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.DONE || state == GameResourcesLoadingState.NONE) {
            bind();
        }
    }
}
