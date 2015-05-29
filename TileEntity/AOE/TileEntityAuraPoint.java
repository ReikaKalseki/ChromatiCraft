/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FastBlockCache;
import Reika.DragonAPI.Instantiable.Data.Collections.FastPlayerCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.CropType;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModCropList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


// Shoot down hostile mobs, speed crop growth, heal players
public class TileEntityAuraPoint extends TileEntityLocusPoint {

	private static final String NBT_TAG = "aurapoint";

	private static final int NEW_CROPS_PER_TICK = 256;
	private static final int CROPS_PER_TICK = 16;
	private static final int CROP_UPDATES = 8;

	private int hue;
	private float saturation;

	private int hueTarget;
	private float saturationTarget;

	private final FastBlockCache cache = new FastBlockCache();
	private final FastPlayerCache hostilePlayers = new FastPlayerCache();

	@Override
	public void breakBlock() {
		super.breakBlock();
		this.removePoint();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.isRemote)
			this.updateColors();
		else {
			this.playSounds(world, x, y, z);
			this.doAmbientEffects(world, x, y, z);
		}
	}

	private void playSounds(World world, int x, int y, int z) {
		double[] arr = {0.5, 1, 2};

		for (int i = 0; i < arr.length; i++) {
			double d = arr[i];
			int t = (int)(221/d);
			if (this.getTicksExisted()%t == 0) {
				ChromaSounds.DRONE.playSoundAtBlock(world, x, y, z, 0.75F, (float)d);
			}

			float f = 0.85F;
			if (this.getTicksExisted()%((int)(72/f)) == 0) {
				ChromaSounds.POWER.playSoundAtBlock(world, x, y, z, 0.1F, f);
			}
		}
	}

	private void doAmbientEffects(World world, int x, int y, int z) {
		if (rand.nextInt(20) == 0)
			this.killEntities(world, x, y, z);
		if (rand.nextInt(160) == 0)
			this.healFriendly(world, x, y, z);
		if (rand.nextInt(2) == 0)
			this.growCrops(world, x, y, z);
	}

	private void killEntities(World world, int x, int y, int z) {
		int r = 64;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-r, 0, z-r, x+1+r, 256, z+1+r);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		boolean flag = false;
		int i = 0;
		for (EntityLivingBase e : li) {
			if (this.shouldAttack(e)) {
				this.attack(world, x, y, z, e);
				flag = true;
				i++;
			}
		}
		if (flag) {
			ChromaSounds.DISCHARGE.playSoundAtBlock(this);
			ChromaSounds.DISCHARGE.playSound(this.getPlacer(), 0.125F, 0.75F);
		}
	}

	private void attack(World world, int x, int y, int z, EntityLivingBase e) {
		float dmg = e instanceof EntityPlayer ? 10 : Math.max(4, e.getHealth()/2);
		e.attackEntityFrom(ChromatiCraft.pylon, dmg);
		ChromaSounds.DISCHARGE.playSound(e, 0.5F, 1);

		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.AURATTACK.ordinal(), this, 192, e.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public void doAttackFX(Entity e) {
		if (e != null) {
			double x = xCoord+0.5;
			double y = yCoord+0.5;
			double z = zCoord+0.5;
			double dx = e.posX-x;
			double dy = e.posY+e.height/2-y;
			double dz = e.posZ-z;
			double dd = ReikaMathLibrary.py3d(dx, dy, dz);
			for (double d = 0; d <= dd; d += 0.5) {
				double f = d/dd;
				double f2 = f < 0.5 ? 1-f : f;
				double px = x+f*dx;
				double py = y+f*dy;
				double pz = z+f*dz;
				px = ReikaRandomHelper.getRandomPlusMinus(px, 0.25);
				py = ReikaRandomHelper.getRandomPlusMinus(py, 0.25);
				pz = ReikaRandomHelper.getRandomPlusMinus(pz, 0.25);
				float sc = 2.5F+(float)((1-f2)*20);
				sc = (float)ReikaRandomHelper.getRandomPlusMinus(sc, 0.25);
				EntityFX fx = new EntityBlurFX(e.worldObj, px, py, pz).setScale(sc).setRapidExpand().setLife(10).setIcon(ChromaIcons.FLARE);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	private boolean shouldAttack(EntityLivingBase e) {
		if (e.getHealth() <= 0 || e.isDead)
			return false;
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			if (this.isPlacer(ep))
				return false;
			return hostilePlayers.containsPlayer(ep);
		}
		if (ReikaEntityHelper.isHostile(e) || e instanceof EntityEnderman || e instanceof EntityPigZombie)
			return true;
		return false;
	}

	private void growCrops(World world, int x, int y, int z) {
		ArrayList<Coordinate> bks = new ArrayList(cache.getBlocks());
		Collection<Coordinate> remove = new ArrayList();
		int n = Math.min(CROPS_PER_TICK, bks.size()/2);
		for (int i = 0; i < n; i++) {
			int index = rand.nextInt(bks.size());
			Coordinate c = bks.get(index);
			CropType type = this.getCropAt(world, c);
			if (type == null) {
				remove.add(c);
			}
			else {
				if (!type.isRipe(world, c.xCoord, c.yCoord, c.zCoord)) {
					int state = type.getGrowthState(world, c.xCoord, c.yCoord, c.zCoord);
					for (int k = 0; k < CROP_UPDATES; k++)
						c.getBlock(world).updateTick(world, c.xCoord, c.yCoord, c.zCoord, rand);
					if (state != type.getGrowthState(world, c.xCoord, c.yCoord, c.zCoord)) {
						ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.AURAGROW.ordinal(), this, 64, c.xCoord, c.yCoord, c.zCoord);
					}
				}
			}
		}
		for (Coordinate c : remove)
			cache.removeBlock(c);

		for (int k = 0; k < NEW_CROPS_PER_TICK; k++) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 64);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 64);
			int dy = rand.nextInt(1+ReikaWorldHelper.getTopNonAirBlock(world, x, z));
			Coordinate c = new Coordinate(dx, dy, dz);
			if (cache.containsBlock(c)) {
				k--;
				continue;
			}
			else {
				CropType type = this.getCropAt(world, c);
				if (type != null) {
					cache.addBlock(c);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void doGrowFX(int x, int y, int z) {
		ReikaParticleHelper.BONEMEAL.spawnAroundBlock(worldObj, x, y, z, 4);
	}

	private CropType getCropAt(World world, Coordinate c) {
		Block b = c.getBlock(world);
		int meta = c.getBlockMetadata(world);
		CropType type = ReikaCropHelper.getCrop(b);
		if (type == null)
			type = ModCropList.getModCrop(b, meta);
		return type;
	}

	private void healFriendly(World world, int x, int y, int z) {
		int r = 32;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x-r, 0, z-r, x+1+r, 256, z+1+r);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		boolean flag = false;
		for (EntityLivingBase e : li) {
			if (this.shouldHeal(e)) {
				this.heal(e);
				flag = true;
			}
		}
	}

	private void heal(EntityLivingBase e) {
		e.heal(e.getMaxHealth());
		ChromaSounds.CAST.playSound(e, 0.5F, 1);
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.AURAHEAL.ordinal(), this, 64, e.getEntityId());
	}

	@SideOnly(Side.CLIENT)
	public void doHealFX(Entity e) {
		for (int i = 0; i < 32; i++) {
			double x = ReikaRandomHelper.getRandomPlusMinus(e.posX, 1.5);
			double z = ReikaRandomHelper.getRandomPlusMinus(e.posZ, 1.5);
			double y = e.posY-1+rand.nextFloat()*(e.height+2);
			ReikaParticleHelper.MOBSPELL.spawnAt(e.worldObj, x, y, z);
		}
	}

	private boolean shouldHeal(EntityLivingBase e) {
		if (e.getHealth() >= e.getMaxHealth())
			return false;
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			return this.isPlacer(ep) || !hostilePlayers.containsPlayer(ep);
		}
		else if (e instanceof EntityAnimal) {
			return true;
		}
		return false;
	}

	public void markHostile(EntityPlayer ep) {
		hostilePlayers.addPlayer(ep);
	}

	private void updateColors() {
		float ds = saturationTarget-saturation;
		int dh = hueTarget-hue;
		if (dh == 0 && Math.abs(ds) < 0.03125) {
			hueTarget = rand.nextInt(360);
			saturationTarget = rand.nextFloat()*rand.nextFloat();
		}

		saturation += 0.03125*0.125*Math.signum(ds);
		int hd = (int)Math.signum(dh);
		if (Math.abs(dh) >= 180) {
			hue -= hd;
		}
		else {
			hue += hd;
		}

		if (hue < 0 || hue >= 360)
			hue = (hue%360+360)%360;

		//ReikaJavaLibrary.pConsole(hue+":"+hueTarget+"/"+saturation+":"+saturationTarget);
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AURAPOINT;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRenderColor() {
		return Color.HSBtoRGB(hue/360F, saturation, 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound tag = new NBTTagCompound();
		cache.writeToNBT(tag);
		NBT.setTag("crops", tag);

		tag = new NBTTagCompound();
		hostilePlayers.writeToNBT(tag);
		NBT.setTag("hostile", tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		cache.readFromNBT(NBT.getCompoundTag("crops"));
		hostilePlayers.readFromNBT(NBT.getCompoundTag("hostile"));
	}

	public void savePoint() {
		EntityPlayer ep = this.getPlacer();
		NBTTagCompound tag = new NBTTagCompound();
		new WorldLocation(this).writeToNBT(tag);
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		nbt.setTag(NBT_TAG, tag);
	}

	public static TileEntityAuraPoint getPoint(EntityPlayer ep) {
		NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		if (nbt.hasKey(NBT_TAG)) {
			NBTTagCompound tag = nbt.getCompoundTag(NBT_TAG);
			WorldLocation loc = WorldLocation.readFromNBT(tag);
			if (loc != null) {
				TileEntity te = loc.getTileEntity();
				if (te instanceof TileEntityAuraPoint) {
					return (TileEntityAuraPoint)te;
				}
			}
		}
		return null;
	}

	public void removePoint() {
		EntityPlayer ep = this.getPlacer();
		NBTTagCompound tag = ReikaPlayerAPI.getDeathPersistentNBT(ep);
		tag.removeTag(NBT_TAG);
	}

}
