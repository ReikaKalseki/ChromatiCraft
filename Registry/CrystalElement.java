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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


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

	private static final Random rand = new Random();

	public static final CrystalElement[] elements = values();
	private static final HashMap<Integer, ArrayList<CrystalElement>> levelMap = new HashMap();

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

	public CrystalElement mixWith(CrystalElement e) {
		return ElementMixer.instance.getMix(this, e);
	}

	public CrystalElement subtract(CrystalElement e) {
		return ElementMixer.instance.subtract(this, e);
	}

	public boolean isPrimary() {
		return this.getLevel() == 0;
	}

	@SideOnly(Side.CLIENT)
	public void setIcons(IIconRegister ico) {
		glowIcon = ico.registerIcon("chromaticraft:runes/glow/tile"+this.ordinal()+"_0");
		animatedFace = ico.registerIcon("chromaticraft:runes/frontpng/tile"+this.ordinal()+"_0");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getGlowRune() {
		return glowIcon;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getFaceRune() {
		return animatedFace;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getBlockRune() {
		return ChromaBlocks.RUNE.getBlockInstance().getIcon(0, this.ordinal());
	}

	public static CrystalElement randomElement() {
		return elements[ReikaDyeHelper.getRandomColor().ordinal()];
	}

	public static CrystalElement randomElement(int level) {
		ArrayList<CrystalElement> li = levelMap.get(level);
		return li != null ? li.get(rand.nextInt(li.size())) : null;
	}

	public static CrystalElement randomPrimaryElement() {
		return randomElement(0);
	}

	static {
		for (int i = 0; i < elements.length; i++) {
			CrystalElement e = elements[i];
			int lvl = e.getLevel();
			ArrayList<CrystalElement> li = levelMap.get(lvl);
			if (li == null) {
				li = new ArrayList();
				levelMap.put(lvl, li);
			}
			li.add(e);
		}
	}

}
