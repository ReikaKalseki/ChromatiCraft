/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.TDMaze;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.ThreeDMazeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;


public class MazeRoom extends StructurePiece {

	private final HashSet<Coordinate> cells = new HashSet();
	public final int size;
	public final Coordinate center;
	public final int cellRadius;
	private final Random rand;

	public MazeRoom(ThreeDMazeGenerator s, int sz, int w, Coordinate c, Random r) {
		super(s);
		size = sz;
		center = c;
		cellRadius = w;
		rand = r;
	}

	public MazeRoom addCell(Coordinate c) {
		cells.add(c);
		return this;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) { //XYZ is of origin cell

		MultiMap<Coordinate, ForgeDirection> openCells = new MultiMap(new HashSetFactory());

		for (Coordinate c : cells) {
			c = c.offset(center.negate());
			for (int i = 0; i <= size; i++) {
				for (int j = 0; j <= size; j++) {
					for (int k = 0; k <= size; k++) {
						int dx = x+c.xCoord*size+i;
						int dy = y+c.yCoord*size+j;
						int dz = z+c.zCoord*size+k;
						if (j == 0) {
							BlockKey bk = world.getBlock(dx, dy, dz);
							if (bk != null && bk.blockID == Blocks.air) {
								world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CRACKS.metadata);
								openCells.addValue(c, ForgeDirection.DOWN);
							}
						}
						else if (j == size) {
							BlockKey bk = world.getBlock(dx, dy, dz);
							if (bk != null && bk.blockID == Blocks.air) {
								world.setBlock(dx, dy, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta());
								openCells.addValue(c, ForgeDirection.UP);
							}
						}
						else {
							boolean flag = false;
							if (c.xCoord > -cellRadius || i > 0) {
								if (c.xCoord < cellRadius || i < size) {
									if (c.zCoord > -cellRadius || k > 0) {
										if (c.zCoord < cellRadius || k < size) {
											world.setBlock(dx, dy, dz, Blocks.air);
											flag = true;
										}
									}
								}
							}
							if (!flag) {
								BlockKey bk = world.getBlock(dx, dy, dz);
								if (bk != null && bk.blockID == Blocks.air) {
									world.setBlock(dx, dy, dz, Blocks.iron_bars);
								}
							}
						}
					}
				}
			}
		}

		/*
		for (int i = 1; i <= size-1; i++) {
			for (int j = 1; j <= size-1; j++) {
				for (int k = 1; k <= size-1; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					world.setBlock(dx, dy, dz, Blocks.glass);
				}
			}
		}
		 */

		if (rand.nextInt(Math.max(1, 3-cellRadius)) == 0) {
			String s = rand.nextBoolean() ? ChestGenHooks.DUNGEON_CHEST : ChestGenHooks.PYRAMID_DESERT_CHEST;
			ItemStack is = rand.nextBoolean() ? ChromaStacks.getChargedShard(CrystalElement.randomElement()) : ChromaStacks.rawCrystal.copy();
			int m = 2;
			if (cellRadius > 1 && rand.nextInt(4) == 0) {
				is = ChromaStacks.complexIngot.copy();
				m = 1;
			}
			is.stackSize = 1+m*rand.nextInt(2*cellRadius);
			parent.generateLootChest(x+size/2, y+size/2, z+size/2, ReikaDirectionHelper.getRandomDirection(false, rand), s, rand.nextInt(1+cellRadius), is, 100);
		}

		if (cellRadius == 2) {
			if (rand.nextBoolean() && !openCells.get(new Coordinate(0, 0, 0)).contains(ForgeDirection.DOWN)) { //blocks center down
				for (int i = -2; i <= size+2; i++) {
					for (int k = -2; k <= size+2; k++) {
						int dx = x+i;
						int dy = y+1;
						int dz = z+k;
						if (i == -2 || i == size+2 || k == -2 || k == size+2) {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						}
						else if (i == -1 || i == size+1 || k == -1 || k == size+1) {
							world.setBlock(dx, dy, dz, ChromaBlocks.CHROMA.getBlockInstance());
						}
						else if (i == -0 || i == size+0 || k == -0 || k == size+0) {
							world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						}
					}
				}
			}
			else {
				for (int i = -1; i <= size+1; i++) {
					for (int k = -1; k <= size+1; k++) {
						int dx = x+i;
						int dy = y+1;
						int dz = z+k;
						if (i == -1 || i == size+1 || k == -1 || k == size+1) {
							world.setBlock(dx, dy, dz, Blocks.glowstone);
							world.setBlock(dx, dy+1, dz, Blocks.glowstone);
							world.setBlock(dx+1, dy, dz, Blocks.glowstone);
							world.setBlock(dx-1, dy, dz, Blocks.glowstone);
							world.setBlock(dx, dy, dz+1, Blocks.glowstone);
							world.setBlock(dx, dy, dz-1, Blocks.glowstone);
						}
					}
				}
			}
		}
	}

}
