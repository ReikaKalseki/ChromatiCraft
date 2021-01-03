package Reika.ChromatiCraft.ModInterface.Bees;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.Structure.RainbowTreeBlueprint;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.RainbowEffect;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.World.RainbowTreeGenerator;
import Reika.ChromatiCraft.World.TreeShaper;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Size;
import Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.ButterflySpecies;
import Reika.DragonAPI.ModInteract.Bees.ButterflySpecies.BasicButterflySpecies;
import Reika.DragonAPI.ModInteract.Bees.ButterflySpecies.ButterflyBranch;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.BasicTreeSpecies;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.NoLocaleDescriptionFruit;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.TraitsTree;
import Reika.DragonAPI.ModInteract.Bees.TreeSpecies.TreeBranch;
import Reika.DragonAPI.ModInteract.Bees.TreeTraits;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFruitFamily;
import forestry.api.world.ITreeGenData;

public class ChromaTrees {

	//yield, sappiness, maturation have better genes not normally obtainable

	static RainbowEffect rainbowEffect;

	static TreeBranch dyeBranch;
	static ButterflyBranch dyeFlyBranch;

	private static TreeSpecies rainbowTree;
	private static TreeSpecies[] dyeTrees = new TreeSpecies[16];
	private static TreeSpecies[] hybridDyeTrees = new TreeSpecies[16];

	private static ButterflySpecies[] butterflies = new ButterflySpecies[16];
	private static ButterflySpecies rainbowButterfly;

	private static Fertility superFertility;
	private static Life superLife;

	private static final IAlleleFruit[] berryFruit = new IAlleleFruit[16];

	private static final IFruitFamily berryFamily = new IFruitFamily() {

		@Override
		public String getUID() {
			return "chromaberry";
		}

		@Override
		public String getName() {
			return "Chroma Berries";
		}

		@Override
		public String getScientific() {
			return "pigmentum elementum";
		}

		@Override
		public String getDescription() {
			return "Chroma Berries";
		}

	};

	public static void register() {
		//superFertility = Saplings.createNew("extreme", 0.25F, false);

		dyeBranch = new TreeBranch("branch.ccdye", "Dye", "Pigmentum", "These leaves shimmer one or more colors, and seem to be associated with crystal energy.");

		rainbowEffect = new RainbowEffect();

		superFertility = Fertility.createNewFromBee(CrystalBees.getSuperFertility());
		superLife = Life.createNewFromBee(CrystalBees.getSuperLife());

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];

			berryFruit[i] = new BerryFruit(color);

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

			DyeButterfly fly = new DyeButterfly(color);
			fly.register();
			butterflies[i] = fly;

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

		rainbowButterfly = new RainbowButterfly();
		rainbowButterfly.register();
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

	public static ButterflySpecies getRainbowButterfly() {
		return rainbowButterfly;
	}

	public static ButterflySpecies getDyeButterfly(CrystalElement e) {
		return butterflies[e.ordinal()];
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
			this.addSuitableFruit(EnumFruitFamily.POMES);
			this.addSuitableFruit(EnumFruitFamily.NUX);
			this.addSuitableFruit(EnumFruitFamily.PRUNES);
		}

		@Override
		protected final String getIconMod(boolean pollen) {
			return "chromaticraft";
		}

		@Override
		protected final String getIconFolderRoot(boolean pollen) {
			return "forestry/trees";
		}

