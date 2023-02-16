package com.leo.cse.frontend.ui.components.plus;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.profile.PlusProfileManager;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.factory.PlusSlotFactory;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.GridLayout;
import com.leo.cse.frontend.ui.layout.HorizontalSpreadLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

public class PlusSlots extends VerticalLayout implements OnGameResourcesLoadingStateChangeListener {
    private static final int SLOT_COUNT = 6;
    private final PlusSlotDialogItem[] items = new PlusSlotDialogItem[SLOT_COUNT];
    private final SlotItemCallback[] callbacks = new SlotItemCallback[SLOT_COUNT];

    private final ProfileManager profileManager;
    private final PlusProfileManager plusProfileManager;
    private final GameResourcesManager resourcesManager;

    private final PlusSlotFactory slotFactory;

    private Callback callback;
    private State state = new IdleState();

    public PlusSlots(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;
        this.plusProfileManager = profileManager.getPlusProfileManager();
        this.resourcesManager = resourcesManager;
        this.slotFactory = new PlusSlotFactory(profileManager, resourcesManager);
        init();
        bind();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        resourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        resourcesManager.removeListener(this);
        super.removeNotify();
    }

    private void init() {
        setBorder(new EmptyBorder(10, 20, 20, 20));
        add(initHeader());
        add(initGrid(), topMargin(10));
    }

    private Component initHeader() {
        final HorizontalSpreadLayout header = new HorizontalSpreadLayout();
        header.setGap(16);
        header.add(initHeaderLabel("Cave Story"));
        header.add(initHeaderLabel("Curly Story"));
        return header;
    }

    private Component initHeaderLabel(String text) {
        final TextLabel textLabel = new TextLabel();
        textLabel.setSingleLine(true);
        textLabel.setTextColor(ThemeData.getForegroundColor());
        textLabel.setFont(Resources.getFont());
        textLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        textLabel.setText(text);
        return textLabel;
    }

    private Component initGrid() {
        final GridLayout grid = new GridLayout();
        grid.setSpanCount(2);
        grid.setHorizontalGap(16);
        grid.setVerticalGap(16);

        for (int i = 0; i < SLOT_COUNT; i++) {
            final int j = i / 2 + (i % 2 == 0 ? 0 : SLOT_COUNT / 2);
            AppLogger.info("Creating component for Slot #" + j);

            final PlusSlotDialogItem component = new PlusSlotDialogItem(resourcesManager);
            component.setMinimumSize(new Dimension(Integer.MAX_VALUE, 138));
            component.setOnClickListener(() -> {
                onSlotClicked(j);
            });
            final SlotItemCallback callback = new SlotItemCallback(j);
            component.setCallback(callback);
            grid.add(component);

            items[j] = component;
            callbacks[j] = callback;
        }

        return grid;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void setState(State state) {
        this.state = state;
        revalidate();
        repaint();
    }

    private void bind() {
        for (int i = 0; i < items.length; i++) {
            final boolean slotExists = plusProfileManager.isSlotExists(i);
            if (slotExists) {
                items[i].bind(slotFactory.create(i));
            } else {
                items[i].unbind(i);
            }
            items[i].idle();
        }
    }

    private void onSlotClicked(int j) {
        AppLogger.info("Slot clicked: " + j);

        if (state instanceof IdleState) {
            final boolean slotExists = plusProfileManager.isSlotExists(j);
            if (!slotExists) {
                callbacks[j].onCreateButtonClicked();
            }
            plusProfileManager.setCurrentSlotId(j);
            profileManager.notifySlotChanged(true);
            if (callback != null) {
                callback.onSlotClicked();
            }
        } else if (state instanceof CopyingState) {
            callbacks[j].onPasteButtonClicked();
        }
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.DONE) {
            bind();
        }
    }

    public interface Callback {
        void onSlotClicked();
    }

    // -- States -- //

    private static abstract class State {}
    private static final class IdleState extends State {}
    private static final class CopyingState extends State {
        public final int slotId;
        public CopyingState(int slotId) {
            this.slotId = slotId;
        }
    }

    // -- Slot item callback -- //

    private class SlotItemCallback implements PlusSlotDialogItem.Callback {
        private final int position;

        public SlotItemCallback(int i) {
            position = i;
        }

        @Override
        public void onCreateButtonClicked() {
            final boolean isSlotExists = plusProfileManager.isSlotExists(position);

            if (isSlotExists) {
                return;
            }

            final StartPoint startPoint;
            if (resourcesManager.hasResources()) {
                startPoint = resourcesManager.getResources().getStartPoint();
            } else {
                startPoint = StartPoint.DEFAULT;
            }

            plusProfileManager.createSlot(position, startPoint);
            items[position].bind(slotFactory.create(position));
            items[position].idle();
        }

        @Override
        public void onCopyButtonClicked() {
            setState(new CopyingState(position));
            for (int i = 0; i < items.length; i++) {
                if (position == i) {
                    items[i].copying();
                } else {
                    items[i].waitingForPaste();
                }
            }
        }

        @Override
        public void onPasteButtonClicked() {
            final CopyingState state = (PlusSlots.this.state instanceof CopyingState) ? (CopyingState) PlusSlots.this.state : null;
            if (state == null) {
                return;
            }

            final boolean isSlotExists = plusProfileManager.isSlotExists(position);

            if (isSlotExists) {
                final int option = JOptionPane.showConfirmDialog(
                        PlusSlots.this,
                        String.format("Are you sure you want to overwrite file #%d with file #%d?", position + 1, state.slotId + 1),
                        "Confirm file overwrite",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            plusProfileManager.cloneSlot(state.slotId, position);

            items[position].bind(slotFactory.create(position));

            for (PlusSlotDialogItem item : items) {
                item.idle();
            }

            setState(new IdleState());
        }

        @Override
        public void onDeleteButtonClicked() {
            final boolean isSlotExists = plusProfileManager.isSlotExists(position);

            if (!isSlotExists) {
                return;
            }

            final int option = JOptionPane.showConfirmDialog(
                    PlusSlots.this,
                    String.format("Are you sure you want to delete slot #%d?", position + 1),
                    "Confirm slot deletion",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                plusProfileManager.deleteSlot(position);
                final boolean isCurrentSlotExists = plusProfileManager.isCurrentSlotExists();
                if (!isCurrentSlotExists) {
                    profileManager.notifySlotChanged(false);
                }

                items[position].unbind(position);
                items[position].idle();
            }
        }

        @Override
        public void onCancelButtonClicked() {
            setState(new IdleState());
            for (PlusSlotDialogItem item : items) {
                item.idle();
            }
        }
    }
}
