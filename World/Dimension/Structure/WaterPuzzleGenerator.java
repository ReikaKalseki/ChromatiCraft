/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockEverFluid;
import Reika.ChromatiCraft.Block.Dimension.Structure.Water.BlockEverFluid.TileEntityEverFluid;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.Lock;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterFloor;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterLoot;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterPath;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterStructureEntrance;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class WaterPuzzleGenerator extends DimensionStructureGenerator {

	private final ArrayList<WaterFloor> levels = new ArrayList();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {

		posX = chunkX;
		posZ = chunkZ;
		posY = 10+rand.nextInt(10);

		int n = this.getSize();
		int r = this.getRadius(0);
		int startx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		int startz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		while (startx == 0 && startz == 0) {
			startx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			startz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		}
		for (int i = 0; i < n; i++) {
			r = this.getRadius(i);
			int endx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			int endz = ReikaRandomHelper.getRandomPlusMinus(0, r);
			while (endx == startx && endz == startz) {
				endx = ReikaRandomHelper.getRandomPlusMinus(0, r);
				endz = ReikaRandomHelper.getRandomPlusMinus(0, r);
			}
			//ReikaJavaLibrary.pConsole("Pathing "+i+" from "+startx+","+startz+" to "+endx+","+endz+", R="+r);
			WaterPath path = new WaterPath(startx, startz, endx, endz, r);
			path.genPath(rand);
			WaterFloor w = new WaterFloor(this, i, r, path, rand);
			levels.add(w);
			startx = endx;
			startz = endz;
		}

		int ty = WaterFloor.HEIGHT+4;

		int y = posY+levels.size()*ty;
		int topY = y;

		for (WaterFloor l : levels) {
			l.generate(world, posX, y, posZ);
			y -= ty;
		}
		y += ty;

		WaterFloor f = levels.get(levels.size()-1);
		r = f.getWidth()-2;
		boolean flag = true;
		for (int i = 0; i < 4; i++) {
			boolean flag2 = i == 3 || rand.nextInt(4) == 0;
			int dx = posX+(i%2)*r*2-r;
			int dz = posZ+(i/2)*r*2-r;
			new WaterLoot(this, flag && flag2).generate(world, dx, y, dz);
			if (flag2)
				flag = false;
		}

		y = topY;
		posY = y;

		f = levels.get(0);

		r = f.getWidth();
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int h = 1; h <= 5; h++) {
					int dx = posX+i;
					int dz = posZ+k;
					int dy = y+WaterFloor.HEIGHT+h;
					if (Math.abs(i) == r || Math.abs(k) == r || h == 5) {
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), (Math.abs(i)+Math.abs(k))%6 == 0 ? BlockType.LIGHT.metadata : BlockType.STONE.metadata);
					}
					else {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}

		Point p = f.getStartLocation();
		int dx = posX+p.x*(Lock.SIZE*2+1+1);
		int dz = posZ+p.y*(Lock.SIZE*2+1+1);
		for (int i = 0; i <= 4; i++) {
			world.setBlock(dx, y+WaterFloor.HEIGHT+i, dz, Blocks.air);
			if (i > 0) {
				world.setBlock(dx+1, y+WaterFloor.HEIGHT+i, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				world.setBlock(dx-1, y+WaterFloor.HEIGHT+i, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				world.setBlock(dx, y+WaterFloor.HEIGHT+i, dz+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				world.setBlock(dx, y+WaterFloor.HEIGHT+i, dz-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
			}
		}
		world.setTileEntity(dx, y+WaterFloor.HEIGHT+4, dz, ChromaBlocks.EVERFLUID.getBlockInstance(), 0, new EverFluidCallback(id, 0));

		y = posY+WaterFloor.HEIGHT;
		this.generatePasswordTile(posX, y, posZ);

		this.addDynamicStructure(new WaterStructureEntrance(this), posX, posZ);
	}

	private int getRadius(int i) {
		return 2+i;
	}

	private static int getSize() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 3;
			case 2:
				return 4;
			case 3:
			default:
				return 6; //12 was too big, overran space -> 6,8,12 to 5,6,8, then 3, 4, 6
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	public boolean hasBeenSolved(World world) {
		for (WaterFloor f : levels) {
			if (!f.hasBeenSolved())
				return false;
		}
		return true;
	}

	@Override
	public void openStructure(World world) {
		for (WaterFloor f : levels) {
			f.setDoorStates(world, true);
		}
	}

	@Override
	protected void clearCaches() {
		levels.clear();
	}

	public WaterFloor getLevel(int i) {
		return levels.isEmpty() ? null : levels.get(i);
	}

	public int levelCount() {
		return levels.size();
	}

	private static class EverFluidCallback extends DimensionStructureTileCallback {

		private final UUID uid;
		private final int level;

		private EverFluidCallback(UUID id, int lvl) {
			uid = id;
			level = lvl;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityEverFluid) {
				BlockEverFluid.placeSource(world, x, y, z);
				((TileEntityEverFluid)te).setData(uid, level);
			}
		}

	}

}
