/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Block.BlockRouterNode.RouterFilter;
import Reika.ChromatiCraft.Block.BlockRouterNode.TileEntityRouterNode;
import Reika.ChromatiCraft.Container.ContainerRouterFilter;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub.ItemRule;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class GuiRouterFilter extends GuiContainer {

	private final RouterFilter tile;
	private boolean blacklist;

	public GuiRouterFilter(EntityPlayer player, RouterFilter te) {
		super(new ContainerRouterFilter(player, te));
		tile = te;

		ySize = 138;
		xSize = 176;

		blacklist = te instanceof TileEntityRouterNode ? ((TileEntityRouterNode)te).isBlacklist : false;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (tile instanceof TileEntityRouterNode) {
			int v = blacklist ? 56 : 66;
			buttonList.add(new ImagedGuiButton(0, j+10, k+10, 10, 10, 90, v, "/Reika/ChromatiCraft/Textures/GUIs/buttons.png", ChromatiCraft.class));
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			blacklist = !blacklist;
			ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.ROUTERFILTERFLAG.ordinal(), (TileEntity)tile, blacklist ? 1 : 0);
			((TileEntityRouterNode)tile).isBlacklist = blacklist;
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);
		fontRendererObj = ChromaFontRenderer.FontType.GUI.renderer;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		ReikaTextureHelper.bindFontTexture();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, StatCollector.translateToLocal("chroma.routerfilter"), xSize/2, 5, 0xffffff);

		for (int i = 0; i < 9; i++) {
			ItemRule ir = tile.getFilter(i);
			if (ir != null) {
				int x = 8+i*18;
				int y = 33;
				ReikaGuiAPI.instance.drawItemStack(itemRender, fontRendererObj, ReikaItemHelper.getSizedItemStack(ir.getItem(), 1), x, y);
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				ReikaGuiAPI.instance.drawRectFrame(x-1, y-1, 18, 18, 0xff000000 | ir.mode.color);
				GL11.glPopAttrib();
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String i = this.getFullTexturePath();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
	}

	public final String getFullTexturePath() {
		return "/Reika/ChromatiCraft/Textures/GUIs/routerfilter.png";
	}

}
