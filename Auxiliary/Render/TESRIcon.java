package Reika.ChromatiCraft.Auxiliary.Render;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Interfaces.IconEnum;

public class TESRIcon {

	public final IconEnum icon;
	private final double sizeWorld;
	private final double sizeNoWorld;
	private final float brightnessWorld;
	private final float brightnessNoWorld;

	public TESRIcon(IconEnum ico, double szw, double sz, float b) {
		this(ico, szw, sz, b, b);
	}

	public TESRIcon(IconEnum ico, double szw, double sz, float b, float bnw) {
		icon = ico;
		sizeWorld = szw;
		sizeNoWorld = sz;
		brightnessNoWorld = bnw;
		brightnessWorld = b;
	}

	public double getSize(TileEntity te) {
		return te.hasWorldObj() ? sizeWorld : sizeNoWorld;
	}

	public float getBrightness(TileEntity te) {
		return te.hasWorldObj() ? brightnessWorld : brightnessNoWorld;
	}

}
