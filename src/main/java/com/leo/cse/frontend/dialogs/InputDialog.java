package com.leo.cse.frontend.dialogs;

import com.leo.cse.log.AppLogger;
import com.leo.cse.util.StringUtils;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

public class InputDialog<T> {
    private final T currentValue;
    private final String message;

    public InputDialog(T currentValue, String message) {
        this.currentValue = currentValue;
        this.message = message;
    }

    public void selectInteger(Component parentComponent, Consumer<Integer> consumer) {
        final String input = JOptionPane.showInputDialog(
                parentComponent,
                message,
                currentValue
        );

        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        try {
            final Integer value = Math.abs(Integer.parseInt(input.trim()));
            if (!Objects.equals(value, currentValue)) {
                consumer.accept(value);
            }
        } catch (NumberFormatException ex) {
            AppLogger.error("Can't parse integer", ex);
        }
    }

    public void selectShort(Component parentComponent, Consumer<Short> consumer) {
        final String input = JOptionPane.showInputDialog(
                parentComponent,
                message,
                currentValue
        );

        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        try {
            final Short value = (short)Math.abs(Short.parseShort(input.trim()));
            if (!Objects.equals(value, currentValue)) {
                consumer.accept(value);
            }
        } catch (NumberFormatException ex) {
            AppLogger.error("Can't parse short", ex);
        }
    }

    public void selectLong(Component parentComponent, Consumer<Long> consumer) {
        final String input = JOptionPane.showInputDialog(
                parentComponent,
                message,
                currentValue
        );

        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        try {
            final Long value = (long)Math.abs(Long.parseLong(input.trim()));
            if (!Objects.equals(value, currentValue)) {
                consumer.accept(value);
            }
        } catch (NumberFormatException ex) {
            AppLogger.error("Can't parse long", ex);
        }
    }

    public void selectString(Component parentComponent, Consumer<String> consumer) {
        final String input = JOptionPane.showInputDialog(
                parentComponent,
                message,
                currentValue
        );

        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        final String value = input.trim();
        if (value.length() == 0) {
            return;
        }

        if (!Objects.equals(value, currentValue)) {
            consumer.accept(value);
        }
    }

    public void selectDate(Component parentComponent, SimpleDateFormat formatter, Consumer<Date> consumer) {
        final Date initialValue = (currentValue instanceof Date) ? (Date)currentValue : new Date();

        final String input = JOptionPane.showInputDialog(
                parentComponent,
                message,
                formatter.format(currentValue)
        );

        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        final String value = input.trim();
        if (value.length() == 0) {
            return;
        }

        final Date inputDate;
        try {
            inputDate = formatter.parse(value);
        } catch (ParseException e) {
            AppLogger.error("Incorrect date format", e);
            JOptionPane.showMessageDialog(
                    parentComponent,
                    String.format("Unable to parse input '%s'", value),
                    "Incorrect date format",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialValue);

        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = calendar.get(Calendar.MINUTE);
        final int seconds = calendar.get(Calendar.SECOND);

        calendar.setTime(inputDate);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);

        final Date newDate = calendar.getTime();
        if (!newDate.equals(initialValue)) {
            consumer.accept(newDate);
        }
    }
}
