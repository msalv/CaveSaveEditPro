package com.leo.cse.frontend.ui.components;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.EntityExtras;
import com.leo.cse.util.GraphicsHelper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Objects;

import javax.swing.JComponent;

public class MapComponent extends JComponent {
    private static final int DRAW_TYPE_NORMAL = 0;
    private static final int DRAW_TYPE_HOVER = 1;

    private static final Color BG_COLOR = new Color(0x000010);

    private final GameResourcesManager resourcesManager;

    private int camX;
    private int camY;

    private short playerHoverX;
    private short playerHoverY;

    private boolean isHovered;
    private boolean isDragging;

    private boolean isGridVisible = false;
    private boolean drawCharacterAboveForeground = false;

    private OnCameraChangeListener cameraChangeListener;
    private OnPlayerPositionChangedListener playerPositionChangeListener;

    private CharacterImageProvider characterImageProvider;
    private FlagValueProvider flagValueProvider;
    private EntityExtrasProvider entityExtrasProvider;

    private int currentMapId = -1;
    private final Short[] playerPosition = new Short[2];
    private int resolution = 1;
    private int slotId = 0;
    private boolean isDirectionRightToLeft = false;

    private final Rectangle tempRect = new Rectangle();

    public MapComponent(GameResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;

        final MouseEventsListener mouseEventsListener = new MouseEventsListener();
        addMouseListener(mouseEventsListener);
        addMouseMotionListener(mouseEventsListener);
        addKeyListener(new KeyEventListener());
        setFocusable(true);
    }

    public int getCameraX() {
        return camX;
    }

    public int getCameraY() {
        return camY;
    }

    public void setCurrentMapId(int mapId) {
        if (currentMapId != mapId) {
            this.currentMapId = mapId;
            repaint();
        }
    }

    public void setPlayerPosition(Short[] playerPosition) {
        if (!Objects.equals(this.playerPosition[0], playerPosition[0]) || !Objects.equals(this.playerPosition[1], playerPosition[1])) {
            this.playerPosition[0] = playerPosition[0];
            this.playerPosition[1] = playerPosition[1];
            repaint();
        }
    }

    public void setResolution(int resolution) {
        if (this.resolution != resolution) {
            this.resolution = resolution;
            repaint();
        }
    }

    public void setSlotId(int slotId) {
        if (this.slotId != slotId) {
            this.slotId = slotId;
            repaint();
        }
    }

    public void setDirection(boolean isDirectionRightToLeft) {
        if (this.isDirectionRightToLeft != isDirectionRightToLeft) {
            this.isDirectionRightToLeft = isDirectionRightToLeft;
            repaint();
        }
    }

    public void setGridVisible(boolean isVisible) {
        if (this.isGridVisible != isVisible) {
            this.isGridVisible = isVisible;
            repaint();
        }
    }

    public void setDrawCharacterAboveForeground(boolean above) {
        if (this.drawCharacterAboveForeground != above) {
            this.drawCharacterAboveForeground = above;
            repaint();
        }
    }

    public void setOnCameraChangeListener(OnCameraChangeListener listener) {
        this.cameraChangeListener = listener;
    }

    public void setOnPlayerPositionChangedListener(OnPlayerPositionChangedListener listener) {
        this.playerPositionChangeListener = listener;
    }

    public void setCharacterImageProvider(CharacterImageProvider imageProvider) {
        this.characterImageProvider = imageProvider;
    }

    public void setFlagValueProvider(FlagValueProvider flagValueProvider) {
        this.flagValueProvider = flagValueProvider;
    }

    public void setEntityExtrasProvider(EntityExtrasProvider entityExtrasProvider) {
        this.entityExtrasProvider = entityExtrasProvider;
    }

