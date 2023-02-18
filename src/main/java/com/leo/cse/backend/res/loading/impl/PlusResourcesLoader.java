package com.leo.cse.backend.res.loading.impl;

import com.leo.cse.backend.CString;
import com.leo.cse.util.BufferCompat;
import com.leo.cse.util.FileUtils;
import com.leo.cse.backend.exe.ExePointers;
import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.dto.StartPoint;
import com.leo.cse.backend.profile.model.Profile;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.PlusGameResources;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PlusResourcesLoader extends GameResourcesLoader {
    public PlusResourcesLoader(File sourceFile, byte[] data, String encoding, boolean shouldLoadNpc) {
        super(sourceFile, data, shouldLoadNpc, encoding);
    }

    @Override
    public int getGraphicsResolution() {
        return 2;
    }

    @Override
    protected File correctFile(File file) {
        if (file.exists()) {
            return file;
        }
        String name = file.getName();
        final String parent = file.getParentFile().getName();
        if ("Stage".equalsIgnoreCase(parent)) {
            name = "Stage" + File.separator + name;
        }
        if ("Npc".equals(parent)) {
            name = "Npc" + File.separator + name;
        }
        final File base = FileUtils.getBaseFolder(file);
        if (base == null) {
            return null;
        }
        return new File(base + File.separator + name);
    }

    @Override
    protected void loadStrings() {
        setExeString(ExePointers.ARMSITEM_PTR, "ArmsItem.tsc");
        setExeString(ExePointers.IMG_EXT_PTR, "%s/%s.bmp");
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
        final int numMaps = data.length / 229;

        final ByteBuffer dBuf = ByteBuffer.allocate(numMaps * 229);
        dBuf.order(ByteOrder.LITTLE_ENDIAN);
        dBuf.put(data, 0, numMaps * 229);
        BufferCompat.flip(dBuf);

        for (int i = 0; i < numMaps; i++) {
            mapDataList.add(createMapData(i, dBuf, encoding));
            callback.onProgress(new GameResourcesLoadingPayload("Loading images from executable", null, i + 1, numMaps));
        }
    }

    private Mapdata createMapData(int mapNum, ByteBuffer buf, String charEncoding) {
        final byte[] buf32 = new byte[0x20];

        buf.get(buf32);
        final String tileset = CString.newInstance(buf32, charEncoding);

        buf.get(buf32);
        final String fileName = CString.newInstance(buf32, charEncoding);

        final int scrollType = buf.getInt();

        buf.get(buf32);
        final String bgName = CString.newInstance(buf32, charEncoding);

        buf.get(buf32);
        final String npcSheet1 = CString.newInstance(buf32, charEncoding);

        buf.get(buf32);
        final String npcSheet2 = CString.newInstance(buf32, charEncoding);

        final int bossNum = buf.get();

        final byte[] jpName = new byte[0x20];
        buf.get(jpName);

        buf.get(buf32);
        final String mapName = CString.newInstance(buf32, charEncoding);

        return new Mapdata(mapNum, tileset, fileName, scrollType, bgName, npcSheet1, npcSheet2, bossNum, mapName, jpName);
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
        return new PlusGameResources(
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
