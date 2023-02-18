package com.leo.cse.frontend.ui.layout;

import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;
import com.leo.cse.util.ObjectPool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

public abstract class ComponentsLayoutManager implements LayoutManager2 {
    private int measuredWidth;
    private int measuredHeight;

    private final Map<Component, LayoutConstraints> constraints = new HashMap<>();
    private final Map<Component, Dimension> childrenDimensions = new HashMap<>();

    private boolean isLayoutInvalid = true;

    private final static Dimension MIN_SIZE = new Dimension(0, 0);
    private final static Dimension MAX_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final static Insets EMPTY_INSETS = new Insets(0,0,0,0);
    private final static LayoutConstraints EMPTY_CONSTRAINTS = new LayoutConstraints();

    private final ObjectPool<Dimension> dimensionsPool = new ObjectPool<>(() -> new Dimension(MIN_SIZE));

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        final LayoutConstraints lc = (constraints instanceof LayoutConstraints)
                ? (LayoutConstraints) constraints
                : new LayoutConstraints();

        this.constraints.put(comp, lc);
        invalidateLayout(comp.getParent());
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        constraints.put(comp, new LayoutConstraints());
        invalidateLayout(comp.getParent());
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        constraints.remove(comp);
        invalidateLayout(comp.getParent());
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        return new Dimension(MIN_SIZE);
    }

    @Override
    public Dimension maximumLayoutSize(Container container) {
        return new Dimension(MAX_SIZE);
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        Dimension size;
        synchronized(this) {
            measureContainer(container);
            size = new Dimension(measuredWidth, measuredHeight);
        }
        return size;
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return Container.LEFT_ALIGNMENT;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return Container.TOP_ALIGNMENT;
    }

    @Override
    public void invalidateLayout(Container container) {
        measuredWidth = 0;
        measuredHeight = 0;

        synchronized (this) {
            returnChildrenDimensionsToPool();
            int i = 0;
            while (i < container.getComponentCount()) {
                childrenDimensions.put(container.getComponent(i++), borrowDimensionFromPool());
            }
        }

        isLayoutInvalid = true;
    }

    private void returnChildrenDimensionsToPool() {
        for (Dimension dimension : childrenDimensions.values()) {
            dimensionsPool.returnObject(dimension);
        }
        childrenDimensions.clear();
    }

    private Dimension borrowDimensionFromPool() {
        try {
            return dimensionsPool.borrowObject();
        } catch (Exception e) {
            return new Dimension(MIN_SIZE);
        }
    }

    public Dimension getChildDimension(Component child) {
        return childrenDimensions.get(child);
    }

    public LayoutConstraints getChildConstraints(Component child) {
        return constraints.getOrDefault(child, EMPTY_CONSTRAINTS);
    }

    private Dimension setChildDimension(Component child, int width, int height) {
        final Dimension size = childrenDimensions.get(child);
        if (size != null) {
            size.width = width;
            size.height = height;
        }
        return size;
    }

    private void measureContainer(Container container) {
        if (isLayoutInvalid) {
            final Container parent = container.getParent();

            final Dimension prefSize = container.isPreferredSizeSet()
                    ? container.getPreferredSize()
                    : container.isMaximumSizeSet() ? container.getMaximumSize() : MAX_SIZE;

            if (parent == null || (parent.getWidth() == 0 && parent.getHeight() == 0)) {
                onMeasure(container, prefSize.width, prefSize.height);
            } else if (parent.getWidth() != 0 && parent.getHeight() != 0 && container.getWidth() != 0 && container.getHeight() != 0) {
                onMeasure(container, container.getWidth(), container.getHeight());
            } else {
                final Insets insets = (parent instanceof JComponent) ? parent.getInsets() : EMPTY_INSETS;
                final int parentWidth = parent.getWidth() - (insets.left + insets.right);
                final int parentHeight = parent.getHeight() - (insets.top + insets.bottom);

                final Dimension minSize = container.isMinimumSizeSet() ? container.getMinimumSize() : MIN_SIZE;
                final Dimension maxSize = container.isMaximumSizeSet() ? container.getMaximumSize() : MAX_SIZE;

                final int w = prefSize.width <= parentWidth
                        ? Math.max(prefSize.width, Math.min(minSize.width, parentWidth))
                        : Math.max(Math.min(minSize.width, parentWidth), Math.min(maxSize.width, parentWidth));

                final int h = prefSize.height <= parentHeight
                        ? Math.max(prefSize.height, Math.min(minSize.height, parentHeight))
                        : Math.max(Math.min(minSize.height, parentHeight), Math.min(maxSize.height, parentHeight));

                onMeasure(container, w, h);
            }
        }
    }

    protected Dimension measureChild(Component child, int availWidth, int availHeight) {
        final boolean isMinimumSizeSet = child.isMinimumSizeSet();
        final boolean isMaximumSizeSet = child.isMaximumSizeSet();
        final Dimension minSize = isMinimumSizeSet ? child.getMinimumSize() : MIN_SIZE;
        final Dimension maxSize = isMaximumSizeSet ? child.getMaximumSize() : MAX_SIZE;

        // Dirty hack to constrain child width
        child.setMinimumSize(new Dimension(Math.min(minSize.width, availWidth), Math.min(minSize.height, availHeight)));
        child.setMaximumSize(new Dimension(Math.min(maxSize.width, availWidth), Math.min(maxSize.height, availHeight)));

        final Dimension prefSize = child.getPreferredSize(); // triggers nested measuring

        // Restore min and max sizes
        child.setMinimumSize(isMinimumSizeSet ? minSize : null);
        child.setMaximumSize(isMaximumSizeSet ? maxSize : null);

        final int prefWidth = prefSize.width;
        final int prefHeight = prefSize.height;

        final int w = prefWidth <= availWidth
                ? Math.max(prefWidth, Math.min(minSize.width, availWidth))
                : Math.max(Math.min(minSize.width, availWidth), Math.min(maxSize.width, availWidth));

        final int h = prefHeight <= availHeight
                ? Math.max(prefHeight, Math.min(minSize.height, availHeight))
                : Math.max(Math.min(minSize.height, availHeight), Math.min(maxSize.height, availHeight));

        return setChildDimension(child, w, h);
    }

    protected void onMeasure(Container container, int maxWidth, int maxHeight) {
        setMeasuredDimensions(maxWidth, maxHeight);
    }

    protected final void setMeasuredDimensions(int width, int height) {
        measuredWidth = width;
        measuredHeight = height;
        isLayoutInvalid = false;
    }

    @Override
    public final void layoutContainer(Container container) {
        synchronized (this) {
            measureContainer(container);
        }
        if (isLayoutInvalid) {
            throw new IllegalStateException("Layout is not measured");
        }
        onLayout(container);
    }

    protected abstract void onLayout(Container container);

}
