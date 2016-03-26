/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

@Deprecated
public class ItemFadingTorch extends ItemChromaTool {

	public static final int STATES = 500;

	private final PlayerMap<BlockArray> lightArea = new PlayerMap();

	public ItemFadingTorch(int index) {
		super(index);
		this.setMaxDamage(STATES);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity e, int slot, boolean held) {
		if (e instanceof EntityPlayer && !world.isRemote && is.getItemDamage() <= 500 && e.ticksExisted%1 == 0) {
			BlockArray b = lightArea.remove((EntityPlayer)e);
			if (b != null)
				b.clearArea();
			is.setItemDamage(is.getItemDamage()+1);
			if (is.getItemDamage() >= 500)
				return;
			b = this.calcLightArea(is, world, e);
			for (Coordinate c : b.keySet()) {
				c.setBlock(world, ChromaBlocks.LIGHT.getBlockInstance());
			}
			lightArea.put((EntityPlayer)e, b);
		}
	}

	private BlockArray calcLightArea(ItemStack is, World world, Entity e) {
		BlockArray a = new BlockArray();
		a.setWorld(world);
		int x = MathHelper.floor_double(e.posX);
		int y = MathHelper.floor_double(e.posY);
		int z = MathHelper.floor_double(e.posZ);
		int r = (int)(1.5*(1-(float)is.getItemDamage()/is.getMaxDamage()));
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (ReikaMathLibrary.py3d(i, j, k) <= r) {
						Block b = world.getBlock(dx, dy, dz);
						if (b == Blocks.air || b == ChromaBlocks.LIGHT.getBlockInstance()) {
							a.addBlockCoordinate(dx, dy, dz);
						}
					}
				}
			}
		}
		return a;
	}

}