		@Override
		protected final String getSaplingIconName() {
			return "sapling";//"rainbow-sapling";
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
			return this.getLeafColour(false);
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
		public boolean hasEffect() {
			return true;
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

		@Override
		public boolean hasEffect() {
			return false;
		}

	}

	private static abstract class DyeTreeBase extends TraitsTree {

		public final CrystalElement color;

		protected DyeTreeBase(CrystalElement e, String name, String uid, String latinName, TreeTraits traits) {
			super(name, uid, latinName, "Reika", dyeBranch, traits);
			color = e;
			this.addSuitableFruit(EnumFruitFamily.POMES);
		}

		@Override
		protected final String getIconMod(boolean pollen) {
			return "chromaticraft";
		}

		@Override
		protected final String getIconFolderRoot(boolean pollen) {
			return "forestry/trees";
		}

		@Override
		protected final String getSaplingIconName() {
			return "sapling";//"dye-sapling-"+color.name().toLowerCase(Locale.ENGLISH);
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
			return this.getLeafColour(false);
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
			return berryFruit[color.ordinal()];
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
		public final boolean isSecret() {
			return false;
		}

		@Override
		public final boolean isCounted() {
			return true;
		}

	}

	private static class BerryFruit implements IAlleleFruit {

		private final CrystalElement color;
		private final IFruitProvider fruit;

		private BerryFruit(CrystalElement e) {
			color = e;
			fruit = new BerryProvider(e);
			AlleleManager.alleleRegistry.registerAllele(this, EnumTreeChromosome.FRUITS);
		}

		@Override
		public String getUID() {
			return "fruit.chromaberry."+color.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public boolean isDominant() {
			return false;
		}

		@Override
		public String getName() {
			return color.displayName+" Berries";
		}

		@Override
		public String getUnlocalizedName() {
			return this.getName();//"chromaberry."+color.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public IFruitProvider getProvider() {
			return fruit;
		}

	}

	private static class BerryProvider implements IFruitProvider, NoLocaleDescriptionFruit {

		private final CrystalElement color;

		private BerryProvider(CrystalElement e) {
			color = e;
		}

		@Override
		public IFruitFamily getFamily() {
			return berryFamily;
		}

		@Override
		public int getColour(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime) {
			float f = ripeningTime/(float)this.getRipeningPeriod();
			int c = color.getColor();
			if (f <= 0.625)
				return 0xffffff;
			else if (f >= 1)
				return c;
			return ReikaColorAPI.mixColors(c, 0xffffff, (f-0.625F)/0.375F);
		}

		@Override
		public boolean markAsFruitLeaf(ITreeGenome genome, World world, int x, int y, int z) {
			return true;
		}

		@Override
		public int getRipeningPeriod() {
			return 5;
		}

		@Override
		public ItemStack[] getProducts() {
			return new ItemStack[] {ChromaItems.BERRY.getStackOf(color)};
		}

		@Override
		public ItemStack[] getSpecialty() {
			return new ItemStack[0];
		}

		@Override
		public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
			float f = ripeningTime/(float)this.getRipeningPeriod();
			if (f >= 1)
				f += 0.05;
			double fnum = Math.min(12, Math.pow(f, 2)+ReikaRandomHelper.getRandomBetween(0, 5));
			int num = 0;
			while (fnum > 0) {
				if (fnum >= 1) {
					num++;
					fnum--;
				}
				else {
					if (ReikaRandomHelper.doWithChance(fnum))
						num++;
					fnum = 0;
				}
			}
			if (num <= 0)
				return new ItemStack[0];
			return new ItemStack[] {ReikaItemHelper.getSizedItemStack(ChromaItems.BERRY.getStackOf(color), num)};
		}

		/** This is in fact a locale key, and is automatically prepended with "for." */
		@Override
		public String getDescription() {
			return color.displayName;
		}

		@Override
		public String getDirectDescription() {
			return color.displayName+" Chroma Berries";
		}

		@Override
		public short getIconIndex(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime, boolean fancy) {
			return 1000;
		}

		@Override
		public boolean requiresFruitBlocks() {
			return false;
		}

		@Override
		public boolean trySpawnFruitBlock(ITreeGenome genome, World world, int x, int y, int z) {
			return false;
		}

		@Override
		public void registerIcons(IIconRegister register) {

		}

	}

	private static abstract class ChromaButterflySpecies extends BasicButterflySpecies {

		private ChromaButterflySpecies(String name, String uid, String sci) {
			super(name, uid, sci, "Reika", dyeFlyBranch);
		}

		@Override
		public final EnumSet<Type> getSpawnBiomes() {
			return EnumSet.of(Type.MAGICAL);
		}

		@Override
		public final boolean strictSpawnMatch() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return false;
		}

		@Override
		public final EnumTemperature getTemperature() {
			return EnumTemperature.NORMAL;
		}

		@Override
		public final EnumHumidity getHumidity() {
			return EnumHumidity.NORMAL;
		}

		@Override
		public float getFlightDistance() {
			return 0;
		}

		@Override
		public Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Territory getTerritorySize() {
			return Reika.DragonAPI.ModInteract.Bees.ButterflyAlleleRegistry.Territory.DEFAULT;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public final String getEntityTexture() {
			return DirectResourceManager.getResource("Reika/ChromatiCraft/Textures/Butterflies/"+this.getTextureName().toLowerCase(Locale.ENGLISH)+".png").toString();
		}

		protected abstract String getTextureName();

	}

	private static class DyeButterfly extends ChromaButterflySpecies {

		private final CrystalElement color;

		private DyeButterfly(CrystalElement e) {
			super(e.displayName+" Dye Leaflet", "chroma.dyefly"+e.name().toLowerCase(Locale.ENGLISH), "Pigmentum "+e.displayName);
			color = e;
		}

		@Override
		public boolean isFireproof() {
			return color == CrystalElement.ORANGE;
		}

		@Override
		public boolean isTolerantFlyer() {
			return color == CrystalElement.CYAN;
		}

		@Override
		public boolean isNocturnal() {
			return color == CrystalElement.BLUE;
		}

		@Override
		public float getRarity() {
			return 0.4F;
		}

		@Override
		public String getDescription() {
			return "Vibrantly glowing "+ReikaDyeHelper.dyes[color.ordinal()].colorName+", this butterfly seems to have adapted itself to "+color.displayName+".";
		}

		@Override
		protected String getTextureName() {
			return color.name();
		}

		@Override
		public int getMetabolism() {
			if (color == CrystalElement.LIGHTBLUE)
				return 5;
			if (color == CrystalElement.LIGHTGRAY)
				return 1;
			return 3;
		}

		@Override
		public int getTemperatureTolerance() {
			if (color == CrystalElement.GRAY)
				return 3;
			return 0;
		}

		@Override
		public int getHumidityTolerance() {
			if (color == CrystalElement.GRAY)
				return 3;
			return 0;
		}

		@Override
		public Tolerance getHumidityToleranceDir() {
			if (color == CrystalElement.GRAY)
				return Tolerance.BOTH;
			return Tolerance.NONE;
		}

		@Override
		public Tolerance getTemperatureToleranceDir() {
			if (color == CrystalElement.GRAY)
				return Tolerance.BOTH;
			return Tolerance.NONE;
		}

		@Override
		public Speeds getSpeed() {
			if (color == CrystalElement.LIME)
				return Speeds.FASTEST;
			return Speeds.NORMAL;
		}

		@Override
		public Size getSize() {
			return Size.AVERAGE;
		}

		@Override
		public Fertility getFertility() {
			if (color == CrystalElement.MAGENTA)
				return superFertility;
			return Fertility.NORMAL;
		}

		@Override
		public Life getLifespan() {
			if (color == CrystalElement.RED)
				return superLife;
			return Life.NORMAL;
		}

	}

	private static class RainbowButterfly extends ChromaButterflySpecies {

		private RainbowButterfly() {
			super("Shimmering Leaflet", "chroma.rainbowfly", "Pigmentum Pluralis");
		}

		@Override
		public float getRarity() {
			return 0.75F;
		}

		@Override
		public String getDescription() {
			return "Shining every color of the spectrum, there is clearly something special about this butterfly.";
		}

		@Override
		protected String getTextureName() {
			return "rainbow";
		}

		@Override
		public int getMetabolism() {
			return 2;
		}

		@Override
		public int getTemperatureTolerance() {
			return 0;
		}

		@Override
		public int getHumidityTolerance() {
			return 0;
		}

		@Override
		public Tolerance getHumidityToleranceDir() {
			return Tolerance.NONE;
		}

		@Override
		public Tolerance getTemperatureToleranceDir() {
			return Tolerance.NONE;
		}

		@Override
		public Speeds getSpeed() {
			return Speeds.NORMAL;
		}

		@Override
		public Size getSize() {
			return Size.LARGER;
		}

		@Override
		public Fertility getFertility() {
			return Fertility.HIGH;
		}

		@Override
		public Life getLifespan() {
			return Life.SHORT;
		}

	}

}
