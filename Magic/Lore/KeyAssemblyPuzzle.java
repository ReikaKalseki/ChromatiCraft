/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Lore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Magic.ElementMixer;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.ResettableRandom;
import Reika.DragonAPI.Instantiable.Data.ExtremaFinder;
import Reika.DragonAPI.Instantiable.Math.HexGrid;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Hex;
import Reika.DragonAPI.Instantiable.Math.HexGrid.MapShape;
import Reika.DragonAPI.Instantiable.Math.HexGrid.Point;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;




public class KeyAssemblyPuzzle {

	private static final int SIZE = 15;//9;
	//private static final int EDGE = (SIZE-1)/2;
	public static final int CELL_SIZE = 15;//30;

	private static final int BASE_SIZE = new HexGrid(SIZE, CELL_SIZE, true, MapShape.HEXAGON).flower().cellCount();

	public static final int TOWER_COUNT = 13; //arranged in a circle http://i.imgur.com/M8cC88i.png
	public static final int GROUPS_PER_TOWER = 3;
	public static final int GROUP_SIZE = 4;

	private static final int REQUIRED_CELLS = TOWER_COUNT*GROUPS_PER_TOWER*GROUP_SIZE;

	//public static final int TOTAL_CELLS = ReikaMathLibrary.roundDownToX(TOWER_COUNT, BASE_SIZE);
	private static final int VOID_COUNT = BASE_SIZE-REQUIRED_CELLS; //for 15->169, =13 //for 91, = 11

	//public static final int GROUP_SIZE = TOTAL_CELLS/TOWER_COUNT;

	private static final double SIN60 = Math.sin(Math.toRadians(60D));

	private final HexGrid grid;
	private final HashMap<Hex, HexCell> cells = new HashMap();
	private final HashSet<Hex> emptyHexes = new HashSet();
	private final ArrayList<TileGroup> groups = new ArrayList();
	private final ArrayList<TileGroup> freeGroups = new ArrayList();

	private int activeCells;

	private static final String hexPNG = "Textures/colorhexes.png";
	private static final String voidPNG = "Textures/colorhexes-voids.png";

	private final ResettableRandom rand = new ResettableRandom();
	private long seed;

	private KeyAssemblyPuzzle() {
		grid = new HexGrid(SIZE, CELL_SIZE, true, MapShape.HEXAGON).flower();
		for (Hex h : grid.getAllHexes()) {
			cells.put(h, new HexCell(h));
		}
	}

	public static KeyAssemblyPuzzle generatePuzzle(EntityPlayer ep) {
		return generatePuzzle(ep, calcSeed(ep));
	}

	private static KeyAssemblyPuzzle generatePuzzle(EntityPlayer ep, long seed) {
		KeyAssemblyPuzzle p = new KeyAssemblyPuzzle();
		p.generate(ep, seed);
		return p;
	}

	public static long calcSeed(EntityPlayer ep) {
		long seed1 = ep.worldObj.getSeed();
		UUID seed0 = ep.getPersistentID();
		long seed2 = ReikaMathLibrary.cantorCombine(seed1, seed0.getMostSignificantBits(), seed0.getLeastSignificantBits());
		return seed2;
	}

	public long getSeed() {
		return seed;
	}

	private void generate(EntityPlayer ep, long seed) { //fill with valid board; delete some cells, shuffle
		this.seed = seed;
		rand.setSeed(seed);
		activeCells = 0;
		groups.clear();
		freeGroups.clear();
		LinkedList<Move> li = null;
		boolean flag = false;
		int attempts = 0;
		long time = System.currentTimeMillis();
		while (!flag) {
			this.fillGrid(rand);
			this.generateVoids(rand);
			li = this.shuffle(rand);
			flag = this.subdivide(rand);
			long dur = System.currentTimeMillis()-time;
			ChromatiCraft.logger.log("Attempted to generate lore puzzle; attempt #"+attempts+" took "+dur+" ms. Success: "+flag);
			if (time > 15000) {
				ChromatiCraft.logger.logError("Could not generate lore puzzle within 15s, even after "+attempts+" attempts!");
				this.errorGrid();
				return;
			}
		}
		if (LoreManager.instance.hasPlayerCompletedBoard(ep)) {
			Iterator<Move> it = li.descendingIterator();
			while (it.hasNext()) {
				Move m = it.next().invert(grid);
				this.getCell(m.hex).occupant.move(this, m.direction, null);
			}
		}
	}

