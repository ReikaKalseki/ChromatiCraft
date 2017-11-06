/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RainbowTreeEffects;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.Artefact.ArtefactSpawner;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffects;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductProvider;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.CrystalBee;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.PrecursorBee;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.StatusLogger;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BasicFlowerProvider;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;


public class FlowerAlleles {

	static final class CrystalAllele extends BasicGene implements IAlleleFlowers {

		public final CrystalElement color;
		private final FlowerProviderCrystal provider;

		public CrystalAllele(CrystalElement color) {
			super("flower.cavecrystal."+color.name().toLowerCase(Locale.ENGLISH), color.displayName, EnumBeeChromosome.FLOWER_PROVIDER);
			this.color = color;
			provider = new FlowerProviderCrystal(color);
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	static final class MultiAllele extends BasicGene implements IAlleleFlowers {

		private final FlowerProviderMulti provider;

		public MultiAllele() {
			super("flower.rainbowleaf", "Rainbow Leaves", EnumBeeChromosome.FLOWER_PROVIDER);
			provider = new FlowerProviderMulti();
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	static final class MetaAlloyAllele extends BasicGene implements IAlleleFlowers {

		private final MetaAlloyFlowers provider;

		public MetaAlloyAllele() {
			super("flower.metaalloy", ChromaBlocks.METAALLOYLAMP.getBasicName(), EnumBeeChromosome.FLOWER_PROVIDER);
			provider = new MetaAlloyFlowers();
		}

		@Override
		public IFlowerProvider getProvider() {
			return provider;
		}
	}

	static class MetaAlloyFlowers extends ConditionalProductFlowerProvider implements ConditionalProductProvider {

		static final ItemHashMap<ProductCondition> conditions = new ItemHashMap();

		private MetaAlloyFlowers() {
			super(ChromaBlocks.METAALLOYLAMP.getBlockInstance(), 1, ChromaBlocks.METAALLOYLAMP.getBasicName());
		}

		@Override
		public String getDescription() {
			return "Mysterious glowing pods";
		}

		@Override
		protected boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!this.matchFlowerGene(ibg))
				return false;
			if (CrystalBees.rand.nextFloat() > ibg.getSpeed()/4)
				return false;
			if (ibg.getFlowering() < CrystalBees.superFlowering.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (CrystalBees.rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (CrystalBees.rand.nextInt(2) > 0 && !ChromaBeeHelpers.isBestPossibleBee(ibg))
				return false;

			return true;
		}

		private boolean matchFlowerGene(IBeeGenome ibg) {
			return ibg.getFlowerProvider() instanceof FlowerProviderMulti;
		}

		@Override
		public ItemHashMap<ProductCondition> getConditions() {
			return conditions;
		}

		@Override
		public ArrayList<String> getGeneralRequirements() {
			ArrayList<String> li = new ArrayList();
			li.add("Unmodified flower allele");
			li.add("'"+CrystalBees.superFlowering.getAllele().getName()+"' flowering");
			li.add("Ambient temperature between 8C and 32C");
			li.add("50% boost from Rainbow Forest");
			li.add("50% boost from genetic superiority");
			li.add("33% boost from '"+ProgressStage.DIMENSION.getTitle()+"' progression");
			li.add("Linear gains from faster production speeds");
			return li;
		}

		@Override
		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh) {
			log.addStatus("Flower Allele", this.matchFlowerGene(ibg));
			log.addStatus("Flowering Level", ibg.getFlowering() >= CrystalBees.superFlowering.getAllele().getValue());
			log.addStatus("Temperature", ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)));
			log.addStatus("Rainbow Forest", ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z)));
			log.addStatus("Gene Superiority", ChromaBeeHelpers.isBestPossibleBee(ibg));
			log.addStatus("Dimension Progression", ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.DIMENSION));
			for (ProductCondition p : this.getConditions().values()) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		@Override
		protected boolean isBeeAllowed(String uid) {
			return uid.equals(CrystalBees.precursor) || uid.equals(CrystalBees.ua) || uid.equals(CrystalBees.tower);
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	static class FlowerProviderMulti extends ConditionalProductFlowerProvider implements ConditionalProductProvider {

		static final ItemHashMap<ProductCondition> conditions = new ItemHashMap();

		private FlowerProviderMulti() {
			super(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), 0, "Rainbow Leaves");
		}

		@Override
		public String getDescription() {
			return "Shimmering, multicolored leaves";
		}

		@Override
		protected boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!this.matchFlowerGene(ibg))
				return false;
			if (CrystalBees.rand.nextFloat() > ibg.getSpeed()/4)
				return false;
			if (ibg.getFlowering() < CrystalBees.superFlowering.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (CrystalBees.rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (CrystalBees.rand.nextInt(2) > 0 && !ChromaBeeHelpers.isBestPossibleBee(ibg))
				return false;

			return CrystalBees.rand.nextInt(3) > 0 || ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.DIMENSION);
		}

		private boolean matchFlowerGene(IBeeGenome ibg) {
			return ibg.getFlowerProvider() instanceof FlowerProviderMulti;
		}

		@Override
		public ItemHashMap<ProductCondition> getConditions() {
			return conditions;
		}

		@Override
		public ArrayList<String> getGeneralRequirements() {
			ArrayList<String> li = new ArrayList();
			li.add("Unmodified flower allele");
			li.add("'"+CrystalBees.superFlowering.getAllele().getName()+"' flowering");
			li.add("Ambient temperature between 8C and 32C");
			li.add("50% boost from Rainbow Forest");
			li.add("50% boost from genetic superiority");
			li.add("33% boost from '"+ProgressStage.DIMENSION.getTitle()+"' progression");
			li.add("Linear gains from faster production speeds");
			return li;
		}

