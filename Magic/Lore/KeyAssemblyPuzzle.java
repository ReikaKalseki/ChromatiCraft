/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.HexGrid;
import Reika.DragonAPI.Instantiable.HexGrid.MapShape;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;




public class KeyAssemblyPuzzle {

	private static final int SIZE = 9;
	private static final int EDGE = (SIZE-1)/2;

	public static final int CELL_SIZE = 64;

	private static final double SIN60 = Math.sin(Math.toRadians(60));
	private static final double COS60 = Math.cos(Math.toRadians(60));

	//private final HexCell[][] grid = new HexCell[SIZE][SIZE];
	private final HexGrid grid;

	private int activeCells;
	private final int totalCells;

	private KeyAssemblyPuzzle() {
		grid = new HexGrid(SIZE, 64, true, MapShape.HEXAGON).flower();
		totalCells = grid.cellCount();
	}

	public static KeyAssemblyPuzzle generatePuzzle(long seed, UUID player) {
		KeyAssemblyPuzzle p = new KeyAssemblyPuzzle();
		long seed2 = ReikaMathLibrary.cantorCombine(seed, player.getMostSignificantBits(), player.getLeastSignificantBits());
		p.generate(seed2);
		return p;
	}

	private void generate(long seed) {
		//TODO
	}

	public boolean isComplete() {
		return activeCells == totalCells;
	}

	public boolean isCellInGrid(int i, int k) {
		return Math.abs(-i+k) < EDGE;
	}

	public HexCell getCell(int i, int k) {
		return grid[i][k];
	}

	private HexCell getCell(HexCell loc) {
		return this.getCell(-loc.xCoord, loc.zCoord);
	}

	private void setCellContents(HexCell loc, HexTile h) {
		this.getCell(loc).occupant = h;
	}

	public HexCell getNeighbor(HexCell loc, int i, int k) {
		int dx = -loc.xCoord+i;
		int dz = loc.zCoord+k;
		return this.isCellInGrid(dx, dz) ? this.getCell(dx, dz) : null;
	}

	private Collection<HexCell> getNeighbors(HexCell loc) {
		Collection<HexCell> c = new HashSet();
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				if (i != k && (i != 0 || k != 0)) {
					int dx = -loc.xCoord+i;
					int dz = loc.zCoord+k;
					if (this.isCellInGrid(dx, dz)) {
						c.add(this.getCell(dx, dz));
					}
				}
			}
		}
		return c;
	}

	public HexCell getCellFromScreenXY(int x, int y) {
		//easier than mathematical solution
		double mind = Double.POSITIVE_INFINITY;
		HexCell closest = null;
		for (int i = 0; i < SIZE; i++) {
			for (int k = 0; k < SIZE; k++) {
				if (this.isCellInGrid(i, k)) {
					HexCell c = this.getCell(i, k);
					double d = c.getScreenDistanceTo(x, y);
					if (closest == null || d < mind) {
						mind = d;
						closest = c;
					}
				}
			}
		}
		return closest;
	}

	private static class HexCell {

		private final int xCoord; //always negative!
		private final int zCoord;

		private HexTile occupant = null;

		private HexCell(int x, int z) {
			xCoord = x;
			zCoord = z;
		}

		public HexTile getOccupant() {
			return occupant;
		}

		public void render(Tessellator v5) {
			if (occupant != null) {
				occupant.render(v5);
			}
			else {
				//render void //TODO
			}
		}

		public Point getScreenXY() {
			int x = (int)(zCoord*CELL_SIZE*SIN60);
			int y = (int)((-xCoord-zCoord*COS60)*CELL_SIZE);
			return new Point(x, y);
		}

		public double getScreenDistanceTo(int x, int y) {
			Point p = this.getScreenXY();
			return p.distance(x, y);
		}

	}

	private static class HexTile {


		private final CrystalElement color;

		private HexCell location;
		private boolean state;

		private HexTile(CrystalElement e) {
			color = e;
		}

		private boolean isValid(KeyAssemblyPuzzle p) {
			for (HexCell c : p.getNeighbors(location)) {
				if (c.occupant != null) {
					if (this.isRelatedTo(c.occupant))
						return true;
				}
			}
			return false;
		}

		private boolean isRelatedTo(HexTile h) {
			return ElementMixer.instance.related(color, h.color);
		}

		public boolean playerKnows(EntityPlayer ep) {
			return true;
		}

		@SideOnly(Side.CLIENT)
		public void render(Tessellator v5) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (this.playerKnows(ep)) {
				//TODO
			}
			else {
				//TODO
			}
		}

		public void move(KeyAssemblyPuzzle p, int dx, int dz) {
			p.setCellContents(location, null);
			location = p.getNeighbor(location, dx, dz);
			p.setCellContents(location, this);
			boolean flag = this.isValid(p);
			if (flag != state) {
				if (flag) {
					p.activeCells++;
				}
				else {
					p.activeCells--;
				}
				state = flag;
			}
		}
	}
}
