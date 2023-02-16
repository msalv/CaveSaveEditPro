package com.leo.cse.frontend.ui.components.color;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.log.AppLogger;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.ColorUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.border.EmptyBorder;

public class ColorPickerComponent extends VerticalLayout {
    private final ColorsPalette palette = new ColorsPalette();
    private final LightnessComponent lightnessSlider = new LightnessComponent();

    private final TextButton rgbButton = new TextButton();
    private final TextButton okayButton = new TextButton();
    private final TextButton cancelButton = new TextButton();

    private Callback callback;

    public ColorPickerComponent() {
        super();
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(16, 16, 16, 16));

        palette.setMinimumSize(new Dimension(Integer.MAX_VALUE, 115));
        palette.setOnColorChangedListener(this::onColorChanged);

        lightnessSlider.setMinimumSize(new Dimension(Integer.MAX_VALUE, 16));
        lightnessSlider.setOnLightnessChangedListener((lightness) -> {
            palette.setLightness(lightness);
            onColorChanged(palette.getColor());
        });

        rgbButton.setPadding(6, 5, 6, 5);
        rgbButton.setMinimumSize(new Dimension(88, 24));
        rgbButton.setOnClickListener(() -> {
            final Color initialColor = palette.getColor();
            final String initial = formatColor(initialColor);
            final InputDialog<String> dialog = new InputDialog<>(initial, "Input color");
            dialog.selectString(this, (input) -> {
                final String hex = (input.charAt(0) == '#') ? input.substring(1) : input;
                try {
                    final int rgb = Integer.parseInt(hex, 16);
                    if (initialColor.getRGB() != rgb) {
                        palette.setSelectedColor(new Color(rgb));
                        onColorChanged(palette.getColor());
                    }
                } catch (NumberFormatException ex) {
                    AppLogger.error(String.format("Can't parse %s", hex), ex);
                }
            });
        });

        okayButton.setText("OK");
        okayButton.setPadding(6, 5, 6, 5);
        okayButton.setMinimumSize(new Dimension(52, 24));
        okayButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onPositiveButtonClicked();
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setPadding(6, 5, 6, 5);
        cancelButton.setMinimumSize(new Dimension(52, 24));
        cancelButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onNegativeButtonClicked();
            }
        });

        add(palette);
        add(lightnessSlider, topMargin(8));

        final HorizontalLayout footer = new HorizontalLayout();
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        footer.add(rgbButton);
        footer.add(okayButton, alignRight());
        footer.add(cancelButton, alignRight(rightMargin(4)));

        add(footer, topMargin(16));
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setSelectedColor(Color color) {
        palette.setSelectedColor(color);
        lightnessSlider.setLightness(ColorUtils.getLightness(color));
        onColorChanged(color);
    }

    private void onColorChanged(Color color) {
        rgbButton.setText(formatColor(color));
        rgbButton.setBackground(color);
        rgbButton.setTextColor(ColorUtils.isDark(color) ? Color.WHITE : Color.BLACK);
    }

    public Color getSelectedColor() {
        return palette.getColor();
    }

    private String formatColor(Color color) {
        final String hex = Integer.toHexString(color.getRGB());
        return String.format("#%s", hex.length() > 6 ? hex.substring(2) : hex)
                .toUpperCase(Locale.ROOT);
    }

    public interface Callback {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
    }
}
