package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockRayblendFloor.TileEntityRayblendFloor;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Magic.ElementMixer;
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
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RayBlendPuzzle extends StructurePiece<RayBlendGenerator> {

	private final int gridSize;
	private final float initialFillFraction;

	private final HashMap<Point, Subgrid> grids = new HashMap();
	private final HashMap<Point, GridCage> cages = new HashMap();

	private final HashSet<Subgrid> unfinished = new HashSet();
	private final HashSet<GridSlot> uncaged = new HashSet();

	private Coordinate generatorOrigin;
	private boolean isComplete;

	public final UUID ID = UUID.randomUUID();

	public RayBlendPuzzle(RayBlendGenerator s, int sz, float f, Random rand) {
		super(s);
		gridSize = sz;
		initialFillFraction = f;

		//this.generateGrids();

		for (int i = 0; i < gridSize; i++) {
			for (int k = 0; k < gridSize; k++) {
				Subgrid sg = new Subgrid(this);
				for (int i2 = 0; i2 < gridSize; i2++) {
					for (int k2 = 0; k2 < gridSize; k2++) {
						int x = i*gridSize+i2;
						int z = k*gridSize+k2;
						sg.createSlot(x, z);
					}
				}
			}
		}

		this.randomize(rand);
	}

	private Subgrid getOrCreateSubgridFor(int x, int z) {
		Subgrid sg = grids.get(new Point(x, z));
		if (sg == null) {
			sg = new Subgrid(this);
		}
		sg.createSlot(x, z);
		return sg;
	}

	private void generateGrids() {
		Subgrid sg = new Subgrid(this);
		this.populate(sg);
		uncaged.addAll(sg.slots.values());
		unfinished.add(sg);
		for (GridSlot gs : sg.slots.values()) {
			grids.put(gs.positionKey(), sg);
		}
	}

	private void populate(Subgrid sg) {

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
		while (!uncaged.isEmpty()) {
			GridSlot slot = ReikaJavaLibrary.getRandomCollectionEntry(rand, uncaged);
			if (slot.isBlocked || slot.color == CrystalElement.BROWN) {
				uncaged.remove(slot);
				continue;
			}
			HashSet<ForgeDirection> dirs = ReikaDirectionHelper.setDirections(false);
			boolean flag = false;
			while (!dirs.isEmpty()) {
				ForgeDirection dir = ReikaJavaLibrary.getRandomCollectionEntry(rand, dirs);
				dirs.remove(dir);
				GridSlot slot2 = slot.getNeighbor(dir);
				if (slot2 != null && uncaged.contains(slot2)) {
					if (this.canCage(slot, slot2)) {
						uncaged.remove(slot);
						uncaged.remove(slot2);
						this.cage(slot, slot2);
						flag = true;
						break;
					}
				}
			}
			if (dirs.isEmpty() && !flag) {
				uncaged.remove(slot);
			}
		}
		for (GridCage g : new ArrayList<GridCage>(cages.values())) {
			HashSet<GridSlot> set = g.getNeighbors();
			while (!set.isEmpty()) {
				GridSlot gs = ReikaJavaLibrary.getRandomCollectionEntry(rand, set);
				set.remove(gs);
				if (g.canAbsorb(gs) && cages.get(gs.positionKey()) == null) {
					g.addSlot(gs);
					cages.put(gs.positionKey(), g);
					break;
				}
			}
		}
	}

	private void cage(GridSlot slot, GridSlot slot2) {
		GridCage g = new GridCage(ElementMixer.instance.getMix(slot.color, slot2.color));
		g.slots.add(slot);
		g.slots.add(slot2);
		cages.put(slot.positionKey(), g);
		cages.put(slot2.positionKey(), g);
	}

	private boolean canCage(GridSlot slot, GridSlot slot2) {
		return slot.color != null && slot2.color != null && ElementMixer.instance.isMixable(slot.color, slot2.color);
	}

	private boolean pickRandomColorForSlot(Random rand, GridSlot gs) {
		HashSet<CrystalElement> set = ReikaJavaLibrary.makeSetFromArray(CrystalElement.elements);
		set.removeAll(gs.parent.presentColors);
		int x = gs.xPos;
		int z = gs.zPos;
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
		if (x < 0 || z < 0 || x >= gridSize*gridSize || z >= gridSize*gridSize)
			return null;
		return grids.get(new Point(x, z)).slots.get(new Point(x, z));
	}

	public void addCrystal(World world, CrystalElement e, int x, int z) {
		GridSlot gs = this.getAt(x, z);
		if (gs == null)
			return;
		gs.currentCrystal = e;
		this.ping(world, x, z);
		isComplete = this.isValid();
	}

	public void removeCrystal(World world, int x, int z) {
		GridSlot gs = this.getAt(x, z);
		if (gs == null)
			return;
		gs.currentCrystal = null;
		isComplete = false;
	}

	public void ping(World world, int x, int z) {
		GridSlot gs = this.getAt(x, z);
		HashSet<GridSlot> pinged = new HashSet();
		if (gs != null && gs.currentCrystal != null) {
			double f = CrystalMusicManager.instance.getDingPitchScale(gs.currentCrystal);
			ChromaSounds.DING.playSoundAtBlock(world, gs.getWorldX(), generatorOrigin.yCoord+2, gs.getWorldZ(), 1, (float)f);

			for (GridSlot gs2 : gs.parent.slots.values()) {
				if (gs != gs2 && !pinged.contains(gs2)) {
					this.colorExclusionPing(world, gs.currentCrystal, gs2);
					pinged.add(gs2);
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
		spawnPingParticle(world, e, gs.getWorldX(), generatorOrigin.yCoord+2, gs.getWorldZ());
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
		for (double i = 0.25; i <= 0.75; i += 0.25) {
			for (double k = 0.25; k <= 0.75; k += 0.25) {
				EntityBlurFX fx = new EntityBlurFX(world, x+i, y+0.5, z+k);
				fx.setColor(e.getColor()).setScale(5).setLife(60).setIcon(ChromaIcons.CENTER).setRapidExpand().setAlphaFading();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	public boolean allowsCrystalAt(CrystalElement e) {

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
					world.setBlock(dx, y+1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
					world.setBlock(dx, y+2, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
				}
				else if (gs.color != null) {
					if (gs.appearsAtStart)
						world.setBlock(dx, y+2, dz, ChromaBlocks.CRYSTAL.getBlockInstance(), gs.color.ordinal());
					else
						world.setBlock(dx, y+2, dz, Blocks.air);
					world.setTileEntity(dx, y+1, dz, ChromaBlocks.RAYBLEND.getBlockInstance(), 0, new RayblendFloorCallback(parent.id, ID, gs.parent.ID, gs.xPos, gs.zPos));
				}
				else {
					world.setBlock(dx, y+1, dz, Blocks.brick_block);
				}
			}
		}
		/*
		for (GridCage g : cages.values()) {
			for (GridSlot gs : g.slots) {
				world.setBlock(gs.getWorldX(), y+1, gs.getWorldZ(), ChromaBlocks.GLASS.getBlockInstance(), g.blendedColor.ordinal());
			}
		}*/
	}

	public boolean isValid() {
		for (Subgrid g : grids.values()) {
			if (!g.isValid())
				return false;
		}
		return true;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public boolean containsCrystalPosition(int x, int y, int z) {
		/*
		boolean xr = x >= generatorOrigin.xCoord && x < generatorOrigin.xCoord+gridSize*gridSize;
		boolean zr = z >= generatorOrigin.zCoord && z < generatorOrigin.zCoord+gridSize*gridSize;
		return xr && zr && y == generatorOrigin.yCoord+1;
		 */
		return y == generatorOrigin.yCoord+2 && this.getAt(x-generatorOrigin.xCoord, z-generatorOrigin.zCoord) != null;
	}

	public CrystalElement getCageColor(int x, int z) {
		GridCage gc = cages.get(new Point(x, z));
		return gc != null ? gc.blendedColor : null;
	}

	private static class Subgrid {

		private final UUID ID = UUID.randomUUID();
		private final RayBlendPuzzle parent;
		private final HashMap<Point, GridSlot> slots = new HashMap();
		private final HashSet<CrystalElement> presentColors = new HashSet();

		private final HashSet<GridSlot> unpopulated = new HashSet();

		private Subgrid(RayBlendPuzzle p) {
			parent = p;
			parent.unfinished.add(this);
		}

		private void createSlot(int x, int z) {
			GridSlot gs = new GridSlot(this, x, z);
			slots.put(gs.positionKey(), gs);
			unpopulated.add(gs);
			parent.uncaged.add(gs);
			parent.grids.put(gs.positionKey(), this);
		}

		public boolean isValid() {
			for (GridSlot gs : slots.values()) {
				if (!gs.isValid())
					return false;
			}
			return true;
		}
		/*
		private Subgrid getNeighbor(ForgeDirection dir) {
			int x = xPos+dir.offsetX;
			int z = zPos+dir.offsetZ;
			if (x >= parent.gridSize) {
				return null;
			}
			else if (x < 0) {
				return null;
			}
			if (z >= parent.gridSize) {
				return null;
			}
			else if (z < 0) {
				return null;
			}
			return parent.grids[x][z];
		}*/

	}

	private static class GridCage {

		private final Collection<GridSlot> slots = new ArrayList();
		private CrystalElement blendedColor;

		private GridCage(CrystalElement e) {
			blendedColor = e;
		}

		public HashSet<GridSlot> getNeighbors() {
			HashSet<GridSlot> set = new HashSet();
			for (GridSlot gs : slots) {
				set.addAll(gs.getNeighbors());
			}
			set.removeAll(slots);
			set.remove(null);
			return set;
		}

		public boolean isExpandable() {
			return ElementMixer.instance.getChildrenOf(blendedColor) != null;
		}

		public boolean canAbsorb(GridSlot gs) {
			return gs.color != null && !gs.isBlocked && ElementMixer.instance.isMixable(blendedColor, gs.color);
		}

		private void addSlot(GridSlot gs) {
			blendedColor = ElementMixer.instance.getMix(blendedColor, gs.color);
			slots.add(gs);
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

		public HashSet<GridSlot> getNeighbors() {
			HashSet<GridSlot> set = new HashSet();
			set.add(this.getNeighbor(ForgeDirection.EAST));
			set.add(this.getNeighbor(ForgeDirection.WEST));
			set.add(this.getNeighbor(ForgeDirection.NORTH));
			set.add(this.getNeighbor(ForgeDirection.SOUTH));
			set.remove(null);
			return set;
		}

		public Point positionKey() {
			return new Point(xPos, zPos);
		}

		private GridSlot getNeighbor(ForgeDirection dir) {
			return parent.parent.getAt(xPos+dir.offsetX, zPos+dir.offsetZ);
		}

		public boolean isValid() {
			return isBlocked ? true : currentCrystal == color;
		}

		public int getWorldX() {
			return parent.parent.generatorOrigin.xCoord+xPos;
		}

		public int getWorldZ() {
			return parent.parent.generatorOrigin.zCoord+zPos;
		}

	}

	private static class RayblendFloorCallback implements TileCallback {

		private final UUID parent;
		private final UUID uid;
		private final UUID grid;
		private final int xPos;
		private final int zPos;

		public RayblendFloorCallback(UUID p, UUID id, UUID grid, int x, int z) {
			parent = p;
			uid = id;
			this.grid = grid;
			xPos = x;
			zPos = z;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			((TileEntityRayblendFloor)te).uid = parent;
			((TileEntityRayblendFloor)te).populate(uid, grid, xPos, zPos);
		}

	}

}