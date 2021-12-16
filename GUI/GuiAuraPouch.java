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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Container.ContainerAuraPouch;
import Reika.ChromatiCraft.Items.Tools.ItemAuraPouch;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiAuraPouch extends GuiContainer {

	private final EntityPlayer player;

	public GuiAuraPouch(EntityPlayer ep) {
		super(new ContainerAuraPouch(ep));
		player = ep;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xffffff);
		if (ChromaItems.AURAPOUCH.matchWith(player.getCurrentEquippedItem())) {
			ItemAuraPouch iap = (ItemAuraPouch)player.getCurrentEquippedItem().getItem();
			boolean[] active = iap.getActiveSlots(player.getCurrentEquippedItem());
			for (int i = 0; i < iap.SIZE; i++) {
				int a = 96+(int)(48*Math.sin(System.currentTimeMillis()/400D+System.identityHashCode(inventorySlots.getSlot(i))));
				int c = (a << 24) | (active[i] ? 0x00ff00 : 0xff0000);
				int x = 8+(i%9)*18;
				int y = 17+(i/9)*18;
				this.drawRect(x, y, x+16, y+16, c);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int mx, int my) {
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ALPHA.apply();
		String var4 = "/Reika/ChromatiCraft/Textures/GUIs/basicstorage_small.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, var4);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
		BlendMode.DEFAULT.apply();
		this.drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
