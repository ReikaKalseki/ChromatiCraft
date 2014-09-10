package Reika.ChromatiCraft.Items;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Registry.ChromaBlocks;

public class ItemChromaBerry extends ItemCrystalBasic {

	public ItemChromaBerry(int tex) {
		super(tex);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		Block b = ei.worldObj.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			ei.setDead();

			//Not going to work
			ei.worldObj.setBlock(x, y, z, ChromaBlocks.ACTIVECHROMA.getBlockInstance(), ei.getEntityItem().getItemDamage(), 3);
		}
		return false;
	}
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
