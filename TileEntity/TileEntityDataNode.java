/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.Decoration.BlockMetaAlloyLamp;
import Reika.ChromatiCraft.Entity.EntityTunnelNuker;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Lore.Towers;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ProximityMap;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer.StructureRenderingParticleSpawner;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityDataNode extends TileEntityChromaticBase implements OperationInterval, StructureRenderingParticleSpawner {

	private double extension0;
	private double extension1;
	private double extension2;

	private double lastLowerHeight;
	private double lastUpperHeight;

	//private static final double EXTENSION_SPEED = 0.03125;
	private static final int EXTENSION_TIME_0 = 24;
	private static final int EXTENSION_TIME_1 = 50;
	private static final int EXTENSION_TIME_2 = 36;

	public static final double EXTENSION_LIMIT_0 = 0.75;//1.75;
	public static final double EXTENSION_LIMIT_1 = 1.375;//2.5;
	public static final double EXTENSION_LIMIT_2 = 1.125;//1.25;

	private static final double EXTENSION_SPEED_0 = EXTENSION_LIMIT_0/EXTENSION_TIME_0;
	private static final double EXTENSION_SPEED_1 = EXTENSION_LIMIT_1/EXTENSION_TIME_1;
	private static final double EXTENSION_SPEED_2 = EXTENSION_LIMIT_2/EXTENSION_TIME_2;

	private double rotation;
	private double rotationSpeed;

	private static final int SCAN_TIME = 120;
	private static final int SCAN_COOLDOWN = 240;

	private int scanTick;
	private int scanSustain;
	private int scanCooldown;

	private static final int PROGRESS_DELAY_LENGTH = 50;

	private EntityPlayer progressPlayer;
	private int progressDelay;

	private Towers tower;
	private final HashSet<String> scannedPlayers = new HashSet(); //not uuid since written to NBT
	private final ProximityMap metaAlloyPlants = new ProximityMap(64, 1);

	private int plantRand = 800;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.DATANODE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		this.setUnmineable(true);
		EntityPlayer ep = world.getClosestPlayer(x+0.5, y+0.5, z+0.5, 16);
		lastLowerHeight = extension0+extension1;
		lastUpperHeight = extension2;
		if (ep != null || (ReikaObfuscationHelper.isDeObfEnvironment() && KeyWatcher.instance.isKeyDown(world.getPlayerEntityByName("Reika"), Key.LCTRL))) {
			if (extension0 < EXTENSION_LIMIT_0) {
				extension0 = Math.min(extension0+EXTENSION_SPEED_0, EXTENSION_LIMIT_0);
			}
			else if (extension1 < EXTENSION_LIMIT_1) {
				extension1 = Math.min(extension1+EXTENSION_SPEED_1, EXTENSION_LIMIT_1);
			}
			else {
				extension2 = Math.min(extension2+EXTENSION_SPEED_2, EXTENSION_LIMIT_2);
			}
		}
		else {
			if (extension1 == 0) {
				extension0 = Math.max(extension0-EXTENSION_SPEED_0, 0);
			}
			else if (extension2 == 0) {
				extension1 = Math.max(extension1-EXTENSION_SPEED_1, 0);
			}
			else {
				extension2 = Math.max(extension2-EXTENSION_SPEED_2, 0);
			}
		}

		if (extension0 > 0 && !world.isRemote) {
			List<ChromaSounds> snd = new ArrayList();
			if (extension0+extension1 > lastLowerHeight || extension2 > lastUpperHeight) {
				if (this.getTicksExisted()%5 == 0 || lastLowerHeight <= 0)
					snd.add(ChromaSounds.TOWEREXTEND1);
			}
			if (extension1 >= EXTENSION_LIMIT_1) {
				if (lastLowerHeight < EXTENSION_LIMIT_0+EXTENSION_LIMIT_1) {
					snd.add(ChromaSounds.TOWEREXTEND2);
				}
			}
			if (extension2 == EXTENSION_LIMIT_2) {
				if (this.getTicksExisted()%50 == 0 || lastUpperHeight < EXTENSION_LIMIT_2)
					snd.add(ChromaSounds.TOWERAMBIENT);
			}

			//ReikaJavaLibrary.pConsole(snd, !snd.isEmpty());
			for (ChromaSounds s : snd) {
				s.playSoundAtBlock(world, x, y+3, z, 2, 1);
				List<EntityPlayer> li = world.getEntitiesWithinAABB(EntityPlayer.class, ReikaAABBHelper.getBlockAABB(this).expand(30, 18, 30));
				for (EntityPlayer ep2 : li) {
					float f = (float)Math.max(0.4, 2-ep2.getDistance(x+0.5, y+2.5, z+0.5)/15D);
					s.playSound(ep2, f, 1);
				}
			}
		}

		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
		else {
			if (tower != null)
				tower.generatedAt(x, y, z);
		}

		if (scanSustain > 0) {
			scanSustain--;
			scanTick++;
		}
		else if (scanTick > 0) {
			scanTick = Math.max(0, scanTick-8);
		}

		if (scanTick > 0) {
			float f = 0.5F+1.5F*this.getScanProgress();
			ChromaSounds.KILLAURA_CHARGE.playSoundAtBlock(this, 1, f);
		}

		if (scanCooldown > 0) {
			scanCooldown--;
		}

		if (progressDelay > 0) {
			progressDelay--;
			if (progressDelay == 0 && tower != null) {
				LoreManager.instance.triggerLore(progressPlayer, tower);
			}
		}

		if (tower != null && !world.isRemote) {
			if (world.getBlock(x, y+1, z) != ChromaBlocks.DUMMYAUX.getBlockInstance() || world.getBlock(x, y-1, z) != ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
				FilledBlockArray arr = ChromaStructures.DATANODE.getArray(world, x, y-1, z);
				arr.remove(x, y, z);
				arr.place();
			}
			if (rand.nextInt(plantRand) == 0) {
				if (this.spawnMetaAlloy(world, x, y, z)) {
					plantRand = 800; //reset random rate;
				}
				else {
					plantRand = Math.max(300, plantRand-50);
					for (Coordinate c : metaAlloyPlants.getLocations()) { //verify all flowers
						if (c.getBlock(world) != ChromaBlocks.METAALLOYLAMP.getBlockInstance()) {
							metaAlloyPlants.remove(c);
						}
					}
				}
			}
			if (rand.nextInt(300) == 0) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(this);
				box = box.expand(192, 64, 192);
				List<EntityTunnelNuker> li = world.getEntitiesWithinAABB(EntityTunnelNuker.class, box);
				if (li.size() < 8) {
					if (ReikaWorldHelper.isRadiusLoaded(world, x, z, 6)) {
						EntityTunnelNuker e = new EntityTunnelNuker(worldObj);
						int dx = ReikaRandomHelper.getRandomPlusMinus(x, 128);
						int dz = ReikaRandomHelper.getRandomPlusMinus(z, 128);
						int dy = world.getTopSolidOrLiquidBlock(dx, dz)+3+rand.nextInt(12);
						e.setLocationAndAngles(dx+rand.nextDouble(), dy, dz+rand.nextDouble(), rand.nextFloat()*360, 0);
						world.spawnEntityInWorld(e);
					}
				}
			}
		}
	}

	@Override
	public void tickFX() {
		//this.doParticles(worldObj, xCoord, yCoord, zCoord);
	}

	private boolean spawnMetaAlloy(World world, int x, int y, int z) {
		int dx = ReikaRandomHelper.getRandomPlusMinus(x, 256);
		int dz = ReikaRandomHelper.getRandomPlusMinus(z, 256);
		int dy = world.getTopSolidOrLiquidBlock(x, z)+1;
		Block b = world.getBlock(dx, dy, dz);
		while (dy >= 0 && (b.isAir(world, dx, dy, dz) || ReikaBlockHelper.isLeaf(world, dx, dy, dz)) || ReikaBlockHelper.isWood(world, dx, dy, dz)) {
			dy--;
			b = world.getBlock(dx, dy, dz);
		}
		if (b == Blocks.grass && world.getBlock(dx, dy+1, dz).isAir(world, dx, dy, dz)) {
			if (metaAlloyPlants.add(new Coordinate(dx, dy+1, dz))) {
				world.setBlock(dx, dy+1, dz, ChromaBlocks.METAALLOYLAMP.getBlockInstance());
				//ReikaJavaLibrary.spamConsole(dx+":"+dz);
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (this.canBeAccessed()) {
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 4);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 4);
			double py = ReikaRandomHelper.getRandomBetween(y+3.5, y+5);
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FADE_RAY).setColor(0xa0e0ff).setLife(30).setScale(0.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		if (this.canBeAccessed() && rand.nextInt(5) == 0/* && !this.hasBeenScanned(Minecraft.getMinecraft().thePlayer)*/) {
			if (tower != null) {
				if (tower == Towers.ALPHA) {
					int idx = 1+((this.getTicksExisted()/120)%(Towers.towerList.length-1));
					this.sendParticlesToTower(world, x, y, z, Towers.towerList[idx]);
				}
				else {
					this.sendParticlesToTower(world, x, y, z, tower.getNeighbor1());
					this.sendParticlesToTower(world, x, y, z, tower.getNeighbor2());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void sendParticlesToTower(World world, int x, int y, int z, Towers t) {
		if (t == null || t.getRootPosition() == null) //sync not yet received
			return;

		double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 1);
		double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 1);
		double py = ReikaRandomHelper.getRandomBetween(y+3.5, y+5);

		double dx = t.getRootPosition().chunkXPos-tower.getRootPosition().chunkXPos;
		double dz = t.getRootPosition().chunkZPos-tower.getRootPosition().chunkZPos;
		double a = -ReikaPhysicsHelper.cartesianToPolar(dx, 0, dz)[2]-90;
		float s = rand.nextFloat()+0.25F;
		EntityFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, px, py, pz, a, 0, ChromaIcons.FADE);
		fx.freedom *= 0.5;
		fx.setColor(0xa0e0ff).setLife(120).setScale(s);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public double getRotation() {
		return rotation;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (extension2 == EXTENSION_LIMIT_2) {
			rotationSpeed = 1.5;
		}
		else if (extension1 == EXTENSION_LIMIT_1) {
			rotationSpeed = 1;
		}
		else if (extension0 == EXTENSION_LIMIT_0) {
			rotationSpeed = 0.5;
		}
		else {
			rotationSpeed = 0;
		}

		rotation += rotationSpeed;
	}

	public double getExtension0() {
		return StructureRenderer.isRenderingTiles() ? EXTENSION_LIMIT_0 : extension0;
	}

	public double getExtension1() {
		return StructureRenderer.isRenderingTiles() ? EXTENSION_LIMIT_1 : extension1;
	}

	public double getExtension2() {
		return StructureRenderer.isRenderingTiles() ? EXTENSION_LIMIT_2 : extension2;
	}

	public boolean canBeAccessed() {
		return extension2 >= EXTENSION_LIMIT_2;
	}

	public void scan(EntityPlayer ep) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment() && KeyWatcher.instance.isKeyDown(ep, Key.LCTRL)) {
			scannedPlayers.clear();
			scanCooldown = 0;
		}

		if (scanCooldown > 0)
			return;
		if (!this.canBeAccessed())
			return;
		if (this.hasBeenScanned(ep))
			return;

		scanTick++;
		scanSustain = 4;

		if (scanTick >= SCAN_TIME && !worldObj.isRemote) {
			this.doScan(ep);
		}
	}

	public float getScanProgress() {
		return (float)scanTick/SCAN_TIME;
	}

	private void doScan(EntityPlayer ep) {
		scanTick = 0;
		scanSustain = 0;
		scanCooldown = SCAN_COOLDOWN;
		progressDelay = PROGRESS_DELAY_LENGTH;
		progressPlayer = ep;
		ProgressStage.TOWER.stepPlayerTo(ep);
		scannedPlayers.add(ep.getUniqueID().toString());
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.DATASCAN.ordinal(), this, 128);
		ItemStack is = ChromaItems.DATACRYSTAL.getStackOf();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setString("owner", ep.getUniqueID().toString());
		EntityItem ei = ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+5, zCoord+0.5, is, 3);
		ei.motionY = Math.max(ei.motionY, 0.75);
		ei.velocityChanged = true;
		scannedPlayers.add(ep.getUniqueID().toString());
		this.syncAllData(true);
	}

	@SideOnly(Side.CLIENT)
	public static void doScanFX(World world, int x, int y, int z) {
		//ReikaSoundHelper.playClientSound(ChromaSounds.BOUNCE, x+0.5, y+0.5, z+0.5, 2, 2, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.BOUNCE, x+0.5, y+0.5, z+0.5, 2, 1, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.BOUNCE, x+0.5, y+0.5, z+0.5, 2, 1, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 2, 0.8F, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 2, 1.6F, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.KILLAURA, x+0.5, y+0.5, z+0.5, 2, 2F, false);
		ReikaSoundHelper.playClientSound(ChromaSounds.KILLAURA, x+0.5, y+0.5, z+0.5, 2, 1F, false);
		//ReikaSoundHelper.playClientSound(ChromaSounds.MONUMENTRAY, x+0.5, y+0.5, z+0.5, 2, 0.4F, false);

		for (double a = 0; a < 360; a += 1) {
			EntityFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(world, x+0.5, y+4.5, z+0.5, a, 0);
			fx.setColor(0xa0e0ff).setLife(120).setRapidExpand();
			fx.particleVelocity *= 2;
			fx.freedom *= 2;
			fx.angleVelocity *= 2;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		/*
		for (int i = -8; i <= 8; i++) {
			float s = (8-Math.abs(i));///2F;
			EntityFX fx = new EntityBlurFX(world, x+0.5, y+4.5+i*0.25, z+0.5, 0, 0.5, 0).setColor(0xa0e0ff).setLife(120).setRapidExpand().setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		 */

		for (double i = 0; i <= 64; i += 0.25) {
			EntityFX fx = new EntityCCBlurFX(world, x+0.5, y+4.5+i, z+0.5).setColor(0xa0e0ff).setLife(120).setRapidExpand().setAlphaFading().setScale(4);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);

			fx = new EntityCCBlurFX(world, x+0.5, y+4.5+i, z+0.5).setColor(0xffffff).setLife(120).setAlphaFading().setScale(1.5F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord-0.5, yCoord, zCoord-0.5, xCoord+0.5, yCoord+EXTENSION_LIMIT_0+EXTENSION_LIMIT_1+EXTENSION_LIMIT_2+2, zCoord+0.5).expand(6, 6, 6);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared()*16;
	}

	@Override
	public float getOperationFraction() {
		return this.getScanProgress();
	}

	@Override
	public OperationState getState() {
		return !this.canBeAccessed() || scanCooldown > 0 ? OperationState.INVALID : scanTick > 0 ? OperationState.RUNNING : OperationState.PENDING;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		//scanTick = NBT.getInteger("scan");
		scanCooldown = NBT.getInteger("cooldown");

		ReikaNBTHelper.readCollectionFromNBT(scannedPlayers, NBT, "players");
		if (NBT.hasKey("tower"))
			tower = Towers.towerList[NBT.getInteger("tower")];
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		//NBT.setInteger("scan", scanTick);
		NBT.setInteger("cooldown", scanCooldown);

		ReikaNBTHelper.writeCollectionToNBT(scannedPlayers, NBT, "players");
		if (tower != null) {
			NBT.setInteger("tower", tower.ordinal());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagList li = new NBTTagList();
		for (Coordinate c : metaAlloyPlants.getLocations()) {
			li.appendTag(c.writeToTag());
		}
		NBT.setTag("plants", li);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		metaAlloyPlants.clear();
		NBTTagList li = NBT.getTagList("plants", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound b = (NBTTagCompound)o;
			Coordinate c = Coordinate.readTag(b);
			metaAlloyPlants.add(c);
		}
	}

	public boolean hasBeenScanned(EntityPlayer ep) {
		return scannedPlayers.contains(ep.getUniqueID().toString());
	}

	public void setTower(Towers tower) {
		this.tower = tower;
	}

	public Towers getTower() {
		return tower;
	}

	@Override
	public boolean hasWork() {
		return false;
	}

	public static void removeMetaAlloy(World world, int x, int y, int z) {
		LoreManager.instance.initTowers(world);
		Coordinate c = new Coordinate(x, y, z);
		for (int i = 0; i < Towers.towerList.length; i++) {
			Towers t = Towers.towerList[i];
			Coordinate loc = t.getGeneratedLocation();
			if (loc != null) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityDataNode) {
					if (((TileEntityDataNode)te).metaAlloyPlants.remove(c)) {
						((TileEntityDataNode)te).syncAllData(true);
						if (ModList.FORESTRY.isLoaded()) {
							BlockMetaAlloyLamp.doBeeDrops(world, x, y, z);
						}
					}
				}
			}
		}
	}

}
