package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.util.IIcon;

public interface LightedTreeBlock {

	public IIcon getOverlay(int meta);

	public boolean renderOverlayOnSide(int s, int meta);

}
