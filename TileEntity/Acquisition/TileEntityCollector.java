/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Acquisition;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaExtractable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.FluidIOInventoryBase;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.ReikaXPFluidHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCollector extends FluidIOInventoryBase implements OwnedTile, ChromaExtractable {

	public static final int XP_PER_CHROMA = 1; //1 xp per mB of liquid
	private static final int XP_PER_BOTTLE = 300;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.COLLECTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

		if (output.canTakeIn(XP_PER_BOTTLE))
			this.internalizeXP();

		if (!input.isEmpty() && !this.hasRedstoneSignal()) {
			FluidStack fs = ReikaXPFluidHelper.getFluid();
			if (fs != null) {
				int speed = this.getConversionSpeed();
				int produce = Math.min(speed, input.getLevel()/fs.amount/XP_PER_CHROMA);
				if (produce > 0) {
					if (output.canTakeIn(produce)) {
						input.removeLiquid(XP_PER_CHROMA*speed*fs.amount);
						output.addLiquid(produce, FluidRegistry.getFluid("chroma"));
					}
				}
			}
		}

		for (EntityPlayer ep : this.getOwners(false)) {
			this.tryIntakeXPFromPlayer(ep, true);
		}

		if (world.isRemote && rand.nextInt(4) == 0)
			this.spawnParticles(world, x, y, z, meta);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {
		double r = 0.75;
		double dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		double dy = rand.nextDouble();
		double dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		while (ReikaMathLibrary.py3d(dx, 0, dz) < 0.65) {
			dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		}

		CrystalElement e = CrystalElement.randomElement();
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, x+dx+0.5, y+dy+0.5, z+dz+0.5, e));
	}

	private void internalizeXP() {
		if (inv[0] != null && inv[0].getItem() == Items.experience_bottle && (inv[1] == null || inv[1].stackSize < inv[1].getMaxStackSize())) {
			ReikaInventoryHelper.decrStack(0, inv);
			output.addLiquid(XP_PER_BOTTLE, FluidRegistry.getFluid("chroma"));
			ReikaInventoryHelper.addOrSetStack(Items.glass_bottle, 1, 0, inv, 1);
		}
	}

	private int getConversionSpeed() {
		return 5;
	}

	public void tryIntakeXPFromPlayer(EntityPlayer ep, boolean doAABB) {
		if (output.canTakeIn(this.getConversionSpeed()))
			this.intakeXPFromPlayer(ep, doAABB);
	}

	private void intakeXPFromPlayer(EntityPlayer ep, boolean doAABB) {
		if (ep != null && !(doAABB ? ReikaPlayerAPI.isFakeOrNotInteractable(ep, xCoord+0.5, yCoord+0.5, zCoord+0.5, 2) : ReikaPlayerAPI.isFake(ep))) {
			int mult = this.getConversionSpeed();
			if (ep.experienceTotal >= XP_PER_CHROMA*mult) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(xCoord, yCoord, zCoord).offset(0, 1, 0);
				if (!doAABB || ep.boundingBox.intersectsWith(box)) {
					int add = Math.min(output.getRemainingSpace(), mult);
					if (add > 0) {
						output.addLiquid(add, FluidRegistry.getFluid("chroma"));
						ReikaPlayerAPI.removeExperience(ep, XP_PER_CHROMA*add);
						ProgressStage.MAKECHROMA.stepPlayerTo(ep);
					}
				}
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getCapacity() {
		return 3000;
	}

	@Override
	public boolean isValidFluid(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("xp")))
			return true;
		if (f.equals(FluidRegistry.getFluid("experience")))
			return true;
		if (f.equals(FluidRegistry.getFluid("xpjuice")))
			return true;
		if (ReikaXPFluidHelper.fluidsExist() && f.equals(ReikaXPFluidHelper.getFluid().getFluid()))
			return true;
		return false;
	}

	@Override
	public Fluid getInputFluid() {
		return null;
	}

	@Override
	public boolean canOutputTo(ForgeDirection to) {
		return true;
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && itemstack.getItem() == Items.experience_bottle;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	public int getProgressScaled(int i) {
		return 0;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

	@Override
	public int getChromaLevel() {
		return output.getLevel();
	}

	@Override
	public void addTooltipInfo(List li, boolean shift) {

	}

}
