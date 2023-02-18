package com.leo.cse.frontend.dialogs;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerVertical;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.rightMargin;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.util.async.AsyncTask;
import com.leo.cse.util.async.DelayedTask;
import com.leo.cse.log.AppLogger;
import com.leo.cse.frontend.App;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.RootDialog;
import com.leo.cse.frontend.ui.layout.VerticalLayout;
import com.leo.cse.util.GraphicsHelper;
import com.leo.cse.util.LinksHelper;
import com.leo.cse.util.MathUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class AboutDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(480, 381);

    public AboutDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "About", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setLocationRelativeTo(parentComponent);

        final Container contentPane = getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);
        contentPane.add(new AboutComponent(this));

        pack();
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static class AboutComponent extends VerticalLayout {
        private final static String TITLE = String.format("About CaveSaveEdit Pro v%s", App.VERSION);
        private final static String SUBTITLE = "Save files editor for Cave Story and Cave Story+";
        private final static String[] DEVELOPERS = {
                "Improved and refactored fork of CaveSaveEdit by Leo40Story",
                "Developed by @msalv"
        };
        private final static String[] CREDITS = {
                "\nApp icon designed by $designer_name",
                "https://behance.net/designer_name",

                "\nUI icons by Gofox",
                "https://www.flaticon.com/authors/gofox",

                "\nArcadepix font by Reekee of Dimenzioned",
                "https://www.dafont.com/reekee-of-dimenzioned.d1065",

                "\nNDS BIOS font by Aaron D. Chand",
                "https://www.dafont.com/aaron-d-chand.d6569",

                "\nBased on Kapow's profile specs",
                "https://www.cavestory.org/guides/profile.txt",

                "\nHonorable Mentions from the Original Version by @Leo40Git:",
                "Noxid @taedixon, gamemanj @20kdc, zxin @zxinmine, Carrotlord"
        };

        private static final String MESSAGE = createMessage();

        private final Image appIcon = findBiggestImage(Resources.getAppIcons());
        private final Insets insets = new Insets(18, 28, 16, 28);
        private final AboutDialog dialog;
        private final TextLabel footerMessage = new TextLabel();
        private final Queue<AsyncTask> tasks = new ConcurrentLinkedQueue<>();

        private long start = 0L;
        private final Timer timer = new Timer(16, this::animateIcon);

        private int appIconX = insets.left;
        private int appIconY = insets.top;
        private int directionX = 1;
        private int directionY = 1;

        AboutComponent(AboutDialog dialog) {
            super();
            this.dialog = dialog;
            init();
            addMouseListener(new MouseEventsListener());
        }

        private void init() {
            setBorder(new EmptyBorder(insets));
            add(initHeader(), leftMargin(80));
            initCredits();
            add(initFooter(), topMargin(16));
        }

        private Component initHeader() {
            final VerticalLayout header = new VerticalLayout();

            final TextLabel title = new TextLabel();
            title.setFont(Resources.getFontPixel().deriveFont(15f));
            title.setTextColor(ThemeData.getTextColor());
            title.setSingleLine(true);
            title.setText(TITLE);

            final TextLabel subtitle = new TextLabel();
            subtitle.setFont(Resources.getFont());
            subtitle.setTextColor(ThemeData.getTextColor());
            subtitle.setText(SUBTITLE);

            header.add(title, topMargin(2));
            header.add(subtitle);

            addMultiLineTextLabels(header, DEVELOPERS);

            return header;
        }

        private void initCredits() {
            addMultiLineTextLabels(this, CREDITS);
        }

        private Component initFooter() {
            final HorizontalLayout footer = new HorizontalLayout();
            footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

            final TextButton okayButton = new TextButton();
            final TextButton copyButton = new TextButton();

            okayButton.setText("OK");
            okayButton.setPadding(6, 5, 6, 5);
            okayButton.setMinimumSize(new Dimension(52, 24));
            okayButton.setOnClickListener(dialog::dispatchClose);

            copyButton.setText("Copy");
            copyButton.setPadding(6, 5, 6, 5);
            copyButton.setMinimumSize(new Dimension(52, 24));
            copyButton.setVisible(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
            copyButton.setOnClickListener(() -> {
                copy(copyButton);
            });

            footerMessage.setFont(Resources.getFont());
            footerMessage.setTextColor(ThemeData.getTextColor());
            footerMessage.setSingleLine(true);
            footerMessage.setVisible(false);

            footer.add(okayButton, alignRight());
            footer.add(copyButton, alignRight(rightMargin(4)));
            footer.add(footerMessage, centerVertical(alignRight(rightMargin(8))));

            return footer;
        }

        private void copy(TextButton button) {
            final StringBuilder sb = new StringBuilder();
            sb.append(TITLE).append('\n').append(SUBTITLE).append('\n');

            for (String line : DEVELOPERS) {
                sb.append(line).append('\n');
            }
            for (String line : CREDITS) {
                sb.append(line).append('\n');
            }

            try {
                final StringSelection selection = new StringSelection(sb.toString().trim());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                footerMessage.setText("Text copied to clipboard!");
                footerMessage.setVisible(true);
                hideFooterMessageDelayed();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to copy text",
                        "Well, this is awkward",
                        JOptionPane.ERROR_MESSAGE);
                button.setEnabled(false);
                AppLogger.info("Unable to copy about info");
            }
        }

        private void hideFooterMessageDelayed() {
            final AsyncTask task = new DelayedTask(3000L, () -> footerMessage.setVisible(false)).execute();
            tasks.add(task);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(ThemeData.getAccentColor());
            g.setFont(Resources.getFont());
            GraphicsHelper.drawTextCentered(g, MESSAGE, insets.left + 32, insets.top + 32);

            g.setColor(ThemeData.getAccentColor());
            g.drawRect(insets.left, insets.top, 63, 63);

            g.drawImage(appIcon, appIconX, appIconY, 64, 64, null);

            g.setColor(ThemeData.getForegroundColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        @Override
        public void removeNotify() {
            timer.stop();
            for (AsyncTask task : tasks) {
                if (!task.isFinished() && !task.isRunning()) {
                    task.cancel(false);
                }
            }
            super.removeNotify();
        }

        private int speed = 1;

        private void animateIcon(ActionEvent e) {
            final int minX = 0;
            final int maxX = getWidth() - appIcon.getWidth(null);
            final int minY = 0;
            final int maxY = getHeight() - appIcon.getHeight(null);

            appIconX = MathUtils.coerceIn(appIconX + directionX * speed, minX, maxX);
            appIconY = MathUtils.coerceIn(appIconY + directionY * speed, minY, maxY);

            if (e.getWhen() - start >= 3000L) {
                start = e.getWhen();
                speed = Math.min(speed + 1, 8);
            }

            if (appIconX >= maxX) {
                directionX = -1;
            } else if (appIconX <= minX) {
                directionX = 1;
            }

            if (appIconY >= maxY) {
                directionY = -1;
            } else if (appIconY <= minY) {
                directionY = 1;
            }

            final int dx = Math.abs(appIconX - insets.left);
            final int dy = Math.abs(appIconY - insets.top);
            if (speed != 1 && dx > 1 && dx < 6 && dy > 1 && dy < 6) {
                appIconX = insets.left;
                appIconY = insets.top;
                timer.stop();
                playSound();
            }

            repaint();
        }

        private void playSound() {
            final Object runnable = Toolkit.getDefaultToolkit()
                    .getDesktopProperty("win.sound.exclamation");
            if (runnable instanceof Runnable) {
                ((Runnable)runnable).run();
            }
        }

        private class MouseEventsListener extends MouseAdapter {
            private final static int CLICKS_COUNT = 1 << 3;
            private int clicks = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (++clicks == CLICKS_COUNT) {
                    animateIcon();
                }
            }

            private void animateIcon() {
                final Image appIcon = AboutComponent.this.appIcon;
                if (appIcon == null) {
                    return;
                }

                start = System.currentTimeMillis();
                timer.restart();
            }
        }

        // --- Static helpers ---

        private static Image findBiggestImage(List<Image> images) {
            Image result = null;
            int size = 0;

            for (Image image : images) {
                final int width = image.getWidth(null);
                if (width > size) {
                    result = image;
                    size = width;
                }
            }

            return result;
        }

        private static void addMultiLineTextLabels(Container container, String[] text) {
            for (String paragraph : text) {
                final String trimmed = paragraph.trim();
                final TextLabel label = new TextLabel();
                label.setFont(Resources.getFont());
                label.setForeground(ThemeData.getForegroundColor());
                label.setText(trimmed);

                if (trimmed.startsWith("http")) {
                    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.setOnClickListener(() -> {
                        LinksHelper.browse(trimmed);
                    });
                }

                container.add(label, paragraph.startsWith("\n") ? topMargin(12) : topMargin(2));
            }
        }

        private static String createMessage() {
            final long[] data = { 0x6f79206b6e616854L, 0x293a0a2175L };
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Long.BYTES * 2; i++) {
                final int j = i < Long.BYTES ? 0 : 1;
                byte code = (byte) (data[j] >> (i % Long.BYTES) * Byte.SIZE);
                if (code != 0) {
                    sb.appendCodePoint(code);
                }
            }
            return sb.toString();
        }
    }
}
