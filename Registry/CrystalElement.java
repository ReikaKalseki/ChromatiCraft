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


public enum CrystalElement {

	BLACK(""), //Hostile/Mob
	RED(""), //Durability
	GREEN(""), //Sickness
	BROWN(""), //Sustenance/Eating/Food
	BLUE(""), //Visibility/Darkness
	PURPLE(""), //Enchantment/Enhancement
	CYAN(""), //Aquatic
	LIGHTGRAY(""), //(move slowdown)
	GRAY(""), //(weakness)
	PINK(""), //Combat/Strength
	LIME(""), //Mobility/Agility
	YELLOW(""), //Energy
	LIGHTBLUE(""), //Time/Acceleration
	MAGENTA(""), //Health
	ORANGE(""), //Fire/Heat
	WHITE(""); //Purity/Clarity

	public final ReikaDyeHelper color;
	public final String displayName;

	private CrystalElement(String n) {
		color = ReikaDyeHelper.getColorFromDamage(this.ordinal());
		displayName = n;
	}

	public String getEnglishName() {
		return color.colorName;
	}

}
