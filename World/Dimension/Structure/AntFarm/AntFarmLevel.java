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

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.AntFarmGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class AntFarmLevel extends StructurePiece {

	public final int posX;
	public final int posY;
	public final int posZ;

	public final int horizontalRadius;
	public final int verticalRadius;

	private final HashMap<Coordinate, Boolean> blocks = new HashMap();

	public AntFarmLevel(AntFarmGenerator a, int x, int y, int z, int rh, int rv) {
		super(a);

		posX = x;
		posY = y;
		posZ = z;

		horizontalRadius = rh;
		verticalRadius = rv;

		this.initialize();
	}

	private void initialize() {
		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		int meta = BlockType.STONE.metadata;
		int r = horizontalRadius;
		int r2 = verticalRadius;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r2; j <= r2; j++) {
					if (ReikaMathLibrary.isPointInsidePowerEllipse(i, j, k, r+0.5, r2+0.5, r+0.5, 4)) {
						int dx = posX+i;
						int dy = posY+j;
						int dz = posZ+k;
						Coordinate c = new Coordinate(dx, dy, dz);
						boolean air = ReikaMathLibrary.isPointInsidePowerEllipse(i, j, k, r-1, r2-1, r-1, 4);
						//((AntFarmGenerator)parent).cutBlock(c, air);
						blocks.put(c, air);
					}
				}
			}
		}
	}

	public void register(AntFarmGenerator g, HashSet<Coordinate> tunnelSpaces) {
		for (Coordinate c : blocks.keySet()) {
			g.cutBlock(c, blocks.get(c));
		}
		tunnelSpaces.addAll(blocks.keySet());
	}

	public boolean intersectsWith(HashSet<Coordinate> tunnelSpaces) {
		for (Coordinate c : blocks.keySet()) {
			if (tunnelSpaces.contains(c))
				return true;
		}
		return false;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}

}
