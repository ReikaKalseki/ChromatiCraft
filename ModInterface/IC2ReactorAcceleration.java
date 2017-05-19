/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import ic2.api.reactor.IReactorChamber;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator.Acceleration;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;

public class IC2ReactorAcceleration extends Acceleration {

	public static final IC2ReactorAcceleration instance = new IC2ReactorAcceleration();

	private Method tick;

	public void register() {
		String s = "ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric";
		String s2 = "ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric";
		this.registerClass(s);
		this.registerClass(s2);

		try {
			Class c = Class.forName(s);
			tick = c.getDeclaredMethod("updateEntityServer");
			tick.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.IC2, e);
			ChromatiCraft.logger.logError("Could not find IC2 reactor internal members!");
		}
	}

	@Override
	protected void tick(TileEntity te, int factor) throws Exception {
		if (te.worldObj.isRemote)
			return;
		if (te instanceof IReactorChamber) {
			te = (TileEntity)((IReactorChamber)te).getReactor();
		}
		for (int i = 0; i < factor; i++)
			tick.invoke(te);
	}

}
