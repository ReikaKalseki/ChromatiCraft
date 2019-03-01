/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Magic.Artefact.UABombingEffect.BlockEffect;


public class ExplodeEffect extends BlockEffect {

	@Override
	public void trigger(IInventory inv, TileEntity te) {
		te.worldObj.createExplosion(null, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, 3, false);
		te.worldObj.setBlock(te.xCoord, te.yCoord, te.zCoord, Blocks.air);
	}

}
