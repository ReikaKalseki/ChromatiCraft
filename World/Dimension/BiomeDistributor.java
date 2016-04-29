/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructurePair;
import Reika.ChromatiCraft.Base.ThreadedGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.Biomes;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.ChromaDimensionBiomeType;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager.SubBiomes;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.LobulatedCurve;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.ListFactory;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//always forest at center?
public class BiomeDistributor extends ThreadedGenerator {

	public final long seed;
	private final Random rand;

	private static final int SIZE = 2048;//4096;

	public static final int SCALE_FACTOR = 1;

	public static final int SPREADERS_PER_DOT = 1024;

	/*
	private static final int[][] DOT_SPREAD = {
		{0, 1, 1, 1, 0},
		{1, 1, 1, 1, 1},
		{1, 1, 1, 1, 1},
		{1, 1, 1, 1, 1},
		{0, 1, 1, 1, 0},
	};
	 */

	//private static WeightedRandom<Coordinate> dotSpread = WeightedRandom.fromArray(DOT_SPREAD);

	private static ChromaDimensionBiomeType[][] biomes = new ChromaDimensionBiomeType[SIZE][SIZE];

	private final Collection<Spreader> spreaders = new ArrayList();
	private final MultiMap<ChromaDimensionBiomeType, Point> blobLocations = new MultiMap(new ListFactory());

	public BiomeDistributor(long seed) {
		this.seed = seed;
		rand = new Random(seed);
	}

	public static ChromaDimensionBiome getBiome(int x, int z) {
		return getAdj(x/SCALE_FACTOR, z/SCALE_FACTOR, 0, 0).getBiome();
	}

	@Override
	public void run() throws Throwable {
		biomes = new ChromaDimensionBiomeType[SIZE][SIZE];
		this.distributeDots();
		//this.createImage("dots");
		this.spreadDots();
		//this.createImage("spread");
		boolean flag = this.fillEmptySpaces();
		while (flag) {
			flag = this.fillEmptySpaces();
			//this.createImage("blur");
		}
		this.featherEdges();
		//this.createImage("feather");

		this.addInternalBiomes();
		//this.createImage("sub");

		this.addStructureBiomes();
		//this.createImage("struct");

		//this.createImage("end");

		ChunkProviderChroma.finishGeneration(ThreadedGenerators.BIOME);
	}

	private void addInternalBiomes() {
		for (int i = 0; i < ChromaDimensionManager.Biomes.biomeList.length; i++) {
			Biomes b = Biomes.biomeList[i];
			SubBiomes s = b.getSubBiome();
			if (s != null) {
				Collection<Point> c = blobLocations.get(b);
				for (Point p : c) {
					if (rand.nextDouble() < s.spawnWeight) {
						int dx = getAdj(p.x, rand.nextInt(33)-16);
						int dz = getAdj(p.y, rand.nextInt(33)-16);
						while (biomes[dx][dz] != b) {
							dx = getAdj(p.x, rand.nextInt(33)-16);
							dz = getAdj(p.y, rand.nextInt(33)-16);
						}
						double f = 0.25+rand.nextDouble()*0.5;
						this.placeBlob(s, dx, dz, f, b);
					}
				}
			}
		}
	}

	private void addStructureBiomes() throws InterruptedException {
		boolean printed = false;
		while(!ChunkProviderChroma.isGeneratorReady(ThreadedGenerators.STRUCTURE)) {
			Thread.sleep(100);
			if (!printed)
				ChromatiCraft.logger.log("Waiting for structure generator to finish to place relevant biomes...");
			printed = true;
		}
		for (StructurePair p : ChunkProviderChroma.getStructures()) {
			int x = p.generator.getEntryPosX();
			int z = p.generator.getEntryPosZ();
			//this.placeBlob(Biomes.STRUCTURE, x, z, 0.125F, over);
		}
	}

