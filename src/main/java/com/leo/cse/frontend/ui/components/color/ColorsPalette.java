package com.leo.cse.frontend.ui.components.color;

import com.leo.cse.util.async.IndeterminateTask;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.util.ColorUtils;
import com.leo.cse.util.MathUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ColorsPalette extends JComponent {

    private final static Color[] SEGMENTS = {
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA
    };

    private final static int POINTER_SIZE = 18;

    private BufferedImage image = null;
    private Color selectedColor = Color.RED;
    private float lightness = 0.5f;

    private final BufferedImage pointerImage = new BufferedImage(POINTER_SIZE, POINTER_SIZE, BufferedImage.TYPE_INT_ARGB);
    private final Point pointerPosition = new Point(0, 0);

    private GradientTask gradientTask;
    private InvalidatePointerTask invalidatePointerTask;

    private OnColorChangedListener onColorChangedListener;

    public ColorsPalette() {
        initPointerImage();

        final MouseListener mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    private void initPointerImage() {
        final Graphics g = pointerImage.getGraphics();
        g.setColor(Color.BLACK);

        g.fillRect(POINTER_SIZE / 2 - 1, 0, 2, 5);
        g.fillRect(POINTER_SIZE / 2 - 1, POINTER_SIZE - 5, 2, 5);

        g.fillRect(0, POINTER_SIZE / 2 - 1, 5, 2);
        g.fillRect(POINTER_SIZE - 5, POINTER_SIZE / 2 - 1, 5, 2);
    }

    public void setSelectedColor(Color color) {
        final float lightness = ColorUtils.getLightness(color);
        if (!Objects.equals(selectedColor, color) || this.lightness != lightness) {
            this.selectedColor = color;
            this.lightness = lightness;
            if (image != null) {
                schedulePointerRepaint();
            }
        }
    }

    public void setLightness(float lightness) {
        if (this.lightness != lightness) {
            this.lightness = lightness;
            repaint();
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.onColorChangedListener = onColorChangedListener;
    }

    public Color getColor() {
        return ColorUtils.setLightness(selectedColor, lightness);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            scheduleImageCreation(getWidth(), getHeight());
        } else {
            g.drawImage(image, 0, 0, null);
            g.drawImage(pointerImage, pointerPosition.x, pointerPosition.y, null);
        }

        g.setColor(ThemeData.getForegroundColor());
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private void onMouseEvent(int x, int y) {
        if (image == null) {
            return;
        }

        final int px = MathUtils.coerceIn(x, 0, image.getWidth() - 1);
        final int py = MathUtils.coerceIn(y, 0, image.getHeight() - 1);

        final int rgb = image.getRGB(px, py);
        if (rgb != selectedColor.getRGB()) {
            selectedColor = new Color(rgb);
            if (onColorChangedListener != null) {
                onColorChangedListener.onColorChanged(getColor());
            }
        }
        updatePointerPosition(px, py);
    }

    private void updatePointerPosition(int x, int y) {
        if (pointerPosition.x != x || pointerPosition.y != y) {
            pointerPosition.x = x - POINTER_SIZE / 2;
            pointerPosition.y = y - POINTER_SIZE / 2;
            repaint();
        }
    }

    private void scheduleImageCreation(int width, int height) {
        if (gradientTask == null) {
            final GradientTask task = new GradientTask(width, height);
            gradientTask = task;
            task.execute();
        }
    }

    private void schedulePointerRepaint() {
        if (invalidatePointerTask == null) {
            final InvalidatePointerTask task = new InvalidatePointerTask();
            invalidatePointerTask = task;
            task.execute();
        }
    }

    private void cancelImageCreationTask() {
        final GradientTask gradientTask = this.gradientTask;
        if (gradientTask != null) {
            gradientTask.cancel(true);
            this.gradientTask = null;
        }
    }

    private void cancelInvalidatePointerTask() {
        final InvalidatePointerTask invalidatePointerTask = this.invalidatePointerTask;
        if (invalidatePointerTask != null) {
            invalidatePointerTask.cancel(true);
            this.invalidatePointerTask = null;
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        cancelImageCreationTask();
        cancelInvalidatePointerTask();
    }

    private static Point getPointerPosition(BufferedImage bitmap, Color color) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        int x = 0;
        int y = 0;

        double distance = Double.MAX_VALUE;
        final int h = ColorUtils.getHueColor(color).getRGB();

        for (int i = 0; i < width - 1; ++i) {
            final int pixelColor = bitmap.getRGB(i, 0);
            final double diff = Math.abs(ColorUtils.distance(h, pixelColor));
            if (diff < distance) {
                x = i;
                distance = diff;
            }
        }

        distance = Double.MAX_VALUE;
        final int s = ColorUtils.setLightness(color, 0.5f).getRGB();

        for (int j = 0; j < height - 1; ++j) {
            final int pixelColor = bitmap.getRGB(x, j);
            final double diff = Math.abs(ColorUtils.distance(s, pixelColor));
            if (diff < distance) {
                y = j;
                distance = diff;
            }
        }

        return new Point(x, y);
    }

    private static BufferedImage createBitmap(int width, int height) {
        final int[] colors = new int[width * height];

        final int spanCount = SEGMENTS.length;
        final int spanWidth = width / spanCount;
        final int rem = width % spanCount;

        for (int j = 0; j < spanCount; ++j) {
            for (int i = spanWidth * j, z = 0; i < spanWidth * (j+1); ++i, ++z) {
                float ratio = (z / (float) spanWidth);
                final int color = ColorUtils.blendARGB(SEGMENTS[j].getRGB(), SEGMENTS[(j + 1) % spanCount].getRGB(), ratio);

                for (int k = 0; k < height; ++k) {
                    final float saturation = 1 - k / (float)height;
                    colors[i + k * width] = ColorUtils.setSaturation(color, saturation);
                }
            }
        }

        // fill remaining space with nearest colors
        for (int i = width - rem; i < width; ++i) {
            for (int k = 0; k < height; ++k) {
                colors[i + k * width] = colors[i - 1 + k * width];
            }
        }

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, colors);
        return image;
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            onMouseEvent(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            onMouseEvent(e.getX(), e.getY());
        }
    }

    private class GradientTask extends IndeterminateTask<Void> {
        private final int width;
        private final int height;

        private GradientTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected Void doInBackground() {
            final BufferedImage bitmap = createBitmap(width, height);
            final Point point = getPointerPosition(bitmap, selectedColor);

            SwingUtilities.invokeLater(() -> {
                ColorsPalette.this.image = bitmap;
                ColorsPalette.this.updatePointerPosition(point.x, point.y);
                ColorsPalette.this.repaint();
            });

            return null;
        }
    }

    private class InvalidatePointerTask extends IndeterminateTask<Point> {
        @Override
        protected Point doInBackground() {
            final BufferedImage bitmap = ColorsPalette.this.image;
            if (bitmap == null) {
                return null;
            }
            return getPointerPosition(bitmap, selectedColor);
        }

        @Override
        protected void onPostExecute(Point point) {
            ColorsPalette.this.updatePointerPosition(point.x, point.y);
            ColorsPalette.this.invalidatePointerTask = null;
        }
    }

    public interface OnColorChangedListener {
        void onColorChanged(Color color);
    }
}
