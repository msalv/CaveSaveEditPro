package com.leo.cse.frontend.ui.components.text;

import com.leo.cse.frontend.ui.FontChecker;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.util.ColorUtils;
import com.leo.cse.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

public class TextLabel extends JComponent {
    private String text;
    private AttributedString styledText;
    private AttributedString ellipsizedStyledText;
    private boolean isSingleLine;
    private int borderWidth = 0;
    private Color borderColor;
    private Color disabledBorderColor;
    private int gravity = Gravity.LEFT | Gravity.TOP;

    private boolean hovered = false;

    private Color textColor;
    private Color disabledTextColor;

    private LineBreakMeasurer measurer;
    private final List<TextLayout> textLayouts = new ArrayList<>();
    private final Dimension measuredSize = new Dimension(0, 0);
    private final Dimension measuredTextSize = new Dimension(0, 0);

    private final MouseListener mouseListener = new MouseEventsListener();
    private Runnable clickListener;

    private Font font;
    private final Font fallbackFont = ThemeData.getFallbackFont();

    public TextLabel() {
        super();
        setTextColor(ThemeData.getTextColor());
        setBorderColor(ThemeData.getForegroundColor());
        addMouseListener(mouseListener);
    }

    public void setText(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = (text != null) ? text : "";
            styledText = StringUtils.newAttributedString(this.text, getFont());
            ellipsizedStyledText = null;
            revalidate();
            repaint();
        }
    }

    public void setSingleLine(boolean isSingleLine) {
        if (this.isSingleLine != isSingleLine) {
            this.isSingleLine = isSingleLine;
            revalidate();
            repaint();
        }
    }

    public void setGravity(int gravity) {
        if (this.gravity != gravity) {
            this.gravity = gravity;
            revalidate();
            repaint();
        }
    }

    @Override
    public void revalidate() {
        measurer = null;
        super.revalidate();
    }

    public void setBorderWidth(int borderWidth) {
        if (this.borderWidth != borderWidth) {
            this.borderWidth = borderWidth;
            repaint();
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        setBorder(new EmptyBorder(top, left, bottom, right));
    }

    @Override
    public void setFont(Font font) {
        final Font oldFont = this.font;
        if (oldFont == null || !oldFont.equals(font)) {
            if (text != null) {
                final Font checkedFont = FontChecker.getInstance(font).canFontDisplayText(text) ? font : fallbackFont;
                styledText = StringUtils.newAttributedString(text, checkedFont);
                ellipsizedStyledText = null;
                revalidate();
                repaint();
            }
            this.font = font;
            super.setFont(font);
        }
    }

    @Override
    public Font getFont() {
        if (font != null && FontChecker.getInstance(font).canFontDisplayText(text)) {
            return font;
        } else {
            return fallbackFont;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) {
            return;
        }

        removeMouseListener(mouseListener);

        super.setEnabled(enabled);

        if (enabled) {
            setForeground(textColor);
            addMouseListener(mouseListener);
        } else {
            hovered = false;
            setForeground(getTextColorDisabled());
        }

        repaint();
    }

    public void setClickable(boolean isClickable) {
        removeMouseListener(mouseListener);
        if (isClickable) {
            addMouseListener(mouseListener);
        }
    }

    public void setTextColor(Color color) {
        if (color == null || !color.equals(textColor)) {
            textColor = color;
            if (isEnabled()) {
                setForeground(color);
            }
        }
    }

    private void setBorderColor(Color color) {
        if (color == null || !color.equals(borderColor)) {
            borderColor = color;
            disabledBorderColor = ColorUtils.setAlphaComponent(color, 31);
            repaint();
        }
    }

    public void setTextColorDisabled(Color color) {
        if (color == null || !color.equals(disabledTextColor)) {
            disabledTextColor = color;
            if (!isEnabled()) {
                setForeground(getTextColorDisabled());
            }
        }
    }

    private Color getTextColorDisabled() {
        if (disabledTextColor != null) {
            return disabledTextColor;
        }
        return ThemeData.getTextColorDisabled();
    }

    public void setOnClickListener(Runnable action) {
        clickListener = action;
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            if (measurer == null) {
                measureText();
            }
            return super.getPreferredSize();
        }

        if (measurer != null) {
            return measuredSize;
        }

        return measureText();
    }

    private Dimension measureText() {
        textLayouts.clear();

        final Dimension prefSize = isPreferredSizeSet() ? super.getPreferredSize() : null;
        final Dimension minSize = isMinimumSizeSet() ? getMinimumSize() : prefSize;
        final Dimension maxSize = isMaximumSizeSet() ? getMaximumSize() : prefSize;

        final int minWidth = minSize != null ? minSize.width : 0;
        final int maxWidth = maxSize != null ? maxSize.width : Integer.MAX_VALUE;
        final int minHeight = minSize != null ? minSize.height : 0;
        final int maxHeight = maxSize != null ? maxSize.height : Integer.MAX_VALUE;

        if (StringUtils.isNullOrEmpty(text)) {
            measuredTextSize.setSize(0, 0);
            measuredSize.setSize(minWidth, minHeight);
            return measuredSize;
        }

        final Insets insets = getInsets();

        final int wrappingWidth = Math.max(0, maxWidth - (insets.right + insets.left));

        final Font font = getFont();
        final FontMetrics fontMetrics = getFontMetrics(font);
        final FontRenderContext frc = fontMetrics.getFontRenderContext();

        final AttributedString styledText;

        if (isSingleLine) {
            if (ellipsizedStyledText == null) {
                final String ellipsizedText = StringUtils.ellipsize(text, fontMetrics, wrappingWidth);
                ellipsizedStyledText = StringUtils.newAttributedString(ellipsizedText, font);
            }
            styledText = ellipsizedStyledText;
        } else {
            styledText = this.styledText;
        }

        final AttributedCharacterIterator iterator = styledText.getIterator();
        measurer = new LineBreakMeasurer(iterator, frc);

        int height = 0;
        int width = 0;

        while (measurer.getPosition() < iterator.getEndIndex()) {
            final TextLayout layout = measurer.nextLayout(wrappingWidth);
            height += layout.getAscent() + layout.getDescent() + layout.getLeading();
            width = Math.max(width, (int) layout.getAdvance());
            textLayouts.add(layout);
        }

        measuredTextSize.setSize(width, height);

        int viewWidth = Math.min(maxWidth, Math.max(width + insets.left + insets.right, minWidth));
        int viewHeight = Math.max(Math.min(height + insets.top + insets.bottom, maxHeight), Math.min(minHeight, maxHeight));
        measuredSize.setSize(viewWidth, viewHeight);

        return measuredSize;
    }

    private static String printLayout(TextLayout layout) {
        final String s = layout.toString();
        final String t = "chars:\"";
        final int start = s.indexOf(t) +  t.length();
        final int end = s.indexOf('"', start);
        final String[] chars = s.substring(start, end).split(" ");
        final StringBuilder sb = new StringBuilder();

        for (String ch : chars) {
            sb.append(Character.toChars(Integer.parseInt(ch, 16)));
        }

        return sb.toString();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hovered) {
            g.setColor(ThemeData.getHoverColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (isBackgroundSet()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        if (borderWidth > 0) {
            g.setColor(isEnabled() ? borderColor : disabledBorderColor);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        final Graphics graphics = getComponentGraphics(g);

        if (StringUtils.isNullOrEmpty(text)) {
            return;
        }

        final Insets insets = getInsets();
        final int left = insets.left;
        final int top = insets.top;
        final int right = getWidth() - insets.right;
        final int bottom = getHeight() - insets.bottom - 1;

        paintText(graphics, left, top, right, bottom);
    }

    private void paintText(Graphics g, int left, int top, int right, int bottom) {
        final Graphics2D g2d = (Graphics2D) g;
        final float wrappingWidth = right - left;

        int y;

        if (Gravity.isSet(gravity, Gravity.CENTER_VERTICAL)) {
            y = top + (bottom - top - measuredTextSize.height) / 2;
        } else if (Gravity.isSet(gravity, Gravity.BOTTOM)) {
            y = bottom - measuredTextSize.height;
        } else {
            y = top;
        }

        for (int i = 0; i < textLayouts.size() && y < bottom; ++i) {
            final TextLayout layout = textLayouts.get(i);

            y += layout.getAscent();

            final float dx;
            if (Gravity.isSet(gravity, Gravity.CENTER_HORIZONTAL)) {
                dx = (wrappingWidth - layout.getAdvance()) / 2;
            } else if (layout.isLeftToRight()) {
                dx = (Gravity.isSet(gravity, Gravity.RIGHT)) ? (wrappingWidth - layout.getAdvance()) : 0;
            } else {
                dx = (Gravity.isSet(gravity, Gravity.RIGHT)) ? 0 : (wrappingWidth - layout.getAdvance());
            }

            layout.draw(g2d, left + dx, y);
            y += layout.getDescent() + layout.getLeading();
        }
    }

    private class MouseEventsListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                clickListener.run();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = true;
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (isEnabled() && clickListener != null) {
                hovered = false;
                repaint();
            }
        }
    }
}
