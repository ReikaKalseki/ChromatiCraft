/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import net.minecraft.util.IIcon;

public interface LightedTreeBlock {

	public IIcon getOverlay(int meta);

	public boolean renderOverlayOnSide(int s, int meta);

}
