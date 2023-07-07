/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator.SpecialAcceleration;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.ModInteract.DeepInteract.ForestryMultiblockControllerHandling;

import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IFarmController;
import forestry.api.multiblock.IMultiblockLogicFarm;

public class ForestryMultifarmAcceleration extends SpecialAcceleration {

	public static final ForestryMultifarmAcceleration instance = new ForestryMultifarmAcceleration();

	//private Method tick;

	public void register() {
		this.registerClass("forestry.farming.tiles.TileFarmControl");

		/*
		try {
			Class c = Class.forName("forestry.farming.multiblock.FarmController");
			tick = c.getDeclaredMethod("updateServer");
			tick.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
			ChromatiCraft.logger.logError("Could not find Forestry Multifarm internal members!");
		}
		 */
	}

	@Override
	protected void tick(TileEntity te, int factor, TileEntity accelerator) throws Exception {
		IFarmComponent ifr = (IFarmComponent)te;
		IMultiblockLogicFarm imf = ifr.getMultiblockLogic();
		IFarmController ifc = imf.getController();
		if (ForestryMultiblockControllerHandling.isMultiblockController(ifc)) {
			for (int k = 0; k < factor; k++) {
				ForestryMultiblockControllerHandling.tickMultiblock(ifc, accelerator);
			}
		}
	}

	@Override
	public boolean usesParentClasses() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Accelerates farm operation";
	}

	@Override
	public Collection<GuiItemDisplay> getRelevantItems() {
		ArrayList<GuiItemDisplay> li = new ArrayList();
		for (int i = 0; i <= 5; i++)
			if (i != 1)
				li.add(new GuiStackDisplay("Forestry:ffarm:"+i));
		return li;
	}

}
