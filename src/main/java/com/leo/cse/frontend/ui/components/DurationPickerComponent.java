package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.constraints;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.frontend.actions.NumPadActionsBinder;
import com.leo.cse.frontend.format.DurationFormatter;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.dialogs.InputDialog;
import com.leo.cse.frontend.ui.layout.GridLayout;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.MathUtils;
import com.leo.cse.util.StringUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.border.EmptyBorder;

public class DurationPickerComponent extends VerticalLayout {
    private static final int MAX_INPUT_LENGTH = 7;

    private final TextButton timeButton = new TextButton();
    private final TextButton okayButton = new TextButton();

    private final DurationFormatter durationFormatter = new DurationFormatter();

    private Callback callback;

    private long currentValue;
    private String formattedValue = "";
    private final StringBuilder stringBuilder = new StringBuilder();

    public DurationPickerComponent() {
        super();
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(9, 9, 9, 9));
        add(initTimeButton(), centerHorizontal(constraints()));
        add(initNumPadGrid(), topMargin(7));
        add(initFooter(), topMargin(8));

        new NumPadActionsBinder().bind(this, this::onKeyPressed);
    }

    private Component initTimeButton() {
        timeButton.setPadding(6, 5, 6, 5);
        timeButton.setMinimumSize(new Dimension(80, 24));
        timeButton.setOnClickListener(() -> {
            final InputDialog<String> dialog = new InputDialog<>(formattedValue, "Input time");
            dialog.selectString(this, this::parseDuration);
        });
        return timeButton;
    }

    private Component initNumPadGrid() {
        final GridLayout numPadGrid = new GridLayout();

        numPadGrid.setSpanCount(3);
        numPadGrid.setHorizontalGap(6);
        numPadGrid.setVerticalGap(4);

        for (int i = 0; i < 10; ++i) {
            final TextButton numButton = new TextButton();
            numButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 38));
            numButton.setPadding(6, 5, 6, 5);
            numButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            final int digit = (i + 1) % 10;
            numButton.setText(String.valueOf(digit));
            numButton.setOnClickListener(() -> {
                onNumButtonClicked(digit);
            });

            numPadGrid.add(numButton);
        }

        final TextButton clearButton = new TextButton();
        clearButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 38));
        clearButton.setPadding(6, 5, 6, 5);
        clearButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        clearButton.setText("C");
        clearButton.setOnClickListener(this::onClearButtonClicked);

        numPadGrid.add(clearButton);

        final TextButton backspaceButton = new TextButton();
        backspaceButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 38));
        backspaceButton.setPadding(6, 5, 6, 5);
        backspaceButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        backspaceButton.setText("\u2190"); // ←
        backspaceButton.setOnClickListener(this::onBackspaceButtonClicked);

        numPadGrid.add(backspaceButton);

        return numPadGrid;
    }

    private Component initFooter() {
        okayButton.setText("OK");
        okayButton.setPadding(6, 5, 6, 5);
        okayButton.setMinimumSize(new Dimension(52, 24));
        okayButton.setOnClickListener(() -> {
            if (callback != null) {
                callback.onPositiveButtonClicked();
            }
        });

        final HorizontalLayout footer = new HorizontalLayout();
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        footer.add(okayButton, alignRight());

        return footer;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void setTimeButtonText(String text) {
        timeButton.setText(text);
        okayButton.setEnabled(!StringUtils.isNullOrEmpty(text));
    }

    public void setDuration(long duration) {
        this.currentValue = duration;
        formattedValue = durationFormatter.format(duration);
        setTimeButtonText(formattedValue);
    }

    public long getDuration() {
        parseDuration(formattedValue);
        return currentValue;
    }

    private void onClearButtonClicked() {
        final String formattedTime = this.formattedValue;
        if (!StringUtils.isNullOrEmpty(formattedTime)) {
            this.formattedValue = "";
            setTimeButtonText(this.formattedValue);
        }
    }

    private void onBackspaceButtonClicked() {
        final String formattedTime = this.formattedValue;
        if (!StringUtils.isNullOrEmpty(formattedTime)) {
            this.formattedValue = formattedTime.substring(0, formattedTime.length() - 1);
            setTimeButtonText(this.formattedValue);
        }
    }

    private void onNumButtonClicked(int value) {
        if (formattedValue.length() >= MAX_INPUT_LENGTH) {
            return;
        }

        stringBuilder.setLength(0);
        stringBuilder.append(formattedValue);

        final int length = stringBuilder.length();

        if (length == 2) {
            stringBuilder.append(':');
        } else if (length == 5) {
            stringBuilder.append('.');
        }

        if (length != 0) {
            final char last = stringBuilder.charAt(stringBuilder.length() - 1);
            if (last == ':' && value > 5) {
                stringBuilder.append('0');
            }
        }

        stringBuilder.append(value);

        formattedValue = stringBuilder.toString();
        setTimeButtonText(formattedValue);
    }

    private void onKeyPressed(int code) {
        if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
            onNumButtonClicked(code % 16);
        } else if (code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9) {
            onNumButtonClicked(code % 16);
        } else if (code == KeyEvent.VK_BACK_SPACE) {
            onBackspaceButtonClicked();
        } else if (code == KeyEvent.VK_DELETE) {
            onClearButtonClicked();
        }
    }

    private void parseDuration(String input) {
        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        final String[] time = input.substring(0, Math.min(MAX_INPUT_LENGTH, input.length())).split(":", 2);
        final String[] parts = (time.length > 1)
                ? time[1].split("\\.", 2)
                : new String[0];

        final int minutes = Math.max(0, StringUtils.parseIntSafe(time[0]));

        final int seconds = (parts.length > 0)
                ? MathUtils.coerceIn(StringUtils.parseIntSafe(parts[0]), 0, 59)
                : 0;

        final int centiseconds = (parts.length > 1)
                ? MathUtils.coerceIn(StringUtils.parseIntSafe(parts[1]), 0, 9)
                : 0;

        final long duration = (minutes * 3600L + seconds * 60L + centiseconds * 6L);
        setDuration(duration);
    }

    public void setPositiveButtonText(String text) {
        okayButton.setText(text);
    }

    public interface Callback {
        void onPositiveButtonClicked();
    }
}
