package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterFloor;
import Reika.ChromatiCraft.World.Dimension.Structure.Water.WaterPath;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class WaterPuzzleGenerator extends DimensionStructureGenerator {

	private final LinkedList<WaterFloor> levels = new LinkedList();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {

		posX = chunkX;
		posZ = chunkZ;
		posY = 10+rand.nextInt(10);

		int n = this.getSize();
		int startx = ReikaRandomHelper.getRandomPlusMinus(0, 2);
		int startz = ReikaRandomHelper.getRandomPlusMinus(0, 2);
		for (int i = 0; i < n; i++) {
			int r = 2+i;
			int endx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			int endz = ReikaRandomHelper.getRandomPlusMinus(0, r);
			WaterPath path = new WaterPath(startx, startz, endx, endz);
			path.genPath();
			WaterFloor w = new WaterFloor(this, r, path);
			levels.add(w);
			startx = endx;
			startz = endx;
		}

		int y = posY+levels.size()*WaterFloor.HEIGHT;

		for (WaterFloor l : levels) {
			l.generate(world, posX, y, posZ);
			y -= l.HEIGHT;
		}
	}

	private static int getSize() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 6;
			case 2:
				return 8;
			case 3:
			default:
				return 12;
		}
	}

	@Override
	public StructureData createDataStorage() {
		return null;
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
	public boolean hasBeenSolved(World world) {
		for (WaterFloor f : levels) {
			if (!f.hasBeenSolved())
				return false;
		}
		return true;
	}

	@Override
	protected void clearCaches() {
		levels.clear();
	}

}
