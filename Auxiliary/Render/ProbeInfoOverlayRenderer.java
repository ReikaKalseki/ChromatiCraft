/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Render;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IIcon;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ProbeInfoOverlayRenderer {

	public static final ProbeInfoOverlayRenderer instance = new ProbeInfoOverlayRenderer();

	private static final int CONNECTIVITY_DURATION = 480;

	private final EnumMap<CrystalElement, PylonConnection> connectivityFlags = new EnumMap(CrystalElement.class);

	private ProbeInfoOverlayRenderer() {

	}

	@SideOnly(Side.CLIENT)
	void renderConnectivityOverlays(EntityPlayer ep, int gsc) {
		if (!connectivityFlags.isEmpty()) {
			Iterator<Entry<CrystalElement, PylonConnection>> it = connectivityFlags.entrySet().iterator();
			boolean nonEmpty = false;

			Tessellator v5 = Tessellator.instance;
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			BlendMode.DEFAULT.apply();
			ReikaTextureHelper.bindTerrainTexture();
			v5.startDrawingQuads();
			v5.setColorOpaque_I(0xffffff);

			while (it.hasNext()) {
				Entry<CrystalElement, PylonConnection> e = it.next();
				PylonConnection tick = e.getValue();
				if (tick.age == CONNECTIVITY_DURATION-5)
					ReikaSoundHelper.playClientSound(ChromaSounds.BOUNCE, ep, 0.25F, 2F);
				if (tick.shouldRender()) {
					this.renderConnectivityStatus(v5, ep, gsc, tick);
				}
				if (tick.age > 0)
					tick.age--;

				if (tick.age <= 0) {
					it.remove();
				}
				else {
					e.setValue(tick);
					nonEmpty = true;
				}
			}

			v5.draw();

			if (!nonEmpty) {
				connectivityFlags.clear();
			}

			GL11.glPopAttrib();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderConnectivityStatus(Tessellator v5, EntityPlayer ep, int gsc, PylonConnection pc) {
		int ar = 12;
		IIcon ico = pc.valid ? (pc.connected ? ChromaIcons.CHECK.getIcon() : ChromaIcons.X.getIcon()) : ChromaIcons.NOENTER.getIcon();
		IIcon ico2 = pc.color.getOutlineRune();

		v5.setColorRGBA_I(0xffffff, pc.getAlpha());

		int dx = pc.color.ordinal() >= 8 ? 20 : -40;
		int dy = (pc.color.ordinal()%8)*15-65;

		int ox = dx+Minecraft.getMinecraft().displayWidth/(gsc*2)+ar-8;
		int oy = dy+Minecraft.getMinecraft().displayHeight/(gsc*2)+ar-8;

		double u = ico.getMinU();
		double v = ico.getMinV();
		double du = ico.getMaxU();
		double dv = ico.getMaxV();

		double u2 = ico2.getMinU();
		double v2 = ico2.getMinV();
		double du2 = ico2.getMaxU();
		double dv2 = ico2.getMaxV();

		double r = 12;
		int dw = 7;
		v5.addVertexWithUV(ox+0+dw, oy+r, 0, u, dv);
		v5.addVertexWithUV(ox+r+dw, oy+r, 0, du, dv);
		v5.addVertexWithUV(ox+r+dw, oy+0, 0, du, v);
		v5.addVertexWithUV(ox+0+dw, oy+0, 0, u, v);

		v5.addVertexWithUV(ox+0-dw, oy+r, 0, u2, dv2);
		v5.addVertexWithUV(ox+r-dw, oy+r, 0, du2, dv2);
		v5.addVertexWithUV(ox+r-dw, oy+0, 0, du2, v2);
		v5.addVertexWithUV(ox+0-dw, oy+0, 0, u2, v2);
	}

	public void markConnectivity(EntityPlayer ep, CrystalElement e, boolean connected, boolean valid) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			connectivityFlags.put(e, new PylonConnection(e, connected, valid));
		}
		else {
			ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.CONNECTIVITY.ordinal(), (EntityPlayerMP)ep, e.ordinal(), connected ? 1 : 0, valid ? 1 : 0);
		}
	}

	private static class PylonConnection {

		public final CrystalElement color;
		public final boolean connected;
		public final boolean valid;

		private int age;

		private PylonConnection(CrystalElement e, boolean conn, boolean v) {
			color = e;
			age = CONNECTIVITY_DURATION+(e.ordinal()%8)*6;
			connected = conn;
			valid = v;
		}

		public boolean shouldRender() {
			return age >= -CONNECTIVITY_DURATION && age <= CONNECTIVITY_DURATION;
		}

		public int getAlpha() {
			int ret = 255;
			int t = Math.abs(age);
			if (t < 20) {
				ret = Math.min(255, 13*t);
			}
			else if (t > CONNECTIVITY_DURATION-20) {
				t = t-(CONNECTIVITY_DURATION-20);
				ret = Math.min(255, 255-12*t);
			}
			return ret;
		}

	}

}
