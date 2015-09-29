package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;

public class ChunkResetter implements TickHandler {

	public static final ChunkResetter instance = new ChunkResetter();

	public static final int SPEED = 32;

	private final MultiMap<Integer, ChunkReset> chunks = new MultiMap();

	private ChunkResetter() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world instanceof WorldServer) {
			ArrayList<ChunkReset> rem = new ArrayList();
			for (ChunkReset r : chunks.get(world.provider.dimensionId)) {
				r.step((WorldServer)world);
				if (r.isDone()) {
					r.onFinish((WorldServer)world);
					rem.add(r);
				}
			}
			for (ChunkReset r : rem)
				chunks.remove(world.provider.dimensionId, r);
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Chunk Reset";
	}

	public void addChunk(WorldServer world, Chunk ch) {
		chunks.addValue(world.provider.dimensionId, new ChunkReset(world, ch));
	}

	private static class ChunkReset {

		//private final ChunkCoordIntPair position;
		private final IChunkProvider generator;
		private final Chunk regen;

		private int stepX = 0;
		private int stepY = 0;
		private int stepZ = 0;

		private ChunkReset(WorldServer world, Chunk c) {
			//position = c.getChunkCoordIntPair();

			generator = world.theChunkProviderServer.currentChunkProvider;
			regen = generator.provideChunk(c.xPosition, c.zPosition);
		}

		private void step(WorldServer world) {
			//ReikaJavaLibrary.pConsole("Ticking reset of "+regen.getChunkCoordIntPair()+"; "+new Coordinate(stepX, stepY, stepZ));
			for (int i = 0; i < SPEED*65536; i++) {
				this.resetCoordinate(world);

				this.updateStepPosition();
				if (this.isDone()) {
					this.onFinish(world);
					return;
				}
			}
		}

		private void onFinish(WorldServer world) {
			world.getChunkFromChunkCoords(regen.xPosition, regen.zPosition).isTerrainPopulated = false;
			generator.populate(generator, regen.xPosition, regen.zPosition);
			GameRegistry.generateWorld(regen.xPosition, regen.zPosition, world, generator, generator);
			ReikaJavaLibrary.pConsole("Finished regenning "+regen.getChunkCoordIntPair());
		}

		private void resetCoordinate(WorldServer world) {
			int wx = stepX+(regen.xPosition << 4);
			int wz = stepZ+(regen.zPosition << 4);
			Block prev = world.getBlock(wx, stepY, wz);
			Block b = regen.getBlock(stepX, stepY, stepZ);
			//ReikaJavaLibrary.pConsole("Replacing "+prev+" with "+b+" @ "+wx+", "+stepY+", "+wz);
			int meta = regen.getBlockMetadata(stepX, stepY, stepZ);
			world.setBlock(wx, stepY, wz, b, meta, 3);

			TileEntity te = regen.getTileEntityUnsafe(stepX, stepY, stepZ);
			if (te != null) {
				world.setTileEntity(wx, stepY, wz, te);
			}

			if (prev != b) {
				if (prev != Blocks.air)
					ReikaSoundHelper.playBreakSound(world, wx, stepY, wz, prev);
				if (b != Blocks.air)
					ReikaSoundHelper.playPlaceSound(world, wx, stepY, wz, b);
			}
		}

		private void updateStepPosition() {
			boolean flag1 = false;
			boolean flag2 = false;
			stepX++;
			if (stepX >= 16) {
				stepX = 0;
				flag1 = true;
			}
			if (flag1) {
				stepZ++;
				//ReikaJavaLibrary.pConsole(stepY+" > "+stepZ+":"+range+" > "+ores.getSize(), Side.SERVER);
				if (stepZ >= 16) {
					stepZ = 0;
					flag2 = true;
				}
				if (flag2) {
					stepY++;
				}
			}
		}

		private boolean isDone() {
			return stepY >= 256;
		}
	}

}
