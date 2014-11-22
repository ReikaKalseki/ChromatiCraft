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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromaStructures;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.Render.Particle.EntityFlareFX;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAuraInfuser extends InventoriedChromaticBase implements ItemOnRightClick {

	private InertItem item;

	private int craftingTick = 0;
	private boolean hasStructure = true;

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.PURPLE, 500);
		required.addTag(CrystalElement.BLACK, 2500);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (hasStructure/* && energy.containsAtLeast(required)*/) {
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
		this.validateMultiblock();
	}

	public void validateMultiblock() {
		hasStructure = ChromaStructures.getInfusionStructure(worldObj, xCoord, yCoord, zCoord).matchInWorld();
		if (!hasStructure) {
			if (craftingTick > 0) {
				this.killCrafting();
			}
			craftingTick = 0;
		}
		this.markDirty();
		this.syncAllData(false);
	}

	private void killCrafting() {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		hasStructure = NBT.getBoolean("struct");

		craftingTick = NBT.getInteger("craft");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("struct", hasStructure);

		NBT.setInteger("craft", craftingTick);
	}

	private void onCraftingTick(World world, int x, int y, int z) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);

		if (craftingTick%304 == 0) {
			ChromaSounds.INFUSION.playSoundAtBlock(this);
		}
		craftingTick--;

		if (craftingTick == 0) {
			;//this.drainEnergy(required);
			this.craft();
			if (world.isRemote) {
				this.craftParticles(world, x, y, z);
			}
		}
	}

	private void craft() {
		ChromaSounds.INFUSE.playSoundAtBlock(this);
		inv[0] = ReikaItemHelper.getSizedItemStack(ChromaStacks.iridCrystal, inv[0].stackSize);
		FilledBlockArray arr = ChromaStructures.getInfusionStructure(worldObj, xCoord, yCoord, zCoord);
		for (int i = 0; i < arr.getSize(); i++) {
			int[] xyz = arr.getNthBlock(i);
			int dx = xyz[0];
			int dy = xyz[1];
			int dz = xyz[2];
			if (arr.hasBlockAt(dx, dy, dz, ChromaBlocks.CHROMA.getBlockInstance(), 0))
				worldObj.setBlock(dx, dy, dz, Blocks.air);
		}
		this.validateMultiblock();
		this.markDirty();
	}

	@SideOnly(Side.CLIENT)
	private void craftParticles(World world, int x, int y, int z) {
		for (int i = 0; i < 360; i += 15) {
			double ang = Math.toRadians(ReikaRandomHelper.getRandomPlusMinus(i, 5));
			double v = 0.075;
			double vx = v*Math.sin(ang);
			double vz = v*Math.cos(ang);
			EntityFlareFX fx = new EntityFlareFX(CrystalElement.WHITE, world, x+0.5, y+0.5, z+0.5, vx, 0, vz);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
		if (flag) {
			item = is != null ? new InertItem(worldObj, is) : null;
		}

		if (ReikaItemHelper.matchStacks(inv[0], ChromaStacks.rawCrystal)) {
			if (craftingTick == 0)
				craftingTick = 608;
		}
		else {
			if (craftingTick > 0) {
				this.killCrafting();
			}
			craftingTick = 0;
		}
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
			double v = tall ? 0.0425*(1+fac) : ReikaRandomHelper.getRandomPlusMinus(0.0425, 0.005);
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
	/*
	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage() {
		return 5000;
	}
	 */
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

	public int getCraftingTick() {
		return craftingTick;
	}

}
