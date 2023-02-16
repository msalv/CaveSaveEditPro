package com.leo.cse.backend.res.loading.impl;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.backend.CString;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.backend.profile.model.Profile;
import com.leo.cse.backend.res.DefaultGameResources;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.dto.StartPoint;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MRMapResourcesLoader extends GameResourcesLoader {
    public MRMapResourcesLoader(File sourceFile, byte[] data, String encoding, boolean shouldLoadNpc) {
        super(sourceFile, data, shouldLoadNpc, encoding);
    }

    @Override
    public int getGraphicsResolution() {
        return 1;
    }

    @Override
    protected void loadStrings() {
        setExeString(ExePointers.ARMSITEM_PTR, "ArmsItem.tsc");
        setExeString(ExePointers.IMG_EXT_PTR, "%s/%s.png");
        setExeString(ExePointers.CREDIT_PTR, "Credit.tsc");
        setExeString(ExePointers.NPC_TBL_PTR, "npc.tbl");
        setExeString(ExePointers.PIXEL_PTR, ""); // not needed
        setExeString(ExePointers.MYCHAR_PTR, "MyChar");
        setExeString(ExePointers.TITLE_PTR, "Title");
        setExeString(ExePointers.ARMSIMAGE_PTR, "ArmsImage");
        setExeString(ExePointers.ARMS_PTR, "Arms");
        setExeString(ExePointers.ITEMIMAGE_PTR, "ItemImage");
        setExeString(ExePointers.STAGEIMAGE_PTR, "StageImage");
        setExeString(ExePointers.NPCSYM_PTR, "Npc/NpcSym");
        setExeString(ExePointers.NPCREGU_PTR, "Npc/NpcRegu");
        setExeString(ExePointers.TEXTBOX_PTR, "TextBox");
        setExeString(ExePointers.CARET_PTR, "Caret");
        setExeString(ExePointers.BULLET_PTR, "Bullet");
        setExeString(ExePointers.FACE_PTR, "Face");
        setExeString(ExePointers.FADE_PTR, "Fade");
        setExeString(ExePointers.DATA_FOLDER_PTR, ""); // not needed
        setExeString(ExePointers.LOADING_PTR, "Loading");
        setExeString(ExePointers.PXM_TAG_PTR, "PXM");
        setExeString(ExePointers.PROFILE_NAME_PTR, "Profile.dat");
        setExeString(ExePointers.PROFILE_HEADER_PTR, Profile.DEFAULT_HEADER);
        setExeString(ExePointers.PROFILE_FLAGH_PTR, Profile.DEFAULT_FLAGH);
        setExeString(ExePointers.STAGESELECT_PTR, "StageSelect.tsc");
        setExeString(ExePointers.STAGE_FOLDER_PTR, "Stage");
        setExeString(ExePointers.PRT_PREFIX_PTR, "%s/Prt%s");
        setExeString(ExePointers.PXA_EXT_PTR, "%s/%s.pxa");
        setExeString(ExePointers.PXM_EXT_PTR, "%s/%s.pxm");
        setExeString(ExePointers.PXE_EXT_PTR, "%s/%s.pxe");
        setExeString(ExePointers.TSC_EXT_PTR, "%s/%s.tsc");
        setExeString(ExePointers.NPC_FOLDER_PTR, "Npc");
        setExeString(ExePointers.NPC_PREFIX_PTR, "%s/Npc%s");
        setExeString(ExePointers.HEAD_PTR, "Head.tsc");
    }

    @Override
    protected void fillMapdata() {
        final int numMaps = BytesReaderWriter.readInt(data, 0);
        final int capacity = numMaps * 0x74;

        final ByteBuffer dBuf = ByteBuffer.allocate(capacity);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        dBuf.put(data, Integer.BYTES, capacity);
        dBuf.flip();

        for (int i = 0; i < numMaps; i++) {
            mapDataList.add(createMapData(i, dBuf, encoding));
            callback.onProgress(new GameResourcesLoadingPayload("Loading images using mrmap.bin", null, i + 1, numMaps));
        }
    }

    private Mapdata createMapData(int mapNum, ByteBuffer buf, String charEncoding) {
        final byte[] buffer = new byte[0x22];

        buf.get(buffer, 0, 0x10);
        final String tileset = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x10);
        final String fileName = CString.newInstance(buffer, charEncoding);

        final int scrollType = buf.get();

        buf.get(buffer, 0, 0x10);
        final String bgName = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x10);
        final String npcSheet1 = CString.newInstance(buffer, charEncoding);

        buf.get(buffer, 0, 0x10);
        final String npcSheet2 = CString.newInstance(buffer, charEncoding);

        final int bossNum = buf.get();

        buf.get(buffer, 0, 0x22);
        final String mapName = CString.newInstance(buffer, charEncoding);

        return new Mapdata(mapNum, tileset, fileName, scrollType, bgName, npcSheet1, npcSheet2, bossNum, mapName);
    }

    @Override
    protected void initStartPoint() {
        this.startPoint = StartPoint.DEFAULT;
    }

    @Override
    protected File getDataDirectory(File base) {
        return base.getParentFile();
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
}
