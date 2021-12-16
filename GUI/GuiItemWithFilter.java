/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemWithItemFilter;
import Reika.ChromatiCraft.Base.ItemWithItemFilter.Filter;
import Reika.ChromatiCraft.Container.ContainerItemWithFilter;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemWithFilter extends GuiContainer
{
	private final EntityPlayer player;

	public GuiItemWithFilter(EntityPlayer p5ep, World par2World)
	{
		super(new ContainerItemWithFilter(p5ep, par2World));
		player = p5ep;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Item Filter", xSize/2, 6, 0xffffff);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);

		ItemStack held = player.getCurrentEquippedItem();
		if (ChromaItems.LINK.matchWith(held)) {
			ItemWithItemFilter iap = (ItemWithItemFilter)held.getItem();
			ArrayList<Filter> li = iap.getItemList(held);
			for (int i = 0; i < 27; i++) {
				int a = 96+(int)(48*Math.sin(System.currentTimeMillis()/400D+System.identityHashCode(inventorySlots.getSlot(i))));
				int c = (a << 24) | (i >= li.size() ? 0xff0000 : (li.get(i).hasNBT() ? 0x00ff00 : 0xffff00));
				int x = 8+(i%9)*18;
				int y = 17+(i/9)*18;
				this.drawRect(x, y, x+16, y+16, c);
			}
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/basicstorage_small.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}
}
