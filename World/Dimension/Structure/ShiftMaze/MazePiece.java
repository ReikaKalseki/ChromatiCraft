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

import java.awt.Point;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class MazePiece extends StructurePiece {

	private int[] connections = new int[6];

	public final int size;

	public final boolean node;

	private final Point position;

	public MazePiece(DimensionStructureGenerator g, int size, Point pos, boolean node) {
		super(g);
		this.size = size;
		this.node = node;
		position = pos;
	}

	public MazePiece connect(ForgeDirection dir, boolean open) {
		connections[dir.ordinal()] = open ? 2 : 1;
		return this;
	}

	public MazePiece disconnect(ForgeDirection dir) {
		connections[dir.ordinal()] = 0;
		return this;
	}

	public static MazePiece omni(DimensionStructureGenerator g, int size, Point pos, boolean node) {
		MazePiece tp = new MazePiece(g, size, pos, node);
		for (int i = 0; i < 6; i++)
			tp.connect(ForgeDirection.VALID_DIRECTIONS[i], true);
		return tp;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= 3; j++) {
				for (int k = 0; k <= size; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					boolean c2 = k == 0 && j != 0 && i != 0 && i != size;
					boolean c3 = k == size && j != 0 && i != 0 && i != size;
					boolean c4 = i == 0 && j != 0 && k != 0 && k != size;
					boolean c5 = i == size && j != 0 && k != 0 && k != size;
					int tunnel2 = c2 ? connections[2] : 0;
					int tunnel3 = c3 ? connections[3] : 0;
					int tunnel4 = c4 ? connections[4] : 0;
					int tunnel5 = c5 ? connections[5] : 0;
					int tunnel = ReikaMathLibrary.multiMax(tunnel2, tunnel3, tunnel4, tunnel5);
					boolean edge = i == 0 || i == size || j == 0 || k == 0 || k == size;
					BlockKey bk = this.getTunnelType(tunnel);
					Block b = edge ? bk.blockID : Blocks.air;
					int meta = edge ? bk.metadata : 0;
					if (bk.blockID == ChromaBlocks.SHIFTLOCK.getBlockInstance()) {
						ForgeDirection dir = ReikaDirectionHelper.getSideOfBox(i, j, k, false, size);
						((ShiftMazeGenerator)parent).cacheLock(position, dir, dx, dy, dz);
					}
					world.setBlock(dx, dy, dz, b, meta);
				}
			}
		}

		if (node) {
			world.setBlock(x+size/2, y+size/2, z+size/2, Blocks.obsidian);
		}
	}

	private BlockKey getTunnelType(int idx) {
		switch(idx) {
			case 0:
			default:
				return new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			case 1:
				return new BlockKey(ChromaBlocks.SHIFTLOCK.getBlockInstance(), 0);
			case 2:
				return new BlockKey(Blocks.air, 0);
		}
	}

}