	public Collection<TileGroup> getRandomGroupsForTower(Towers t) {
		rand.setSeed(seed ^ t.ordinal());
		ArrayList<TileGroup> ret = new ArrayList();
		for (int i = 0; i < GROUPS_PER_TOWER; i++) {
			int idx = rand.nextInt(freeGroups.size());
			TileGroup g = freeGroups.get(idx);
			ret.add(g);
			freeGroups.remove(idx);
			for (Hex h : g.hexes) {
				HexCell c = this.getCell(h);
				c.tower = t;
			}
		}
		return ret;
	}

	private void fillGrid(Random rand) {
		for (HexCell c : cells.values()) {
			c.occupant = new HexTile(this.generateColor(c.location, rand));
			this.setCellContents(c.location, c.occupant, null);
			c.occupant.update(this, true, null);
		}
	}

	private void errorGrid() {
		for (HexCell c : cells.values()) {
			c.occupant = new HexTile(rand.nextBoolean() ? CrystalElement.BLACK : CrystalElement.MAGENTA);
			this.setCellContents(c.location, c.occupant, null);
			c.occupant.update(this, true, null);
		}
	}

	private void generateVoids(Random rand) {
		ArrayList<HexCell> li = new ArrayList(cells.values());
		for (int i = 0; i < VOID_COUNT; i++) {
			HexCell c = li.get(rand.nextInt(li.size()));
			while (c.occupant == null/* || grid.isHexAtEdge(c.location)*/)
				c = li.get(rand.nextInt(li.size()));
			c.occupant = null;
			this.setCellContents(c.location, null, null);
			emptyHexes.add(c.location);
		}
	}

	private LinkedList<Move> shuffle(Random rand) {
		LinkedList<Move> moves = new LinkedList();
		for (int i = 0; i < 500; i++) {
			/*
			HexCell c = li.get(rand.nextInt(li.size()));
			if (c.occupant != null) {
				ArrayList<Integer> li2 = grid.getValidMovementDirections(c.location);
				c.occupant.move(this, li2.get(rand.nextInt(li2.size())));
			}
			 */
			Hex h = ReikaJavaLibrary.getRandomCollectionEntry(rand, emptyHexes);
			ArrayList<Integer> li = grid.getValidMovementDirections(h);
			int dir = li.get(rand.nextInt(li.size()));
			Hex h2 = h.getNeighbor(dir);
			while (!li.isEmpty() && this.getCell(h2).occupant == null) {
				li.remove(Integer.valueOf(dir));
				if (!li.isEmpty()) {
					dir = li.get(rand.nextInt(li.size()));
					h2 = h.getNeighbor(dir);
				}
			}
			if (!li.isEmpty()) {
				try {
					int dir2 = grid.getOppositeDirection(dir);
					if (this.getCell(h2).occupant.move(this, dir2, null))
						moves.add(new Move(h2, dir2));
				}
				catch (Exception e) {
					ReikaJavaLibrary.pConsole("Errored on shuffle "+i+": moving "+h2+" into "+h+", dir = "+grid.getOppositeDirection(dir)+", from "+dir+"; c="+this.getCell(h));
					//e.printStackTrace();
					ReikaJavaLibrary.pConsole(e.toString());
					break;
				}
			}

			if (i == 500 && this.isComplete())
				i = 0;
		}
		return moves;
	}

	private boolean subdivide(Random rand) {
		LinkedList<Hex> li = this.findHamiltonianPath(rand);
		if (li == null)
			return false;
		TileGroup g = new TileGroup();
		for (Hex h : li) {
			g.hexes.add(h);
			if (g.hexes.size() == GROUP_SIZE) {
				groups.add(g);
				g = new TileGroup();
			}
		}
		freeGroups.addAll(groups);
		return true;
	}

