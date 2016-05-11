/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;


public interface OperationInterval {

	public float getOperationFraction();

	public OperationState getState();

	public static enum OperationState {

		INVALID(),
		PENDING(),
		RUNNING();

	}

}
