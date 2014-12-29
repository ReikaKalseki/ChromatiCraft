/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaHelpHUD {

	public static ChromaHelpHUD instance = new ChromaHelpHUD();

	private int rollx;
	private int rolly;

	private MovingObjectPosition last_look;

	private static final int xSize = 64;
	private static final int ySize = 128;

	private ChromaHelpHUD() {

	}

	@SubscribeEvent
	public void renderHUD(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			int gsc = evt.resolution.getScaleFactor();
			MovingObjectPosition look = ReikaPlayerAPI.getLookedAtBlock(ep, 5, false);
			if (look != null) {
				if (this.isDifferent(look)) {
					this.closePanel();
				}
				else {
					int x = look.blockX;
					int y = look.blockY;
					int z = look.blockZ;
					Block b = Minecraft.getMinecraft().theWorld.getBlock(x, y, z);
					int meta = Minecraft.getMinecraft().theWorld.getBlockMetadata(x, y, z);
					boolean flag = true;
					if (b instanceof BlockTieredResource) {
						flag = ((BlockTieredResource)b).isPlayerSufficientTier(Minecraft.getMinecraft().theWorld, x, y, z, ep);
					}
					String text = ChromaHelpData.instance.getText(b, meta);
					if (text != null && !text.isEmpty()) {
						this.openPanel();
						this.renderPanel(gsc);
						if (this.isPanelOpen()) {
							this.renderText(text, gsc, flag);
							if (flag)
								ChromaHelpData.instance.markDiscovered(ep, b, meta);
						}
					}
				}
			}
			last_look = look;

			ReikaTextureHelper.bindHUDTexture();
		}
	}

	private void renderHelpGui(int gsc) {

	}

	private void renderPanel(int gsc) {
		Tessellator v5 = Tessellator.instance;

		boolean big = gsc > 2;
		double sc = big ? 1.375 : 0.75;

		int n1 = gsc;
		int n2 = 2*gsc;

		int w = (int)(sc*rollx*2/gsc);
		int h = (int)(sc*rolly*2/gsc);
		int hm = (int)(sc*ySize*2/gsc);

		int dx = Minecraft.getMinecraft().displayWidth/n1-w-3;
		int dy = Minecraft.getMinecraft().displayHeight/n2-hm/2;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorRGBA_I(0xffffff, 255);
		v5.addVertex(dx, dy, 0);
		v5.addVertex(dx+w, dy, 0);
		v5.addVertex(dx+w, dy+h, 0);
		v5.addVertex(dx, dy+h, 0);
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/helphud4.png");
		v5.startDrawingQuads();
		double u = 0.25*rollx/xSize;
		double v = 0.5*rolly/ySize;
		v5.addVertexWithUV(dx, dy+h, 0, 0, v);
		v5.addVertexWithUV(dx+w, dy+h, 0, u, v);
		v5.addVertexWithUV(dx+w, dy, 0, u, 0);
		v5.addVertexWithUV(dx, dy, 0, 0, 0);
		v5.draw();
		//GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderText(String s, int gsc, boolean know) {
		if (!know) {
			s = EnumChatFormatting.OBFUSCATED.toString()+s;
		}
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		GL11.glPushMatrix();
		boolean big = gsc > 2;
		double ft = big ? 2 : 1;
		double d = ft/gsc;//Math.max(0.5, 1D/gsc);
		double fac = big ? 1.375 : 0.75;

		int w = (int)(fac*rollx*2/gsc);
		int wm = (int)(fac*xSize*2/gsc);
		int hm = (int)(fac*ySize*2/gsc);

		int n1 = gsc;
		int n2 = 2*gsc;

		GL11.glScaled(d, d, d);
		int dx = (int)((Minecraft.getMinecraft().displayWidth/n1-w-1)/d);
		int dy = (int)((Minecraft.getMinecraft().displayHeight/n2-hm/2+2)/d);
		int tw = (int)((gsc*wm-10)/ft);
		f.drawSplitString(s, dx, dy, tw, 0xffffff);
		GL11.glPopMatrix();
	}

	private boolean isPanelOpen() {
		return rollx == xSize && rolly == ySize;
	}

	private void openPanel() {
		int step = Math.max(1, 90/Math.max(1, ReikaRenderHelper.getFPS()));
		if (rollx < xSize)
			rollx += step;
		else if (rolly < ySize)
			rolly += step;
		if (rollx > xSize)
			rollx = xSize;
		if (rolly > ySize)
			rolly = ySize;
	}

	private void closePanel() {
		rollx = rolly = 0;
	}

	private boolean isDifferent(MovingObjectPosition look) {
		if (look == last_look)
			return false;
		if (look == null || last_look == null)
			return true;
		return look.blockX != last_look.blockX || look.blockY != last_look.blockY || look.blockZ != last_look.blockZ;
	}

}
