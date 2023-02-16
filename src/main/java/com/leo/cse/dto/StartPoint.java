package com.leo.cse.dto;

/**
 * Stores information about the game's starting point.
 *
 * @author Leo
 */
public class StartPoint {
    /**
     * Starting map.
     */
    public final int map;

    /**
     * Starting X position.
     */
    public final short positionX;

    /**
     * Starting Y position.
     */
    public final short positionY;

    /**
     * Starting direction.
     */
    public final int direction;

    /**
     * Starting current health.
     */
    public final short curHealth;

    /**
     * Starting maximum health.
     */
    public final short maxHealth;

    public static StartPoint DEFAULT = new StartPoint(13, (short)10, (short)8, 2, (short)3, (short)3);

    public StartPoint(int map, short positionX, short positionY, int direction, short curHealth, short maxHealth) {
        this.map = map;
        this.positionX = positionX;
        this.positionY = positionY;
        this.direction = direction;
        this.curHealth = curHealth;
        this.maxHealth = maxHealth;
    }
}
