/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

@Strippable(value={"thaumcraft.api.aspects.IAspectContainer", "thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityAspectJar extends TileEntityChromaticBase implements IAspectContainer, IEssentiaTransport, NBTTile {

	public static final int CAPACITY = 240000;

	private AspectTank tank = new AspectTank(CAPACITY);

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ASPECTJAR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getAspects() {
		return new AspectList().add(tank.aspect, tank.level);
	}

	public int getRemainingSpace() {
		return CAPACITY-tank.level;
	}

	public int getAmount() {
		return tank.level;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getAspect() {
		return tank.aspect;
	}

	private void removeAspect(int amt) {
		tank.level -= amt;
		if (tank.level <= 0) {
			tank.level = 0;
			tank.aspect = null;
		}
	}

	@ModDependent(ModList.THAUMCRAFT)
	private void addAspect(Aspect tag, int add) {
		tank.level += add;
		tank.aspect = tag;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setAspects(AspectList aspects) {
		tank.aspect = aspects.getAspects()[0];
		tank.level = aspects.getAmount(tank.aspect);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerAccept(Aspect tag) {
		return tank.aspect == null || (tag == tank.aspect && tank.level < CAPACITY);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addToContainer(Aspect tag, int amount) {
		if (this.doesContainerAccept(tag)) {
			int add = Math.min(amount, this.getRemainingSpace());
			this.addAspect(tag, add);
			return amount-add;
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(Aspect tag, int amount) {
		if (tag == tank.aspect && tank.level >= amount) {
			this.removeAspect(amount);
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(AspectList ot) {
		return this.takeFromContainer(ot.getAspects()[0], ot.getAmount(ot.getAspects()[0]));
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		return tank.aspect == tag && amount >= amount;
	}

	@Override
	@Deprecated
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerContain(AspectList ot) {
		return this.doesContainerContainAmount(ot.getAspects()[0], ot.getAmount(ot.getAspects()[0]));
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int containerContains(Aspect tag) {
		return tank.aspect == tag ? tank.level : 0;
	}

	@Override
	public boolean isConnectable(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		return true;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		return true;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setSuction(Aspect aspect, int amount) {

	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getSuctionType(ForgeDirection face) {
		return tank.aspect;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		return tank.aspect != null ? 64 : 32;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		int rem = tank.aspect == aspect ? Math.min(tank.level, amount) : 0;
		this.removeAspect(rem);
		return rem;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		int add = this.doesContainerAccept(aspect) ? Math.min(amount, this.getRemainingSpace()) : 0;
		this.addAspect(aspect, add);
		return add;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getEssentiaType(ForgeDirection face) {
		return tank.aspect;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		return tank.level;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		tank.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tank.readFromNBT(NBT);
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		tank.writeToNBT(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound != null && ReikaItemHelper.matchStacks(is, ChromaTiles.ASPECTJAR.getCraftedProduct()))
			tank.readFromNBT(is.stackTagCompound);
	}

	private static class AspectTank {

		public final int capacity;
		private int level;
		@ModDependent(ModList.THAUMCRAFT)
		private Aspect aspect;

		private AspectTank(int c) {
			capacity = c;
		}

		public void writeToNBT(NBTTagCompound NBT) {
			if (!ModList.THAUMCRAFT.isLoaded())
				return;
			NBT.setString("aspect", aspect != null ? aspect.getTag() : "null");
			NBT.setInteger("amount", level);
		}

		public void readFromNBT(NBTTagCompound NBT) {
			if (!ModList.THAUMCRAFT.isLoaded())
				return;
			String s = NBT.getString("aspect");
			aspect = s.equals("null") || s.isEmpty() ? null : Aspect.getAspect(s);
			level = NBT.getInteger("amount");
		}

	}

}
