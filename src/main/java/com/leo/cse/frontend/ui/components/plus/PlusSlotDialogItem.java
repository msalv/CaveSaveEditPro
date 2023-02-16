package com.leo.cse.frontend.ui.components.plus;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topRightMargin;

import com.leo.cse.dto.PlusSlot;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Dimension;

public class PlusSlotDialogItem extends VerticalLayout {
    private final PlusSlotComponent slotComponent;

    private final TextButton createButton = new TextButton();
    private final TextButton copyButton = new TextButton();
    private final TextButton deleteButton = new TextButton();
    private final TextButton cancelButton = new TextButton();
    private final TextButton pasteButton = new TextButton();
    private Callback callback;

    private boolean isBound;

    public PlusSlotDialogItem(GameResourcesManager resourcesManager) {
        super();
        slotComponent = new PlusSlotComponent(resourcesManager);
        init();
    }

    public void setOnClickListener(Runnable action) {
        slotComponent.setOnClickListener(action);
    }

    private void init() {
        final HorizontalLayout container = new HorizontalLayout();

        createButton.setMinimumSize(new Dimension(88, 19));
        createButton.setText("New");
        createButton.setVisible(false);
        createButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onCreateButtonClicked();
            }
        });

        copyButton.setMinimumSize(new Dimension(88, 19));
        copyButton.setText("Copy");
        copyButton.setVisible(false);
        copyButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onCopyButtonClicked();
            }
        });

        deleteButton.setMinimumSize(new Dimension(88, 19));
        deleteButton.setText("Delete");
        deleteButton.setVisible(false);
        deleteButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onDeleteButtonClicked();
            }
        });

        cancelButton.setMinimumSize(new Dimension(88, 19));
        cancelButton.setText("Cancel");
        cancelButton.setVisible(false);
        cancelButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onCancelButtonClicked();
            }
        });

        pasteButton.setMinimumSize(new Dimension(88, 19));
        pasteButton.setText("Paste");
        pasteButton.setVisible(false);
        pasteButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onPasteButtonClicked();
            }
        });

        container.add(pasteButton, alignRight(rightMargin(6)));
        container.add(deleteButton, alignRight(rightMargin(6)));
        container.add(cancelButton, alignRight(rightMargin(6)));
        container.add(copyButton, alignRight(rightMargin(6)));
        container.add(createButton, alignRight(rightMargin(6)));

        container.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        slotComponent.setMinimumSize(new Dimension(Integer.MAX_VALUE, 110));

        add(slotComponent);
        add(container, topRightMargin(9, 4));
    }

    public void bind(PlusSlot plusSlot) {
        isBound = true;
        slotComponent.bind(plusSlot);
    }

    public void unbind(int position) {
        isBound = false;
        slotComponent.unbind(position);
    }

    public void idle() {
        bindState(State.IDLE);
    }

    public void copying() {
        bindState(State.COPY);
    }

    public void waitingForPaste() {
        bindState(State.PASTE);
    }

    private void bindState(State state) {
        createButton.setVisible(false);
        copyButton.setVisible(false);
        deleteButton.setVisible(false);
        cancelButton.setVisible(false);
        pasteButton.setVisible(false);

        deleteButton.setEnabled(true);

        if (state == State.PASTE) {
            pasteButton.setVisible(true);
        } else if (!isBound) {
            createButton.setVisible(true);
        } else if (state == State.COPY) {
            cancelButton.setVisible(true);
            deleteButton.setVisible(true);
            deleteButton.setEnabled(false);
        } else {
            copyButton.setVisible(true);
            deleteButton.setVisible(true);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onCreateButtonClicked();
        void onCopyButtonClicked();
        void onPasteButtonClicked();
        void onDeleteButtonClicked();
        void onCancelButtonClicked();
    }

    private enum State {
        IDLE,
        COPY,
        PASTE
    }
}