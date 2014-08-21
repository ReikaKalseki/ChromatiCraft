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

import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;


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
	private IIcon glowIcon;

	public static final CrystalElement[] elements = values();

	private CrystalElement(String n) {
		color = ReikaDyeHelper.getColorFromDamage(this.ordinal());
		displayName = this.name();//n;
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

	public void setIcons(IIconRegister ico) {
		glowIcon = ico.registerIcon("chromaticraft:runes/glow/tile"+this.ordinal()+"_0");
	}

	public IIcon getGlowRune() {
		return glowIcon;
	}

	public IIcon getBlockRune() {
		return ChromaBlocks.RUNE.getBlockInstance().getIcon(0, this.ordinal());
	}

	public static CrystalElement randomElement() {
		return elements[ReikaDyeHelper.getRandomColor().ordinal()];
	}

}
