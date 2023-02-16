package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.mci.MCI;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.dialogs.niku.BestTimeSelectionDialog;
import com.leo.cse.frontend.dialogs.niku.NikuInfoDialog;
import com.leo.cse.frontend.format.DurationFormatter;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.dto.Difficulty;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfilePointers;
import com.leo.cse.dto.SoundtrackType;
import com.leo.cse.frontend.Resources;
import com.leo.cse.dto.Challenge;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.LabeledGroup;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.compound.LabeledRadioButton;
import com.leo.cse.frontend.ui.components.compound.OnCheckedStateChangedListener;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.dialogs.DurationPickerDialog;
import com.leo.cse.frontend.dialogs.TimePickerDialog;
import com.leo.cse.frontend.editor.cells.ChallengesRow;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.JContainer;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.frontend.ui.layout.constraints.LayoutConstraints;
import com.leo.cse.util.MathUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.border.LineBorder;

public class CSPlusPage extends JContainer implements ProfileStateChangeListener {
    // last one is unused
    private static final int CHALLENGES_COUNT = ProfilePointers.BEST_MOD_TIMES_LENGTH;
    private static final int CHALLENGES_CELL_HEIGHT = 24;

    public static final int NEMESIS_WEAPON_ID = 12;

    // Challenge ids
    public static final int TIME_TRIAL_CHALLENGE_ID = 0;
    public static final int BOSS_CHALLENGE_ID = 1;
    public static final int KAZE_CHALLENGE_ID = 3;
    public static final int NEMESIS_CHALLENGE_ID = 4;

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    private LabeledGroup gameGroup;
    private LabeledGroup musicGroup;
    private LabeledGroup modDateGroup;
    private LabeledGroup langGroup;
    private LabeledGroup hellGroup;
    private LabeledGroup miscGroup;

    private LabeledRadioButton difficultyOriginalRadioButton;
    private LabeledRadioButton difficultyEasyRadioButton;
    private LabeledRadioButton difficultyHardRadioButton;

    private LabeledRadioButton graphicsNewRadioButton;
    private LabeledRadioButton graphicsOriginalRadioButton;

    private LabeledRadioButton soundtrackOriginalRadioButton;
    private LabeledRadioButton soundtrackRemasteredRadioButton;
    private LabeledRadioButton soundtrackNewRadioButton;

    private TextButton musicVolumeButton;
    private TextButton soundVolumeButton;

    private TextButton modDateDayValueButton;
    private TextButton modDateTimeValueButton;

    private LabeledRadioButton langEngRadioButton;
    private LabeledRadioButton langJpRadioButton;

    private TextButton hellBestTimeValue;

    private LabeledCheckBox miscBeatHellCheckBox;

    private final ChallengesRow challengesHeader = new ChallengesRow();
    private final JList<Challenge> challengesList = new JList<>();
    private TextLabel challengesNote;

    private final ChallengesRow.CellRenderer challengesRenderer = new ChallengesRow.CellRenderer();
    private final Vector<Challenge> challengesDataSet = new Vector<>();

    private int contentWidth = 0;
    private int contentHeight = 0;

    private final Date modDateHolder = new Date();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    private final DurationFormatter durationFormatter = new DurationFormatter();

    public CSPlusPage(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();

        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;

        initGameGroup();
        initMusicGroup();
        initModDateGroup();
        initLangGroup();
        initHellGroup();
        initChallengesTable();
        initMiscGroup();

        bind();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        profileManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        super.removeNotify();
    }

    private void initGameGroup() {
        final LabeledGroup group = new LabeledGroup();
        group.setLabelText("Game");

        final HorizontalLayout content = new HorizontalLayout();

        content.add(initDifficultyGroup());
        content.add(initGraphicsGroup(), leftMargin(24));

        group.setContent(content);

        this.gameGroup = group;

        add(group);
    }

    private void initMusicGroup() {
        final LabeledGroup group = new LabeledGroup();
        group.setLabelText("Music");

        final HorizontalLayout content = new HorizontalLayout();

        content.add(initSoundtrackGroup());
        content.add(initVolumeGroup(), leftMargin(33));

        group.setContent(content);

        this.musicGroup = group;

        add(group, leftMargin(16));
    }

    private Component initDifficultyGroup() {
        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(62, 0));

        // difficulty

        final TextLabel difficultyLabel = new TextLabel();
        difficultyLabel.setFont(Resources.getFont());
        difficultyLabel.setForeground(ThemeData.getForegroundColor());
        difficultyLabel.setSingleLine(true);
        difficultyLabel.setText("Difficulty:");

