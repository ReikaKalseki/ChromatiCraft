package Reika.ChromatiCraft.World.Dimension.Structure.RayBlend;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.OverlayColor;
import Reika.ChromatiCraft.Auxiliary.OverlayColor.IntOverlayColor;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
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
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Instantiable.IO.PacketTarget.RadiusTarget;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary.CloneCallback;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RayBlendPuzzle extends StructurePiece<RayBlendGenerator> {

	private final int gridSize;
	private final float initialFillFraction;

	private final HashMap<Point, Subgrid> grids = new HashMap();
	private final HashMap<Point, GridCage> cages = new HashMap();
	private final HashMap<Point, CrystalMix> mixPoints = new HashMap();

	private final HashSet<Point> unGriddedPoints = new HashSet();

	private final HashSet<Subgrid> unfinished = new HashSet();
	private final HashSet<GridSlot> uncaged = new HashSet();
	private final HashSet<GridSlot> candidateStarts = new HashSet();

	private final HashSet<Coordinate> doors = new HashSet();

	private Coordinate generatorOrigin;
	private boolean isComplete;

	private final int edgeLength;
	private final int totalCellCount;
	private final int cellsPerSubgrid;

	public final UUID ID = UUID.randomUUID();

	private static boolean GENERATE_SOLVED = false;

	public static final int PADDING_LOWER = 3;
	public static final int STEP_HEIGHT = 1;
	public static final int PADDING_UPPER = 4;

	private BlockBox generationBounds = BlockBox.nothing();

	public RayBlendPuzzle(RayBlendGenerator s, int sz, float f, Random rand) {
		super(s);
		gridSize = sz;
		initialFillFraction = f;
		edgeLength = gridSize*gridSize;
		totalCellCount = edgeLength*edgeLength;
		cellsPerSubgrid = edgeLength;
	}

	public boolean prepare(PuzzleProfile p, Random rand) {
		Subgrid.CURRENTINDEX = 0;
		/*
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
		}*/

		for (int i = 0; i < edgeLength; i++) {
			for (int k = 0; k < edgeLength; k++) {
				unGriddedPoints.add(new Point(i, k));
			}
		}

		boolean flag = this.generateGrids(rand);
		if (flag) {
			//this.debugPrint();
			this.randomize(p, rand);
		}
		return flag;

	}

	public void addDoor(Coordinate c) {
		doors.add(c);
	}

	private void debugPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < edgeLength; i++) {
			sb.append("[");
			for (int k = 0; k < edgeLength; k++) {
				Subgrid gs = grids.get(new Point(i, k));
				if (gs == null) {
					sb.append("0");
				}
				else {
					char c = (char)('A'+gs.index);
					sb.append(c);
				}
			}
			sb.append("]\n");
		}
		sb.append("}\n");
		ReikaJavaLibrary.pConsole(sb.toString());
	}

	private Subgrid getOrCreateSubgridFor(int x, int z) {
		Subgrid sg = grids.get(new Point(x, z));
		if (sg == null) {
			sg = new Subgrid(this);
		}
		sg.createSlot(x, z);
		return sg;
	}

	private boolean generateGrids(Random rand) {
		while (!unGriddedPoints.isEmpty()) {
			Subgrid sg = new Subgrid(this);
			if (!this.populate(sg, rand))
				return false;

			uncaged.addAll(sg.slots.values());
			unfinished.add(sg);
			for (GridSlot gs : sg.slots.values()) {
				grids.put(gs.positionKey(), sg);
			}
		}
		return this.isNotUniform(rand);
	}

	private boolean isNotUniform(Random rand) {
		Subgrid g1 = ReikaJavaLibrary.getRandomCollectionEntry(rand, grids.values());
		for (Subgrid g : grids.values()) {
			//ReikaJavaLibrary.pConsole("Comparing "+g.slots.keySet()+" and "+g1.slots.keySet()+": "+g1.isCongruent(g));
			if (!g1.isCongruent(g)) {
				return true;
			}
		}
		return false;
	}

	private boolean populate(Subgrid sg, Random rand) {

		int attempts0 = 0;
		HashSet<Point> starts = new HashSet(unGriddedPoints);
		while (attempts0 < 80) {
			attempts0++;
			Point start = this.getWeightedRandomStart(rand, starts);
			starts.remove(start);
			if (start == null) {
				return false;
			}

			boolean valid = unGriddedPoints.size() == totalCellCount || this.canExpandInto(sg, start);
			if (valid) {
				sg.createSlot(start.x, start.y);
				break;
			}
			else {
				if (starts.isEmpty())
					return false;
			}
		}

		int attempts = 0;
		while (attempts < 80) {
			attempts++;
			while (sg.size() < cellsPerSubgrid) {
				Point p = this.getCandidateNextPoint(sg, rand);
				if (p == null) {
					sg.clear();
					break;
				}
				sg.createSlot(p.x, p.y);
			}
			if (sg.size() == cellsPerSubgrid && sg.getStringiness() <= this.getMaxAllowedStringiness()) {
				return true;
			}
		}
		return false;
	}

	private double getMaxAllowedStringiness() {
		switch(gridSize) {
			case 1:
			case 2:
				return 1;
			case 3:
				return 0.9;
			case 4:
				return 0.7;
			case 5:
				return 0.55;
			case 6:
				return 0.4;
			default:
				return Math.max(0.1, 0.4-0.05*(gridSize-6));
		}
	}

	private Point getWeightedRandomStart(Random rand, HashSet<Point> starts) {
		if (starts.isEmpty())
			return null;
		WeightedRandom<Point> wr = new WeightedRandom();
		for (Point p : starts) {
			//ReikaJavaLibrary.pConsole(p+" > "+this.getFilledNeighbors(p));
			wr.addEntry(p, this.getFilledNeighbors(p));
		}
		return wr.isEmpty() ? null : wr.getRandomEntry();
	}

	private int getFilledNeighbors(Point p) {
		Collection<Point> c = this.getNeighbors(p);
		int ret = c.size();
		for (Point p2 : c) {
			if (unGriddedPoints.contains(p2))
				ret--;
		}
		if (p.x == 0)
			ret++;
		if (p.y == 0)
			ret++;
		if (p.x == edgeLength-1)
			ret++;
		if (p.y == edgeLength-1)
			ret++;
		return ret;
	}

	private Collection<Point> getNeighbors(Point p) {
		Collection<Point> c = new ArrayList();
		if (p.x > 0)
			c.add(new Point(p.x-1, p.y));
		if (p.x < edgeLength-1)
			c.add(new Point(p.x+1, p.y));
		if (p.y > 0)
			c.add(new Point(p.x, p.y-1));
		if (p.y < edgeLength-1)
			c.add(new Point(p.x, p.y+1));
		return c;
	}

	private Point getCandidateNextPoint(Subgrid sg, Random rand) {
		HashSet<Point> set = new HashSet();
		for (Point p : sg.slots.keySet()) {
			set.addAll(this.getNeighbors(p));
		}
		set.retainAll(unGriddedPoints);
		if (set.isEmpty()) {
			//ReikaJavaLibrary.pConsole(sg+" is trapped!");
			return null;
		}
		WeightedRandom<Point> wr = new WeightedRandom();
		for (Point p : set) {
			HashSet<Point> points = new HashSet(set);
			points.add(p);
			wr.addEntry(p, 100-100*this.calcStringiness(points));
		}
		while (!set.isEmpty()) {
			Point p = wr.getRandomEntry();
			if (this.canExpandInto(sg, p)) {
				return p;
			}
			else {
				set.remove(p);
				wr.remove(p);
			}
		}
		return null;
	}

	private boolean canExpandInto(Subgrid sg, Point p) { //determines whether the cell splits the remaining cells into 2+ isolated regions
		//ReikaJavaLibrary.pConsole("Checking canExpandInto for point "+p+" in "+sg);
		Collection<Point> c = this.getNeighbors(p);
		c.retainAll(unGriddedPoints);
		if (c.isEmpty()) {
			//ReikaJavaLibrary.pConsole("Returned true; unGridded neighbors was empty.");
			return true;
		}
		HashSet<Point> visited = new HashSet();
		visited.add(p);
		ArrayList<LinkedList<Point>> groups = new ArrayList();
		for (Point p2 : unGriddedPoints) {
			if (visited.contains(p2))
				continue;
			LinkedList<Point> li = new LinkedList();
			LinkedList<Point> li2 = new LinkedList();
			groups.add(li);
			li2.add(p2);
			while (!li2.isEmpty()) {
				Point p3 = li2.removeLast();
				if (visited.contains(p3))
					continue;
				visited.add(p3);
				li.add(p3);
				for (Point np3 : this.getNeighbors(p3)) {
					if (!visited.contains(np3) && unGriddedPoints.contains(np3)) {
						li2.add(np3);
					}
				}
			}
		}
		if (groups.size() == 1) {
			//ReikaJavaLibrary.pConsole("Returned true; no split detected.");
			return true;
		}
		else {
			Collections.sort(groups, new Comparator<LinkedList>() {

				@Override
				public int compare(LinkedList o1, LinkedList o2) {
					return Integer.compare(o1.size(), o2.size());
				}

			});
			//ReikaJavaLibrary.pConsole("Split detected; group list of size "+groups.size()+": "+groups);
			groups.remove(groups.size()-1);
			ArrayList<Point> merge = new ArrayList();
			for (LinkedList<Point> li : groups) {
				merge.addAll(li);
			}
			if (cellsPerSubgrid-sg.size() > merge.size()) {
				for (Point in : merge)
					sg.createSlot(in.x, in.y);
				//ReikaJavaLibrary.pConsole("Returned true; could merge");
				return true;
			}
			else {
				//ReikaJavaLibrary.pConsole("Returned false; not mergable");
				return false;
			}
		}
	}

	private double calcStringiness(Set<Point> points) {
		int stringy = 0;
		for (Point p : points) {
			Collection<Point> c = this.getNeighbors(p);
			c.retainAll(points);
			if (c.size() <= 2) {
				stringy++;
			}
		}
		return stringy/(double)points.size();
	}

	private void createNewSlot(GridSlot gs, Subgrid g) {
		uncaged.add(gs);
		grids.put(gs.positionKey(), g);
		unGriddedPoints.remove(gs.positionKey());
	}

	private void randomize(PuzzleProfile p, Random rand) {
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
		if (p.allowCaging) {
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
			if (!cages.isEmpty()) {
				GridCage cage = ReikaJavaLibrary.getRandomCollectionEntry(rand, cages.values());
				GridSlot gs = ReikaJavaLibrary.getRandomCollectionEntry(rand, cage.slots);
				gs.appearsAtStart = true; //force at least one cage to have at least one crystal per puzzle
			}
		}
		if (p.markIntersections) {
			HashSet<Point> set = new HashSet(grids.keySet());
			while (!set.isEmpty() && mixPoints.size() < gridSize) {
				Point mix = ReikaJavaLibrary.getRandomCollectionEntry(rand, set);
				set.remove(mix);
				CrystalMix e = this.getValidMix(mix, rand);
				if (e != null) {
					mixPoints.put(mix, e);
				}
			}
		}
		int n = (int)(initialFillFraction*totalCellCount);
		while (n > 0 && !candidateStarts.isEmpty()) {
			GridSlot gs = ReikaJavaLibrary.getRandomCollectionEntry(rand, candidateStarts);
			candidateStarts.remove(gs);
			gs.appearsAtStart = true;
			n--;
		}
	}

	private CrystalMix getValidMix(Point p, Random rand) {
		if (true)
			return null;
		//Point p1 = new Point(mix.x, mix.y);
		//Point p2 = new Point(mix.x, mix.y);
		//for (int i = 0; i < edgeLength; i++) {
		//	for (int k = 0; k < edgeLength; k++) {
		GridSlot at = this.getAt(p.x, p.y);
		if (at.color == null || at.isBlocked)
			return null;
		GridSlot g1 = this.getRandomSlotExcluding(rand, p);//this.getAt(p.x, i);
		GridSlot g2 = this.getRandomSlotExcluding(rand, p, g1.positionKey());//this.getAt(i, p.y);
		int n = 0;
		CrystalMix mix = this.getValidMix(p, at.color, g1, g2);
		while (mix == null && n < 4) {
			g1 = this.getRandomSlotExcluding(rand, p);
			g2 = this.getRandomSlotExcluding(rand, p, g1.positionKey());
			mix = this.getValidMix(p, at.color, g1, g2);
			n++;
		}
		//	}
		//}
		return mix;
	}


	private GridSlot getRandomSlotExcluding(Random rand, Point... pts) {
		Point p = ReikaJavaLibrary.getRandomCollectionEntry(rand, grids.keySet());
		while (ReikaArrayHelper.contains(pts, p))
			p = ReikaJavaLibrary.getRandomCollectionEntry(rand, grids.keySet());
		return this.getAt(p.x, p.y);
	}

	private CrystalMix getValidMix(Point p, CrystalElement base, GridSlot g1, GridSlot g2) {
		if (g1.color == null || g2.color == null)
			return null;
		CrystalElement e = g1.color.mixWith(g2.color);
		return e == base ? new CrystalMix(e, p, g1, g2) : null;
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
		set.removeAll(gs.parent.presentColors.keySet());
		int x = gs.xPos;
		int z = gs.zPos;
		for (int p = 0; p < edgeLength; p++) {
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
			gs.isGoal = false;//rand.nextInt(20) == 0 && gs.color != CrystalElement.BROWN;
			//gs.appearsAtStart = !gs.isGoal && rand.nextFloat() < initialFillFraction;
			if (!gs.isGoal) {
				candidateStarts.add(gs);
			}
		}
		return true;
	}

	private GridSlot getAt(int x, int z) {
		if (x < 0 || z < 0 || x >= edgeLength || z >= edgeLength)
			return null;
		return grids.get(new Point(x, z)).slots.get(new Point(x, z));
	}

	public void updateDoors(World world) {
		for (Coordinate c : doors) {
			BlockChromaDoor.setOpen(world, c.xCoord, c.yCoord, c.zCoord, isComplete);
		}
	}

	public void addCrystal(World world, CrystalElement e, int x, int z) {
		GridSlot gs = this.getAt(x, z);
		if (gs == null)
			return;
		gs.setCrystal(world, e);
		this.ping(world, x, z);
		isComplete = this.isValid(world);
	}

	public void removeCrystal(World world, int x, int z) {
		GridSlot gs = this.getAt(x, z);
		if (gs == null)
			return;
		gs.setCrystal(world, null);
		//isComplete = false;
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
			for (int p = 0; p < edgeLength; p++) {
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

	public void tick(World world) {
		for (CrystalMix mix : mixPoints.values()) {
			mix.sendParticles(world, generatorOrigin.xCoord, generatorOrigin.yCoord+2, generatorOrigin.zCoord);
		}
	}

	public boolean allowsCrystalAt(World world, int x, int z, CrystalElement e) {
		if (!parent.isChunkGenerated(x, z))
			return true;
		int dx = x-generatorOrigin.xCoord;
		int dz = z-generatorOrigin.zCoord;
		GridSlot gs = this.getAt(dx, dz);
		return gs.parent.countColorPresence(world, e) == 0 && !this.rowOrColHas(dx, dz, e);
	}

	private boolean rowOrColHas(int x, int z, CrystalElement e) {
		for (int i = 0; i < edgeLength; i++) {
			GridSlot g1 = this.getAt(x, i);
			GridSlot g2 = this.getAt(i, z);
			if (g1 != null && g1.currentCrystal != null && g1.currentCrystal == e) {
				return true;
			}
			if (g2 != null && g2.currentCrystal != null && g2.currentCrystal == e) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		generatorOrigin = new Coordinate(x, y, z);

		float midX = x+edgeLength/2F;
		float midZ = z+edgeLength/2F;

		int h = 8;
		int min1 = -PADDING_LOWER-1;
		int max1 = edgeLength+PADDING_LOWER;
		int min2 = -PADDING_LOWER-PADDING_UPPER-1;
		int max2 = edgeLength+PADDING_LOWER+PADDING_UPPER;
		if (!RayBlendGenerator.DEBUG) {
			for (int i = min2; i < max2; i++) {
				for (int k = min2; k < max2; k++) {
					int dx = x+i;
					int dz = z+k;
					for (int d = 0; d < h; d++) {
						world.setBlock(dx, y+d, dz, Blocks.air);
					}
				}
			}
			for (int i = min1; i < max1; i++) {
				for (int k = min1; k < max1; k++) {
					int dx = x+i;
					int dz = z+k;
					int m = BlockType.CLOAK.metadata;
					if (i >= -1 && i <= edgeLength && k >= -1 && k <= edgeLength) {
						m = BlockType.STONE.metadata;
					}
					world.setBlock(dx, y-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(dx, y+1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
				}

				for (int d = 0; d < STEP_HEIGHT; d++) {
					world.setBlock(x+min1, y+1+d, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+max1, y+1+d, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i, y+1+d, z+min1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i, y+1+d, z+max1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
			}
		}

		if (!RayBlendGenerator.DEBUG) {
			for (int i = min2; i < max2; i++) {
				for (int k = min2; k < max2; k++) {
					int dx = x+i;
					int dz = z+k;
					if (i <= min1 || i >= max1 || k <= min1 || k >= max1) {
						int m = BlockType.STONE.metadata;
						if (i > min2+1 && i < max2-1 && k > min2+1 && k < max2-1) {
							if (i > max1 || i < min1 || k > max1 || k < min1) {
								m = BlockType.CLOAK.metadata;
							}
						}
						boolean edgeI = i == min1 || i == max1 || i == max2-1 || i == min2+1;
						boolean edgeK = k == min1 || k == max1 || k == max2-1 || k == min2+1;
						if ((edgeI && edgeK) || (dx == MathHelper.floor_float(midX) && edgeK) || (dz == MathHelper.floor_float(midZ) && edgeI) && m == BlockType.STONE.metadata)
							m = BlockType.LIGHT.metadata;
						world.setBlock(dx, y+1+STEP_HEIGHT, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), m);
					}
					world.setBlock(dx, y+h-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}

				for (int d = 0; d < h; d++) {
					world.setBlock(x+min2, y-1+d, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+max2, y-1+d, z+i, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i, y-1+d, z+min2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					world.setBlock(x+i+1, y-1+d, z+max2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
			}
		}

		for (int i = 0; i < edgeLength; i++) {
			for (int k = 0; k < edgeLength; k++) {
				int dx = x+i;
				int dz = z+k;
				boolean light = (i/gridSize+k/gridSize)%2 == 0;
				if (!RayBlendGenerator.DEBUG)
					world.setBlock(dx, y, dz, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), light ? 1 : 0);
				GridSlot gs = this.getAt(i, k);
				if (RayBlendGenerator.DEBUG) {
					world.setTileEntity(dx, y+1, dz, ChromaBlocks.RAYBLEND.getBlockInstance(), 0, new RayblendFloorCallback(parent.id, ID, gs.parent.ID, gs.xPos, gs.zPos));
				}
				else {
					if (gs.isBlocked) {
						world.setBlock(dx, y, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
						world.setBlock(dx, y+1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
						world.setBlock(dx, y+2, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.GLASS.metadata);
					}
					else if (gs.color != null) {
						if (gs.appearsAtStart || GENERATE_SOLVED)
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
		}
		/*
		for (GridCage g : cages.values()) {
			for (GridSlot gs : g.slots) {
				world.setBlock(gs.getWorldX(), y+1, gs.getWorldZ(), ChromaBlocks.GLASS.getBlockInstance(), g.blendedColor.ordinal());
			}
		}*/

		generationBounds = generationBounds.addCoordinate(x+min2, y-1, z+min2);
		generationBounds = generationBounds.addCoordinate(x+max2, y+h, z+max2);
	}

	public BlockBox getGenerationBounds() {
		return generationBounds;
	}

	public boolean isValid(World world) {
		for (Subgrid g : grids.values()) {
			if (!g.isValid(world))
				return false;
		}
		for (Entry<Point, CrystalMix> en : mixPoints.entrySet()) {
			Point p = en.getKey();
			GridSlot gs = this.getAt(p.x, p.y);
			if (gs.currentCrystal != en.getValue().color)
				return false;
		}
		return true;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public boolean containsCrystalPosition(int x, int y, int z) {
		/*
		boolean xr = x >= generatorOrigin.xCoord && x < generatorOrigin.xCoord+edgeLength;
		boolean zr = z >= generatorOrigin.zCoord && z < generatorOrigin.zCoord+edgeLength;
		return xr && zr && y == generatorOrigin.yCoord+1;
		 */
		return y == generatorOrigin.yCoord+2 && this.getAt(x-generatorOrigin.xCoord, z-generatorOrigin.zCoord) != null;
	}

	public OverlayColor getCageColor(int x, int z) {
		if (RayBlendGenerator.DEBUG) {
			int idx = this.getAt(x, z).parent.index;
			float hue = idx/(float)(totalCellCount/cellsPerSubgrid);
			return new IntOverlayColor(Color.HSBtoRGB(hue, 1, 1));
		}
		else {
			GridCage gc = cages.get(new Point(x, z));
			return gc != null ? gc.blendedColor : null;
		}
	}

	private static class Subgrid implements CloneCallback<Point> {

		private final UUID ID = UUID.randomUUID();
		private final RayBlendPuzzle parent;
		private final HashMap<Point, GridSlot> slots = new HashMap();
		private final EnumMap<CrystalElement, HashSet<Point>> presentColors = new EnumMap(CrystalElement.class);

		private long hash;

		private final HashSet<GridSlot> unpopulated = new HashSet();

		private final int index;

		private static int CURRENTINDEX = 0;

		private Subgrid(RayBlendPuzzle p) {
			parent = p;
			parent.unfinished.add(this);
			index = CURRENTINDEX;
			CURRENTINDEX++;

			hash = this.calculateHash();
		}

		public boolean isCongruent(Subgrid g) {
			if (g == this)
				return true;
			if (g.slots.size() != slots.size())
				return false;
			Collection<Point> set1 = new ArrayList(ReikaJavaLibrary.cloneCollectionObjects(slots.keySet(), this));
			Collection<Point> set2 = new ArrayList(ReikaJavaLibrary.cloneCollectionObjects(g.slots.keySet(), this));
			this.normalizePoints(set1);
			this.normalizePoints(set2);
			if (ReikaJavaLibrary.collectionsHaveSameValues(set1, set2))
				return true;
			for (int i = 0; i < 3; i++) {
				this.rotatePoints(set2);
				this.normalizePoints(set2);
				if (ReikaJavaLibrary.collectionsHaveSameValues(set1, set2))
					return true;
			}
			return false;
		}

		private void rotatePoints(Collection<Point> set) {
			for (Point p : set) {
				Vec3 r = ReikaVectorHelper.rotateVector(Vec3.createVectorHelper(p.x, 0, p.y), 0, 90, 0);
				p.x = MathHelper.floor_double(r.xCoord);
				p.y = MathHelper.floor_double(r.zCoord);
			}
		}

		private void normalizePoints(Collection<Point> set) {
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			for (Point p : set) {
				minX = Math.min(minX, p.x);
				minY = Math.min(minY, p.y);
			}
			for (Point p : set) {
				p.x -= minX;
				p.y -= minY;
			}
		}

		public int countColorPresence(World world, CrystalElement e) {
			return this.getOrCreateSet(world, e).size();
		}

		private void addColorPresence(World world, CrystalElement e, GridSlot c) {
			this.getOrCreateSet(world, e).add(c.positionKey());
		}

		private void removeColorPresence(World world, CrystalElement e, GridSlot c) {
			this.getOrCreateSet(world, e).remove(c.positionKey());
		}

		private HashSet<Point> getOrCreateSet(World world, CrystalElement e) {
			HashSet<Point> set = presentColors.get(e);
			if (set == null) {
				set = new HashSet();
				presentColors.put(e, set);
			}
			else {
				Iterator<Point> it = set.iterator();
				while (it.hasNext()) {
					GridSlot gs = slots.get(it.next());
					int x = gs.getWorldX();
					int z = gs.getWorldZ();
					Block b = world.getBlock(x, parent.generatorOrigin.yCoord+2, z);
					if (!(b instanceof CrystalTypeBlock)) {
						it.remove();
					}
					else if (world.getBlockMetadata(x, parent.generatorOrigin.yCoord+2, z) != e.ordinal()) {
						it.remove();
					}
				}
			}
			return set;
		}

		public double getStringiness() {
			return parent.calcStringiness(slots.keySet());
		}

		public void clear() {
			slots.clear();
			presentColors.clear();
			unpopulated.clear();

			hash = this.calculateHash();
		}

		public int size() {
			return slots.size();
		}

		private void createSlot(int x, int z) {
			GridSlot gs = new GridSlot(this, x, z);
			slots.put(gs.positionKey(), gs);
			unpopulated.add(gs);
			parent.createNewSlot(gs, this);

			hash = this.calculateHash();
		}

		private long calculateHash() {
			long ret = 0;
			for (GridSlot gs : slots.values()) {
				ret += gs.xPos+1000000L*gs.zPos;
			}
			return ret;
		}

		public boolean isValid(World world) {
			for (GridSlot gs : slots.values()) {
				if (!gs.isValid(world))
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

		public int getIndex() {
			return index;
		}

		@Override
		public String toString() {
			return "Subgrid #"+index+" of size "+this.size();
		}

		@Override
		public Point clone(Point o) {
			return new Point(o.x, o.y);
		}

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

		private void setCrystal(World world, CrystalElement e) {
			if (e != null)
				parent.addColorPresence(world, e, this);
			else if (currentCrystal != null)
				parent.removeColorPresence(world, currentCrystal, this);
			currentCrystal = e;
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

		public boolean isValid(World world) {
			/*
			return isBlocked ? true : currentCrystal == color;
			 */
			if (isBlocked)
				return true;
			if (currentCrystal == null)
				return false;
			if (parent.countColorPresence(world, currentCrystal) > 1)
				return false;
			boolean flag = true;
			CrystalElement e = currentCrystal;
			currentCrystal = null;
			boolean row = parent.parent.rowOrColHas(xPos, zPos, e);
			currentCrystal = e;
			return !row;
		}

		public int getWorldX() {
			return parent.parent.generatorOrigin.xCoord+xPos;
		}

		public int getWorldZ() {
			return parent.parent.generatorOrigin.zCoord+zPos;
		}

		@Override
		public String toString() {
			return "["+xPos+", "+zPos+"]; "+color+"/"+currentCrystal+"; "+isBlocked+"/"+isGoal+"/"+appearsAtStart;
		}

	}

	public static class CrystalMix {

		private final CrystalElement color;
		private final Point position;
		private final GridSlot pos1;
		private final GridSlot pos2;

		public CrystalMix(CrystalElement e, Point p, GridSlot g1, GridSlot g2) {
			color = e;
			position = p;
			pos1 = g1;
			pos2 = g2;
		}

		public void sendParticles(World world, int x0, int y0, int z0) {
			if (world.rand.nextInt(5) == 0)
				ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.RAYBLENDMIX.ordinal(), world, position.x+x0+0.5, y0+0.5, position.y+z0+0.5, new PacketTarget.RadiusTarget(world, x0, y0, z0, 32), color.ordinal(), 1);
			if (world.rand.nextInt(5) == 0)
				ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.RAYBLENDMIX.ordinal(), world, pos1.xPos+x0+0.5, y0+0.5, pos1.zPos+z0+0.5, new PacketTarget.RadiusTarget(world, x0, y0, z0, 32), color.ordinal(), 0);
			if (world.rand.nextInt(5) == 0)
				ReikaPacketHelper.sendPositionPacket(ChromatiCraft.packetChannel, ChromaPackets.RAYBLENDMIX.ordinal(), world, pos2.xPos+x0+0.5, y0+0.5, pos2.zPos+z0+0.5, new PacketTarget.RadiusTarget(world, x0, y0, z0, 32), color.ordinal(), 0);
			//this.doParticle(world, position.x+x0+0.5, y0+0.5, position.y+z0+0.5, color, true);
			//this.doParticle(world, pos1.xPos+x0+0.5, y0+0.5, pos1.zPos+z0+0.5, pos1.color, false);
			//this.doParticle(world, pos2.xPos+x0+0.5, y0+0.5, pos2.zPos+z0+0.5, pos2.color, false);
		}

		@SideOnly(Side.CLIENT)
		public static void doParticle(World world, double x, double y, double z, CrystalElement e, boolean mix) {
			if (mix) {
				EntityBlurFX fx = new EntityBlurFX(world, x, y, z);
				fx.setColor(e.getColor()).setScale(3).setLife(60).setIcon(ChromaIcons.FADE).setRapidExpand().setAlphaFading();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			else {
				EntityBlurFX fx = new EntityBlurFX(world, x, y, z);
				fx.setColor(e.getColor()).setScale(2).setLife(60).setIcon(ChromaIcons.FADE).setRapidExpand().setAlphaFading();
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
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