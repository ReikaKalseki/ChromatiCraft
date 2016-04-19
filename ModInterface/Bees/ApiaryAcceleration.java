/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator.Acceleration;
import forestry.api.apiculture.IBeeHousing;

public class ApiaryAcceleration extends Acceleration {

	public static final ApiaryAcceleration instance = new ApiaryAcceleration();

	public void register() {
		//Until complete
		//this.registerClass("forestry.apiculture.gadgets.TileApiary");
		//this.registerClass("forestry.apiculture.gadgets.TileAlvearyPlain");
		//this.registerClass("net.bdew.gendustry.machines.apiary.TileApiary");
	}

	private void registerClass(String sg) {
		Class c = null;
		try {
			c = Class.forName(sg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null)
			TileEntityAccelerator.customizeTile(c, this);
	}

	@Override
	protected void tick(TileEntity te, int factor) {
		IBeeHousing ibh = (IBeeHousing)te;
		//need logic access and access to its internal timers
	}

}
