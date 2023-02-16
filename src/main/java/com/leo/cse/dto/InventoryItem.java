package com.leo.cse.dto;

import java.awt.Image;
import java.util.Objects;

public class InventoryItem {
    public final int id;
    public final String title;
    public final Image image;

    public InventoryItem(int id, String title, Image image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryItem that = (InventoryItem) o;

        if (id != that.id) return false;
        if (!title.equals(that.title)) return false;
        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", id, title);
    }
}
