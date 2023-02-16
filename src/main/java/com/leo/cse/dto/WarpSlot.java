package com.leo.cse.dto;

import java.awt.Image;
import java.util.Objects;

public class WarpSlot {
    public final int id;
    public final String name;
    public final int locationId;
    public final String locationName;
    public final Image image;

    public WarpSlot(int id, String name, int locationId, String locationName, Image image) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.locationName = locationName;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WarpSlot warpSlot = (WarpSlot) o;

        if (id != warpSlot.id) return false;
        if (locationId != warpSlot.locationId) return false;
        if (!Objects.equals(name, warpSlot.name)) return false;
        if (!Objects.equals(locationName, warpSlot.locationName)) return false;
        return Objects.equals(image, warpSlot.image);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + locationId;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", id, name);
    }
}
