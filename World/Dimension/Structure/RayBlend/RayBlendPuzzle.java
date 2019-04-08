package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import java.util.HashSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.Structure.RayBlendGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.RadiusTarget;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RayBlendPuzzle extends StructurePiece<RayBlendGenerator> {

	private final int gridSize;
	private final float initialFillFraction;

	private final Subgrid[][] grids;

	private final HashSet<Subgrid> unfinished = new HashSet();

	private Coordinate generatorOrigin;
	private boolean isComplete;

	public RayBlendPuzzle(RayBlendGenerator s, int sz, float f, Random rand) {
		super(s);
		gridSize = sz;
		initialFillFraction = f;
		grids = new Subgrid[sz][sz];
		for (int i = 0; i < grids.length; i++) {
			for (int k = 0; k < grids[i].length; k++) {
				Subgrid sg = new Subgrid(i, k, sz);
				unfinished.add(sg);
				grids[i][k] = sg;
			}
		}
		this.randomize(rand);
	}

	private void randomize(Random rand) {
		while (!unfinished.isEmpty()) {
			//Subgrid g = grids[rand.nextInt(gridSize)][rand.nextInt(gridSize)];
			Subgrid g = ReikaJavaLibrary.getRandomCollectionEntry(rand, unfinished);//g.slots[rand.nextInt(gridSize)][rand.nextInt(gridSize)];
			if (g.unpopulated.isEmpty()) {
				unfinished.remove(g);
			}
			else {
				GridSlot slot = ReikaJavaLibrary.getRandomCollectionEntry(rand, g.unpopulated);//g.slots[rand.nextInt(gridSize)][rand.nextInt(gridSize)];
				if (this.pickRandomColorForSlot(rand, slot)) {
					g.unpopulated.remove(slot);
				}
			}
		}
	}

	private boolean pickRandomColorForSlot(Random rand, GridSlot gs) {
		HashSet<CrystalElement> set = ReikaJavaLibrary.makeSetFromArray(CrystalElement.elements);
		set.removeAll(gs.parent.presentColors);
		int x = gs.getTrueX();
		int z = gs.getTrueZ();
		for (int p = 0; p < gridSize*gridSize; p++) {
			GridSlot g1 = this.getAt(x, p);
			GridSlot g2 = this.getAt(p, z);
			if (g1 != null && g1 != gs) {
				set.remove(g1.color);
			}
			if (g2 != null && g2 != gs) {
				set.remove(g2.color);
			}
		}
		if (set.isEmpty()) {
			gs.isBlocked = true;
		}
		else {
			gs.color = ReikaJavaLibrary.getRandomCollectionEntry(rand, set);
			gs.isGoal = rand.nextInt(20) == 0 && gs.color != CrystalElement.BROWN;
			gs.appearsAtStart = !gs.isGoal && rand.nextFloat() < initialFillFraction;
		}
		return true;
	}

	private GridSlot getAt(int x, int z) {
		int gx = x/4;
		int gz = z/4;
		int ox = x%4;
		int oz = z%4;
		return grids[gx][gz].slots[ox][oz];
	}

	public void addCrystal(World world, CrystalElement e, int x, int z) {
		GridSlot gs = this.getAt(x-generatorOrigin.xCoord, z-generatorOrigin.zCoord);
		gs.currentCrystal = e;
		this.ping(world, x, z);
		isComplete = this.isValid();
	}

	public void removeCrystal(World world, int x, int z) {
		GridSlot gs = this.getAt(x-generatorOrigin.xCoord, z-generatorOrigin.zCoord);
		gs.currentCrystal = null;
		isComplete = false;
	}

	public void ping(World world, int x, int z) {
		GridSlot gs = this.getAt(x-generatorOrigin.xCoord, z-generatorOrigin.zCoord);
		HashSet<GridSlot> pinged = new HashSet();
		if (gs != null && gs.currentCrystal != null) {
			double f = CrystalMusicManager.instance.getDingPitchScale(gs.currentCrystal);
			ChromaSounds.PING.playSoundAtBlock(world, x, generatorOrigin.yCoord+1, z, 1, (float)f);

			for (int i = 0; i < gridSize; i++) {
				for (int k = 0; k < gridSize; k++) {
					GridSlot gs2 = gs.parent.slots[i][k];
					if (gs != gs2 && !pinged.contains(gs2)) {
						this.colorExclusionPing(world, gs.currentCrystal, gs2);
						pinged.add(gs2);
					}
				}
			}
			for (int p = 0; p < gridSize*gridSize; p++) {
				GridSlot g1 = this.getAt(x, p);
				GridSlot g2 = this.getAt(p, z);
				if (g1 != null && g1 != gs && !pinged.contains(g1)) {
					this.colorExclusionPing(world, gs.currentCrystal, g1);
					pinged.add(g1);
				}
				if (g2 != null && g2 != gs && !pinged.contains(g2)) {
					this.colorExclusionPing(world, gs.currentCrystal, g2);
					pinged.add(g2);
				}
			}
		}
	}

	private void colorExclusionPing(World world, CrystalElement e, GridSlot gs) {
		spawnPingParticle(world, e, gs.getWorldX(this), generatorOrigin.yCoord+1, gs.getWorldZ(this));
	}

	public static void spawnPingParticle(World world, CrystalElement e, int x, int y, int z) {
		if (world.isRemote) {
			doPingParticle(world, e, x, y, z);
		}
		else {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.RAYBLENDPING.ordinal(), world, x, y, z, new RadiusTarget(world, x, y, z, 32), e.ordinal());
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doPingParticle(World world, CrystalElement e, int x, int y, int z) {
		EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5);
		fx.setColor(e.getColor()).setScale(6).setLife(60).setIcon(ChromaIcons.FLARE);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		generatorOrigin = new Coordinate(x, y, z);
		for (int i = 0; i < gridSize*gridSize; i++) {
			for (int k = 0; k < gridSize*gridSize; k++) {
				int dx = x+i;
				int dz = z+k;
				boolean light = (i/4+k/4)%2 == 0;
				world.setBlock(dx, y, dz, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), light ? 1 : 0);
				GridSlot gs = this.getAt(i, k);
				if (gs.isBlocked) {
					world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
					world.setBlock(dx, y+1, dz, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), 5);
				}
				else if (gs.color != null) {
					if (gs.appearsAtStart)
						world.setBlock(dx, y+1, dz, ChromaBlocks.CRYSTAL.getBlockInstance(), gs.color.ordinal());
					else
						world.setBlock(dx, y+1, dz, Blocks.air);
				}
				else {
					world.setBlock(dx, y+1, dz, Blocks.brick_block);
				}
			}
		}
	}

	public boolean isValid() {
		for (int i = 0; i < grids.length; i++) {
			for (int k = 0; k < grids[i].length; k++) {
				if (!grids[i][k].isValid())
					return false;
			}
		}
		return true;
	}

	public boolean isComplete() {
		return isComplete;
	}

	private static class Subgrid {

		public final int xPos;
		public final int zPos;
		private final GridSlot[][] slots;
		private final HashSet<CrystalElement> presentColors = new HashSet();
		private final int gridSize;

		private final HashSet<GridSlot> unpopulated = new HashSet();

		private Subgrid(int x, int z, int s) {
			xPos = x;
			zPos = z;
			gridSize = s;
			slots = new GridSlot[s][s];

			for (int i = 0; i < slots.length; i++) {
				for (int k = 0; k < slots[i].length; k++) {
					GridSlot gs = new GridSlot(this, i, k);
					slots[i][k] = gs;
					unpopulated.add(gs);
				}
			}

		}

		public boolean isValid() {
			for (int i = 0; i < slots.length; i++) {
				for (int k = 0; k < slots[i].length; k++) {
					if (!slots[i][k].isValid())
						return false;
				}
			}
			return true;
		}

	}

	private static class GridSlot {

		private final Subgrid parent;
		public final int xPos;
		public final int zPos;

		private boolean isGoal;
		private boolean appearsAtStart;
		private boolean isBlocked;
		private CrystalElement color;

		private CrystalElement currentCrystal;

		public GridSlot(Subgrid s, int x, int z) {
			parent = s;
			xPos = x;
			zPos = z;
		}

		public boolean isValid() {
			return isBlocked ? true : currentCrystal == color;
		}

		public int getTrueX() {
			return parent.xPos*parent.gridSize+xPos;
		}

		public int getTrueZ() {
			return parent.zPos*parent.gridSize+zPos;
		}

		public int getWorldX(RayBlendPuzzle p) {
			return p.generatorOrigin.xCoord+this.getTrueX();
		}

		public int getWorldZ(RayBlendPuzzle p) {
			return p.generatorOrigin.zCoord+this.getTrueZ();
		}

	}

}