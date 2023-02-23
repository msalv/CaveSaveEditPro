package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topLeftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topRightMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.backend.res.GameResources;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.ui.components.RecentFileComponent;
import com.leo.cse.log.AppLogger;
import com.leo.cse.util.ColorUtils;
import com.leo.cse.util.PlayerCharUtils;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.LabeledGroup;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.compound.LabeledRadioButton;
import com.leo.cse.frontend.ui.components.compound.OnCheckedStateChangedListener;
import com.leo.cse.frontend.ui.components.MapComponent;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.editor.selectors.MapSelectionDialog;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.editor.selectors.SavePointsSelectionDialog;
import com.leo.cse.frontend.editor.selectors.SongSelectionDialog;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.JContainer;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;
import com.leo.cse.util.ArrayUtils;
import com.leo.cse.util.StringUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GeneralPage extends JContainer implements
        ProfileStateChangeListener,
        OnGameResourcesLoadingStateChangeListener {
    private final static int DIRECTION_LEFT = 0;
    private final static int DIRECTION_RIGHT = 2;

    private MapComponent mapView;
    private RecentFileComponent noMapDataView;

    private LabeledGroup levelGroup;
    private LabeledGroup positionGroup;
    private LabeledGroup healthGroup;
    private LabeledGroup miscGroup;

    private LabeledGroup viewGroup;
    private LabeledGroup cameraGroup;
    private LabeledGroup tipsGroup;

    private TextButton mapNameButton;
    private TextButton mapSongButton;
    private TextButton savePointsButton;

    private TextButton positionLeftButton;
    private TextButton positionTopButton;
    private TextButton positionXButton;
    private TextButton positionYButton;

    private LabeledRadioButton directionLeftRadioButton;
    private LabeledRadioButton directionRightRadioButton;

    private TextButton snapButton;

    private TextLabel healthValueButton;
    private TextLabel healthMaxValueButton;

    private TextButton refillHealthButton;

    private TextButton miscFramesButton;
    private TextLabel miscInfoLabel;

    private LabeledCheckBox showGridCheckBox;
    private LabeledCheckBox showAboveFgCheckBox;

    private TextLabel cameraPositionLabel;
    private TextLabel cameraExactPositionLabel;

    private TextLabel tipsTileInfoLabel;

    private final DecimalFormat numberFormatter = new DecimalFormat();

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;
    private final Callback callback;
    private final Map<Integer, String> locations = new HashMap<>();

    public GeneralPage(ProfileManager profileManager, GameResourcesManager resourcesManager, Callback callback) {
        super();
        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;
        this.callback = callback;
        initPage();
        initLocations();
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

    private void initLocations() {
        locations.clear();

        final String[] fallback = profileManager.getCurrentMCI().getMapNames();

        if (resourcesManager.hasResources()) {
            final GameResources resources = resourcesManager.getResources();
            for (int i = 0; i < resources.getMapInfoCount(); i++) {
                final MapInfo mi = resources.getMapInfo(i);
                final String mapName = StringUtils.isNullOrEmpty(mi.getMapName())
                        ? ArrayUtils.getOrDefault(fallback, i, "")
                        : mi.getMapName();
                locations.put(i, mapName);
            }
        } else {
            for (int i = 0; i < fallback.length; i++) {
                locations.put(i, fallback[i]);
            }
        }
    }

    private void initPage() {
        setBorder(new EmptyBorder(16, 17, 16, 17));

        initMap();
        initLevelGroup();
        initSavePointsButton();

        initPositionGroup();
        initSnapToGridButton();

        initHealthGroup();
        initRefillHealthButton();

        initMiscGroup();

        initViewGroup();
        initCameraGroup();
        initTipsGroup();
    }

    private void initMap() {
        add(initNoMapDataView(), topRightMargin(16, 16));
        add(initMapView(), topRightMargin(16, 16));
    }

    private Component initMapView() {
        mapView = new MapComponent(resourcesManager);
        mapView.setOnCameraChangeListener(this::bindCameraPosition);

        mapView.setOnPlayerPositionChangedListener((x, y) -> {
            profileManager.setField(
                    ProfileFields.FIELD_POSITION,
                    new Short[] { x, y }
            );
        });

        mapView.setCharacterImageProvider((resourcesManager, slotId, isDirectionRightToLeft) -> {
            return PlayerCharUtils.getCharacterImage(profileManager, resourcesManager, slotId, isDirectionRightToLeft);
        });

        mapView.setFlagValueProvider((flagId -> profileManager.getBooleanField(ProfileFields.FIELD_FLAGS, flagId)));
        mapView.setEntityExtrasProvider(entry -> profileManager.getCurrentMCI().getEntityExtras(entry));
        mapView.setPreferredSize(new Dimension(640, 480));

        return mapView;
    }

    private Component initNoMapDataView() {
        noMapDataView = new RecentFileComponent();
        noMapDataView.setBorder(new EmptyBorder(12, 12, 12, 12));
        noMapDataView.setPreferredSize(new Dimension(370, 206));

        noMapDataView.setStateText("There are no game resources loaded yet");
        noMapDataView.setTitle("Recent:");
        noMapDataView.setStateColor(ThemeData.getForegroundColor());
        noMapDataView.setButtonText("Load...");

        final TextLabel resParagraph1 = new TextLabel();
        resParagraph1.setFont(Resources.getFont());
        resParagraph1.setTextColor(ThemeData.getForegroundColor());
        resParagraph1.setText("Select one of these files according to your version of the game:");

        noMapDataView.addContentComponent(resParagraph1, null);

        final String[] files = {
                "\u2022 Cave Story (freeware) - Doukutsu.exe",
                "\u2022 Cave Story+ - stage.tbl",
                "\u2022 NXEngine/NXEngine-evo - stage.dat",
                "\u2022 Doukutsu-rs/CSE2E - mrmap.bin"
        };

        for (String file : files) {
            final TextLabel resParagraph = new TextLabel();
            resParagraph.setFont(Resources.getFont());
            resParagraph.setTextColor(ThemeData.getForegroundColor());
            resParagraph.setText(file);
            noMapDataView.addContentComponent(resParagraph, topLeftMargin(2, 6));
        }

        final TextLabel resParagraph2 = new TextLabel();
        resParagraph2.setFont(Resources.getFont());
        resParagraph2.setTextColor(ThemeData.getForegroundColor());
        resParagraph2.setText("One of these files should be located in the game folder or in the \u2018data\u2019 subfolder.");

        noMapDataView.addContentComponent(resParagraph2, topMargin(12));

        noMapDataView.setButtonClickListener(callback::onLoadResourcesButtonClicked);

        noMapDataView.setPathClickListener(this::loadRecentGameResources);
        noMapDataView.setBorder(new CompoundBorder(
                new LineBorder(ThemeData.getForegroundColor()),
                noMapDataView.getBorder()
        ));

        noMapDataView.setStateColor(ThemeData.getTextColor());

        return noMapDataView;
    }

    private void initLevelGroup() {
        final LabeledGroup group = new LabeledGroup();

        group.setLabelText("Level");

        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        // map

        final TextLabel mapLabel = new TextLabel();
        mapLabel.setFont(Resources.getFont());
        mapLabel.setForeground(ThemeData.getForegroundColor());
        mapLabel.setSingleLine(true);
        mapLabel.setText("Map:");

        final TextButton mapValue = new TextButton();
        mapValue.setGravity(Gravity.LEFT);
        mapValue.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        // song

        final TextLabel songLabel = new TextLabel();
        songLabel.setFont(Resources.getFont());
        songLabel.setForeground(ThemeData.getForegroundColor());
        songLabel.setSingleLine(true);
        songLabel.setText("Song:");

        final TextButton songValue = new TextButton();
        songValue.setGravity(Gravity.LEFT);
        songValue.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        content.add(mapLabel);
        content.add(mapValue, topMargin(3));

        content.add(songLabel, topMargin(6));
        content.add(songValue, topMargin(3));

        mapValue.setOnClickListener(() -> {
            new MapSelectionDialog(profileManager, resourcesManager).select();
        });

        songValue.setOnClickListener(() -> {
            new SongSelectionDialog(profileManager).select();
        });

        group.setContent(content);

        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        mapNameButton = mapValue;
        mapSongButton = songValue;

        levelGroup = group;

        add(group);
    }

    private void loadRecentGameResources() {
        final String path = Config.get(Config.KEY_LAST_MOD, "");
        final File file = new File(path);
        if (file.exists()) {
            try {
                resourcesManager.load(file, null);
            } catch (Exception e) {
                AppLogger.error("Unable to load recent resources: " + file, e);
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load recent resources:\n" + file,
                    "File not found",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void initSavePointsButton() {
        savePointsButton = new TextButton();
        savePointsButton.setText("Save Points...");
        savePointsButton.setMinimumSize(new Dimension(88, 0));

        savePointsButton.setOnClickListener(() -> {
            new SavePointsSelectionDialog(profileManager, resourcesManager).select();
        });

        add(savePointsButton, topRightMargin(12, 10));
    }

    private void initPositionGroup() {
        final LabeledGroup group = new LabeledGroup();
        final HorizontalLayout content = new HorizontalLayout();

        group.setLabelText("Position");

        content.add(initLocationGroup());
        content.add(initCoordinatesGroup(), leftMargin(16));
        content.add(initDirectionGroup(), leftMargin(16));

        group.setContent(content);

        group.setMinimumSize(new Dimension(0, Integer.MAX_VALUE));

        positionGroup = group;

        add(group, leftMargin(16));
    }

    private Component initLocationGroup() {
        final VerticalLayout content = new VerticalLayout();

        // left

        final TextLabel leftLabel = new TextLabel();
        leftLabel.setFont(Resources.getFont());
        leftLabel.setForeground(ThemeData.getForegroundColor());
        leftLabel.setSingleLine(true);
        leftLabel.setText("Left:");

        final TextButton leftValue = new TextButton();
        leftValue.setGravity(Gravity.RIGHT);
        leftValue.setMinimumSize(new Dimension(85, 0));

        // top

        final TextLabel topLabel = new TextLabel();
        topLabel.setFont(Resources.getFont());
        topLabel.setForeground(ThemeData.getForegroundColor());
        topLabel.setSingleLine(true);
        topLabel.setText("Top:");

        final TextButton topValue = new TextButton();
        topValue.setGravity(Gravity.RIGHT);
        topValue.setMinimumSize(new Dimension(85, 0));

        content.add(leftLabel);
        content.add(leftValue, topMargin(3));

        content.add(topLabel, topMargin(6));
        content.add(topValue, topMargin(3));

        leftValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    (short)(profileManager.getShortField(ProfileFields.FIELD_X_POSITION) / 32),
                    "Input new value for left position"
            );
            dialog.selectShort(this, (left) -> {
                profileManager.setField(ProfileFields.FIELD_X_POSITION, (short)(left * 32));
            });
        });

        topValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    (short)(profileManager.getShortField(ProfileFields.FIELD_Y_POSITION) / 32),
                    "Input new value for top position"
            );
            dialog.selectShort(this, (top) -> {
                profileManager.setField(ProfileFields.FIELD_Y_POSITION, (short)(top * 32));
            });
        });

        positionLeftButton = leftValue;
        positionTopButton = topValue;

        return content;
    }

    private Component initCoordinatesGroup() {
        final VerticalLayout content = new VerticalLayout();

        // x

        final TextLabel xLabel = new TextLabel();
        xLabel.setFont(Resources.getFont());
        xLabel.setForeground(ThemeData.getForegroundColor());
        xLabel.setSingleLine(true);
        xLabel.setText("X:");

        final TextButton xValue = new TextButton();
        xValue.setGravity(Gravity.RIGHT);
        xValue.setMinimumSize(new Dimension(85, 0));

        // y

        final TextLabel yLabel = new TextLabel();
        yLabel.setFont(Resources.getFont());
        yLabel.setForeground(ThemeData.getForegroundColor());
        yLabel.setSingleLine(true);
        yLabel.setText("Y:");

        final TextButton yValue = new TextButton();
        yValue.setGravity(Gravity.RIGHT);
        yValue.setMinimumSize(new Dimension(85, 0));

        content.add(xLabel);
        content.add(xValue, topMargin(3));

        content.add(yLabel, topMargin(6));
        content.add(yValue, topMargin(3));

        xValue.setOnClickListener(() -> {
            final int density = profileManager.getCurrentMCI().getGraphicsDensity();

            final InputDialog<Short> dialog = new InputDialog<>(
                    (short)(profileManager.getShortField(ProfileFields.FIELD_X_POSITION) / (2f / density)),
                    "Input new value for x coordinate"
            );
            dialog.selectShort(this, (x) -> {
                profileManager.setField(ProfileFields.FIELD_X_POSITION, (short)(x * (2f / density)));
            });
        });

        yValue.setOnClickListener(() -> {
            final int density = profileManager.getCurrentMCI().getGraphicsDensity();

            final InputDialog<Short> dialog = new InputDialog<>(
                    (short)(profileManager.getShortField(ProfileFields.FIELD_Y_POSITION) / (2f / density)),
                    "Input new value for y coordinate"
            );
            dialog.selectShort(this, (y) -> {
                profileManager.setField(ProfileFields.FIELD_Y_POSITION, (short)(y * (2f / density)));
            });
        });

        positionXButton = xValue;
        positionYButton = yValue;

        return content;
    }

    private Component initDirectionGroup() {
        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(62, 0));

        // direction

        final TextLabel directionLabel = new TextLabel();
        directionLabel.setFont(Resources.getFont());
        directionLabel.setForeground(ThemeData.getForegroundColor());
        directionLabel.setSingleLine(true);
        directionLabel.setText("Direction:");

        final LabeledRadioButton leftRadioButton = new LabeledRadioButton();
        leftRadioButton.setText("Left");

        final LabeledRadioButton rightRadioButton = new LabeledRadioButton();
        rightRadioButton.setText("Right");

        final OnCheckedStateChangedListener listener = (button, isChecked) -> {
            if (isChecked) {
                final int direction;
                if (button == leftRadioButton) {
                    rightRadioButton.setChecked(false);
                    direction = DIRECTION_LEFT;
                } else {
                    leftRadioButton.setChecked(false);
                    direction = DIRECTION_RIGHT;
                }
                profileManager.setField(ProfileFields.FIELD_DIRECTION, direction);
            }
        };

        leftRadioButton.setOnCheckedStateListener(listener);
        rightRadioButton.setOnCheckedStateListener(listener);

        content.add(directionLabel);
        content.add(leftRadioButton, topMargin(8));
        content.add(rightRadioButton, topMargin(6));

        directionLeftRadioButton = leftRadioButton;
        directionRightRadioButton = rightRadioButton;

        return content;
    }

    private void initSnapToGridButton() {
        snapButton = new TextButton();
        snapButton.setText("Snap to Grid");
        snapButton.setMinimumSize(new Dimension(88, 0));

        snapButton.setOnClickListener(this::snapToGrid);

        add(snapButton, topRightMargin(12, 10));
    }

    private void initHealthGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("Health");

        // hp

        final TextLabel hpLabel = new TextLabel();
        hpLabel.setFont(Resources.getFont());
        hpLabel.setForeground(ThemeData.getForegroundColor());
        hpLabel.setSingleLine(true);
        hpLabel.setText("HP:");

        final TextButton hpValue = new TextButton();
        hpValue.setGravity(Gravity.RIGHT);
        hpValue.setMinimumSize(new Dimension(100, 0));

        // max hp

        final TextLabel maxHpLabel = new TextLabel();
        maxHpLabel.setFont(Resources.getFont());
        maxHpLabel.setForeground(ThemeData.getForegroundColor());
        maxHpLabel.setSingleLine(true);
        maxHpLabel.setText("Max HP:");

        final TextButton maxHpValue = new TextButton();
        maxHpValue.setGravity(Gravity.RIGHT);
        maxHpValue.setMinimumSize(new Dimension(100, 0));

        content.add(hpLabel);
        content.add(hpValue, topMargin(3));

        content.add(maxHpLabel, topMargin(6));
        content.add(maxHpValue, topMargin(3));

        hpValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_CURRENT_HEALTH),
                    "Input new current health value"
            );
            dialog.selectShort(this, (hp) -> {
                profileManager.setField(ProfileFields.FIELD_CURRENT_HEALTH, hp);
            });
        });

        maxHpValue.setOnClickListener(() -> {
            final InputDialog<Short> dialog = new InputDialog<>(
                    profileManager.getShortField(ProfileFields.FIELD_MAXIMUM_HEALTH),
                    "Input new max health value"
            );
            dialog.selectShort(this, (hp) -> {
                profileManager.setField(ProfileFields.FIELD_MAXIMUM_HEALTH, hp);
            });
        });

        group.setContent(content);

        healthGroup = group;

        healthValueButton = hpValue;
        healthMaxValueButton = maxHpValue;

        add(group, leftMargin(16));
    }

    private void initRefillHealthButton() {
        refillHealthButton = new TextButton();
        refillHealthButton.setText("Refill");
        refillHealthButton.setMinimumSize(new Dimension(72, 0));

        refillHealthButton.setOnClickListener(this::refillHealth);

        add(refillHealthButton, topRightMargin(12, 10));
    }

    private void initMiscGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("Misc");

        // frames passed

        final TextLabel framesLabel = new TextLabel();
        framesLabel.setFont(Resources.getFont());
        framesLabel.setForeground(ThemeData.getForegroundColor());
        framesLabel.setSingleLine(true);
        framesLabel.setText("Frames Passed:");

        final TextButton framesValue = new TextButton();
        framesValue.setGravity(Gravity.RIGHT);
        framesValue.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        // note

        final TextLabel noteLabel = new TextLabel();
        noteLabel.setFont(Resources.getFont());
        noteLabel.setForeground(ThemeData.getForegroundColor());
        noteLabel.setGravity(Gravity.BOTTOM);
        noteLabel.setMinimumSize(new Dimension(0, Integer.MAX_VALUE));

        content.add(framesLabel);
        content.add(framesValue, topMargin(3));
        content.add(noteLabel);

        framesValue.setOnClickListener(() -> {
            final int frames = profileManager.getIntField(ProfileFields.FIELD_TIME_PLAYED);

            final InputDialog<Integer> dialog = new InputDialog<>(
                    frames,
                    "Input new value for frames passed"
            );
            dialog.selectInteger(this, (time) -> {
                profileManager.setField(ProfileFields.FIELD_TIME_PLAYED, time);
            });
        });

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        miscGroup = group;

        miscFramesButton = framesValue;
        miscInfoLabel = noteLabel;

        add(group, leftMargin(16));
    }

    private void initViewGroup() {
        final LabeledGroup group = new LabeledGroup();

        group.setLabelText("View");

        final VerticalLayout content = new VerticalLayout();

        final LabeledCheckBox showGridCheckBox = new LabeledCheckBox();
        showGridCheckBox.setText("Show Grid");
        content.add(showGridCheckBox);

        final LabeledCheckBox showAboveFgCheckBox = new LabeledCheckBox();
        showAboveFgCheckBox.setText("Show Player Above Foreground");
        content.add(showAboveFgCheckBox, topMargin(4));

        showGridCheckBox.setOnCheckedStateListener((button, isChecked) -> {
            Config.setBoolean(Config.KEY_SHOW_MAP_GRID, isChecked);
            mapView.setGridVisible(isChecked);
        });

        showAboveFgCheckBox.setOnCheckedStateListener((button, isChecked) -> {
            Config.setBoolean(Config.KEY_SHOW_PLAYER_ABOVE_FG, isChecked);
            mapView.setDrawCharacterAboveForeground(isChecked);
        });

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        viewGroup = group;

        this.showGridCheckBox = showGridCheckBox;
        this.showAboveFgCheckBox = showAboveFgCheckBox;

        add(group, topMargin(-6));
    }

    private void initCameraGroup() {
        final LabeledGroup group = new LabeledGroup();

        group.setLabelText("Camera");

        final TextLabel positionLabel = new TextLabel();
        positionLabel.setFont(Resources.getFont());
        positionLabel.setForeground(ThemeData.getForegroundColor());
        positionLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        final TextLabel exactPositionLabel = new TextLabel();
        exactPositionLabel.setFont(Resources.getFont());
        exactPositionLabel.setForeground(ThemeData.getForegroundColor());
        exactPositionLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        final VerticalLayout content = new VerticalLayout();
        content.add(positionLabel);
        content.add(exactPositionLabel);

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        cameraGroup = group;

        cameraPositionLabel = positionLabel;
        cameraExactPositionLabel = exactPositionLabel;

        add(group, topMargin(16));
    }

    private void initTipsGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("\u263C Quick Tips");

        final String[] labels = {
                "\nArrow keys modifiers:",
                "\u2022 Shift - 1/2 tile",
                "\u2022 Ctrl - 1/4 tile",
                "\u2022 Ctrl+Shift - 1 px",
                "\nHold Shift while dragging to snap player to grid"
        };

        final TextLabel movementLabel = new TextLabel();
        movementLabel.setFont(Resources.getFont());
        movementLabel.setForeground(ThemeData.getForegroundColor());
        movementLabel.setText("Move player by clicking, dragging, or using arrow keys");

        final TextLabel tileInfoLabel = new TextLabel();
        tileInfoLabel.setFont(Resources.getFont());
        tileInfoLabel.setForeground(ThemeData.getForegroundColor());

        content.add(movementLabel);
        content.add(tileInfoLabel, topMargin(16));

        for (String text : labels) {
            final TextLabel label = new TextLabel();
            label.setFont(Resources.getFont());
            label.setForeground(ThemeData.getForegroundColor());
            label.setText(text.trim());

            content.add(label, text.startsWith("\n") ? topMargin(12) : null);
        }

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        tipsGroup = group;

        tipsTileInfoLabel = tileInfoLabel;

        add(group, topMargin(16));
    }

    private void bind() {
        final MCI mci = profileManager.getCurrentMCI();

        bindMapName();

        final int song = profileManager.getIntField(ProfileFields.FIELD_SONG);
        mapSongButton.setText(String.format("%d - %s", song, mci.getSongName(song)));

        final short x = profileManager.getShortField(ProfileFields.FIELD_X_POSITION);
        final short y = profileManager.getShortField(ProfileFields.FIELD_Y_POSITION);

        final int density = mci.getGraphicsDensity();

        positionLeftButton.setText(numberFormatter.format(x / 32));
        positionTopButton.setText(numberFormatter.format(y / 32));
        positionXButton.setText(numberFormatter.format((int)(x / (2f / density) + 0.5f)));
        positionYButton.setText(numberFormatter.format((int)(y / (2f / density) + 0.5f)));

        final int direction = profileManager.getIntField(ProfileFields.FIELD_DIRECTION);
        directionLeftRadioButton.setChecked(direction == DIRECTION_LEFT);
        directionRightRadioButton.setChecked(direction == DIRECTION_RIGHT);

        final short currentHealth = profileManager.getShortField(ProfileFields.FIELD_CURRENT_HEALTH);
        final short maxHealth = profileManager.getShortField(ProfileFields.FIELD_MAXIMUM_HEALTH);

        healthValueButton.setText(numberFormatter.format(currentHealth));
        healthMaxValueButton.setText(numberFormatter.format(maxHealth));
        refillHealthButton.setEnabled(currentHealth < maxHealth);

        final int frames = profileManager.getIntField(ProfileFields.FIELD_TIME_PLAYED);
        final int fps = mci.getFPS();

        miscFramesButton.setText(numberFormatter.format(frames));
        miscInfoLabel.setText(String.format("Approx. %s seconds played", numberFormatter.format(frames / fps)));

        showGridCheckBox.setChecked(Config.getBoolean(Config.KEY_SHOW_MAP_GRID, false));
        showAboveFgCheckBox.setChecked(Config.getBoolean(Config.KEY_SHOW_PLAYER_ABOVE_FG, true));

        mapView.setGridVisible(showGridCheckBox.isChecked());
        mapView.setDrawCharacterAboveForeground(showAboveFgCheckBox.isChecked());
        mapView.setCurrentMapId(profileManager.getCurrentMapId());
        mapView.setPlayerPosition(profileManager.getPlayerPosition());
        mapView.setResolution(density);
        mapView.setSlotId(profileManager.getCurrentSlotId());
        mapView.setDirection(profileManager.getIntField(ProfileFields.FIELD_DIRECTION) == DIRECTION_RIGHT);

        bindCameraPosition(mapView.getCameraX(), mapView.getCameraY());
        bindNoMapDataView();

        tipsTileInfoLabel.setText(String.format("1 tile = %1$dx%1$d px", 16 * density));

        savePointsButton.setEnabled(resourcesManager.hasResources() && resourcesManager.shouldLoadNpc());
    }

    private void bindNoMapDataView() {
        if (!resourcesManager.hasResources()) {
            final String recentResourcesPath = Config.get(Config.KEY_LAST_MOD, "");
            noMapDataView.setRecentFilePath(recentResourcesPath);
            noMapDataView.setVisible(true);
        } else {
            noMapDataView.setVisible(false);
        }
    }

    private void bindMapName() {
        final int mapId = profileManager.getIntField(ProfileFields.FIELD_MAP);
        mapNameButton.setText(String.format("%d - %s", mapId, locations.getOrDefault(mapId, "")));
    }

    private void bindCameraPosition(int x, int y) {
        final MCI mci = profileManager.getCurrentMCI();
        final int density = mci.getGraphicsDensity();

        cameraPositionLabel.setText(String.format("Position: (%d, %d)", x / 32, y / 32));
        cameraExactPositionLabel.setText(String.format("Exact: (%d, %d)", x / 2 / density, y / 2 / density));
    }

    private void snapToGrid() {
        final Short[] position = profileManager.getPlayerPosition();
        position[0] = (short)(Math.round(position[0] / 32f) * 32);
        position[1] = (short)(Math.round(position[1] / 32f) * 32);
        profileManager.setField(ProfileFields.FIELD_POSITION, position);
    }

    private void refillHealth() {
        final short maxHealth = profileManager.getShortField(ProfileFields.FIELD_MAXIMUM_HEALTH);
        profileManager.setField(ProfileFields.FIELD_CURRENT_HEALTH, maxHealth);
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int availHeight = maxHeight - (insets.bottom + insets.top);

        // map

        Dimension mapSize = measureChild(mapView, availWidth, availHeight);
        LayoutConstraints mapConstraints = getChildConstraints(mapView);

        if (noMapDataView.isVisible()) {
            measureChild(noMapDataView, mapSize.width, mapSize.height);
        }

        Dimension size;
        LayoutConstraints lc;

        // measure side panel

        final int availSidePanelWidth = availWidth - mapSize.width - mapConstraints.getHorizontalMargins();
        int availSidePanelHeight = availHeight;

        if (viewGroup.isVisible()) {
            lc = getChildConstraints(viewGroup);
            size = measureChild(viewGroup, availSidePanelWidth, availSidePanelHeight - lc.getVerticalMargins());

            availSidePanelHeight -= size.height + lc.getVerticalMargins();
        }

        if (cameraGroup.isVisible()) {
            lc = getChildConstraints(cameraGroup);
            size = measureChild(cameraGroup, availSidePanelWidth, availSidePanelHeight - lc.getVerticalMargins());

            availSidePanelHeight -= size.height + lc.getVerticalMargins();
        }

        if (tipsGroup.isVisible()) {
            lc = getChildConstraints(tipsGroup);
            size = measureChild(tipsGroup, availSidePanelWidth, availSidePanelHeight - lc.getVerticalMargins());

            availSidePanelHeight -= size.height + lc.getVerticalMargins();
        }

        // measure top panel from right to left

        int availTopPanelWidth = availWidth;
        final int availTopPanelHeight = availHeight - mapSize.height - mapConstraints.getVerticalMargins();

        int topPanelGroupHeight = availTopPanelHeight;

        if (healthGroup.isVisible()) {
            lc = getChildConstraints(healthGroup);
            size = measureChild(healthGroup, availTopPanelWidth - lc.getHorizontalMargins(), availTopPanelHeight);

            topPanelGroupHeight = size.height;

            availTopPanelWidth -= size.width + lc.getHorizontalMargins();

            if (refillHealthButton.isVisible()) {
                lc = getChildConstraints(refillHealthButton);
                size = measureChild(refillHealthButton, size.width - lc.getHorizontalMargins(), availTopPanelHeight - topPanelGroupHeight);
            }
        }

        if (miscGroup.isVisible()) {
            lc = getChildConstraints(miscGroup);
            size = measureChild(miscGroup, availSidePanelWidth, topPanelGroupHeight);

            availTopPanelWidth -= size.width + lc.getHorizontalMargins();
        }

        if (positionGroup.isVisible()) {
            lc = getChildConstraints(positionGroup);
            size = measureChild(positionGroup, availTopPanelWidth - lc.getHorizontalMargins(), topPanelGroupHeight);

            availTopPanelWidth -= size.width + lc.getHorizontalMargins();

            if (snapButton.isVisible()) {
                lc = getChildConstraints(snapButton);
                size = measureChild(snapButton, size.width - lc.getHorizontalMargins(), availTopPanelHeight - topPanelGroupHeight);
            }
        }

        if (levelGroup.isVisible()) {
            size = measureChild(levelGroup, availTopPanelWidth, topPanelGroupHeight);
            lc = getChildConstraints(levelGroup);

            if (savePointsButton.isVisible()) {
                lc = getChildConstraints(savePointsButton);
                size = measureChild(savePointsButton, size.width - lc.getHorizontalMargins(), availTopPanelHeight - topPanelGroupHeight);
            }
        }

        setMeasuredDimensions(maxWidth, maxHeight);
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int top = insets.top;
        int right = container.getWidth() - insets.right;
        int bottom = container.getHeight() - insets.bottom;

        final Dimension mapSize = getChildDimension(mapView);
        final LayoutConstraints mapConstraints = getChildConstraints(mapView);

        mapView.setBounds(
                left + mapConstraints.leftMargin,
                bottom - mapConstraints.bottomMargin - mapSize.height,
                mapSize.width,
                mapSize.height
        );

        Dimension size;
        LayoutConstraints lc;

        if (noMapDataView.isVisible()) {
            size = getChildDimension(noMapDataView);
            noMapDataView.setBounds(
                    mapView.getX() + (mapView.getWidth() - size.width) / 2,
                    mapView.getY() + (mapView.getHeight() - size.height) / 2,
                    size.width,
                    size.height
            );
        }

        // layout side panel
        final int sideX = mapView.getX() + mapSize.width + mapConstraints.getHorizontalMargins();
        int sideY = mapView.getY();

        if (viewGroup.isVisible()) {
            size = getChildDimension(viewGroup);
            lc = getChildConstraints(viewGroup);

            viewGroup.setBounds(
                    sideX,
                    sideY + lc.topMargin,
                    size.width,
                    size.height
            );

            sideY += size.height + lc.getVerticalMargins();
        }

        if (cameraGroup.isVisible()) {
            size = getChildDimension(cameraGroup);
            lc = getChildConstraints(cameraGroup);

            cameraGroup.setBounds(
                    sideX,
                    sideY + lc.topMargin,
                    size.width,
                    size.height
            );

            sideY += size.height + lc.getVerticalMargins();
        }

        if (tipsGroup.isVisible()) {
            size = getChildDimension(tipsGroup);
            lc = getChildConstraints(tipsGroup);

            tipsGroup.setBounds(
                    sideX,
                    sideY + lc.topMargin,
                    size.width,
                    size.height
            );

            sideY += size.height + lc.getVerticalMargins();
        }

        // layout top panel from right to left

        int topX = right;

        if (miscGroup.isVisible()) {
            size = getChildDimension(miscGroup);
            lc = getChildConstraints(miscGroup);

            miscGroup.setBounds(
                    topX - size.width - lc.rightMargin,
                    top + lc.topMargin,
                    size.width,
                    size.height
            );

            topX -= size.width + lc.getHorizontalMargins();
        }

        if (healthGroup.isVisible()) {
            final Dimension healthSize = getChildDimension(healthGroup);
            final LayoutConstraints healthLc = getChildConstraints(healthGroup);

            final int healthX = topX - healthSize.width - healthLc.rightMargin;
            final int healthY = top + healthLc.topMargin;

            healthGroup.setBounds(
                    healthX,
                    healthY,
                    healthSize.width,
                    healthSize.height
            );

            topX -= healthSize.width + healthLc.getHorizontalMargins();

            if (refillHealthButton.isVisible()) {
                size = getChildDimension(refillHealthButton);
                lc = getChildConstraints(refillHealthButton);

                refillHealthButton.setBounds(
                        healthX + healthSize.width - size.width - lc.rightMargin,
                        healthY + healthSize.height + lc.topMargin,
                        size.width,
                        size.height
                );
            }
        }

        if (positionGroup.isVisible()) {
            final Dimension positionSize = getChildDimension(positionGroup);
            final LayoutConstraints positionLc = getChildConstraints(positionGroup);

            final int x = topX - positionSize.width - positionLc.rightMargin;
            final int y = top + positionLc.topMargin;

            positionGroup.setBounds(
                    x,
                    y,
                    positionSize.width,
                    positionSize.height
            );

            topX -= positionSize.width + positionLc.getHorizontalMargins();

            if (snapButton.isVisible()) {
                size = getChildDimension(snapButton);
                lc = getChildConstraints(snapButton);

                snapButton.setBounds(
                        x + positionSize.width - size.width - lc.rightMargin,
                        y + positionSize.height + lc.topMargin,
                        size.width,
                        size.height
                );
            }
        }

        if (levelGroup.isVisible()) {
            final Dimension levelSize = getChildDimension(levelGroup);
            final LayoutConstraints levelLc = getChildConstraints(levelGroup);

            final int x = topX - levelSize.width - levelLc.rightMargin;
            final int y = top + levelLc.topMargin;

            levelGroup.setBounds(
                    x,
                    y,
                    levelSize.width,
                    levelSize.height
            );

            if (savePointsButton.isVisible()) {
                size = getChildDimension(savePointsButton);
                lc = getChildConstraints(savePointsButton);

                savePointsButton.setBounds(
                        x + levelSize.width - size.width - lc.rightMargin,
                        y + levelSize.height + lc.topMargin,
                        size.width,
                        size.height
                );
            }
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
            mapView.repaint();
            savePointsButton.setEnabled(resourcesManager.hasResources() && resourcesManager.shouldLoadNpc());
            initLocations();
            bindMapName();
            bindNoMapDataView();
        }
    }

    public interface Callback {
        void onLoadResourcesButtonClicked();
    }
}
