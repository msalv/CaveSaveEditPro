package com.leo.cse.dto;

import java.awt.Image;
import java.util.List;
import java.util.Objects;

public class PlusSlot {
    public final int id;
    public final List<Weapon> weapons;
    public final short hp;
    public final short maxHp;
    public final String location;
    public final String modificationDate;
    public final Image character;

    public PlusSlot(int id, List<Weapon> weapons, short hp, short maxHp, String location, String modificationDate, Image character) {
        this.id = id;
        this.weapons = weapons;
        this.hp = hp;
        this.maxHp = maxHp;
        this.location = location;
        this.modificationDate = modificationDate;
        this.character = character;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PlusSlot plusSlot = (PlusSlot) o;

        if (id != plusSlot.id) return false;
        if (hp != plusSlot.hp) return false;
        if (maxHp != plusSlot.maxHp) return false;
        if (!weapons.equals(plusSlot.weapons)) return false;
        if (!Objects.equals(location, plusSlot.location)) return false;
        if (!Objects.equals(modificationDate, plusSlot.modificationDate)) return false;
        return Objects.equals(character, plusSlot.character);
    }

    @Override
    public int hashCode() {
        int result = weapons.hashCode();
        result = 31 * result + id;
        result = 31 * result + hp;
        result = 31 * result + maxHp;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (modificationDate != null ? modificationDate.hashCode() : 0);
        result = 31 * result + (character != null ? character.hashCode() : 0);
        return result;
    }
}
