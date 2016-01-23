/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre.TieredOres;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.ModInterface.ItemColoredModInteract;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BasicFlowerProvider;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeSpecies.TraitsBee;
import Reika.DragonAPI.ModInteract.Bees.BeeTraits;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ForestryHandler.Combs;
import Reika.DragonAPI.ModInteract.ItemHandlers.OreBerryBushHandler.BerryTypes;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;

public class CrystalBees {

	private static final String LOGGER_TAG = "CrystalBees";

	private static final Random rand = new Random();

	protected static BasicBee protective;
	protected static BasicBee luminous;
	protected static BasicBee hostile;

	protected static BasicBee crystal;
	protected static BasicBee purity;

	protected static final EnumMap<CrystalElement, CrystalBee> beeMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, CrystalEffect> effectMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, CrystalAllele> flowerMap = new EnumMap(CrystalElement.class);
	protected static final EnumMap<CrystalElement, ItemHashMap<ProductCondition>> productConditions = new EnumMap(CrystalElement.class);

	static {
		ModularLogger.instance.addLogger(ChromatiCraft.instance, LOGGER_TAG);
	}

	public static void register() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement color = CrystalElement.elements[i];
			BeeTraits traits = CrystalBeeTypes.list[color.ordinal()].getTraits();
			CrystalBee bee = new CrystalBee(color, traits);
			CrystalEffect eff = new CrystalEffect(color);
			CrystalAllele flw = new CrystalAllele(color);
			effectMap.put(color, eff);
			flowerMap.put(color, flw);
			bee.register();
			beeMap.put(color, bee);
		}

		protective = new BasicBee("Protective", "Vitreus Auxilium", Speeds.SLOWER, Life.ELONGATED, Flowering.SLOWER, Fertility.NORMAL, Territory.DEFAULT, 0xFF5993);
		luminous = new BasicBee("Luminous", "Vitreus Lumens", Speeds.SLOW, Life.SHORTER, Flowering.SLOWER, Fertility.HIGH, Territory.DEFAULT, 0xBAEBFF);
		hostile = new BasicBee("Hostile", "Vitreus Inimicus", Speeds.SLOWEST, Life.SHORT, Flowering.SLOW, Fertility.LOW, Territory.DEFAULT, 0xFF6A00);

		crystal = new BasicBee("Crystalline", "Vitreus Crystallum", Speeds.NORMAL, Life.SHORTEST, Flowering.SLOWEST, Fertility.LOW, Territory.DEFAULT, 0x46A7FF);
		purity = new BasicBee("Pure", "Purus Mundi", Speeds.SLOWER, Life.NORMAL, Flowering.AVERAGE, Fertility.NORMAL, Territory.DEFAULT, 0xffffff);
		crystal.setCave();
		purity.setCave();

		protective.register();
		luminous.register();
		hostile.register();
		crystal.register();
		purity.register();

		addBreeding(CrystalElement.RED, CrystalElement.YELLOW, CrystalElement.ORANGE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GREEN, CrystalElement.LIME);
		addBreeding(CrystalElement.RED, CrystalElement.WHITE, CrystalElement.PINK);
		addBreeding(CrystalElement.RED, CrystalElement.BLUE, CrystalElement.PURPLE);
		addBreeding(CrystalElement.WHITE, CrystalElement.BLACK, CrystalElement.GRAY);
		addBreeding(CrystalElement.BLUE, CrystalElement.GREEN, CrystalElement.CYAN);
		addBreeding(CrystalElement.BLUE, CrystalElement.WHITE, CrystalElement.LIGHTBLUE);
		addBreeding(CrystalElement.WHITE, CrystalElement.GRAY, CrystalElement.LIGHTGRAY);
		addBreeding(CrystalElement.PINK, CrystalElement.PURPLE, CrystalElement.MAGENTA);

		addBreeding(purity, crystal, CrystalElement.WHITE);
		addBreeding(protective, hostile, CrystalElement.BLACK);
		addBreeding(protective, crystal, CrystalElement.RED);
		addBreeding(luminous, crystal, CrystalElement.BLUE);

		addBreeding("Rural", crystal, CrystalElement.BROWN);
		addBreeding("Industrious", crystal, CrystalElement.YELLOW);
		addBreeding("Tropical", hostile, CrystalElement.GREEN);

		protective.addBreeding("Heroic", crystal, 10);
		hostile.addBreeding("Demonic", crystal, 10);
		luminous.addBreeding("Ended", purity, 5);

		protective.addProduct(new ItemStack(Blocks.obsidian), 2);
		hostile.addProduct(new ItemStack(Items.gunpowder), 4);
		luminous.addProduct(new ItemStack(Items.glowstone_dust), 5);
		protective.addProduct(Combs.HONEY.getItem(), 10);
		hostile.addProduct(Combs.HONEY.getItem(), 10);
		luminous.addProduct(Combs.HONEY.getItem(), 10);

		GameRegistry.registerWorldGenerator(HiveGenerator.instance, -5);
	}

	protected static final CrystalBee getBeeFor(CrystalElement color) {
		return beeMap.get(color);
	}

	public static BeeSpecies getCrystalBee() {
		return crystal;
	}

	public static BeeSpecies getPureBee() {
		return purity;
	}

	public static BeeSpecies getElementalBee(CrystalElement e) {
		return beeMap.get(e);
	}

	private static final void addBreeding(String in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(BeeSpecies in1, BeeSpecies in2, CrystalElement out) {
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(in1, in2, 8);
	}

	private static final void addBreeding(CrystalElement in1, CrystalElement in2, CrystalElement out) {
		CrystalBee p1 = beeMap.get(in1);
		CrystalBee p2 = beeMap.get(in2);
		CrystalBee cb = beeMap.get(out);
		cb.addBreeding(p1, p2, 8);
	}

	private static final class BasicBee extends TraitsBee {

		public final int outline;

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color) {
			this(name, latin, s, l, f, f2, a, color, EnumTemperature.NORMAL);
		}

		private BasicBee(String name, String latin, Speeds s, Life l, Flowering f, Fertility f2, Territory a, int color, EnumTemperature t) {
			super(name, "bee."+name.toLowerCase(), latin, "Reika", new BeeTraits());
			traits.speed = s;
			traits.lifespan = l;
			traits.flowering = f;
			traits.fertility = f2;
			traits.area = a;
			traits.tempDir = Tolerance.NONE;
			traits.humidDir = Tolerance.NONE;
			traits.tempTol = 0;
			traits.humidTol = 0;
			traits.temperature = t;
			traits.humidity = EnumHumidity.NORMAL;
			traits.temperature = t;
			outline = color;
		}

		private BasicBee setCave() {
			traits.isCaveDwelling = true;
			traits.isNocturnal = true;
			return this;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees do nothing on their own, but perhaps they could be purified into something stronger.";
		}

		@Override
		public boolean hasEffect() {
			return false;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAllele getFlowerAllele() {
			return Flower.VANILLA.getAllele();
		}

		@Override
		public IAllele getEffectAllele() {
			return Effect.NONE.getAllele();
		}

		@Override
		public int getOutlineColor() {
			return outline;
		}

	}

	private static final class CrystalEffect extends BasicGene implements IAlleleBeeEffect {

		public final CrystalElement color;

		public CrystalEffect(CrystalElement color) {
			super("effect.cavecrystal."+color.name().toLowerCase(), color.displayName+" Crystal");
			this.color = color;
		}

		@Override
		public boolean isCombinable() {
			return false;
		}

		@Override
		public IEffectData validateStorage(IEffectData ied) {
			return null;
		}

		@Override
		public IEffectData doEffect(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (ibg.getPrimary() instanceof CrystalBee && ((CrystalBee)ibg.getPrimary()).color == color) {
				if (ibg.getSecondary() instanceof CrystalBee && ((CrystalBee)ibg.getSecondary()).color == color) {
					World world = ibh.getWorld();
					ChunkCoordinates c = ibh.getCoordinates();
					int[] r = ibg.getTerritory();
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
					for (EntityLivingBase e : li) {
						CrystalBlock.applyEffectFromColor(600, 0, e, color);
					}
				}
			}
			return null;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			return null;
		}

	}

	private static final class CrystalAllele extends BasicGene implements IAlleleFlowers {

		public final CrystalElement color;
		private final FlowerProviderCrystal provider;

		public CrystalAllele(CrystalElement color) {
			super("flower.cavecrystal."+color.name().toLowerCase(), color.displayName+" Cave Crystal");
			this.color = color;
			provider = new FlowerProviderCrystal(color);
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	public static class FlowerProviderCrystal extends BasicFlowerProvider {

		public final CrystalElement color;

		private FlowerProviderCrystal(CrystalElement color) {
			super(ChromaBlocks.CRYSTAL.getBlockInstance(), color.ordinal(), color.name().toLowerCase());
			this.color = color;
		}

		@Override
		public String getDescription() {
			return color.displayName+" Crystals";
		}

		@Override
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ItemHashMap<ProductCondition> map = productConditions.get(color);
			ModularLogger.instance.log(LOGGER_TAG, "Flower provider "+this.getDescription()+" affecting products "+li+" for "+bee1.getName()+"; map="+map);
			if (map != null) {
				Iterator<ItemStack> it = li.iterator();
				while (it.hasNext()) {
					ItemStack is = it.next();
					ProductCondition c = map.get(is);
					ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+": "+c);
					if (c != null) {
						boolean flag = false;
						if (bee1.getUID().equals(bee2.getUID())) {
							if (bee1.getUID().equals(beeMap.get(color).getUID())) {
								if (this.areConditionalsAvailable(world, x, y, z, ibg, ibh)) {
									if (c.check(world, x, y, z, ibg, ibh)) {
										ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" passed.");
										flag = true;
									}
								}
								else {
									ModularLogger.instance.log(LOGGER_TAG, "Conditionals unavailable. Removing.");
								}
							}
						}
						if (!flag) {
							ModularLogger.instance.log(LOGGER_TAG, "Check for "+is.getDisplayName()+" failed. Removing.");
							it.remove();
						}
					}
				}
			}
			ItemStack[] ret = li.toArray(new ItemStack[li.size()]);
			return ret;
		}

		private boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderCrystal))
				return false;
			if (((FlowerProviderCrystal)ibg.getFlowerProvider()).color != color)
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
			}
			return false;
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	private static final class CrystalBee extends BeeSpecies {

		public final CrystalElement color;
		public final Speeds speed;
		public final Fertility fertility;
		public final Flowering flowering;
		public final Life lifespan;
		public final Territory area;
		public final Tolerance tempDir;
		public final Tolerance humidDir;
		public final int tempTol;
		public final int humidTol;
		public final EnumTemperature temperature;
		public final EnumHumidity humidity;

		public CrystalBee(CrystalElement color, BeeTraits traits) {
			super(color.displayName+" Crystal", "bee.crystal."+color.name().toLowerCase(), "Vitreus "+color.displayName, "Reika");
			this.color = color;
			speed = traits.speed;
			fertility = traits.fertility;
			flowering = traits.flowering;
			lifespan = traits.lifespan;
			area = traits.area;
			tempDir = traits.tempDir;
			humidDir = traits.humidDir;
			tempTol = traits.tempTol;
			humidTol = traits.humidTol;
			temperature = traits.temperature;
			humidity = traits.humidity;

			this.addConditionalProduct(ChromaItems.BERRY.getStackOf(color), 25, false, new LeafCheck(color));
			this.addConditionalProduct(ItemColoredModInteract.ColoredModItems.COMB.getItem(color), 8, true, new CrystalPlantCheck(color));
			this.addProduct(ForestryHandler.Combs.HONEY.getItem(), 15);
			this.addConditionalProduct(ChromaOptions.isVanillaDyeMoreCommon() ? new ItemStack(Items.dye, 1, color.ordinal()) : ChromaItems.DYE.getStackOf(color), 20, false, new FlowerCheck(color));
			switch(color) {
				case BLACK:
					this.addConditionalProduct(ChromaStacks.auraDust, 5, true, new ProgressionCheck(TieredPlants.FLOWER.level));
					break;
				case GREEN:
					this.addSpecialty(ForestryHandler.Combs.SILKY.getItem(), 10);
					break;
				case PURPLE:
					if (ModList.TINKERER.isLoaded())
						this.addSpecialty(BerryTypes.XP.getStack(), 3);
					break;
				case BROWN: {
					ArrayList<ItemStack> li = OreDictionary.getOres("nuggetIron");
					if (!li.isEmpty())
						this.addSpecialty(li.get(0), 5);
					break;
				}
				case ORANGE:
					this.addSpecialty(new ItemStack(Items.blaze_powder), 10);
					break;
				case BLUE:
					this.addConditionalProduct(ChromaStacks.beaconDust, 5, true, new ProgressionCheck(TieredPlants.DESERT.level));
					break;
				case YELLOW:
					this.addSpecialty(new ItemStack(Items.redstone), 10);
					break;
				case WHITE:
					this.addConditionalProduct(ChromaStacks.purityDust, 5, true, new ProgressionCheck(TieredPlants.CAVE.level));
					break;
				case CYAN:
					this.addConditionalProduct(ChromaStacks.waterDust, 5, true, new ProgressionCheck(TieredOres.WATERY.level));
					break;
				case LIME:
					this.addConditionalProduct(ChromaStacks.spaceDust, 5, true, new ProgressionCheck(TieredOres.END2.level));
					break;
				default:
					break;
			}
		}

		private void addConditionalProduct(ItemStack is, int chance, boolean specialty, ProductCondition c) {
			if (specialty)
				this.addSpecialty(is, chance);
			else
				this.addProduct(is, chance);
			ItemHashMap<ProductCondition> map = productConditions.get(color);
			if (map == null) {
				map = new ItemHashMap();
				productConditions.put(color, map);
			}
			map.put(is, c);
		}

		@Override
		public boolean isNocturnal() {
			return true;
		}

		@Override
		public boolean isJubilant(IBeeGenome ibg, IBeeHousing ibh) {
			World world = ibh.getWorld();
			int x = ibh.getCoordinates().posX;
			int y = ibh.getCoordinates().posY;
			int z = ibh.getCoordinates().posZ;
			if (!(ibg.getFlowerProvider() instanceof FlowerProviderCrystal))
				return false;
			if (((FlowerProviderCrystal)ibg.getFlowerProvider()).color != color)
				return false;
			if (rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "These bees seem to enjoy the magic aura of the cave crystals. " +
					"So much so, in fact, that they will only thrive around their corresponding color.";
		}

		@Override
		public EnumTemperature getTemperature() {
			return temperature;
		}

		@Override
		public EnumHumidity getHumidity() {
			return humidity;
		}

		@Override
		public boolean hasEffect() {
			return true;
		}

		@Override
		public boolean isSecret() {
			return false;
		}

		@Override
		public boolean isCounted() {
			return false;
		}

		@Override
		public boolean isDominant() {
			return true;
		}

		@Override
		public IAllele getFlowerAllele() {
			return flowerMap.get(color);
		}

		@Override
		public Speeds getProductionSpeed() {
			return speed;
		}

		@Override
		public Fertility getFertility() {
			return fertility;
		}

		@Override
		public Flowering getFloweringRate() {
			return flowering;
		}

		@Override
		public Life getLifespan() {
			return lifespan;
		}

		@Override
		public Territory getTerritorySize() {
			return area;
		}

		@Override
		public boolean isCaveDwelling() {
			return true;
		}

		@Override
		public int getTemperatureTolerance() {
			return tempTol;
		}

		@Override
		public int getHumidityTolerance() {
			return humidTol;
		}

		@Override
		public Tolerance getHumidityToleranceDir() {
			return humidDir;
		}

		@Override
		public Tolerance getTemperatureToleranceDir() {
			return tempDir;
		}

		@Override
		public int getOutlineColor() {
			return color.getJavaColor().getRGB();
		}

		@Override
		public IAllele getEffectAllele() {
			return effectMap.get(color);
		}

	}

	private static abstract class ProductCondition {

		public abstract boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh);

	}

	private static class ProgressionCheck extends ProductCondition {

		private final ProgressStage progress;

		private ProgressionCheck(ProgressStage p) {
			progress = p;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			return ep != null && progress.isPlayerAtStage(ep);
		}

	}

	private static class CrystalPlantCheck extends ProductCondition {

		private final CrystalElement color;

		private CrystalPlantCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.PLANT.getBlockInstance(), color.ordinal());
		}
	}

	private static class FlowerCheck extends ProductCondition {

		private final CrystalElement color;

		private FlowerCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return ReikaWorldHelper.findNearBlock(world, x, y, z, r2, ChromaBlocks.DYEFLOWER.getBlockInstance(), color.ordinal());
		}
	}

	private static class LeafCheck extends ProductCondition {

		private final CrystalElement color;

		private LeafCheck(CrystalElement e) {
			color = e;
		}

		@Override
		public boolean check(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(ibh);
			int tr = (int)(ibg.getTerritory()[0]*3F*beeModifier.getTerritoryModifier(ibg, 1.0F)); //x, should == z; code from HasFlowersCache
			int r = MathHelper.clamp_int(16*ReikaMathLibrary.intpow2(2, (tr-9)/2), 16, 96);
			int r2 = r >= 64 ? 24 : r >= 32 ? 16 : r >= 16 ? 12 : 8;

			return this.findLeaf(world, x, y, z, r, r2);
		}

		private boolean findLeaf(World world, int x, int y, int z, int r, int vr) {
			int d = 2;
			boolean last = false;
			for (int i = -r; i <= r; i += d) {
				for (int k = -r; k <= r; k += d) {
					for (int h = -vr; h <= vr; h += d) {
						int dx = x+i;
						int dy = y+h;
						int dz = z+k;
						Block b = world.getBlock(dx, dy, dz);
						if (b instanceof BlockDyeLeaf && world.getBlockMetadata(dx, dy, dz) == color.ordinal()) {
							if (last)
								return true;
							else
								last = true;
						}
						else
							last = false;
					}
				}
			}
			return false;
		}

	}

}
