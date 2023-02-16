package com.leo.cse.dto;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.util.StringUtils;

import java.util.Objects;

public class MapFlag {
    public static final int FLAGS_COUNT = 128;

    public static final String DESC_NONE = "(unknown map)";

    public final int id;
    public final String description;
    public final boolean value;

    public MapFlag(int id, String description, boolean value) {
        this.id = id;
        this.description = description;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MapFlag flag = (MapFlag) o;

        if (id != flag.id) return false;
        if (value != flag.value) return false;
        return Objects.equals(description, flag.description);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (value ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MapFlag{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }

    public static String getDescription(MCI mci, GameResourcesManager resourcesManager, int id) {
        String mapName = null;
        if (resourcesManager.hasResources()) {
            if (isValid(mci, resourcesManager, id)) {
                final MapInfo mi = resourcesManager.getResources().getMapInfo(id);
                final StringBuilder stringBuilder = new StringBuilder();
                if (!StringUtils.isNullOrEmpty(mi.getMapName())) {
                    stringBuilder.append(mi.getMapName());
                } else {
                    stringBuilder.append(mci.getMapName(id));
                }
                if (!StringUtils.isNullOrEmpty(mi.getFileName())) {
                    final boolean shouldAddBraces = stringBuilder.length() != 0;
                    if (shouldAddBraces) {
                        stringBuilder.append(' ').append('(');
                    }

                    stringBuilder.append(mi.getFileName());

                    if (shouldAddBraces) {
                        stringBuilder.append(')');
                    }
                }
                mapName = stringBuilder.toString();
            }
        } else {
            mapName = mci.getMapName(id);
        }
        return mapName != null ? mapName : DESC_NONE;
    }

    public static boolean isValid(MCI mci, GameResourcesManager resourcesManager, int id) {
        if (id > FLAGS_COUNT - 1) {
            return false;
        } else if (resourcesManager.hasResources()) {
            return resourcesManager.getResources().getMapInfoCount() - 1 >= id;
        } else {
            return mci.getMapName(id) != null;
        }
    }
}
