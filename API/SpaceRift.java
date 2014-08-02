/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.WorldLocation;

public interface SpaceRift {

	public int getBlockIDFrom(ForgeDirection dir);
	public int getBlockMetadataFrom(ForgeDirection dir);
	public TileEntity getTileEntityFrom(ForgeDirection dir);
	public WorldLocation getLinkTarget();

}
