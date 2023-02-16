package com.leo.cse.frontend.dialogs;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingPayload;
import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.layout.RootDialog;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;

import javax.swing.border.EmptyBorder;

public class ResourcesLoaderDialog extends RootDialog {
    private final static Dimension CONTENT_SIZE = new Dimension(380, 126);

    private ResourcesLoaderDialog(
            Frame parentFrame,
            Component parentComponent) {
        super(parentFrame, "Loading", true);

        setMinimumSize(CONTENT_SIZE);

        setResizable(false);
        setUndecorated(true);
        setLocationRelativeTo(parentComponent);
    }

    public static void show(
            Frame parentFrame,
            Component parentComponent,
            GameResourcesManager resourcesManager) {

        final ResourcesLoaderDialog dialog = new ResourcesLoaderDialog(parentFrame, parentComponent);

        final Container contentPane = dialog.getContentPane();
        contentPane.setBackground(ThemeData.getBackgroundColor());
        contentPane.setPreferredSize(CONTENT_SIZE);

        final LoaderComponent loaderComponent = new LoaderComponent();
        resourcesManager.addListener(loaderComponent);
        loaderComponent.setOnCompleteAction(dialog::dispatchClose);
        contentPane.add(loaderComponent);

        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        resourcesManager.removeListener(loaderComponent);
    }

    private void dispatchClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static class LoaderComponent extends VerticalLayout implements OnGameResourcesLoadingStateChangeListener {
        private final static int PROGRESS_WIDTH = 260;
        private final static int PROGRESS_HEIGHT = 18;
        private final static long BIND_DELAY = 20L;

        private final TextLabel title = new TextLabel();
        private final TextLabel description = new TextLabel();
        private final TextLabel progressLabel = new TextLabel();

        private final StringBuilder stringBuilder = new StringBuilder();
        private Runnable onComplete;

        private float progress = 0f;
        private long lastEvent = 0;

        LoaderComponent() {
            super();
            init();
        }

        private void init() {
            setBorder(new EmptyBorder(18, 50, 18, 50));

            title.setFont(Resources.getFontPixel());
            title.setSingleLine(true);
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            title.setTextColor(ThemeData.getForegroundColor());
            title.setMinimumSize(new Dimension(Integer.MAX_VALUE, 11));

            progressLabel.setFont(Resources.getFont());
            progressLabel.setSingleLine(true);
            progressLabel.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            progressLabel.setTextColor(ThemeData.getTextColor());
            progressLabel.setPadding(0, 2, 0, 0);
            progressLabel.setMinimumSize(new Dimension(260, 18));

            description.setFont(Resources.getFont());
            description.setGravity(Gravity.CENTER_HORIZONTAL);
            description.setTextColor(ThemeData.getForegroundColor());
            description.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

            add(title, centerHorizontal());
            add(progressLabel, centerHorizontal(topMargin(12)));
            add(description, centerHorizontal(topMargin(14)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            final int x = progressLabel.getX();
            final int y = progressLabel.getY();

            g.setColor(ThemeData.getForegroundColor());
            g.drawRect(x, y, PROGRESS_WIDTH, PROGRESS_HEIGHT);

            if (progress > 0f) {
                g.fillRect(x + 2, y + 2, (int)((PROGRESS_WIDTH - 3) * progress + 0.5f), PROGRESS_HEIGHT - 3);
            }

            g.setColor(ThemeData.getForegroundColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        private void bind(GameResourcesLoadingPayload payload) {
            title.setText(payload.title);
            description.setText(payload.description);

            stringBuilder.setLength(0);
            if (payload.total > 0) {
                stringBuilder.append(payload.progress);
                stringBuilder.append('/');
                stringBuilder.append(payload.total);
            }
            progressLabel.setText(stringBuilder.toString());
        }

        private void bindProgress(GameResourcesLoadingPayload payload) {
            if (payload.total > 0) {
                final float progress = payload.progress / (float) payload.total;
                if (this.progress != progress) {
                    this.progress = progress;
                    repaint();
                }
            } else if (progress > 0f) {
                repaint();
            }
        }

        @Override
        public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
            if (state == GameResourcesLoadingState.IN_PROGRESS) {
                if (payload instanceof GameResourcesLoadingPayload) {
                    final long ts = System.currentTimeMillis();
                    if (ts - lastEvent > BIND_DELAY) {
                        bind((GameResourcesLoadingPayload) payload);
                        bindProgress((GameResourcesLoadingPayload) payload);
                        lastEvent = ts;
                    }
                }
            } else if (state == GameResourcesLoadingState.DONE) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }

        public void setOnCompleteAction(Runnable action) {
            this.onComplete = action;
        }
    }
}
