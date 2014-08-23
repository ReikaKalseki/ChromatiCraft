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

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable.RecipeType;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiCastingTable extends GuiChromaBase {

	private final TileEntityCastingTable tile;

	public GuiCastingTable(EntityPlayer ep, TileEntityCastingTable te) {
		super(new ContainerCastingTable(ep, te), te);

		tile = te;
		ySize = this.isMultiForm() ? 240 : 209;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int a = (width - xSize) / 2;
		int b = (height - ySize) / 2;
		HashMap<List<Integer>, TileEntityItemStand> li = tile.getOtherStands();
		for (List<Integer> key : li.keySet()) {
			TileEntityItemStand te = li.get(key);
			ItemStack is = te.getStackInSlot(0);
			if (is != null) {
				int i = key.get(0);
				int k = key.get(1);
				int sx = i == 0 ? 0 : i < 0 ? -1 : 1;
				int sy = k == 0 ? 0 : k < 0 ? -1 : 1;
				int tx = Math.abs(i) == 2 ? 38 : 64;
				int ty = Math.abs(k) == 2 ? 38 : 63;
				int dx = 80+sx*(tx);
				int dy = 75+sy*(ty);
				api.drawItemStack(itemRender, is, dx, dy);
				int x1 = a+dx-1;
				int x2 = a+dx+17;
				int y1 = b+dy-1;
				int y2 = b+dy+17;
				if (api.isMouseInBox(x1, x2, y1, y2)) {
					api.drawTooltip(fontRendererObj, is.getDisplayName());
				}
			}
		}
	}

	@Override
	protected int getTitlePosition() {
		return this.isMultiForm() ? 3 : super.getTitlePosition();
	}

	@Override
	protected boolean labelInventory() {
		return !this.isMultiForm();
	}

	private boolean isMultiForm() {
		return tile.getTier().isAtLeast(RecipeType.MULTIBLOCK);
	}

	@Override
	public String getGuiTexture() {
		return this.isMultiForm() ? "table4" : "table2";
	}

}
