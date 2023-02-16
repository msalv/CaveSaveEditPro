package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.constraints;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.frontend.actions.NumPadActionsBinder;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.border.EmptyBorder;

public class TimePickerComponent extends VerticalLayout {
    private static final int MAX_INPUT_LENGTH = 8;
    private final TextButton timeButton = new TextButton();
    private final TextButton okayButton = new TextButton();

    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    private Callback callback;

    private Date currentTime;
    private String formattedTime;
    private final Calendar calendar = Calendar.getInstance();
    private final StringBuilder stringBuilder = new StringBuilder();

    public TimePickerComponent() {
        super();
        init();

        new NumPadActionsBinder().bind(this, this::onKeyPressed);
    }

    private void init() {
        setBorder(new EmptyBorder(9, 9, 9, 9));
        add(initTimeButton(), centerHorizontal(constraints()));
        add(initNumPadGrid(), topMargin(7));
        add(initFooter(), topMargin(8));
    }

    private Component initTimeButton() {
        timeButton.setPadding(6, 5, 6, 5);
        timeButton.setMinimumSize(new Dimension(80, 24));
        timeButton.setOnClickListener(() -> {
            final InputDialog<String> dialog = new InputDialog<>(formattedTime, "Input time");
            dialog.selectString(this, this::parseTime);
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

        final TextButton nowButton = new TextButton();
        nowButton.setText("Now");
        nowButton.setPadding(6, 5, 6, 5);
        nowButton.setMinimumSize(new Dimension(52, 24));
        nowButton.setOnClickListener(this::onNowButtonClicked);

        final HorizontalLayout footer = new HorizontalLayout();
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        footer.add(okayButton, alignRight());
        footer.add(nowButton, alignRight(rightMargin(4)));

        return footer;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void setTimeButtonText(String text) {
        timeButton.setText(text);
        okayButton.setEnabled(!StringUtils.isNullOrEmpty(text));
    }

    public void setTime(Date time) {
        this.currentTime = time;
        formattedTime = timeFormatter.format(time);
        setTimeButtonText(formattedTime);
    }

    public Date getTime() {
        parseTime(formattedTime);
        return currentTime;
    }

    private void onNowButtonClicked() {
        final Calendar now = Calendar.getInstance();
        now.setTime(new Date(System.currentTimeMillis()));

        calendar.setTime(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, now.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));

        setTime(calendar.getTime());
    }

    private void onClearButtonClicked() {
        final String formattedTime = this.formattedTime;
        if (!StringUtils.isNullOrEmpty(formattedTime)) {
            this.formattedTime = "";
            setTimeButtonText(this.formattedTime);
        }
    }

    private void onBackspaceButtonClicked() {
        final String formattedTime = this.formattedTime;
        if (!StringUtils.isNullOrEmpty(formattedTime)) {
            this.formattedTime = formattedTime.substring(0, formattedTime.length() - 1);
            setTimeButtonText(this.formattedTime);
        }
    }

    private void onNumButtonClicked(int value) {
        if (formattedTime.length() >= MAX_INPUT_LENGTH) {
            return;
        }

        stringBuilder.setLength(0);
        stringBuilder.append(formattedTime);

        final int length = stringBuilder.length();

        if (length == 0 && value > 2) {
            stringBuilder.append('0');
        } else if (length == 2 || length == 5) {
            stringBuilder.append(':');
        }

        if (length != 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ':') {
            if (value > 5) {
                stringBuilder.append('0');
            }
        }

        if (length == 1 && stringBuilder.charAt(0) == '2') {
            stringBuilder.append(Math.min(value, 3));
        } else {
            stringBuilder.append(value);
        }

        formattedTime = stringBuilder.toString();
        setTimeButtonText(formattedTime);
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

    private void parseTime(String input) {
        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        final String[] parts = input.substring(0, Math.min(MAX_INPUT_LENGTH, input.length())).split(":", 3);

        final int hours = (parts.length > 0)
                ? MathUtils.coerceIn(StringUtils.parseIntSafe(parts[0]), 0, 23)
                : 0;

        final int minutes = (parts.length > 1)
                ? MathUtils.coerceIn(StringUtils.parseIntSafe(parts[1]), 0, 59)
                : 0;

        final int seconds = (parts.length > 2)
                ? MathUtils.coerceIn(StringUtils.parseIntSafe(parts[2]), 0, 59)
                : 0;

        calendar.setTime(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);

        setTime(calendar.getTime());
    }

    public interface Callback {
        void onPositiveButtonClicked();
    }
}
