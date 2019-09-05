package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape.BlockPistonTarget.PistonEmitterTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class TapeStage extends StructurePiece<PistonTapeGenerator> {

	private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final DoorSection[] doors;
	private final HashSet<Integer> generatedIDs = new HashSet();
	final TapeArea tape;
	public final ForgeDirection mainDirection;

	public final int doorCount;
	public final int bitsPerDoor;
	public final int totalBitWidth;

	private int height;

	public TapeStage(PistonTapeGenerator g, int bus, int n, Random rand) {
		super(g);
		doorCount = n;
		bitsPerDoor = bus;
		totalBitWidth = bus*n;

		mainDirection = PistonTapeGenerator.DIRECTION;

		tape = new TapeArea(g, new PistonTapeLoop(g, ReikaDirectionHelper.getRightBy90(mainDirection), this));

		doors = new DoorSection[doorCount];
		for (int i = 0; i < doorCount; i++) {
			doors[i] = new DoorSection(g, this, mainDirection, new DoorKey(i, this.generateID(rand), bus));
			height = Math.max(height, doors[i].getHeight());
		}
	}

	private int generateID(Random rand) {
		int id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
		while (generatedIDs.contains(id)) {
			id = MIN_ID+rand.nextInt(MAX_ID-MIN_ID+1);
		}
		generatedIDs.add(id);
		return id;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		tape.generate(world, x-tape.tape.facing.offsetX*6, y, z-tape.tape.facing.offsetZ*6);
		int dx = x+(2+tape.tape.busWidth)*mainDirection.offsetX;
		int dz = z+(2+tape.tape.busWidth)*mainDirection.offsetZ;
		new PistonTapeAccessHall(parent, tape).generate(world, dx-tape.tape.facing.offsetX, y, dz-tape.tape.facing.offsetZ);
		dx += (PistonTapeAccessHall.DEPTH+2+tape.tape.busWidth/2)*mainDirection.offsetX;
		dz += (PistonTapeAccessHall.DEPTH+2+tape.tape.busWidth/2)*mainDirection.offsetZ;
		for (DoorSection s : doors) {
			s.generate(world, dx, y, z);
			dx += (s.getLength()+1)*mainDirection.offsetX;
			dz += (s.getLength()+1)*mainDirection.offsetZ;
		}
		//ForgeDirection left = ReikaDirectionHelper.getLeftBy90(PistonTapeGenerator.DIRECTION);
		//tape.generate(world, x+left.offsetX*(3+DoorSection.WIDTH)-PistonTapeGenerator.DIRECTION.offsetX*(2+tape.busWidth), y, z+left.offsetZ*(3+DoorSection.WIDTH)-PistonTapeGenerator.DIRECTION.offsetZ*(2+tape.busWidth));
	}

	public int getHeight() {
		return height;
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

}
