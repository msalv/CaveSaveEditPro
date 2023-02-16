package com.leo.cse.frontend.editor.selectors;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.util.ArrayUtils;
import com.leo.cse.util.Dialogs;
import com.leo.cse.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapSelectionDialog {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public MapSelectionDialog(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public void select() {
        final List<GameMap> maps = getMaps();

        if (maps.isEmpty()) {
            return;
        }

        final GameMap current = getCurrentMap();

        final GameMap selected = Dialogs.showSelectionDialog("Select a map",
                maps.toArray(new GameMap[0]),
                current);

        if (selected == null || selected.equals(current)) {
            return;
        }

        profileManager.setField(ProfileFields.FIELD_MAP, selected.mapId);
    }

    private GameMap getCurrentMap() {
        final int currentMapId = profileManager.getCurrentMapId();
        final String[] fallback = profileManager.getCurrentMCI().getMapNames();

        final String currentMapName;

        if (resourcesManager.hasResources()) {
            final GameResources resources = resourcesManager.getResources();
            final MapInfo info = resources.getMapInfo(currentMapId);
            currentMapName = StringUtils.isNullOrEmpty(info.getMapName())
                    ? ArrayUtils.getOrDefault(fallback, currentMapId, "")
                    : info.getMapName();
        } else {
            currentMapName = ArrayUtils.getOrDefault(fallback, currentMapId, "");
        }

        return new GameMap(currentMapId, currentMapName);
    }

    private List<GameMap> getMaps() {
        final List<GameMap> maps = new ArrayList<>();
        final String[] fallback = profileManager.getCurrentMCI().getMapNames();

        if (resourcesManager.hasResources()) {
            final GameResources resources = resourcesManager.getResources();
            final int mapsCount = resources.getMapInfoCount();
            for (int i = 0; i < mapsCount; ++i) {
                final MapInfo info = resources.getMapInfo(i);
                final String mapName = StringUtils.isNullOrEmpty(info.getMapName())
                        ? ArrayUtils.getOrDefault(fallback, i, "")
                        : info.getMapName();

                maps.add(new GameMap(i, mapName));
            }
        }

        if (maps.isEmpty()) {
            for (int i = 0; i < fallback.length; ++i) {
                maps.add(new GameMap(i, fallback[i]));
            }
        }

        return maps;
    }

    private static class GameMap implements Comparable<GameMap> {
        final int mapId;
        final String name;

        private GameMap(int mapId, String name) {
            this.mapId = mapId;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameMap gameMap = (GameMap) o;

            if (mapId != gameMap.mapId) return false;
            return Objects.equals(name, gameMap.name);
        }

        @Override
        public int hashCode() {
            int result = mapId;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d - %s", mapId, name);
        }

        @Override
        public int compareTo(GameMap o) {
            return name.compareTo(o.name);
        }
    }
}
