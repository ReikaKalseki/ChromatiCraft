/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Altar;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class AltarTunnel extends StructurePiece {

	public final int length;
	public final ForgeDirection direction;
	private final ForgeDirection left;

	public AltarTunnel(DimensionStructureGenerator g, ForgeDirection dir, int len) {
		super(g);
		direction = dir;
		length = len;
		left = ReikaDirectionHelper.getLeftBy90(dir);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (int i = 0; i < length; i++) {
			this.generateSlice(world, x+direction.offsetX*i, y+direction.offsetY*i, z+direction.offsetZ*i);
		}
	}

	private void generateSlice(ChunkSplicedGenerationCache world, int dx, int dy, int dz) {
		int w = 4;
		int h = 6;
		for (int i = -w; i <= w; i++) {
			for (int k = 0; k < h; k++) {
				int dx2 = dx+left.offsetX*i;
				int dz2 = dz+left.offsetZ*i;
				int dy2 = dy+k;
				boolean edge = k == 0 || k == h-1 || i == w || i == -w;
				Block b = edge ? ChromaBlocks.STRUCTSHIELD.getBlockInstance() : Blocks.air;
				int mt = edge ? BlockType.STONE.metadata : 0;
				world.setBlock(dx2, dy2, dz2, b, mt);
			}
		}
	}

}
