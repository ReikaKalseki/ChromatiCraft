/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Instantiable.CustomStringDamageSource;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityAvoLaser extends TileEntityRelayPowered implements SidePlacedTile {

	public static final int MAXDIST = 8;

	private static final ElementTagCompound required = new ElementTagCompound();

	private DamageSource damageSource;

	static {
		required.addValueToColor(CrystalElement.PINK, 25);
		required.addValueToColor(CrystalElement.BLUE, 5);
		required.addValueToColor(CrystalElement.YELLOW, 10);
	}

	private int startDist = 0;
	private int endDist = 0;

	private ForgeDirection facing;

	private final TimerMap<UUID> attackCooldowns = new TimerMap();

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.UP;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		this.stepDistances(world, x, y, z);
		AxisAlignedBB box = this.getScanBox(world, x, y, z);
		if (box != null) {
			if (world.isRemote) {
				this.spawnParticles(world, x, y, z, box);
			}
			else {
				this.drainEnergy(required);
				List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
				for (EntityLivingBase e : li) {
					this.attack(e);
				}
			}
		}

		if (!world.isRemote) {
			attackCooldowns.tick();
		}
	}

	private void attack(EntityLivingBase e) {
		if (!attackCooldowns.containsKey(e.getUniqueID())) {
			attackCooldowns.put(e.getUniqueID(), 40);
			float base = this.hasPinkRune() ? 12 : 8;//4 hearts, since original does 40/100 damage
			int adj = TileEntityAdjacencyUpgrade.getAdjacentUpgrade(this, CrystalElement.PINK);
			double f = adj == 0 ? 1 : AdjacencyUpgrades.PINK.getFactor(adj);
			base *= f;
			e.attackEntityFrom(this.getDamageSource(), base);
		}
	}

	private DamageSource getDamageSource() {
		if (damageSource == null) {
			damageSource = new CustomStringDamageSource("has sacrificed themselves to the Sovereign Temple of "+this.getPlacerName());
			damageSource.setDamageIsAbsolute().setMagicDamage().setDamageBypassesArmor();
		}
		return damageSource;
	}

	private boolean hasPinkRune() {
		int dx = xCoord-this.getFacing().offsetX;
		int dy = yCoord-this.getFacing().offsetY;
		int dz = zCoord-this.getFacing().offsetZ;
		return worldObj.getBlock(dx, dy, dz) == ChromaBlocks.RUNE.getBlockInstance() && worldObj.getBlockMetadata(dx, dy, dz) == CrystalElement.PINK.ordinal();
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, AxisAlignedBB box) {
		ForgeDirection dir = this.getFacing();
		box = box.expand(0.125, 0.125, 0.125).offset(dir.offsetX*0.5, dir.offsetY*0.5, dir.offsetZ*0.5);
		for (int i = 0; i < 2; i++) {
			double px = ReikaRandomHelper.getRandomBetween(box.minX, box.maxX);
			double py = ReikaRandomHelper.getRandomBetween(box.minY, box.maxY);
			double pz = ReikaRandomHelper.getRandomBetween(box.minZ, box.maxZ);
			EntityLaserFX fx = new EntityLaserFX(CrystalElement.RED, world, px, py, pz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public boolean isActive() {
		return endDist > 0;
	}

	private void stepDistances(World world, int x, int y, int z) {
		//ReikaJavaLibrary.pConsole("tick");
		ForgeDirection dir = this.getFacing();

		if (endDist > 0) {
			endDist = Math.min(endDist+1, this.getRange(world, x, y, z, dir));
			//ReikaJavaLibrary.pConsole("endDist++, ="+endDist, Side.SERVER);
		}

		boolean active = this.hasRedstoneSignal() || world.isBlockIndirectlyGettingPowered(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
		active &= energy.containsAtLeast(required);
		if (active) {
			if (endDist == 0) {
				endDist++;
				//ReikaJavaLibrary.pConsole("endDist++, ="+endDist, Side.SERVER);
			}
			startDist = Math.max(startDist-1, 0);
			//ReikaJavaLibrary.pConsole("startDist--, ="+startDist, Side.SERVER);
			if (this.getTicksExisted()%3 == 0)
				ChromaSounds.AVOLASER.playSoundAtBlock(this, 0.5F, 1);
		}
		else {
			if (endDist > 0) {
				startDist = Math.min(startDist+1, this.getRange(world, x, y, z, dir));
				//ReikaJavaLibrary.pConsole("startDist++, ="+startDist, Side.SERVER);
			}
		}

		if (startDist >= endDist) {
			startDist = 0;
			endDist = 0;
			//ReikaJavaLibrary.pConsole("reset", Side.SERVER);
		}
	}

	private int getRange(World world, int x, int y, int z, ForgeDirection dir) {
		for (int i = 1; i <= MAXDIST; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			if (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
				return i-1;
			}
		}
		return MAXDIST;
	}

	public AxisAlignedBB getScanBox(World world, int x, int y, int z) {
		ForgeDirection dir = this.getFacing();
		if (endDist > 0) {
			AxisAlignedBB box = ReikaAABBHelper.getBeamBox(x, y, z, dir, startDist, endDist);
			return box;
		}
		else {
			return null;
		}
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return this.isAcceptingColor(e) && dir != this.getFacing();
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 6000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AVOLASER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void placeOnSide(int s) {
		facing = dirs[s];
	}

	@Override
	public boolean checkLocationValidity() {
		ForgeDirection dir = this.getFacing().getOpposite();
		int dx = xCoord+dir.offsetX;
		int dy = yCoord+dir.offsetY;
		int dz = zCoord+dir.offsetZ;
		return worldObj.getBlock(dx, dy, dz).isSideSolid(worldObj, dx, dy, dz, dir.getOpposite());
	}

	@Override
	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

	public int getBeamStart() {
		return startDist;
	}

	public int getBeamEnd() {
		return endDist;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("dir")];

		startDist = NBT.getInteger("start");
		endDist = NBT.getInteger("end");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("dir", this.getFacing().ordinal());

		NBT.setInteger("start", startDist);
		NBT.setInteger("end", endDist);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		ForgeDirection dir = this.getFacing();
		return ReikaAABBHelper.getBlockAABB(this).addCoord(MAXDIST*dir.offsetX, MAXDIST*dir.offsetY, MAXDIST*dir.offsetZ);
	}

}
