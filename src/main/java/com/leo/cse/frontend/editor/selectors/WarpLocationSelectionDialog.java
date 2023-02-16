package com.leo.cse.frontend.editor.selectors;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.WarpSlot;
import com.leo.cse.util.Dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WarpLocationSelectionDialog {
    private final ProfileManager profileManager;
    private final WarpLocationItem initialSelection;
    private final int position;

    public WarpLocationSelectionDialog(WarpSlot initialSlot, int position, ProfileManager profileManager) {
        this.profileManager = profileManager;
        this.initialSelection = new WarpLocationItem(initialSlot.locationId, initialSlot.locationName);
        this.position = position;
    }

    public void select() {
        final List<WarpLocationItem> locationItems = getLocationItems();

        if (locationItems.isEmpty()) {
            return;
        }

        final WarpLocationItem selected = Dialogs.showSelectionDialog("Select a location",
                locationItems.toArray(new WarpLocationItem[0]),
                initialSelection);

        if (selected == null || selected.equals(initialSelection)) {
            return;
        }

        profileManager.setField(ProfileFields.FIELD_WARP_LOCATIONS, position, selected.id);
    }

    private List<WarpLocationItem> getLocationItems() {
        final List<WarpLocationItem> maps = new ArrayList<>();
        final Map<Integer, String> locations = profileManager.getCurrentMCI().getWarpLocations();

        for (Map.Entry<Integer, String> entry : locations.entrySet()) {
            maps.add(new WarpLocationItem(entry.getKey(), entry.getValue()));
        }

        return maps;
    }

    private static class WarpLocationItem implements Comparable<WarpLocationItem> {
        final int id;
        final String name;

        private WarpLocationItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WarpLocationItem item = (WarpLocationItem) o;

            if (id != item.id) return false;
            return Objects.equals(name, item.name);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d - %s", id, name);
        }

        @Override
        public int compareTo(WarpLocationItem o) {
            return name.compareTo(o.name);
        }
    }
}