	private LinkedList<Hex> findHamiltonianPath(Random rand) {
		LinkedList<Hex> path = null;
		boolean flag = true;
		int attempts = 0;
		long time = System.currentTimeMillis();
		while (flag) {
			attempts++;
			Hex h = grid.getRandomEdgeCell(rand);
			while (this.getCell(h).occupant == null)
				h = grid.getRandomEdgeCell(rand);
			//ReikaJavaLibrary.pConsole("Starting path at "+h);
			path = new LinkedList();
			HashSet<Hex> pathCache = new HashSet();
			flag = !this.pathSearch(rand, h, path, pathCache);
			long dur = System.currentTimeMillis()-time;
			ChromatiCraft.logger.log("Attempted to path lore puzzle; attempt #"+attempts+" took "+dur+" ms. Success: "+flag);
			if (time > 15000) {
				ChromatiCraft.logger.logError("Could not path lore puzzle within 15s, even after "+attempts+" attempts!");
				return null;
			}
		}
		return path;
	}

	private boolean pathSearch(Random rand, Hex h, LinkedList<Hex> path, HashSet<Hex> pathCache) {
		path.add(h);
		pathCache.add(h);
		ExtremaFinder<Hex> ef = new ExtremaFinder();
		for (Hex n : h.getNeighbors()) {
			if (this.isValidPathCell(n, pathCache)) {
				double f = 0;
				ArrayList<Integer> dirs = grid.getValidMovementDirections(n);
				Iterator<Integer> it = dirs.iterator();
				while (it.hasNext()) {
					int dir = it.next();
					Hex n2 = n.getNeighbor(dir);
					if (!this.isValidPathCell(n2, pathCache) && !pathCache.contains(n2)) {
						it.remove();
					}
				}
				int val = 6-dirs.size();
				f += 1.4*val;
				for (Hex n2 : n.getNeighbors()) {
					if (grid.containsHex(n2)) {
						if (pathCache.contains(n2))
							f += 1;
					}
				}
				ef.addValue(n, f);
			}
		}
		Hex step = ef.getHighest();
		if (step != null) {
			return this.pathSearch(rand, step, path, pathCache);
		}
		else {
			if (path.size() == REQUIRED_CELLS) {
				return true;
			}
			else {
				return false; //retry
			}
		}
	}

	private boolean isValidPathCell(Hex h, HashSet<Hex> pathCache) {
		return grid.containsHex(h) && !pathCache.contains(h) && this.getCell(h).occupant != null && !grid.dividesGrid(h, ReikaJavaLibrary.combineCollections(pathCache, emptyHexes));
	}

	private CrystalElement generateColor(Hex h, Random rand) {
		if (rand.nextInt(32) == 0)
			return CrystalElement.BROWN; //always lit
		HashSet<CrystalElement> li = new HashSet();
		for (Hex h2 : h.getNeighbors()) {
			if (grid.containsHex(h2)) {
				HexCell c = this.getCell(h2);
				if (c.occupant != null) {
					Collection<CrystalElement> ce = ElementMixer.instance.getRelatedColors(c.occupant.color);
					if (ce != null)
						li.addAll(ce);
				}
			}
		}
		return li.isEmpty() ? CrystalElement.elements[rand.nextInt(16)] : new ArrayList<CrystalElement>(li).get(rand.nextInt(li.size()));
	}

