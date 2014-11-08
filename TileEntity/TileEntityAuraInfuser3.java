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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedFiberPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAuraInfuser3 extends InventoriedFiberPowered implements ItemOnRightClick {

	private InertItem item;

	private int craftingTick = 200;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.PURPLE, 500);
		required.addTag(CrystalElement.BLACK, 2500);
	}

	private boolean isCollecting() {
		return ReikaMathLibrary.isValueInsideBounds(0, 40, craftingTick);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		energy.addValueToColor(CrystalElement.randomElement(), 4000);
		craftingTick = 250;
		if (craftingTick == 0)
			craftingTick = 250;
		if (energy.containsAtLeast(required)) {
			if (craftingTick > 0) {
				this.onCraftingTick(world, x, y, z);
			}
		}
		else {
			craftingTick = 0;
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {

	}

	private void onCraftingTick(World world, int x, int y, int z) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		craftingTick--;

		if (craftingTick == 0) {
			this.drainEnergy(required);
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();

		ItemStack is = inv[0];
		boolean flag = false;
		if (item == null)
			flag = is != null;
		else if (!ReikaItemHelper.matchStacks(inv[0], item.getEntityItem()))
			flag = true;
		if (flag)
			item = is != null ? new InertItem(worldObj, is) : null;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double ang = Math.toRadians(this.getTicksExisted()*2%360);
		float fac = (float)Math.sin(Math.toRadians(this.getTicksExisted()*4));
		float s = 1.25F+0.25F*fac;
		for (int i = 0; i < 360; i += 60) {
			boolean tall = i%120 == 0;
			float g = tall ? 0.375F*(0.5F+0.5F*fac) : 0.375F;
			double a = ang+Math.toRadians(i);
			double r = 1.85;
			double v = tall ? 0.0425*(1+fac) : 0.0425;
			double px = x+0.5+r*Math.sin(a);
			double py = y-0.75;
			double pz = z+0.5+r*Math.cos(a);
			double vx = -v*(px-x-0.5);
			double vy = 0.3;
			double vz = -v*(pz-z-0.5);
			EntityChromaFluidFX fx = new EntityChromaFluidFX(CrystalElement.WHITE, world, px, py, pz, vx, vy, vz).setScale(s).setGravity(g);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canExtractItem(int side, ItemStack is, int slot) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 16;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReikaItemHelper.matchStacks(is, ChromaStacks.rawCrystal);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INFUSER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public EntityItem getItem() {
		return item;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage() {
		return 5000;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		if (item != null && !this.isItemValidForSlot(0, item))
			return item;
		if (inv[0] != null)
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, inv[0]);
		inv[0] = item != null ? item.copy() : null;
		this.markDirty();
		return null;
	}

}
