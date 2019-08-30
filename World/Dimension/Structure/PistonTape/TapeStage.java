package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public class TapeStage extends StructurePiece<PistonTapeGenerator> {

	private static final int MAX_ID = 511;
	private static final int MIN_ID = 1;

	private final ArrayList<DoorSection> doors = new ArrayList();
	private final HashSet<Integer> generatedIDs = new HashSet();

	public final int doorCount;
	public final int bitsPerDoor;
	public final int totalBitWidth;

	private int height;

	public TapeStage(PistonTapeGenerator g, int bus, int n, Random rand) {
		super(g);
		doorCount = n;
		bitsPerDoor = bus;
		totalBitWidth = bus*n;

		for (int i = 0; i < doorCount; i++) {
			DoorSection s = new DoorSection(g, this, PistonTapeGenerator.DIRECTION, new DoorKey(this.generateID(rand), bus));
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
		int dx = x;
		for (DoorSection s : doors) {
			s.generate(world, dx, y, z);
			dx += s.getLength()+1;
		}
	}

	public int getHeight() {
		return height;
	}

}
