package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignBottom;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.dto.InventoryItem;
import com.leo.cse.dto.Weapon;
import com.leo.cse.dto.factory.InventoryItemsFactory;
import com.leo.cse.dto.factory.WeaponsFactory;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.InventoryItemComponent;
import com.leo.cse.frontend.ui.components.WeaponComponent;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.compound.OnCheckedStateChangedListener;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.editor.selectors.InventoryItemSelectionDialog;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.editor.selectors.WeaponSelectionDialog;
import com.leo.cse.frontend.ui.layout.GridLayout;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.HorizontalSpreadLayout;
import com.leo.cse.frontend.ui.layout.JContainer;
import com.leo.cse.frontend.ui.layout.VerticalFlowLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.border.EmptyBorder;

public class InventoryPage extends VerticalLayout implements
        ProfileStateChangeListener,
        OnGameResourcesLoadingStateChangeListener {

    private final static int WEAPONS_COUNT = 7;
    private final static int ITEMS_COUNT = 30;

    private final List<WeaponComponent> weaponComponents = new ArrayList<>();
    private final List<InventoryItemComponent> itemsComponents = new ArrayList<>();
    private final Map<Integer, LabeledCheckBox> flagsComponents = new HashMap<>();

    private TextButton whimsicalStarCountButton;
    private TextButton mimCostumeButton;
    private TextButton buyHackButton;

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private final WeaponsFactory weaponsFactory;
    private final InventoryItemsFactory inventoryItemsFactory;

    public InventoryPage(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.weaponsFactory = new WeaponsFactory(profileManager, resourcesManager);
        this.inventoryItemsFactory = new InventoryItemsFactory(profileManager, resourcesManager);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        initPage();
        bind();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        profileManager.addListener(this);
        resourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        resourcesManager.removeListener(this);
        super.removeNotify();
    }

    private void initPage() {
        add(initWeapons());
        add(initEquips(), alignBottom(topMargin(10)));
        add(initItemsGrid(), topMargin(10));
    }

    private Component initWeapons() {
        final HorizontalSpreadLayout weaponsLayout = new HorizontalSpreadLayout();
        weaponsLayout.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        weaponsLayout.setGap(9);

        final OnCheckedStateChangedListener changedListener = (button, isChecked) -> {
            for (int i = 0; i < weaponComponents.size(); ++i) {
                final WeaponComponent comp = weaponComponents.get(i);
                if (comp != button) {
                    comp.setChecked(false);
                } else {
                    profileManager.setField(ProfileFields.FIELD_CURRENT_WEAPON, i);
                }
            }
        };

        for (int i = 0; i < WEAPONS_COUNT; i++) {
            final WeaponComponent comp = new WeaponComponent();
            comp.setOnCheckedStateListener(changedListener);
            comp.setComponentController(new WeaponComponentController(i));
            weaponComponents.add(comp);
            weaponsLayout.add(comp);
        }

        return weaponsLayout;
    }

    private Component initItemsGrid() {
        final GridLayout grid = new GridLayout();
        grid.setSpanCount(5);
        grid.setHorizontalGap(5);
        grid.setVerticalGap(6);

        for (int i = 0; i < ITEMS_COUNT; i++) {
            final InventoryItemComponent comp = new InventoryItemComponent();
            comp.setMinimumSize(new Dimension(Integer.MAX_VALUE, 61));
            comp.setText("Add Item");

            final int position = i;
            comp.setOnClickListener((item) -> {
                showInventoryItemSelectionDialog(item, position);
            });

            itemsComponents.add(comp);
            grid.add(comp);
        }

        return grid;
    }

    private void showInventoryItemSelectionDialog(InventoryItem item, int position) {
        new InventoryItemSelectionDialog(item, position, profileManager, inventoryItemsFactory)
                .select();
    }

    private Component initEquips() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        layout.add(initWhimsicalStarCountField(), alignRight());
        layout.add(initMimCostumeField(), alignRight(rightMargin(9)));
        layout.add(initBuyHackField(), alignRight(rightMargin(9)));

        layout.add(initFlags());

        return layout;
    }

    private Component initFlags() {
        final VerticalFlowLayout layout = new VerticalFlowLayout();
        layout.setHorizontalGap(9);
        layout.setVerticalGap(4);
        layout.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        final String[] equipNames = profileManager.getCurrentMCI().getEquipNames();

        for (int i = 0; i < equipNames.length; ++i) {
            final int itemId = i;
            final LabeledCheckBox checkBox = new LabeledCheckBox();
            checkBox.setText(equipNames[i]);
            checkBox.setSingleLine(true);
            checkBox.setMinimumSize(new Dimension(120, 0));
            checkBox.setOnCheckedStateListener(((button, isChecked) -> {
                profileManager.setField(ProfileFields.FIELD_EQUIPS, itemId, isChecked);
            }));
            layout.add(checkBox);
            flagsComponents.put(itemId, checkBox);
        }

        return layout;
    }

    private Component initWhimsicalStarCountField() {
        final JContainer field = createEquipField("Whimsical Star Count:", () -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_STAR_COUNT),
                    "Input new Whimsical Star Count value"
            );
            dialog.selectShort(this, (count) -> {
                profileManager.setField(ProfileFields.FIELD_STAR_COUNT, count);
            });
        });
        whimsicalStarCountButton = field.findByName("button");
        return field;
    }

    private Component initMimCostumeField() {
        final JContainer field = createEquipField("<MIM Costume:", () -> {
            final InputDialog<Long> dialog = new InputDialog<>(
                    profileManager.getLongField(ProfileFields.FIELD_MIM_COSTUME),
                    "Input new value for <MIM costume"
            );
            dialog.selectLong(this, (count) -> {
                profileManager.setField(ProfileFields.FIELD_MIM_COSTUME, count);
            });
        });
        mimCostumeButton = field.findByName("button");
        field.setVisible(false);
        return field;
    }

    private Component initBuyHackField() {
        final JContainer field = createEquipField("Amount of Cash:", () -> {
            final InputDialog<Long> dialog = new InputDialog<>(
                    profileManager.getLongField(ProfileFields.FIELD_CASH),
                    "Input new amount of cash"
            );
            dialog.selectLong(this, (count) -> {
                profileManager.setField(ProfileFields.FIELD_CASH, count);
            });
        });
        buyHackButton = field.findByName("button");
        field.setVisible(false);
        return field;
    }

    private JContainer createEquipField(String text, Runnable clickListener) {
        final VerticalLayout layout = new VerticalLayout();

        final TextLabel label = new TextLabel();
        label.setFont(Resources.getFont());
        label.setForeground(ThemeData.getForegroundColor());
        label.setGravity(Gravity.RIGHT);
        label.setSingleLine(true);
        label.setMinimumSize(new Dimension(120, 0));
        label.setText(text);

        final TextButton button = new TextButton();
        button.setGravity(Gravity.RIGHT);
        button.setMinimumSize(new Dimension(64, 0));
        button.setOnClickListener(clickListener);
        button.setName("button");

        layout.add(label);
        layout.add(button, alignRight(topMargin(6)));

        return layout;
    }

    private void bind() {
        bindWeapons();
        bindItems();
        bindFlags();
        bindSpecials();

        whimsicalStarCountButton.setText(profileManager.getStringOfField(ProfileFields.FIELD_STAR_COUNT));
    }

    private void bindSpecials() {
        final MCI mci = profileManager.getCurrentMCI();
        if (!mci.hasSpecial("VarHack") && mci.hasSpecial("MimHack")) {
            mimCostumeButton.setText(profileManager.getStringOfField(ProfileFields.FIELD_MIM_COSTUME));
            mimCostumeButton.getParent().setVisible(true);
            buyHackButton.getParent().setVisible(false);
        } else if (mci.hasSpecial("BuyHack")) {
            buyHackButton.setText(profileManager.getStringOfField(ProfileFields.FIELD_CASH));
            buyHackButton.getParent().setVisible(true);
            mimCostumeButton.getParent().setVisible(false);
        } else {
            buyHackButton.getParent().setVisible(false);
            mimCostumeButton.getParent().setVisible(false);
        }
    }

    private void bindWeapons() {
        final int currentWeapon = profileManager.getIntField(ProfileFields.FIELD_CURRENT_WEAPON);
        final int size = weaponComponents.size();
        for (int i = 0; i < size; i++) {
            final WeaponComponent component = weaponComponents.get(i);
            component.bind(weaponsFactory.create(i));
            component.setChecked(i == currentWeapon);
        }
    }

    private void bindItems() {
        final int size = itemsComponents.size();

        for (int i = 0; i < size; i++) {
            final InventoryItemComponent component = itemsComponents.get(i);
            component.bind(inventoryItemsFactory.create(i));
        }
    }

    private void bindFlags() {
        for (int key : flagsComponents.keySet()) {
            final LabeledCheckBox checkBox = flagsComponents.get(key);
            checkBox.setChecked(profileManager.getBooleanField(ProfileFields.FIELD_EQUIPS, key));
        }
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.MODIFIED || event == ProfileStateEvent.SLOT_CHANGED) {
            bind();
        }
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.DONE || state == GameResourcesLoadingState.NONE) {
            bind();
        }
    }

    private class WeaponComponentController implements WeaponComponent.ComponentController {
        private final int position;

        private WeaponComponentController(int position) {
            this.position = position;
        }

        @Override
        public void onSetButtonClick(Weapon weapon) {
            new WeaponSelectionDialog(weapon, position, profileManager, weaponsFactory).select();
        }

        @Override
        public void onLevelButtonClick(Weapon weapon) {
            final InputDialog<Integer> dialog = new InputDialog<>(
                    weapon.level,
                    "Input new level value"
            );

            dialog.selectInteger(InventoryPage.this, (level) -> {
                profileManager.setField(ProfileFields.FIELD_WEAPON_LEVELS, position, level);
            });
        }

        @Override
        public void onExpButtonClick(Weapon weapon) {
            final InputDialog<Integer> dialog = new InputDialog<>(
                    weapon.exp,
                    "Input new value for exp points"
            );

            dialog.selectInteger(InventoryPage.this, (exp) -> {
                profileManager.setField(ProfileFields.FIELD_WEAPON_EXP, position, exp);
            });
        }

        @Override
        public void onAmmoButtonClick(Weapon weapon) {
            final InputDialog<Integer> dialog = new InputDialog<>(
                    weapon.currentAmmo,
                    "Input new value for current ammo"
            );

            dialog.selectInteger(InventoryPage.this, (ammo) -> {
                profileManager.setField(ProfileFields.FIELD_WEAPON_CURRENT_AMMO, position, ammo);
            });
        }

        @Override
        public void onMaxAmmoButtonClick(Weapon weapon) {
            final InputDialog<Integer> dialog = new InputDialog<>(
                    weapon.currentAmmo,
                    "Input new value for max ammo"
            );

            dialog.selectInteger(InventoryPage.this, (ammo) -> {
                profileManager.setField(ProfileFields.FIELD_WEAPON_MAXIMUM_AMMO, position, ammo);
            });
        }
    }
}
