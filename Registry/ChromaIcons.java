package Reika.ChromatiCraft.Registry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public enum ChromaIcons {

	TRANSPARENT("transparent"),
	GUARDIANOUTER("guardian_outer"),
	GUARDIANMIDDLE("guardian_middle"),
	GUARDIANINNER("guardian_inner"),
	SPARKLEPARTICLE("sparkle-particle"),
	SPARKLE("sparkle2"),
	GLOWSECTION("glowsections"),
	RADIATE("plant_glow");

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
		icon = ico.registerIcon("chromaticraft:icons/"+iconName);
	}

	public static void registerAll(IIconRegister ico) {
		for (int i = 0; i < iconList.length; i++) {
			ChromaIcons c = iconList[i];
			c.register(ico);
		}
	}

}