		@Override
		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh) {
			log.addStatus("Flower Allele", this.matchFlowerGene(ibg));
			log.addStatus("Flowering Level", ibg.getFlowering() >= CrystalBees.superFlowering.getAllele().getValue());
			log.addStatus("Temperature", ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)));
			log.addStatus("Rainbow Forest", ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z)));
			log.addStatus("Gene Superiority", ChromaBeeHelpers.isBestPossibleBee(ibg));
			log.addStatus("Dimension Progression", ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.DIMENSION));
			for (ProductCondition p : this.getConditions().values()) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		@Override
		protected boolean isBeeAllowed(String uid) {
			return uid.equals(CrystalBees.multi.getUID());
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	static class FlowerProviderCrystal extends ConditionalProductFlowerProvider {

		public final CrystalElement color;

		private FlowerProviderCrystal(CrystalElement color) {
			super(ChromaBlocks.CRYSTAL.getBlockInstance(), color.ordinal(), color.name().toLowerCase(Locale.ENGLISH));
			this.color = color;
		}

		@Override
		public String getDescription() {
			return color.displayName;
		}

		@Override
		protected boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
			if (!this.matchFlowerGene(ibg))
				return false;
			if (CrystalBees.rand.nextFloat() > ibg.getSpeed())
				return false;
			if (ibg.getFlowering() < Flowering.AVERAGE.getAllele().getValue())
				return false;
			if (!ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z))) {
				if (CrystalBees.rand.nextInt(2) > 0) {
					return false;
				}
			}
			if (!ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)))
				return false;

			if (CrystalBees.rand.nextInt(2) > 0 && !ChromaBeeHelpers.isBestPossibleBee(ibg))
				return false;

			return CrystalBees.rand.nextInt(3) > 0 || ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.SHARDCHARGE);
		}

		private boolean matchFlowerGene(IBeeGenome ibg) {
			return ibg.getFlowerProvider() instanceof FlowerProviderCrystal && ((FlowerProviderCrystal)ibg.getFlowerProvider()).color == color;
		}

		@Override
		public ItemHashMap<ProductCondition> getConditions() {
			return CrystalBees.productConditions.get(color);
		}

		@Override
		public ArrayList<String> getGeneralRequirements() {
			ArrayList<String> li = new ArrayList();
			li.add("Unmodified flower allele");
			li.add("'"+Flowering.AVERAGE.getAllele().getName()+"' flowering");
			li.add("Ambient temperature between 8C and 32C");
			li.add("50% boost from Rainbow Forest");
			li.add("50% boost from genetic superiority");
			li.add("33% boost from '"+ProgressStage.SHARDCHARGE.getTitle()+"' progression");
			li.add("Linear gains from faster production speeds");
			return li;
		}

		@Override
		public void sendStatusInfo(World world, int x, int y, int z, StatusLogger log, IBeeGenome ibg, IBeeHousing ibh) {
			log.addStatus("Flower Allele", this.matchFlowerGene(ibg));
			log.addStatus("Flowering Level", ibg.getFlowering() >= Flowering.AVERAGE.getAllele().getValue());
			log.addStatus("Temperature", ReikaMathLibrary.isValueInsideBoundsIncl(8, 32, ReikaWorldHelper.getAmbientTemperatureAt(world, x, y, z)));
			log.addStatus("Rainbow Forest", ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(x, z)));
			log.addStatus("Gene Superiority", ChromaBeeHelpers.isBestPossibleBee(ibg));
			log.addStatus("Boosted Shard Progression", ChromaBeeHelpers.checkProgression(world, ibh, ProgressStage.SHARDCHARGE));
			for (ProductCondition p : this.getConditions().values()) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		@Override
		protected boolean isBeeAllowed(String uid) {
			return uid.equals(CrystalBees.beeMap.get(color).getUID());
		}

	}

	static abstract class ConditionalProductFlowerProvider extends BasicFlowerProvider implements ConditionalProductProvider {

		protected ConditionalProductFlowerProvider(Block b, int meta, String name) {
			super(b, meta, name);
		}

		@Override
		public final ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ItemHashMap<ProductCondition> map = this.getConditions();
			ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Flower provider "+this.getDescription()+" affecting products "+li+" for "+bee1.getName()+"; map="+map);
			if (map != null) {
				Iterator<ItemStack> it = li.iterator();
				while (it.hasNext()) {
					ItemStack is = it.next();
					ProductCondition c = map.get(is);
					ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Check for "+is.getDisplayName()+": "+c);
					if (c != null) {
						boolean flag = false;
						if (bee1.getUID().equals(bee2.getUID())) {
							if (this.isBeeAllowed(bee1.getUID())) {
								if (this.areConditionalsAvailable(world, x, y, z, ibg, ibh)) {
									ibh.getErrorLogic().setCondition(false, CrystalBees.conditionalsUnavailable);
									if (c.check(world, x, y, z, ibg, ibh)) {
										ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Check for "+is.getDisplayName()+" passed.");
										flag = true;
									}
								}
								else {
									ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Conditionals unavailable. Removing.");
									ibh.getErrorLogic().setCondition(true, CrystalBees.conditionalsUnavailable);
								}
							}
						}
						if (!flag) {
							ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Check for "+is.getDisplayName()+" failed. Removing.");
							it.remove();
						}
					}
				}
			}
			ItemStack[] ret = li.toArray(new ItemStack[li.size()]);
			return ret;
		}

		protected abstract boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh);
		protected abstract boolean isBeeAllowed(String uid);

	}
}
