package com.leo.cse.frontend.ui.layout;

import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;
import com.leo.cse.util.StringUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

abstract public class JContainer extends JComponent {
    private final transient AtomicBoolean invalidateRunnableScheduled = new AtomicBoolean(false);
    private final LayoutManager layoutManager = new LayoutManager();

    public JContainer() {
        super();
        setLayout(layoutManager);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isBackgroundSet()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Makes invalidate() method behave as revalidate()
     */
    @Override
    public void invalidate() {
        if (getParent() == null) {
            return;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            super.invalidate();
            RepaintManager.currentManager(this).addInvalidComponent(this);
        } else {
            if (invalidateRunnableScheduled.getAndSet(true)) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                invalidateRunnableScheduled.set(false);
                invalidate();
            });
        }
    }

    public void setMeasuredDimensions(int measuredWidth, int measuredHeight) {
        layoutManager.setMeasuredDimensions(measuredWidth, measuredHeight);
    }

    public Dimension measureChild(Component child, int availWidth, int availHeight) {
        return layoutManager.measureChild(child, Math.max(0, availWidth), Math.max(0, availHeight));
    }

    public Dimension getChildDimension(Component child) {
        return layoutManager.getChildDimension(child);
    }

    public LayoutConstraints getChildConstraints(Component child) {
        return layoutManager.getChildConstraints(child);
    }

    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        setMeasuredDimensions(maxWidth, maxHeight);
    }

    public abstract void onLayout(Container container);

    @SuppressWarnings("unchecked")
    public <T extends Component> T findByName(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }

        for (int i = 0; i < getComponentCount(); ++i) {
            final Component child = getComponent(i);
            if (name.equals(child.getName())) {
                return (T)child;
            }
        }

        return null;
    }

    private class LayoutManager extends ComponentsLayoutManager {
        @Override
        protected void onMeasure(Container container, int maxWidth, int maxHeight) {
            JContainer.this.onMeasure(container, maxWidth, maxHeight);
        }

        @Override
        protected void onLayout(Container container) {
            JContainer.this.onLayout(container);
        }
    }
}
