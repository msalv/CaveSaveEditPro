package com.leo.cse.backend.res;

import com.leo.cse.util.FileUtils;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.log.AppLogger;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class GameResources {
    /**
     * The currently loaded game/mod's starting point.
     */
    private final StartPoint startPoint;

    /**
     * Array of strings loaded from the executable.
     *
     * @see ExePointers#STRING_POINTERS
     */
    private final Map<Integer, String> exeStrings;

    /**
     * List of map data.
     *
     * @see Mapdata
     */
    private final List<Mapdata> mapdata;

    /**
     * List of map information.
     *
     * @see MapInfo
     */
    private final List<MapInfo> mapInfo;

    /**
     * Image repository.
     */
    private final Map<File, BufferedImage> imageMap;

    /**
     * Item images cache
     */
    private final Map<Integer, BufferedImage> itemImagesCache = new HashMap<>();

    /**
     * Weapon images cache
     */
    private final Map<String, BufferedImage> weaponImagesCache = new HashMap<>();

    /**
     * Warp Slot images caches
     */
    private final Map<Integer, BufferedImage> warpSlotImagesCache = new HashMap<>();

    /**
     * Character images cache
     */
    private final Map<Rectangle, BufferedImage> characterImagesCache = new HashMap<>();

    /**
     * NPC regu images cache
     */
    private final Map<Rectangle, BufferedImage> npcReguImagesCache = new HashMap<>();

    /**
     * HUD images cache
     */
    private final Map<Rectangle, BufferedImage> hudImagesCache = new HashMap<>();

    /**
     * PXA file repository.
     */
    private final Map<File, byte[]> pxaMap;

    /**
     * "Title" graphics file.
     */
    private final File title;

    /**
     * "MyChar" graphics file.
     */
    private final File myChar;

    /**
     * "ArmsImage" graphics file.
     */
    private final File armsImage;

    /**
     * "Arms" graphics file.
     */
    private final File arms;

    /**
     * "ItemImage" graphics file.
     */
    private final File itemImage;

    /**
     * "StageImage" graphics file.
     */
    private final File stageImage;
    /**
     * "NpcSym" graphics file.
     */
    private final File npcSym;

    /**
     * "NpcRegu" graphics file.
     */
    private final File npcRegu;

    /**
     * "TextBox" graphics file.
     */
    private final File textBox;

    /**
     * "Caret" graphics file.
     */
    private final File caret;

    /**
     * "Bullet" graphics file.
     */
    private final File bullet;

    /**
     * "Face" graphics file.
     */
    private final File face;

    /**
     * "Fade" graphics file.
     */
    private final File fade;

    /**
     * "Loading" graphics file.
     */
    private final File loading;

    public GameResources(StartPoint startPoint, Map<Integer, String> exeStrings, List<Mapdata> mapdata, List<MapInfo> mapInfo, Map<File, BufferedImage> imageMap, Map<File, byte[]> pxaMap, File title, File myChar, File armsImage, File arms, File itemImage, File stageImage, File npcSym, File npcRegu, File textBox, File caret, File bullet, File face, File fade, File loading) {
        this.startPoint = startPoint;
        this.exeStrings = exeStrings;
        this.mapdata = mapdata;
        this.mapInfo = mapInfo;
        this.imageMap = imageMap;
        this.pxaMap = pxaMap;
        this.title = title;
        this.myChar = myChar;
        this.armsImage = armsImage;
        this.arms = arms;
        this.itemImage = itemImage;
        this.stageImage = stageImage;
        this.npcSym = npcSym;
        this.npcRegu = npcRegu;
        this.textBox = textBox;
        this.caret = caret;
        this.bullet = bullet;
        this.face = face;
        this.fade = fade;
        this.loading = loading;
    }

    public String getExeString(int pointer) {
        return exeStrings.getOrDefault(pointer, "");
    }

    public String getHeader() {
        return getExeString(ExePointers.PROFILE_HEADER_PTR);
    }

    public String getFlagsHeader() {
        return getExeString(ExePointers.PROFILE_FLAGH_PTR);
    }

    public MapInfo getMapInfo(int num) {
        if (num < 0) {
            throw new IndexOutOfBoundsException(String.format("Requested map number (%d) is negative!", num));
        }
        if (num >= mapInfo.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Requested map number is %d, but maximum map number is %d!", num, mapdata.size()));
        }
        return mapInfo.get(num);
    }

    public int[][][] getMap(int mapId, boolean shouldLoadNpc) {
        final MapInfo mapInfo = getMapInfo(mapId);

        if (mapInfo == null || MapInfo.hasMissingAssets(mapInfo, this, shouldLoadNpc)) {
            return null;
        }

        return mapInfo.getMap();
    }

    public int getMapInfoCount() {
        return mapInfo.size();
    }

    public File getTitleFile() {
        return title;
    }

    public File getMyCharFile() {
        return myChar;
    }

    public File getArmsImageFile() {
        return armsImage;
    }

    public File getArmsFile() {
        return arms;
    }

    public File getItemImageFile() {
        return itemImage;
    }

    public File getStageImageFile() {
        return stageImage;
    }

    public File getNpcSymFile() {
        return npcSym;
    }

    public File getNpcReguFile() {
        return npcRegu;
    }

    public File getTextBoxFile() {
        return textBox;
    }

    public File getCaretFile() {
        return caret;
    }

    public File getBulletFile() {
        return bullet;
    }

    public File getFaceFile() {
        return face;
    }

    public File getFadeFile() {
        return fade;
    }

    public File getLoadingFile() {
        return loading;
    }

    public byte[] getPxa(File srcFile) {
        return pxaMap.get(FileUtils.newFile(srcFile.getAbsolutePath()));
    }

    public BufferedImage getImage(File key) {
        if (key == null) {
            return null;
        }
        if (key.exists()) {
            key = FileUtils.newFile(key.getAbsolutePath());
        }
        if (imageMap.containsKey(key)) {
            return imageMap.get(key);
        }
        AppLogger.error(String.format("Key not found for getImage: %s", key));
        return null;
    }

    public BufferedImage getWeaponImage(int i, int size, int y) {
        final String key = String.format("%d:%d:%d", i, size, y);
        final BufferedImage cached = weaponImagesCache.get(key);
        if (cached != null) {
            return cached;
        }

        final BufferedImage image = getImage(getArmsImageFile())
                .getSubimage(i * size, y, size, size);

        weaponImagesCache.put(key, image);

        return image;
    }

    public BufferedImage getInventoryItemImage(int i) {
        final BufferedImage cached = itemImagesCache.get(i);
        if (cached != null) {
            return cached;
        }

        final int sourceX = (i % 8) * 64;
        final int sourceY = (i / 8) * 32;

        final BufferedImage image = getImage(getItemImageFile())
                .getSubimage(sourceX, sourceY, 64, 32);

        itemImagesCache.put(i, image);

        return image;
    }

    public BufferedImage getWarpSlotImage(int i) {
        final BufferedImage cached = warpSlotImagesCache.get(i);
        if (cached != null) {
            return cached;
        }

        final BufferedImage image = getImage(getStageImageFile())
                .getSubimage(64 * i, 0, 64, 32);

        warpSlotImagesCache.put(i, image);

        return image;
    }

    public BufferedImage getCharacterImage(Rectangle bounds) {
        final BufferedImage cached = characterImagesCache.get(bounds);
        if (cached != null) {
            return cached;
        }

        final BufferedImage image = getImage(getMyCharFile())
                .getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);

        characterImagesCache.put(bounds, image);

        return image;
    }

    public BufferedImage getNpcReguImage(Rectangle bounds) {
        final BufferedImage cached = npcReguImagesCache.get(bounds);
        if (cached != null) {
            return cached;
        }

        final BufferedImage image = getImage(getNpcReguFile())
                .getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);

        npcReguImagesCache.put(bounds, image);

        return image;
    }

    public StartPoint getStartPoint() {
        return startPoint;
    }

    private BufferedImage getHUDImage(Rectangle bounds) {
        final BufferedImage cached = hudImagesCache.get(bounds);
        if (cached != null) {
            return cached;
        }

        final BufferedImage image = getImage(getTextBoxFile())
                .getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);

        hudImagesCache.put(bounds, image);

        return image;
    }

    public BufferedImage getHealthBarImage() {
        return getHUDImage(new Rectangle(0, 80, 128, 16));
    }

    public BufferedImage getHealthBarFillImage() {
        return getHUDImage(new Rectangle(0, 50, 78, 10));
    }

    public BufferedImage getNumberImage(int i) {
        if (i < 0 || i > 9) {
            return null;
        }
        return getHUDImage(new Rectangle(16 * i, 112, 16, 16));
    }
}
