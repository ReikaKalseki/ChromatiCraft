/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Bridge;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class BridgeEntryPlatform extends BridgeNode {

	public BridgeEntryPlatform(BridgeGenerator s) {
		super(s, 4);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		super.generate(world, x, y, z);

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				if (i != 0 || k != 0) {
					world.setBlock(x+i, y, z+k, Blocks.planks, ReikaItemHelper.spruceWood.getItemDamage());
				}
			}
		}

		for (int i = -3; i <= 3; i++) {
			world.setBlock(x+i, y, z+3, Blocks.planks, ReikaItemHelper.spruceWood.getItemDamage());
			world.setBlock(x+i, y, z-3, Blocks.planks, ReikaItemHelper.spruceWood.getItemDamage());
			world.setBlock(x+3, y, z+i, Blocks.planks, ReikaItemHelper.spruceWood.getItemDamage());
			world.setBlock(x-3, y, z+i, Blocks.planks, ReikaItemHelper.spruceWood.getItemDamage());
		}

		for (int i = 0; i <= 2; i++) {
			for (int h = 1; h <= 2; h++) {
				Block b = h == 1 ? Blocks.planks : Blocks.fence;
				world.setBlock(x-radius+i, y+h, z+radius, b);
				world.setBlock(x+radius-i, y+h, z+radius, b);

				world.setBlock(x-radius+i, y+h, z-radius, b);
				world.setBlock(x+radius-i, y+h, z-radius, b);

				world.setBlock(x+radius, y+h, z-radius+i, b);
				world.setBlock(x+radius, y+h, z+radius-i, b);

				world.setBlock(x-radius, y+h, z-radius+i, b);
				world.setBlock(x-radius, y+h, z+radius-i, b);
			}
		}

		for (int i = 0; i < 4; i++) {
			if (!connections[i]) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				for (int k = -1; k <= 1; k++) {
					int dx = x+dir.offsetX*radius+k*dir.offsetZ;
					int dz = z+dir.offsetZ*radius+k*dir.offsetX;
					world.setBlock(dx, y+2, dz, Blocks.fence);
				}
			}
		}

		world.setBlock(x+2, y+1, z+2, Blocks.torch, 5);
		world.setBlock(x-2, y+1, z+2, Blocks.torch, 5);
		world.setBlock(x+2, y+1, z-2, Blocks.torch, 5);
		world.setBlock(x-2, y+1, z-2, Blocks.torch, 5);

		parent.generateLootChest(x-3, y+1, z-3, ForgeDirection.SOUTH, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 0);
		parent.generateLootChest(x+3, y+1, z-3, ForgeDirection.WEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 0);
		parent.generateLootChest(x-3, y+1, z+3, ForgeDirection.EAST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 0);
		parent.generateLootChest(x+3, y+1, z+3, ForgeDirection.NORTH, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 0);
	}

}
