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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.GuiController;

import com.google.common.base.Strings;

public class TileEntityAspectFormer extends CrystalReceiverBase implements GuiController {

	private Aspect selected;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (selected != null) {
			ElementTagCompound cost = this.getCost();
			if (energy.containsAtLeast(cost)) {
				TileEntity te = this.getAdjacentTileEntity(ForgeDirection.DOWN);
				if (te instanceof IAspectContainer) {
					this.addAspect(selected, (IAspectContainer)te, cost);
				}
			}
		}
	}

	public void selectAspect(String asp) {
		selected = Strings.isNullOrEmpty(asp) ? null : Aspect.aspects.get(asp);
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
		return null;
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

}
