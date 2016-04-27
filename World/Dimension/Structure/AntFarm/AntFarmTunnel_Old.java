/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.AntFarm;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.init.Blocks;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class AntFarmTunnel_Old extends StructurePiece {

	public final CubeDirections direction;
	public final int slope;

	public final int length1;
	public final int length2;
	public final int length3;

	public final int tunnelRadius;

	private final HashSet<Coordinate> air = new HashSet();
	private final HashMap<Coordinate, BlockKey> blocks = new HashMap();

	public AntFarmTunnel_Old(AntFarmGenerator a, CubeDirections dir, int s, int l1, int l2, int l3, int r, int x, int y, int z, HashSet<Coordinate> airSpaces) {
		super(a);
		direction = dir;
		slope = s;

		length1 = l1;
		length2 = l2;
		length3 = l3;

		tunnelRadius = r;

		this.initialize(x, y, z, airSpaces);
	}

	private void initialize(int x, int y, int z, HashSet<Coordinate> airSpaces) {
		for (int d = 0; d < length1; d++) {
			int dx = x+direction.directionX*d;
			int dz = z+direction.directionZ*d;

			this.generateTunnelSection(dx, y, dz, tunnelRadius, airSpaces);
		}

		x += direction.directionX*length1;
		z += direction.directionZ*length1;

		for (int d = 0; d < length2; d++) {
			int dx = x+direction.directionX*d;
			int dz = z+direction.directionZ*d;
			int dy = y+d*slope;

			this.generateTunnelSection(dx, dy, dz, tunnelRadius, airSpaces);
		}

		y += length2*slope;

		x += direction.directionX*length2;
		z += direction.directionZ*length2;

		for (int d = 0; d < length3; d++) {
			int dx = x+direction.directionX*d;
			int dz = z+direction.directionZ*d;

			this.generateTunnelSection(dx, y, dz, Math.max(2, Math.round(tunnelRadius-0.125F*d)), airSpaces);
		}

		x += direction.directionX*length3;
		z += direction.directionZ*length3;
	}

	private void generateTunnelSection(int dx, int dy, int dz, int r, HashSet<Coordinate> airSpaces) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					double dd = ReikaMathLibrary.py3d(i, j, k);
					if (dd <= r+0.5) {
						int ddx = dx+i;
						int ddy = dy+j;
						int ddz = dz+k;
						Coordinate c = new Coordinate(ddx, ddy, ddz);
						BlockKey b = dd <= r-0.5 ? new BlockKey(Blocks.air) : new BlockKey(ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
						if (air.contains(c) || airSpaces.contains(c))
							b = new BlockKey(Blocks.air);
						blocks.put(c, b);
						if (b.blockID == Blocks.air)
							air.add(c);
					}
				}
			}
		}
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		for (Coordinate c : blocks.keySet()) {
			BlockKey bk = blocks.get(c);
			world.setBlock(c.xCoord, c.yCoord, c.zCoord, bk.blockID, bk.metadata);
		}
	}

	public Collection<Coordinate> getAirSpaces() {
		return Collections.unmodifiableCollection(air);
	}

	public boolean intersectsWith(HashSet<Coordinate> space) {
		for (Coordinate c : air) {
			if (space.contains(c))
				return true;
		}
		return false;
	}

}
