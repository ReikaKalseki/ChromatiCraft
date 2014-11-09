/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.Interfaces.GuiController;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAspectFormer extends CrystalReceiverBase implements GuiController {

	private Aspect selected;

	private ForgeDirection facing = ForgeDirection.DOWN;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (this.isActive()) {
			ElementTagCompound cost = this.getCost();
			if (!world.isRemote) {
				this.addAspect(selected, (IAspectContainer)this.getAdjacentTileEntity(facing), cost);
			}
			if (world.isRemote)
				this.spawnParticles(world, x, y, z, cost);
		}
	}

	public boolean isActive() {
		return selected != null && energy.containsAtLeast(this.getCost()) && this.getAdjacentTileEntity(facing) instanceof IAspectContainer;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, ElementTagCompound tag) {
		double h = 0.2875;
		for (CrystalElement e : tag.elementSet()) {
			if (ReikaRandomHelper.doWithChance(15)) {
				double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.375);
				double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.375);
				EntityCenterBlurFX fx = new EntityCenterBlurFX(e, world, rx, y+h, rz, 0, 0, 0).setColor(selected.getColor());
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		if (NBT.hasKey("aspect"))
			this.selectAspect(NBT.getString("aspect"));
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setString("aspect", selected != null ? selected.getTag() : "null");
	}

	public void selectAspect(String asp) {
		selected = Strings.isNullOrEmpty(asp) ? null : Aspect.aspects.get(asp);
	}

	public Aspect getAspect() {
		return selected;
	}

	private void addAspect(Aspect a, IAspectContainer iac, ElementTagCompound tag) {
		int amt = selected.isPrimal() ? 4 : 1;
		int left = iac.addToContainer(a, amt);
		int added = amt-left;
		if (added > 0) {
			this.drainEnergy(tag.scale(added));
		}
	}

	public ElementTagCompound getCost(Aspect a) {
		return ChromaAspectManager.instance.getElementCost(a, 2).square();
	}

	private ElementTagCompound getCost() {
		return this.getCost(selected);
	}

	private void checkAndRequest() {
		int capacity = this.getMaxStorage();
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int space = capacity-this.getEnergy(e);
			if (space > 0) {
				this.requestEnergy(e, space);
			}
		}
	}

	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public ImmutableTriple<Double, Double, Double> getTargetRenderOffset(CrystalElement e) {
		double d = 0.21875-0.5;
		double dx = d+(e.ordinal()/4)*(0.1875);
		double dz = d+(e.ordinal()%4)*(0.1875);
		//ReikaJavaLibrary.pConsole(dx+", "+dz);
		return new ImmutableTriple(dx, 0.4, dz);
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return true;
	}

	@Override
	public int maxThroughput() {
		return 100;
	}

	@Override
	public boolean canConduct() {
		return false;
	}

	@Override
	public int getMaxStorage() {
		return 20000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ASPECT;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord-1, zCoord, xCoord+1, yCoord+1, zCoord+2);
	}

}
