package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.DoorKey;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeData;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.PistonTapeLoop;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.TapeAssemblyArea;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTape.TapeStage;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;


public class PistonTapeGenerator extends DimensionStructureGenerator {

	private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final HashSet<Integer> generatedIDs = new HashSet();
	private ArrayList<TapeStage> doors;

	private Coordinate emitterColumnBase;
	private Coordinate targetColumnBase;

	private int length;

	private boolean isActive = false;

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		doors = new ArrayList();
		int x = chunkX;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;
		length = this.getDoorCount();
		new TapeAssemblyArea(this).generate(world, x, y, z);
		x += length+2;
		this.generateDataTile(x, y+1, z);
		x += 2;
		for (int i = 0; i < length; i++) {
			int id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
			while (generatedIDs.contains(id)) {
				id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
			}
			generatedIDs.add(id);
			HashSet<Coordinate> set = new HashSet();
			TapeStage ts = new TapeStage(new DoorKey(id, set), x, y, z);
			doors.add(ts);
			x++;
		}
		PistonTapeLoop pl = new PistonTapeLoop(this);
		pl.generate(world, x, y, z+12);
	}

	public int getLength() {
		return length;
	}

	private EmitterTile getEmitter(World world, int i) {
		return (EmitterTile)emitterColumnBase.offset(0, i, 0).getTileEntity(world);
	}

	public TargetTile getTarget(World world, int i) {
		return (TargetTile)targetColumnBase.offset(0, i, 0).getTileEntity(world);
	}

	private int getDoorCount() {
		return 12;
	}

	@Override
	public StructureData createDataStorage() {
		return new PistonTapeData(this, doors);
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
	protected boolean hasBeenSolved(World world) {
		return false;
	}

	@Override
	protected void openStructure(World world) {

	}

	@Override
	protected void clearCaches() {
		generatedIDs.clear();
		doors = null;
	}

	public void setActive(World world, boolean active) {
		isActive = active;

		EmitterTile te1 = this.getEmitter(world, 0);
		EmitterTile te2 = this.getEmitter(world, 1);
		EmitterTile te3 = this.getEmitter(world, 2);

		te1.keepFiring = isActive;
		te2.keepFiring = isActive;
		te3.keepFiring = isActive;
	}

	public void tick(World world) {
		if (isActive) {
			//door.setOpen(world, this.isCorrect(world));
		}
	}

	/*
	have only one set of emitters and targets, and move the bit blocks

	MAKE THE PLAYER BUILD THE TAPE **NOT** IN FRONT OF THE READ HEAD - MAKE IT MOVE THERE LATER DURING PLAYBACK

	ALSO, HAVE THE PLAYER BUILD IT INLINE, BUT THE MECHANISM SPLITS IT INTO THREE LINES OF THREE*/

	static abstract class PulseTileCallback implements TileCallback {

		protected final Coordinate columnBase;

		protected PulseTileCallback(int x, int y, int z) {
			columnBase = new Coordinate(x, y, z);
		}

	}

	static class EmitterCallback extends PulseTileCallback {

		protected EmitterCallback(int x, int y, int z) {
			super(x, y, z);
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof EmitterTile) {
				((EmitterTile)te).renderAsFullBlock = true;
				((EmitterTile)te).silent = true;
				((EmitterTile)te).speedFactor = 1.25;
				((EmitterTile)te).setDirection(CubeDirections.SOUTH);
			}
		}
	}

	static class TargetCallback extends PulseTileCallback {

		private final DoorKey door;

		protected TargetCallback(DoorKey d, int x, int y, int z) {
			super(x, y, z);
			door = d;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TargetTile) {
				((TargetTile)te).renderAsFullBlock = true;
				((TargetTile)te).autoReset = PistonTapeData.STEP_DURATION;
				((TargetTile)te).setDirection(CubeDirections.SOUTH);

				int dy = te.yCoord-columnBase.yCoord;
				ColorData c = new ColorData(true, true, true);
				switch(dy) {
					case 0:
						c = door.getColor1();
						break;
					case 1:
						c = door.getColor2();
						break;
					case 2:
						c = door.getColor3();
						break;
				}
				((TargetTile)te).setColor(c);
			}
		}
	}

}
