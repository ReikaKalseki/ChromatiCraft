/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.TDMaze;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class TunnelPiece extends StructurePiece {

	private boolean[] connections = new boolean[6];
	private boolean lights = false;
	private boolean[] windows = new boolean[6];

	public final int size;

	public TunnelPiece(DimensionStructureGenerator g, int size) {
		super(g);
		this.size = size;
	}

	public TunnelPiece connect(ForgeDirection dir) {
		connections[dir.ordinal()] = true;
		return this;
	}

	public TunnelPiece disconnect(ForgeDirection dir) {
		connections[dir.ordinal()] = false;
		return this;
	}

	public TunnelPiece setLighted() {
		lights = true;
		return this;
	}

	public TunnelPiece addWindow(ForgeDirection dir) {
		windows[dir.ordinal()] = true;
		return this;
	}

	public static TunnelPiece omni(DimensionStructureGenerator g, int size) {
		TunnelPiece tp = new TunnelPiece(g, size);
		for (int i = 0; i < 6; i++)
			tp.connect(ForgeDirection.VALID_DIRECTIONS[i]);
		return tp;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				for (int k = 0; k <= size; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					boolean c0 = j == 0 && i != 0 && i != size && k != 0 && k != size;
					boolean c1 = j == size && i != 0 && i != size && k != 0 && k != size;
					boolean c2 = k == 0 && j != 0 && j != size && i != 0 && i != size;
					boolean c3 = k == size && j != 0 && j != size && i != 0 && i != size;
					boolean c4 = i == 0 && j != 0 && j != size && k != 0 && k != size;
					boolean c5 = i == size && j != 0 && j != size && k != 0 && k != size;
					boolean tunnel0 = connections[0] && c0;
					boolean tunnel1 = connections[1] && c1;
					boolean tunnel2 = connections[2] && c2;
					boolean tunnel3 = connections[3] && c3;
					boolean tunnel4 = connections[4] && c4;
					boolean tunnel5 = connections[5] && c5;
					boolean window0 = windows[0] && c0;
					boolean window1 = windows[1] && c1;
					boolean window2 = windows[2] && c2;
					boolean window3 = windows[3] && c3;
					boolean window4 = windows[4] && c4;
					boolean window5 = windows[5] && c5;
					boolean tunnel = tunnel0 || tunnel1 || tunnel2 || tunnel3 || tunnel4 || tunnel5;
					boolean window = window0 || window1 || window2 || window3 || window4 || window5;
					boolean fill = !tunnel && (i == 0 || i == size || j == 0 || j == size || k == 0 || k == size);
					boolean light0 = lights && i == 0 && j == size/2 && k == size/2;
					boolean light1 = lights && i == size && j == size/2 && k == size/2;
					boolean light2 = lights && j == 0 && i == size/2 && k == size/2;
					boolean light3 = lights && j == size && i == size/2 && k == size/2;
					boolean light4 = lights && k == 0 && j == size/2 && i == size/2;
					boolean light5 = lights && k == size && j == size/2 && i == size/2;
					boolean light = light0 || light1 || light2 || light3 || light4 || light5;
					Block b = fill ? ChromaBlocks.STRUCTSHIELD.getBlockInstance() : Blocks.air;
					int meta = fill ? (light ? BlockType.LIGHT.metadata : BlockType.STONE.metadata) : 0;
					if (window)
						meta = BlockType.GLASS.metadata;
					world.setBlock(dx, dy, dz, b, meta);
				}
			}
		}
	}

}
