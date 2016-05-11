/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class CrystalMaterial extends Material {

	public CrystalMaterial() {
		super(MapColor.quartzColor);
		//this.setRequiresTool();
		this.setImmovableMobility();
	}

	@Override
	public boolean blocksMovement()
	{
		return true;
	}

	@Override
	public boolean isOpaque()
	{
		return false;
	}

}
