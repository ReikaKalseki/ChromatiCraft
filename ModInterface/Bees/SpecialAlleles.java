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
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductProvider;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.CrystalBee;
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
				long time = world.getTotalWorldTime();
				if (this.canApplyEffect(world, c.posX, c.posY, c.posZ)) {
					int[] r = ChromaBeeHelpers.getEffectiveTerritory(ibh, c, ibg, time);
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					IEntitySelector s = null;
					TileEntityLumenAlveary te = ChromaBeeHelpers.getLumenAlvearyController(ibh, world, c);
					Class ce = te != null && te.effectsOnlyOnPlayers() ? EntityPlayer.class : EntityLivingBase.class;
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
						case PURPLE:
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
					List<WeakReference<EntityLivingBase>> li = ChromaBeeHelpers.getEntityList(box, time, world, c, ce, s);
					boolean boost = te != null && te.isColorBoosted(color);
					int dur = boost ? 900 : 400;
					for (WeakReference<EntityLivingBase> w : li) {
						EntityLivingBase e = w.get();
						if (e != null)
							CrystalPotionController.applyEffectFromColor(dur, boost ? 1 : 0, e, color, CrystalBees.rand.nextInt(240) == 0 && e.getDistanceSq(c.posX+0.5, c.posY+0.5, c.posZ+0.5) < 256);
					}
				}
				if (lastWorldTick != time && CrystalBees.rand.nextInt(8000) == 0) {
					ChromaAux.spawnInteractionBallLightning(world, c.posX, c.posY, c.posZ, color);
				}
				lastWorldTick  = time;
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
				long time = world.getTotalWorldTime();
				if (lastWorldTickClient != time) {
					ChunkCoordinates c = ibh.getCoordinates();
					int delay = 12;
					ImmutablePair<CrystalElement, Integer> p = this.getActiveFXColor(world, delay);
					if (p.left == color) {
						int[] r = ChromaBeeHelpers.getEffectiveTerritory(ibh, c, ibg, time);
						AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
						int n = (int)(0.625*Math.sqrt(ReikaAABBHelper.getVolume(box)/480D)/2D);
						for (int i = 0; i < n; i++) {
							double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
							double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
							double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
							if (ReikaWorldHelper.isPositionEmpty(world, px, py, pz)) {
								float s = 1+CrystalBees.rand.nextFloat();
								EntityFX fx = new EntityRuneFX(world, px, py, pz, color).setGravity(0).setScale(s).setFading();
								Minecraft.getMinecraft().effectRenderer.addEffect(fx);
							}
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
							if (ReikaWorldHelper.isPositionEmpty(world, px, py, pz)) {
								EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(color.getColor()).setScale(2).setIcon(ChromaIcons.FADE_RAY);
								Minecraft.getMinecraft().effectRenderer.addEffect(fx);
							}
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
									this.playSound(c, box, vol/3F, f);
								}
							}
							else {
								float f = CrystalMusicManager.instance.getScaledDing(color, sound-1);
								this.playSound(c, box, vol, f);
							}
						}
					}
					lastWorldTickClient  = world.getTotalWorldTime();
				}
			}
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private void playSound(ChunkCoordinates c, AxisAlignedBB box, float vol, float f) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			ReikaSoundHelper.playClientSound(ChromaSounds.DING, c.posX+0.5, c.posY+0.5, c.posZ+0.5, vol, f, !ep.boundingBox.intersectsWith(box));

		}

		private ImmutablePair<CrystalElement, Integer> getActiveFXColor(World world, int delay) {
			long tick = world.getTotalWorldTime()/delay;
			int steps = 6;
			int active = (int)((tick/steps)%16);
			int sound = (int)(tick%steps);
			return new ImmutablePair(CrystalElement.elements[active], sound);
		}

	}

	static final class PolychromaEffect extends BasicGene implements IAlleleBeeEffect {

		private long lastWorldTick = -1;
		private long lastWorldTickClient = -1;

		//private int currentKey = 0;

		private MusicKey lastKey;
		private int currentChord;
		private long nextNoteTime;
		private int currentBarTime;

		private static final Random musicRand = new Random();

		private static final MusicKey[] chords = {
			MusicKey.C5, MusicKey.G4, MusicKey.A4, MusicKey.E4, MusicKey.F4, MusicKey.C4, MusicKey.F4, MusicKey.G4
		};

		private static final ArrayList<MusicKey>[] validNotes = new ArrayList[chords.length];

		private static final WeightedRandom<Integer> noteLengths = new WeightedRandom();

		static {
			for (int k = 0; k < chords.length; k++) {
				MusicKey cur = chords[k];
				validNotes[k] = new ArrayList();
				for (int i = 0; i <= 12; i++) {
					MusicKey key = cur.getInterval(i);
					KeySignature chordKey = cur.getNote() == Note.A || cur.getNote() == Note.E ? KeySignature.getByMinorTonic(cur) : KeySignature.getByTonic(cur);
					if (KeySignature.C.isNoteValid(key.getNote()) && chordKey.isNoteValid(key.getNote())) {
						validNotes[k].add(key);
					}
				}
			}

			noteLengths.addEntry(12, 100); //call this a quarter note, thus max 48 ticks per bar
			noteLengths.addEntry(6, 50);
			noteLengths.addEntry(3, 5);
			noteLengths.addEntry(24, 25);
			noteLengths.addEntry(36, 15);
			noteLengths.addEntry(48, 8);
			noteLengths.addEntry(18, 15);
		}

		public PolychromaEffect() {
			super("effect.polychroma", "Polychromatic Aura", EnumBeeChromosome.EFFECT);
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
				long time = world.getTotalWorldTime();
				ChunkCoordinates c = ibh.getCoordinates();
				int[] r = ChromaBeeHelpers.getEffectiveTerritory(ibh, c, ibg, time);
				RainbowTreeEffects.doRainbowTreeEffects(world, c.posX, c.posY, c.posZ, 1, r[0]/16F, CrystalBees.rand, false);
				CrystalElement e = CrystalElement.randomElement();
				CrystalBees.effectMap.get(e).doEffect(ibg, ied, ibh);
				lastWorldTick  = time;
			}
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee == CrystalBees.multi;
		}

		/*
		private MusicKey[] getChords(int chord) {
			MusicKey[] mk = new MusicKey[4];
			for (int i = 0; i < 4; i++) {
				int d = 0;
				switch(i) {
					case 1:
						d = (chord == 2 || chord == 3) ? 3 : 4;
						break;
					case 2:
						d = 7;
						break;
					case 3:
						d = 12;
				}
				mk[i] = chords[chord].getInterval(d+(chords[currentKey].ordinal()+2-MusicKey.C5.ordinal()));
			}
			return mk;
		}*/

		private boolean keyIsCurrentlyValid(MusicKey key) {
			if (lastKey == null)
				return true;
			int diff = key.ordinal()-lastKey.ordinal();
			if (diff > 12 || diff < -12) //nothing over an octave
				return false;
			int mod = diff%12;
			if (mod == 11) //7th
				return false;
			if (mod == 6) //tritone
				return false;
			return true;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				long wtime = world.getTotalWorldTime();
				if (lastWorldTickClient != wtime) {
					ChunkCoordinates c = ibh.getCoordinates();

					//int steps = 4;
					//int chord = (int)((tick/steps)%chords.length);
					//int sound = (int)(tick%steps);

					int[] r = ChromaBeeHelpers.getEffectiveTerritory(ibh, c, ibg, wtime);
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					int n = (int)(Math.sqrt(ReikaAABBHelper.getVolume(box)/480D)/2D);
					int n2 = (int)(Math.pow(n*1.5, 1.5)/4D);
					for (int i = 0; i < n2; i++) {
						double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
						double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
						double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
						if (ReikaWorldHelper.isPositionEmpty(world, px, py, pz)) {
							EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(world, px, py, pz, 0, 90);
							fx.angleVelocity *= 2;
							fx.freedom *= 4;
							fx.setScale(2+CrystalBees.rand.nextFloat()).setCyclingColor(0.2F);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
					GuiScreen gui = Minecraft.getMinecraft().currentScreen;
					String cl = gui != null ? gui.getClass().getName().toLowerCase(Locale.ENGLISH) : "";
					boolean open = gui != null && (cl.contains("apiculture") || cl.contains("gendustry"));
					float vol = open ? 0.3F : 0.125F;
					long time = System.currentTimeMillis();
					if (time >= nextNoteTime) {
						//MusicKey[] mk = this.getChords(chord);
						//float f = (float)mk[sound].getRatio(MusicKey.C5);
						musicRand.setSeed(wtime ^ 1965346947);
						noteLengths.setSeed(wtime ^ -1456904597);
						MusicKey key = this.randomKey(currentChord);
						float f = (float)key.getRatio(MusicKey.C5);
						this.playSound(c, box, vol, f);
						int len = Math.min(noteLengths.getRandomEntry(), 48-currentBarTime)*50*2; //50ms per tick
						nextNoteTime = time+len;
						currentBarTime += len;
						if (currentBarTime >= 48) {
							currentBarTime = 0;
							currentChord = (currentChord+1)%chords.length;
						}
						/*
						if (chord == chords.length-1 && sound == 3) {
							//currentKey = currentKey.getInterval(currentKey.getNote() == Note.E || currentKey.getNote() == Note.B ? 1 : 2);
							//if (currentKey.ordinal() >= MusicKey.D6.ordinal())
							//	currentKey = MusicKey.C5;
							currentKey = (currentKey+1)%chords.length;
						}
						 */
					}
					lastWorldTickClient = wtime;
				}
			}
			return ied;
		}

		private MusicKey randomKey(int chord) {
			MusicKey key = validNotes[chord].get(musicRand.nextInt(validNotes[chord].size()));
			while (!this.keyIsCurrentlyValid(key)) {
				key = validNotes[chord].get(musicRand.nextInt(validNotes[chord].size()));
			}
			lastKey = key;
			return key;
		}

		@SideOnly(Side.CLIENT)
		private void playSound(ChunkCoordinates c, AxisAlignedBB box, float vol, float f) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			boolean atten = !ep.boundingBox.intersectsWith(box);
			f *= 2;
			if (f > 1) {
				ReikaSoundHelper.playClientSound(ChromaSounds.DRONE_HI, c.posX+0.5, c.posY+0.5, c.posZ+0.5, vol, f, atten);
			}
			else {
				f *= 2;
				ReikaSoundHelper.playClientSound(ChromaSounds.DRONE, c.posX+0.5, c.posY+0.5, c.posZ+0.5, vol, f, atten);
			}
		}

	}

	static final class RechargeEffect extends BasicGene implements IAlleleBeeEffect {

		public RechargeEffect() {
			super("effect.pylonrecharge", "Lumen Boost", EnumBeeChromosome.EFFECT);
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
				int[] r = ChromaBeeHelpers.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());
				ArrayList<TileEntityCrystalPylon> li = CrystalNetworker.instance.getAllNearbyPylons(world, c.posX, c.posY, c.posZ, r[0], true);
				if (li != null && !li.isEmpty()) {
					li.get(CrystalBees.rand.nextInt(li.size())).speedRegenShortly(8);
				}
			}
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee == CrystalBees.aura;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				ChunkCoordinates c = ibh.getCoordinates();

				int n = 6+CrystalBees.rand.nextInt(6);

				for (int i = 0; i < n; i++) {
					double v = ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
					double[] xyz = ReikaPhysicsHelper.polarToCartesian(v, CrystalBees.rand.nextDouble()*360, CrystalBees.rand.nextDouble()*360);
					float s = ReikaRandomHelper.getRandomBetween(3, 6);

					double px = c.posX+0.5;
					double py = c.posY+0.5;
					double pz = c.posZ+0.5;

					CrystalElement e = CrystalElement.randomElement();

					EntityFX fx = new EntityLaserFX(e, world, px, py, pz, xyz[0], xyz[1], xyz[2]).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);

					fx = new EntityLaserFX(e, world, px, py, pz, -xyz[0], -xyz[1], -xyz[2]).setScale(s);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
			return ied;
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

		/*
		@Override
		public ItemStack[] getItemStacks() {
			return new ItemStack[]{new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, color.ordinal())};
		}
		 */

	}
}