        final LabeledRadioButton originalRadioButton = new LabeledRadioButton();
        originalRadioButton.setText("Original");

        final LabeledRadioButton easyRadioButton = new LabeledRadioButton();
        easyRadioButton.setText("Easy");

        final LabeledRadioButton hardRadioButton = new LabeledRadioButton();
        hardRadioButton.setText("Hard");

        final OnCheckedStateChangedListener listener = (button, isChecked) -> {
            if (isChecked) {
                if (button != originalRadioButton) {
                    originalRadioButton.setChecked(false);
                }
                if (button != easyRadioButton) {
                    easyRadioButton.setChecked(false);
                }
                if (button != hardRadioButton) {
                    hardRadioButton.setChecked(false);
                }
                final Difficulty difficulty;
                if (button == difficultyOriginalRadioButton) {
                    difficulty = Difficulty.ORIGINAL;
                } else if (button == difficultyEasyRadioButton) {
                    difficulty = Difficulty.EASY;
                } else {
                    difficulty = Difficulty.HARD;
                }
                profileManager.setField(ProfileFields.FIELD_DIFFICULTY, difficulty.value);
            }
        };

        originalRadioButton.setOnCheckedStateListener(listener);
        easyRadioButton.setOnCheckedStateListener(listener);
        hardRadioButton.setOnCheckedStateListener(listener);

        content.add(difficultyLabel);
        content.add(originalRadioButton, topMargin(5));
        content.add(easyRadioButton, topMargin(6));
        content.add(hardRadioButton, topMargin(6));

        difficultyOriginalRadioButton = originalRadioButton;
        difficultyEasyRadioButton = easyRadioButton;
        difficultyHardRadioButton = hardRadioButton;

