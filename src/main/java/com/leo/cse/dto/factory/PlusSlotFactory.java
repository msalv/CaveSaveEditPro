package com.leo.cse.dto.factory;

import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.PlusProfileManager;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.dto.PlusSlot;
import com.leo.cse.dto.Weapon;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.util.PlayerCharUtils;

import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlusSlotFactory {
    private final ProfileManager profileManager;
    private final PlusProfileManager plusProfileManager;
    private final GameResourcesManager resourcesManager;

    private final WeaponsFactory weaponsFactory;

    private final Date modDateHolder = new Date();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public PlusSlotFactory(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.weaponsFactory = new WeaponsFactory(profileManager, resourcesManager);
        this.plusProfileManager = profileManager.getPlusProfileManager();
    }

    public PlusSlot create(int i) {
        final int currentSlotId = plusProfileManager.setCurrentSlotId(i);

        final List<Weapon> weapons = weaponsFactory.createList();
        final short hp = profileManager.getShortField(ProfileFields.FIELD_CURRENT_HEALTH);
        final short maxHp = profileManager.getShortField(ProfileFields.FIELD_MAXIMUM_HEALTH);

        modDateHolder.setTime(profileManager.getLongField(ProfileFields.FIELD_MODIFY_DATE) * 1000L);
        final String modificationDate = dateFormatter.format(modDateHolder);

        final int map = profileManager.getIntField(ProfileFields.FIELD_MAP);
        final String location;
        if (resourcesManager.hasResources()) {
            final MapInfo info = resourcesManager.getResources().getMapInfo(map);
            location = (info != null) ? info.getMapName() : null;
        } else {
            location = profileManager.getCurrentMCI().getMapName(map);
        }

        final Image character = PlayerCharUtils.getCharacterImage(profileManager, resourcesManager, i, false);

        plusProfileManager.setCurrentSlotId(currentSlotId);

        return new PlusSlot(i, weapons, hp, maxHp, location, modificationDate, character);
    }
}
