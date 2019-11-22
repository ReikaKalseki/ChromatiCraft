/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.Interfaces.MultiBlockChromaTile;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRelaySource extends InventoriedCrystalReceiver implements InertIInv, ItemOnRightClick, MultiBlockChromaTile {

	private static final int CAPACITY = 720000;
	private static final int CAPACITY_BOOSTED = 3600000;

	private int cooldown = 200;

	private int[] drainValue = new int[16];

	private boolean enhanced;
	private boolean hasEnhancedStructure;

	@Override
	protected int getCooldownLength() {
		return cooldown;
	}

	/*
	@Override
	public ResearchLevel getResearchTier() {
		return ResearchLevel.ENERGYEXPLORE;
	}
	 */

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (world.isRemote && enhanced) {
			this.spawnParticles(world, x, y, z);
		}

		int maxDrain = 0;
		for (int i = 0; i < 16; i++) {
			drainValue[i] *= 0.95;
			maxDrain += drainValue[i];
		}
		maxDrain /= 16;
		if (cooldown > 100 && maxDrain >= CAPACITY/cooldown)
			cooldown--;
		else if (cooldown < 200)
			cooldown++;

		checkTimer.setCap(cooldown);

		if (inv[0] != null && ChromaItems.STORAGE.matchWith(inv[0])) {
			for (CrystalElement e : ItemStorageCrystal.getStoredTags(inv[0]).elementSet()) {
				int amt = ItemStorageCrystal.getStoredEnergy(inv[0], e);
				int add = Math.min(amt, Math.min(this.getMaxStorage(e)-energy.getValue(e), this.maxThroughput()*4));
				if (add > 0) {
					ItemStorageCrystal.removeEnergy(inv[0], e, add);
					energy.addValueToColor(e, add);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		if (rand.nextInt(3) == 0) {
			int dx = rand.nextBoolean() ? x+1 : x-1;
			int dz = rand.nextBoolean() ? z+1 : z-1;
			int dy = y-2;
			double px = dx+rand.nextDouble();
			double py = dy+rand.nextDouble();
			double pz = dz+rand.nextDouble();
			double v = 0.03125;
			double vx = (x+0.5-px)*v;
			double vz = (z+0.5-pz)*v;
			double vy = ReikaRandomHelper.getRandomBetween(0.125, 0.1875);
			EntityChromaFluidFX fx = new EntityChromaFluidFX(world, px, py, pz, vx, vy, vz).setGravity((float)vy*0.75F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}

		if (rand.nextInt(2) == 0) {
			int color = CrystalElement.getBlendedColor(this.getTicksExisted(), 50);
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 2.25);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 2.25);
			double py = ReikaRandomHelper.getRandomPlusMinus(y+0.25, 0.5)+0.5;
			float g = (float)ReikaRandomHelper.getRandomBetween(0.03125, 0.1);
			int l = ReikaRandomHelper.getRandomPlusMinus(40, 100);
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setColor(color).setGravity(g).setLife(l).setScale(1.25F).setRapidExpand().setIcon(ChromaIcons.FADE_GENTLE);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		EntityPlayer ep = this.getPlacer();
		if (ep != null && !ReikaPlayerAPI.isFake(ep) && !world.isRemote) {
			this.validateStructure();
			enhanced = ChromaResearchManager.instance.playerHasFragment(ep, ChromaResearch.RELAYSTRUCT) && hasEnhancedStructure;
		}
		else {
			//enhanced = false;
		}
	}

	public void validateStructure() {
		ChromaStructures.RELAY.getStructure().resetToDefaults();
		hasEnhancedStructure = ChromaStructures.RELAY.getArray(worldObj, xCoord, yCoord, zCoord).matchInWorld();
	}

	private void checkAndRequest() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int space = this.getRemainingSpace(e);
			if (space > this.getEnergy(e)) { // < 50% full
				this.requestEnergy(e, space);
			}
		}
	}

	@Override
	public int getReceiveRange() {
		return enhanced ? 48 : 32;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return enhanced ? 30000 : 6000;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return enhanced ? CAPACITY_BOOSTED : CAPACITY;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.RELAYSOURCE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		enhanced = NBT.getBoolean("enhance");
		hasEnhancedStructure = NBT.getBoolean("enstruct");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("enhance", enhanced);
		NBT.setBoolean("enstruct", hasEnhancedStructure);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return side == 0;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ChromaItems.STORAGE.matchWith(is) && ItemStorageCrystal.getTotalEnergy(is) > 0;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		this.dropItem(inv[0]);
		inv[0] = null;
		if (this.isItemValidForSlot(0, item)) {
			inv[0] = item.copy();
			item = null;
		}
		return item;
	}

	private void dropItem(ItemStack is) {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.75, zCoord+0.5, is);
	}

	public void onDrain(CrystalElement e, int amt) {
		drainValue[e.ordinal()] += amt;
	}

	public boolean isEnhanced() {
		return enhanced;
	}

	@SideOnly(Side.CLIENT)
	public void setEnhanced(boolean en) {
		enhanced = en;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return enhanced ? ReikaAABBHelper.getBlockAABB(this).expand(3, 2, 3) : ReikaAABBHelper.getBlockAABB(this);
	}

}
