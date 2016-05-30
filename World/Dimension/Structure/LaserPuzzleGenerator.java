/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.LaserEffectTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.Laser.LaserEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.Laser.LaserLevel;
import Reika.ChromatiCraft.World.Dimension.Structure.Laser.LaserLoot;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class LaserPuzzleGenerator extends DimensionStructureGenerator {

	private static final ArrayList<String> order = new ArrayList();

	private final HashMap<String, LaserPuzzleStatus> rooms = new HashMap();

	static {
		int d = ChromaOptions.getStructureDifficulty();
		order.add("mirrortut");
		order.add("mirrors2");
		order.add("refractortut");
		order.add("splittertut");
		if (d > 1) {
			order.add("filtertut");
			order.add("polartut");
			order.add("polar2");
			order.add("oneway");
		}
		order.add("prismtut1");
		order.add("prismtut2");
		if (d > 2) {
			order.add("prism3");
			order.add("complex");
		}
	}

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		int x = chunkX+13;
		int z = chunkZ;
		int y = 10+rand.nextInt(70);
		posY = y;
		for (String s : order) {
			LaserLevel l = new LaserLevel(this, s);
			rooms.put(s, new LaserPuzzleStatus(l));
			l.generate(world, x, y, z);

			int dx = x+l.getFullLengthX();
			int dx2 = dx+8;
			if (!s.equals("complex")) {
				for (int ddx = dx; ddx < dx2; ddx++) {
					//ReikaJavaLibrary.pConsole("Generating a tunnel segment @ "+ddx);
					for (int dy = y+2; dy <= y+7; dy++) {
						int w = dy >= y+6 ? 2 : 3;
						for (int d = -w; d <= w; d++) {
							int dz = z+d;
							boolean wall = dy == y+2 || dy == y+7 || Math.abs(d) == w;
							if (wall) {
								world.setBlock(ddx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
							}
							else {
								world.setBlock(ddx, dy, dz, Blocks.air);
							}
						}
					}
				}
			}
			else {
				new LaserLoot(this).generate(world, dx, y, z);
			}
			x = dx2+7;
		}

		this.addDynamicStructure(new LaserEntrance(this), chunkX, chunkZ);
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
		for (LaserPuzzleStatus l : rooms.values()) {
			if (!l.puzzle.isSolved) {
				return false;
			}
		}
		return true;
	}

	@Override
	public StructureData createDataStorage() {
		return new LaserPuzzleData(this);
	}

	@Override
	protected void clearCaches() {
		rooms.clear();
	}

	public void addEmitter(String name, Coordinate c) {
		rooms.get(name).emitters.add(c);
	}

	public void addTarget(String name, Coordinate c) {
		rooms.get(name).targets.add(c);
	}

	private static class LaserPuzzleStatus {

		private final LaserLevel puzzle;
		private final HashSet<Coordinate> emitters = new HashSet();
		private final HashSet<Coordinate> targets = new HashSet();
		private final HashSet<Coordinate> activeTargets = new HashSet();
		private boolean laserStatus = false;

		private LaserPuzzleStatus(LaserLevel p) {
			puzzle = p;
		}

		private void checkCompletion(World world) {
			puzzle.isSolved = targets.equals(activeTargets);
			//ReikaJavaLibrary.pConsole(puzzle.getName()+":  "+puzzle.isSolved+" from "+activeTargets.size()+":"+activeTargets+" & "+targets.size()+":"+targets);
			puzzle.applyDoorState(world);
			if (puzzle.isSolved) {
				AxisAlignedBB box = puzzle.getBounds().asAABB();
				List<EntityLaserPulse> li = world.getEntitiesWithinAABB(EntityLaserPulse.class, box);
				for (EntityLaserPulse e : li) {
					e.kill();
				}
			}
		}
	}

	private static class LaserPuzzleData extends StructureData {

		private String level;
		private long lastClickTime = -1;

		private LaserPuzzleData(LaserPuzzleGenerator g) {
			super(g);
		}

		@Override
		public void load(HashMap<String, Object> map) {
			level = (String)map.get("level");
		}

		@Override
		public void onInteract(World world, int x, int y, int z, EntityPlayer ep, int s, HashMap<String, Object> extraData) {
			if (level.equals(extraData.get("level"))) {
				long time = world.getTotalWorldTime();
				if (time-lastClickTime >= 5) {
					lastClickTime = time;
					LaserPuzzleGenerator gen = (LaserPuzzleGenerator)generator;
					LaserPuzzleStatus l = gen.rooms.get(level);
					l.activeTargets.clear();
					l.puzzle.isSolved = false;
					boolean on = gen.areLasersInPlay(level);
					for (Coordinate c : l.targets) {
						TileEntity te = c.getTileEntity(world);
						if (te instanceof TargetTile) {
							((TargetTile)te).trigger(false, false, on);
						}
					}
					if (on) {
						l.laserStatus = false;
						AxisAlignedBB box = l.puzzle.getBounds().asAABB();
						List<EntityLaserPulse> li = world.getEntitiesWithinAABB(EntityLaserPulse.class, box);
						for (EntityLaserPulse e : li) {
							e.kill();
						}
					}
					else {
						for (Coordinate c : l.emitters) {
							TileEntity te = c.getTileEntity(world);
							if (te instanceof LaserEffectTile) {
								((LaserEffectTile)te).fire();
							}
						}
						l.laserStatus = true;
					}
				}
			}
		}
	}

	public boolean areLasersInPlay(String level) {
		return rooms.get(level).laserStatus;
	}

	public void completeTrigger(String level, World world, Coordinate c, boolean complete) {
		LaserPuzzleStatus l = rooms.get(level);
		if (complete) {
			l.activeTargets.add(c);
		}
		else {
			l.activeTargets.remove(c);
		}
		l.checkCompletion(world);
	}

}
