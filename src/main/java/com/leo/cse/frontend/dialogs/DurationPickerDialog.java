package com.leo.cse.frontend.dialogs;

import com.leo.cse.frontend.AppEventQueue;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.DurationPickerComponent;
import com.leo.cse.frontend.ui.layout.RootDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;

public class DurationPickerDialog extends RootDialog implements AppEventQueue.DraggableComponent {
    private final static Dimension CONTENT_SIZE = new Dimension(210, 244);

    private final DurationPickerComponent durationPickerComponent = new DurationPickerComponent();

    public DurationPickerDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "Set Duration", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(durationPickerComponent);
        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static long pick(Component parentComponent,
                            long initialValue) {
        final DurationPickerDialog dialog = new DurationPickerDialog(null, parentComponent);
        final DurationPickerComponent pickerComponent = dialog.durationPickerComponent;

        pickerComponent.setDuration(initialValue);
        final long[] result = { initialValue };

        pickerComponent.setCallback(() -> {
            result[0] = pickerComponent.getDuration();
            dialog.dispatchClose();
        });

        dialog.setVisible(true);
        dialog.dispose();

        return result[0];
    }
}
