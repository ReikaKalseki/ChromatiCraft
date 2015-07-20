package Reika.ChromatiCraft.World.Dimension.Structure.ShiftMaze;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class MazeLock extends StructurePiece {

	public MazeLock(DimensionStructureGenerator s) {
		super(s);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {

	}
	/*
	private static void setGate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		world.setTileEntity(x, y, z, ChromaBlocks.SHIFTLOCK.getBlockInstance(), 1, new GateCallback());
	}

	private static void setDoor(ChunkSplicedGenerationCache world, int x, int y, int z, int channel) {
		world.setTileEntity(x, y, z, ChromaBlocks.SHIFTLOCK.getBlockInstance(), 0, new DoorCallback(channel));
	}

	private static class GateCallback implements TileCallback {

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {

		}

	}

	private static class DoorCallback implements TileCallback {

		private final int channel;

		private DoorCallback(int ch) {
			channel = ch;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityShiftLock) {
				((TileEntityShiftLock)te).setChannel(channel);
			}
		}

	}*/

}
