package com.leo.cse.frontend.ui.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.JDialog;

public class RootDialog extends JDialog {

    public RootDialog(Frame parentFrame, String title, boolean isModal) {
        super(parentFrame, title, isModal);
        replaceContentPane();
    }

    private void replaceContentPane() {
        final ValidateRootPanel contentPane = new ValidateRootPanel();

        contentPane.setLayout(new BorderLayout() {
            /* This BorderLayout subclass maps a null constraint to CENTER.
             * Although the reference BorderLayout also does this, some VMs
             * throw an IllegalArgumentException.
             */
            public void addLayoutComponent(Component comp, Object constraints) {
                if (constraints == null) {
                    constraints = BorderLayout.CENTER;
                }
                super.addLayoutComponent(comp, constraints);
            }
        });

        setContentPane(contentPane);
    }
}
