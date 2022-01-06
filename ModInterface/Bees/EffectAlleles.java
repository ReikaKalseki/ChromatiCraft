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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Block.Worldgen.BlockSparkle;
import Reika.ChromatiCraft.Block.Worldgen.BlockSparkle.BlockTypes;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.Magic.RainbowTreeEffects;
import Reika.ChromatiCraft.Magic.Artefact.ArtefactSpawner;
import Reika.ChromatiCraft.Magic.Artefact.UABombingEffects;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.CrystalBee;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees.PrecursorBee;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Math.Noise.Simplex3DGenerator;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.Note;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BasicGene;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IEffectData;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IAlvearyController;
import forestry.api.multiblock.IMultiblockComponent;


public class EffectAlleles {

	private static abstract class ChromaBeeEffect extends BasicGene implements IAlleleBeeEffect {

		protected ChromaBeeEffect(String uid, String name) {
			super(uid, name, EnumBeeChromosome.EFFECT);
		}

	}

	private static abstract class ChromaTreeEffect extends BasicGene implements IAlleleLeafEffect {

		protected ChromaTreeEffect(String uid, String name) {
			super(uid, name, EnumTreeChromosome.EFFECT);
		}

	}

	static final class CrystalEffect extends ChromaBeeEffect {

		public final CrystalElement color;
		private long lastWorldTick = -1;
		private long lastWorldTickClient = -1;

		private static boolean spawnBallLightnings = true;

