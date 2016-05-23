/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Event.PylonEvents.PlayerChargedFromPylonEvent;
import Reika.ChromatiCraft.Auxiliary.Event.PylonEvents.PylonDrainedEvent;
import Reika.ChromatiCraft.Auxiliary.Event.PylonEvents.PylonFullyChargedEvent;
import Reika.ChromatiCraft.Auxiliary.Event.PylonEvents.PylonRechargedEvent;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaOverlays;
import Reika.ChromatiCraft.Base.TileEntity.CrystalTransmitterBase;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Interfaces.ChargingPoint;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalReceiver;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalRepeater;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalTransmitter;
import Reika.ChromatiCraft.Magic.Interfaces.NaturalCrystalSource;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.MystPages;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBallLightningFX;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityChromaCrystal;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.ChunkManager;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.ChunkLoadingTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.RotaryCraft.TileEntities.Weaponry.TileEntityEMP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value = {"thaumcraft.api.nodes.INode", "thaumcraft.api.wands.IWandable"})
public class TileEntityCrystalPylon extends CrystalTransmitterBase implements NaturalCrystalSource, ChargingPoint, ChunkLoadingTile, INode, IWandable {

	private boolean hasMultiblock = false;
	private boolean enhanced = false;
	private CrystalElement color = CrystalElement.WHITE;
	public int randomOffset = rand.nextInt(360);
	public static final int MAX_ENERGY = 180000;
	public static final int MAX_ENERGY_ENHANCED = 900000;
	private int energy = MAX_ENERGY;
	private int energyStep = 1;
	private long lastWorldTick;
	private boolean forceLoad;

	public static final int RANGE = 48;

	public static boolean TUNED_PYLONS = true;

	public boolean enhancing = false;

	private static final Collection<Coordinate> crystalPositions = new ArrayList();

	private static Class node;
	private static HashMap<String, ArrayList<Integer>> nodeCache;

