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

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public interface CrystalRenderedBlock {

	public boolean renderBase();

	public Block getBaseBlock(ForgeDirection up);

	public boolean renderAllArms();

	public int getTintColor(int meta);

}
