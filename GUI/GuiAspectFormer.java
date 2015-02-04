/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class GuiAspectFormer extends GuiChromaBase {

	private int index = 0;
	private ArrayList<Aspect> list = new ArrayList();

	private TileEntityAspectFormer tile;

	private int dx = 0;
	private int bright;
	private int clickDelay = 0;

	public GuiAspectFormer(EntityPlayer ep, TileEntityAspectFormer te) {
		super(new CoreContainer(ep, te), ep, te);
		list.addAll(ReikaThaumHelper.getAllDiscoveredAspects(ep));
		ReikaThaumHelper.sortAspectList(list);
		ySize = 74;
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int midx = j+xSize/2;
		int midy = k+42;
		int w = 8;
		int h = 48;
		int out = xSize/2-14;

		String tex = "Textures/GUIs/aspect.png";
		buttonList.add(new ImagedGuiButton(0, midx-w-out, midy-h/2, w, h, 184, 0, tex, ChromatiCraft.class));
		buttonList.add(new ImagedGuiButton(1, midx+out, midy-h/2, w, h, 176, 0, tex, ChromatiCraft.class));
	}

	private Aspect getActive() {
		return list.get(index);
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		switch(b.id) {
		case 0:
			if (dx == 0) {
				if (index > 0) {
					dx++;
					this.markButtons(false);
				}
				else {
					index = list.size()-1;
				}
			}
			break;
		case 1:
			if (dx == 0) {
				if (index < list.size()-1) {
					dx--;
					this.markButtons(false);
				}
				else {
					index = 0;
				}
			}
			break;
		}
		//this.initGui();
	}

	private void markButtons(boolean on) {
		this.initGui();
		for (int i = 0; i < buttonList.size(); i++) {
			GuiButton b2 = (GuiButton)buttonList.get(i);
			b2.visible = on;
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int minx = 120;
		int miny = 24;
		int w = 36;
		int maxx = minx+w;
		int maxy = miny+w;
		if (api.isMouseInBox(j+minx, j+maxx, k+miny, k+maxy)) {
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 1, 1);
			clickDelay = 90;
			ReikaPacketHelper.sendStringPacket(ChromatiCraft.packetChannel, ChromaPackets.ASPECT.ordinal(), this.getActive().getTag(), tile);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int minx = 120;
		int miny = 24;
		int w = 36;
		int maxx = minx+w;
		int maxy = miny+w;

		//api.drawRectFrame(minx, miny, w, w, ReikaColorAPI.mixColors(this.getActive().getColor(), 0x1e1e1e, clickDelay/90F));
		//Color.HSBtoRGB(hsb[0], Math.min(clickDelay/90F, hsb[1]), Math.min(bright/255F, hsb[2]))
		int color = ReikaColorAPI.mixColors(0x010101, 0xffffff, 1-bright/255F-clickDelay/90F);
		color = ReikaColorAPI.mixColors(color, this.getActive().getColor(), 1-clickDelay/90F);
		api.drawRectFrame(minx, miny, w, w, color);
		if (api.isMouseInBox(j+minx, j+maxx, k+miny, k+maxy)) {
			bright = Math.min(bright+12, 255);
		}
		else {
			bright = Math.max(bright-4, 30);
		}
		if (clickDelay > 0) {
			clickDelay = Math.max(clickDelay-4, 0);
		}

		ElementTagCompound tag = TileEntityAspectFormer.getAspectCost(this.getActive());
		int dx = 18;
		int dy = 21;
		for (CrystalElement e : tag.elementSet()) {
			String s = e.displayName+": "+tag.getValue(e)+" L/Vis";
			fontRendererObj.drawString(s, dx, dy, ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.75F));
			dy += fontRendererObj.FONT_HEIGHT;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int r, int v) {
		super.drawGuiContainerBackgroundLayer(f, r, v);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		Aspect c = this.getActive();
		int sep = 120;
		double px = 2D*Math.abs(dx)/width;
		int sp = 4000;//Math.max(1, (int)(6*Math.abs(-(px*px)+2*px)));
		//dx = -30;////////////////////////////////////////////////////////////////////////////////////////////////////////REMOVE
		if (dx > 0) {
			dx += sp;
		}
		if (dx < 0) {
			dx -= sp;
		}
		if (dx >= sep) {
			dx = 0;
			index--;
			this.markButtons(true);
		}
		if (dx <= -sep) {
			dx = 0;
			index++;
			this.markButtons(true);
		}
		int m = dx != 0 ? 1 : 0;
		int min = index > 0 ? -m : 0;
		int max = index < list.size()-1 ? m : 0;
		Tessellator v5 = Tessellator.instance;
		//for (int i = min; i <= max; i++) {
		int i = 0;
		int diff = i-index;
		int a = j+i*sep+dx;
		Aspect ca = list.get(index+i);
		ResourceLocation loc = ca.getImage();
		mc.renderEngine.bindTexture(loc);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(ca.getColor());
		int s = 32;
		int w = s;
		int tx = dx+j+xSize/2-s/2+50+i*sep;
		int ty = k+42-s/2;
		/*
			if (diff < 0) {
				int over = j-a;
				if (over > 0) {
					w -= over;
					//ReikaJavaLibrary.pConsole(ca.getName()+" : "+over);
				}
			}
			else if (diff > 0) {
				int over = tx+s-xSize-j+16;
				if (over > 0) {
					w -= over;
					//ReikaJavaLibrary.pConsole(ca.getName()+" : "+a+" > "+over);
				}
			}*/
		float tw = (float)w/s;
		v5.addVertexWithUV(tx, ty+s, 0, 0, 1);
		v5.addVertexWithUV(tx+w, ty+s, 0, tw, 1);
		v5.addVertexWithUV(tx+w, ty, 0, tw, 0);
		v5.addVertexWithUV(tx, ty, 0, 0, 0);
		v5.draw();
		//}
		//ReikaJavaLibrary.pConsole(ca+" > "+c+" > "+cb);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public String getGuiTexture() {
		return "aspect";
	}

}
