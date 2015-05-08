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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.ModInteract.CompoundAspectTank;
import Reika.DragonAPI.Interfaces.HitAction;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper;

@Strippable(value={"thaumcraft.api.aspects.IAspectSource", "thaumcraft.api.aspects.IEssentiaTransport"})
public class TileEntityAspectJar extends TileEntityChromaticBase implements IAspectSource, IEssentiaTransport, NBTTile, HitAction {

	public static final int CAPACITY = 500;
	public static final int CAPACITY_PRIMAL = 6000;

	@ModDependent(ModList.THAUMCRAFT)
	private PrimalBiasAspectTank tank;
	private JarTilt angle = null;

	public TileEntityAspectJar() {
		if (ModList.THAUMCRAFT.isLoaded())
			tank = new PrimalBiasAspectTank(CAPACITY, CAPACITY_PRIMAL, 16);
	}

	public static class PrimalBiasAspectTank extends CompoundAspectTank {

		public final int primalCapacity;

		public PrimalBiasAspectTank(int c, int pc, int max) {
			super(c, max);
			primalCapacity = pc;
		}

		@Override
		public int getMaxCapacity(Aspect a) {
			return a.isPrimal() ? primalCapacity : capacity;
		}
	}

	public static class JarTilt {

		public final ForgeDirection direction;
		private float angle = 0;
		private boolean increasing = true;
		private final float maxAngle;

		private JarTilt(ForgeDirection dir, float max) {
			direction = dir;
			maxAngle = max;
		}

		public float getAngle() {
			return angle;
		}

		private boolean update() {
			float speed = 1+(maxAngle-angle)/4F;
			if (increasing) {
				angle += speed;
				if (angle >= maxAngle) {
					angle = maxAngle;
					increasing = false;
				}
			}
			else {
				angle -= speed;
				if (angle <= 0) {
					angle = 0;
					return true;
				}
			}
			return false;
		}

	}

	public JarTilt getAngle() {
		return angle;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ASPECTJAR;
	}

	@Override
	public void onHit(World world, int x, int y, int z, EntityPlayer ep) {
		ReikaSoundHelper.playBreakSound(world, x, y, z, ThaumItemHelper.BlockEntry.JAR.getBlock());
		if (angle == null) {
			ForgeDirection dir = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false);
			angle = new JarTilt(dir, 30);
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (angle != null) {
			if (angle.update())
				angle = null;
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public AspectList getAspects() {
		AspectList al = new AspectList();
		for (Aspect a : tank.getAspects()) {
			al.add(a, tank.getLevel(a));
		}
		return al;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public int getAmount(Aspect a) {
		return tank.getLevel(a);
	}

	@ModDependent(ModList.THAUMCRAFT)
	public Collection<Aspect> getAllAspects() {
		return tank.getAspects();
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setAspects(AspectList aspects) {
		if (aspects.size() == 0) {
			tank.empty();
		}
		else {
			Aspect a = aspects.getAspects()[0];
			tank.setAspect(a, aspects.getAmount(a));
		}
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean doesContainerAccept(Aspect tag) {
		return tank.canAccept(tag);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addToContainer(Aspect tag, int amount) {
		if (this.doesContainerAccept(tag)) {
			int add = Math.min(amount, tank.getRemainingSpace(tag));
			tank.addAspect(tag, add);
			return amount-add;
		}
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean takeFromContainer(Aspect tag, int amount) {
		if (tag == tank.getAspects() && tank.getLevel(tag) >= amount) {
			tank.drainAspect(tag, amount);
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
		return tank.getAspects() == tag && amount >= amount;
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
		return tank.getAspects() == tag ? tank.getLevel(tag) : 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean isConnectable(ForgeDirection face) {
		return true;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean canInputFrom(ForgeDirection face) {
		return true;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
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
		return tank.getFirstAspect();
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getSuctionAmount(ForgeDirection face) {
		return tank.getAspects() != null ? 64 : 32;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return tank.getAspects() == aspect ? tank.drainAspect(aspect, amount) : 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return tank.canAccept(aspect) ? tank.addAspect(aspect, amount) : 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getEssentiaType(ForgeDirection face) {
		return tank.getFirstAspect();
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getEssentiaAmount(ForgeDirection face) {
		return tank.getLevel(tank.getFirstAspect());
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (ModList.THAUMCRAFT.isLoaded())
			tank.writeToNBT(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (ModList.THAUMCRAFT.isLoaded())
			tank.readFromNBT(NBT);
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		if (ModList.THAUMCRAFT.isLoaded())
			tank.writeToNBT(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		if (is.stackTagCompound != null && ReikaItemHelper.matchStacks(is, ChromaTiles.ASPECTJAR.getCraftedProduct()) && ModList.THAUMCRAFT.isLoaded())
			tank.readFromNBT(is.stackTagCompound);
	}

	@ModDependent(ModList.THAUMCRAFT)
	public boolean hasAspects() {
		return !tank.isEmpty();
	}

	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getFirstAspect() {
		return tank.getFirstAspect();
	}

	public static ArrayList<String> parseNBT(NBTTagCompound tag) {
		ArrayList<String> li = new ArrayList();
		if (ModList.THAUMCRAFT.isLoaded()) {
			CompoundAspectTank cat = new CompoundAspectTank(Integer.MAX_VALUE);
			cat.readFromNBT(tag);
			li.add("Aspects: ");
			for (Aspect a : cat.getAspects()) {
				int amt = cat.getLevel(a);
				li.add("  "+a.getName()+": "+amt);
			}
		}
		return li;
	}

}
