package com.leo.cse.frontend.dialogs;

import com.leo.cse.frontend.AppEventQueue;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.TimePickerComponent;
import com.leo.cse.frontend.ui.layout.RootDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.util.Date;

public class TimePickerDialog extends RootDialog implements AppEventQueue.DraggableComponent {
    private final static Dimension CONTENT_SIZE = new Dimension(210, 244);

    private final TimePickerComponent timePickerComponent = new TimePickerComponent();

    public TimePickerDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "Set Time", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(timePickerComponent);
        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static Date pick(Component parentComponent,
                            Date time) {
        final TimePickerDialog dialog = new TimePickerDialog(null, parentComponent);
        final TimePickerComponent pickerComponent = dialog.timePickerComponent;

        pickerComponent.setTime(time);
        final Date[] result = { time };

        pickerComponent.setCallback(() -> {
            result[0] = pickerComponent.getTime();
            dialog.dispatchClose();
        });

        dialog.setVisible(true);
        dialog.dispose();

        return result[0];
    }
}
