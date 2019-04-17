package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendPuzzle;


public class RayBlendGenerator extends DimensionStructureGenerator {

	private final HashMap<UUID, RayBlendPuzzle> puzzles = new HashMap();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int x = chunkX;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;
		RayBlendPuzzle rb = this.createPuzzle(rand);
		puzzles.put(rb.ID, rb);
		rb.generate(world, x, y, z);
	}

	private RayBlendPuzzle createPuzzle(Random rand) {
		RayBlendPuzzle ret = new RayBlendPuzzle(this, 4, this.getInitialFillFraction(), rand);
		while (!ret.prepare(rand)) {
			ChromatiCraft.logger.log("Puzzle population failed; generating a new rayblend puzzle");
			ret = new RayBlendPuzzle(this, 4, this.getInitialFillFraction(), rand);
		}
		return ret;
	}

	private float getInitialFillFraction() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 0:
				return 0.33F;
			case 1:
				return 0.2F;
			case 2:
			default:
				return 0.1F;
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
	protected boolean hasBeenSolved(World world) {
		for (RayBlendPuzzle rb : puzzles.values()) {
			if (!rb.isComplete())
				return false;
		}
		return true;
	}

	@Override
	protected void openStructure(World world) {

	}

	@Override
	protected void clearCaches() {
		puzzles.clear();
	}
	/*
	@Override
	public void onBlockUpdate(World world, int x, int y, int z, Block b) {
		//if (!isDoneGenerating)
		//	return;
		b = world.getBlock(x, y+1, z);
		RayBlendPuzzle p = this.getPuzzleFromCrystalPos(x, y+1, z);
		if (p != null) {
			if (b instanceof CrystalTypeBlock && world.getBlockMetadata(x, y-1, z) <= 1) {
				p.addCrystal(world, CrystalElement.elements[world.getBlockMetadata(x, y+1, z)], x, z);
			}
			else {
				p.removeCrystal(world, x, z);
			}
		}
	}

	private RayBlendPuzzle getPuzzleFromCrystalPos(int x, int y, int z) {
		for (RayBlendPuzzle rp : puzzles.values()) {
			if (rp.containsCrystalPosition(x, y, z)) {
				return rp;
			}
		}
		return null;
	}
	 */

	public boolean allowsCrystalAt(UUID id, int x, int z, CrystalElement e) {
		RayBlendPuzzle p = puzzles.get(id);
		if (p != null) {
			return p.allowsCrystalAt(x, z, e);
		}
		return true;
	}

	public void setCrystal(World world, UUID id, int x, int z, CrystalElement e) {
		RayBlendPuzzle p = puzzles.get(id);
		if (p != null) {
			if (e != null)
				p.addCrystal(world, e, x, z);
			else
				p.removeCrystal(world, x, z);
		}
	}

	public CrystalElement getCageColor(UUID id, int x, int z) {
		RayBlendPuzzle p = puzzles.get(id);
		return p != null ? p.getCageColor(x, z) : null;
	}
}
