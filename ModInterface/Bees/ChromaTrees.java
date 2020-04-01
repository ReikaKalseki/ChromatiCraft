package Reika.ChromatiCraft.ModInterface.Bees;

import java.awt.Color;
import java.util.Locale;
import java.util.Random;

import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.RainbowEffect;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.RainbowTreeGenerator;
import Reika.ChromatiCraft.World.TreeShaper;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.BasicTreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.TreeBranch;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.world.ITreeGenData;

public class ChromaTrees {

	//yield, sappiness, maturation have better genes not normally obtainable

	static RainbowEffect rainbowEffect;

	static TreeBranch dyeBranch;

	private static TreeSpecies rainbowTree;
	private static TreeSpecies[] dyeTrees = new TreeSpecies[16];

	public static void register() {
		//superFertility = Saplings.createNew("extreme", 0.25F, false);

		dyeBranch = new TreeBranch("branch.ccdye", "Dye", "Pigmentum", "These leaves shimmer one or more colors, and seem to be associated with crystal energy.");

		rainbowEffect = new RainbowEffect();

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			DyeTree tree = new DyeTree(color);
			tree.register();
			ITree ii = tree.constructIndividual();
			AlleleManager.ersatzSaplings.put(ChromaBlocks.DECAY.getStackOf(color), ii);
			AlleleManager.ersatzSaplings.put(ChromaBlocks.DYELEAF.getStackOf(color), ii);
			AlleleManager.ersatzSpecimen.put(ChromaBlocks.DECAY.getStackOf(color), ii);
			AlleleManager.ersatzSpecimen.put(ChromaBlocks.DYELEAF.getStackOf(color), ii);
			dyeTrees[i] = tree;
		}

		rainbowTree = new RainbowTree();
		rainbowTree.register();
		ITree tree = rainbowTree.constructIndividual();
		AlleleManager.ersatzSaplings.put(ChromaBlocks.RAINBOWLEAF.getStackOf(), tree);
		AlleleManager.ersatzSpecimen.put(ChromaBlocks.RAINBOWLEAF.getStackOf(), tree);
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
			return Yield.HIGH;
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
			return Sappiness.HIGH;
		}

		@Override
		public Maturation getMaturation() {
			return Maturation.AVERAGE;
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
			return Color.HSBtoRGB(rand.nextFloat(), 0.7F, 1F);
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
			return ReikaItemHelper.oakWood;
		}

		@Override
		protected boolean generate(World world, int x, int y, int z, Random rand, ITreeGenData data) {
			boolean big = data.getGirth(world, x, y, z) >= 2 && data.getHeightModifier() >= 1.5;
			if (big) {
				if (RainbowTreeGenerator.getInstance().checkRainbowTreeSpace(world, x, y, z)) {
					RainbowTreeGenerator.getInstance().generateLargeRainbowTree(world, x, y, z, rand);
					return true;
				}
				else {
					return false;
				}
			}
			return RainbowTreeGenerator.getInstance().tryGenerateSmallRainbowTree(world, x, y, z, rand, data.getHeightModifier());
		}

	}

	private static class DyeTree extends BasicTreeSpecies {

		private final CrystalElement color;

		public DyeTree(CrystalElement e) {
			super(e.displayName+" Dye", "chroma.dye"+e.name().toLowerCase(Locale.ENGLISH), "Pigmentum "+e.displayName, "Reika", dyeBranch);
			color = e;
		}

		@Override
		public int getLeafColour(boolean pollinated) {
			return BlockDyeLeaf.getColor(color.ordinal(), false);
		}

		@Override
		public IIcon getLeafIcon(boolean pollinated, boolean fancy) {
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
		public int getIconColour(int renderPass) {
			return this.getGermlingColour(EnumGermlingType.SAPLING, renderPass);
		}

		@Override
		public boolean isDominant() {
			return false;
		}

		@Override
		public Yield getYield() {
			return color == CrystalElement.BLACK ? Yield.HIGHEST : Yield.HIGH;
		}

		@Override
		public Heights getHeight() {
			return Heights.AVERAGE;
		}

		@Override
		public int getGirth() {
			return 1;
		}

		@Override
		public Sappiness getSappiness() {
			switch(color) {
				case CYAN:
					return Sappiness.HIGHEST;
				case YELLOW:
					return Sappiness.HIGHER;
				default:
					return Sappiness.LOW;
			}
		}

		@Override
		public Maturation getMaturation() {
			switch(color) {
				case LIGHTBLUE:
					return Maturation.FASTEST;
				case GREEN:
					return Maturation.FAST;
				default:
					return Maturation.AVERAGE;
			}
		}

		@Override
		public Saplings getSaplingRate() {
			switch(color) {
				case MAGENTA:
					return Saplings.HIGHEST;
				case GREEN:
					return Saplings.HIGHER;
				default:
					return Saplings.HIGH;
			}
		}

		@Override
		public Territory getTerritorySize() {
			return color == CrystalElement.LIME ? Territory.LARGE : Territory.DEFAULT;
		}

		@Override
		protected BlockKey getLogBlock(ITreeGenome genes, World world, int x, int y, int z, Random rand, ITreeGenData data) {
			return ReikaItemHelper.oakWood;
		}

		@Override
		protected boolean generate(World world, int x, int y, int z, Random rand, ITreeGenData data) {
			if (BlockDyeSapling.canGrowAt(world, x, y, z, false)) {
				TreeShaper.getInstance().generateRandomWeightedTree(world, x, y, z, ReikaDyeHelper.dyes[color.ordinal()], true);
				return true;
			}
			return false;
		}

	}

}
