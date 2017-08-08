/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class TileEntityBlockTicker extends TileEntityAdjacencyUpgrade {

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		double n = this.getTicksPerTick(this.getTier());
		if (n < 1)
			n = ReikaRandomHelper.doWithChance(n) ? 1 : 0;
		for (int i = 0; i < n; i++) {
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (this.canTickBlock(b)) {
				b.updateTick(world, dx, dy, dz, rand);
				MinecraftForge.EVENT_BUS.post(new BlockTickEvent(world, dx, dy, dz, b, UpdateFlags.FORCED.flag));
			}
		}
		return true;
	}

	private boolean canTickBlock(Block b) {
		if (b instanceof BlockRedstoneDiode)
			return false;
		return true;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.GREEN;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getTicksPerTick(int tier) {
		return Math.pow(2, tier-5);
	}

}
