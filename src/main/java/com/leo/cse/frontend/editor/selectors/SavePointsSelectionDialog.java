package com.leo.cse.frontend.editor.selectors;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.util.Dialogs;
import com.leo.cse.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;

public class SavePointsSelectionDialog {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public SavePointsSelectionDialog(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public void select() {
        final List<SavePoint> savePoints = getSavePoints();

        if (savePoints.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No Save Points found.",
                    "Search for Save Points",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        final Integer[] position = (Integer[]) profileManager.getField(ProfileFields.FIELD_MAP_AND_POSITION);

        final MapInfo info = resourcesManager.getResources().getMapInfo(position[0]);
        final String fileName = (info != null) ? info.getFileName() : null;
        final String mapName = (info != null) ? info.getMapName() : null;
        final SavePoint current = new SavePoint(position[0], toShort(position[1] / 32), toShort(position[2] / 32), fileName, mapName);

        final SavePoint selected = Dialogs.showSelectionDialog("Select Save Point to go to",
                savePoints.toArray(new SavePoint[0]),
                current);

        if (selected == null) {
            return;
        }

        profileManager.setField(
                ProfileFields.FIELD_MAP_AND_POSITION,
                new Integer[] { selected.map, selected.x * 32, selected.y * 32 });
    }

    private List<SavePoint> getSavePoints() {
        final int saveEvent = profileManager.getCurrentMCI().getSaveEvent();
        final List<SavePoint> savePoints = new ArrayList<>();

        final int mapsCount = resourcesManager.getResources().getMapInfoCount();
        for (int i = 0; i < mapsCount; i++) {
            final MapInfo map = resourcesManager.getResources().getMapInfo(i);

            final Iterator<MapInfo.PxeEntry> entities = map.getPxeIterator();

            while (entities.hasNext()) {
                final MapInfo.PxeEntry entity = entities.next();
                if (entity.getEvent() != saveEvent) {
                    // if entity's event is not the save event, continue
                    continue;
                }
                final int flagID = entity.getFlagID();
                final int entityFlags = entity.getFlags() | entity.getInfo().getFlags();

                if ((entityFlags & 0x0800) != 0) {
                    // Appear once flagID set
                    // if entity isn't supposed to appear, continue
                    if (!profileManager.getBooleanField(ProfileFields.FIELD_FLAGS, flagID)) {
                        continue;
                    }
                }
                if ((entityFlags & 0x4000) != 0) {
                    // No Appear if flagID set
                    // if entity isn't supposed to appear, continue
                    if (profileManager.getBooleanField(ProfileFields.FIELD_FLAGS, flagID)) {
                        continue;
                    }
                }
                if ((entityFlags & 0x2000) == 0) {
                    // if entity cannot be interacted with, continue
                    continue;
                }

                final String name = StringUtils.isNullOrEmpty(map.getMapName())
                        ? profileManager.getCurrentMCI().getMapName(i)
                        : map.getMapName();

                savePoints.add(new SavePoint(i, entity.getX(), entity.getY(), map.getFileName(), name));
            }
        }

        return savePoints;
    }

    private static short toShort(int i) {
        return (short) Math.min(Math.max(i, Short.MIN_VALUE), Short.MAX_VALUE);
    }

    private static class SavePoint {
        public final int map;
        public final short x;
        public final short y;
        public final String fileName;
        public final String mapName;

        public SavePoint(int map, short x, short y, String fileName, String mapName) {
            this.map = map;
            this.x = x;
            this.y = y;
            this.fileName = fileName;
            this.mapName = mapName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SavePoint savePoint = (SavePoint) o;

            if (map != savePoint.map) return false;
            if (x != savePoint.x) return false;
            if (y != savePoint.y) return false;
            if (!Objects.equals(fileName, savePoint.fileName)) return false;
            return Objects.equals(mapName, savePoint.mapName);
        }

        @Override
        public int hashCode() {
            int result = map;
            result = 31 * result + (int) x;
            result = 31 * result + (int) y;
            result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
            result = 31 * result + (mapName != null ? mapName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%3$d - %5$s (%4$s) at [%1$s, %2$s]",
                    x,
                    y,
                    map,
                    fileName != null ? fileName : "None",
                    mapName != null ? mapName : "None");
        }
    }
}
