package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class TapeStage extends StructurePiece<PistonTapeGenerator> {

	private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final ArrayList<DoorSection> doors = new ArrayList();
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

		for (int i = 0; i < doorCount; i++) {
			DoorSection s = new DoorSection(g, this, mainDirection, new DoorKey(i, this.generateID(rand), bus));
			doors.add(s);
			height = Math.max(height, s.getHeight());
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
		tape.generate(world, x, y, z);
		int dx = x+(6+tape.tape.busWidth)*mainDirection.offsetX;
		int dz = z+(6+tape.tape.busWidth)*mainDirection.offsetZ;
		new PistonTapeAccessHall(parent).generate(world, dx, y, dz);
		dx += (PistonTapeAccessHall.DEPTH+6)*mainDirection.offsetX;
		dz += (PistonTapeAccessHall.DEPTH+6)*mainDirection.offsetZ;
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

	public void fireEmitters(World world) {
		for (int i = 0; i < bitsPerDoor; i++) {
			Coordinate c = tape.tape.getEmitter(i);
			EmitterTile te = (EmitterTile)c.getTileEntity(world);
			te.fire();
		}
	}

}
