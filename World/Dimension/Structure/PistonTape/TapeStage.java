package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget.PistonEmitterTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class TapeStage extends StructurePiece<PistonTapeGenerator> {

	//private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final DoorSection[] doors;
	private final HashSet<Integer> generatedIDs = new HashSet();
	final TapeArea tape;
	public final ForgeDirection mainDirection;

	public final int index;
	public final int doorCount;
	public final int bitsPerDoor;
	public final int totalBitWidth;

	public final int height;

	private Coordinate exit;
	private Coordinate entrance;

	private boolean[] pings;

	public TapeStage(PistonTapeGenerator g, int idx, int bus, int n, ForgeDirection dir, Random rand) {
		super(g);
		index = idx;
		doorCount = n;
		bitsPerDoor = bus;
		totalBitWidth = bus*n;
		mainDirection = dir;

		tape = new TapeArea(g, mainDirection, new PistonTapeLoop(g, ReikaDirectionHelper.getRightBy90(mainDirection), this));
		tape.tape.randomize(rand);

		height = tape.tape.dimensions.totalHeight+3;

		doors = new DoorSection[doorCount];
		for (int i = 0; i < doorCount; i++) {
			DoorKey dk = new DoorKey(i, bus, this.getColorList(i));
			doors[i] = new DoorSection(g, this, mainDirection, dk, i, i == doorCount-1);
		}

		pings = new boolean[doorCount];
	}

	public ArrayList<Integer> getNumberList() {
		ArrayList<Integer> li = new ArrayList();
		for (DoorSection s : doors) {
			li.add(s.doorData.value);
		}
		return li;
	}

	public Coordinate getEntrance() {
		return entrance;
	}

	public Coordinate getExit() {
		return exit;
	}

	private RGBColorData[] getColorList(int i) {
		RGBColorData[] data = new RGBColorData[bitsPerDoor];
		for (int d = 0; d < data.length; d++) {
			data[d] = tape.tape.getColor(d, i);
		}
		return data;
	}
	/*
	private int generateID(Random rand) {
		int MAX_ID = ReikaMathLibrary.intpow2(2, 3*bitsPerDoor);
		int id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
		while (generatedIDs.contains(id) && generatedIDs.size() < MAX_ID-MIN_ID) {
			id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
		}
		generatedIDs.add(id);
		return id;
	}
	 */
	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int dx = x;
		int dz = z;
		entrance = new Coordinate(x-tape.tape.facing.offsetX*3-mainDirection.offsetX, y+2, z-tape.tape.facing.offsetZ*3-mainDirection.offsetZ);
		new PistonTapeEntryArea(parent, this, tape).generate(world, dx-tape.tape.facing.offsetX, y, dz-tape.tape.facing.offsetZ);
		dx += (PistonTapeEntryArea.DEPTH+1)*mainDirection.offsetX;
		dz += (PistonTapeEntryArea.DEPTH+1)*mainDirection.offsetZ;
		tape.generate(world, dx-tape.tape.facing.offsetX*6, y, dz-tape.tape.facing.offsetZ*6);
		dx += (2+tape.tape.busWidth)*mainDirection.offsetX;
		dz += (2+tape.tape.busWidth)*mainDirection.offsetZ;
		new PistonTapeAccessHall(parent, tape).generate(world, dx-tape.tape.facing.offsetX, y, dz-tape.tape.facing.offsetZ);
		dx += (PistonTapeAccessHall.DEPTH+2+tape.tape.busWidth/2)*mainDirection.offsetX;
		dz += (PistonTapeAccessHall.DEPTH+2+tape.tape.busWidth/2)*mainDirection.offsetZ;
		for (DoorSection s : doors) {
			s.generate(world, dx, y, z);
			dx += (s.getLength()+1)*mainDirection.offsetX;
			dz += (s.getLength()+1)*mainDirection.offsetZ;
		}
		exit = new Coordinate(dx-tape.tape.facing.offsetX*3, y+2, dz-tape.tape.facing.offsetZ*3);
		//ForgeDirection left = ReikaDirectionHelper.getLeftBy90(PistonTapeGenerator.DIRECTION);
		//tape.generate(world, x+left.offsetX*(3+DoorSection.WIDTH)-PistonTapeGenerator.DIRECTION.offsetX*(2+tape.busWidth), y, z+left.offsetZ*(3+DoorSection.WIDTH)-PistonTapeGenerator.DIRECTION.offsetZ*(2+tape.busWidth));
	}

	public void fireEmitters(World world, int stage) {
		for (int i = 0; i < bitsPerDoor; i++) {
			Coordinate c = tape.tape.getEmitter(i);
			EmitterTile te = (EmitterTile)c.getTileEntity(world);
			te.fire();
			c = tape.tape.getTarget(i);
			PistonEmitterTile pt = (PistonEmitterTile)c.getTileEntity(world);
			pt.setTarget(stage, doors[stage].doorData.getValue(i).getTargetLocation(), mainDirection, this.getHallSplinePoints(i));
		}
	}

	private ArrayList<Coordinate> getHallSplinePoints(int idx) {
		ArrayList<Coordinate> li = new ArrayList();
		Coordinate c = tape.tape.getTarget(idx).offset(tape.tape.facing, -4);
		li.add(c);
		c = c.offset(mainDirection, 3);
		li.add(c);
		c = c.offset(tape.tape.facing, -5);
		li.add(c);
		return li;
	}

	public boolean cycle(World world) {
		return tape.tape.cycle(world);
	}

	public int getTotalLength() {
		return tape.tape.dimensions.bitLength;
	}

	public int getDirectionLength() {
		int base = 1+3+PistonTapeEntryArea.DEPTH+PistonTapeAccessHall.DEPTH+tape.getWidth();
		for (DoorSection s : doors) {
			base += s.getLength()+1;
		}
		return base;
	}

	public UUID getID() {
		return parent.id;
	}

	public void openAllDoors(World world) {
		for (DoorSection s : doors) {
			s.forceOpenDoor(world);
		}
	}

	public void validate(int door) {
		pings[door] = true;
	}

	public void resetValidate() {
		pings = new boolean[doorCount];
	}

	public boolean isSolved() {
		return ReikaArrayHelper.isAllTrue(pings);
	}

}