	private void distributeDots() {
		for (int i = 0; i < ChromaDimensionManager.Biomes.biomeList.length; i++) {
			Biomes b = Biomes.biomeList[i];
			for (int k = 0; k < b.spawnWeight; k++) {
				int dx = rand.nextInt(SIZE);
				int dz = rand.nextInt(SIZE);
				while (biomes[dx][dz] != null) {
					dx = rand.nextInt(SIZE);
					dz = rand.nextInt(SIZE);
				}
				//for (int a = -4; a <= 4; a++)
				//for (int c = -4; c <= 4; c++)
				//biomes[dx][dz] = b;
				/*
				this.paint(dx, dz, b, 4);
				for (int m = 0; m < SPREADERS_PER_DOT; m++) {
					int sx = getAdj(dx, ReikaRandomHelper.getRandomPlusMinus(0, 16));
					int sz = getAdj(dz, ReikaRandomHelper.getRandomPlusMinus(0, 16));
					spreaders.add(new Spreader(b, ForgeDirection.VALID_DIRECTIONS[2+m%4], sx, sz));
				}
				 */
				this.placeBlob(b, dx, dz, 1, null);
			}
		}
	}

	private void placeBlob(ChromaDimensionBiomeType b, int dx, int dz, double f, ChromaDimensionBiomeType over) {
		f *= SIZE/4096F;
		blobLocations.addValue(b, new Point(dx, dz));
		double da = 0.5;
		LobulatedCurve c = new LobulatedCurve(384*f, 48*f, 6, da).generate(rand);
		for (double d = 0; d < 360; d += da) {
			double r = c.getRadius(d);
			r = r+f*(rand.nextInt(9)+rand.nextInt(9)+rand.nextInt(9)); //some variability
			for (double dr = 0; dr <= r; dr += 0.5) {
				double ax = dx+dr*Math.cos(Math.toRadians(d));
				double az = dz+dr*Math.sin(Math.toRadians(d));
				int x = MathHelper.floor_double(ax);
				int z = MathHelper.floor_double(az);
				this.paint(x, z, b, 2, over);
			}
		}
	}

	private void spreadDots() {
		while (!spreaders.isEmpty()) {
			Iterator<Spreader> it = spreaders.iterator();
			while (it.hasNext()) {
				Spreader s = it.next();
				if (s.update()) {

				}
				else {
					it.remove();
				}
			}
		}
	}

	private boolean fillEmptySpaces() {
		//boolean flag = false;
		ArrayList<Point> li = new ArrayList();
		for (int i = 0; i < SIZE; i++) {
			for (int k = 0; k < SIZE; k++) {
				if (biomes[i][k] == null) {
					/*
					this.fillEmptySpace(i, k);
					 */
					li.add(new Point(i, k));
					//flag = true;
				}
			}
		}
		if (!li.isEmpty()) {
			Collections.shuffle(li);
			for (Point p : li) {
				this.fillEmptySpace(p.x, p.y);
			}
			return true;
		}
		return false;
		//return flag;
	}

