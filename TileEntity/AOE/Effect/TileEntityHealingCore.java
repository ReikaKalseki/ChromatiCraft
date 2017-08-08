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
import net.minecraft.block.BlockAnvil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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
			if (meta > 0 && rand.nextInt(200) < 1+tier*8) {
				world.setBlockMetadataWithNotify(dx, dy, dz, meta-4, 3);
			}
		}

		TileEntity te = this.getAdjacentTileEntity(dir);
		if (te instanceof Repairable) {
			((Repairable)te).repair(world, dx, dy, dz, tier);
		}
		else if (te instanceof IInventory) {
			IInventory ii = (IInventory)te;
			int slot = rand.nextInt(ii.getSizeInventory());
			ItemStack is = ii.getStackInSlot(slot);
			if (this.canRepair(is)) {
				this.repair(is);
			}
		}
		return true;
	}

	private boolean canRepair(ItemStack is) {
		if (is == null)
			return false;
		if (is.getItem().isRepairable() && is.getItemDamage() > 0)
			return true;
		return false;
	}

	private void repair(ItemStack is) {
		is.setItemDamage(is.getItemDamage()-this.getTier());
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.MAGENTA;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
