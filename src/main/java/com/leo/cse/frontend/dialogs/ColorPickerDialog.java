package com.leo.cse.frontend.dialogs;

import com.leo.cse.frontend.AppEventQueue;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.color.ColorPickerComponent;
import com.leo.cse.frontend.ui.layout.RootDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;

public class ColorPickerDialog extends RootDialog implements AppEventQueue.DraggableComponent {
    private final static Dimension CONTENT_SIZE = new Dimension(300, 211);

    private final ColorPickerComponent pickerComponent = new ColorPickerComponent();

    public ColorPickerDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "Edit Colors", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(pickerComponent);
        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static Color pick(Component parentComponent,
                             Color color) {
        final ColorPickerDialog dialog = new ColorPickerDialog(null, parentComponent);
        final ColorPickerComponent pickerComponent = dialog.pickerComponent;

        pickerComponent.setSelectedColor(color);
        final Color[] result = { color };

        pickerComponent.setCallback(new ColorPickerComponent.Callback() {
            @Override
            public void onPositiveButtonClicked() {
                result[0] = pickerComponent.getSelectedColor();
                dialog.dispatchClose();
            }

            @Override
            public void onNegativeButtonClicked() {
                dialog.dispatchClose();
            }
        });

        dialog.setVisible(true);
        dialog.dispose();

        return result[0];
    }
}
