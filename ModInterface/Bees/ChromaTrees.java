package Reika.ChromatiCraft.ModInterface.Bees;

import java.awt.Color;
import java.util.Locale;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Structure.RainbowTreeBlueprint;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.RainbowEffect;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.RainbowTreeGenerator;
import Reika.ChromatiCraft.World.TreeShaper;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.BasicTreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.TraitsTree;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.TreeBranch;
import Reika.DragonAPI.ModInteract.Bees.TreeTraits;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.world.ITreeGenData;

public class ChromaTrees {

	//yield, sappiness, maturation have better genes not normally obtainable

	static RainbowEffect rainbowEffect;

	static TreeBranch dyeBranch;

	private static TreeSpecies rainbowTree;
	private static TreeSpecies[] dyeTrees = new TreeSpecies[16];
	private static TreeSpecies[] hybridDyeTrees = new TreeSpecies[16];

	public static void register() {
		//superFertility = Saplings.createNew("extreme", 0.25F, false);

		dyeBranch = new TreeBranch("branch.ccdye", "Dye", "Pigmentum", "These leaves shimmer one or more colors, and seem to be associated with crystal energy.");

		rainbowEffect = new RainbowEffect();

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			DyeTree tree = new DyeTree(color, DyeTreeTypes.list[i].traits);
			tree.register();
			ITree ii = tree.constructIndividual();
			AlleleManager.ersatzSaplings.put(ChromaBlocks.DECAY.getStackOf(color), ii);
			//AlleleManager.ersatzSaplings.put(ChromaBlocks.DYELEAF.getStackOf(color), ii);
			AlleleManager.ersatzSpecimen.put(ChromaBlocks.DECAY.getStackOf(color), ii);
			AlleleManager.ersatzSaplings.put(ChromaBlocks.DYESAPLING.getStackOf(color), ii);
			AlleleManager.ersatzSpecimen.put(ChromaBlocks.DYESAPLING.getStackOf(color), ii);
			//AlleleManager.ersatzSpecimen.put(ChromaBlocks.DYELEAF.getStackOf(color), ii);
			dyeTrees[i] = tree;

			HybridDyeTree tree2 = new HybridDyeTree(color, getHybridParents(color));
			tree2.register();
			hybridDyeTrees[i] = tree2;
		}

