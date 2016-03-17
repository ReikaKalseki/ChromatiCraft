/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Libraries.ReikaDispenserHelper;


public class ManipulatorDispenserAction implements IBehaviorDispenseItem {

	@Override
	public ItemStack dispense(IBlockSource ibs, ItemStack is) {
		ChromatiCraft.logger.debug("Manipulator used in dispenser, IBS="+ibs);
		TileEntity te = ibs.getBlockTileEntity();
		if (te instanceof TileEntityItemStand) {
			((TileEntityItemStand)te).onRightClickWith(null, ReikaDispenserHelper.getDispenserPlayer(ibs, is));
		}
		if (te instanceof TileEntityAuraInfuser) {
			((TileEntityAuraInfuser)te).onRightClickWith(null, ReikaDispenserHelper.getDispenserPlayer(ibs, is));
		}
		return null;
	}

}
