package com.leo.cse.frontend;

import static javax.swing.SwingConstants.EAST;
import static javax.swing.SwingConstants.NORTH;
import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingConstants.WEST;

import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.util.ArrayUtils;
import com.leo.cse.util.TintHelper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

public class Resources {
	private static final String[] APP_ICON_SIZES = new String[] { "32", "64" };

	private Resources() {
	}

	private static Font font;
	private static Font fontPixel;
	private static Font fontPixelLarge;

	// base images
	private final static List<BufferedImage> appIcons = new ArrayList<>();

	// colored images
	private static BufferedImage arrowDown;
	private static BufferedImage arrowUp;
	private static BufferedImage arrowLeft;
	private static BufferedImage arrowRight;

	// uncolored images
	private final static BufferedImage[] tabIcons = new BufferedImage[8];
	private final static BufferedImage[] icons = new BufferedImage[8];

	public static void loadIcons() throws IOException {
		appIcons.clear();
		for (String s : APP_ICON_SIZES) {
			final InputStream is = Resources.class.getResourceAsStream(String.format("/appicon%s.png", s));
			appIcons.add(ImageIO.read(Objects.requireNonNull(is)));
		}
	}

	public static void loadFonts() {
		try (InputStream is = Resources.class.getResourceAsStream("/arcadepi.ttf")) {
			final Font f = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(is));
			fontPixel = f.deriveFont(Font.PLAIN, 10.0f);
			fontPixelLarge = f.deriveFont(Font.PLAIN, 50.0f);
		} catch (Exception e) {
			fontPixel = ThemeData.getFallbackFont();
			fontPixelLarge = fontPixel.deriveFont(Font.PLAIN, 48f);
		}

		try (InputStream is = Resources.class.getResourceAsStream("/ndsbios.ttf")) {
			final Font f = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(is));
			font = f.deriveFont(Font.PLAIN, 16f);
		} catch (Exception e) {
			font = ThemeData.getFallbackFont();
		}
	}

	public static void loadUI(Color tint) throws IOException {
		final InputStream is = Resources.class.getResourceAsStream("/ui.png");
		final BufferedImage ui = ImageIO.read(Objects.requireNonNull(is));

		final BufferedImage down = ui.getSubimage(80, 0, 8, 8);
		arrowDown = TintHelper.tint(down, tint, 1f);

		final BufferedImage up = ui.getSubimage(80, 8, 8, 8);
		arrowUp = TintHelper.tint(up, tint, 1f);

		final BufferedImage left = ui.getSubimage(88, 0, 8, 8);
		arrowLeft = TintHelper.tint(left, tint, 1f);

		final BufferedImage right = ui.getSubimage(88, 8, 8, 8);
		arrowRight = TintHelper.tint(right, tint, 1f);

		for (int i = 0; i < tabIcons.length; i++) {
			tabIcons[i] = ui.getSubimage(i * 16, 16, 16, 16);
		}

		int tbx = 0;
		int tby = 16;

		for (int i = 0; i < icons.length; i++) {
			if (i % 10 == 0) {
				tby += 16;
			}
			icons[i] = ui.getSubimage(tbx, tby, 16, 16);
			tbx += 16;
		}
	}

	public static Image getTabIcon(int iconId) {
		return ArrayUtils.getOrDefault(tabIcons, iconId, null);
	}

	public static Image getMenuIcon(int iconId) {
		return ArrayUtils.getOrDefault(icons, iconId, null);
	}

	public static List<Image> getAppIcons() {
		return new ArrayList<>(appIcons);
	}

	public static Image getArrowIcon(int direction) {
		final Image icon;
		switch (direction) {
			case WEST:
				icon = Resources.arrowLeft;
				break;
			case NORTH:
				icon = Resources.arrowUp;
				break;
			case EAST:
				icon = Resources.arrowRight;
				break;
			case SOUTH:
			default:
				icon = Resources.arrowDown;
				break;
		}
		return icon;
	}

	public static Font getFont() {
		return font;
	}

	public static Font getFontPixel() {
		return fontPixel;
	}

	public static Font getFontPixelLarge() {
		return fontPixelLarge;
	}
}
