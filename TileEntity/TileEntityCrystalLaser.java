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

import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.Base.OneSlotMachine;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCrystalLaser extends InventoriedChromaticBase implements OneSlotMachine {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.LASER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getFacing();

		if (this.isActive()) {
			this.applyEffects(world, x, y, z, dir);

			if (world.isRemote) {
				this.spawnParticle(world, x, y, z, dir);
			}
		}
	}

	public boolean isActive() {
		return ChromaItems.LENS.matchWith(inv[1]) && this.getStoredEnergy(this.getColor()) > 0;
	}

	private int getStoredEnergy(CrystalElement e) {
		if (e == null)
			return 0;
		if (ChromaItems.STORAGE.matchWith(inv[0])) {
			ItemStack is = inv[0];
			return ((ItemStorageCrystal)is.getItem()).getStoredEnergy(is, e);
		}
		return 0;
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
		if (!this.isActive())
			return 0;
		int energy = ((ItemStorageCrystal)inv[0].getItem()).getStoredEnergy(inv[0], this.getColor());
		return (int)Math.min(Math.sqrt(energy), 128);
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

}
