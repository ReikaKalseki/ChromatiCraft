/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.Repairable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class TileEntityHealingCore extends TileEntityAdjacencyUpgrade {

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int tier = this.getTier();
		Block b = world.getBlock(dx, dy, dz);
		if (b instanceof Repairable) {
			((Repairable)b).repair(world, dx, dy, dz, tier);
		}
		else if (b instanceof BlockAnvil) {
			int meta = world.getBlockMetadata(dx, dy, dz);
			if (meta > 0 && rand.nextInt(40) < 1+tier*2) {
				world.setBlockMetadataWithNotify(dx, dy, dz, meta-1, 3);
			}
		}

		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te instanceof Repairable) {
			((Repairable)te).repair(world, dx, dy, dz, tier);
		}
		return true;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.MAGENTA;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
