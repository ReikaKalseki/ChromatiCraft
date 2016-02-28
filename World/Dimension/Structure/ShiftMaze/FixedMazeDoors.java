/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class FixedMazeDoors extends StructurePiece {

	private final UUID[] ids;
	private final Random rand;

	public FixedMazeDoors(DimensionStructureGenerator s, int len, Random r) {
		super(s);
		ids = new UUID[len];
		for (int i = 0; i < len; i++) {
			ids[i] = UUID.randomUUID();
		}
		rand = r;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		this.generateGate(world, x+1, y+1, z+22, 1);
		this.generateGate(world, x+3, y+1, z+4, 0, 1, 3);
		this.generateGate(world, x+3, y+1, z+6, 2);
		this.generateGate(world, x+6, y+1, z+11, 1, 2, 3, 4);
		this.generateGate(world, x+7, y+1, z+16, 4);
		this.generateGate(world, x+8, y+1, z+3, 0, 1, 4, 5);
		this.generateGate(world, x+10, y+1, z+7, 3);
		this.generateGate(world, x+10, y+1, z+15, 0, 1, 2, 3, 4);
		this.generateGate(world, x+11, y+1, z+20, 4, 5);
		this.generateGate(world, x+15, y+1, z+6, 0);
		this.generateGate(world, x+16, y+1, z+1, 0, 1, 2, 3, 4, 5);
		this.generateGate(world, x+16, y+1, z+17, 2);
		this.generateGate(world, x+17, y+1, z+14, 0, 5);
		this.generateGate(world, x+17, y+1, z+20, 0, 2, 3);
		this.generateGate(world, x+18, y+1, z+17, 5);

		this.generateKey(world, x+19, y+1, z+13, 0, ForgeDirection.SOUTH);
		this.generateKey(world, x+15, y+1, z+19, 1, ForgeDirection.WEST);
		this.generateKey(world, x+13, y+1, z+9, 2, ForgeDirection.WEST);
		this.generateKey(world, x+13, y+1, z+24, 3, ForgeDirection.NORTH);
		this.generateKey(world, x+11, y+1, z+3, 4, ForgeDirection.EAST);
		this.generateKey(world, x+9, y+1, z+17, 5, ForgeDirection.WEST);

		parent.generateDataTile(x+24, y+5, z+11);

		for (int i = 0; i < ids.length*2-1; i++) {
			for (int d = -1; d <= 1; d++) {
				if (i%2 == 1) {
					this.generateDoor(world, x-1-i, y+1, z+9+d, i/2);
					this.generateDoor(world, x-1-i, y+2, z+9+d, i/2);
				}
				else {
					world.setBlock(x-1-i, y+1, z+9+d, Blocks.air);
					world.setBlock(x-1-i, y+2, z+9+d, Blocks.air);
				}
				world.setBlock(x-1-i, y+0, z+9+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-1-i, y+3, z+9+d, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
			for (int k = 0; k < 4; k++) {
				world.setBlock(x-1-i, y+k, z+7, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(x-1-i, y+k, z+11, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
		}

	}

	private void generateGate(ChunkSplicedGenerationCache world, int x, int y, int z, int... openStates) {
		int m = ReikaJavaLibrary.makeIntListFromArray(openStates).contains(0) ? 1 : 0;
		for (int h = 0; h <= 1; h++) {
			int dy = y+h;
			world.setBlock(x, dy, z, ChromaBlocks.SHIFTLOCK.getBlockInstance(), m);
			((ShiftMazeGenerator)parent).cacheLock(x, dy, z);
		}
		for (int i = 0; i < openStates.length; i++) {
			((ShiftMazeGenerator)parent).addDoorState(x, z, openStates[i]);
		}
	}

	private void generateKey(ChunkSplicedGenerationCache world, int x, int y, int z, int ch, ForgeDirection dir) {
		world.setTileEntity(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), BlockLootChest.getMeta(dir), new LootChestCallback(ids[ch], rand));
	}

	private void generateDoor(ChunkSplicedGenerationCache world, int x, int y, int z, int ch) {
		world.setTileEntity(x, y, z, ChromaBlocks.DOOR.getBlockInstance(), 0, new DoorCallback(ids[ch]));
	}

	private static class LootChestCallback implements TileCallback {

		private final UUID uid;
		private final Random rand;

		private LootChestCallback(UUID id, Random r) {
			uid = id;
			rand = r;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityLootChest) {
				TileEntityLootChest tc = (TileEntityLootChest)te;
				ItemStack key = ChromaItems.KEY.getStackOf();
				((ItemDoorKey)ChromaItems.KEY.getItemInstance()).setID(key, uid);
				tc.setInventorySlotContents(rand.nextInt(tc.getSizeInventory()), key);
			}
		}

	}

	private static class DoorCallback implements TileCallback {

		private final UUID uid;

		private DoorCallback(UUID id) {
			uid = id;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityChromaDoor) {
				((TileEntityChromaDoor)te).bindUUID(uid);
			}
		}

	}

}
