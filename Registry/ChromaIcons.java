/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Auxiliary.Trackers.SpecialDayTracker;
import Reika.DragonAPI.Interfaces.IconEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum ChromaIcons implements IconEnum {

	TRANSPARENT("transparent"),
	GUARDIANOUTER("guardian_outer"),
	GUARDIANMIDDLE("guardian_middle"),
	GUARDIANINNER("guardian_inner"),
	SPARKLEPARTICLE("sparkle-particle"),
	SPARKLE("sparkle2"),
	SPARKLE_ROUNDED("roundsparkle"),
	GLOWSECTION("glowsections"),
	RADIATE("plant_glow"),
	FLARE("flare"),
	RINGFLARE("flare4"),
	REPEATER("repeater"),
	MULTIREPEATER("multirepeater"),
	BROADCAST("broadcaster"),
	SPINFLARE("rotating flare_pulse"),
	LASER("laser_2"),
	LASEREND("laser_end_2"),
	BIGFLARE("bigflare"),
	ROUNDFLARE(SpecialDayTracker.instance.loadAprilTextures() ? "smileflare" : SpecialDayTracker.instance.loadXmasTextures() ? "xmasflare" : "roundflare"),
	RINGS("ringrow_fade"),
	RIFT("rift"),
	RIFTHALO("rift_halo"),
	FADE(SpecialDayTracker.instance.loadXmasTextures() ? "fade_snowflake" : "fade"),
	FADE_SNOW("fade_snowflake"),
	FADE_BASICBLEND("fade_basic"),
	FADE_GENTLE("fade_gentle"),
	FADE_STAR("fade_star"),
	FADE_RAY("fade_ray"),
	FADE_CLOUD("fade_cloud"),
	FADE_LIQUID("fade_liquid"),
	CLOUDGROUP("cloudgroup"),
	CLOUDGROUP_TRANS("cloudgroup_trans"),
	CLOUDGROUP_TRANS_BLUR("cloudgroup_trans_blur"),
	TRANSFADE("transfade"),
	CENTER("centerblur3"),
	CHROMA("chroma_particle"),
	NOENTER("noentry"),
	CHECK("check"),
	MARKER("marker"),
	X("x"),
	DIAMOND("diamond"),
	QUESTION("question"),
	BLUEFIRE("bluefire"),
	BATTERY("battery_outer"),
	BLANK("blank"),
	GLOWFRAME("glowframe"),
	GLOWFRAMEDOT("glowframe_dot"),
	GLOWFRAME_TRANS("glowframe2"),
	GLOWFRAMEDOT_TRANS("glowframe_dot2"),
	ALLCOLORS("allcolors"),
	NODE("node"),
	NODE2("node2"),
	TURBO("turbo"),
	TRIDOT("tridot-strip"),
	PURPLESPIN("purplespin"),
	ROSES("roses"),
	ROSES_WHITE("roses_w"),
	BASICFADE("basicfade"),
	BASICFADE_FAST("basicfade_f"),
	FRAME("frame"),
	//PINWHEEL("pinwheel"),
	LATTICE("lattice"),
	LATTICEITEM("lattice"),
	REGIONS("regions"),
	CAUSTICS("caustics"),
	CAUSTICS_GENTLE("caustics-g"),
	CAUSTICS_GENTLE_ALPHA("caustics-g-a"),
	CAUSTICS_TINY("caustics-tiny"),
	CAUSTICS_TINY_ALPHA("caustics-tiny-a"),
	CAUSTICS_CRYSTAL("caustics-crystal3"),
	OVALS("oval-rings"),
	WEAKREPEATER("weakrepeater"),
	SMOKE("smoke"),
	BLACKHOLE("blackhole"),
	WHITEHOLE("blackhole_white"),
	ALPHAHOLE("blackhole_alpha"),
	BLURFLARE("blurflare2"),
	HOLE("hole"),
	PINWHEEL("pinwheel"),
	HEXFLARE("hexflare"),
	HEXFLARE2("hexflare2"),
	CONCENTRIC("concentric"),
	CONCENTRIC2("concentric2"),
	CONCENTRIC2REV("concentric2rev"),
	FAN("fan"),
	SIDEDFLOW("sidedflow"),
	SIDEDFLOWBI("sidedflow-bi"),
	RING0("ring0"),
	RING1("ring1"),
	RING2("ring2"),
	RING3("ring3"),
	WIDEBAR("widebar"),
	PORTALRING("portalring"),
	CONDENSEFLARE("condenseflare4"),
	STARFLARE("starflare"),
	AVOLASER("avolaser"),
	AVOLASER_CORE("avolaser_2"),
	HIVE("hive"),
	HIVESPARKS("hivesparks"),
	FLARE7("flare7"),
	CELLFLARE("cellflare"),
	ECLIPSEFLARE("eclipseflare"),
	SUNFLARE("sunflare"),
	COLORWHIRL("colorwhirl3"),
	COLORWHIRLFLARE("colorwhirlflare"),
	RAINFLARE("rainflare"),
	STATIC("static"),
	BLASTFLARE("blastflare"),
	ROSEFLARE("roseflare"),
	;

	private IIcon icon;
	private final String iconName;

	public static final ChromaIcons[] iconList = values();

	private ChromaIcons(String icon) {
		iconName = icon;
	}

	public IIcon getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	private void register(IIconRegister ico) {
		String s = this.getIconName();
		icon = ico.registerIcon(s);
	}

	private String getIconName() {
		return "chromaticraft:icons/"+iconName;
	}

	/*
	private void register(IIconRegister ico) {
		String s = this.getIconName();
		//icon = ico.registerIcon(s);
		icon = new HardPathIcon(s).register((TextureMap)ico);
	}

	private String getIconName() {
		return "Reika/ChromatiCraft/Textures/Icons/Animated/"+iconName+".png";
	}
	 */

	@SideOnly(Side.CLIENT)
	public static void registerAll(TextureMap ico) {
		for (int i = 0; i < iconList.length; i++) {
			ChromaIcons c = iconList[i];
			if (c.isOnSheet(ico.getTextureType()))
				c.register(ico);
		}
	}

	private boolean isOnSheet(int sheet) {
		switch(this) {
			case LATTICEITEM:
				return sheet == 1;
			default:
				return sheet == 0;
		}
	}

	public boolean allowTextureModification() {
		switch(this) {
			case QUESTION:
			case FRAME:
			case GLOWFRAME:
			case GLOWFRAMEDOT:
			case GLOWFRAME_TRANS:
			case GLOWFRAMEDOT_TRANS:
			case BASICFADE:
			case BASICFADE_FAST:
			case REPEATER:
			case MULTIREPEATER:
			case BROADCAST:
			case WEAKREPEATER:
			case SPARKLEPARTICLE:
				return true;
			default:
				return false;
		}
	}

	public boolean isTransparent() {
		switch(this) {
			case BASICFADE:
			case FADE_BASICBLEND:
			case BASICFADE_FAST:
			case FRAME:
			case BATTERY:
			case REPEATER:
			case MULTIREPEATER:
			case BROADCAST:
			case WEAKREPEATER:
			case SPARKLEPARTICLE:
			case TRANSPARENT:
			case GUARDIANOUTER:
			case GUARDIANMIDDLE:
			case GLOWFRAME_TRANS:
			case GLOWFRAMEDOT_TRANS:
			case PURPLESPIN:
			case TRANSFADE:
			case CHROMA:
			case NOENTER:
			case CHECK:
			case DIAMOND:
			case QUESTION:
			case CLOUDGROUP_TRANS:
			case CLOUDGROUP_TRANS_BLUR:
			case SMOKE:
				return true;
			default:
				return false;
		}
	}

}