	private void drawTexturedHex(Tessellator v5, Hex h, double sizeX, double sizeY, float f) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/lorestruct.png");

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);

		HexCell c = cells.get(h);
		Point p = this.getHexLocation(h);
		double fx = (p.x-CELL_SIZE/2D)/sizeX;
		double fy = (p.y-CELL_SIZE/2D)/sizeY;
		GL11.glPushMatrix();
		GL11.glTranslated(p.x, p.y, 0);
		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		//v5.setColorRGBA_I(ReikaColorAPI.mixColors(0xffffff, 0x000000, 1-f), 255);

		double cu = 0.5+fx;
		double cv = 0.5+fy;
		//this.drawTexturedHex(v5, h, 0.5+fx, 0.5+fy, 1D/size*0.625);

		double r = CELL_SIZE/2D;
		double x = r+0.1;
		double y = r-1;
		double u = cu+x/sizeX;
		double v = cv+y/sizeY;
		v5.addVertexWithUV(x, y, 0, u, v);

		for (double a = 0; a <= 360; a += 60) {
			double ang = Math.toRadians(a);
			double dx = r+0.1+r*Math.cos(ang);
			double dy = r-1+r*Math.sin(ang);

			u = cu+dx/sizeX;
			v = cv+dy/sizeY;
			v5.addVertexWithUV(dx, dy, 0, u, v);
		}

		v5.draw();

		if (f < 3) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			int clr = f >= 2 ? 0xffffff : ReikaColorAPI.mixColors(0xffffff, 0x000000, f-1);
			int ap = f < 2 ? 255 : (int)(255*(1-(f-2)));

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorRGBA_I(clr, ap);

			r = CELL_SIZE/2D;
			x = r+0.1;
			y = r-1;
			v5.addVertex(x, y, 0);

			for (double a = 0; a <= 360; a += 60) {
				double ang = Math.toRadians(a);
				double dx = r+0.1+r*Math.cos(ang);
				double dy = r-1+r*Math.sin(ang);

				v5.addVertex(dx, dy, 0);
			}

			v5.draw();
			GL11.glPopAttrib();
		}

		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	public void render(Tessellator v5, EntityPlayer ep, ScaledResolution res) {
		GL11.glPushMatrix();
		//GL11.glTranslated(SIZE*CELL_SIZE/2D, SIZE*CELL_SIZE/2D, 0);
		double w2 = res.getScaledWidth_double()/2;
		double h2 = res.getScaledHeight_double()/2;
		GL11.glTranslated(w2, h2, 0);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		boolean flag = LoreManager.instance.hasPlayerCompletedBoard(ep);
		boolean flag2 = flag;
		if (flag) {
			//this.drawTexturedGrid(v5);
			for (HexCell c : cells.values()) {
				if (c.getSolveFactor() < 1)
					flag2 = false;
			}
			if (flag2) {
				GL11.glTranslated(-w2, -h2, 0);
				LoreManager.instance.getOrCreateRosetta(ep).render(res, w2, h2);
			}
		}
		if (!flag2) {
			//else {
			for (HexCell c : cells.values()) {
				c.tickSolve();
				float f = flag ? c.getSolveFactor() : 0;

				/*
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 200);
			GL11.glScaled(0.5, 0.5, 0.5);
			int x = (int)grid.getHexLocation(c.location).x;
			int y = (int)grid.getHexLocation(c.location).y;
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(Minecraft.getMinecraft().fontRenderer, String.valueOf(f), x*2+16, y*2+8, 0xffffff);
			GL11.glPopMatrix();
				 */

				if (flag && f >= 1) {
					Hex h = c.location;
					/*
					Point pt = grid.getHexLocation(h);
					double u = 0.5+pt.x*0.03125;//CELL_SIZE/SIZE;
					double v = 0.5-pt.y*0.03125;//CELL_SIZE/SIZE;
					GL11.glPushMatrix();
					GL11.glTranslated(pt.x, pt.y, 0);
					grid.drawTexturedHex(v5, h, u, v, /*CELL_SIZE/1024D*//*0.03125);
					GL11.glPopMatrix();
					 */
					//this.drawTexturedHex(v5, h, grid.getGridProperties().sizeX, grid.getGridProperties().sizeY, f);
				}
				else {
					c.render(this, v5, true, 1-Math.min(1, f*1.0625F));
				}
			}
		}
		//}
		/*
		if (tempPath != null) {
			float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			GL11.glLineWidth(5);
			GL11.glPushMatrix();
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glTranslated(CELL_SIZE/2D, CELL_SIZE/2D, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			v5.startDrawing(GL11.GL_LINE_STRIP);
			v5.setColorOpaque_I(0x000000);
			for (Hex h : tempPath) {
				Point loc = grid.getHexLocation(h);
				v5.addVertex(loc.x, loc.y, 0);
			}
			v5.draw();

			GL11.glLineWidth(2);

			v5.startDrawing(GL11.GL_LINE_STRIP);
			v5.setColorOpaque_I(0xffff00);
			for (Hex h : tempPath) {
				Point loc = grid.getHexLocation(h);
				if (h == tempPath.getLast())
					v5.setColorOpaque_I(0x00ffff);
				v5.addVertex(loc.x, loc.y, 0);
				v5.setColorOpaque_I(0xffffff);
			}
			v5.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glLineWidth(w);
		}
		 */
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public Hex getOrigin() {
		return grid.getHex(0, 0, 0);
	}

	public boolean isComplete() {
		/*
		ReikaJavaLibrary.pConsole(activeCells+"/"+grid.cellCount());
		for (HexCell c : cells.values()) {
			if (c.occupant != null && !c.occupant.state) {
				ReikaJavaLibrary.pConsole(c);
			}
		}
		 */
		boolean flag = activeCells == grid.cellCount();//TOTAL_CELLS;
		if (flag) {
			for (HexCell c : cells.values()) {
				c.solveTick = 1;
			}
		}
		else {
			for (HexCell c : cells.values()) {
				c.solveTick = 0;
			}
		}
		return flag;
	}

	private void setCellContents(Hex loc, HexTile h, EntityPlayer ep) {
		HexCell c = this.getCell(loc);
		c.occupant = h;
		if (h != null) {
			h.location = c;
			h.update(this, true, ep);
		}
		else {
			for (Hex n : loc.getNeighbors()) {
				//ReikaJavaLibrary.pConsole(loc+" > "+n);
				HexCell c2 = this.getCell(n);
				if (c2 != null && c2.occupant != null) {
					//ReikaJavaLibrary.pConsole(loc+" > "+n+" > "+c2.occupant.color);
					c2.occupant.update(this, false, ep);
				}
			}
		}
	}

	public HexCell getCell(Hex loc) {
		return cells.get(loc);
	}

	public Hex getHexAt(int x, int y) {
		return grid.getHexAtLocation(x, y);
	}

	/*
	public void processClick(int x, int y) {
		Hex h = this.getHexAt(x, y);
		//ReikaJavaLibrary.pConsole(h);
		if (h != null) {
			HexCell c = this.getCell(h);
			if (c != null) {
				c.onClick(this, x, y);
			}
		}
	}
	 */
	public Point getHexLocation(Hex h) {
		return grid.getHexLocation(h);
	}

	public void flashUnknownHexes(EntityPlayer ep) {
		for (Hex h : cells.keySet()) {
			HexCell c = cells.get(h);
			if (c != null && !c.playerKnows(ep)) {
				c.flash();
			}
		}
	}

	public static class HexCell {

		private final Hex location;

		private HexTile occupant = null;
		private Towers tower = null;

		public boolean isHovered;
		private int flashTick = 0;
		private int solveTick = 0;
		private final int solveOffset;

		private HexCell(Hex h) {
			location = h;
			solveOffset = -(h.q+h.s)*2-20;
		}

		public void onClick(KeyAssemblyPuzzle p, double dx, double dy, EntityPlayer ep) {
			if (occupant != null) {
				//int dx = x-p.grid.getHexLocation(location).x-CELL_SIZE/2;
				//int dy = (int)(y-p.grid.getHexLocation(location).y-CELL_SIZE*SIN60/2);
				double r = ReikaMathLibrary.py3d(dx, dy, 0);
				//ReikaJavaLibrary.pConsole(dx+", "+dy+" @ "+r+" / "+CELL_SIZE/4D);
				//ReikaJavaLibrary.pConsole(a+" @ "+location+" > "+dir+" > "+h);
				if (r >= CELL_SIZE/4) {
					double a = -Math.toDegrees(Math.atan2(dy, dx));
					int dir = p.grid.getNeighborDirection(a);
					//Hex h = location.getNeighbor(dir);
					//ReikaJavaLibrary.pConsole(a+" > "+dir+" > "+this.getNeighbor(p, dir).occupant);
					occupant.move(p, dir, ep);
				}
			}
			//ReikaJavaLibrary.pConsole(p.isComplete()+": "+p.activeCells+"/"+p.totalCells);
		}

		public HexTile getOccupant() {
			return occupant;
		}

		public void render(KeyAssemblyPuzzle p, Tessellator v5, boolean inBoard, float f) {
			GL11.glPushMatrix();
			if (inBoard) {
				Point loc = p.grid.getHexLocation(location);
				GL11.glTranslated(loc.x, loc.y, 0);
			}
			if (occupant != null) {
				occupant.render(p, v5, inBoard, f);
			}
			else {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, voidPNG);
				double u = 66/128D;
				double v = 6/64D;
				double du = 126/128D;
				double dv = 59/64D;
				v5.startDrawingQuads();
				v5.addVertexWithUV(0, CELL_SIZE*SIN60, 0, u, dv);
				v5.addVertexWithUV(CELL_SIZE, CELL_SIZE*SIN60, 0, du, dv);
				v5.addVertexWithUV(CELL_SIZE, 0, 0, du, v);
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.draw();
			}
			if (inBoard) {
				if (isHovered || flashTick > 0) {
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					if (flashTick > 0) {
						int a = (int)(255*flashTick/20D);
						int a2 = a/2-32;
						p.grid.drawHexEdges(v5, location, 0xff0000 | (a << 24));
						if (a2 > 0)
							p.grid.drawFilledHex(v5, location, 0xff0000 | (a2 << 24));
					}
					else if (isHovered) {
						p.grid.drawHexEdges(v5, location, 0xffffffff);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						for (int i = 0; i < 6; i++) {
							if (Keyboard.isKeyDown(i == 0 ? Keyboard.KEY_0 : Keyboard.KEY_1+i-1)) {
								if (this.getNeighbor(p, i) != null)
									this.getNeighbor(p, i).isHovered = true;
							}
						}
					}
				}
			}
			isHovered = false;
			if (flashTick > 0)
				flashTick--;
			GL11.glPopMatrix();
		}

		public HexCell getNeighbor(KeyAssemblyPuzzle p, int direction) {
			return p.getCell(location.getNeighbor(direction));
		}

		public void flash() {
			flashTick = 20;
		}

		public float getSolveFactor() { //0-10 (0-1) fade to black; 10-20 (1-2) fade from black to white; 20-30 (2-3) fade from white
			if (solveTick == 0 || this.solveTick() <= 0)
				return 0;
			int tick = this.solveTick();
			return tick/20F;//tick <= 10 ? tick/10F : tick <= 20 ? 1+(tick-10)/10F : 2+(tick-20)/10F;
		}

		public void tickSolve() {
			if (solveTick > 0 && solveTick < /*30*/20-solveOffset/* && GuiScreen.isCtrlKeyDown()*/) {
				solveTick++;
			}
		}

		private int solveTick() {
			return solveTick+solveOffset;
		}

		@Override
		public String toString() {
			return location.toString()+" : "+occupant;
		}

		public boolean playerKnows(EntityPlayer ep) {
			if (occupant == null)
				return true;
			return tower == null || LoreManager.instance.hasPlayerScanned(ep, tower);
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
			if (!ElementMixer.instance.hasMixes(color)) {
				//ReikaJavaLibrary.pConsole(color);
				return true;
			}
			for (Hex h : location.location.getNeighbors()) {
				HexCell c = p.getCell(h);
				if (c != null && c.occupant != null) {
					if (this.isRelatedTo(c.occupant)) {
						//ReikaJavaLibrary.pConsole(color+" & "+c.occupant.color+" @ "+location, color == CrystalElement.WHITE);
						return true;
					}
				}
			}
			return false;
		}

		private boolean isRelatedTo(HexTile h) {
			return /*color == h.color || */ElementMixer.instance.related(color, h.color);
		}

		@SideOnly(Side.CLIENT)
		public void render(KeyAssemblyPuzzle p, Tessellator v5, boolean inBoard, float f) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			Point loc = p.grid.getHexLocation(location.location);
			if (!inBoard || location.playerKnows(ep)) {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, hexPNG);
				double u = (2+64*(color.ordinal()%8))/512D;
				double v = (6+color.ordinal()/8*64*2+(!inBoard || state ? 64 : 0))/256D;
				double du = (u*512+60)/512D;
				double dv = (v*256+53)/256D;
				v5.startDrawingQuads();
				v5.setColorRGBA_I(ReikaColorAPI.mixColors(0xffffff, 0x000000, f), 255);
				v5.addVertexWithUV(0, CELL_SIZE*SIN60, 0, u, dv);
				v5.addVertexWithUV(CELL_SIZE, CELL_SIZE*SIN60, 0, du, dv);
				v5.addVertexWithUV(CELL_SIZE, 0, 0, du, v);
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.draw();
			}
			else {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, voidPNG);
				double u = 2/128D;
				double v = 6/64D;
				double du = 62/128D;
				double dv = 59/64D;
				v5.startDrawingQuads();
				v5.addVertexWithUV(0, CELL_SIZE*SIN60, 0, u, dv);
				v5.addVertexWithUV(CELL_SIZE, CELL_SIZE*SIN60, 0, du, dv);
				v5.addVertexWithUV(CELL_SIZE, 0, 0, du, v);
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.draw();
			}
		}

		public boolean move(KeyAssemblyPuzzle p, int direction, EntityPlayer ep) {
			if (!p.grid.getValidMovementDirections(location.location).contains(direction)) {
				if (ep != null) {
					location.flash();
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, Minecraft.getMinecraft().thePlayer, 1, 2F);
				}
				return false;
			}
			//ReikaJavaLibrary.pConsole(location.location+" > "+direction+" into "+p.getCell(location.getNeighbor(p, direction).location).occupant.color);
			HexCell target = location.getNeighbor(p, direction);
			if (target == null) {
				throw new RuntimeException(location.location.getNeighbor(direction).toString()+" has null cell yet was moved to?!");
			}
			if (target.occupant != null) {
				if (ep != null) {
					target.flash();
					ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, Minecraft.getMinecraft().thePlayer, 1, 2F);
				}
				return false;
			}
			p.setCellContents(location.location, null, ep);
			p.emptyHexes.add(location.location);
			location = location.getNeighbor(p, direction);
			p.emptyHexes.remove(location.location);
			p.setCellContents(location.location, this, ep);
			this.update(p, true, ep);
			return true;
		}

		private void update(KeyAssemblyPuzzle p, boolean adj, EntityPlayer ep) {
			boolean flag = this.isValid(p);
			//ReikaJavaLibrary.pConsole(location.location+": "+color+", "+state+" > "+flag);
			if (flag != state) {
				if (flag) {
					p.activeCells++;
					if (ep != null) {
						ReikaSoundHelper.playClientSound(ChromaSounds.CAST, Minecraft.getMinecraft().thePlayer, 0.75F, 2);
						ReikaSoundHelper.playClientSound(ChromaSounds.CAST, Minecraft.getMinecraft().thePlayer, 0.75F, 1.5F);
					}
				}
				else {
					p.activeCells--;
					if (ep != null) {
						ReikaSoundHelper.playClientSound(ChromaSounds.RIFT, Minecraft.getMinecraft().thePlayer, 1, 0.875F);
						ReikaSoundHelper.playClientSound(ChromaSounds.RIFT, Minecraft.getMinecraft().thePlayer, 1, 0.5F);
					}
				}
				state = flag;
			}
			if (adj) {
				for (Hex h : location.location.getNeighbors()) {
					HexCell c = p.getCell(h);
					if (c != null && c.occupant != null) {
						c.occupant.update(p, false, ep);
					}
				}
			}
		}

		@Override
		public String toString() {
			return color.name();
		}
	}

	public static class TileGroup {

		private final HashSet<Hex> hexes = new HashSet();

		private TileGroup() {

		}

		@Override
		public String toString() {
			return hexes.toString();
		}

		public Collection<Hex> getHexes() {
			return Collections.unmodifiableCollection(hexes);
		}

		public Point getCenter(KeyAssemblyPuzzle pz) {
			Point p = new Point(0, 0);
			for (Hex h : hexes) {
				Point p2 = pz.getHexLocation(h);
				p = p.translate(p2.x, p2.y);
			}
			p = p.scale(1D/hexes.size());
			return p;
		}

		public static TileGroup random(KeyAssemblyPuzzle p) {
			Random rand = new Random();

			TileGroup g = new TileGroup();
			Hex h = p.getOrigin();
			g.hexes.add(h);
			for (int i = 0; i < 3; i++) {
				h = h.getNeighbor(rand.nextInt(6));
				g.hexes.add(h);
			}
			return g;
		}

	}

	private static class Move {

		private final Hex hex;
		private final int direction;

		private Move(Hex h, int dir) {
			hex = h;
			direction = dir;
		}

		private Move invert(HexGrid grid) {
			return new Move(hex.getNeighbor(direction), grid.getOppositeDirection(direction));
		}
	}
}
