package com.leo.cse.frontend.dialogs.niku;

import com.leo.cse.backend.BytesReaderWriter;
import com.leo.cse.log.AppLogger;
import com.leo.cse.frontend.Config;
import com.leo.cse.util.Dialogs;
import com.leo.cse.util.MathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NikuEditController {
    private static final FileNameExtensionFilter NIKU_FILTER = new FileNameExtensionFilter("290.rec file", "rec");

    private File file;

    public void unload(Consumer<Long> callback) {
        file = null;
        callback.accept(0L);
    }

    public void open(Consumer<Long> callback) {
        File dir = new File(Config.get(Config.KEY_LAST_NIKU, System.getProperty("user.dir")));
        if (!dir.exists()) {
            dir = new File(System.getProperty("user.dir"));
        }

        final File file = Dialogs.openFileChooser(
                null,
                "Open 290.rec...",
                NIKU_FILTER,
                dir,
                false,
                false);

        if (file == null || !file.exists()) {
            return;
        }

        try {
            final long duration = load(file);
            callback.accept(duration);
            this.file = file;
        } catch (IOException e) {
            AppLogger.error("Failed to open 290.rec", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while loading the 290.rec file:\n" + e,
                    "Could not load 290.rec file!",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Config.set(Config.KEY_LAST_NIKU, file.getParent());
        }
    }

    public void save(long duration) {
        if (file == null || !file.exists()) {
            saveAs(duration);
            return;
        }

        try {
            save(file, (int)duration);
            AppLogger.info("290.rec file saved");
        } catch (Exception e) {
            AppLogger.error("Failed to save 290.rec", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while saving the 290.rec file:\n" + e,
                    "Could not save 290.rec file!",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void saveAs(long duration) {
        File dir = new File(Config.get(Config.KEY_LAST_NIKU, System.getProperty("user.dir")));
        if (!dir.exists()) {
            dir = new File(System.getProperty("user.dir"));
        }

        final File file = Dialogs.openFileChooser(
                null,
                "Save 290.rec...",
                NIKU_FILTER,
                dir,
                false,
                true);

        if (file == null) {
            return;
        }

        if (file.exists()) {
            final int answer = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to overwrite this file?",
                    "Overwrite confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            save(file, (int)duration);
            this.file = file;
            AppLogger.info("290.rec file saved");
        } catch (Exception e) {
            AppLogger.error("Failed to save 290.rec", e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while saving the 290.rec file:\n" + e,
                    "Could not save 290.rec file!",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Config.set(Config.KEY_LAST_NIKU, file.getParent());
        }
    }

    private static long load(File src) throws IOException {
        final int[] result = new int[4];
        final byte[] buf = new byte[20];

        try (FileInputStream fis = new FileInputStream(src)) {
            fis.read(buf);
        }

        for (int i = 0; i < 4; i++) {
            final int key = Byte.toUnsignedInt(buf[i + 16]);
            final int j = i * 4;

            buf[j] = (byte) (buf[j] - key);
            buf[j + 1] = (byte) (buf[j + 1] - key);
            buf[j + 2] = (byte) (buf[j + 2] - key);
            buf[j + 3] = (byte) (buf[j + 3] - key / 2);

            result[i] = BytesReaderWriter.readInt(buf, j);
        }

        if (result[0] != result[1] || result[0] != result[2] || result[0] != result[3]) {
            throw new IOException("290.rec file is corrupt");
        }

        final int value = result[0];

        final int minutes = value / 3000;
        final int seconds = (value / 50) % 60;
        final int centiseconds = (value / 5) % 10;

        return (minutes * 3600L + seconds * 60L + centiseconds * 6L);
    }

    private static void save(File dest, int duration) throws IOException {
        final int minutes   = duration / 3600;
        final int seconds = (duration - (minutes * 3600)) / 60;
        final int centiseconds = (duration - (minutes * 3600) - (seconds * 60)) * 100 / 600;

        final int value = MathUtils.coerceIn(minutes * 3000 + seconds * 50 + centiseconds * 5, 0, 299_999);

        final int[] bufInt = new int[4];
        Arrays.fill(bufInt, value);

        final byte[] bufByte = new byte[20];
        BytesReaderWriter.writeInts(bufByte, 0, 0, bufInt);

        final Random r = new Random();
        for (int i = 16; i < bufByte.length; ++i) {
            bufByte[i] = (byte) r.nextInt(0xFF);
        }

        for (int i = 0; i < 4; i++) {
            final int key = Byte.toUnsignedInt(bufByte[i + 16]);
            final int j = i * 4;
            bufByte[j] = (byte) (bufByte[j] + key);
            bufByte[j + 1] = (byte) (bufByte[j + 1] + key);
            bufByte[j + 2] = (byte) (bufByte[j + 2] + key);
            bufByte[j + 3] = (byte) (bufByte[j + 3] + key / 2);
        }

        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(bufByte);
        }
    }
}
