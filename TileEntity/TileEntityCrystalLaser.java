/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.ChromatiCraft.Base.TileEntity.ChargedCrystalPowered;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.OneSlotMachine;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCrystalLaser extends ChargedCrystalPowered implements OneSlotMachine {

	private int range;
	private StepTimer rangeTimer = new StepTimer(20);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LASER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getFacing();

		if (DragonAPICore.debugtest) {
			ItemStack is = ChromaItems.STORAGE.getStackOfMetadata(4);
			((ItemStorageCrystal)is.getItem()).addEnergy(is, CrystalElement.WHITE, 20000);
			inv[0] = is;
			inv[1] = ChromaItems.LENS.getStackOfMetadata(CrystalElement.WHITE.ordinal());
		}

		rangeTimer.update();
		if (rangeTimer.checkCap()) {
			range = this.updateRange(dir);
		}

		if (this.isActive()) {
			this.applyEffects(world, x, y, z, dir);

			int c = 4+4*Minecraft.getMinecraft().gameSettings.particleSetting;
			if (world.isRemote && rand.nextInt(c) == 0) {
				this.spawnParticle(world, x, y, z, dir);
			}
		}
	}

	private int updateRange(ForgeDirection dir) {
		if (!this.isActive())
			return 0;
		int energy = ((ItemStorageCrystal)inv[0].getItem()).getStoredEnergy(inv[0], this.getColor());
		int max = (int)Math.min(Math.sqrt(energy), 128);
		for (int i = 1; i <= max; i++) {
			int dx = xCoord+dir.offsetX*i;
			int dy = yCoord+dir.offsetY*i;
			int dz = zCoord+dir.offsetZ*i;
			Block b = worldObj.getBlock(dx, dy, dz);
			if (b != Blocks.air && b.isOpaqueCube())
				return i;
		}
		return max;
	}

	public boolean isActive() {
		return ChromaItems.LENS.matchWith(inv[1]) && this.getStoredEnergy(this.getColor()) > 0;
	}

	private void applyEffects(World world, int x, int y, int z, ForgeDirection dir) {
		int r = this.getRange();
		for (int i = 1; i <= r; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
		}
		((ItemStorageCrystal)inv[0].getItem()).removeEnergy(inv[0], this.getColor(), 1);
	}

	private void spawnParticle(World world, int x, int y, int z, ForgeDirection dir) {
		int num = 1+this.getRange()/32;
		for (int i = 0; i < num; i++) {
			double r = rand.nextDouble()*this.getRange();
			double rx = x+0.5+r*dir.offsetX;
			double ry = y+0.5+r*dir.offsetY;
			double rz = z+0.5+r*dir.offsetZ;
			double px = ReikaRandomHelper.getRandomPlusMinus(rx, 0.25);
			double py = ReikaRandomHelper.getRandomPlusMinus(ry, 0.25);
			double pz = ReikaRandomHelper.getRandomPlusMinus(rz, 0.25);
			EntityLaserFX fx = new EntityLaserFX(this.getColor(), world, px, py, pz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
		case 0:
			return ForgeDirection.WEST;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.SOUTH;
		case 4:
			return ForgeDirection.UP;
		case 5:
			return ForgeDirection.DOWN;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public CrystalElement getColor() {
		return inv[1] != null && ChromaItems.LENS.matchWith(inv[1]) ? CrystalElement.elements[inv[1].getItemDamage()] : null;
	}

	public int getRange() {
		return range;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		if (slot == 0)
			return this.getStoredEnergy(this.getColor()) == 0;
		return side == 0;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public ItemStack swapLens(ItemStack is) {
		ItemStack ret = inv[1] != null ? inv[1].copy() : null;
		inv[1] = is != null ? is.copy() : null;
		return ret;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot == 1)
			return ChromaItems.LENS.matchWith(is);
		if (slot == 0)
			return ChromaItems.STORAGE.matchWith(is);
		return false;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		int r = this.getRange();
		ForgeDirection f = this.getFacing();
		int fx = f.offsetX;
		int fy = f.offsetY;
		int fz = f.offsetZ;
		return r > 0 ? ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).expand(fx*r, fy*r, fz*r) : super.getRenderBoundingBox();
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		range = NBT.getInteger("range");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("range", range);
	}

}
