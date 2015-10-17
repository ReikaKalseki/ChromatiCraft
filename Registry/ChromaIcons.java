/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public enum ChromaIcons {

	TRANSPARENT("transparent"),
	GUARDIANOUTER("guardian_outer"),
	GUARDIANMIDDLE("guardian_middle"),
	GUARDIANINNER("guardian_inner"),
	SPARKLEPARTICLE("sparkle-particle"),
	SPARKLE("sparkle2"),
	GLOWSECTION("glowsections"),
	RADIATE("plant_glow"),
	FLARE("flare"),
	REPEATER("repeater"),
	MULTIREPEATER("multirepeater"),
	BROADCAST("broadcaster"),
	SPINFLARE("rotating flare_pulse"),
	LASER("laser_2"),
	LASEREND("laser_end_2"),
	BIGFLARE("bigflare"),
	ROUNDFLARE("roundflare"),
	RINGS("ringrow_fade"),
	RIFT("rift"),
	RIFTHALO("rift_halo"),
	FADE("fade"),
	CENTER("centerblur3"),
	CHROMA("chroma_particle"),
	NOENTER("noentry"),
	CHECK("check"),
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
	REGIONS("regions"),
	CAUSTICS("caustics"),
	CAUSTICS_GENTLE("caustics-g");

	private IIcon icon;
	private final String iconName;

	public static final ChromaIcons[] iconList = values();

	private ChromaIcons(String icon) {
		iconName = icon;
	}

	public IIcon getIcon() {
		return icon;
	}

	private void register(IIconRegister ico) {
		String s = this.getIconName();
		icon = ico.registerIcon(s);
	}

	private String getIconName() {
		return "chromaticraft:icons/"+iconName;
	}

	public static void registerAll(TextureMap ico) {
		for (int i = 0; i < iconList.length; i++) {
			ChromaIcons c = iconList[i];
			c.register(ico);
		}
	}

}
