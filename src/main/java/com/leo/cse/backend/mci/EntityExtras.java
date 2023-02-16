package com.leo.cse.backend.mci;

import java.awt.Point;
import java.awt.Rectangle;

public class EntityExtras {
    private final Rectangle frameRect;
    private final Point offset;

    public EntityExtras(Rectangle frameRect, Point offset) {
        this.frameRect = frameRect;
        this.offset = offset;
    }

    public Rectangle getFrameRect() {
        return frameRect;
    }

    public Point getOffset() {
        return offset;
    }
}
