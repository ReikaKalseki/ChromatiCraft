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

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator.Acceleration;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;

@Deprecated
public class RailCraftMultiBlockAcceleration extends Acceleration {

	public static final RailCraftMultiBlockAcceleration instance = new RailCraftMultiBlockAcceleration();

	private Method getMaster;

	public void register() {
		String s = "blusunrize.immersiveengineering.common.blocks.metal";
		this.registerClass(s);

		try {
			Class c = Class.forName(s);
			getMaster = c.getDeclaredMethod("master");
			getMaster.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.IMMERSIVEENG, e);
			ChromatiCraft.logger.logError("Could not find IE Multiblock internal members!");
		}
	}

	@Override
	protected void tick(TileEntity te, int factor) throws Exception {
		for (int k = 0; k < factor; k++) {
			te.updateEntity();
		}
	}

	@Override
	protected TileEntity getActingTileEntity(TileEntity root) throws Exception {
		TileEntity relay = (TileEntity)getMaster.invoke(root);
		return relay != null ? relay : root;
	}

	@Override
	public boolean usesParentClasses() {
		return true;
	}

}
