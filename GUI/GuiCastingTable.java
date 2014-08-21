/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiCastingTable extends GuiChromaBase {

	private final TileEntityCastingTable tile;

	public GuiCastingTable(EntityPlayer ep, TileEntityCastingTable te) {
		super(new ContainerCastingTable(ep, te), te);

		tile = te;
		ySize = 209;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		ArrayList<TileEntityItemStand> li = tile.getOtherStands(tile);
		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				int dx = tile.xCoord+i;
				int dz = tile.zCoord+k;
				int dy = tile.yCoord+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
				ChromaTiles c = ChromaTiles.getTile(tile.worldObj, dx, dy, dz);
				if (c == ChromaTiles.STAND) {
					TileEntityItemStand te = (TileEntityItemStand)tile.worldObj.getTileEntity(dx, dy, dz);
					ItemStack is = te.getStackInSlot(0);
					ReikaGuiAPI.instance.drawItemStackWithTooltip(itemRender, is, 80+19*i/2, 55+19*k/2);
				}
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "table";
	}

}
