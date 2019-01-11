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

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntitySkypeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntitySkypeater.NodeClass;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper.GlowCliffRegion;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import cpw.mods.fml.common.FMLCommonHandler;


public class SkypeaterGenerator implements RetroactiveGenerator {

	public static final SkypeaterGenerator instance = new SkypeaterGenerator();

	private final ArrayList<Coordinate> attemptLocations = new ArrayList();

	private SkypeaterGenerator() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {
				attemptLocations.add(new Coordinate(i*4+2, 0, k*4+2));
			}
		}
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		//if (rand.nextInt(2) == 0) {
		chunkX *= 16;
		chunkZ *= 16;
		ArrayList<Coordinate> attempts = new ArrayList(attemptLocations);
		while (!attempts.isEmpty()) {
			Coordinate c = attempts.remove(rand.nextInt(attempts.size()));
			int dx = chunkX+c.xCoord;//rand.nextInt(16)+8;
			int dz = chunkZ+c.zCoord;//+rand.nextInt(16)+8;
			BiomeGenBase b = world.getBiomeGenForCoords(dx, dz);
			if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
				GlowingCliffsColumnShaper terrain = BiomeGlowingCliffs.getTerrain(world);
				GlowCliffRegion region = terrain.getRegion(world, dx, dz, b);
				if (region == GlowCliffRegion.WATER) {
					//if (rand.nextInt(16) > 0)
					//	return;
					int y = -16+GlowingCliffsColumnShaper.MAX_MIDDLE_TOP_Y+rand.nextInt(8);
					if (this.generateAt(world, dx, y, dz, NodeClass.WATER))
						break;
				}
				else if (region == GlowCliffRegion.SHORES) {
					int y = -4+GlowingCliffsColumnShaper.MAX_MIDDLE_TOP_Y+rand.nextInt(8);
					if (this.generateAt(world, dx, y, dz, NodeClass.SHORE))
						break;
				}
			}
		}
		//}
	}

	private boolean generateAt(World world, int x, int y, int z, NodeClass c) {
		if (world.getBlock(x, y, z).isAir(world, x, y, z) && world.canBlockSeeTheSky(x, y, z) && world.getBlock(x, world.getTopSolidOrLiquidBlock(x, z)+1, z) == Blocks.water) {
			if (CrystalNetworker.instance.getNearestTileOfType(world, x, y, z, TileEntitySkypeater.class, 32) != null)
				return false;
			world.setBlock(x, y, z, ChromaTiles.SKYPEATER.getBlock(), ChromaTiles.SKYPEATER.getBlockMetadata(), 3);
			TileEntitySkypeater te = (TileEntitySkypeater)world.getTileEntity(x, y, z);
			te.setNodeType(c);
			CrystalNetworker.instance.addTile(te);
			return true;
		}
		return false;
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ChromatiCraft Airpeaters";
	}

}
