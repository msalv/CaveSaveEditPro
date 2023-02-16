package com.leo.cse.backend.exe;

import java.awt.Rectangle;

/**
 * Stores information for a npc.tbl entry, plus the entity's framerect.
 *
 * @author Leo
 *
 */
public class EntityData {

	/**
	 * The entity's ID.
	 */
	private final int entityNum;

	/**
	 * Gets the entity's ID.
	 *
	 * @return entity ID
	 */
	public int getID() {
		return entityNum;
	}

	/**
	 * The entity's HP.
	 */
	private final int tbl_HP;

	/**
	 * Gets the entity's HP.
	 *
	 * @return entity HP
	 */
	public int getHP() {
		return tbl_HP;
	}

	/**
	 * The entity's display rectangle.
	 */
	private final Rectangle tbl_display;

	/**
	 * Gets the entity's display rectangle.
	 *
	 * @return entity display rect
	 */
	public Rectangle getDisplay(Rectangle rect) {
		rect.setBounds(tbl_display);
		return rect;
	}

	/**
	 * The entity's hitbox.
	 */
	private final Rectangle tbl_hitbox;

	/**
	 * Gets the entity's hitbox.
	 *
	 * @return entity hitbox
	 */
	public Rectangle getHit() {
		return new Rectangle(tbl_hitbox);
	}

	/**
	 * The entity's tileset ID.
	 */
	private final int tbl_tileset;

	/**
	 * Gets the entity's tileset ID.
	 *
	 * @return entity tileset ID
	 */
	public int getTileset() {
		return tbl_tileset;
	}

	/**
	 * The amount of EXP this entity drops on death.
	 */
	private final int tbl_exp;

	/**
	 * Gets the amount of EXP this entity drops on death.
	 *
	 * @return entity EXP drops
	 */
	public int getXP() {
		return tbl_exp;
	}

	/**
	 * The amount of damage this entity deals.
	 */
	private final int tbl_damage;

	/**
	 * Gets the amount of damage this entity deals.
	 *
	 * @return entity damage
	 */
	public int getDmg() {
		return tbl_damage;
	}

	/**
	 * The entity's flags.
	 */
	private final int tbl_flags;

	/**
	 * Get the entity's flags.
	 *
	 * @return entity flags
	 */
	public int getFlags() {
		return tbl_flags;
	}

	/**
	 * The entity's "death" sound.
	 */
	private final int tbl_deathSound;

	/**
	 * Get the entity's "death" sound.
	 *
	 * @return entity death sound
	 */
	public int getDeath() {
		return tbl_deathSound;
	}

	/**
	 * The entity's "hurt" sound.
	 */
	private final int tbl_hurtSound;

	/**
	 * Get the entity's "hurt" sound.
	 *
	 * @return entity hurt sound
	 */
	public int getHurt() {
		return tbl_hurtSound;
	}

	/**
	 * The amount of smoke this entity creates on death.
	 */
	private final int tbl_size;

	/**
	 * Gets the amount of smoke this entity creates on death.
	 *
	 * @return entity smoke amount
	 */
	public int getSize() {
		return tbl_size;
	}

	/**
	 * Creates a npc.tbl entry.
	 *
	 * @param num
	 *            entity ID
	 * @param dam
	 *            damage amount
	 * @param deathSound
	 *            death sound
	 * @param exp
	 *            EXP amount
	 * @param flags
	 *            entity flags
	 * @param hp
	 *            HP amount
	 * @param hurt
	 *            hurt sound
	 * @param size
	 *            smoke amount
	 * @param tileset
	 *            tileset ID
	 * @param display
	 *            display rectangle
	 * @param hitbox
	 *            hitbox
	 */
	public EntityData(int num, int dam, int deathSound, int exp, int flags, int hp, int hurt, int size, int tileset,
			Rectangle display, Rectangle hitbox) {
		entityNum = num;
		tbl_damage = dam;
		tbl_deathSound = deathSound;
		tbl_exp = exp;
		tbl_flags = flags;
		tbl_HP = hp;
		tbl_hurtSound = hurt;
		tbl_size = size;
		tbl_tileset = tileset;
		tbl_display = display;
		tbl_hitbox = hitbox;
	}
}