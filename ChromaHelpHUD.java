/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.ChromaHelpData;
import Reika.ChromatiCraft.Auxiliary.ChromaHelpData.HelpKey;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
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
			MovingObjectPosition look = ReikaPlayerAPI.getLookedAtBlockClient(5, false);

			if (look != null) {
				if (this.isDifferent(look)) {
					this.closePanel();
				}
				else {
					this.openPanel();
					HelpKey key = ChromaHelpData.ChromaHelpKeys.instance.getKey(Minecraft.getMinecraft().theWorld, look);
					if (key != null) {
						this.renderPanel();
						if (this.isPanelOpen()) {
							this.renderText(key);
						}
					}
				}
			}
			last_look = look;
		}

		ReikaTextureHelper.bindHUDTexture();
	}

	private void renderPanel() {
		Tessellator v5 = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorRGBA_I(0xffffff, 255);
		int dx = Minecraft.getMinecraft().displayWidth/2-rollx-3;
		int dy = Minecraft.getMinecraft().displayHeight/4-ySize/2;
		v5.addVertex(dx, dy, 0);
		v5.addVertex(dx+rollx, dy, 0);
		v5.addVertex(dx+rollx, dy+rolly, 0);
		v5.addVertex(dx, dy+rolly, 0);
		v5.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void renderText(HelpKey key) {

	}

	private boolean isPanelOpen() {
		return rollx == xSize && rolly == ySize;
	}

	private void openPanel() {
		if (rollx < xSize)
			rollx++;
		else if (rolly < ySize)
			rolly++;
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
