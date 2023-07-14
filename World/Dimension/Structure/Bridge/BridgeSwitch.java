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

import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureTileCallback;
import Reika.ChromatiCraft.Block.Dimension.Structure.Bridge.BlockBridgeControl.TileBridgeControl;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.BridgeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class BridgeSwitch extends BridgeNode {

	public BridgeSwitch(BridgeGenerator s) {
		super(s, 3);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		super.generate(world, x, y, z);

		for (int i = 0; i < 4; i++) {
			if (!connections[i]) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
				int dx = x+dir.offsetX*radius+(radius-1)*dir.offsetZ;
				int dz = z+dir.offsetZ*radius+(radius-1)*dir.offsetX;
				world.setBlock(dx, y+2, dz, Blocks.torch, 5);

				dx = x+dir.offsetX*radius+(-radius+1)*dir.offsetZ;
				dz = z+dir.offsetZ*radius+(-radius+1)*dir.offsetX;
				world.setBlock(dx, y+2, dz, Blocks.torch, 5);
			}
		}

		for (int i = -2; i <= 2; i++) {
			world.setBlock(x+i, y, z-2, Blocks.planks, ReikaItemHelper.spruceWood.metadata);
			world.setBlock(x+i, y, z+2, Blocks.planks, ReikaItemHelper.spruceWood.metadata);
			world.setBlock(x-2, y, z+i, Blocks.planks, ReikaItemHelper.spruceWood.metadata);
			world.setBlock(x+2, y, z+i, Blocks.planks, ReikaItemHelper.spruceWood.metadata);
		}

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				world.setBlock(x+i, y+5, z+k, Blocks.planks);
			}
		}

		this.generateStairs(world, x-3, y+1, z-3);

		this.addSwitch(world, x, y + 1, z);
	}

	private void generateStairs(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setBlock(x + 0, y + 2, z + 0, Blocks.planks);
		world.setBlock(x + 0, y + 2, z + 6, Blocks.planks);
		world.setBlock(x + 0, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 0, y + 3, z + 1, Blocks.oak_stairs);
		world.setBlock(x + 0, y + 3, z + 2, Blocks.oak_stairs);
		world.setBlock(x + 0, y + 3, z + 3, Blocks.oak_stairs);
		world.setBlock(x + 0, y + 3, z + 4, Blocks.oak_stairs);
		world.setBlock(x + 0, y + 3, z + 5, Blocks.oak_stairs);
		world.setBlock(x + 0, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 1, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 1, y + 3, z + 1, Blocks.oak_stairs, 5);
		world.setBlock(x + 1, y + 3, z + 2, Blocks.oak_stairs, 5);
		world.setBlock(x + 1, y + 3, z + 3, Blocks.oak_stairs, 5);
		world.setBlock(x + 1, y + 3, z + 4, Blocks.oak_stairs, 5);
		world.setBlock(x + 1, y + 3, z + 5, Blocks.oak_stairs, 6);
		world.setBlock(x + 1, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 1, y + 4, z + 1, Blocks.oak_stairs);
		world.setBlock(x + 1, y + 4, z + 2, Blocks.oak_stairs);
		world.setBlock(x + 1, y + 4, z + 3, Blocks.oak_stairs);
		world.setBlock(x + 1, y + 4, z + 4, Blocks.oak_stairs);
		world.setBlock(x + 1, y + 4, z + 5, Blocks.oak_stairs, 3);
		world.setBlock(x + 2, y + 0, z + 2, Blocks.oak_stairs, 2);
		world.setBlock(x + 2, y + 0, z + 3, Blocks.oak_stairs);
		world.setBlock(x + 2, y + 0, z + 4, Blocks.oak_stairs);
		world.setBlock(x + 2, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 2, y + 3, z + 1, Blocks.oak_stairs, 7);
		world.setBlock(x + 2, y + 3, z + 5, Blocks.oak_stairs, 6);
		world.setBlock(x + 2, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 2, y + 4, z + 1, Blocks.oak_stairs, 2);
		world.setBlock(x + 2, y + 4, z + 5, Blocks.oak_stairs, 3);
		world.setBlock(x + 3, y + 0, z + 2, Blocks.oak_stairs, 2);
		world.setBlock(x + 3, y + 0, z + 4, Blocks.oak_stairs, 3);
		world.setBlock(x + 3, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 3, y + 3, z + 1, Blocks.oak_stairs, 7);
		world.setBlock(x + 3, y + 3, z + 5, Blocks.oak_stairs, 6);
		world.setBlock(x + 3, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 3, y + 4, z + 1, Blocks.oak_stairs, 2);
		world.setBlock(x + 3, y + 4, z + 5, Blocks.oak_stairs, 3);
		world.setBlock(x + 4, y + 0, z + 2, Blocks.oak_stairs, 2);
		world.setBlock(x + 4, y + 0, z + 3, Blocks.oak_stairs, 1);
		world.setBlock(x + 4, y + 0, z + 4, Blocks.oak_stairs, 3);
		world.setBlock(x + 4, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 4, y + 3, z + 1, Blocks.oak_stairs, 7);
		world.setBlock(x + 4, y + 3, z + 5, Blocks.oak_stairs, 6);
		world.setBlock(x + 4, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 4, y + 4, z + 1, Blocks.oak_stairs, 2);
		world.setBlock(x + 4, y + 4, z + 5, Blocks.oak_stairs, 3);
		world.setBlock(x + 5, y + 3, z + 0, Blocks.oak_stairs, 2);
		world.setBlock(x + 5, y + 3, z + 1, Blocks.oak_stairs, 7);
		world.setBlock(x + 5, y + 3, z + 2, Blocks.oak_stairs, 4);
		world.setBlock(x + 5, y + 3, z + 3, Blocks.oak_stairs, 4);
		world.setBlock(x + 5, y + 3, z + 4, Blocks.oak_stairs, 4);
		world.setBlock(x + 5, y + 3, z + 5, Blocks.oak_stairs, 6);
		world.setBlock(x + 5, y + 3, z + 6, Blocks.oak_stairs, 3);
		world.setBlock(x + 5, y + 4, z + 1, Blocks.oak_stairs, 1);
		world.setBlock(x + 5, y + 4, z + 2, Blocks.oak_stairs, 1);
		world.setBlock(x + 5, y + 4, z + 3, Blocks.oak_stairs, 1);
		world.setBlock(x + 5, y + 4, z + 4, Blocks.oak_stairs, 1);
		world.setBlock(x + 5, y + 4, z + 5, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 2, z + 0, Blocks.planks);
		world.setBlock(x + 6, y + 2, z + 6, Blocks.planks);
		world.setBlock(x + 6, y + 3, z + 0, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 1, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 2, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 3, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 4, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 5, Blocks.oak_stairs, 1);
		world.setBlock(x + 6, y + 3, z + 6, Blocks.oak_stairs, 3);
	}

	private void addSwitch(ChunkSplicedGenerationCache world, int x, int y, int z) {
		UUID uid = UUID.randomUUID();
		world.setTileEntity(x, y, z, ChromaBlocks.BRIDGECONTROL.getBlockInstance(), 0, new KeyCallback(parent.id, uid));
		parent.addKey(uid);
	}

	private static class KeyCallback extends DimensionStructureTileCallback {

		private final UUID structure;
		private final UUID key;

		public KeyCallback(UUID s, UUID k) {
			structure = s;
			key = k;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileBridgeControl) {
				((TileBridgeControl)te).setData(structure, key);
			}
		}

	}

}
