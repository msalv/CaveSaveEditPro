package com.leo.cse.backend.exe;

import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.leo.cse.log.AppLogger;
import com.leo.cse.backend.res.GameResources;

/**
 * Stores information for loaded maps.
 */
public class MapInfo {
	public static final int LAYER_BG = 0;
	public static final int LAYER_FG = 1;

	/**
	 * The map tiles.
	 *
	 * The first index is the layer: 0 for background, 1 for
	 * foreground.
	 *
	 * The second and third indexes are X and Y positions,
	 * respectively.
	 */
	private final int[][][] map;

	/**
	 * The map's tileset file.
	 */
	private final File tileset;

	/**
	 * The map's background image file.
	 */
	private final File bgImage;

	/**
	 * The map's file name.
	 */
	private final String fileName;

	/**
	 * The map's scroll type.
	 */
	private final int scrollType;

	/**
	 * The map's 1st NPC sheet file.
	 */
	private final File npcSheet1;

	/**
	 * The map's 2nd NPC sheet file.
	 */
	private final File npcSheet2;

	/**
	 * The map's name.
	 */
	private final String mapName;

	/**
	 * The map's PXA file.
	 */
	private final File pxaFile;

	/**
	 * List of the map's entities.
	 *
	 * @see PxeEntry
	 */
	private final List<PxeEntry> pxeList;

	public MapInfo(int[][][] map, File tileset, File bgImage, String fileName, int scrollType, File npcSheet1, File npcSheet2, String mapName, File pxaFile, List<PxeEntry> pxeList) {
		this.map = map;
		this.tileset = tileset;
		this.bgImage = bgImage;
		this.fileName = fileName;
		this.scrollType = scrollType;
		this.npcSheet1 = npcSheet1;
		this.npcSheet2 = npcSheet2;
		this.mapName = mapName;
		this.pxaFile = pxaFile;
		this.pxeList = pxeList;
	}

	/**
	 * Calculates a tile's type.
	 *
	 * @param tileNum tile ID
	 * @param resources game resources
	 * @return tile type
	 */
	public int calcPxa(int tileNum, GameResources resources) {
		final byte[] pxaData = resources.getPxa(pxaFile);
		if (pxaData == null) {
			return 0;
		}
		try {
			return pxaData[tileNum] & 0xFF;
		} catch (Exception e) {
			AppLogger.error(String.format("Could not get tile %d in PXA (length is %d)", tileNum, pxaData.length), e);
		}
		return 0;
	}

	/**
	 * Stores information about an entity.
	 *
	 * @author Leo
	 *
	 */
	public static class PxeEntry {
		/**
		 * The entity's X position, in tiles.
		 */
		private final short xTile;

		public short getX() {
			return xTile;
		}

		/**
		 * The entity's Y position, in tiles.
		 */
		private final short yTile;

		public short getY() {
			return yTile;
		}

		/**
		 * The entity's flag ID.
		 */
		private final short flagID;

		public short getFlagID() {
			return flagID;
		}

		/**
		 * The entity's event number.
		 */
		private final short eventNum;

		public short getEvent() {
			return eventNum;
		}

		/**
		 * The entity's type.
		 */
		private final short entityType;

		public short getType() {
			return entityType;
		}

		/**
		 * The entity's flags.
		 */
		private final short flags;

		public short getFlags() {
			return flags;
		}

		/**
		 * The entity's npc.tbl entry.
		 */
		private final EntityData inf;

		public EntityData getInfo() {
			return inf;
		}

		public PxeEntry(short pxeX, short pxeY, short pxeFlagID, short pxeEvent, short pxeType, short pxeFlags, EntityData pxeInf) {
			if (pxeInf == null) {
				throw new IllegalArgumentException(String.format("Entity type %s is undefined!", pxeType));
			}

			xTile = pxeX;
			yTile = pxeY;
			flagID = pxeFlagID;
			eventNum = pxeEvent;
			entityType = pxeType;
			flags = pxeFlags;
			inf = pxeInf;
		}