    private void validateCameraPosition(int[][][] map, Short[] playerPos) {
        if (map != null && map.length > 0 && playerPos != null && playerPos.length > 0) {
            final int camX = Math.max(0, Math.min((map[0][0].length - 21) * 32, playerPos[0] - getWidth() / 2));
            final int camY = Math.max(0, Math.min((map[0].length - 16) * 32, playerPos[1] - getHeight() / 2));
            if (this.camX != camX || this.camY != camY) {
                this.camX = camX;
                this.camY = camY;
                if (cameraChangeListener != null) {
                    cameraChangeListener.onCameraPositionChanged(camX, camY);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!resourcesManager.hasResources()) {
            paintPlaceholder(g);
            return;
        }

        final GameResources resources = resourcesManager.getResources();
        final MapInfo mapInfo = resources.getMapInfo(currentMapId);

        if (mapInfo == null || MapInfo.hasMissingAssets(mapInfo, resources, resourcesManager.shouldLoadNpc())) {
            paintMissingAssetsMessage(g, resources, mapInfo, resourcesManager.shouldLoadNpc());
            return;
        }

        final int[][][] map = mapInfo.getMap();
        final Short[] playerPos = playerPosition;
        validateCameraPosition(map, playerPos);

        paintMap(g, mapInfo, map, playerPos);

        if (hasFocus()) {
            paintFocusedBorder(g);
        }
    }

    private void paintPlaceholder(Graphics g) {
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
//        g.setColor(Color.white);
//        g.setFont(Resources.getFontPixelLarge());
//        GraphicsHelper.drawTextCentered(g, "NO MOD LOADED!", getWidth() / 2, getHeight() / 2);
    }

    private void paintFocusedBorder(Graphics g) {
        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private void paintMissingAssetsMessage(Graphics g, GameResources resources, MapInfo mapInfo, boolean shouldLoadNpc) {
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.white);
        g.setFont(Resources.getFontPixelLarge());
        GraphicsHelper.drawTextCentered(g, "MISSING ASSETS!", getWidth() / 2, getHeight() / 2);
        g.setFont(Resources.getFont());

        if (mapInfo != null) {
            GraphicsHelper.drawTextCentered(g,
                    "The following assets failed to load:\n" + MapInfo.getMissingAssets(mapInfo, resources, shouldLoadNpc),
                    getWidth() / 2, getHeight() / 2 + 50);
        }
    }

    private void paintMap(Graphics g, MapInfo mapInfo, int[][][] map, Short[] playerPos) {
        final GameResources resources = resourcesManager.getResources();

        final BufferedImage surf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D sg = (Graphics2D) surf.getGraphics();
        sg.setColor(BG_COLOR);
        sg.fillRect(0, 0, getWidth(), getHeight());

        drawMapBackground(sg, resources, mapInfo, map);
        sg.translate(-camX, -camY);

        final BufferedImage tileset = resources.getImage(mapInfo.getTilesetFile());
        drawMapTiles(sg, resources, mapInfo, map, tileset, MapInfo.LAYER_BG);

        if (!drawCharacterAboveForeground) {
            drawCharacter(sg, playerPos);
        }

        drawMapEntities(sg, resources, mapInfo);
        drawMapTiles(sg, resources, mapInfo, map, tileset, MapInfo.LAYER_FG);

        if (drawCharacterAboveForeground) {
            drawCharacter(sg, playerPos);
        }

        if (isHovered && !isDragging) {
            drawCharacter(sg, DRAW_TYPE_HOVER, playerHoverX, playerHoverY);
        }

        if (isGridVisible) {
            drawGrid(sg, map);
        }

        sg.translate(camX, camY);

        g.drawImage(surf, 0, 0, null);
    }

    private void drawMapBackground(Graphics g, GameResources resources, MapInfo mapInfo, int[][][] map) {
        final int scrollType = mapInfo.getScrollType();
        if (scrollType == 3 || scrollType == 4) {
            return;
        }

        final BufferedImage bg = resources.getImage(mapInfo.getBgImageFile());

        final int iw = bg.getWidth(null);
        final int ih = bg.getHeight(null);

        for (int x = 0; x < map[0][0].length * 32; x += iw) {
            for (int y = 0; y < map[0].length * 32; y += ih) {
                g.drawImage(bg, x, y, iw, ih, null);
            }
        }
    }

    private void drawMapTiles(Graphics g, GameResources resources, MapInfo mapInfo, int[][][] map, BufferedImage tileset, int line) {
        final int tilesetWidth = tileset.getWidth() / 32;

        for (int yy = 0; yy < map[line].length; yy++) {
            for (int xx = 0; xx < map[line][yy].length; xx++) {
                final int xPixel = xx * 32 - 16;
                final int yPixel = yy * 32 - 16;

                final int tile = map[line][yy][xx];
                final int pxa = mapInfo.calcPxa(tile, resources);
                if (pxa >= 0x20 && pxa <= 0x3F || pxa >= 0x81 && pxa <= 0xFF) {
                    // no draw
                    continue;
                }

                // draw normal tile
                int sourceX = (tile % tilesetWidth) * 32;
                int sourceY = (tile / tilesetWidth) * 32;
                g.drawImage(tileset,
                        xPixel,
                        yPixel,
                        xPixel + 32,
                        yPixel + 32,
                        sourceX,
                        sourceY,
                        sourceX + 32,
                        sourceY + 32,
                        null);

                if (pxa == 0x43) {
                    // draw breakable tile on top of normal tile
                    g.drawImage(resources.getImage(resources.getNpcSymFile()),
                            xPixel,
                            yPixel,
                            xPixel + 32,
                            yPixel + 32,
                            512,
                            96,
                            544,
                            128,
                            null);
                }
            }
        }
    }

    private void drawMapEntities(Graphics2D g, GameResources resources, MapInfo mapInfo) {
        if (flagValueProvider == null) {
            return;
        }

        final Iterator<MapInfo.PxeEntry> iterator = mapInfo.getPxeIterator();
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            final MapInfo.PxeEntry e = iterator.next();
            final short flags = e.getFlags();
            final short flagID = e.getFlagID();
            if ((flags & 0x0800) != 0) {
                // Appear once flagID set
                if (!flagValueProvider.isFlagSet(flagID)) {
                    return;
                }
            }
            if ((flags & 0x4000) != 0) {
                // No Appear if flagID set
                if (flagValueProvider.isFlagSet(flagID)) {
                    return;
                }
            }
            final BufferedImage srcImg;
            final int tilesetNum = e.getInfo().getTileset();
            if (tilesetNum == 0) { // title
                srcImg = resources.getImage(resources.getTitleFile());
            } else if (tilesetNum == 1) { // PIXEL
                srcImg = null; // skip
            } else if (tilesetNum == 6) { // fade
                srcImg = resources.getImage(resources.getFadeFile());
            } else if (tilesetNum == 2) { // map tileset
                srcImg = resources.getImage(mapInfo.getTilesetFile());
            } else if (tilesetNum == 8) { // itemimage
                srcImg = resources.getImage(resources.getItemImageFile());
            } else if (tilesetNum == 11) { // arms
                srcImg = resources.getImage(resources.getArmsFile());
            } else if (tilesetNum == 12) { // armsimage
                srcImg = resources.getImage(resources.getArmsImageFile());
            } else if (tilesetNum == 14) { // stageimage
                srcImg = resources.getImage(resources.getStageImageFile());
            } else if (tilesetNum == 15) { // loading
                srcImg = resources.getImage(resources.getLoadingFile());
            } else if (tilesetNum == 16) { // npc myChar
                srcImg = resources.getImage(resources.getMyCharFile());
            } else if (tilesetNum == 17) { // bullet
                srcImg = resources.getImage(resources.getBulletFile());
            } else if (tilesetNum == 19) { // caret
                srcImg = resources.getImage(resources.getCaretFile());
            } else if (tilesetNum == 20) { // npc sym
                srcImg = resources.getImage(resources.getNpcSymFile());
            } else if (tilesetNum == 21) { // map npc sheet 1
                srcImg = resources.getImage(mapInfo.getNpcSheet1());
            } else if (tilesetNum == 22) { // map npc sheet 2
                srcImg = resources.getImage(mapInfo.getNpcSheet2());
            } else if (tilesetNum == 23) { // npc regu
                srcImg = resources.getImage(resources.getNpcReguFile());
            } else if (tilesetNum == 26) { // textbox
                srcImg = resources.getImage(resources.getTextBoxFile());
            } else if (tilesetNum == 27) { // face
                srcImg = resources.getImage(resources.getFaceFile());
            } else if (tilesetNum == 28) { // map background
                srcImg = resources.getImage(mapInfo.getBgImageFile());
            } else {
                srcImg = null;
            }

            if (entityExtrasProvider == null) {
                return;
            }

            final EntityExtras ee = entityExtrasProvider.getEntityExtras(e);
            if (ee == null) {
                continue;
            }
            final Rectangle frameRect = ee.getFrameRect();
            if (frameRect.x < 0 || frameRect.y < 0 || frameRect.width < 0 || frameRect.height < 0) {
                continue;
            }
            if (frameRect.x == 0 && frameRect.y == 0 && frameRect.width == 0 && frameRect.height == 0) {
                continue;
            }
            final Point offset = ee.getOffset();
            if (srcImg != null) {
                int srcX = frameRect.x;
                int srcY = frameRect.y;
                int srcX2 = frameRect.width;
                int srcY2 = frameRect.height;
                final Rectangle dest = e.getDrawArea(tempRect);
                int dstX = dest.x + offset.x;
                int dstY = dest.y + offset.y;
                g.drawImage(srcImg, dstX, dstY, dstX + dest.width, dstY + dest.height, srcX, srcY, srcX2, srcY2, null);
            }
        }
    }

    private void drawCharacter(Graphics2D sg, Short[] position) {
        final short x = isDragging ? playerHoverX : position[0];
        final short y = isDragging ? playerHoverY : position[1];
        drawCharacter(sg, DRAW_TYPE_NORMAL, x, y);
    }

    private void drawCharacter(Graphics2D g, int drawType, short playerX, short playerY) {
        if (characterImageProvider == null) {
            return;
        }

        final Image image = characterImageProvider.getCharacterImage(resourcesManager, slotId, isDirectionRightToLeft);
        if (image == null) {
            return;
        }

        float snap = Math.max(1f, 2f / resolution);

        int xPixel = playerX - 16;
        xPixel -= (xPixel % snap);

        int yPixel = playerY - 16;
        yPixel -= (yPixel % snap);

        final Composite originalComposite = g.getComposite();

        final float alpha = drawType == DRAW_TYPE_HOVER ? 0.25f : 1f;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g.drawImage(image, xPixel, yPixel, null);

        g.setComposite(originalComposite);
    }

    private void drawGrid(Graphics2D g, int[][][] map) {
        g.setColor(ThemeData.getForegroundColor());

        final Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        final int width = map[0][0].length * 32;
        final int height = map[0].length * 32;

        for (int i = 0; i < map[0].length; i++) {
            g.drawLine(0, i * 32 + 16, width, i * 32 + 16);
        }

        for (int j = 0; j < map[0][0].length; j++) {
            g.drawLine(j * 32 + 16, 0, j * 32 + 16, height);
        }

        g.setComposite(originalComposite);
    }

    public interface OnCameraChangeListener {
        void onCameraPositionChanged(int x, int y);
    }

    public interface OnPlayerPositionChangedListener {
        void onPlayerPositionChanged(short x, short y);
    }

    public interface CharacterImageProvider {
        Image getCharacterImage(GameResourcesManager resourcesManager, int slotId, boolean isDirectionRightToLeft);
    }

    public interface FlagValueProvider {
        boolean isFlagSet(int flagId);
    }

    public interface EntityExtrasProvider {
        EntityExtras getEntityExtras(MapInfo.PxeEntry entry);
    }

    private class MouseEventsListener extends MouseAdapter {
        private final Short[] tempPoint = new Short[2];

        private void toMapPoint(MouseEvent e, Short[] point) {
            short x = (short) (e.getX() + camX);
            short y = (short) (e.getY() + camY);
            if (e.isShiftDown()) {
                x = (short) (((x + 16) / 32) * 32);
                y = (short) (((y + 16) / 32) * 32);
            }
            point[0] = x;
            point[1] = y;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!hasFocus()) {
                requestFocusInWindow();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isDragging = false;

            if (!hasFocus()) {
                return;
            }

            if (!resourcesManager.hasResources()) {
                return;
            }

            final GameResources resources = resourcesManager.getResources();
            final int[][][] map = resources.getMap(currentMapId, resourcesManager.shouldLoadNpc());

            if (map == null) {
                return;
            }

            validateCameraPosition(map, playerPosition);

            toMapPoint(e, tempPoint);

            if (playerPositionChangeListener != null) {
                playerPositionChangeListener.onPlayerPositionChanged(tempPoint[0], tempPoint[1]);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            toMapPoint(e, tempPoint);

            if (playerHoverX != tempPoint[0] || playerHoverY != tempPoint[1]) {
                playerHoverX = tempPoint[0];
                playerHoverY = tempPoint[1];
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isDragging && hasFocus()) {
                isDragging = true;
            }
            mouseMoved(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (!isHovered) {
                isHovered = true;
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (isHovered) {
                isHovered = false;
                repaint();
            }

            if (hasFocus()) {
                try {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                } catch (Exception ex) {
                    AppLogger.error("Unable to clear focus", ex);
                }
                repaint();
            }
        }
    }

    private class KeyEventListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (!resourcesManager.hasResources()) {
                return;
            }

            final GameResources resources = resourcesManager.getResources();

            final int[][][] map = resources.getMap(currentMapId, resourcesManager.shouldLoadNpc());
            if (map == null) {
                return;
            }

            final int code = e.getKeyCode();
            final int mods = e.getModifiersEx();
            final boolean shiftDown = (mods & InputEvent.SHIFT_DOWN_MASK) != 0;
            final boolean ctrlDown = (mods & InputEvent.CTRL_DOWN_MASK) != 0;

            final Short[] playerPos = playerPosition;

            validateCameraPosition(map, playerPos);

            int px = playerPos[0];
            int py = playerPos[1];

            int amount = 32;

            if (shiftDown) {
                if (ctrlDown) {
                    amount = 2;
                } else {
                    amount = 16;
                }
            } else if (ctrlDown) {
                amount = 8;
            }

            if (code == KeyEvent.VK_UP) {
                py -= amount;
            } else if (code == KeyEvent.VK_DOWN) {
                py += amount;
            } else if (code == KeyEvent.VK_LEFT) {
                px -= amount;
            } else if (code == KeyEvent.VK_RIGHT) {
                px += amount;
            } else {
                return;
            }

            px = Math.max(0, Math.min(map[0][0].length * 32, px));
            py = Math.max(0, Math.min(map[0].length * 32, py));

            final Short[] pos = new Short[] { (short)px, (short)py };

            validateCameraPosition(map, pos);

            if (playerPositionChangeListener != null) {
                playerPositionChangeListener.onPlayerPositionChanged(pos[0], pos[1]);
            }
        }
    }
}
