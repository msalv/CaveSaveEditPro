package com.leo.cse.backend.res;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.exe.Mapdata;
import com.leo.cse.dto.StartPoint;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Resources loaded from exe-file
 */
public class DefaultGameResources extends GameResources {

    public DefaultGameResources(StartPoint startPoint,
                                Map<Integer, String> exeStrings,
                                List<Mapdata> mapdata,
                                List<MapInfo> mapInfo,
                                Map<File, BufferedImage> imageMap,
                                Map<File, byte[]> pxaMap,
                                File title,
                                File myChar,
                                File armsImage,
                                File arms,
                                File itemImage,
                                File stageImage,
                                File npcSym,
                                File npcRegu,
                                File textBox,
                                File caret,
                                File bullet,
                                File face,
                                File fade,
                                File loading) {
        super(startPoint,
                exeStrings,
                mapdata,
                mapInfo,
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