	static {
		if (ModList.THAUMCRAFT.isLoaded()) {
			try {
				node = Class.forName("thaumcraft.common.tiles.TileNode");
				Field f = node.getDeclaredField("locations");
				f.setAccessible(true);
				nodeCache = (HashMap<String, ArrayList<Integer>>)f.get(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		crystalPositions.add(new Coordinate(-3, -3, -1));
		crystalPositions.add(new Coordinate(-1, -3, -3));
		crystalPositions.add(new Coordinate(3, -3, -1));
		crystalPositions.add(new Coordinate(1, -3, -3));
		crystalPositions.add(new Coordinate(-3, -3, 1));
		crystalPositions.add(new Coordinate(-1, -3, 3));
		crystalPositions.add(new Coordinate(3, -3, 1));
		crystalPositions.add(new Coordinate(1, -3, 3));
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.PYLON;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == color;
	}

	@Override
	public boolean needsLineOfSight() {
		return true;
	}

	public CrystalElement getColor() {
		return color;
	}

	public int getEnergy(CrystalElement e) {
		return e == color ? energy : 0;
	}

	public int getRenderColor() {
		return ReikaColorAPI.mixColors(color.getColor(), 0x888888, (float)energy/this.getCapacity());
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		PylonGenerator.instance.cachePylon(this);
		if (ChromaOptions.PYLONLOAD.getState()) {
			if (forceLoad && this.getEnergy(color) < this.getMaxStorage(color)) {
				ChunkManager.instance.loadChunks(this);
			}
			else {
				forceLoad = false;
				this.unload();
			}
		}
		else {
			forceLoad = false;
			this.unload();
		}

		if (ModList.THAUMCRAFT.isLoaded() && nodeCache != null) {
			ArrayList li = new ArrayList();
			li.add(world.provider.dimensionId);
			li.add(x);
			li.add(y);
			li.add(z);
			nodeCache.put(this.getId(), li);
		}
	}

	private void forceLoading() {
		if (!forceLoad) {
			if (ChromaOptions.PYLONLOAD.getState()) {
				forceLoad = true;
				ChunkManager.instance.loadChunks(this);
			}
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (DragonAPICore.debugtest) {
			if (!hasMultiblock) {
				CrystalElement e = CrystalElement.randomElement();
				FilledBlockArray b = ChromaStructures.getPylonStructure(world, x, y-9, z, e);
				b.place();
				//world.setBlock(x, y+9, z, this.getTile().getBlock(), this.getTile().getBlockMetadata(), 3);
				//TileEntityCrystalPylon te = (TileEntityCrystalPylon)world.getTileEntity(x, y+9, z);
				color = e;
				hasMultiblock = true;
				this.syncAllData(true);
			}
		}

		if (!world.getBlock(x, y-1, z).isAir(world, x, y-1, z) && ReikaWorldHelper.isBlockEncased(world, x, y, z, null)) { //Someone attempting to jar
			this.doJarRejection(world, x, y, z);
			//ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONJAR.ordinal(), this, 128);
		}

		long diff = 1;//world.getTotalWorldTime()-lastWorldTick;
		lastWorldTick = world.getTotalWorldTime();

		if (hasMultiblock) {
			//ReikaJavaLibrary.pConsole(energy, Side.SERVER, color == CrystalElement.BLUE);

			int max = this.getCapacity();
			if (diff > 0) {
				this.charge(world, x, y, z, max, Math.max(1, (int)diff));
			}
			energy = Math.min(energy, max);

			if (world.isRemote) {
				this.spawnParticle(world, x, y, z);
			}

			if (!world.isRemote && rand.nextInt(80) == 0) {
				int r = 8+rand.nextInt(8);
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(r, r, r);
				List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : li) {
					boolean attack = !e.isDead && e.getHealth() > 0;
					if (e instanceof EntityPlayer) {
						EntityPlayer ep = (EntityPlayer)e;
						attack = attack && !ep.capabilities.isCreativeMode && !Chromabilities.PYLON.enabledOn(ep);
					}
					else if (e instanceof EntityBallLightning) {
						attack = ((EntityBallLightning)e).getElement() != color;
					}
					if (attack) {
						this.attackEntity(e);
						this.sendClientAttack(this, e);
					}
				}
			}

			float f = this.isEnhanced() ? 1.125F : 1;

			if (TileEntityCrystalPylon.TUNED_PYLONS)
				f *= CrystalMusicManager.instance.getDingPitchScale(color);

			if (this.getTicksExisted()%(int)(72/f) == 0) {
				ChromaSounds.POWER.playSoundAtBlock(this, 1, f);
			}

			int n = this.isEnhanced() ? 24 : 36;
			if (world.isRemote && rand.nextInt(n) == 0) {
				this.spawnLightning(world, x, y, z);
			}

			if (!world.isRemote && ChromaOptions.BALLLIGHTNING.getState() && energy >= this.getCapacity()/2 && rand.nextInt(1000) == 0 && this.isChunkLoaded()) {
				world.spawnEntityInWorld(new EntityBallLightning(world, color, x+0.5, y+0.5, z+0.5).setPylon().setNoDrops());
			}
		}
	}

	private void doJarRejection(World world, int x, int y, int z) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || j != 0 || k != 0) {
						int dx = x+i;
						int dy = y+j;
						int dz = z+k;
						ReikaSoundHelper.playBreakSound(world, dx, dy, dz, world.getBlock(dx, dy, dz));
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 2, 0.5F);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 2, 1);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode", 2, 2);
		ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(this, 2, 0.5F, 64);
		ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(this, 2, 1F, 64);
		ChromaSounds.DISCHARGE.playSoundAtBlockNoAttenuation(this, 2, 2F, 64);
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(16, 16, 16);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			double dx = e.posX-x-0.5;
			double dy = e.posY-y-0.5;
			double dz = e.posZ-z-0.5;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double v = 10;
			double vy = 3;
			e.addVelocity(v*dx/dd, vy+0*Math.max(v*dy/dd, vy), v*dz/dd);
			e.fallDistance = 250;
			if (e instanceof EntityPlayer) {
				((EntityPlayer)e).capabilities.allowFlying = false;
				((EntityPlayer)e).capabilities.isFlying = false;
			}
		}
		if (!world.isRemote) {
			int n = 8+rand.nextInt(12);
			for (int i = 0; i < n; i++) {
				int rx = ReikaRandomHelper.getRandomPlusMinus(x, 12);
				int ry = ReikaRandomHelper.getRandomPlusMinus(y, 4);
				int rz = ReikaRandomHelper.getRandomPlusMinus(z, 12);
				ReikaWorldHelper.ignite(world, rx, ry, rz);
			}
			if (rand.nextBoolean())
				this.destroyPowerCrystals(1);
		}
		else
			this.doJarRejectionParticles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void doJarRejectionParticles(World world, int x, int y, int z) {
		ReikaParticleHelper.EXPLODE.spawnAroundBlockWithOutset(world, x, y, z, 0, 0, 0, 16, 0.25);
		for (int i = 0; i < 256; i++) {
			EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, rand.nextDouble()*360, -90+rand.nextDouble()*180);
			fx.setColor(color.getColor()).setScale(2+rand.nextFloat()*8).setLife(40+rand.nextInt(120));
			fx.particleVelocity = 0.5;
			fx.angleVelocity *= 2;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		for (int i = 0; i < 16; i++) {
			this.spawnLightning(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnLightning(World world, int x, int y, int z) {
		EntityBallLightningFX e = new EntityBallLightningFX(world, x+0.5, y+0.5, z+0.5, color);
		e.setVelocity(0.125, rand.nextInt(360), 0);
		Minecraft.getMinecraft().effectRenderer.addEffect(e);
	}

	public void destroyPowerCrystals(int n) {
		ArrayList<TileEntityChromaCrystal> crys = this.getBoosterCrystals(worldObj, xCoord, yCoord, zCoord, false);
		for (int i = 0; i < n && !crys.isEmpty(); i++) {
			int idx = rand.nextInt(crys.size());
			crys.get(idx).destroy();
			crys.remove(idx);
		}
	}

	private void charge(World world, int x, int y, int z, int max, int ticks) {
		int laste = energy;
		boolean lastconn = this.canConduct();

		if (energy < max) {
			energy += energyStep*ticks;
		}
		if (energyStep > 1)
			energyStep--;

		int a = ticks;
		if (energy < max) {
			ArrayList<TileEntityChromaCrystal> blocks = this.getBoosterCrystals(world, x, y, z, true);
			int c = this.isEnhanced() ? 3 : 2;
			for (int i = 0; i < blocks.size(); i++) {
				energy += a;
				a *= c;
				if (i == 7) { //8 crystals
					energy += a*2;
				}
				if (energy >= max) {
					break;
				}
			}
			//if (blocks.size() > 0 && this.getTicksExisted()%875 == 0) {
			//	ChromaSounds.POWERCRYS.playSoundAtBlock(this);
			//}
			if (blocks.size() == 8) {
				ProgressStage.POWERCRYSTAL.stepPlayerTo(blocks.get(0).getPlacer());
			}
			if (world.isRemote && !blocks.isEmpty()) {
				this.spawnRechargeParticles(world, x, y, z, blocks);
			}
		}

		energy = Math.min(energy, this.getCapacity());

		if (energy == this.getCapacity() && laste != this.getCapacity()) {
			MinecraftForge.EVENT_BUS.post(new PylonFullyChargedEvent(this));
			this.unload();
		}
		if (this.canConduct() && !lastconn) {
			MinecraftForge.EVENT_BUS.post(new PylonRechargedEvent(this));
		}
	}

	public void speedRegenShortly() {
		energyStep = 5;
	}

	@SideOnly(Side.CLIENT)
	private void spawnRechargeParticles(World world, int x, int y, int z, ArrayList<TileEntityChromaCrystal> blocks) {
		int i = 0;
		for (TileEntityChromaCrystal te : blocks) {
			int dx = te.xCoord;
			int dy = te.yCoord;
			int dz = te.zCoord;
			double ddx = dx-x;
			double ddy = dy-y-0.25;
			double ddz = dz-z;
			double dd = ReikaMathLibrary.py3d(ddx, ddy, ddz);
			double v = 0.125;
			double vx = -v*ddx/dd;
			double vy = -v*ddy/dd;
			double vz = -v*ddz/dd;
			double px = dx+0.5;
			double py = dy+0.125;
			double pz = dz+0.5;
			//EntityRuneFX fx = new EntityRuneFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz, color);
			float sc = (float)(2F+Math.sin(4*Math.toRadians(this.getTicksExisted()+i*90/blocks.size())));
			EntityBlurFX fx = new EntityBlurFX(color, world, px, py, pz, vx, vy, vz).setScale(sc).setLife(38).setNoSlowdown();
			//EntityLaserFX fx = new EntityLaserFX(color, world, px, py, pz, vx, vy, vz).setScale(3);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			i++;
		}
	}

	public BlockArray getRuneLocations(World world, int x, int y, int z) {
		BlockArray blocks = new BlockArray();
		blocks.addBlockCoordinate(x-3, y-4, z-1);
		blocks.addBlockCoordinate(x-1, y-4, z-3);

		blocks.addBlockCoordinate(x+3, y-4, z-1);
		blocks.addBlockCoordinate(x+1, y-4, z-3);

		blocks.addBlockCoordinate(x-3, y-4, z+1);
		blocks.addBlockCoordinate(x-1, y-4, z+3);

		blocks.addBlockCoordinate(x+3, y-4, z+1);
		blocks.addBlockCoordinate(x+1, y-4, z+3);
		return blocks;
	}

	public ArrayList<TileEntityChromaCrystal> getBoosterCrystals(World world, int x, int y, int z, boolean matchOwner) {
		ArrayList<TileEntityChromaCrystal> li = new ArrayList();
		EntityPlayer owner = null;
		for (Coordinate c : crystalPositions) {
			if (ChromaTiles.getTile(world, x+c.xCoord, y+c.yCoord, z+c.zCoord) == ChromaTiles.CRYSTAL) {
				TileEntityChromaCrystal te = (TileEntityChromaCrystal)world.getTileEntity(x+c.xCoord, y+c.yCoord, z+c.zCoord); {
					EntityPlayer ep = te.getPlacer();
					if (!matchOwner || (ep != null && (owner == null || ep == owner))) {
						if (owner == null)
							owner = ep;
						li.add(te);
					}
				}
			}
		}
		return li;
	}

	public boolean isValidPowerCrystal(TileEntityChromaCrystal te) {
		return crystalPositions.contains(new Coordinate(te).offset(-xCoord, -yCoord, -zCoord));
	}

	public void onPowerCrystalBreak(TileEntityChromaCrystal te) {
		this.disenhance();
		this.drain(color, energy/4);
		worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5));
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(24, 16, 24);
		List<EntityLivingBase> li = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase e : li) {
			if (e instanceof EntityPlayer) {
				if (((EntityPlayer)e).capabilities.isCreativeMode) {
					e.attackEntityFrom(DamageSource.outOfWorld, 0.001F);
				}
				else {
					float amt = Math.max(5, Math.min(e.getHealth()-4, e.getMaxHealth()*0.75F));
					ChromaAux.doPylonAttack(color, e, amt, false);
				}
			}
			else {
				e.attackEntityFrom(DamageSource.magic, 0); //only appear to hurt
			}
		}
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.PYLONCRYSTALBREAK.ordinal(), this, 64);
		this.syncAllData(true);
	}

	@SideOnly(Side.CLIENT)
	public void doPowerCrystalBreakFX(World world, int x, int y, int z) {
		int n = 24+rand.nextInt(32);
		for (int i = 0; i < n; i++) {
			float s = 1+rand.nextFloat()*2;
			int l = 30+rand.nextInt(50);
			EntityFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, rand.nextDouble()*360, rand.nextDouble()*360).setColor(color.getColor()).setScale(s).setLife(l).setIcon(ChromaIcons.NODE2);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.DISCHARGE, x+0.5, y+0.5, z+0.5, 1, 1, false);
	}

	@SideOnly(Side.CLIENT)
	public void particleAttack(int sx, int sy, int sz, int x, int y, int z) {
		int n = 8+rand.nextInt(24);
		for (int i = 0; i < n; i++) {
			float rx = sx+rand.nextFloat();
			float ry = sy+rand.nextFloat();
			float rz = sz+rand.nextFloat();
			double dx = x-sx;
			double dy = y-sy;
			double dz = z-sz;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			double vx = 2*dx/dd;
			double vy = 2*dy/dd;
			double vz = 2*dz/dd;
			EntityFlareFX f = new EntityFlareFX(color, worldObj, rx, ry, rz, vx, vy, vz).setNoGravity();
			Minecraft.getMinecraft().effectRenderer.addEffect(f);
		}

		ChromaOverlays.instance.triggerPylonEffect(color);
	}

	void attackEntityByProxy(EntityPlayer player, CrystalRepeater te) {
		this.attackEntity(player);
		this.sendClientAttack(te, player);
	}

	void attackEntity(EntityLivingBase e) {
		ChromaSounds.DISCHARGE.playSoundAtBlock(this);

		ChromaAux.doPylonAttack(color, e, 5, true);

		PotionEffect eff = CrystalPotionController.getEffectFromColor(color, 200, 2);
		if (eff != null) {
			e.addPotionEffect(eff);
		}
	}

	private void sendClientAttack(CrystalTransmitter te, EntityLivingBase e) {
		int tx = te.getX();
		int ty = te.getY();
		int tz = te.getZ();
		int x = MathHelper.floor_double(e.posX);
		int y = MathHelper.floor_double(e.posY)+1;
		int z = MathHelper.floor_double(e.posZ);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.PYLONATTACK.ordinal(), this, tx, ty, tz, x, y, z);
	}

	public void invalidateMultiblock() {
		if (hasMultiblock) {
			ChromaSounds.POWERDOWN.playSoundAtBlock(this);
			ChromaSounds.POWERDOWN.playSound(worldObj, xCoord, yCoord, zCoord, 1F, 2F);
			ChromaSounds.POWERDOWN.playSound(worldObj, xCoord, yCoord, zCoord, 1F, 0.5F);

			if (worldObj.isRemote)
				this.invalidatationParticles();
		}
		hasMultiblock = false;
		this.clearTargets(false);
		energy = 0;
		this.syncAllData(true);
	}

	@SideOnly(Side.CLIENT)
	private void invalidatationParticles() {
		double d = 1.25;
		int n = 64+rand.nextInt(64);
		for (int i = 0; i < n; i++) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, d);
			double ry = ReikaRandomHelper.getRandomPlusMinus(yCoord+0.5, d);
			double rz = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, d);
			double vx = rand.nextDouble()-0.5;
			double vy = rand.nextDouble()-0.5;
			double vz = rand.nextDouble()-0.5;
			EntityRuneFX fx = new EntityRuneFX(worldObj, rx, ry, rz, vx, vy, vz, color);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public void validateMultiblock() {
		hasMultiblock = true;
		this.syncAllData(true);
	}

	public boolean hasStructure() {
		return hasMultiblock;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle(World world, int x, int y, int z) {
		int p = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(1+p/2) == 0) {
			double d = 1.25;
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, d);
			double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5, d);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, d);
			EntityFlareFX fx = new EntityFlareFX(color, world, rx, ry, rz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		if (this.isEnhanced()) {
			int n = 2+(int)Math.sin(Math.toRadians(this.getTicksExisted()));
			for (int i = 0; i < n; i++) {
				float s = (float)ReikaRandomHelper.getRandomPlusMinus(2D, 1);
				int l = 10+rand.nextInt(50);
				EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(world, x+0.5, y+0.5, z+0.5, rand.nextInt(360), ReikaRandomHelper.getRandomPlusMinus(0, 90));
				fx.fadeColors(ReikaColorAPI.mixColors(color.getColor(), 0xffffff, 0.375F), color.getColor()).setScale(s).setLife(l).setRapidExpand();
				fx.freedom *= 3;
				fx.angleVelocity *= 3;
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		color = CrystalElement.elements[NBT.getInteger("color")];
		hasMultiblock = NBT.getBoolean("multi");
		energy = NBT.getInteger("energy");
		enhanced = NBT.getBoolean("enhance");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("color", color.ordinal());
		NBT.setBoolean("multi", hasMultiblock);
		NBT.setInteger("energy", energy);
		NBT.setBoolean("enhance", enhanced);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("load", forceLoad);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		forceLoad = NBT.getBoolean("load");
	}

	@Override
	public int getSendRange() {
		return RANGE;
	}

	@Override
	public boolean canConduct() {
		return hasMultiblock && (energy >= 5000 || (energy > 10 && !this.getTargets().isEmpty()));
	}

	@Override
	public int maxThroughput() {
		int base = this.isEnhanced() ? 15000 : 5000;
		int thresh = this.getCapacity()/4;
		return energy >= thresh ? base : this.getReducedThroughput(thresh, base);
	}

	private int getReducedThroughput(int thresh, int max) {
		if (energy == 0)
			return 0;
		int sigx = energy/(thresh/12)-6;
		int sig = (int)(max/(1+Math.pow(Math.E, -sigx))); //sigmoid function
		return Math.max(1, Math.min(energy-1, sig-10));
	}

	@Override
	public int getTransmissionStrength() {
		return this.isEnhanced() ? 50000 : 10000;
	}

	public void generateColor(CrystalElement e) {
		color = e;
	}

	public void setColor(CrystalElement e) {
		color = e;
	}

	@Override
	public boolean drain(CrystalElement e, int amt) {
		if (e == color && energy >= amt && amt > 0) {
			if (ModList.MYSTCRAFT.isLoaded() && MystPages.Pages.LOSSY.existsInWorld(worldObj))
				amt = amt*3/2;
			energy -= amt;
			energy = Math.max(energy, 0);
			if (energy <= 0) {
				MinecraftForge.EVENT_BUS.post(new PylonDrainedEvent(this));
			}
			return true;
		}
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getAspects() {
		AspectList as = new AspectList();
		int n = this.isEnhanced() ? 6000 : 400;
		as.add(Aspect.AURA, n);
		Collection<Aspect> li = ChromaAspectManager.instance.getAspects(this.getColor(), true);
		for (Aspect a : li) {
			as.add(a, n);
		}
		return as;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setAspects(AspectList aspects) {}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerAccept(Aspect tag) {
		return this.getAspects().getAmount(tag) > 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addToContainer(Aspect tag, int amount) {return 0;}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(Aspect tag, int amount) {
		return this.doesContainerContainAmount(tag, amount);
	}

	@Override
	@Deprecated
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(AspectList ot) {
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		return this.getAspects().getAmount(tag) > amount;
	}

	@Override
	@Deprecated
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContain(AspectList ot) {
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int containerContains(Aspect tag) {
		return this.getAspects().getAmount(tag);
	}

	@Override
	public String getId() { //Normally based on world coords, but uses just color to make each pylon color scannable once
		String s = "Pylon_"+color.toString();//"Pylon_"+worldObj.provider.dimensionId+":"+xCoord+":"+yCoord+":"+zCoord;
		if (this.isEnhanced())
			s = s+"_E";
		return s;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getAspectsBase() {
		return this.getAspects();
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public NodeType getNodeType() {
		switch(color) {
			case BLACK:
				return NodeType.DARK;
			case GRAY:
				return NodeType.UNSTABLE;
			case WHITE:
				return NodeType.PURE;
			default:
				return NodeType.NORMAL;
		}
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setNodeType(NodeType nodeType) {}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setNodeModifier(NodeModifier nodeModifier) {}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public NodeModifier getNodeModifier() {
		return NodeModifier.BRIGHT;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getNodeVisBase(Aspect aspect) {
		return this.containerContains(aspect);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setNodeVisBase(Aspect aspect, short nodeVisBase) {}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int mode) {
		return -1;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
		player.setItemInUse(wandstack, Integer.MAX_VALUE);
		ReikaThaumHelper.setWandInUse(wandstack, this);
		return wandstack;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
		if (!worldObj.isRemote && this.canConduct() && player.ticksExisted%5 == 0) {
			if (!ChromaOptions.HARDTHAUM.getState() || ReikaThaumHelper.isResearchComplete(player, "NODETAPPER2")) {
				AspectList al = ReikaThaumHelper.decompose(this.getAspects());
				for (Aspect a : al.aspects.keySet()) {
					int amt = 2;
					if (ReikaThaumHelper.isResearchComplete(player, "NODETAPPER1"))
						amt *= 2;
					if (ReikaThaumHelper.isResearchComplete(player, "NODETAPPER2"))
						amt *= 2;
					amt = Math.min(amt, al.getAmount(a));
					amt = Math.min(amt, ReikaThaumHelper.getWandSpaceFor(wandstack, a));
					int ret = ReikaThaumHelper.addVisToWand(wandstack, a, amt);
					int added = amt-ret;
					if (added > 0) {
						this.drain(color, Math.min(energy, added*900));
					}
				}
			}
		}
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {

	}

	public final ElementTagCompound getEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		tag.setTag(color, energy);
		return tag;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return this.getCapacity();
	}

	private int getCapacity() {
		return this.isEnhanced() ? MAX_ENERGY_ENHANCED : MAX_ENERGY;
	}

	public boolean isEnhanced() {
		return enhanced && this.canConduct();
	}

	@Override
	public int getSourcePriority() {
		return 0;
	}

	@Override
	public boolean canSupply(CrystalReceiver te) {
		return true;
	}

	@Override
	public boolean canTransmitTo(CrystalReceiver te) {
		return true;
	}

	@Override
	public boolean regeneratesEnergy() {
		return true;
	}

	@ModDependent(ModList.ROTARYCRAFT)
	public void onEMP(TileEntityEMP te) {
		energy = rand.nextBoolean() ? 0 : this.getCapacity();
		worldObj.createExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 16, false);
		ChromaSounds.DISCHARGE.playSoundAtBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final double getMaxRenderDistanceSquared() {
		return 65536D;
	}

	@Override
	public void breakBlock() {
		this.unload();
	}

	private void unload() {
		ChunkManager.instance.unloadChunks(this);
	}

	@Override
	public Collection<ChunkCoordIntPair> getChunksToLoad() {
		return ChunkManager.getChunkSquare(xCoord, zCoord, 1); //load a 3x3 to ensure power crystals
	}

	@Override
	protected final void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		if (!world.isRemote) {
			if (invalid) {
				this.unload();
			}
		}
	}

	@Override
	public void onUsedBy(EntityPlayer ep, CrystalElement e) {
		this.forceLoading();
		MinecraftForge.EVENT_BUS.post(new PlayerChargedFromPylonEvent(this, ep));
	}

	@Override
	public boolean playerCanUse(EntityPlayer ep) {
		return true;
	}

	@Override
	public boolean allowCharging(EntityPlayer ep, CrystalElement e) {
		return this.playerCanUse(ep);
	}

	@Override
	public float getChargeRateMultiplier(EntityPlayer ep, CrystalElement e) {
		return 1;
	}

	@Override
	public CrystalElement getDeliveredColor(EntityPlayer ep, World world, int clickX, int clickY, int clickZ) {
		return color;
	}

	public void enhance() {
		enhanced = true;
		enhancing = false;
		this.syncAllData(true);
	}

	public void disenhance() {
		enhanced = false;
		enhancing = false;
		energy = Math.min(energy, this.getMaxStorage(color));
		this.syncAllData(true);
	}

	@Override
	public Coordinate getChargeParticleOrigin(EntityPlayer ep, CrystalElement e) {
		return new Coordinate(this);
	}

}
