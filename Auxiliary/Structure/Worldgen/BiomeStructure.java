package Reika.ChromatiCraft.Auxiliary.Structure.Worldgen;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.BiomeStructurePuzzle;
import Reika.ChromatiCraft.Base.FragmentStructureBase;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl.InteractionDelegateTile;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;


public class BiomeStructure extends FragmentStructureBase {

	private BiomeStructurePuzzle puzzle;

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
	}

	public void setRNG(Random r) {
		puzzle = new BiomeStructurePuzzle();
		puzzle.generate(r);
	}

	@Override
	public Coordinate getControllerRelativeLocation() {
		return new Coordinate(0, 0, 0);
	}

	@Override
	public int getStructureVersion() {
		return 0;
	}

	@Override
	public FilledBlockArray getArray(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Coordinate c = new Coordinate(x, y, z);
		ControllerDelegateCallback del = new ControllerDelegateCallback(c);
		this.addCallback(c, new PuzzleCacheCallback(puzzle));
		//this.addCallback(?, del);
		return array;
	}

	private static class PuzzleCacheCallback implements TileCallback {

		private final BiomeStructurePuzzle puzzle;

		private PuzzleCacheCallback(BiomeStructurePuzzle p) {
			puzzle = p;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((TileEntityStructControl)te).setStructureData(puzzle);
		}

	}

	private static class ControllerDelegateCallback implements TileCallback {

		private final Coordinate controllerLocation;

		private ControllerDelegateCallback(Coordinate c) {
			controllerLocation = c;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((InteractionDelegateTile)te).setDelegate(controllerLocation);
		}

	}

}
