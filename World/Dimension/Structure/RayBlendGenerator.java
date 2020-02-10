package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.OverlayColor;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.PuzzleProfile;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendLoot;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlend.RayBlendPuzzle;
import Reika.DragonAPI.Instantiable.Data.GappedRange;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class RayBlendGenerator extends DimensionStructureGenerator {

	public static boolean DEBUG = false;

	private final HashMap<UUID, RayBlendPuzzle> puzzles = new HashMap();
	//private final HashMap<UUID, Coordinate> doors = new HashMap();

	@Override
	public void updateTick(World world) {
		if (!world.isRemote) {
			for (RayBlendPuzzle p : puzzles.values()) {
				p.tick(world);
			}
		}
	}

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int x = chunkX;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;

		PuzzleProfile[] sz = this.getProfileList();
		int extra = 5+RayBlendPuzzle.PADDING_LOWER*2+RayBlendPuzzle.PADDING_UPPER*2;
		HashMap<Integer, UUID> doors = new HashMap();

		int len = 0;
		for (PuzzleProfile p : sz) {
			len += p.gridSize*p.gridSize+extra;
		}

		GappedRange gr = new GappedRange();

		int dx = x;
		for (int i = 0; i < sz.length; i++) {
			PuzzleProfile p = sz[i];
			RayBlendPuzzle rb = this.createPuzzle(rand, p);
			if (rb == null)
				continue;
			int sizePre = i == 0 ? 0 : sz[i-1].gridSize;
			puzzles.put(rb.ID, rb);
			dx += DEBUG ? 7 : extra+sizePre*sizePre;
			int dz = z-p.gridSize*p.gridSize/2;
			rb.generate(world, dx, y, dz);
			gr.addGap(rb.getGenerationBounds().minX+1, rb.getGenerationBounds().maxX-1);
			doors.put(rb.getGenerationBounds().maxX+2, rb.ID);
		}
		len += 7;

		gr.addEndpoint(x-6, true);
		gr.addEndpoint(x+len+2, true);

		if (!DEBUG) {
			int h = 4;
			for (int d = -2; d < len+4; d++) {
				int odx = x+d;
				if (!gr.isInGap(odx)) {
					int w = 3;
					for (int i = -w; i <= w; i++) {
						for (int dh = -1; dh <= h; dh++) {
							if (Math.abs(i) == w || dh == -1 || dh == h) {
								world.setBlock(odx, y+3+dh, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							}
							else {
								if (doors.containsKey(odx)) {
									world.setBlock(odx, y+3+dh, z+i, ChromaBlocks.DOOR.getBlockInstance());
									UUID id = doors.get(odx);
									puzzles.get(id).addDoor(new Coordinate(odx, y+3+dh, z+i));
								}
								else {
									world.setBlock(odx, y+3+dh, z+i, Blocks.air);
								}
							}
						}
					}
				}
			}
		}

		new RayBlendLoot(this).generate(world, x+len+3, y+2, z-7);
		this.addDynamicStructure(new RayBlendEntrance(this), x-4, z);
	}

	private RayBlendPuzzle createPuzzle(Random rand, PuzzleProfile p) {
		int attempts = 1;
		RayBlendPuzzle ret = new RayBlendPuzzle(this, p.gridSize, p.initialFill, rand);
		while (!ret.prepare(p, rand)) {
			//ChromatiCraft.logger.log("Puzzle population failed; generating a new rayblend puzzle");
			ret = new RayBlendPuzzle(this, p.gridSize, p.initialFill, rand);
			attempts++;
			if (attempts > 40) {
				ChromatiCraft.logger.logError("Failed to generate a "+p+" puzzle in a reasonable time!");
				return null;
			}
		}
		//ChromatiCraft.logger.log("Successfully generated a "+p+" puzzle in "+attempts+" attempts.");
		return ret;
	}

	private PuzzleProfile[] getProfileList() {
		if (DEBUG)
			return new PuzzleProfile[] {new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0), new PuzzleProfile(2, 0)};
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return new PuzzleProfile[] {new PuzzleProfile(2, 0.5F), new PuzzleProfile(2, 0.4F, true, false), new PuzzleProfile(3, 0.4F), new PuzzleProfile(3, 0.3F, true, true), new PuzzleProfile(4, 0.3F, true, false)};
			case 2:
				return new PuzzleProfile[] {new PuzzleProfile(2, 0.5F), new PuzzleProfile(2, 0.4F, true, false), new PuzzleProfile(2, 0.25F, true, true), new PuzzleProfile(3, 0.25F), new PuzzleProfile(3, 0.2F, true, false), new PuzzleProfile(4, 0.2F, true, true)};
			case 3:
			default:
				return new PuzzleProfile[] {new PuzzleProfile(2, 0.5F), new PuzzleProfile(2, 0.4F, true, false), new PuzzleProfile(2, 0.3F, true, true), new PuzzleProfile(2, 0.2F, true, true), new PuzzleProfile(3, 0.2F), new PuzzleProfile(3, 0.15F, true, false), new PuzzleProfile(3, 0.1F, true, true), new PuzzleProfile(4, 0.2F, true, false), new PuzzleProfile(4, 0.1F, true, true)};
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
		for (RayBlendPuzzle rb : puzzles.values()) {
			rb.forceOpen(world);
		}
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

	public boolean allowsCrystalAt(World world, UUID id, int x, int z, CrystalElement e) {
		RayBlendPuzzle p = puzzles.get(id);
		if (p != null) {
			return p.allowsCrystalAt(world, x, z, e);
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
			p.updateDoors(world);
		}
	}

	public OverlayColor getCageColor(UUID id, int x, int z) {
		RayBlendPuzzle p = puzzles.get(id);
		return p != null ? p.getCageColor(x, z) : null;
	}
}
