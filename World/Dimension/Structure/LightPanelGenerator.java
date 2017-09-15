/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.FixedLightPanelRoom;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.FixedLightPattern;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.LightPanelEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.LightPanelLoot;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.LightPanelRoom;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.LightType;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.common.FMLCommonHandler;


public class LightPanelGenerator extends DimensionStructureGenerator {

	private static final int[] ROW_COUNTS = {
		3,
		4,
		6,
		6,
		8,
		8,
		10,
		10,
		12,
		12,
		14,
		16,
	};

	private static final int[] SWITCH_COUNTS = {
		3,
		3,
		4,
		4,
		5,
		6,
		6,
		8,
		8,
		8,
		10,
		10,
	};

	private static final int[] PATTERN_TIERS = {
		0,
		0,
		1,
		1,
		2,
		2,
		3,
		3,
		4,
		4,
		5,
		6,
	};

	private static final int[][] PREFAB_SIZES = {
		{3, 3},
		{4, 4},
		{4, 6},
		{4, 8},
		{5, 8},
		{6, 10},
		{8, 12},
	};

	private static final String PATH = "Structure Data/LightPanel";

	private static final ArrayList<FixedLightPattern>[] usablePatterns = new ArrayList[7];

	private final ArrayList<FixedLightPattern>[] patterns = new ArrayList[7];

	static {
		for (int i = 0; i < usablePatterns.length; i++) {
			usablePatterns[i] = new ArrayList();
			try {
				loadData(i);
			}
			catch (Exception e) {
				FMLCommonHandler.instance().raiseException(e, "Error initializing Light Panel Puzzle", true);
			}
		}
	}

	private static void loadData(int tier) throws Exception {
		int nsw = PREFAB_SIZES[tier][0];
		int nrw = PREFAB_SIZES[tier][1];
		int x1 = 1;
		int y1 = 1;
		int sx = LightType.list.length*nsw+nsw+1+1;
		int sy = nrw+2+1;
		//int w = ;
		//int h = ;

		/*
		InputStream in = ChromatiCraft.class.getResourceAsStream(PATH+"/tier_"+tier+".lpuzzle");
		String sg = ReikaFileReader.getFileAsLines(in, true).get(0);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(sg)));//new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		 */

		InputStream in = ChromatiCraft.class.getResourceAsStream(PATH+"/tier"+tier+".png");
		BufferedImage img = ImageIO.read(in);

		int bx = x1;
		int by = y1;
		while (bx < img.getWidth()) {
			FixedLightPattern p = new FixedLightPattern(tier, nrw, nsw);
			for (int k = 0; k < nsw; k++) {
				for (int i = 0; i < nrw; i++) {
					for (int l = 0; l < LightType.list.length; l++) {
						LightType type = LightType.list[l];
						int x = bx+1+l+k*(LightType.list.length+1);
						int y = by+1+i;
						int rgb = img.getRGB(x, y);
						int value = ReikaMathLibrary.clipLeadingHexBits(rgb & type.renderColor);
						if (value == 0xff) {
							p.connect(k, i, type);
						}
					}
				}
			}
			if (!p.isEmpty())
				usablePatterns[tier].add(p);
			bx += sx;
		}
		if (usablePatterns[tier].isEmpty())
			throw new IllegalStateException("This puzzle is unsolvable, as there are no valid patterns for tier "+tier+"!");
	}

	private LightPanelRoom[] levels;

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {

		for (int i = 0; i < patterns.length; i++) {
			patterns[i] = new ArrayList(usablePatterns[i]);
		}

		posY = 20+rand.nextInt(80);
		int x = chunkX;
		int z = chunkZ;

		this.addDynamicStructure(new LightPanelEntrance(this), x, z);

		x += 11;

		int size = getSize();
		levels = new LightPanelRoom[size];
		for (int i = 0; i < levels.length; i++) {
			FixedLightPattern p = this.getPattern(i, rand);
			int r = p.rowCount;//ROW_COUNTS[i];
			int s = p.switchCount;//SWITCH_COUNTS[i];
			LightPanelRoom lpr = new FixedLightPanelRoom(this, r, s, i, rand, x, posY, z, p);
			lpr.generatePuzzle();
			levels[i] = lpr;
			lpr.generate(world, x, posY, z);
			x += LightPanelRoom.DEPTH+1;
		}
		x += LightPanelLoot.WIDTH;
		new LightPanelLoot(this).generate(world, x, posY, z);
	}

	private FixedLightPattern getPattern(int level, Random rand) {
		int tier = PATTERN_TIERS[level];
		int idx = rand.nextInt(patterns[tier].size());
		FixedLightPattern p = patterns[tier].get(idx);
		if (patterns[tier].size() > 1)
			patterns[tier].remove(idx);
		return p;
	}

	private static int getSize() {
		switch(ChromaOptions.getStructureDifficulty()) {
			case 1:
				return 5;
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
		for (int i = 0; i < levels.length; i++) {
			if (!levels[i].isComplete())
				return false;
		}
		return true;
	}

	@Override
	public void openStructure(World world) {
		for (int i = 0; i < levels.length; i++) {
			levels[i].updateDoor(world, true);
		}
	}

	public void toggleSwitch(World world, int x, int y, int z, int level, int channel, boolean active) {
		levels[level].toggleSwitch(world, x, y, z, channel, active);
	}

	@Override
	protected void clearCaches() {
		levels = null;
	}

}
