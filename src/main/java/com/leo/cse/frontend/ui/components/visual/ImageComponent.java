package com.leo.cse.frontend.ui.components.visual;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

public class ImageComponent extends JComponent {
	private final MouseListener mouseEventListener = new MouseEventsListener();
	private Image image;
	private Color placeholder;
	private ScaleType scaleType = ScaleType.NONE;
	private Runnable clickListener;

	public ImageComponent() {
		super();
		addMouseListener(mouseEventListener);
	}

	public void setImage(Image image) {
		if (this.image != image) {
			this.image = image;
			repaint();
		}
	}

	public void setPlaceholderColor(Color placeholder) {
		if (placeholder != null && !placeholder.equals(this.placeholder)) {
			this.placeholder = placeholder;
			repaint();
		}
	}

	public void setScaleType(ScaleType scaleType) {
		if (this.scaleType != scaleType) {
			this.scaleType = scaleType;
			repaint();
		}
	}

	public void setOnClickListener(Runnable clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	public void setEnabled(boolean enable) {
		if (isEnabled() != enable) {
			removeMouseListener(mouseEventListener);
			if (enable) {
				addMouseListener(mouseEventListener);
			}
			super.setEnabled(enable);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		if (image != null) {
			if (scaleType == ScaleType.FIT_XY) {
				g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			} else if (scaleType == ScaleType.CENTER_CROP) {
				final int imageWidth = image.getWidth(null);
				final int imageHeight = image.getHeight(null);
				final int dx = (imageWidth - getWidth()) / 2;
				final int dy = (imageHeight - getHeight()) / 2;
				g.drawImage(image,
						0,
						0,
						getWidth(),
						getHeight(),
						dx,
						dy,
						imageWidth - dx,
						imageHeight - dy,
						null);
			} else {
				g.drawImage(image, 0, 0, null);
			}
		} else if (placeholder != null) {
			g.setColor(placeholder);
			g.fillRect(0, 0, getWidth(), getHeight());
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
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isEnabled() && clickListener != null) {
				setCursor(null);
			}
		}
	}

	public enum ScaleType {
		NONE,
		FIT_XY,
		CENTER_CROP
	}
}
