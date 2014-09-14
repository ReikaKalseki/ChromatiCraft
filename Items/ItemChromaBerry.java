/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import Reika.ChromatiCraft.Base.ItemCrystalBasic;

public class ItemChromaBerry extends ItemCrystalBasic {

	public ItemChromaBerry(int tex) {
		super(tex);
	}
	/*
	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		Block b = ei.worldObj.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance() || b == ChromaBlocks.ACTIVECHROMA.getBlockInstance()) {
			if (ei.worldObj.getBlockMetadata(x, y, z) == 0) {
				if (b == ChromaBlocks.CHROMA.getBlockInstance())
					ei.worldObj.setBlock(x, y, z, ChromaBlocks.ACTIVECHROMA.getBlockInstance());
				TileEntity te = ei.worldObj.getTileEntity(x, y, z);
				if (te instanceof TileEntityChroma) {
					TileEntityChroma tc = (TileEntityChroma)te;
					if (tc.activate(CrystalElement.elements[ei.getEntityItem().getItemDamage()])) {
						ei.setDead();
					}
				}
			}
		}
		return false;
	}
	 */
	/*
	@Override
	public boolean hasCustomEntity(ItemStack is) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityChromaBerry ei = new EntityChromaBerry(world, location.posX, location.posY, location.posZ, itemstack);
		ei.motionX = location.motionX;
		ei.motionY = location.motionY;
		ei.motionZ = location.motionZ;
		ei.delayBeforeCanPickup = 10;
		return ei;
	}*/

}
