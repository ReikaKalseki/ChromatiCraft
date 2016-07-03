/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate.GateData;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate.Statuses;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.ObjectWeb;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

public class GuiTeleportGate extends GuiChromaBase {

	private final TileEntityTeleportGate gate;

	private final ConnectionWeb web = new ConnectionWeb();
	private final RegionMap<LinkNode> pointLocs = new RegionMap();

	private static final int SIZE = 16;

	public GuiTeleportGate(EntityPlayer ep, TileEntityTeleportGate te) {
		super(new CoreContainer(ep, te), ep, te);
		gate = te;
		xSize = 240;
		ySize = 190;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		web.clear();
		for (GateData dat : TileEntityTeleportGate.getCache()) {
			LinkNode l = new LinkNode(dat);
			l.statusFlags |= dat.statusFlags;
			web.addNode(l);
		}
		web.scaleTo(xSize, ySize, SIZE);
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);
		this.initGui();
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		LinkNode l = pointLocs.getRegion(-j+x, -k+y);
		if (l != null) {
			if (!l.location.equals(gate.worldObj, gate.xCoord, gate.yCoord, gate.zCoord)) {
				if (TileEntityTeleportGate.canTeleport(new WorldLocation(gate), l.location, player)) {
					ReikaSoundHelper.playClientSound(ChromaSounds.USE, player, 1, 1);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.TRIGGERTELEPORT.ordinal(), new PacketTarget.ServerTarget(), gate.worldObj.provider.dimensionId, gate.xCoord, gate.yCoord, gate.zCoord, l.location.dimensionID, l.location.xCoord, l.location.yCoord, l.location.zCoord);
					player.closeScreen();
					player.rotationPitch = 0;
					return;
				}
			}
			ReikaSoundHelper.playClientSound(ChromaSounds.ERROR, player, 1, 1);
		}
		else if (x >= j && y >= k && x <= j+fontRendererObj.getStringWidth("Reload Preview")+3 && y <= k+fontRendererObj.FONT_HEIGHT+3) {
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			player.closeScreen();
			gate.takeSnapshot();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		//fontRendererObj = Minecraft.getMinecraft().fontRenderer;
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		pointLocs.clear();
		web.render(j, k, SIZE);

		String s = "Reload Preview";
		int c = ReikaColorAPI.mixColors(0xffffffff, 0x80808080, Math.min(1, 0.625F+0.5F*(float)Math.sin(System.currentTimeMillis()/400D)));
		api.drawRectFrame(0, 0, fontRendererObj.getStringWidth(s)+3, fontRendererObj.FONT_HEIGHT+3, c);
		fontRendererObj.drawString(s, 2, 2, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p, int a, int b) {
		//super.drawGuiContainerBackgroundLayer(p, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

	}

	@Override
	protected boolean drawTitle() {
		return false;
	}

	@Override
	public String getGuiTexture() {
		return "teleport";
	}

	private class ConnectionWeb {

		private final ObjectWeb<LinkNode> web = new ObjectWeb();

		private int minX = Integer.MAX_VALUE;
		private int minZ = Integer.MAX_VALUE;
		private int maxX = Integer.MIN_VALUE;
		private int maxZ = Integer.MIN_VALUE;

		private void addNode(LinkNode l) {
			web.addNode(l);
			if (Statuses.OWNED.check(l.statusFlags) && Statuses.STRUCTURE.check(l.statusFlags)) {
				for (LinkNode l2 : web.objects()) {
					if (Statuses.POWERED.check(l.statusFlags) || Statuses.POWERED.check(l2.statusFlags)) {
						if (Statuses.OWNED.check(l2.statusFlags) && Statuses.STRUCTURE.check(l2.statusFlags)) {
							web.addBilateralConnection(l, l2);
						}
					}
				}
			}
			l.renderX = l.location.xCoord;
			l.renderZ = l.location.zCoord;
			minX = Math.min(minX, l.location.xCoord);
			minZ = Math.min(minZ, l.location.zCoord);
			maxX = Math.max(maxX, l.location.xCoord);
			maxZ = Math.max(maxZ, l.location.zCoord);
		}

		private void clear() {
			web.clear();
			minX = Integer.MAX_VALUE;
			minZ = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;
			maxZ = Integer.MIN_VALUE;
		}

		@Override
		public String toString() {
			return web.toString();
		}

		private void scaleTo(int sizeX, int sizeY, int elementSize) {
			sizeX -= elementSize;
			sizeY -= elementSize;
			for (LinkNode l : web.objects()) {
				l.renderX -= minX;
				l.renderZ -= minZ;
			}
			maxX -= minX;
			maxZ -= minZ;
			double sx = sizeX/(double)maxX;
			double sz = sizeY/(double)maxZ;
			for (LinkNode l : web.objects()) {
				l.renderX *= sx;
				l.renderZ *= sz;
			}
		}

		private void render(int j, int k, int s) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			for (LinkNode l : web.objects()) {
				boolean mbox = api.isMouseInBox(j+l.renderX+3, j+l.renderX+s-4, k+l.renderZ+3, k+l.renderZ+s-4);
				for (LinkNode l2 : web.getChildren(l)) {
					int lc = mbox ? 0xffffffff : 0x04040404;
					api.drawLine(l.renderX+s/2, l.renderZ+s/2, l2.renderX+s/2, l2.renderZ+s/2, lc);
				}
			}

			BlendMode.DEFAULT.apply();

			ReikaTextureHelper.bindTerrainTexture();
			for (LinkNode l : web.objects()) {
				int c = l.getRenderColor();
				boolean mbox = api.isMouseInBox(j+l.renderX+3, j+l.renderX+s-4, k+l.renderZ+3, k+l.renderZ+s-4);
				GL11.glColor3f(ReikaColorAPI.getRed(c)/255F, ReikaColorAPI.getGreen(c)/255F, ReikaColorAPI.getBlue(c)/255F);
				api.drawTexturedModelRectFromIcon(l.renderX, l.renderZ, ChromaIcons.DIAMOND.getIcon(), s, s);
				pointLocs.addRegionByWH(l.renderX, l.renderZ, s, s, l);
				if (mbox) {
					BufferedImage img = gate.getPreview(l.location);
					if (img == null) {
						ReikaTextureHelper.bindFinalTexture(ChromatiCraft.class, "Textures/GateNotFound.png");
					}
					else {
						ReikaTextureHelper.bindRawTexture(img, "telegate "+l.location.toString());
					}
					double w = img == null ? 854/8D : img.getWidth()/8D;
					double h = img == null ? 480/8D : img.getHeight()/8D;
					GL11.glColor3f(1, 1, 1);
					double x = api.getMouseRealX()-w/1.25;
					double y = api.getMouseRealY()-h/4D;
					x = Math.min(x, xSize-w);
					y = Math.min(y, ySize-h);
					Tessellator.instance.startDrawingQuads();
					Tessellator.instance.setColorOpaque_I(0xffffff);
					Tessellator.instance.addVertexWithUV(x, y+h, 0, 0, 1);
					Tessellator.instance.addVertexWithUV(x+w, y+h, 0, 1, 1);
					Tessellator.instance.addVertexWithUV(x+w, y, 0, 1, 0);
					Tessellator.instance.addVertexWithUV(x, y, 0, 0, 0);
					Tessellator.instance.draw();
					ReikaTextureHelper.bindTerrainTexture();
					String sg = l.location.toString();
					api.drawTooltipAt(fontRendererObj, sg, (int)x+fontRendererObj.getStringWidth(sg)+19, (int)y-(fontRendererObj.FONT_HEIGHT-8));
				}
			}
			GL11.glPopAttrib();
		}

	}

	private class LinkNode implements Comparable<LinkNode> {

		private int statusFlags;
		private final WorldLocation location;

		private int renderX;
		private int renderZ;

		private LinkNode(GateData dat) {
			location = dat.location;
			if (location.dimensionID != Minecraft.getMinecraft().theWorld.provider.dimensionId)
				this.setFlag(Statuses.DIMENSION);
			if (dat.isOwnedBy(player))
				this.setFlag(Statuses.OWNED);
		}

		private LinkNode setFlag(Statuses s) {
			statusFlags |= s.flag;
			return this;
		}

		public int getRenderColor() {
			for (int i = 0; i < Statuses.list.length; i++) {
				Statuses s = Statuses.list[i];
				if (!s.check(statusFlags))
					return s.color;
			}
			return 0xffffff;
		}

		@Override
		public String toString() {
			return location.toString()+" ["+renderX+"/"+renderZ+"/"+Integer.toBinaryString(statusFlags)+"]";
		}

		@Override
		public int compareTo(LinkNode o) {
			return Integer.compare(location.hashCode(), o.location.hashCode());
		}

	}

}
