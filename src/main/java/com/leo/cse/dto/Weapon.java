package com.leo.cse.dto;

import java.awt.Image;
import java.util.Objects;

public class Weapon {
    public final int id;
    public final String title;
    public final Image image;
    public final int level;
    public final int exp;
    public final int currentAmmo;
    public final int maxAmmo;

    public Weapon(int id, String title, Image image, int level, int exp, int currentAmmo, int maxAmmo) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.level = level;
        this.exp = exp;
        this.currentAmmo = currentAmmo;
        this.maxAmmo = maxAmmo;
    }

    public Weapon(int id, String title, Image image) {
        this(id, title, image, 1, 0, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Weapon weapon = (Weapon) o;

        if (id != weapon.id) return false;
        if (level != weapon.level) return false;
        if (exp != weapon.exp) return false;
        if (currentAmmo != weapon.currentAmmo) return false;
        if (maxAmmo != weapon.maxAmmo) return false;
        if (!Objects.equals(title, weapon.title)) return false;
        return Objects.equals(image, weapon.image);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + level;
        result = 31 * result + exp;
        result = 31 * result + currentAmmo;
        result = 31 * result + maxAmmo;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", id, title);
    }
}
