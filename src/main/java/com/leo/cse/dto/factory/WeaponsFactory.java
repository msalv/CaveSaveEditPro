package com.leo.cse.dto.factory;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.Weapon;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.backend.mci.MCI;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class WeaponsFactory {
    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    public WeaponsFactory(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
    }

    public Weapon create(int i) {
        final MCI mci = profileManager.getCurrentMCI();
        final int id = profileManager.getIntField(ProfileFields.FIELD_WEAPON_IDS, i);

        final int yStart = mci.getArmsImageYStart();
        final int size = mci.getArmsImageSize();

        final Image image = (id != 0 && resourcesManager.hasResources())
                ? resourcesManager.getResources().getWeaponImage(id, size, yStart)
                : null;

        return new Weapon(
                id,
                mci.getWeaponName(id),
                image,
                profileManager.getIntField(ProfileFields.FIELD_WEAPON_LEVELS, i),
                profileManager.getIntField(ProfileFields.FIELD_WEAPON_EXP, i),
                profileManager.getIntField(ProfileFields.FIELD_WEAPON_CURRENT_AMMO, i),
                profileManager.getIntField(ProfileFields.FIELD_WEAPON_MAXIMUM_AMMO, i)
        );
    }

    public List<Weapon> createList() {
        final List<Weapon> weapons = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weapons.add(create(i));
        }
        return weapons;
    }

    public List<Weapon> createAll() {
        final MCI mci = profileManager.getCurrentMCI();
        final String[] weaponNames = mci.getWeaponNames();
        final int yStart = mci.getArmsImageYStart();
        final int size = mci.getArmsImageSize();

        final List<Weapon> items = new ArrayList<>();

        for (int itemId = 0; itemId < weaponNames.length; ++itemId) {
            final String title = weaponNames[itemId];
            if (title == null) {
                continue;
            }

            final Image image = (itemId != 0 && resourcesManager.hasResources())
                    ? resourcesManager.getResources().getWeaponImage(itemId, size, yStart)
                    : null;

            items.add(new Weapon(itemId, title, image));
        }

        return items;
    }
}
