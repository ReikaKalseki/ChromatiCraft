/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.DragonAPI.ASM.APIStripper.Strippable;

import buildcraft.api.tiles.IHasWork;

@Strippable("buildcraft.api.tiles.IHasWork")
public interface OperationInterval extends IHasWork {

	public float getOperationFraction();

	public OperationState getState();

	public static enum OperationState {

		INVALID(),
		PENDING(),
		RUNNING();

	}

}
