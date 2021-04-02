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

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.GuiChromaTool;
import Reika.ChromatiCraft.Items.Tools.ItemEnderBucket;
import Reika.ChromatiCraft.Items.Tools.ItemEnderBucket.BucketMode;
import Reika.ChromatiCraft.Items.Tools.ItemEnderBucket.TankLink;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Instantiable.GUI.SubviewableList;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;

public class GuiEnderBucket extends GuiChromaTool {

	private static final int PER_PAGE = 5-1;

	private final int xSize = 195;
	private final int ySize = 168;

	private int offset;
	private final SubviewableList<TankLink> links;

	public GuiEnderBucket(EntityPlayer ep) {
		super(ep);
		links = new SubviewableList(this.getItem().getLinks(player.getCurrentEquippedItem(), player), PER_PAGE);
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/enderbucket2.png";

		int h = 24;
		for (int i = 0; i < links.clampedSize(); i++) {
			buttonList.add(new CustomSoundImagedGuiButton(i, j+8, k+27+h+i*h, 180, 24, 0, 168, tex, ChromatiCraft.class, this));
		}
		buttonList.add(new CustomSoundImagedGuiButton(1000, j+8, k+16+h, 180, 8, 0, 192, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1001, j+8, k+150, 180, 8, 0, 200, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundGuiButton(1002, j+13, k+18, 80, 20, this.getCurrentMode().displayName+" Mode", this));
		buttonList.add(new CustomSoundGuiButton(1003, j+103, k+18, 80, 20, "Remove", this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		//this.getItem().setMode(player.getCurrentEquippedItem(), BucketMode.list[b.id]);
		if (b.id < 1000) {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENDERBUCKETLINK.ordinal(), PacketTarget.server, links.getAbsoluteIndex(b.id));
			//player.closeScreen();
		}
		int id = b.id-1000;
		switch(id) {
			case 0:
				links.stepOffset(-1);
				break;
			case 1:
				links.stepOffset(1);
				break;
			case 2:
				this.getItem().stepMode(player.getCurrentEquippedItem());
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENDERBUCKETMODE.ordinal(), PacketTarget.server);
				break;
			case 3:
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ENDERBUCKETREMOVE.ordinal(), PacketTarget.server, this.getItem().getActiveLinkIndex(player.getCurrentEquippedItem(), player));
				player.closeScreen();
				break;
		}
		this.initGui();
	}

	@Override
	public void drawScreen(int x, int y, float ptick) {
		super.drawScreen(x, y, ptick);
		int h = 24;
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int tx = j+xSize/2;
		int ty = k+6;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(ChromaFontRenderer.FontType.GUI.renderer, "Ender Bucket", tx, ty, 0xffffff);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_LIGHTING);
		List<TankLink> li = links.getVisibleSublist();
		if (!li.isEmpty()) {
			int active = this.getItem().getActiveLinkIndex(player.getCurrentEquippedItem(), player);
			for (int i = 0; i < li.size(); i++) {
				boolean sel = i == active;
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glDisable(GL11.GL_LIGHTING);
				TankLink tl = li.get(i);
				int dy = k+35+h+i*h;
				fontRendererObj.drawString(tl.getDisplayName(), j+35, dy, sel ? 0xa0ffa0 : 0xffffff);
				ItemStack is = tl.getIcon();
				if (is != null) {
					ReikaGuiAPI.instance.drawItemStack(itemRender, fontRendererObj, is, j+12, dy-4);
				}
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				Fluid f = tl.getCurrentFluidToDrain(false, true);
				if (f != null) {
					IIcon ico = ReikaLiquidRenderer.getFluidIconSafe(f);
					GL11.glColor4f(1, 1, 1, 1);
					ReikaLiquidRenderer.bindFluidTexture(f);
					this.drawTexturedModelRectFromIcon(j+168, dy-4, ico, 16, 16);
				}
				if (ReikaGuiAPI.instance.isMouseInBox(j+12, j+186, dy-6, dy+14)) {
					ReikaGuiAPI.instance.drawTooltip(fontRendererObj, (f != null ? f.getLocalizedName() : "Fill-Only")+" @ "+tl.tank.toString());
				}
			}
		}
		GL11.glPopAttrib();
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, -400);
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/GUIs/enderbucket2.png");
		ReikaGuiAPI.instance.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		GL11.glPopMatrix();
	}

	private BucketMode getCurrentMode() {
		return this.getItem().getMode(player.getCurrentEquippedItem());
	}

	private ItemEnderBucket getItem() {
		return (ItemEnderBucket)player.getCurrentEquippedItem().getItem();
	}

}
