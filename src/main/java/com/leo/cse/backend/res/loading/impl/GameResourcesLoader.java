package com.leo.cse.backend.res.loading.impl;

import com.leo.cse.util.FileUtils;
import com.leo.cse.backend.exe.EntityData;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.backend.exe.ProgressStrings;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.util.ColorUtils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

abstract public class GameResourcesLoader {
    protected final byte[] data;
    protected final File sourceFile;
    protected File dataDirectory;
    protected final String encoding;

    private final boolean shouldLoadNpc;

    protected final Map<Integer, String> exeStrings = new HashMap<>();
    protected final List<EntityData> entityList = new ArrayList<>();
    protected final List<Mapdata> mapDataList = new ArrayList<>();
    protected final List<MapInfo> mapInfoList = new ArrayList<>();
    protected final Map<File, BufferedImage> imageMap = new HashMap<>();
    protected final Map<File, byte[]> pxaMap = new HashMap<>();

    // Graphics files
    protected File title;
    protected File myChar;
    protected File armsImage;
    protected File arms;
    protected File itemImage;
    protected File stageImage;
    protected File npcSym;
    protected File npcRegu;
    protected File textBox;
    protected File caret;
    protected File bullet;
    protected File face;
    protected File fade;
    protected File loading;

    protected StartPoint startPoint;

    protected Callback callback;

    /**
     * Gets a graphics file using the image extension loaded from the executable.
     *
     * @param directory
     *            file directory
     * @param name
     *            file name
     * @return graphics file
     */
    private static File getGraphicsFile(String directory, String name, String ext) {
        final String file = String.format(ext, directory, name);
        return new File(file);
    }

    public interface Callback {
        void onProgress(GameResourcesLoadingPayload payload);
    }

