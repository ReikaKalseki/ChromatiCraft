/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiomeTerrainShaper;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChunkProviderChroma;
import Reika.DragonAPI.Instantiable.Math.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class TerrainGenGlowingCracks extends ChromaDimensionBiomeTerrainShaper {

	private final SimplexNoiseGenerator crackShapeA;
	private final SimplexNoiseGenerator crackShapeB;
	private final SimplexNoiseGenerator crackShapeC;

	private final SimplexNoiseGenerator shieldingThickness;
	private final SimplexNoiseGenerator shieldingDarkThickness;

	public static final int CRACK_DEPTH = 12;
	private static final int MIN_SHIELD_THICKNESS = 2;
	private static final int MAX_SHIELD_THICKNESS = 10;

	public static final int BIOME_SEARCH = 24;

	public TerrainGenGlowingCracks(long seed) {
		super(seed, Biomes.GLOWCRACKS);

		crackShapeA = new SimplexNoiseGenerator(seed).setFrequency(1/7D);
		crackShapeB = new SimplexNoiseGenerator(-seed).setFrequency(1/7D);
		crackShapeC = new SimplexNoiseGenerator(~seed).setFrequency(1/7D);

		shieldingThickness = new SimplexNoiseGenerator(seed*2).setFrequency(4D);
		shieldingDarkThickness = new SimplexNoiseGenerator(-seed*2).setFrequency(4D);
	}

	@Override
	public void generateColumn(World world, int chunkX, int chunkZ, int i, int k, int surface, Random rand, double edgeFactor) {
		double innerScale = 1/16D;
		double mainScale = 2/3D;
		int dx = chunkX+i;
		int dz = chunkZ+k;
		int dt = 1;
		double rx = this.calcR(chunkX, i, innerScale, mainScale);
		double rz = this.calcR(chunkZ, k, innerScale, mainScale);
		if (Math.abs(crackShapeA.getValue(rx, rz)) <= 0.05 || Math.abs(crackShapeB.getValue(rx, rz)) <= 0.05 || Math.abs(crackShapeC.getValue(rx, rz)) <= 0.05) {
			int space = 30;
			int y = 64+ChunkProviderChroma.VERTICAL_OFFSET+space;
			int m = CRACK_DEPTH+space;
			int st = (int)ReikaMathLibrary.normalizeToBounds(shieldingThickness.getValue(rx, rz), MIN_SHIELD_THICKNESS, Math.min(m-2, MAX_SHIELD_THICKNESS));
			int shield = m-st;
			int dark = m-(int)ReikaMathLibrary.normalizeToBounds(shieldingDarkThickness.getValue(rx, rz), 1, st);
			for (int j = 0; j <= m; j++) {
				int dy = y-j;
				this.cutBlock(world, dx, dy, dz);
				if (j >= shield || (j > 1 && j < shield && rand.nextInt(Math.max(2, shield-j)) <= 1)) {
					for (int d = 2; d < 6; d++) {
						ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
						int ddx = dx+dir.offsetX;
						int ddz = dz+dir.offsetZ;
						Block at = world.getBlock(ddx, dy, ddz);
						if (at == Blocks.stone || at == ChromaBlocks.TIEREDORE.getBlockInstance()) {
							world.setBlock(ddx, dy, ddz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), j >= dark ? BlockType.CLOAK.ordinal() : BlockType.STONE.ordinal(), 2);
						}
					}
				}
			}
			for (int d = 0; d < 6; d++)
				world.setBlock(dx, y-space-CRACK_DEPTH-1-d, dz, ChromatiCraft.lumen.getBlock());
		}
		else {
			for (int y = 64+ChunkProviderChroma.VERTICAL_OFFSET-10; y <= 64+ChunkProviderChroma.VERTICAL_OFFSET+10; y++) {
				if (world.getBlock(dx, y, dz) == Blocks.water || world.getBlock(dx, y, dz) == Blocks.flowing_water) {
					world.setBlock(dx, y, dz, Blocks.sand);
				}
			}
		}
	}

	@Override
	public double getBiomeSearchDistance() {
		return BIOME_SEARCH;
	}


}