	private void fillEmptySpace(int x, int z) {
		CountMap<ChromaDimensionBiomeType> map = new CountMap();

		int r = 6;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				ChromaDimensionBiomeType b = this.getAdj(x, z, i, k);
				if (b != null)
					map.increment(b);
			}
		}

		/*
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			Biomes b = this.getAdj(x, z, dir.offsetX, dir.offsetZ);
			if (b != null)
				map.increment(b);
		}
		 */

		if (!map.isEmpty()) {
			WeightedRandom<ChromaDimensionBiomeType> w = map.asWeightedRandom();
			ChromaDimensionBiomeType b = w.getRandomEntry();
			//biomes[x][z] = b;
			this.paint(x, z, b, 4, null);
		}
	}

	private void featherEdges() {
		for (int i = 0; i < SIZE; i++) {
			for (int k = 0; k < SIZE; k++) {
				ChromaDimensionBiomeType b = biomes[i][k];
				ChromaDimensionBiomeType bb = null;
				boolean flag = true;
				for (int d = 2; d < 6; d++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[d];
					ChromaDimensionBiomeType b2 = this.getAdj(i, k, dir.offsetX, dir.offsetZ);
					if (b == b2) {
						flag = false;
						break;
					}
					else {
						bb = b2;
					}
				}
				if (flag) {
					biomes[i][k] = bb;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void createImage(String phase) throws IOException {
		File f = new File(DragonAPICore.getMinecraftDirectory(), "DimensionMap/"+seed+"L/"+System.nanoTime()+"_"+phase+".png");
		if (f.exists())
			f.delete();
		f.getParentFile().mkdirs();
		f.createNewFile();
		BufferedImage img = new BufferedImage(biomes.length, biomes.length, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < biomes.length; i++) {
			for (int k = 0; k < biomes[i].length; k++) {
				ChromaDimensionBiomeType b = biomes[i][k];
				ChromaDimensionBiomeType o = b;
				if (b instanceof SubBiomes)
					b = ((SubBiomes)b).getParent();
				int color = b != null ? 0xff000000 | Color.HSBtoRGB(((Biomes)b).ordinal()/(float)Biomes.biomeList.length, 1, 1) : 0xffffffff;
				if (o instanceof SubBiomes)
					color = ReikaColorAPI.getColorWithBrightnessMultiplier(color, 0.67F);
				if (o == Biomes.STRUCTURE)
					color = 0x606060;
				img.setRGB(i, k, color);
			}
		}
		ImageIO.write(img, "png", f);
	}

	private	static ChromaDimensionBiomeType getAdj(int x, int z, int dx, int dz) {
		return biomes[getAdj(x, dx)][getAdj(z, dz)];
	}

	private	static void setAdj(int x, int z, int dx, int dz, ChromaDimensionBiomeType b) {
		biomes[getAdj(x, dx)][getAdj(z, dz)] = b;
	}

	private	static int getAdj(int p, int d) {
		return (SIZE+(p+d)%SIZE)%SIZE;
	}

	private static void paint(int x, int z, ChromaDimensionBiomeType b, int r, ChromaDimensionBiomeType over) {
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				if (i*i+k*k <= r*r) {
					if (getAdj(x, z, i, k) == over)
						setAdj(x, z, i, k, b);
				}
			}
		}
	}

	private class Spreader {

		private final ChromaDimensionBiomeType biome;
		private ForgeDirection direction;

		private int posX;
		private int posZ;

		private int stepSize = 6;

		private Spreader(ChromaDimensionBiomeType b, ForgeDirection dir, int x, int z) {
			biome = b;
			direction = dir;

			posX = x;
			posZ = z;
		}

		private boolean update() {
			ArrayList<ForgeDirection> li = this.getDirections();
			if (li.isEmpty())
				return false;
			direction = this.randomizeDirection(li);
			posX = BiomeDistributor.this.getAdj(posX, direction.offsetX*stepSize);
			posZ = BiomeDistributor.this.getAdj(posZ, direction.offsetZ*stepSize);
			BiomeDistributor.this.paint(posX, posZ, biome, stepSize-1, null);
			return true;
		}

		private ForgeDirection randomizeDirection(ArrayList<ForgeDirection> li) {
			if (rand.nextInt(5) > 0 && li.contains(direction))
				return direction;
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(direction);
			ForgeDirection right = ReikaDirectionHelper.getRightBy90(direction);
			if (rand.nextBoolean() && li.contains(right)) {
				return right;
			}
			else if (li.contains(left)) {
				return left;
			}
			return li.get(rand.nextInt(li.size()));
		}

		private ArrayList<ForgeDirection> getDirections() {
			ArrayList<ForgeDirection> li = new ArrayList();
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				ChromaDimensionBiomeType b = BiomeDistributor.this.getAdj(posX, posZ, dir.offsetX*stepSize, dir.offsetZ*stepSize);
				if (b == null)
					li.add(dir);
			}
			return li;
		}

	}

	@Override
	public String getStateMessage() {
		return "Biome array populated, "+SIZE+"x"+SIZE+" with "+blobLocations.totalSize()+" biome patches of "+blobLocations.keySet().size()+" types.";
	}

}
