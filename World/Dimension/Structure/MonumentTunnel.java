/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class MonumentTunnel {

	public final ForgeDirection direction;
	public final int length;
	private final ForgeDirection left;

	public MonumentTunnel(ForgeDirection dir, int len) {
		direction = dir;
		length = len;
		left = ReikaDirectionHelper.getLeftBy90(direction);
	}

	public void generate(World world, Random rand, int x, int y, int z) {
		int m = BlockType.STONE.metadata;
		for (int i = 0; i < length; i++) {
			int dx = x+direction.offsetX*i;
			int dz = z+direction.offsetZ*i;

			int h = y+5;
			for (int dy = y; dy < h; dy++) {
				int r = dy == y+4 ? 2 : 3;
				for (int w = -r; w <= r; w++) {
					world.setBlock(dx+left.offsetX*w, dy-1, dz+left.offsetZ*w, Blocks.air);
				}
			}

			for (int dy = y; dy < h; dy++) {
				int w = dy-y <= 2 ? 3 : 3-(dy-y-2);
				world.setBlock(dx+left.offsetX*w, dy, dz+left.offsetZ*w, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
				world.setBlock(dx-left.offsetX*w, dy, dz-left.offsetZ*w, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			}
			world.setBlock(dx, y+4, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), i%4 < 2 ? BlockType.GLASS.metadata : m, 3);

			for (int w = -3; w <= 3; w++) {
				world.setBlock(dx+left.offsetX*w, y-1, dz+left.offsetZ*w, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m, 3);
			}
		}
	}

}
