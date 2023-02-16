package com.leo.cse.backend.mci;

import com.google.gson.stream.JsonReader;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.log.AppLogger;
import com.leo.cse.util.ArrayUtils;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class MCI {
    protected final GameInfo gameInfo;
    private final String filePath;

    public MCI(InputStream is, String filePath) throws IOException, MCIException {
        this.filePath = filePath;
        final JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
        gameInfo = GameInfo.fromJSON(jsonReader);
        validate();
    }

    private void validate() throws MCIException {
        final int fps = gameInfo.fps;
        if (fps <= 0) {
            throw new MCIException("FPS cannot be less than or equal to 0!");
        }
        final int res = gameInfo.graphicsDensity;
        if (res <= 0) {
            throw new MCIException("GraphicsResolution cannot be less than or equal to 0!");
        }
        final boolean var = hasSpecial("VarHack");
        final boolean phy = hasSpecial("PhysVarHack");
        if (!var && phy) {
            throw new MCIException("Special support: PhysVarHack requires VarHack!");
        }
        final boolean mim = hasSpecial("MimHack");
        final boolean buy = hasSpecial("BuyHack");
        if (var && mim) {
            throw new MCIException("Special support: VarHack is incompatible with MimHack!");
        }
        if (var && buy) {
            throw new MCIException("Special support: VarHack is incompatible with BuyHack!");
        }
        if (mim && buy) {
            throw new MCIException("Special support: MimHack is incompatible with BuyHack!");
        }
    }

    public abstract boolean hasSpecial(String value);
    public abstract String getSpecials();

    public String getFilePath() {
        return filePath;
    }

    public int getFPS() {
        return gameInfo.fps;
    }

    public int getGraphicsDensity() {
        return gameInfo.graphicsDensity;
    }

    public Class<?> getProfileClass() throws ClassNotFoundException {
        return Class.forName(gameInfo.profileClass);
    }

    public String getProfileClassName() {
        return gameInfo.profileClass;
    }

    public EntityExtras getEntityExtras(MapInfo.PxeEntry e) {
        if (gameInfo.entityFrames == null) {
            return null;
        }

        final Rectangle frameRect = gameInfo.entityFrames.get((int)e.getType());
        if (frameRect == null) {
            AppLogger.error("frameRect is null");
            return null;
        }

        final Point offset = gameInfo.entityOffsets.getOrDefault((int)e.getType(), new Point());

        return new EntityExtras(frameRect, offset);
    }

    public String getName() {
        return gameInfo.name;
    }

    public String getAuthor() {
        return gameInfo.author;
    }

    public String getFlagDescription(int id) {
        return gameInfo.flagDescriptions.get(id);
    }

    public String getMapName(int id) {
        return ArrayUtils.getOrDefault(gameInfo.mapNames, id, null);
    }

    public String getSongName(int song) {
        return ArrayUtils.getOrDefault(gameInfo.songNames, song, null);
    }

    public String getItemName(int itemId) {
        return ArrayUtils.getOrDefault(gameInfo.itemNames, itemId, null);
    }

    public String getWarpName(int id) {
        return ArrayUtils.getOrDefault(gameInfo.warpNames, id, null);
    }

    public String getWarpLocName(int id) {
        return gameInfo.warpLocNames.get(id);
    }

    public String getWeaponName(int id) {
        return ArrayUtils.getOrDefault(gameInfo.weaponNames, id, null);
    }

    public String[] getMapNames() {
        return gameInfo.mapNames;
    }

    public String[] getSongNames() {
        return gameInfo.songNames;
    }

    public String[] getItemNames() {
        return gameInfo.itemNames;
    }

    public String[] getWeaponNames() {
        return gameInfo.weaponNames;
    }

    public Map<Integer, String> getWarpLocations() {
        return gameInfo.warpLocNames;
    }

    public String[] getEquipNames() {
        return gameInfo.equipNames;
    }

    public String[] getWarpNames() {
        return gameInfo.warpNames;
    }

    public int getSaveEvent() {
        return gameInfo.saveEvent;
    }

    public int getSaveFlagId() {
        return gameInfo.saveFlagID;
    }

    public int getArmsImageYStart() {
        return gameInfo.armsImageYStart;
    }

    public int getArmsImageSize() {
        return gameInfo.armsImageSize;
    }

    public String getChallengeName(int id) {
        return ArrayUtils.getOrDefault(gameInfo.challengeNames, id, null);
    }
}
