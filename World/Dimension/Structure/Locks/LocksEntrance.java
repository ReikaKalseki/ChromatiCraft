/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.DynamicStructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class LocksEntrance extends DynamicStructurePiece<LocksGenerator> {

	public final int radius;
	public final ForgeDirection facing;
	private final ForgeDirection left;
	public final int length;

	public LocksEntrance(LocksGenerator s, ForgeDirection dir, int r, int len) {
		super(s);
		radius = r;
		facing = dir;
		left = ReikaDirectionHelper.getLeftBy90(facing);
		length = len;
	}

	@Override
	public void generate(World world, int x, int z) {
		int y = world.getTopSolidOrLiquidBlock(x, z)-1;
		Block bk = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		for (int i = -radius; i <= radius; i++) {
			int dx = x+i;
			for (int k = -radius; k <= radius; k++) {
				int dz = z+k;

				int sign = (int)Math.signum(facing.offsetX+facing.offsetZ);
				int step = facing.offsetX != 0 ? i : k;
				int d = Math.abs(facing.offsetX == 0 ? i : k);
				int d2 = Math.abs(step);
				int h = this.getHeight(d);
				int hm = d > 0 ? this.getHeight(d-1) : -1;
				int hp = this.getHeight(d+1);
				for (int j = 0; j <= h; j++) {
					int dy = y+j;

					boolean enter = step*sign == -radius && j > 0 && j < h-1 && d < radius-1;
					boolean wall = !enter && (Math.abs(i) == radius || Math.abs(k) == radius || j == 0 || j == h);
					boolean window = j == h && (radius == 6 ? d == 1 : h == this.getHeight(0)-1 && h == hm && h == hp) && d2 > 0 && d2 < radius-1;
					world.setBlock(dx, dy, dz, wall ? bk : Blocks.air, wall ? window ? BlockType.GLASS.metadata : BlockType.STONE.metadata : 0, 3);
				}
			}
		}

		int dx = x+facing.offsetX*radius;
		int dz = z+facing.offsetZ*radius;
		int w = 2;
		for (int a = -w; a <= w; a++) {
			int dx2 = dx+left.offsetX*a;
			int dz2 = dz+left.offsetZ*a;
			int h = Math.abs(a) == w ? 3 : 4;
			for (int b = 1; b <= h; b++) {
				int dy = y+b;
				world.setBlock(dx2, dy, dz2, Blocks.air);
			}
		}

		this.generateTunnel(world, x, y, z);
	}

	private int getHeight(int d) {
		int in = radius-d;
		return (int)(2+Math.sqrt(2.5*in));
	}

	private void generateTunnel(World world, int x, int y, int z) {
		int r = length+radius+3;
		int dx = x+facing.offsetX*r;
		int dz = z+facing.offsetZ*r;
		int posY = parent.getPosY();
		int d = y-posY;
		LockRoomConnector tunnel = new LockRoomConnector(parent, 0, 0, 0, 0).setWindowed().setLength(facing.getOpposite(), length).setOpenFloor(d-5);
		tunnel.generate(new ChunkSplicedGenerationCache.RelayCache(world), dx, posY+d, dz);
	}

}
