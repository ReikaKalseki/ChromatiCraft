/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public enum CrystalElement {

	BLACK("Kuro", 0x191919, EnumChatFormatting.BLACK), //Magic
	RED("Karmir", 0xFF0000, EnumChatFormatting.DARK_RED), //Endurance
	GREEN("Kijani", 0x007F0E, EnumChatFormatting.DARK_GREEN), //Nature
	BROWN("Ruskea", 0x724528, EnumChatFormatting.GOLD), //Mineral
	BLUE("Nila", 0x0026FF, EnumChatFormatting.BLUE), //Light
	PURPLE("Zambarau", 0x8C00EA, EnumChatFormatting.DARK_PURPLE), //Enhancement
	CYAN("Vadali", 0x009FBF, EnumChatFormatting.DARK_AQUA), //Water
	LIGHTGRAY("Argia", 0x979797, EnumChatFormatting.GRAY), //Deception
	GRAY("Ykri", 0x404040, EnumChatFormatting.DARK_GRAY), //Change
	PINK("Ruzova", 0xFFBAD9, EnumChatFormatting.RED), //Aggression
	LIME("Asveste", 0x00FF00, EnumChatFormatting.GREEN), //Motion
	YELLOW("Kitrino", 0xFFFF00, EnumChatFormatting.YELLOW), //Energy
	LIGHTBLUE("Galazio", 0x7FD4FF, EnumChatFormatting.AQUA), //Time
	MAGENTA("Kurauri", 0xFF00DC, EnumChatFormatting.LIGHT_PURPLE), //Life
	ORANGE("Portokali", 0xFF6A00, EnumChatFormatting.GOLD), //Fire
	WHITE("Tahara", 0xFFFFFF, EnumChatFormatting.WHITE); //Purity/Harmony

	private final ReikaDyeHelper color;
	public final String displayName;
	private IIcon glowIcon;
	private IIcon animatedFace;
	private IIcon engraving;
	private IIcon outline;
	private IIcon overbright;
	private final int rgb;
	private final EnumChatFormatting chat;

	private final float[] hsb;

	private static final Random rand = new Random();

	public static final CrystalElement[] elements = values();
	private static final MultiMap<Integer, CrystalElement> levelMap = new MultiMap(new MultiMap.HashSetFactory());
	private static final HashMap<String, CrystalElement> nameMap = new HashMap();
	private static final HashMap<CrystalElement, Integer> colorMap = new HashMap();

	private CrystalElement(String n, int rgb, EnumChatFormatting c) {
		color = ReikaDyeHelper.getColorFromDamage(this.ordinal());
		displayName = n;
		this.rgb = 0xff000000 | rgb;
		chat = c;

		hsb = Color.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null);
	}

	public String getEnglishName() {
		return color.colorName;
	}

	public int getColor() {
		return rgb;
	}

	public int getHue() {
		return (int)(hsb[0]*360F);
	}

	public int getSaturation() {
		return (int)(hsb[1]*255F);
	}

	public int getValue() {
		return (int)(hsb[2]*255F);
	}

	public int getRed() {
		return ReikaColorAPI.getRed(this.getColor());
	}

	public int getGreen() {
		return ReikaColorAPI.getGreen(this.getColor());
	}

	public int getBlue() {
		return ReikaColorAPI.getBlue(this.getColor());
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

	public boolean isCompatible(CrystalElement e) {
		return ElementMixer.instance.isCompatible(this, e);
	}

	public boolean isPrimary() {
		return this.getLevel() == 0;
	}

	@SideOnly(Side.CLIENT)
	public void setIcons(IIconRegister ico) {
		glowIcon = ico.registerIcon("chromaticraft:runes/glow/tile"+this.ordinal()+"_0");
		animatedFace = ico.registerIcon("chromaticraft:runes/frontpng/tile"+this.ordinal()+"_0");
		engraving = ico.registerIcon("chromaticraft:runes/engraved/tile"+this.ordinal()+"_0");
		outline = ico.registerIcon("chromaticraft:runes/outline/tile"+this.ordinal()+"_0");
		overbright = ico.registerIcon("chromaticraft:crystal/overbright/bloom_"+this.name().toLowerCase(Locale.ENGLISH));
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

	@SideOnly(Side.CLIENT)
	public IIcon getEngravingRune() {
		return engraving;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getOutlineRune() {
		return outline;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getOverbrightIcon() {
		return overbright;
	}

	public static CrystalElement randomElement() {
		return elements[ReikaDyeHelper.getRandomColor().ordinal()];
	}

	public static CrystalElement randomElement(int level) {
		Collection<CrystalElement> li = levelMap.get(level);
		return li != null ? ReikaJavaLibrary.getRandomCollectionEntry(rand, li) : null;
	}

	public static CrystalElement randomPrimaryElement() {
		return randomElement(0);
	}

	public static int getBlendedColor(int tick, int mod) {
		CrystalElement e = CrystalElement.elements[(tick/mod)%16];
		CrystalElement e2 = CrystalElement.elements[(tick/mod+1)%16];
		float mix = tick%mod/(float)mod;
		return ReikaColorAPI.mixColors(e2.getColor(), e.getColor(), mix);
	}

	static {
		for (int i = 0; i < elements.length; i++) {
			CrystalElement e = elements[i];
			int lvl = e.getLevel();
			levelMap.addValue(lvl, e);
			nameMap.put(e.displayName, e);
			colorMap.put(e, e.getColor());
		}
	}

	public String getChatColorString() {
		return chat.toString();
	}

	public CrystalElementProxy getAPIProxy() {
		return CrystalElementProxy.list[this.ordinal()];
	}

	public static CrystalElement getFromAPI(CrystalElementProxy e) {
		return elements[e.ordinal()];
	}

	public static CrystalElement getByName(String s) {
		return nameMap.get(s);
	}

	public static Map<CrystalElement, Integer> getColorMap() {
		return Collections.unmodifiableMap(colorMap);
	}

}
