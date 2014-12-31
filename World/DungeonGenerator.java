package Reika.ChromatiCraft.World;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModInteract.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
import cpw.mods.fml.common.IWorldGenerator;

public class DungeonGenerator implements IWorldGenerator {

	public static final DungeonGenerator instance = new DungeonGenerator();

	private final ForgeDirection[] dirs = ForgeDirection.values();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (this.canGenerateIn(world)) {
			if (this.isGennableChunk(world, chunkX, chunkZ)) {
				this.tryGenerate(world, chunkX*16, chunkZ*16, random);
			}
		}
	}

	private void tryGenerate(World world, int x, int z, Random random) {

	}

	private boolean isGennableChunk(World world, int chunkX, int chunkZ) {
		return false;
	}

	private boolean canGenerateIn(World world) {
		if (world.provider.dimensionId == 0)
			return true;
		if (Math.abs(world.provider.dimensionId) == 1)
			return false;
		if (world.provider.dimensionId == ExtraUtilsHandler.getInstance().darkID)
			return false;
		if (world.provider.dimensionId == TwilightForestHandler.getInstance().dimensionID)
			return false;
		if (world.getWorldInfo().getTerrainType() == WorldType.FLAT)
			return ChromaOptions.FLATGEN.getState();
		return true;
	}

}
