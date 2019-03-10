/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Music;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.BlockHoverBlock.HoverType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;


public class MusicEntrance extends DynamicStructurePiece {

	private final MusicFunnel funnel = new MusicFunnel();

	public MusicEntrance(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public void generate(World world, int x, int z) {
		int y = world.getTopSolidOrLiquidBlock(x, z);
		int y1 = parent.getPosY();

		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int m = BlockType.STONE.metadata;

		funnel.generate(world, x, y, z);

		for (int dy = y1; dy < y-5; dy++) {
			for (int i = -2; i <= 2; i++) {
				int dx = x+i+5; //not sure why +5
				for (int k = -2; k <= 2; k++) {
					int dz = z+k;
					boolean wall = dy == y1 || Math.abs(i) == 2 || (Math.abs(k) == 2 && !(k == 2 && dy <= y1+3));
					if (wall) {
						world.setBlock(dx, dy, dz, b, m, 3);
					}
					else if ((dy-y1)%12 == 6 && dy < y-6) {
						world.setBlock(dx, dy, dz, ChromaBlocks.HOVER.getBlockInstance(), HoverType.DAMPER.getPermanentMeta(), 3);
					}
					else {
						world.setBlock(dx, dy, dz, STRUCTURE_AIR);
					}
				}
			}
		}
	}

}
