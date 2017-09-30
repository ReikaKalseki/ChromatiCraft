/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProjectileFiringTool;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaDispenserHelper;

public class ToolDispenserHandlers {

	public static class ManipulatorDispenserAction implements IBehaviorDispenseItem {

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
			return is;
		}

	}

	public static class ProjectileToolDispenserAction implements IBehaviorDispenseItem {

		@Override
		public ItemStack dispense(IBlockSource ibs, ItemStack is) {
			ChromatiCraft.logger.debug("Projectile tool used in dispenser, IBS="+ibs);
			Item i = is.getItem();
			if (i instanceof ProjectileFiringTool) {
				EntityPlayer ep = ReikaDispenserHelper.getDispenserPlayer(ibs, is);
				ForgeDirection facing = ForgeDirection.VALID_DIRECTIONS[BlockDispenser.func_149937_b(ibs.getBlockMetadata()).ordinal()];
				ep.setLocationAndAngles(ibs.getX(), ibs.getY()-1.62, ibs.getZ(), -ReikaDirectionHelper.getRelativeAngle(facing, ForgeDirection.SOUTH), 0);
				((ProjectileFiringTool)i).fire(is, ibs.getWorld(), ep);
			}
			else {
				ChromatiCraft.logger.logError("Invalid item "+is.getDisplayName()+" for projectile-tool dispenser action!");
			}
			return is;
		}

	}
}
