package com.leo.cse.frontend.ui.components;

import com.leo.cse.frontend.ui.layout.StackLayout;

import java.awt.Component;

public class Pager extends StackLayout {

    public void setPage(Component page) {
        removeAll();
        add(page);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (getComponentCount() > 0) {
            throw new IllegalStateException("This container can have only one direct child");
        }
        super.addImpl(comp, constraints, index);
    }
}