		/**
		 * Gets the entity's draw area.
		 *
		 * @return draw area
		 */
		public Rectangle getDrawArea(Rectangle rect) {
			final Rectangle offset = inf.getDisplay(rect);

			final int destW = (offset.width + offset.x) * 2;
			final int destH = (offset.height + offset.y) * 2;
			final int destX = xTile * 32 - offset.x * 2;
			final int destY = yTile * 32 - offset.y * 2;

			rect.setBounds(destX, destY, destW, destH);

			return rect;
		}
	}

	/**
	 * Gets the map tiles. For indexes, see {@link #map}.
	 *
	 * @return map tiles
	 * @see #map
	 */
	public int[][][] getMap() {
		return map;
	}

	/**
	 * Gets the map's tileset.
	 *
	 * @return tileset
	 */
	public File getTilesetFile() {
		return tileset;
	}

	/**
	 * Gets the map's background image.
	 *
	 * @return background image
	 */
	public File getBgImageFile() {
		return bgImage;
	}

	/**
	 * Gets the map's file name.
	 *
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the map's scroll type.
	 *
	 * @return scroll type
	 */
	public int getScrollType() {
		return scrollType;
	}

	/**
	 * Gets the map's 1st NPC sheet.
	 *
	 * @return 1st NPC sheet
	 */
	public File getNpcSheet1() {
		return npcSheet1;
	}

	/**
	 * Gets the map's 2nd NPC sheet.
	 *
	 * @return 2nd NPC sheet
	 */
	public File getNpcSheet2() {
		return npcSheet2;
	}

	/**
	 * Gets the map's name.
	 *
	 * @return display name
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Gets an iterator over the map's entities.
	 *
	 * @return iterator over elements of entity list
	 * @see #pxeList
	 */
	public Iterator<PxeEntry> getPxeIterator() {
		if (pxeList == null) {
			return null;
		}
		return pxeList.iterator();
	}

	/**
	 * Checks if there are missing assets.
	 *
	 * @return <code>true</code> if there are missing assets, <code>false</code>
	 *         otherwise.
	 */
	public static boolean hasMissingAssets(MapInfo mapInfo, GameResources resources, boolean shouldLoadNpc) {
		if (mapInfo.map == null) return true;
		if (resources.getImage(mapInfo.tileset) == null) return true;
		if (resources.getImage(mapInfo.bgImage) == null) return true;
		if (shouldLoadNpc) {
			if (resources.getImage(mapInfo.npcSheet1) == null) return true;
			if (resources.getImage(mapInfo.npcSheet2) == null) return true;
			return mapInfo.pxeList == null;
		}
		return false;
	}

	/**
	 * Returns a readable list of missing assets. Useful for reporting errors.
	 *
	 * @return list of missing assets, or an empty string if there are no missing
	 *         assets
	 */
	public static String getMissingAssets(MapInfo mapInfo, GameResources resources, boolean shouldLoadNpc) {
		if (!hasMissingAssets(mapInfo, resources, shouldLoadNpc)) {
			return "";
		}

		final String[] assetName = new String[] {
				"PXM file",
				"tileset",
				"background image",
				"NPC sheet 1",
				"NPC sheet 2",
				"PXE file"
		};

		final boolean[] assetStat = new boolean[] {
				mapInfo.map == null,
				resources.getImage(mapInfo.tileset) == null,
				resources.getImage(mapInfo.bgImage) == null,
				shouldLoadNpc && resources.getImage(mapInfo.npcSheet1) == null,
				shouldLoadNpc && resources.getImage(mapInfo.npcSheet2) == null,
				shouldLoadNpc && mapInfo.pxeList == null
		};

		final StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < assetStat.length; i++) {
			if (assetStat[i]) {
				if (stringBuilder.length() != 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(assetName[i]);
			}
		}

		if (stringBuilder.length() == 0) {
			return "";
		}

		stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
		return stringBuilder.toString();
	}
}