		rainbowTree = new RainbowTree();
		rainbowTree.register();
		ITree tree = rainbowTree.constructIndividual();
		AlleleManager.ersatzSaplings.put(ChromaBlocks.RAINBOWLEAF.getStackOfMetadata(0), tree);
		AlleleManager.ersatzSpecimen.put(ChromaBlocks.RAINBOWLEAF.getStackOfMetadata(0), tree);
		AlleleManager.ersatzSaplings.put(ChromaBlocks.RAINBOWSAPLING.getStackOf(), tree);
		AlleleManager.ersatzSpecimen.put(ChromaBlocks.RAINBOWSAPLING.getStackOf(), tree);
	}

	public static TreeSpecies getRainbowTree() {
		return rainbowTree;
	}

	public static TreeSpecies getDyeTree(CrystalElement e, boolean hybrid) {
		TreeSpecies[] arr = hybrid ? hybridDyeTrees : dyeTrees;
		return arr[e.ordinal()];
	}

	public static boolean isDyeTree(IAlleleTreeSpecies ie) {
		return ie instanceof DyeTreeBase;
	}

	private static String[] getHybridParents(CrystalElement color) {
		switch(color) {
			case BLACK:
				return new String[]{"forestry.treeDate"};
			case RED:
				return new String[]{"extratrees.species.redmaple", "forestry.treeBaobab"};
			case GREEN:
				return new String[]{"forestry.treeBalsa"};
			case BROWN:
				return new String[]{"forestry.treeChestnut"};
			case BLUE:
				return new String[]{"forestry.treeWillow"};
			case PURPLE:
				return new String[]{"forestry.treePlum"};
			case CYAN:
				return new String[]{"forestry.treeMahoe"};
			case LIGHTGRAY:
				return new String[]{"forestry.treePoplar"};
			case GRAY:
				return new String[]{"extratrees.species.locust", "forestry.treeZebrawood"};
			case PINK:
				return new String[]{"forestry.treeCherry"};
			case LIME:
				return new String[]{"forestry.treeSipiri"};
			case YELLOW:
				return new String[]{"forestry.treeLemon"};
			case LIGHTBLUE:
				return new String[]{"forestry.treeWillow"};
			case MAGENTA:
				return new String[]{"forestry.treeBalsa"};
			case ORANGE:
				return new String[]{"forestry.treeSequioa"};
			case WHITE:
				return new String[]{"forestry.treeBalsa"};
		}
		return new String[0];
	}

	private static class RainbowTree extends BasicTreeSpecies {

		protected RainbowTree() {
			super("Rainbow", "chroma.rainbow", "Pigmentum Pluralis", "Reika", dyeBranch);
		}

		@Override
		public String getDescription() {
			return ChromaDescriptions.getHoverText("rainbowleaf");
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public boolean isDominant() {
			return false;
		}

		@Override
		public IAlleleLeafEffect getEffectAllele() {
			return rainbowEffect;
		}

		@Override
		public Yield getYield() {
			return Yield.LOWER;
		}

		@Override
		public Heights getHeight() {
			return Heights.LARGER;
		}

		@Override
		public int getGirth() {
			return 2;
		}

		@Override
		public Sappiness getSappiness() {
			return Sappiness.LOW;
		}

		@Override
		public Maturation getMaturation() {
			return Maturation.SLOW;
		}

		@Override
		public Saplings getSaplingRate() {
			return Saplings.LOWEST;
		}

		@Override
		public Territory getTerritorySize() {
			return Territory.LARGER;
		}

		@Override
		public int getLeafColour(boolean pollinated) {
			return Color.HSBtoRGB((int)((System.currentTimeMillis()/10)%360)/360F, 0.7F, 1F);
		}

		@Override
		public IIcon getLeafIcon(boolean pollinated, boolean fancy) {
			return ChromaBlocks.RAINBOWLEAF.getBlockInstance().getIcon(0, 0);
		}

		@Override
		public int getGermlingColour(EnumGermlingType type, int renderPass) {
			return CrystalBees.getRainbowBee().getOutlineColor();
		}

		@Override
		public int getIconColour(int renderPass) {
			return this.getGermlingColour(EnumGermlingType.SAPLING, renderPass);
		}

		@Override
		protected BlockKey getLogBlock(ITreeGenome genes, World world, int x, int y, int z, Random rand, ITreeGenData data) {
			return ReikaTreeHelper.OAK.getLog();
		}

		@Override
		protected boolean generate(World world, int x, int y, int z, Random rand, ITreeGenData data) {
			boolean big = data.getGirth(world, x, y, z) >= 2 && data.getHeightModifier() >= 1.5;
			if (big) {
				if (RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z)) {
					//RainbowTreeGenerator.getInstance().generateLargeRainbowTree(world, x, y, z, rand);
					FilledBlockArray arr = RainbowTreeBlueprint.getBlueprint(world, x-5, y-2, z-5, ReikaTreeHelper.OAK);
					for (Coordinate c : arr.keySet()) {
						BlockKey bk = arr.getBlockKeyAt(c.xCoord, c.yCoord, c.zCoord);
						if (bk.blockID == Blocks.log) {
							ForgeDirection dir = ForgeDirection.UP;
							if (bk.metadata == 4) {
								dir = ForgeDirection.EAST;
							}
							else if (bk.metadata == 8) {
								dir = ForgeDirection.SOUTH;
							}
							data.setLogBlock(world, c.xCoord, c.yCoord, c.zCoord, dir);
						}
						else {
							data.setLeaves(world, null, c.xCoord, c.yCoord, c.zCoord);
						}
					}
					return true;
				}
				else {
					return false;
				}
			}
			return RainbowTreeGenerator.getInstance().tryGenerateSmallRainbowTree(world, x, y, z, rand, data.getHeightModifier());
		}

	}

	private static class HybridDyeTree extends DyeTreeBase {

		private final String[] breedParents;

		public HybridDyeTree(CrystalElement e, String... other) {
			super(e, e.displayName+" Hybrid", "chroma.hybrid"+e.name().toLowerCase(Locale.ENGLISH), "Pigmentum Hybridus "+e.displayName, DyeTreeTypes.list[e.ordinal()].traitsHybrid);
			breedParents = other;
		}

		@Override
		protected void onRegister() {
			for (String s : breedParents) {
				IAllele ia = AlleleManager.alleleRegistry.getAllele(s);
				if (ia instanceof IAlleleTreeSpecies) {
					this.addBreeding((IAlleleTreeSpecies)ia, dyeTrees[color.ordinal()], 5);
					break;
				}
			}
		}

		@Override
		protected void placeTree(World world, int x, int y, int z, GameProfile owner, ITreeGenData data) {
			int h0 = 2+rand.nextInt(3);
			int h = h0+MathHelper.ceiling_float_int(data.getHeightModifier()*(5+rand.nextInt(7)));
			for (int i = 1; i <= h; i++) {
				if (world.getBlock(x, y+i, z) != Blocks.air) {
					h = i;
					break;
				}
			}
			while (h > 2 && world.getBlock(x, y+h+4, z) != Blocks.air)
				h--;
			if (h <= 3)
				return;
			h0 = Math.min(h0, h/2);
			int w = 0;
			int maxw = 2+data.getGirth(world, x, y, z);
			for (int j = 0; j < h; j++) {
				int dy = y+j;
				data.setLogBlock(world, x, dy, z, ForgeDirection.UP);
				if (j >= h0) {
					boolean canUp = w < maxw && h-j-1 > w;
					boolean canDown = w > 1;
					boolean up = (rand.nextBoolean() && canUp) || !canDown;
					if (up) {
						w++;
					}
					else {
						w--;
					}
					//ReikaJavaLibrary.pConsole("j = "+j+"/"+h+", "+(up ? "up" : "down")+" to "+w);
					int r = w;
					for (int i = -r; i <= r; i++) {
						for (int k = -r; k <= r; k++) {
							if ((i != 0 || k != 0) && Math.abs(i)+Math.abs(k) <= w+1) {
								int dx = x+i;
								int dz = z+k;
								if (world.getBlock(dx, dy, dz).canBeReplacedByLeaves(world, dx, dy, dz))
									data.setLeaves(world, owner, dx, dy, dz);
							}
						}
					}
				}
			}
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (i == 0 || k == 0) {
						int dx = x+i;
						int dz = z+k;
						if (world.getBlock(dx, y+h, dz).canBeReplacedByLeaves(world, dx, y+h, dz))
							data.setLeaves(world, owner, dx, y+h, dz);
					}
				}
			}
			if (world.getBlock(x, y+h+1, z).canBeReplacedByLeaves(world, x, y+h+1, z))
				data.setLeaves(world, owner, x, y+h+1, z);
		}

	}

	private static class DyeTree extends DyeTreeBase {

		protected DyeTree(CrystalElement e, TreeTraits t) {
			super(e, e.displayName+" Dye", "chroma.dye"+e.name().toLowerCase(Locale.ENGLISH), "Pigmentum "+e.displayName, t);
		}

		@Override
		protected void placeTree(World world, int x, int y, int z, GameProfile owner, ITreeGenData data) {
			TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[color.ordinal()], true);
		}

	}

	private static abstract class DyeTreeBase extends TraitsTree {

		public final CrystalElement color;

		protected DyeTreeBase(CrystalElement e, String name, String uid, String latinName, TreeTraits traits) {
			super(name, uid, latinName, "Reika", dyeBranch, traits);
			color = e;
		}

		@Override
		public final int getLeafColour(boolean pollinated) {
			return BlockDyeLeaf.getColor(color.ordinal(), false);
		}

		@Override
		public final IIcon getLeafIcon(boolean pollinated, boolean fancy) {
			return ChromaBlocks.DYELEAF.getBlockInstance().getIcon(0, 0);
		}

		@Override
		public int getGermlingColour(EnumGermlingType type, int renderPass) {
			return this.getLeafColour(false);
		}

		@Override
		public String getDescription() {
			return ChromaDescriptions.getHoverText("dyeleaf");
		}

		@Override
		public final int getIconColour(int renderPass) {
			return this.getGermlingColour(EnumGermlingType.SAPLING, renderPass);
		}

		@Override
		public final boolean isDominant() {
			return false;
		}

		@Override
		protected final BlockKey getLogBlock(ITreeGenome genes, World world, int x, int y, int z, Random rand, ITreeGenData data) {
			return ReikaTreeHelper.OAK.getLog();
		}

		@Override
		protected final boolean generate(World world, int x, int y, int z, Random rand, ITreeGenData data) {
			if (BlockDyeSapling.canGrowAt(world, x, y, z, false)) {
				this.placeTree(world, x, y, z, null, data);
				return true;
			}
			return false;
		}

		protected abstract void placeTree(World world, int x, int y, int z, GameProfile owner, ITreeGenData data);

		@Override
		public final IAlleleFruit getFruitAllele() {
			return this.getNoFruit();
		}

		@Override
		public final IAlleleLeafEffect getEffectAllele() {
			return this.getNoEffect();
		}

		@Override
		public final IAlleleGrowth getGrowthAllele() {
			return this.getLightGrowth();
		}

		@Override
		public final boolean hasEffect() {
			return false;
		}

		@Override
		public final boolean isSecret() {
			return false;
		}

		@Override
		public final boolean isCounted() {
			return true;
		}

	}

}
