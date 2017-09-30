/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.IWG;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class UnknownArtefactGenerator implements RetroactiveGenerator {

	public static final UnknownArtefactGenerator instance = new UnknownArtefactGenerator();

	private static final int INNER_RADIUS = 384;
	private static final int OUTER_RADIUS = 512;

	//block coords
	private long cachedSeed;
	private final HashSet<ChunkCoordIntPair> UAChunks = new HashSet();

	private UnknownArtefactGenerator() {

	}

	private void calculateUAChunks(World world) {
		LoreManager.instance.initTowers(world);
		cachedSeed = world.getSeed();
		UAChunks.clear();
		for (int i = 0; i < Towers.towerList.length; i++) {
			Towers t = Towers.towerList[i];
			ChunkCoordIntPair p = t.getRootPosition();
			int x = p.chunkXPos+8;
			int z = p.chunkZPos+8;
			for (double a = 0; a < 360; a += 2) {
				double cos = Math.cos(Math.toRadians(a));
				double sin = Math.sin(Math.toRadians(a));
				for (int r = INNER_RADIUS; r <= OUTER_RADIUS; r += 8) {
					int dx = MathHelper.floor_double(x+r*cos);
					int dz = MathHelper.floor_double(z+r*sin);
					ChunkCoordIntPair p2 = new ChunkCoordIntPair((dx >> 4) << 4, (dz >> 4) << 4);
					UAChunks.add(p2);
				}
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			if (UAChunks.isEmpty() || cachedSeed != world.getSeed())
				this.calculateUAChunks(world);
			if (this.isGennableChunk(world, chunkX*16, chunkZ*16, random)) {
				if (this.generate(world, chunkX*16, chunkZ*16, random)) {
					//ChromatiCraft.logger.log("Successful generation of "+s.name()+" at "+chunkX*16+", "+chunkZ*16);
				}
			}
		}
	}

	private boolean generate(World world, int cx, int cz, Random rand) {
		if (rand.nextInt(40) > 0)
			return false;
		if (!this.isUAChunk(world, cx, cz))
			return false;

		int x = cx+rand.nextInt(16);
		int z = cz+rand.nextInt(16);

		int y = world.getTopSolidOrLiquidBlock(x, z)-1;
		if (!this.canGenerateArtefactAt(world, x, y, z))
			return false;

		world.setBlock(x, y-1, z, ChromaBlocks.ARTEFACT.getBlockInstance());

		return true;
	}

	public boolean isUAChunk(World world, int cx, int cz) {
		/*
		ChunkCoordIntPair p = LoreManager.instance.getNearestTowerChunk(cx, cz);
		if (p == null)
			return false;
		double d = ReikaMathLibrary.py3d(p.chunkXPos*16-cx, 0, p.chunkZPos*16-cz);
		if (d > OUTER_RADIUS || d < INNER_RADIUS)
			return false;
		return true;
		 */
		if (UAChunks.isEmpty() || cachedSeed != world.getSeed())
			this.calculateUAChunks(world);
		return this.canGenerateIn(world) && UAChunks.contains(new ChunkCoordIntPair(cx, cz));
	}

	public static boolean canGenerateArtefactAt(World world, int x, int y, int z) { //the artefact would be at x, y-1, z
		Block b = world.getBlock(x, y, z);
		if (b != Blocks.dirt && b != Blocks.grass)
			return false;
		if (!world.getBlock(x, y+1, z).isAir(world, x, y+1, z))
			return false;
		if (world.getBlock(x, y-1, z) != Blocks.dirt)
			return false;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (dir != ForgeDirection.UP) {
				for (int d = 1; d <= 3; d++) {
					int dx = x+dir.offsetX*d;
					int dy = y+dir.offsetY*d;
					int dz = z+dir.offsetZ*d;
					if (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz))
						return false;
				}
			}
		}
		return true;
	}

	private boolean isVoidWorld(World world, int x, int z) {
		return world.getBlock(x, 0, z) == Blocks.air || world.canBlockSeeTheSky(x, 1, z);
	}

	private boolean isGennableChunk(World world, int x, int z, Random r) {
		if (this.isVoidWorld(world, x, z))
			return false;
		return true;
	}

	private boolean canGenerateIn(World world) {
		if (world.provider.dimensionId != 0)
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !ChromaOptions.FLATGEN.getState()) {
			return ReikaWorldHelper.getSuperflatHeight(world) > 15;
		}
		return true;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Unknown Artefacts";
	}

}
