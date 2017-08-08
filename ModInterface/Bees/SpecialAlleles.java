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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductProvider;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.CrystalBee;
import Reika.ChromatiCraft.ModInterface.Bees.ProductChecks.ProductCondition;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Auxiliary.ModularLogger;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.StatusLogger;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BasicFlowerProvider;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
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


public class SpecialAlleles {

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

	static final class CrystalEffect extends BasicGene implements IAlleleBeeEffect {

		public final CrystalElement color;
		private long lastWorldTick = -1;
		private long lastWorldTickClient = -1;

		public CrystalEffect(CrystalElement color) {
			super("effect.cavecrystal."+color.name().toLowerCase(Locale.ENGLISH), color.displayName+" Aura", EnumBeeChromosome.EFFECT);
			this.color = color;
		}

		@Override
		public boolean isCombinable() {
			return true;
		}

		@Override
		public IEffectData validateStorage(IEffectData ied) {
			return ied;
		}

		@Override
		public IEffectData doEffect(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				ChunkCoordinates c = ibh.getCoordinates();
				if (this.canApplyEffect(world, c.posX, c.posY, c.posZ)) {
					int[] r = ibg.getTerritory();
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					IEntitySelector s = null;
					Class ce = EntityLivingBase.class;
					switch(color) {
						case MAGENTA:
						case CYAN:
						case LIGHTBLUE:
						case ORANGE:
						case PINK:
						case LIME:
						case RED:
							s = ReikaEntityHelper.nonMobSelector;
							break;
						case GREEN:
							ce = EntityPlayer.class;
							break;
						case BLACK:
						case GRAY:
						case LIGHTGRAY:
							s = ReikaEntityHelper.hostileOrPlayerSelector;
							break;
						default:
							break;
					}
					List<EntityLivingBase> li = world.selectEntitiesWithinAABB(ce, box, s);
					for (EntityLivingBase e : li) {
						CrystalPotionController.applyEffectFromColor(600, 0, e, color, CrystalBees.rand.nextInt(240) == 0 && e.getDistanceSq(c.posX+0.5, c.posY+0.5, c.posZ+0.5) < 256);
					}
				}
				if (lastWorldTick != world.getTotalWorldTime() && CrystalBees.rand.nextInt(8000) == 0) {
					ChromaAux.spawnInteractionBallLightning(world, c.posX, c.posY, c.posZ, color);
				}
				lastWorldTick  = world.getTotalWorldTime();
			}
			return ied;
		}

		private boolean canApplyEffect(World world, int x, int y, int z) {
			if (color == CrystalElement.BLUE)
				return world.canBlockSeeTheSky(x, y+1, z);
			return true;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee == CrystalBees.multi || (bee instanceof CrystalBee && ((CrystalBee)bee).color == color);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				if (lastWorldTickClient != world.getTotalWorldTime()) {
					ChunkCoordinates c = ibh.getCoordinates();
					int delay = 12;
					ImmutablePair<CrystalElement, Integer> p = this.getActiveFXColor(world, delay);
					if (p.left == color) {
						int[] r = ibg.getTerritory();
						AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
						int n = (int)(Math.sqrt(ReikaAABBHelper.getVolume(box)/480D)/2D);
						for (int i = 0; i < n; i++) {
							double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
							double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
							double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
							float s = 1+CrystalBees.rand.nextFloat();
							EntityFX fx = new EntityRuneFX(world, px, py, pz, color).setGravity(0).setScale(s).setFading();
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
						int n2 = (int)(Math.pow(n*1.5, 1.5)/4D);
						for (int i = 0; i < n2; i++) {
							double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
							double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
							double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
							int s = CrystalBees.rand.nextInt(6);
							switch(s) {
								case 0:
									px = box.minX;
									break;
								case 1:
									px = box.maxX;
									break;
								case 2:
									py = box.minY;
									break;
								case 3:
									py = box.maxY;
									break;
								case 4:
									pz = box.minZ;
									break;
								case 5:
									pz = box.maxZ;
									break;
							}
							EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(color.getColor()).setScale(2).setIcon(ChromaIcons.FADE_RAY);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
						GuiScreen gui = Minecraft.getMinecraft().currentScreen;
						String cl = gui != null ? gui.getClass().getName().toLowerCase(Locale.ENGLISH) : "";
						boolean open = gui != null && (cl.contains("apiculture") || cl.contains("gendustry"));
						float vol = open ? 1.5F : 0.45F;
						if (world.getTotalWorldTime()%delay == 0) {
							int sound = p.right;
							if (sound == 0 || sound == 5) {
								for (int i = 0; i <= 3; i++) {
									float f = CrystalMusicManager.instance.getScaledDing(color, i);
									ReikaSoundHelper.playClientSound(ChromaSounds.DING, c.posX+0.5, c.posY+0.5, c.posZ+0.5, vol/3F, f);
								}
							}
							else {
								float f = CrystalMusicManager.instance.getScaledDing(color, sound-1);
								ReikaSoundHelper.playClientSound(ChromaSounds.DING, c.posX+0.5, c.posY+0.5, c.posZ+0.5, vol, f);
							}
						}
					}
					lastWorldTickClient  = world.getTotalWorldTime();
				}
			}
			return ied;
		}

		private ImmutablePair<CrystalElement, Integer> getActiveFXColor(World world, int delay) {
			long tick = world.getTotalWorldTime()/delay;
			int steps = 6;
			int active = (int)((tick/steps)%16);
			int sound = (int)(tick%steps);
			return new ImmutablePair(CrystalElement.elements[active], sound);
		}

	}