        return content;
    }

    private Component initGraphicsGroup() {
        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(62, 0));

        // graphics

        final TextLabel graphicsLabel = new TextLabel();
        graphicsLabel.setFont(Resources.getFont());
        graphicsLabel.setForeground(ThemeData.getForegroundColor());
        graphicsLabel.setSingleLine(true);
        graphicsLabel.setText("Graphics:");

        final LabeledRadioButton newRadioButton = new LabeledRadioButton();
        newRadioButton.setText("New");

        final LabeledRadioButton originalRadioButton = new LabeledRadioButton();
        originalRadioButton.setText("Original");

        final OnCheckedStateChangedListener listener = (button, isChecked) -> {
            if (isChecked) {
                if (button != newRadioButton) {
                    newRadioButton.setChecked(false);
                }
                if (button != originalRadioButton) {
                    originalRadioButton.setChecked(false);
                }
                final boolean isGraphicsOriginal = (button == originalRadioButton);
                profileManager.setField(ProfileFields.FIELD_GRAPHICS_STYLE, isGraphicsOriginal);
            }
        };

        newRadioButton.setOnCheckedStateListener(listener);
        originalRadioButton.setOnCheckedStateListener(listener);

        content.add(graphicsLabel);
        content.add(newRadioButton, topMargin(5));
        content.add(originalRadioButton, topMargin(6));

        graphicsNewRadioButton = newRadioButton;
        graphicsOriginalRadioButton = originalRadioButton;

        return content;
    }

    private Component initSoundtrackGroup() {
        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(83, 0));

        // soundtrack

        final TextLabel soundtrackLabel = new TextLabel();
        soundtrackLabel.setFont(Resources.getFont());
        soundtrackLabel.setForeground(ThemeData.getForegroundColor());
        soundtrackLabel.setSingleLine(true);
        soundtrackLabel.setText("Soundtrack:");

        final LabeledRadioButton remasteredRadioButton = new LabeledRadioButton();
        remasteredRadioButton.setText("Remastered");
        
        final LabeledRadioButton originalRadioButton = new LabeledRadioButton();
        originalRadioButton.setText("Original");

        final LabeledRadioButton newRadioButton = new LabeledRadioButton();
        newRadioButton.setText("New");

        final OnCheckedStateChangedListener listener = (button, isChecked) -> {
            if (isChecked) {
                if (button != remasteredRadioButton) {
                    remasteredRadioButton.setChecked(false);
                }
                if (button != originalRadioButton) {
                    originalRadioButton.setChecked(false);
                }
                if (button != newRadioButton) {
                    newRadioButton.setChecked(false);
                }

                final SoundtrackType soundtrackType;
                if (button == remasteredRadioButton) {
                    soundtrackType = SoundtrackType.REMASTERED;
                } else if (button == originalRadioButton) {
                    soundtrackType = SoundtrackType.ORIGINAL;
                } else {
                    soundtrackType = SoundtrackType.NEW;
                }
                profileManager.setField(ProfileFields.FIELD_SOUNDTRACK_TYPE, soundtrackType.value);
            }
        };

        remasteredRadioButton.setOnCheckedStateListener(listener);
        originalRadioButton.setOnCheckedStateListener(listener);
        newRadioButton.setOnCheckedStateListener(listener);

        content.add(soundtrackLabel);
        content.add(originalRadioButton, topMargin(5));
        content.add(remasteredRadioButton, topMargin(6));
        content.add(newRadioButton, topMargin(6));

        soundtrackOriginalRadioButton = originalRadioButton;
        soundtrackRemasteredRadioButton = remasteredRadioButton;
        soundtrackNewRadioButton = newRadioButton;

        return content;
    }

    private Component initVolumeGroup() {
        final VerticalLayout content = new VerticalLayout();
        content.setMinimumSize(new Dimension(104, 0));

        // music volume

        final TextLabel musicVolumeLabel = new TextLabel();
        musicVolumeLabel.setFont(Resources.getFont());
        musicVolumeLabel.setForeground(ThemeData.getForegroundColor());
        musicVolumeLabel.setSingleLine(true);
        musicVolumeLabel.setText("Music Volume");

        final Component musicVolumeControls = initMusicVolumeControls();

        // sound volume

        final TextLabel soundVolumeLabel = new TextLabel();
        soundVolumeLabel.setFont(Resources.getFont());
        soundVolumeLabel.setForeground(ThemeData.getForegroundColor());
        soundVolumeLabel.setSingleLine(true);
        soundVolumeLabel.setText("Sound Volume");

        final Component soundVolumeControls = initSoundVolumeControls();

        content.add(musicVolumeLabel);
        content.add(musicVolumeControls, topMargin(3));
        content.add(soundVolumeLabel, topMargin(9));
        content.add(soundVolumeControls, topMargin(3));

        return content;
    }

    private JContainer initMusicVolumeControls() {
        final JContainer musicVolumeControls = createVolumeControls();
        final TextButton musicDecButton = musicVolumeControls.findByName("decButton");
        final TextButton musicIncButton = musicVolumeControls.findByName("incButton");
        musicVolumeButton = musicVolumeControls.findByName("valueButton");

        musicDecButton.setOnClickListener(() -> {
            final int value = profileManager.getIntField(ProfileFields.FIELD_MUSIC_VOLUME);
            final int newValue = MathUtils.coerceIn(value - 1, 1, 10);
            if (value != newValue) {
                profileManager.setField(ProfileFields.FIELD_MUSIC_VOLUME, newValue);
            }
        });

        musicIncButton.setOnClickListener(() -> {
            final int value = profileManager.getIntField(ProfileFields.FIELD_MUSIC_VOLUME);
            final int newValue = MathUtils.coerceIn(value + 1, 1, 10);
            if (value != newValue) {
                profileManager.setField(ProfileFields.FIELD_MUSIC_VOLUME, newValue);
            }
        });

        musicVolumeButton.setOnClickListener(() -> {
            final int initial = MathUtils.coerceIn(profileManager.getIntField(ProfileFields.FIELD_MUSIC_VOLUME), 1, 10) - 1;
            final InputDialog<Integer> dialog = new InputDialog<>(initial, "Input new music volume value");
            dialog.selectInteger(this, (value) -> {
                final int newValue = MathUtils.coerceIn(value, 0, 9) + 1;
                if (initial != newValue) {
                    profileManager.setField(ProfileFields.FIELD_MUSIC_VOLUME, newValue);
                }
            });
        });

        return musicVolumeControls;
    }

    private JContainer initSoundVolumeControls() {
        final JContainer soundVolumeControls = createVolumeControls();
        final TextButton soundDecButton = soundVolumeControls.findByName("decButton");
        final TextButton soundIncButton = soundVolumeControls.findByName("incButton");
        soundVolumeButton = soundVolumeControls.findByName("valueButton");

        soundDecButton.setOnClickListener(() -> {
            final int value = profileManager.getIntField(ProfileFields.FIELD_SOUND_VOLUME);
            final int newValue = MathUtils.coerceIn(value - 1, 1, 10);
            if (value != newValue) {
                profileManager.setField(ProfileFields.FIELD_SOUND_VOLUME, newValue);
            }
        });

        soundIncButton.setOnClickListener(() -> {
            final int value = profileManager.getIntField(ProfileFields.FIELD_SOUND_VOLUME);
            final int newValue = MathUtils.coerceIn(value + 1, 1, 10);
            if (value != newValue) {
                profileManager.setField(ProfileFields.FIELD_SOUND_VOLUME, newValue);
            }
        });

        soundVolumeButton.setOnClickListener(() -> {
            final int initial = MathUtils.coerceIn(profileManager.getIntField(ProfileFields.FIELD_SOUND_VOLUME), 1, 10) - 1;
            final InputDialog<Integer> dialog = new InputDialog<>(initial, "Input new sound volume value");
            dialog.selectInteger(this, (value) -> {
                final int newValue = MathUtils.coerceIn(value, 0, 9) + 1;
                if (initial != newValue) {
                    profileManager.setField(ProfileFields.FIELD_SOUND_VOLUME, newValue);
                }
            });
        });

        return soundVolumeControls;
    }

    private JContainer createVolumeControls() {
        final HorizontalLayout content = new HorizontalLayout();

        final TextButton decButton = new TextButton();
        decButton.setMinimumSize(new Dimension(19, 18));
        decButton.setGravity(Gravity.CENTER_VERTICAL);
        decButton.setName("decButton");
        decButton.setText("-");

        final TextButton incButton = new TextButton();
        incButton.setMinimumSize(new Dimension(19, 18));
        incButton.setName("incButton");
        incButton.setText("+");

        final TextButton valueButton = new TextButton();
        valueButton.setMinimumSize(new Dimension(62, 18));
        valueButton.setGravity(Gravity.RIGHT);
        valueButton.setName("valueButton");

        content.add(decButton);
        content.add(valueButton, leftMargin(2));
        content.add(incButton, leftMargin(2));

        return content;
    }

    private void initModDateGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("Modification Date");

        // day

        final TextLabel dayLabel = new TextLabel();
        dayLabel.setFont(Resources.getFont());
        dayLabel.setForeground(ThemeData.getForegroundColor());
        dayLabel.setSingleLine(true);
        dayLabel.setText("Day:");

        final TextButton dayValue = new TextButton();
        dayValue.setGravity(Gravity.RIGHT);
        dayValue.setMinimumSize(new Dimension(148, 18));

        // time

        final TextLabel timeLabel = new TextLabel();
        timeLabel.setFont(Resources.getFont());
        timeLabel.setForeground(ThemeData.getForegroundColor());
        timeLabel.setSingleLine(true);
        timeLabel.setText("Time:");

        final TextButton timeValue = new TextButton();
        timeValue.setGravity(Gravity.RIGHT);
        timeValue.setMinimumSize(new Dimension(148, 18));

        content.add(dayLabel);
        content.add(dayValue, topMargin(3));

        content.add(timeLabel, topMargin(9));
        content.add(timeValue, topMargin(3));

        dayValue.setOnClickListener(() -> {
            final Date initial = new Date(profileManager.getLongField(ProfileFields.FIELD_MODIFY_DATE) * 1000L);
            final InputDialog<Date> dialog = new InputDialog<>(initial, "Input new date");

            dialog.selectDate(this, dateFormatter, (value) -> {
                profileManager.setField(ProfileFields.FIELD_MODIFY_DATE, value.getTime() / 1000L);
            });
        });

        timeValue.setOnClickListener(() -> {
            final Date initial = new Date(profileManager.getLongField(ProfileFields.FIELD_MODIFY_DATE) * 1000L);

            final Date time = TimePickerDialog.pick(this, initial);
            if (!Objects.equals(initial, time)) {
                profileManager.setField(ProfileFields.FIELD_MODIFY_DATE, time.getTime() / 1000L);
            }
        });

        group.setContent(content);

        modDateGroup = group;

        modDateDayValueButton = dayValue;
        modDateTimeValueButton = timeValue;

        add(group, leftMargin(16));
    }

    private void initLangGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("Language");

        final LabeledRadioButton engRadioButton = new LabeledRadioButton();
        engRadioButton.setText("English");

        final LabeledRadioButton jpRadioButton = new LabeledRadioButton();
        jpRadioButton.setText("Japanese");

        final OnCheckedStateChangedListener listener = (button, isChecked) -> {
            if (isChecked) {
                if (button != engRadioButton) {
                    engRadioButton.setChecked(false);
                }
                if (button != jpRadioButton) {
                    jpRadioButton.setChecked(false);
                }
                final boolean isLangJp = (button == jpRadioButton);
                profileManager.setField(ProfileFields.FIELD_LANGUAGE, isLangJp);
            }
        };

        engRadioButton.setOnCheckedStateListener(listener);
        jpRadioButton.setOnCheckedStateListener(listener);

        content.add(engRadioButton, topMargin(6));
        content.add(jpRadioButton, topMargin(6));

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        langEngRadioButton = engRadioButton;
        langJpRadioButton = jpRadioButton;

        langGroup = group;

        add(group, topMargin(10));
    }

    private void initHellGroup() {
        final LabeledGroup group = new LabeledGroup();
        final VerticalLayout content = new VerticalLayout();

        group.setLabelText("Bloodstained Sanctuary");

        // best time

        final TextLabel bestTimeLabel = new TextLabel();
        bestTimeLabel.setFont(Resources.getFont());
        bestTimeLabel.setForeground(ThemeData.getForegroundColor());
        bestTimeLabel.setSingleLine(true);
        bestTimeLabel.setText("Best Time:");

        final TextButton bestTimeValue = new TextButton();
        bestTimeValue.setGravity(Gravity.RIGHT);
        bestTimeValue.setMinimumSize(new Dimension(148, 18));

        content.add(bestTimeLabel);
        content.add(bestTimeValue, topMargin(3));
        content.add(initTitleScreenContainer(), topMargin(6));

        bestTimeValue.setOnClickListener(() -> {
            final int time = profileManager.getIntField(ProfileFields.FIELD_BEST_HELL_TIME);
            final int value = (int) DurationPickerDialog.pick(this, time);
            if (time != value) {
                profileManager.setField(ProfileFields.FIELD_BEST_HELL_TIME, value);
            }
        });

        group.setContent(content);
        group.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        hellGroup = group;
        hellBestTimeValue = bestTimeValue;

        add(group, topMargin(10));
    }

    private Component initTitleScreenContainer() {
        final HorizontalLayout container = new HorizontalLayout();

        final TextButton titleScreenButton = new TextButton();
        titleScreenButton.setGravity(Gravity.CENTER_HORIZONTAL);
        titleScreenButton.setMinimumSize(new Dimension(114, 18));
        titleScreenButton.setText("Set Title Screen...");

        final TextButton helpButton = new TextButton();
        helpButton.setGravity(Gravity.CENTER_HORIZONTAL);
        helpButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 18));
        helpButton.setText("?");

        container.add(titleScreenButton);
        container.add(helpButton, leftMargin(8));

        titleScreenButton.setOnClickListener(() -> {
            final int initial = profileManager.getIntField(ProfileFields.FIELD_BEST_HELL_TIME);
            final int selection = (int) new BestTimeSelectionDialog(initial, profileManager, resourcesManager).select();
            if (initial != selection) {
                profileManager.setField(ProfileFields.FIELD_BEST_HELL_TIME, selection);
            }
        });

        helpButton.setOnClickListener(() -> {
            final NikuInfoDialog dialog = new NikuInfoDialog(null, this, profileManager, resourcesManager);
            dialog.setVisible(true);
        });

        return container;
    }

    private void initMiscGroup() {
        final LabeledGroup group = new LabeledGroup();

        group.setLabelText("Misc");

        final VerticalLayout content = new VerticalLayout();

        final LabeledCheckBox beatHellCheckBox = new LabeledCheckBox();
        beatHellCheckBox.setText("Beaten Bloodstained Sanctuary");
        content.add(beatHellCheckBox, topMargin(4));

        beatHellCheckBox.setOnCheckedStateListener((button, isChecked) -> {
            profileManager.setField(ProfileFields.FIELD_BEAT_HELL, isChecked);
        });

        final MCI mci = profileManager.getCurrentMCI();

        final TextLabel noteLabel = new TextLabel();
        noteLabel.setFont(Resources.getFont());
        noteLabel.setForeground(ThemeData.getForegroundColor());
        noteLabel.setText(String.format(
                "\u263C This flag unlocks \u2018%s\u2019, \u2018%s\u2019 and \u2018%s\u2019 challenge.",
                mci.getChallengeName(TIME_TRIAL_CHALLENGE_ID),
                mci.getChallengeName(BOSS_CHALLENGE_ID),
                mci.getChallengeName(KAZE_CHALLENGE_ID)
        ));
        content.add(noteLabel, topMargin(8));

        final TextLabel nemesisLabel = new TextLabel();
        nemesisLabel.setFont(Resources.getFont());
        nemesisLabel.setForeground(ThemeData.getForegroundColor());
        nemesisLabel.setText(String.format(
                "\u263C \u2018%s\u2019 is unlocked after acquiring the %s",
                mci.getChallengeName(NEMESIS_CHALLENGE_ID),
                mci.getWeaponName(NEMESIS_WEAPON_ID)
        ));
        content.add(nemesisLabel, topMargin(8));

        group.setContent(content);

        miscGroup = group;

        miscBeatHellCheckBox = beatHellCheckBox;

        add(group, topMargin(10));
    }

    private void initChallengesTable() {
        final LineBorder border = new LineBorder(ThemeData.getForegroundColor());

        challengesHeader.setChallengeName("Challenge");
        challengesHeader.setBestTime("Best Time");
        challengesHeader.shouldPaintLine(false);
        challengesHeader.setBorder(border);
        challengesHeader.setMinimumSize(new Dimension(0, CHALLENGES_CELL_HEIGHT));

        challengesList.setCellRenderer(challengesRenderer);
        challengesList.setFixedCellHeight(CHALLENGES_CELL_HEIGHT);
        challengesList.setBorder(border);
        challengesList.setMinimumSize(new Dimension(0, CHALLENGES_CELL_HEIGHT * CHALLENGES_COUNT + border.getThickness() * 2));
        challengesList.setBackground(ThemeData.getBackgroundColor());

        final ChallengesMouseListener mouseListener = new ChallengesMouseListener();
        challengesList.addMouseMotionListener(mouseListener);
        challengesList.addMouseListener(mouseListener);

        challengesNote = new TextLabel();
        challengesNote.setFont(Resources.getFont());
        challengesNote.setForeground(ThemeData.getForegroundColor());
        challengesNote.setPadding(12, 0, 10, 0);
        challengesNote.setText(String.format(
                "\u263C In order to see the best time in the game, there should be a .best.rec file " +
                        "for each challenge in the game folder (i. e. \u2018%1$s.best.rec\u2019 " +
                        "alongside with \u2018%1$s.rec\u2019)",
                profileManager.getCurrentMCI().getChallengeName(BOSS_CHALLENGE_ID)
        ));

        add(challengesHeader, topMargin(16));
        add(challengesList, topMargin(-1)); // to overlap header's bottom line
        add(challengesNote, topMargin(10));
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.MODIFIED || event == ProfileStateEvent.SLOT_CHANGED) {
            bind();
        }
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        final int availWidth = maxWidth - (insets.right + insets.left);
        final int availHeight = maxHeight - (insets.bottom + insets.top);

        contentWidth = 0;
        contentHeight = measureTopRow(availWidth, availHeight);
        contentHeight += measureBottomRow(contentWidth, availHeight - contentHeight);

        setMeasuredDimensions(maxWidth, maxHeight);
    }

    private int measureTopRow(int availWidth, int availHeight) {
        int measuredWidth = 0;
        int measuredHeight = 0;

        // gameGroup

        LayoutConstraints lc = getChildConstraints(gameGroup);
        Dimension childSize = measureChild(gameGroup, availWidth - lc.getHorizontalMargins(), availHeight - lc.getVerticalMargins());

        measuredWidth = childSize.width + lc.getHorizontalMargins();
        measuredHeight = childSize.height + lc.getVerticalMargins();
        availWidth -= measuredWidth;

        // musicGroup
        lc = getChildConstraints(musicGroup);
        childSize = measureChild(musicGroup, availWidth - lc.getHorizontalMargins(), availHeight - lc.getVerticalMargins());

        measuredWidth += childSize.width + lc.getHorizontalMargins();
        measuredHeight = Math.max(measuredHeight, childSize.height + lc.getVerticalMargins());
        availWidth -= measuredWidth;

        // modDateGroup
        lc = getChildConstraints(modDateGroup);
        childSize = measureChild(modDateGroup, availWidth - lc.getHorizontalMargins(), availHeight - lc.getVerticalMargins());

        measuredWidth += childSize.width + lc.getHorizontalMargins();
        measuredHeight = Math.max(measuredHeight, childSize.height + lc.getVerticalMargins());

        contentWidth = measuredWidth;

        return measuredHeight;
    }

    private int measureBottomRow(int availWidth, int availHeight) {
        int measuredHeight = 0;

        // challenges header
        LayoutConstraints lc = getChildConstraints(challengesHeader);
        Dimension childSize = measureChild(challengesHeader, availWidth, availHeight - lc.getVerticalMargins());
        measuredHeight = childSize.height + lc.getVerticalMargins();

        // challenges list
        lc = getChildConstraints(challengesList);
        childSize = measureChild(challengesList, getChildDimension(musicGroup).width, availHeight - lc.getVerticalMargins() - measuredHeight);
        measuredHeight += childSize.height + lc.getVerticalMargins();

        // challenges note
        lc = getChildConstraints(challengesNote);
        measureChild(challengesNote, getChildDimension(musicGroup).width, availHeight - lc.getVerticalMargins() - measuredHeight);

        // miscGroup
        lc = getChildConstraints(miscGroup);
        measureChild(miscGroup, getChildDimension(modDateGroup).width, measuredHeight - lc.getVerticalMargins());

        // langGroup
        lc = getChildConstraints(langGroup);
        childSize = measureChild(langGroup, getChildDimension(gameGroup).width, measuredHeight - lc.getVerticalMargins());

        // hellGroup
        lc = getChildConstraints(hellGroup);
        measureChild(hellGroup, getChildDimension(gameGroup).width, measuredHeight - childSize.height - lc.getVerticalMargins());

        return measuredHeight;
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        final int l = insets.left;
        final int t = insets.top;
        final int r = container.getWidth() - insets.right;
        final int b = container.getHeight() - insets.bottom;

        // center inside container
        final int left = l + (r - l - contentWidth) / 2;
        final int top = t + (b - t - contentHeight) / 2;

        layoutTopRow(left, top);
        layoutBottomRow(gameGroup.getY() + gameGroup.getHeight());
    }

    private void layoutTopRow(int left, int top) {
        int dx = 0;

        // gameGroup

        int topRowHeight = Math.max(
                getChildDimension(gameGroup).height,
                Math.max(
                        getChildDimension(musicGroup).height,
                        getChildDimension(modDateGroup).height
                )
        );

        LayoutConstraints lc = getChildConstraints(gameGroup);
        Dimension childSize = getChildDimension(gameGroup);

        gameGroup.setBounds(left + dx + lc.leftMargin, top  + lc.topMargin, childSize.width, topRowHeight);
        dx += childSize.width + lc.getHorizontalMargins();

        // musicGroup

        lc = getChildConstraints(musicGroup);
        childSize = getChildDimension(musicGroup);

        musicGroup.setBounds(left + dx + lc.leftMargin, top  + lc.topMargin, childSize.width, topRowHeight);
        dx += childSize.width + lc.getHorizontalMargins();

        // modDateGroup
        lc = getChildConstraints(modDateGroup);
        childSize = getChildDimension(modDateGroup);

        modDateGroup.setBounds(left + dx + lc.leftMargin, top  + lc.topMargin, childSize.width, topRowHeight);
    }

    private void layoutBottomRow(int top) {
        // challenges

        LayoutConstraints lc = getChildConstraints(challengesHeader);
        Dimension childSize = getChildDimension(challengesHeader);

        challengesHeader.setBounds(musicGroup.getX(), top + lc.topMargin, musicGroup.getWidth(), childSize.height);

        lc = getChildConstraints(challengesList);
        childSize = getChildDimension(challengesList);

        challengesList.setBounds(
                musicGroup.getX(),
                challengesHeader.getY() + challengesHeader.getHeight() + lc.topMargin,
                musicGroup.getWidth(),
                childSize.height - 1); // subtract last cell's border width

        lc = getChildConstraints(challengesNote);
        childSize = getChildDimension(challengesNote);

        challengesNote.setBounds(
                challengesList.getX(),
                challengesList.getY() + challengesList.getHeight() + lc.topMargin,
                childSize.width,
                childSize.height
        );

        // misc

        lc = getChildConstraints(miscGroup);

        int miscHeight = challengesHeader.getY()
                        + challengesHeader.getHeight()
                        + getChildConstraints(challengesList).topMargin
                        + challengesList.getHeight()
                        - (top + lc.topMargin);
        miscGroup.setBounds(
                modDateGroup.getX(),
                top + lc.topMargin,
                modDateGroup.getWidth(),
                miscHeight
        );

        // lang

        lc = getChildConstraints(langGroup);
        childSize = getChildDimension(langGroup);

        langGroup.setBounds(gameGroup.getX(), top + lc.topMargin, gameGroup.getWidth(), childSize.height);

        // hell

        lc = getChildConstraints(hellGroup);
        childSize = getChildDimension(hellGroup);

        hellGroup.setBounds(gameGroup.getX(), langGroup.getY() + langGroup.getHeight() + lc.topMargin, gameGroup.getWidth(), childSize.height);
    }

    private void bind() {
        final Difficulty difficulty = Difficulty.valueOf(profileManager.getShortField(ProfileFields.FIELD_DIFFICULTY));
        difficultyOriginalRadioButton.setChecked(difficulty == Difficulty.ORIGINAL);
        difficultyEasyRadioButton.setChecked(difficulty == Difficulty.EASY);
        difficultyHardRadioButton.setChecked(difficulty == Difficulty.HARD);

        final boolean isGraphicsOriginal = profileManager.getBooleanField(ProfileFields.FIELD_GRAPHICS_STYLE);
        graphicsNewRadioButton.setChecked(!isGraphicsOriginal);
        graphicsOriginalRadioButton.setChecked(isGraphicsOriginal);

        final SoundtrackType soundtrackType = SoundtrackType.valueOf(profileManager.getByteField(ProfileFields.FIELD_SOUNDTRACK_TYPE));
        soundtrackOriginalRadioButton.setChecked(soundtrackType == SoundtrackType.ORIGINAL);
        soundtrackRemasteredRadioButton.setChecked(soundtrackType == SoundtrackType.REMASTERED);
        soundtrackNewRadioButton.setChecked(soundtrackType == SoundtrackType.NEW);

        final int musicVolume = MathUtils.coerceIn(profileManager.getIntField(ProfileFields.FIELD_MUSIC_VOLUME), 1, 10) - 1;
        musicVolumeButton.setText(String.valueOf(musicVolume));

        final int soundVolume = MathUtils.coerceIn(profileManager.getIntField(ProfileFields.FIELD_SOUND_VOLUME), 1, 10) - 1;
        soundVolumeButton.setText(String.valueOf(soundVolume));

        modDateHolder.setTime(profileManager.getLongField(ProfileFields.FIELD_MODIFY_DATE) * 1000L);

        modDateDayValueButton.setText(dateFormatter.format(modDateHolder));
        modDateTimeValueButton.setText(timeFormatter.format(modDateHolder));

        final boolean isLangEng = !profileManager.getBooleanField(ProfileFields.FIELD_LANGUAGE);
        langEngRadioButton.setChecked(isLangEng);
        langJpRadioButton.setChecked(!isLangEng);

        final String hellBestTime = durationFormatter.format(profileManager.getIntField(ProfileFields.FIELD_BEST_HELL_TIME));
        hellBestTimeValue.setText(hellBestTime);

        miscBeatHellCheckBox.setChecked(profileManager.getBooleanField(ProfileFields.FIELD_BEAT_HELL));

        rebuildChallengesDataSet();
    }

    private void rebuildChallengesDataSet() {
        challengesDataSet.clear();

        for (int i = 0; i < ProfilePointers.BEST_MOD_TIMES_LENGTH; ++i) {
            final String name = profileManager.getCurrentMCI().getChallengeName(i);
            final Integer time = (Integer) profileManager.getField(ProfileFields.FIELD_BEST_MOD_TIMES, i);
            challengesDataSet.add(new Challenge(name, (time != null) ? time : 0L));
        }

        challengesList.setListData(challengesDataSet);
    }

    private class ChallengesMouseListener extends MouseAdapter {
        private final Point point = new Point();

        @Override
        public void mouseMoved(MouseEvent e) {
            point.setLocation(e.getX(), e.getY());

            final int index = challengesList.locationToIndex(point);

            if (challengesRenderer.setHoveredIndex(index)) {
                challengesList.repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (challengesRenderer.clearHover()) {
                challengesList.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            point.setLocation(e.getX(), e.getY());

            final int index = challengesList.locationToIndex(point);
            if (index >= 0 && index < CHALLENGES_COUNT) {
                final Challenge challenge = challengesDataSet.get(index);
                final long time = challenge.time;

                final int value = (int)DurationPickerDialog.pick(CSPlusPage.this, time);
                if (time != value) {
                    profileManager.setField(ProfileFields.FIELD_BEST_MOD_TIMES, index, value);
                }
            }
        }
    }
}