    public GameResourcesLoader(File sourceFile, byte[] data, boolean shouldLoadNpc, String encoding) {
        this.sourceFile = sourceFile;
        this.data = data;
        this.shouldLoadNpc = shouldLoadNpc;
        this.encoding = encoding;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public GameResources load() throws Exception {
        loadStrings();
        this.dataDirectory = getDataDirectory(sourceFile);

        loadNpcTbl();
        fillMapdata();

        loadGraphics();
        initStartPoint();
        loadMapInfo();

        return build();
    }

    public abstract int getGraphicsResolution();

    protected abstract void loadStrings();

    protected abstract void fillMapdata();

    protected abstract void initStartPoint();

    protected abstract File getDataDirectory(File base);

    protected File correctFile(File file) {
        return file;
    }

    protected abstract GameResources build();

    /**
     * Gets a string loaded from the executable.
     *
     * @param pointer pointer
     * @return string from executable
     */
    protected String getExeString(int pointer) {
        return exeStrings.getOrDefault(pointer, "");
    }

    /**
     * Sets an EXE string and notifies listeners.
     *
     * @param pointer pointer
     * @param s value
     */
    protected void setExeString(int pointer, String s) {
        exeStrings.put(pointer, s);
        callback.onProgress(new GameResourcesLoadingPayload("Reading game strings", null, exeStrings.size(), ExePointers.STRING_POINTERS.length));
    }

    /**
     * Loads the "npc.tbl" file.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void loadNpcTbl() throws IOException {
        File tblFile = correctFile(FileUtils.newFile(dataDirectory + File.separator + getExeString(ExePointers.NPC_TBL_PTR)));

        if (tblFile == null || !tblFile.exists()) {
            throw new IOException(String.format("Could not find \"%s\"!", tblFile));
        }

        final FileInputStream inputStream = new FileInputStream(tblFile);
        final int calculatedNpcs = (int) (tblFile.length() / 24);
        final FileChannel channel = inputStream.getChannel();

        // read flags section
        ByteBuffer dBuf = ByteBuffer.allocateDirect(2 * calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final short[] flagDat = new short[calculatedNpcs];
        for (int i = 0; i < flagDat.length; i++) {
            flagDat[i] = dBuf.getShort();
            callback.onProgress(new GameResourcesLoadingPayload("Reading game strings", null, i + 1, calculatedNpcs));
        }

        // read health section
        dBuf = ByteBuffer.allocate(2 * calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final short[] healthDat = new short[calculatedNpcs];
        for (int i = 0; i < healthDat.length; i++) {
            healthDat[i] = dBuf.getShort();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_HEALTH), null, i + 1, calculatedNpcs));
        }

        // read tileset section
        dBuf = ByteBuffer.allocate(calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final byte[] tilesetDat = new byte[calculatedNpcs];
        for (int i = 0; i < tilesetDat.length; i++) {
            tilesetDat[i] = dBuf.get();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_TILESET), null, i + 1, calculatedNpcs));
        }

        // read death sound section
        dBuf = ByteBuffer.allocate(calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();
        final byte[] deathDat = new byte[calculatedNpcs];
        for (int i = 0; i < deathDat.length; i++) {
            deathDat[i] = dBuf.get();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_DEATHSND), null, i + 1, calculatedNpcs));
        }

        // read hurt sound section
        dBuf = ByteBuffer.allocate(calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final byte[] hurtDat = new byte[calculatedNpcs];
        for (int i = 0; i < hurtDat.length; i++) {
            hurtDat[i] = dBuf.get();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_HURTSND), null, i + 1, calculatedNpcs));
        }

        // read size section
        dBuf = ByteBuffer.allocate(calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final byte[] sizeDat = new byte[calculatedNpcs];
        for (int i = 0; i < sizeDat.length; i++) {
            sizeDat[i] = dBuf.get();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_SIZE), null, i + 1, calculatedNpcs));
        }

        // read experience section
        dBuf = ByteBuffer.allocate(4 * calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final int[] expDat = new int[calculatedNpcs];
        for (int i = 0; i < expDat.length; i++) {
            expDat[i] = dBuf.getInt();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_EXP), null, i + 1, calculatedNpcs));
        }

        // read damage section
        dBuf = ByteBuffer.allocate(4 * calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final int[] damageDat = new int[calculatedNpcs];
        for (int i = 0; i < damageDat.length; i++) {
            damageDat[i] = dBuf.getInt();
            callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_DAMAGE), null, i + 1, calculatedNpcs));
        }

        int hitBoxCounter = 0;

        // read hitbox section
        dBuf = ByteBuffer.allocate(4 * calculatedNpcs);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(dBuf);
        dBuf.flip();

        final byte[] hitboxDat = new byte[4 * calculatedNpcs];
        for (int i = 0; i < hitboxDat.length; i++) {
            hitboxDat[i] = dBuf.get();
            if (i != 0 && i % 4 == 0) {
                callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_HITBOX), null, ++hitBoxCounter, calculatedNpcs));
            }
        }

        int displayBoxCounter = 0;

        // read display box section
        dBuf.clear();
        channel.read(dBuf);
        dBuf.flip();

        final byte[] displayDat = new byte[4 * calculatedNpcs];
        for (int i = 0; i < displayDat.length; i++) {
            displayDat[i] = dBuf.get();
            if (i != 0 && i % 4 == 0) {
                callback.onProgress(new GameResourcesLoadingPayload(String.format("Reading entity definitions for %s", ProgressStrings.LOADNAME_NPC_TBL_DISPLAYBOX), null, ++displayBoxCounter, calculatedNpcs));
            }
        }
        // finished reading file
        channel.close();
        inputStream.close();

        // build the master list
        for (int i = 0; i < calculatedNpcs; i++) {
            final EntityData e = new EntityData(i,
                    damageDat[i],
                    deathDat[i],
                    expDat[i],
                    flagDat[i],
                    healthDat[i],
                    hurtDat[i],
                    sizeDat[i],
                    tilesetDat[i],
                    new Rectangle(displayDat[i * 4], displayDat[i * 4 + 1], displayDat[i * 4 + 2], displayDat[i * 4 + 3]),
                    new Rectangle(hitboxDat[i * 4], hitboxDat[i * 4 + 1], hitboxDat[i * 4 + 2], hitboxDat[i * 4 + 3]));
            entityList.add(e);
        }
    }

    /**
     * Attempts to add an image to the repository.
     *
     * @param srcFile image to load
     */
    private void addImage(File srcFile) {
        if (srcFile == null) {
            return;
        }
        srcFile = FileUtils.newFile(srcFile.getAbsolutePath());

        callback.onProgress(new GameResourcesLoadingPayload(
                String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_IMAGES),
                String.format("Loading image:\n %s", srcFile.getAbsolutePath()),
                0, 1));

        try {
            if (imageMap.containsKey(srcFile)) {
                return;
            }
            imageMap.put(srcFile, loadImage(srcFile));
        } catch (Exception e) {
            AppLogger.error("Failed to add image " + srcFile, e);
        }

        callback.onProgress(new GameResourcesLoadingPayload(
                String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_IMAGES),
                String.format("Loading image:\n %s", srcFile.getAbsolutePath()),
                1, 1));
    }

    /**
     * Loads graphics files
     */
    protected void loadGraphics() throws IOException {
        title = loadGraphic(ExePointers.TITLE_PTR);
        myChar = loadGraphic(ExePointers.MYCHAR_PTR);
        armsImage = loadGraphic(ExePointers.ARMSIMAGE_PTR);
        arms = loadGraphic(ExePointers.ARMS_PTR);
        itemImage = loadGraphic(ExePointers.ITEMIMAGE_PTR);
        stageImage = loadGraphic(ExePointers.STAGEIMAGE_PTR);
        npcSym = loadGraphic(ExePointers.NPCSYM_PTR);
        npcRegu = loadGraphic(ExePointers.NPCREGU_PTR);
        textBox = loadGraphic(ExePointers.TEXTBOX_PTR);
        caret = loadGraphic(ExePointers.CARET_PTR);
        bullet = loadGraphic(ExePointers.BULLET_PTR);
        face = loadGraphic(ExePointers.FACE_PTR);
        fade = loadGraphic(ExePointers.FADE_PTR);
        loading = loadGraphic(ExePointers.LOADING_PTR);
    }

    /**
     * Loads a graphics file.
     *
     * @param strid executable string id to get file name from
     * @return file that was loaded
     */
    private File loadGraphic(int strid) {
        return loadGraphic(getExeString(strid));
    }

    /**
     * Loads a graphics file.
     *
     * @param name
     *            file name
     * @return file that was loaded
     */
    protected File loadGraphic(String name) {
        final String imgExt = getExeString(ExePointers.IMG_EXT_PTR);
        final File ret = correctFile(getGraphicsFile(dataDirectory.toString(), name, imgExt));
        addImage(ret);
        return ret;
    }

    /**
     * Loads an image.
     *
     * @param srcFile source file
     * @return filtered image
     * @throws Exception if an error occurs.
     */
    private BufferedImage loadImage(File srcFile) throws Exception {
        if (srcFile == null) {
            return null;
        }
        try (FileInputStream is = new FileInputStream(srcFile)) {
            final BufferedImage img = ColorUtils.replaceColor(ImageIO.read(is),
                    Color.BLACK,
                    ColorUtils.TRANSPARENT);

            if (getGraphicsResolution() == 2) {
                return img;
            }

            final double scale = 2.0 / getGraphicsResolution();
            if (scale == 1) {
                return img;
            }

            final int w = img.getWidth();
            final int h = img.getHeight();

            final BufferedImage after = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
            final AffineTransform at = new AffineTransform();
            at.scale(scale, scale);

            final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            return scaleOp.filter(img, after);
        } catch (Exception e) {
            AppLogger.error(String.format("Failed to load image %s", srcFile), e);
            throw e;
        }
    }

    /**
     * Loads map info.
     */
    private void loadMapInfo() {
        final String stage = getExeString(ExePointers.STAGE_FOLDER_PTR);
        final String npc = getExeString(ExePointers.NPC_FOLDER_PTR);
        final String prt = getExeString(ExePointers.PRT_PREFIX_PTR);
        final String npcP = getExeString(ExePointers.NPC_PREFIX_PTR);
        final String pxa = getExeString(ExePointers.PXA_EXT_PTR);

        final String dataDirPath = dataDirectory.getPath();

        final int mdSize = mapDataList.size();
        for (int i = 0; i < mdSize; i++) {
            final Mapdata data = mapDataList.get(i);

            final String fileName = data.getFileName();
            final int scrollType = data.getScrollType();
            final String mapName = data.getMapName();

            final List<MapInfo.PxeEntry> pxeList = new ArrayList<>();

            final String imgExt = getExeString(ExePointers.IMG_EXT_PTR);
            final File tileset = correctFile(getGraphicsFile(dataDirPath, String.format(prt, stage, data.getTileset()), imgExt));
            final File bgImage = correctFile(getGraphicsFile(dataDirPath, data.getBgName(), imgExt));
            final File npcSheet1 = correctFile(getGraphicsFile(dataDirPath, String.format(npcP, npc, data.getNpcSheet1()), imgExt));
            final File npcSheet2 = correctFile(getGraphicsFile(dataDirPath, String.format(npcP, npc, data.getNpcSheet2()), imgExt));

            addImage(tileset);
            addImage(bgImage);

            if (shouldLoadNpc) {
                addImage(npcSheet1);
                addImage(npcSheet2);
            }

            final File pxaFile = new File(String.format(pxa, dataDirectory + File.separator + stage, data.getTileset()));
            addPxa(pxaFile);

            callback.onProgress(new GameResourcesLoadingPayload(String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_PXA), null, i + 1, mdSize));

            final int[][][] map = loadMap(data, pxaFile);

            callback.onProgress(new GameResourcesLoadingPayload(String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_PXM), null, i + 1, mdSize));

            if (shouldLoadNpc) {
                final List<MapInfo.PxeEntry> entities = loadEntities(data);
                if (entities != null) {
                    pxeList.addAll(entities);
                }
                callback.onProgress(new GameResourcesLoadingPayload(String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_PXE), null, i + 1, mdSize));
            }

            final MapInfo info = new MapInfo(
                    map,
                    tileset,
                    bgImage,
                    fileName,
                    scrollType,
                    npcSheet1,
                    npcSheet2,
                    mapName,
                    pxaFile,
                    pxeList
            );
            mapInfoList.add(info);
        }
    }

    /**
     * Loads this map's layout file.
     */
    public int[][][] loadMap(Mapdata d, File pxaFile) {
        // load the map data
        ByteBuffer mapBuf;
        final String currentFileName = String.format(
                getExeString(ExePointers.PXM_EXT_PTR),
                this.dataDirectory + File.separator + getExeString(ExePointers.STAGE_FOLDER_PTR),
                d.getFileName()
        );

        int mapX;
        int mapY;

        final File currentFile = FileUtils.newFile(currentFileName);

        try (FileInputStream inStream = new FileInputStream(currentFile); FileChannel inChan = inStream.getChannel()) {
            final ByteBuffer hBuf = ByteBuffer.allocate(8);
            hBuf.order(ByteOrder.LITTLE_ENDIAN);
            inChan.read(hBuf);
            // read the filetag
            hBuf.flip();
            final byte[] tagArray = new byte[3];
            hBuf.get(tagArray, 0, 3);
            if (!(new String(tagArray).equals(getExeString(ExePointers.PXM_TAG_PTR)))) {
                inChan.close();
                inStream.close();
                throw new IOException("Bad file tag");
            }
            hBuf.get();
            mapX = hBuf.getShort();
            mapY = hBuf.getShort();
            mapBuf = ByteBuffer.allocate(mapY * mapX);
            mapBuf.order(ByteOrder.LITTLE_ENDIAN);
            inChan.read(mapBuf);
            mapBuf.flip();
        } catch (IOException e) {
            AppLogger.error(String.format("Failed to load PXM:\n %s", currentFileName), e);
            mapX = 21;
            mapY = 16;
            mapBuf = ByteBuffer.allocate(mapY * mapX);
        }

        final int[][][] map = new int[2][mapY][mapX];

        for (int y = 0; y < mapY; y++) {
            for (int x = 0; x < mapX; x++) {
                final int tile = 0xFF & mapBuf.get();
                if (calcPxa(tile, pxaFile) > 0x20) {
                    map[1][y][x] = tile;
                } else {
                    map[0][y][x] = tile;
                }
            }
        }

        return map;
    }

    /**
     * Calculates a tile's type.
     *
     * @param tileNum tile ID
     * @return tile type
     */
    private int calcPxa(int tileNum, File pxaFile) {
        final byte[] pxaData = getPxa(pxaFile);
        if (pxaData == null) {
            return 0;
        }
        try {
            return pxaData[tileNum] & 0xFF;
        } catch (Exception e) {
            AppLogger.error(String.format("Could not get tile %d in PXA (length is %d)", tileNum, pxaData.length), e);
        }
        return 0;
    }

    private byte[] getPxa(File srcFile) {
        srcFile = FileUtils.newFile(srcFile.getAbsolutePath());
        return pxaMap.get(srcFile);
    }

    /**
     * Attempts to add a PXA file to the repository.
     *
     * @param srcFile source file
     */
    private void addPxa(File srcFile) {
        srcFile = FileUtils.newFile(srcFile.getAbsolutePath());
        if (pxaMap.containsKey(srcFile)) {
            pxaMap.get(srcFile);
            return;
        }

        callback.onProgress(new GameResourcesLoadingPayload(
                String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_PXA),
                String.format("Loading PXA File:\n %s", srcFile.getAbsolutePath()),
                0, 1));

        final byte[] pxaArray = new byte[256]; // this is the max size. Indeed, the only size..
        try (FileInputStream inStream = new FileInputStream(srcFile); FileChannel inChan = inStream.getChannel()) {
            final ByteBuffer pxaBuf = ByteBuffer.wrap(pxaArray);
            inChan.read(pxaBuf);
            pxaBuf.flip();
            //final byte[] pxaArray = pxaBuf.array();
            //pxaMap.put(srcFile, pxaArray);
        } catch (Exception e) {
            AppLogger.error(String.format("Failed to load PXA:\n %s", srcFile), e);
        } finally {
            pxaMap.put(srcFile, pxaArray);
        }

        callback.onProgress(new GameResourcesLoadingPayload(
                String.format("Loading %s for map", ProgressStrings.LOADNAME_MAP_INFO_PXA),
                String.format("Loading PXA File:\n %s", srcFile.getAbsolutePath()),
                1, 1));
    }

    /**
     * Loads this map's entities.
     */
    private List<MapInfo.PxeEntry> loadEntities(Mapdata d) {
        final List<MapInfo.PxeEntry> pxeList = new ArrayList<>();
        final String currentFileName = String.format(getExeString(ExePointers.PXE_EXT_PTR),
                dataDirectory + File.separator + getExeString(ExePointers.STAGE_FOLDER_PTR), d.getFileName());

        final File currentFile = FileUtils.newFile(currentFileName);

        try (FileInputStream inStream = new FileInputStream(currentFile); FileChannel inChan = inStream.getChannel()) {
            final ByteBuffer hBuf = ByteBuffer.allocate(6);
            hBuf.order(ByteOrder.LITTLE_ENDIAN);

            inChan.read(hBuf);
            hBuf.flip();

            final int nEnt = hBuf.getShort(4);
            final ByteBuffer eBuf = ByteBuffer.allocate(nEnt * 12 + 2);

            eBuf.order(ByteOrder.LITTLE_ENDIAN);
            inChan.read(eBuf);
            eBuf.flip();
            eBuf.getShort(); // discard this value

            for (int i = 0; i < nEnt; i++) {
                final short pxeX = eBuf.getShort();
                final short pxeY = eBuf.getShort();
                final short pxeFlagID = eBuf.getShort();
                final short pxeEvent = eBuf.getShort();
                final short pxeType = eBuf.getShort();
                final short pxeFlags = (short)(eBuf.getShort() & 0xFFFF);
                final MapInfo.PxeEntry p = new MapInfo.PxeEntry(pxeX,
                        pxeY,
                        pxeFlagID,
                        pxeEvent,
                        pxeType,
                        pxeFlags,
                        entityList.get(pxeType));
                pxeList.add(p);
            }
        } catch (IOException e) {
            AppLogger.error(String.format("Failed to load PXE:\n %s", currentFileName), e);
            return null;
        }

        return pxeList;
    }
}