		public CrystalEffect(CrystalElement color) {
			super("effect.cavecrystal."+color.name().toLowerCase(Locale.ENGLISH), color.displayName+" Aura");
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
					int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, time);
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
					if (te != null && te.hasOmnipresence()) {
						EntityPlayer owner = te.getPlacer();
						if (owner != null && !ReikaPlayerAPI.isFake(owner))
							li.add(new WeakReference(owner));
					}
					boolean boost = te != null && te.isColorBoosted(color);
					int dur = boost ? 900 : 400;
					HashSet<Integer> ticked = new HashSet();
					for (WeakReference<EntityLivingBase> w : li) {
						EntityLivingBase e = w.get();
						if (e != null) {
							if (ticked.contains(e.getEntityId()))
								continue;
							ticked.add(e.getEntityId());
							CrystalPotionController.instance.applyEffectFromColor(dur, boost ? 1 : 0, e, color, CrystalBees.rand.nextInt(240) == 0 && e.getDistanceSq(c.posX+0.5, c.posY+0.5, c.posZ+0.5) < 256);
						}
					}
				}
				if (spawnBallLightnings && lastWorldTick != time && CrystalBees.rand.nextInt(8000) == 0) {
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
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				long time = world.getTotalWorldTime();
				if (lastWorldTickClient != time) {
					ChunkCoordinates c = ibh.getCoordinates();
					int delay = 12;
					ImmutablePair<CrystalElement, Integer> p = this.getActiveFXColor(world, delay);
					if (p.left == color) {
						int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, time);
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
								EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FADE_RAY).setColor(color.getColor()).setScale(2);
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

	static final class PolychromaEffect extends ChromaBeeEffect {

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
			super("effect.polychroma", "Polychromatic Aura");
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
				int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, time);
				RainbowTreeEffects.instance.doRainbowTreeEffects(world, c.posX, c.posY, c.posZ, 1, r[0]/16F, CrystalBees.rand, false);
				CrystalElement e = CrystalElement.randomElement();
				CrystalEffect.spawnBallLightnings = false;
				CrystalBees.effectMap.get(e).doEffect(ibg, ied, ibh);
				CrystalEffect.spawnBallLightnings = true;
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
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				long wtime = world.getTotalWorldTime();
				if (lastWorldTickClient != wtime) {
					ChunkCoordinates c = ibh.getCoordinates();

					//int steps = 4;
					//int chord = (int)((tick/steps)%chords.length);
					//int sound = (int)(tick%steps);

					int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, wtime);
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					int n = (int)(Math.sqrt(ReikaAABBHelper.getVolume(box)/480D)/2D);
					int n2 = (int)(Math.pow(n*1.5, 1.5)/4D);
					for (int i = 0; i < n2; i++) {
						double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
						double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
						double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
						if (ReikaWorldHelper.isPositionEmpty(world, px, py, pz)) {
							EntityCCFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, px, py, pz, 0, 90);
							fx.angleVelocity *= 2;
							fx.freedom *= 4;
							fx.setCyclingColor(0.2F).setScale(2+CrystalBees.rand.nextFloat());
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

	static final class RechargeEffect extends ChromaBeeEffect {

		public RechargeEffect() {
			super("effect.playerbuffer", "Lumen Balance");
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
				int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
				List<WeakReference<EntityLivingBase>> li = ChromaBeeHelpers.getEntityList(box, world.getTotalWorldTime(), world, c, EntityPlayer.class, null);
				TileEntityLumenAlveary te = ChromaBeeHelpers.getLumenAlvearyController(ibh, world, c);
				if (te != null && te.hasOmnipresence()) {
					EntityPlayer owner = te.getPlacer();
					if (owner != null && !ReikaPlayerAPI.isFake(owner))
						li.add(new WeakReference(owner));
				}
				for (WeakReference<EntityLivingBase> w : li) {
					EntityLivingBase e = w.get();
					if (e instanceof EntityPlayer && CrystalBees.rand.nextInt(36) == 0) {
						EntityPlayer ep = (EntityPlayer)e;
						if (ProgressStage.CHARGE.isPlayerAtStage(ep) && ProgressStage.USEENERGY.isPlayerAtStage(ep) && ProgressStage.INFUSE.isPlayerAtStage(ep)) {
							int max = -1;
							int min = Integer.MAX_VALUE;
							CrystalElement most = null;
							CrystalElement least = null;
							int cap = PlayerElementBuffer.instance.getElementCap(ep);
							for (int i = 0; i < 16; i++) {
								CrystalElement clr = CrystalElement.elements[i];
								int has = PlayerElementBuffer.instance.getPlayerContent(ep, clr);
								if (has < min) {
									least = clr;
									min = has;
								}
								if (has > max) {
									most = clr;
									max = has;
								}
							}
							int diff = max-min;
							if (max > 0 && min < cap/2 && diff > cap/4) {
								PlayerElementBuffer.instance.addToPlayer(ep, least, Math.max(1, Math.min(500, diff/2000)), false);
							}
						}
					}
				}
			}
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee == CrystalBees.aura;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
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

	static final class ChromaEffect extends ChromaBeeEffect {

		public ChromaEffect() {
			super("effect.pylonrecharge", "Power Aura");
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
				int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());
				ArrayList<TileEntityCrystalPylon> li = CrystalNetworker.instance.getAllNearbyPylons(world, c.posX, c.posY, c.posZ, r[0], true);
				if (li != null && !li.isEmpty()) {
					li.get(CrystalBees.rand.nextInt(li.size())).speedRegenShortly(8);
				}
			}
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee == CrystalBees.chroma;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				ChunkCoordinates c = ibh.getCoordinates();

				Random rand = CrystalBees.rand;
				if (ibh instanceof IAlvearyComponent) {
					ibh = ((IAlvearyComponent)ibh).getMultiblockLogic().getController();
				}
				if (ibh instanceof IAlvearyController) {
					IAlvearyController iac = (IAlvearyController)ibh;
					Collection<IMultiblockComponent> li = iac.getComponents();
					IMultiblockComponent imc = ReikaJavaLibrary.getRandomCollectionEntry(rand, li);
					c = imc.getCoordinates();
				}
				int x = c.posX;
				int y = c.posY;
				int z = c.posZ;
				double[] v = ReikaPhysicsHelper.polarToCartesian(ReikaRandomHelper.getRandomBetween(0.03125, 0.125), rand.nextDouble()*90, rand.nextDouble()*360);
				EntityFX fx = new EntityChromaFluidFX(world, x+rand.nextDouble(), y+rand.nextDouble()*3, z+rand.nextDouble(), v[0], v[1], v[2]).setGravity(0.125F);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			return ied;
		}
	}

	static final class PrecursorEffect extends ChromaBeeEffect {

		public PrecursorEffect() {
			super("effect.precursor", "Ancient Knowledge");
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
			/*
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				ChunkCoordinates c = ibh.getCoordinates();
				if (world.rand.nextInt(200) == 0) {
					int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());

					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					List<WeakReference<EntityLivingBase>> li = ChromaBeeHelpers.getEntityList(box, world.getTotalWorldTime(), world, c, EntityPlayer.class, null);
					for (WeakReference<EntityLivingBase> w : li) {
						EntityLivingBase e = w.get();
						if (e instanceof EntityPlayer) {

						}
					}
				}
			}*/
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee instanceof PrecursorBee;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				if (world.rand.nextInt(4) == 0) {
					ChunkCoordinates c = ibh.getCoordinates();

					double px = ReikaRandomHelper.getRandomPlusMinus(c.posX+0.5, 2);
					double pz = ReikaRandomHelper.getRandomPlusMinus(c.posZ+0.5, 2);
					double py = ReikaRandomHelper.getRandomBetween(c.posY-1.5, c.posY+4);

					Towers t1 = Towers.towerList[(int)((world.getTotalWorldTime()/1200)%Towers.towerList.length)];
					if (t1.getRootPosition() != null) {
						double dx = t1.getRootPosition().chunkXPos-c.posX-0.5;
						double dz = t1.getRootPosition().chunkZPos-c.posZ-0.5;
						double a = -ReikaPhysicsHelper.cartesianToPolarFast(dx, 0, dz)[2]-90;
						float s = world.rand.nextFloat()+0.25F;
						EntityCCFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, px, py, pz, a, 0);
						fx.freedom *= 0.5;
						fx.setIcon(ChromaIcons.FADE).setColor(0xa0e0ff).setLife(120).setScale(s);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
			}
			return ied;
		}
	}

	static final class ArtefactEffect extends ChromaBeeEffect {

		public ArtefactEffect() {
			super("effect.artefact", "Mysterious Aura");
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
				if (world.rand.nextInt(500) == 0) {
					int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());
					TileEntityLumenAlveary te = ChromaBeeHelpers.getLumenAlvearyController(ibh, world, c);
					AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(c.posX, c.posY, c.posZ).expand(r[0], r[1], r[2]);
					List<WeakReference<EntityLivingBase>> li = ChromaBeeHelpers.getEntityList(box, world.getTotalWorldTime(), world, c, EntityPlayer.class, null);
					for (WeakReference<EntityLivingBase> w : li) {
						EntityLivingBase e = w.get();
						if (e instanceof EntityPlayer) {
							if (world.rand.nextInt(4) == 0) {
								if (te == null || !te.effectsOnlyOnPlayers())
									UABombingEffects.instance.trigger(e);
							}
							if (world.rand.nextInt(100) == 0) {
								int dx = ReikaRandomHelper.getRandomPlusMinus(c.posX, r[0]);
								int dz = ReikaRandomHelper.getRandomPlusMinus(c.posZ, r[2]);
								ArtefactSpawner.instance.addArtefact(dx, dz, (EntityPlayer)e, 72000);
							}
						}
					}
				}
			}
			return ied;
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return bee instanceof PrecursorBee;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				ChunkCoordinates c = ibh.getCoordinates();
				double d = Minecraft.getMinecraft().thePlayer.getDistanceSq(c.posX+0.5, c.posY+0.5, c.posZ+0.5);
				if (d < 4096) {
					double f = 1;
					if (d >= 1024)
						f = 4*(1-d/4096);
					if (f >= 1 || ReikaRandomHelper.doWithChance(f))
						ItemUnknownArtefact.doUA_FX(world, c.posX+0.5, c.posY+0.5, c.posZ+0.5, d <= 256);
				}
			}
			return ied;
		}
	}

	static final class SparklifyEffect extends ChromaBeeEffect {

		private Simplex3DGenerator sparkleNoise;

		public SparklifyEffect() {
			super("effect.sparkle", "Glittering");
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
				if (world.rand.nextInt(200) == 0) {
					int[] r = ReikaBeeHelper.getEffectiveTerritory(ibh, c, ibg, world.getTotalWorldTime());
					BlockBox box = BlockBox.block(c).expand(r[0], r[1], r[2]);
					Coordinate loc = box.getRandomContainedCoordinate(world.rand);
					if (this.canSparklify(world, loc)) {
						Block b = loc.getBlock(world);
						BlockTypes type = BlockSparkle.getByProxy(b);
						if (type != null) {
							loc.setBlock(world, ChromaBlocks.SPARKLE.getBlockInstance(), type.ordinal());
							ReikaSoundHelper.playBreakSound(world, loc.xCoord, loc.yCoord, loc.zCoord, b);
						}
					}
				}
			}
			return ied;
		}

		private boolean canSparklify(World world, Coordinate loc) {
			if (sparkleNoise == null || sparkleNoise.seed != world.getSeed()) {
				sparkleNoise = new Simplex3DGenerator(world.getSeed());
				sparkleNoise.clampEdge = true;
				sparkleNoise.setFrequency(0.125);
				sparkleNoise.addOctave(1.75, 0.25, 0.65);
				sparkleNoise.addOctave(3, 0.125, 0.2);
			}
			return sparkleNoise.getValue(loc.xCoord, loc.yCoord, loc.zCoord) > 0 && ReikaWorldHelper.isExposedToAir(world, loc.xCoord, loc.yCoord, loc.zCoord);
		}

		private boolean isValidBeeForEffect(IAlleleBeeSpecies bee) {
			return true;//bee == CrystalBees.sparkle;
		}

		@Override
		public IEffectData doFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				return this.doClientFX(ibg, ied, ibh);
			return ied;
		}

		@SideOnly(Side.CLIENT)
		private IEffectData doClientFX(IBeeGenome ibg, IEffectData ied, IBeeHousing ibh) {
			if (this.isValidBeeForEffect(ibg.getPrimary()) && this.isValidBeeForEffect(ibg.getSecondary())) {
				World world = ibh.getWorld();
				if (world.rand.nextInt(4) == 0) {
					ChunkCoordinates c = ibh.getCoordinates();

					double px = ReikaRandomHelper.getRandomPlusMinus(c.posX+0.5, 2);
					double pz = ReikaRandomHelper.getRandomPlusMinus(c.posZ+0.5, 2);
					double py = ReikaRandomHelper.getRandomBetween(c.posY-1.5, c.posY+4);

					EntityFlareFX fx = new EntityFlareFX(CrystalElement.WHITE, world, px, py, pz);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
			return ied;
		}
	}

	static final class RainbowEffect extends ChromaTreeEffect {

		RainbowEffect() {
			super("effect.rainbowleaf", "Protective Aura");
		}

		@Override
		public IEffectData doEffect(ITreeGenome genome, IEffectData storedData, World world, int x, int y, int z) {
			if (world.rand.nextInt(20) > 0)
				return storedData;
			double r = Math.min(4, 0.125*Math.pow(1.25*genome.getHeight()*genome.getGirth(), 2));
			RainbowTreeEffects.instance.doRainbowTreeEffects(world, x, y, z, 1, r, world.rand, false);
			return storedData;
		}

		@Override
		public boolean isCombinable() {
			return false;
		}

		@Override
		public IEffectData validateStorage(IEffectData storedData) {
			return storedData;
		}

	}
}
