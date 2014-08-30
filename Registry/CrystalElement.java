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

import java.awt.Color;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;


public enum CrystalElement {

	BLACK("Kuro", 0x191919), //Hostile/Mob/Evil/Void
	RED("Karmir", 0xFF0000), //Durability/Protection/Resistance
	GREEN("Kijani", 0x007F0E), //Sickness
	BROWN("Ruskea", 0x724528), //Sustenance/Eating/Food
	BLUE("Nila", 0x0026FF), //Visibility/Darkness/Light
	PURPLE("Zambarau", 0x8C00EA), //Enchantment/Enhancement
	CYAN("Vadali", 0x009FBF), //Aquatic/Water
	LIGHTGRAY("Argia", 0x979797), //stealth,deception
	GRAY("Ykri", 0x404040), //(weakness,fraility)
	PINK("Ruzova", 0xFFBAD9), //Combat/Strength
	LIME("Asveste", 0x00FF00), //Mobility/Agility
	YELLOW("Kitrino", 0xFFFF00), //Energy
	LIGHTBLUE("Galazio", 0x7FD4FF), //Time/Acceleration
	MAGENTA("Kurauri", 0xFF00DC), //Health/Healing/Life
	ORANGE("Portokali", 0xFF6A00), //Fire/Heat
	WHITE("Tahara", 0xFFFFFF); //Purity/Clarity

	private final ReikaDyeHelper color;
	public final String displayName;
	private IIcon glowIcon;
	private IIcon animatedFace;
	private final int rgb;

	public static final CrystalElement[] elements = values();

	private CrystalElement(String n, int rgb) {
		color = ReikaDyeHelper.getColorFromDamage(this.ordinal());
		displayName = n;
		this.rgb = (255<<24)+rgb;
	}

	public String getEnglishName() {
		return color.colorName;
	}

	public int getColor() {
		return rgb;
	}

	public int getRed() {
		return (this.getColor() >> 16) & 255;
	}

	public int getGreen() {
		return (this.getColor() >> 8) & 255;
	}

	public int getBlue() {
		return this.getColor() & 255;
	}

	public Color getJavaColor() {
		return new Color(rgb);
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
		animatedFace = ico.registerIcon("chromaticraft:runes/frontpng/tile"+this.ordinal()+"_0");
	}

	public IIcon getGlowRune() {
		return glowIcon;
	}

	public IIcon getFaceRune() {
		return animatedFace;
	}

	public IIcon getBlockRune() {
		return ChromaBlocks.RUNE.getBlockInstance().getIcon(0, this.ordinal());
	}

	public static CrystalElement randomElement() {
		return elements[ReikaDyeHelper.getRandomColor().ordinal()];
	}

}
