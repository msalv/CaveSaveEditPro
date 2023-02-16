package com.leo.cse.backend.res.loading.impl;

import com.leo.cse.backend.CString;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.backend.exe.PEFile;
import com.leo.cse.backend.res.DefaultGameResources;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.dto.StartPoint;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ExeResourcesLoader extends GameResourcesLoader {
    private static final int RVA_STARTING_HP_MAX = 0x14BD8;
    private static final int RVA_STARTING_HP_CUR = 0x14BCF;
    private static final int RVA_STARTING_MAP = 0x1D599;
    private static final int RVA_STARTING_POS_X = 0x1D592;
    private static final int RVA_STARTING_POS_Y = 0x1D590;
    private static final int RVA_STARTING_DIR = 0x14B74;

    /**
     * The ".rdata" segment. Used to read {@linkplain #exeStrings the executable
     * strings}.
     */
    private PEFile.Section rdataSection;

    /**
     * PE data.
     *
     * @see PEFile
     */
    private PEFile peData;

    public ExeResourcesLoader(File sourceFile, byte[] data, String encoding, boolean shouldLoadNpc) {
        super(sourceFile, data, shouldLoadNpc, encoding);
    }

    @Override
    public int getGraphicsResolution() {
        return 1;
    }

    @Override
    public GameResources load() throws Exception {
        locateSections();
        return super.load();
    }

    @Override
    protected void loadStrings() {
        final byte[] buffer = new byte[0x10];
        final ByteBuffer uBuf = ByteBuffer.wrap(buffer);
        uBuf.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < ExePointers.STRING_POINTERS.length; i++) {
            final int pointer = ExePointers.STRING_POINTERS[i];
            uBuf.put(rdataSection.rawData, pointer, buffer.length);
            uBuf.flip();

            // backslashes are Windows-only, so replace them with forward slashes
            final String str = CString.newInstance(buffer, encoding)
                    .replaceAll("\\\\", "/");

            uBuf.clear();
            setExeString(pointer, str);
        }
    }

    @Override
    protected void fillMapdata() {
        // find the .csmap or .swdata segment
        PEFile.Section mapSec = null;
        String mapSecTag = null;
        for (int i = 0; i < peData.getSectionCount(); i++) {
            final PEFile.Section s = peData.getSectionAt(i);
            final String st = s.decodeTag();
            if (st.equals(".csmap") || st.equals(".swdata")) {
                mapSec = s;
                mapSecTag = st;
                break;
            }
        }

        final int mapSize = 200;

        if (mapSec == null) { // original exe
            final ByteBuffer uBuf = ByteBuffer.allocate(mapSize);
            uBuf.order(ByteOrder.LITTLE_ENDIAN);

            final int numMaps = 95;
            int offset = 0x937B0; // seek to start of mapdatas

            for (int i = 0; i < numMaps; i++) {
                uBuf.put(data, offset, mapSize);
                uBuf.flip();

                mapDataList.add(createMapData(i, uBuf, encoding));
                callback.onProgress(new GameResourcesLoadingPayload("Reading data for map", null, i + 1, numMaps));

                offset += mapSize;
                uBuf.clear();
            }
        } else { // exe has been edited probably
            final ByteBuffer buf = ByteBuffer.wrap(mapSec.rawData);
            final int numMaps = mapSec.rawData.length / mapSize;
            if (mapSecTag.contains(".csmap")) {
                // cave editor/booster's lab
                for (int i = 0; i < numMaps; i++) {
                    mapDataList.add(createMapData(i, buf, encoding));
                    callback.onProgress(new GameResourcesLoadingPayload("Reading data for map", null, i + 1, numMaps));
                }
            } else {
                // sue's workshop
                int nMaps = 0;
                while (nMaps < numMaps) {
                    mapDataList.add(createMapData(nMaps, buf, encoding));
                    callback.onProgress(new GameResourcesLoadingPayload("Reading data for map", null, ++nMaps, numMaps));
                }
            }
        }
    }

    private Mapdata createMapData(int mapNum, ByteBuffer buf, String charEncoding) {
        final byte[] buffer = new byte[0x23];

        buf.get(buffer, 0, 0x20);
        final String tileset = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x20);
        final String fileName = CString.newInstance(buffer, charEncoding);
        final int scrollType = buf.getInt() & 0xFF;

        buf.get(buffer, 0, 0x20);
        final String bgName = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x20);
        final String npcSheet1 = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x20);
        final String npcSheet2 = CString.newInstance(buffer, charEncoding);
        final int bossNum = buf.get();

        buf.get(buffer, 0, 0x23);
        final String mapName = CString.newInstance(buffer, charEncoding);

        return new Mapdata(mapNum, tileset, fileName, scrollType, bgName, npcSheet1, npcSheet2, bossNum, mapName);
    }

    @Override
    protected void initStartPoint() {
        startPoint = new StartPoint(
                Byte.toUnsignedInt(peData.setupRVAPoint(RVA_STARTING_MAP).get()),
                peData.setupRVAPoint(RVA_STARTING_POS_X).get(),
                peData.setupRVAPoint(RVA_STARTING_POS_Y).get(),
                peData.setupRVAPoint(RVA_STARTING_DIR).getInt(),
                peData.setupRVAPoint(RVA_STARTING_HP_CUR).getShort(),
                peData.setupRVAPoint(RVA_STARTING_HP_MAX).getShort()
        );
    }

    @Override
    protected File getDataDirectory(File base) {
        return new File(base.getParent() + getExeString(ExePointers.DATA_FOLDER_PTR));
    }

    @Override
    protected void loadGraphics() throws IOException {
        super.loadGraphics();
        callback.onProgress(new GameResourcesLoadingPayload("Loading images from executable", null, 0, 1));
    }

    @Override
    protected GameResources build() {
        return new DefaultGameResources(
                startPoint,
                exeStrings,
                mapDataList,
                mapInfoList,
                imageMap,
                pxaMap,
                title,
                myChar,
                armsImage,
                arms,
                itemImage,
                stageImage,
                npcSym,
                npcRegu,
                textBox,
                caret,
                bullet,
                face,
                fade,
                loading
        );
    }

    /**
     * Locate the executable sections.
     *
     * @throws IOException if an I/O exception occurs.
     */
    private void locateSections() throws IOException {
        final ByteBuffer bb = ByteBuffer.wrap(data);

        peData = new PEFile(bb, 0x1000);

        // get sections
        final int rdataSecId = peData.getSectionIndexByTag(".rdata");
        if (rdataSecId == -1) {
            throw new IOException("Could not find .rdata segment!");
        }

        rdataSection = peData.getSectionAt(rdataSecId);

        final int rsrcSecId = peData.getResourcesIndex();
        if (rsrcSecId == -1) {
            throw new IOException("Could not find .rsrc segment!");
        }
    }
}
