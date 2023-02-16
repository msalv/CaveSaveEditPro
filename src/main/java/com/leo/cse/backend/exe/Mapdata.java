package com.leo.cse.backend.exe;

/**
 * Stores an information about a map. An instance of {@link MapInfo} should
 * be used for loading maps.
 *
 * @author Leo
 *
 */
public class Mapdata {
	/**
	 * The map's ID.
	 */
	private final int mapNum;
	/**
	 * The map's tileset.
	 */
	private final String tileset;
	/**
	 * The map's file name.
	 */
	private final String fileName;
	/**
	 * The map's scroll type.
	 */
	private final int scrollType;
	/**
	 * The map's background image.
	 */
	private final String bgName;
	/**
	 * The map's 1st NPC sheet.
	 */
	private final String npcSheet1;
	/**
	 * The map's 2nd NPC sheet.
	 */
	private final String npcSheet2;
	/**
	 * The map's special boss ID.
	 */
	private final int bossNum;
	/**
	 * The map's name.
	 */
	private final String mapName;
	/**
	 * The map's name in Japanese. CS+ only.
	 */
	private final byte[] jpName;

	public Mapdata(int mapNum, String tileset, String fileName, int scrollType, String bgName, String npcSheet1, String npcSheet2, int bossNum, String mapName, byte[] jpName) {
		this.mapNum = mapNum;
		this.tileset = tileset;
		this.fileName = fileName;
		this.scrollType = scrollType;
		this.bgName = bgName;
		this.npcSheet1 = npcSheet1;
		this.npcSheet2 = npcSheet2;
		this.bossNum = bossNum;
		this.mapName = mapName;
		this.jpName = jpName;
	}

	public Mapdata(int mapNum, String tileset, String fileName, int scrollType, String bgName, String npcSheet1, String npcSheet2, int bossNum, String mapName) {
		this(mapNum, tileset, fileName, scrollType, bgName, npcSheet1, npcSheet2, bossNum, mapName, new byte[0x20]);
	}

	/**
	 * Gets the map's tileset.
	 *
	 * @return tileset
	 */
	public String getTileset() {
		return tileset;
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
	 * Gets the map's background image.
	 *
	 * @return background image
	 */
	public String getBgName() {
		return bgName;
	}

	/**
	 * Gets the map's 1st NPC sheet.
	 *
	 * @return 1st NPC sheet
	 */
	public String getNpcSheet1() {
		return npcSheet1;
	}

	/**
	 * Gets the map's 2nd NPC sheet.
	 *
	 * @return 2nd NPC sheet
	 */
	public String getNpcSheet2() {
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

	@Override
	public String toString() {
		return "Mapdata{" +
				"mapNum=" + mapNum +
				", tileset='" + tileset + '\'' +
				", fileName='" + fileName + '\'' +
				", scrollType=" + scrollType +
				", bgName='" + bgName + '\'' +
				", npcSheet1='" + npcSheet1 + '\'' +
				", npcSheet2='" + npcSheet2 + '\'' +
				", bossNum=" + bossNum +
				", mapName='" + mapName + '\'' +
				'}';
	}
}
