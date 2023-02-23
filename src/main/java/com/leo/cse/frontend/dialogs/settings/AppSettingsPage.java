package com.leo.cse.frontend.dialogs.settings;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.CString;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.editor.selectors.EncodingSelectionDialog;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.dialogs.ColorPickerDialog;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Locale;
import java.util.function.Consumer;

public class AppSettingsPage extends VerticalLayout {
    private final LabeledCheckBox autoloadGameResCheckbox = new LabeledCheckBox();
    private final LabeledCheckBox autoloadRecentFileCheckbox = new LabeledCheckBox();
    private final LabeledCheckBox loadNPCsCheckbox = new LabeledCheckBox();

    private TextButton fgColorResetButton;
    private TextButton bgColorResetButton;
    private TextButton encodingResetButton;

    private final TextButton fgColorButton = button("Foreground Color", (button) -> {
        fgColorResetButton = button;
    });
    private final TextButton bgColorButton = button("Background Color", (button) -> {
        bgColorResetButton = button;
    });
    private final TextButton encodingButton = button("Encoding", (button) -> {
        encodingResetButton = button;
    });

    private final GameResourcesManager resourcesManager;
    private final Callback callback;

    public AppSettingsPage(GameResourcesManager resourcesManager, Callback callback) {
        super();
        this.resourcesManager = resourcesManager;
        this.callback = callback;
        init();
        bind();
    }

    private void init() {
        setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        autoloadGameResCheckbox.setText("Autoload Recent Game Resources");
        autoloadGameResCheckbox.setOnCheckedStateListener((button, isChecked) -> {
            Config.setBoolean(Config.KEY_AUTOLOAD_EXE, isChecked);
        });

        autoloadRecentFileCheckbox.setText("Autoload Recent Save File");
        autoloadRecentFileCheckbox.setOnCheckedStateListener((button, isChecked) -> {
            Config.setBoolean(Config.KEY_AUTOLOAD_PROFILE, isChecked);
        });

        loadNPCsCheckbox.setText("Load NPCs");
        loadNPCsCheckbox.setOnCheckedStateListener((button, isChecked) -> {
            Config.setBoolean(Config.KEY_LOAD_NPCS, isChecked);
            resourcesManager.reload();
        });

        fgColorButton.setOnClickListener(() -> {
            final Color color = Config.getColor(Config.KEY_FOREGROUND_COLOR, ThemeData.getForegroundColor());
            final Color newColor = ColorPickerDialog.pick(this, color);
            if (!color.equals(newColor)) {
                Config.setColor(Config.KEY_FOREGROUND_COLOR, newColor);
                onColorChanged();
            }
        });

        fgColorResetButton.setOnClickListener(() -> {
            Config.setColor(Config.KEY_FOREGROUND_COLOR, ThemeData.COLOR_FG_DEFAULT);
            onColorChanged();
        });

        bgColorButton.setOnClickListener(() -> {
            final Color color = Config.getColor(Config.KEY_BACKGROUND_COLOR, ThemeData.getBackgroundColor());
            final Color newColor = ColorPickerDialog.pick(this, color);
            if (!color.equals(newColor)) {
                Config.setColor(Config.KEY_BACKGROUND_COLOR, newColor);
                onColorChanged();
            }
        });

        bgColorResetButton.setOnClickListener(() -> {
            Config.setColor(Config.KEY_BACKGROUND_COLOR, ThemeData.COLOR_BG_DEFAULT);
            onColorChanged();
        });

        encodingButton.setOnClickListener(() -> {
            final String initial = Config.get(Config.KEY_ENCODING, CString.DEFAULT_ENCODING);
            final String encoding = new EncodingSelectionDialog().select(initial);
            if (encoding != null && !encoding.equals(initial)) {
                if (StringUtils.isEncodingSupported(encoding)) {
                    Config.set(Config.KEY_ENCODING, encoding);
                    bind();
                    resourcesManager.reload();
                }
            }
        });

        encodingResetButton.setOnClickListener(() -> {
            Config.set(Config.KEY_ENCODING, CString.DEFAULT_ENCODING);
            bind();
            resourcesManager.reload();
        });

        add(autoloadGameResCheckbox);
        add(autoloadRecentFileCheckbox, topMargin(8));
        add(loadNPCsCheckbox, topMargin(8));
        add(fgColorButton.getParent(), topMargin(8));
        add(bgColorButton.getParent(), topMargin(4));
        add(encodingButton.getParent(), topMargin(4));
    }

    private void bind() {
        autoloadGameResCheckbox.setChecked(Config.getBoolean(Config.KEY_AUTOLOAD_EXE, false));
        autoloadRecentFileCheckbox.setChecked(Config.getBoolean(Config.KEY_AUTOLOAD_PROFILE, false));
        loadNPCsCheckbox.setChecked(Config.getBoolean(Config.KEY_LOAD_NPCS, false));

        fgColorButton.setText(Config.getColorString(Config.KEY_FOREGROUND_COLOR, ThemeData.getForegroundColor()).toUpperCase(Locale.ROOT));
        bgColorButton.setText(Config.getColorString(Config.KEY_BACKGROUND_COLOR, ThemeData.getBackgroundColor()).toUpperCase(Locale.ROOT));
        encodingButton.setText(Config.get(Config.KEY_ENCODING, CString.DEFAULT_ENCODING));

        fgColorResetButton.setVisible(
            !Config.getColor(Config.KEY_FOREGROUND_COLOR, ThemeData.COLOR_FG_DEFAULT).equals(ThemeData.COLOR_FG_DEFAULT)
        );

        bgColorResetButton.setVisible(
            !Config.getColor(Config.KEY_BACKGROUND_COLOR, ThemeData.COLOR_BG_DEFAULT).equals(ThemeData.COLOR_BG_DEFAULT)
        );

        encodingResetButton.setVisible(
            !Config.get(Config.KEY_ENCODING, CString.DEFAULT_ENCODING).equals(CString.DEFAULT_ENCODING)
        );
    }

    private void onColorChanged() {
        ThemeData.reset();
        if (callback != null) {
            callback.onThemeChanged();
        }
        bind();
    }

    private static TextButton button(String text, Consumer<TextButton> onResetButtonCreated) {
        final HorizontalLayout container = new HorizontalLayout();
        container.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        final TextLabel label = new TextLabel();
        final TextButton button = new TextButton();
        final TextButton resetButton = new TextButton();

        label.setMinimumSize(new Dimension(93, 19));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
        label.setSingleLine(true);
        label.setClickable(false);
        label.setTextColor(ThemeData.getForegroundColor());
        label.setFont(Resources.getFont());
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 1, 0, 0);
        label.setText(text);

        button.setMinimumSize(new Dimension(72, 19));
        button.setPadding(6, 3, 6, 2);

        resetButton.setText("Reset");
        resetButton.setMinimumSize(new Dimension(72, 19));
        resetButton.setPadding(6, 3, 6, 2);
        resetButton.setVisible(false);

        onResetButtonCreated.accept(resetButton);

        container.add(label);
        container.add(button, leftMargin(4));
        container.add(resetButton, leftMargin(4));

        return button;
    }

    public interface Callback {
        void onThemeChanged();
    }
}