	static class FlowerProviderMulti extends BasicFlowerProvider implements ConditionalProductProvider {

		static final ItemHashMap<ProductCondition> conditions = new ItemHashMap();

		private FlowerProviderMulti() {
			super(ChromaBlocks.RAINBOWLEAF.getBlockInstance(), 0, "Rainbow Leaves");
		}

		@Override
		public String getDescription() {
			return "Shimmering, multicolored leaves";
		}

		@Override
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Flower provider "+this.getDescription()+" affecting products "+li+" for "+bee1.getName()+"; map="+conditions);
			Iterator<ItemStack> it = li.iterator();
			while (it.hasNext()) {
				ItemStack is = it.next();
				ProductCondition c = conditions.get(is);
				ModularLogger.instance.log(CrystalBees.LOGGER_TAG, "Check for "+is.getDisplayName()+": "+c);
				if (c != null) {
					boolean flag = false;
					if (bee1.getUID().equals(bee2.getUID())) {
						if (bee1.getUID().equals(CrystalBees.multi.getUID())) {
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
			ItemStack[] ret = li.toArray(new ItemStack[li.size()]);
			return ret;
		}

		private boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
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

			if (!ChromaBeeHelpers.isBestPossibleBee(ibg) && CrystalBees.rand.nextInt(2) > 0)
				return false;

			if (CrystalBees.rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.DIMENSION.isPlayerAtStage(ep);
			}
			return false;
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
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			log.addStatus("Dimension Progression", ep != null && ProgressStage.DIMENSION.isPlayerAtStage(ep));
			for (ProductCondition p : this.getConditions().values()) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}

	static class FlowerProviderCrystal extends BasicFlowerProvider implements ConditionalProductProvider {

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
		public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
			IBeeGenome ibg = ((IBee)individual).getGenome();
			IAlleleBeeSpecies bee1 = ibg.getPrimary();
			IAlleleBeeSpecies bee2 = ibg.getSecondary();
			IBeeHousing ibh = (IBeeHousing)world.getTileEntity(x, y, z);
			ArrayList<ItemStack> li = ReikaJavaLibrary.makeListFromArray(products);
			ItemHashMap<ProductCondition> map = CrystalBees.productConditions.get(color);
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
							if (bee1.getUID().equals(CrystalBees.beeMap.get(color).getUID())) {
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

		private boolean areConditionalsAvailable(World world, int x, int y, int z, IBeeGenome ibg, IBeeHousing ibh) {
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

			if (!ChromaBeeHelpers.isBestPossibleBee(ibg) && CrystalBees.rand.nextInt(2) > 0)
				return false;

			if (CrystalBees.rand.nextInt(3) > 0)
				return true;
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			if (ep != null) {
				return ProgressStage.SHARDCHARGE.isPlayerAtStage(ep);
			}
			return false;
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
			EntityPlayer ep = world.func_152378_a(ibh.getOwner().getId());
			log.addStatus("Boosted Shard Progression", ep != null && ProgressStage.SHARDCHARGE.isPlayerAtStage(ep));
			for (ProductCondition p : this.getConditions().values()) {
				log.addStatus(p.getDescription(), p.check(world, x, y, z, ibg, ibh));
			}
		}

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}
}
