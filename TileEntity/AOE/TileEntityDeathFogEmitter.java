/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Entity.EntityDeathFog;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class TileEntityDeathFogEmitter extends InventoriedChromaticBase {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DEATHFOG;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			boolean flag = false;
			if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.voidmonsterEssence)) {
				flag = true;
				if (rand.nextInt(600) == 0)
					ReikaInventoryHelper.decrStack(0, inv);
			}
			double ang = rand.nextDouble()*360;
			double ang2 = rand.nextDouble()*360;
			while (ang2 < -10) {
				ang2 = rand.nextDouble()*360;
			}
			while (ang2 > 30 && rand.nextInt(5) > 0) {
				ang2 = rand.nextDouble()*360;
			}
			double v = ReikaRandomHelper.getRandomBetween(0.0625, 0.25);
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, ang2, ang);
			EntityDeathFog e = new EntityDeathFog(world, x+0.5, y+0.5, z+0.5, vel[0], vel[1], vel[2], flag);
			world.spawnEntityInWorld(e);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 32;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReikaItemHelper.matchStacks(is, ChromaStacks.voidmonsterEssence);
	}

}
