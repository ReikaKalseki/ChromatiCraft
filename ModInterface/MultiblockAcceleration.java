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

import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityAccelerator.Acceleration;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;

public class MultiblockAcceleration extends Acceleration {

	public static final MultiblockAcceleration instance = new MultiblockAcceleration();

	public void register() {
		for (Class c : MultiblockControllerFinder.instance.getClasses()) {
			this.registerClass(c);
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
		return MultiblockControllerFinder.instance.getController(root);
	}

	@Override
	public boolean usesParentClasses() {
		return true;
	}

}
