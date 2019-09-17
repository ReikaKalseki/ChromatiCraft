/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.CastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.RecursiveCastingAutomationSystem;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCastingAuto extends CrystalReceiverBase implements CastingAutomationBlock {

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.BLACK, 50);
		required.addTag(CrystalElement.PURPLE, 20);
		required.addTag(CrystalElement.LIGHTBLUE, 10);
		required.addTag(CrystalElement.LIGHTGRAY, 20);
	}

	private RecursiveCastingAutomationSystem handler;

	public TileEntityCastingAuto() {
		handler = new RecursiveCastingAutomationSystem(this);
	}

	public TileEntityCastingTable getTable() {
		World world = worldObj;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		for (int i = 1; i < 5; i++) {
			Block b = world.getBlock(x, y-i, z);
			if (!b.isAir(world, x, y-i, z))
				return null;
		}
		TileEntity te = world.getTileEntity(x, y-5, z);
		return te instanceof TileEntityCastingTable ? (TileEntityCastingTable)te : null;
	}

	public TileEntityCastingInjector getInjector(TileEntityCastingTable te) {
		TileEntity te2 = te.getAdjacentTileEntity(ForgeDirection.DOWN);
		return te2 instanceof TileEntityCastingInjector ? (TileEntityCastingInjector)te2 : null;
	}

	public Collection<CastingRecipe> getAvailableRecipes() {
		TileEntityCastingTable te = this.getTable();
		return te != null ? te.getCompletedRecipes() : new HashSet();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AUTOMATOR;
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		handler.destroy();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		TileEntityCastingTable te = this.getTable();

		if (te != null && world.isRemote) {
			this.doConnectionParticles(world, x, y, z, te);
		}

		if (!world.isRemote) {
			if (te != null) {
				TileEntityCastingInjector te2 = this.getInjector(te);
				if (te2 != null) {
					te2.setInactive(this);
				}
			}
			handler.tick(world);
			if (this.getCooldown() == 0 && checkTimer.checkCap()) {
				this.checkAndRequest();
			}
		}
	}

	private void checkAndRequest() {
		for (CrystalElement e : required.elementSet()) {
			int amt = this.getEnergy(e);
			int sp = this.getRemainingSpace(e);
			if (amt < sp)
				this.requestEnergy(e, sp);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doConnectionParticles(World world, int x, int y, int z, TileEntityCastingTable te) {
		double a = Math.toRadians((this.getTicksExisted()*2)%360);
		int n = 6;
		int sp = 360/n;
		double r = 0.5+0.125*Math.sin(this.getTicksExisted()/10D);
		for (int i = 0; i < 360; i += sp) {
			double ri = Math.toRadians(i);
			double dx = x+0.5+r*Math.sin(a+ri);
			double dy = te.yCoord+1;
			double dz = z+0.5+r*Math.cos(a+ri);
			EntityFX fx = new EntityGlobeFX(world, dx, dy, dz, 0, 0.125, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public boolean canCraft(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.isReadyToCraft() && te.getPlacerUUID() != null && te.getPlacerUUID().equals(this.getPlacerUUID());
	}

	public boolean isAbleToRun(TileEntityCastingTable te) {
		TileEntityCastingInjector te2 = this.getInjector(te);
		return te2 != null && te2.isAbleToRun(te) && energy.containsAtLeast(required);
	}

	@Override
	public int getInjectionTickRate() {
		return 5;
	}

	@Override
	public boolean canRecursivelyRequest(CastingRecipe c) {
		return true;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {
		//this.cancelCrafting();
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int maxThroughput() {
		return 2500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 12000;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return handler.getGridNode(dir);
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return handler.getActionableNode();
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.COVERED;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	public boolean canTriggerCrafting() {
		return true;
	}

	@Override
	public CastingAutomationSystem getAutomationHandler() {
		return handler;
	}

	@Override
	public void consumeEnergy(CastingRecipe c, TileEntityCastingTable te, ItemStack is) {
		ElementTagCompound tag = required.copy();
		tag.scale(c.getAutomationCostFactor(this, te, is));
		this.drainEnergy(tag);
	}

	@Override
	public boolean canPlaceCentralItemForMultiRecipes() {
		return true;
	}

	@Override
	public TileEntity getItemPool() {
		return this.getAdjacentTileEntity(ForgeDirection.UP);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		handler.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		handler.readFromNBT(NBT);
	}

	@Override
	public void breakBlock() {
		handler.onBreak(worldObj);
	}

}
