package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfilePointers;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.LabeledGroup;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.ui.layout.GridLayout;
import com.leo.cse.frontend.ui.layout.HorizontalSpreadLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.ArrayUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.EmptyBorder;

public class VariablesPage extends VerticalLayout implements ProfileStateChangeListener {
    private final static String[] PHYSICS_NAMES = {
            "Max Walk Speed", "Max Fall Speed", "Gravity", "Alt Gravity",
            "Walk Accel", "Jump Control", "Friction", "Jump Force"
    };

    private final GridLayout variablesGrid = new GridLayout();
    private final HorizontalSpreadLayout physicsContainer = new HorizontalSpreadLayout();
    private final LabeledCheckBox physicsWaterCheckbox = new LabeledCheckBox();

    private final Map<Integer, TextButton> variablesButtons = new HashMap<>();
    private final TextButton[] physicsButtons = new TextButton[PHYSICS_NAMES.length * 2];

    private final ProfileManager profileManager;

    public VariablesPage(ProfileManager profileManager) {
        super();
        this.profileManager = profileManager;
        initPage();
        bind();
    }

    @Override
    public void addNotify() {
        profileManager.addListener(this);
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        super.removeNotify();
    }

    private void initPage() {
        setBorder(new EmptyBorder(14, 22, 8, 22));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        add(initVariablesGrid());
        add(initPhysicsContainer(), topMargin(16));
        add(initPhysicsWaterCheckbox(), topMargin(14));
    }

    private Component initVariablesGrid() {
        variablesGrid.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        variablesGrid.setVerticalGap(6);
        variablesGrid.setHorizontalGap(16);
        variablesGrid.setSpanCount(11);

        for (int i = 0; i < ProfilePointers.EXT_VARIABLES_LENGTH; ++i) {
            if (i < 8 && i != 6) {
                continue;
            }
            variablesGrid.add(initVarComponent(i));
        }

        return variablesGrid;
    }

    private Component initPhysicsContainer() {
        physicsContainer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
        physicsContainer.setGap(8);

        for (int i = 0; i < PHYSICS_NAMES.length; ++i) {
            physicsContainer.add(initPhysicsComponent(i));
        }

        return physicsContainer;
    }

    private Component initPhysicsWaterCheckbox() {
        physicsWaterCheckbox.setText("Water doesn't cause splash and trigger air timer");

        physicsWaterCheckbox.setOnCheckedStateListener((component, isChecked) -> {
            profileManager.setField(ProfileFields.FIELD_PHYSICS_VARIABLES, ProfilePointers.EXT_PHYSICS_VARS_LENGTH - 1, (short) (isChecked ? 1 : 0));
        });

        return physicsWaterCheckbox;
    }

    private Component initVarComponent(int varId) {
        final VerticalLayout content = new VerticalLayout();

        final TextLabel label = new TextLabel();
        label.setFont(Resources.getFont());
        label.setForeground(ThemeData.getForegroundColor());
        label.setSingleLine(true);
        label.setText(String.format("VAR: %d", varId));

        final TextButton button = new TextButton();
        button.setGravity(Gravity.RIGHT);
        button.setMinimumSize(new Dimension(60, 19));

        content.add(label);
        content.add(button, topMargin(3));

        button.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_VARIABLES, varId),
                    String.format("Input value for var %d", varId)
            );
            dialog.selectShort(this, (value) -> {
                profileManager.setField(ProfileFields.FIELD_VARIABLES, varId, value);
            });
        });

        variablesButtons.put(varId, button);

        return content;
    }

    private Component initPhysicsComponent(int id) {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText(PHYSICS_NAMES[id]);

        // Ground

        final TextLabel groundLabel = new TextLabel();
        groundLabel.setFont(Resources.getFont());
        groundLabel.setForeground(ThemeData.getForegroundColor());
        groundLabel.setSingleLine(true);
        groundLabel.setText("Ground:");

        final TextButton groundValue = new TextButton();
        groundValue.setGravity(Gravity.RIGHT);
        groundValue.setMinimumSize(new Dimension(75, 19));

        // Water

        final TextLabel waterLabel = new TextLabel();
        waterLabel.setFont(Resources.getFont());
        waterLabel.setForeground(ThemeData.getForegroundColor());
        waterLabel.setSingleLine(true);
        waterLabel.setText("Water:");

        final TextButton waterValue = new TextButton();
        waterValue.setGravity(Gravity.RIGHT);
        waterValue.setMinimumSize(new Dimension(75, 19));

        content.add(groundLabel);
        content.add(groundValue, topMargin(3));

        content.add(waterLabel, topMargin(6));
        content.add(waterValue, topMargin(3));

        groundValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_PHYSICS_VARIABLES, id),
                    String.format("Input new value for %s (ground)", PHYSICS_NAMES[id])
            );
            dialog.selectShort(this, (value) -> {
                profileManager.setField(ProfileFields.FIELD_PHYSICS_VARIABLES, id, value);
            });
        });

        waterValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_PHYSICS_VARIABLES, id + 8),
                    String.format("Input new value for %s (water)", PHYSICS_NAMES[id])
            );
            dialog.selectShort(this, (value) -> {
                profileManager.setField(ProfileFields.FIELD_PHYSICS_VARIABLES, id + 8, value);
            });
        });

        physicsButtons[id] = groundValue;
        physicsButtons[id + 8] = waterValue;

        group.setContent(content);

        return group;
    }

    private void bind() {
        for (int i = 0; i < ProfilePointers.EXT_VARIABLES_LENGTH; ++i) {
            final TextButton button = variablesButtons.get(i);
            if (button != null) {
                button.setText(String.valueOf(profileManager.getShortField(ProfileFields.FIELD_VARIABLES, i)));
            }
        }
        for (int i = 0; i < ProfilePointers.EXT_PHYSICS_VARS_LENGTH - 1; ++i) {
            final TextButton button = ArrayUtils.getOrDefault(physicsButtons, i, null);
            if (button != null) {
                button.setText(String.valueOf(profileManager.getShortField(ProfileFields.FIELD_PHYSICS_VARIABLES, i)));
            }
        }
        physicsWaterCheckbox.setChecked(profileManager.getShortField(ProfileFields.FIELD_PHYSICS_VARIABLES, ProfilePointers.EXT_PHYSICS_VARS_LENGTH - 1) == 1);

        final boolean hasPhysVarHack = profileManager.getCurrentMCI().hasSpecial("PhysVarHack");
        physicsContainer.setVisible(hasPhysVarHack);
        physicsWaterCheckbox.setVisible(hasPhysVarHack);
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.MODIFIED) {
            bind();
        }
    }
}
