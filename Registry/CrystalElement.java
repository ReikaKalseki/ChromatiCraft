/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;


public enum CrystalElement {

	BLACK(""), //Hostile/Mob/Evil/Void
	RED(""), //Durability/Protection/Resistance
	GREEN(""), //Sickness
	BROWN(""), //Sustenance/Eating/Food
	BLUE(""), //Visibility/Darkness/Light
	PURPLE(""), //Enchantment/Enhancement
	CYAN(""), //Aquatic/Water
	LIGHTGRAY(""), //stealth,deception
	GRAY(""), //(weakness,fraility)
	PINK(""), //Combat/Strength
	LIME(""), //Mobility/Agility
	YELLOW(""), //Energy
	LIGHTBLUE(""), //Time/Acceleration
	MAGENTA(""), //Health/Healing/Life
	ORANGE(""), //Fire/Heat
	WHITE(""); //Purity/Clarity

	public final ReikaDyeHelper color;
	public final String displayName;
	private Icon glowIcon;

	public static final CrystalElement[] elements = values();

	private CrystalElement(String n) {
		color = ReikaDyeHelper.getColorFromDamage(this.ordinal());
		displayName = n;
	}

	public String getEnglishName() {
		return color.colorName;
	}

	public int getLevel() {
		switch(this) {
		case BLACK:
		case BLUE:
		case BROWN:
		case GREEN:
		case RED:
		case WHITE:
		case YELLOW:
			return 0;
		case CYAN:
		case LIGHTBLUE:
		case GRAY:
		case LIME:
		case ORANGE:
		case PURPLE:
		case PINK:
			return 1;
		case LIGHTGRAY:
		case MAGENTA:
			return 2;
		default:
			return -1;
		}
	}

	public boolean isPrimary() {
		return this.getLevel() == 0;
	}

	public void setIcons(IconRegister ico) {
		glowIcon = ico.registerIcon("chromaticraft:runes/glow/tile"+this.ordinal()+"_0");
	}

	public Icon getGlowRune() {
		return glowIcon;
	}

	public Icon getBlockRune() {
		return ChromaBlocks.RUNE.getBlockVariable().getIcon(0, this.ordinal());
	}

	public static CrystalElement randomElement() {
		return elements[ReikaDyeHelper.getRandomColor().ordinal()];
	}

}
