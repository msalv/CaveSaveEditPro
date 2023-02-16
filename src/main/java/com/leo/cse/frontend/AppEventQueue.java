package com.leo.cse.frontend;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;

/**
 * This event queue skips some drag events
 * replacing them with move events
 * and then dispatches click event instead
 * making mouse events less sensitive to drags
 */
public class AppEventQueue extends EventQueue {
    private final static int DRAG_SKIP_DISTANCE = 32;

    private MouseEvent prevMouseEvent;
    private boolean dragSkipped;

    @Override
    protected void dispatchEvent(AWTEvent event) {
        if (shouldSkipDrag(event)) {
            dispatchFakeMove(event);
            return;
        }

        super.dispatchEvent(event);

        if (event instanceof MouseEvent) {
            final MouseEvent e = (MouseEvent) event;
            postProcessMouseEvent(e);
            prevMouseEvent = e;
        }
    }

    private boolean shouldSkipDrag(AWTEvent event) {
        if (prevMouseEvent == null || prevMouseEvent.getID() != MouseEvent.MOUSE_PRESSED) {
            return false;
        }

        if (event instanceof MouseEvent) {
            final MouseEvent e = (MouseEvent) event;

            if (e.getComponent() instanceof DraggableComponent) {
                dragSkipped = false;
                return false;
            }

            final int id = e.getID();
            if (prevMouseEvent.getSource() == e.getSource() && id == MouseEvent.MOUSE_DRAGGED) {
                final int distance = (int)Math.sqrt(Math.pow(e.getX() - prevMouseEvent.getX(), 2) + Math.pow(e.getY() - prevMouseEvent.getY(), 2));
                dragSkipped = (distance < DRAG_SKIP_DISTANCE);
                if (dragSkipped) {
                    e.consume();
                }
                return dragSkipped;
            }
        }

        return false;
    }

    private void dispatchFakeMove(AWTEvent event) {
        final MouseEvent e = (event instanceof MouseEvent) ? (MouseEvent) event : null;
        if (e == null) {
            return;
        }

        final Component component = e.getComponent();
        if (component == null) {
            return;
        }

        super.dispatchEvent(new MouseEvent(
                component,
                MouseEvent.MOUSE_MOVED,
                e.getWhen(),
                e.getModifiers(),
                e.getX(),
                e.getY(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton()
        ));
    }

    private void postProcessMouseEvent(MouseEvent e) {
        if (dragSkipped) {
            final boolean isSameSource = (prevMouseEvent != null && prevMouseEvent.getSource() == e.getSource());
            if (isSameSource &&
                    prevMouseEvent.getID() == MouseEvent.MOUSE_PRESSED &&
                    e.getID() == MouseEvent.MOUSE_RELEASED) {
                final Component component = e.getComponent();
                if (component == null) {
                    return;
                }
                super.dispatchEvent(new MouseEvent(
                        component,
                        MouseEvent.MOUSE_CLICKED,
                        e.getWhen(),
                        e.getModifiers(),
                        prevMouseEvent.getX(),
                        prevMouseEvent.getY(),
                        e.getClickCount(),
                        e.isPopupTrigger(),
                        e.getButton()
                ));
            }
            dragSkipped = (isSameSource && e.getID() == MouseEvent.MOUSE_DRAGGED);
        }
    }

    public interface DraggableComponent {

    }
}
