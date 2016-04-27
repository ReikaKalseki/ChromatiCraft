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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarm.AntFarmEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarm.AntFarmTunnel;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class AntFarmGenerator extends DimensionStructureGenerator {

	public static final float LIGHT_DURATION = 1024;

	private ArrayList<AntFarmTunnel> tunnels = new ArrayList();
	private HashSet<Coordinate> mainSpaces = new HashSet();
	private HashSet<Coordinate> airSpaces = new HashSet();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int n = this.getTunnelCount()/4;
		int h = 90;
		int mh = 20;

		int r = 6;
		for (int y = mh; y <= mh+h; y++) {
			int dx = (int)(2.5*Math.cos(4*Math.toRadians(y)));
			int dz = (int)(2.5*Math.sin(4*Math.toRadians(y*2)));
			for (int i = -r; i <= r; i++) {
				for (int k = -r; k <= r; k++) {
					double d = ReikaMathLibrary.py3d(i, 0, k);
					if (d <= r+0.5) {
						int x = chunkX+i+dx;
						int z = chunkZ+k+dz;
						BlockKey b = d <= r-0.75 ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						world.setBlock(x, y, z, b.blockID, b.metadata);
						if (b.blockID == Blocks.air)
							mainSpaces.add(new Coordinate(x, y, z));
					}
				}
			}
		}

		for (int i = 0; i < n; i++) {
			AntFarmTunnel a = this.createTunnel(chunkX, chunkZ, h, mh, rand);
			int t = 0;
			while (t < 50 && a.intersectsWith(airSpaces)) {
				a = this.createTunnel(chunkX, chunkZ, h, mh, rand);
				t++;
			}
			airSpaces.addAll(a.getAirSpaces());
			tunnels.add(a);
		}

		for (AntFarmTunnel a : tunnels) {
			a.generate(world, 0, 0, 0);
		}

		this.addDynamicStructure(new AntFarmEntrance(this, mh+h), chunkX, chunkZ);
	}

	private AntFarmTunnel createTunnel(int x, int z, int h, int mh, Random rand) {
		double s = ReikaRandomHelper.getRandomPlusMinus(0D, 45D);
		int in = 15;
		int y = mh+in+rand.nextInt(h-mh-in*2);
		return new AntFarmTunnel(this, rand.nextDouble()*360, 24+rand.nextInt(96), s, 3+rand.nextInt(2), x, y, z, mainSpaces);
	}

	private int getTunnelCount() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 15;
			case 2:
				return 18;
			case 3:
			default:
				return 24;
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
		return false;
	}

	@Override
	protected void clearCaches() {
		tunnels.clear();
		airSpaces.clear();
	}

}
