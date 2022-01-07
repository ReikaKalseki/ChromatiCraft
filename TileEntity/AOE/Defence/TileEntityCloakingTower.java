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

import net.machinemuse.api.electricity.MuseElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.ThreadSafeTileCache;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

//Some sort of indicator? Cloaking?
public class TileEntityCloakingTower extends TileEntityChromaticBase implements LocationCached, MultiBlockChromaTile {

	private static final ThreadSafeTileCache cache = new ThreadSafeTileCache().setTileClass(TileEntityCloakingTower.class);

	public static final int MAXRANGE = 128;

	private boolean active;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CLOAKING;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
		this.validateStructure();
	}

	public void validateStructure() {
		ChromaStructures.CLOAKTOWER.getStructure().resetToDefaults();
		active = ChromaStructures.CLOAKTOWER.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (active) {
			if (world.isRemote) {
				this.createParticles(world, x, y, z);
			}
			else {
				/*
				EntityPlayer ep = this.getPlacer();
				if (ep.boundingBox.intersectsWith(this.getCloseEffectBox(world, x, y, z))) {
					this.rechargeTools(ep);
				}*/
			}
		}
	}

	private void rechargeTools(EntityPlayer ep) {
		int slot = rand.nextBoolean() ? rand.nextInt(9) : rand.nextInt(ep.inventory.getSizeInventory()); //bias to hotbar
		ItemStack is = ep.inventory.getStackInSlot(slot);
		if (is != null) {
			if (this.canCharge(is)) {
				this.charge(is);
			}
		}
	}

	private boolean canCharge(ItemStack is) {
		if (InterfaceCache.RFENERGYITEM.instanceOf(is.getItem()) || InterfaceCache.IELECTRICITEM.instanceOf(is.getItem()) || InterfaceCache.MUSEELECTRICITEM.instanceOf(is.getItem())) {
			if (is.getItem() instanceof ItemBlock)
				return false;
			return true;
		}
		return false;
	}

	private boolean charge(ItemStack is) {
		if (InterfaceCache.RFENERGYITEM.instanceOf(is.getItem())) {
			IEnergyContainerItem ie = (IEnergyContainerItem)is.getItem();
			int amt = Math.min(Math.max(1, ie.getMaxEnergyStored(is)/100), ie.getMaxEnergyStored(is)-ie.getEnergyStored(is));
			if (amt > 0) {
				int add = ie.receiveEnergy(is, amt, false);
				return add > 0;
			}
			return false;
		}
		else if (InterfaceCache.IELECTRICITEM.instanceOf(is.getItem())) {
			double max = ((IElectricItem)is.getItem()).getMaxCharge(is);
			double amt = Math.min(Math.max(1, max/100), max-ElectricItem.manager.getCharge(is));
			if (amt > 0) {
				double add = ElectricItem.manager.charge(is, amt, Integer.MAX_VALUE, true, false);
				return add > 0;
			}
			return false;
		}
		else if (InterfaceCache.MUSEELECTRICITEM.instanceOf(is.getItem())) {
			MuseElectricItem ms = (MuseElectricItem)is.getItem();
			int amt = Math.min(Math.max(1, ms.getMaxCharge(is)/100), ms.getMaxCharge(is)-(int)ms.getCurrentEnergy(is));
			if (amt > 0) {
				int add = ms.receiveEnergy(is, amt, false);
				return add > 0;
			}
		}
		return false;
	}

	private AxisAlignedBB getCloseEffectBox(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z).expand(2, 5, 2);
	}

	@SideOnly(Side.CLIENT)
	private void createParticles(World world, int x, int y, int z) {
		int n = 3-Minecraft.getMinecraft().gameSettings.particleSetting;
		for (int i = 0; i < n; i++) {
			double a = rand.nextDouble()*360;
			double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 2.5);
			double h = dy-yCoord;
			double r = ReikaRandomHelper.getRandomPlusMinus(this.getWidth(h)+0.5, 0.125);
			double dx = x+0.5+r*Math.sin(Math.toRadians(a));
			double dz = z+0.5+r*Math.cos(Math.toRadians(a));
			int l = 30+rand.nextInt(30);
			float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			if (rand.nextBoolean())
				g = -g;
			EntityFX fx = new EntityCenterBlurFX(world, dx, dy, dz).setGravity(g).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		int nd = 8;
		int di = Minecraft.getMinecraft().gameSettings.particleSetting == 2 ? 3 : 2;
		for (int i = 0; i < nd; i += di) {
			int t = (this.getTicksExisted()+i*360/nd)*8;
			double a = t%360;
			double dy = y+0.5+(t%(360D*nd))/360D-4;
			double r = 0.5;
			double dx = x+0.5+r*Math.sin(Math.toRadians(a));
			double dz = z+0.5+r*Math.cos(Math.toRadians(a));
			int l = 60;
			EntityFX fx = new EntityCenterBlurFX(world, dx, dy, dz).setGravity(0).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public double getWidth(double i) {
		double t = System.currentTimeMillis()/50D+this.hashCode();
		if (!this.isActive()) {
			return 0.75+0.03125*Math.sin(-t/8D+i)+0.03125*Math.sin(t/16D+i);
		}
		return 0.75+0.125*Math.sin(i-t/16D)+0.0625*Math.cos(t/8D)+0.375*Math.sin(t/64D);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(3, 3.5, 3);
	}

	public boolean isActive() {
		return active;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		active = NBT.getBoolean("active");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("active", active);
	}

	public static boolean isPlayerCloaked(EntityPlayer ep) {
		return cache.lookForMatch(ep.worldObj, true, (WorldLocation loc, TileEntity tile) -> {
			TileEntityCloakingTower te = (TileEntityCloakingTower)tile;
			return te.isOwnedByPlayer(ep) && te.isActive() && loc.getCylinderDistanceTo(ep) <= MAXRANGE;
		}, (WorldLocation loc, TileEntity te) -> {
			ChromaAux.logTileCacheError(ep.worldObj, loc, te, ChromaTiles.CLOAKING);
		});
	}

	public static void clearCache() {
		cache.clear();
	}

	@Override
	public ChromaStructures getPrimaryStructure() {
		return ChromaStructures.CLOAKTOWER;
	}

	@Override
	public Coordinate getStructureOffset() {
		return null;
	}

	public boolean canStructureBeInspected() {
		return true;
	}

	public boolean hasStructure() {
		return active;
	}

}
