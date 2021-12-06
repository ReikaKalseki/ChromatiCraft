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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MobSonarRenderer {

	public static final MobSonarRenderer instance = new MobSonarRenderer();

	private final TimerMap<EntityEntry> coords = new TimerMap();
	private final ArrayList<Ping> pings = new ArrayList();

	private static final int DURATION = 20;

	private MobSonarRenderer() {

	}

	@SideOnly(Side.CLIENT)
	public void addCoordinate(Entity e) {
		EntityEntry ee = new EntityEntry(e);
		coords.put(ee, DURATION);
		ReikaSoundHelper.playClientSound(ChromaSounds.DING, e, 1, ee.soundPitch);
		ReikaSoundHelper.playClientSound(ChromaSounds.DING, Minecraft.getMinecraft().thePlayer, 0.2F, ee.soundPitch);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tick(ClientTickEvent evt) {
		if (Minecraft.getMinecraft().theWorld == null)
			return;
		coords.tick();
		Iterator<Ping> it = pings.iterator();
		while (it.hasNext()) {
			Ping p = it.next();
			if (p.tick(Minecraft.getMinecraft().theWorld))
				it.remove();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(EntityRenderingLoopEvent evt) {
		if (!coords.isEmpty()) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			ReikaTextureHelper.bindTerrainTexture();
			int dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			RenderManager rm = RenderManager.instance;
			GL11.glTranslated(-rm.renderPosX, -rm.renderPosY, -rm.renderPosZ);
			GL11.glLineWidth(1.5F);
			Tessellator.instance.startDrawing(GL11.GL_LINES);
			for (EntityEntry loc : coords.keySet()) {
				if (loc.dimID == dim) {
					this.renderPoint(loc, coords.get(loc), ep, Tessellator.instance);
				}
			}
			Tessellator.instance.draw();
			GL11.glPopAttrib();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderPoint(EntityEntry loc, int ticks, EntityPlayer ep, Tessellator v5) {
		float f1 = (float)ticks/DURATION;
		float f = Math.min(1, f1+0.375F);
		double s = 1.5*Math.sqrt(f);
		int c = ReikaColorAPI.getColorWithBrightnessMultiplier(loc.baseColor, f1);
		v5.setColorOpaque_I(c);
		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.minZ);
		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.minZ);

		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.minZ);
		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.minZ);

		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.minZ);
		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.maxZ);

		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.maxZ);
		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.maxZ);

		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.maxZ);
		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.maxZ);

		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.maxZ);
		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.minZ);

		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.maxZ);
		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.maxZ);

		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.maxZ);
		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.minZ);

		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.minZ);
		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.minZ);

		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.minZ);
		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.maxZ);

		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.minZ);
		v5.addVertex(loc.box.maxX, loc.box.maxY, loc.box.minZ);

		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.minZ);
		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.maxZ);

		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.maxZ);
		v5.addVertex(loc.box.maxX, loc.box.minY, loc.box.maxZ);

		v5.addVertex(loc.box.minX, loc.box.minY, loc.box.maxZ);
		v5.addVertex(loc.box.minX, loc.box.maxY, loc.box.maxZ);
		/*
		GL11.glPushMatrix();
		RenderManager rm = RenderManager.instance;
		GL11.glTranslated(loc.location.xCoord-rm.renderPosX, loc.location.yCoord-rm.renderPosY, loc.location.zCoord-rm.renderPosZ);

		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		Tessellator v5 = Tessellator.instance;
		IIcon ico = ChromaIcons.FADE_CLOUD.getIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		v5.startDrawingQuads();
		v5.setBrightness(240);

		float f1 = (float)ticks/DURATION;
		float f = Math.min(1, f1+0.375F);
		double s = 1.5*Math.sqrt(f);
		int c1 = EntityAnimal.class.isAssignableFrom(loc.entityType) ? 0x22ff22 : 0xffffff;
		int c = ReikaColorAPI.getColorWithBrightnessMultiplier(c1, f1);
		v5.setColorOpaque_I(c);
		v5.addVertexWithUV(-s, s, 0, u, dv);
		v5.addVertexWithUV(+s, s, 0, du, dv);
		v5.addVertexWithUV(+s, -s, 0, du, v);
		v5.addVertexWithUV(-s, -s, 0, u, v);
		v5.draw();
		GL11.glPopMatrix();
		 */

	}

	public void addPing(EntityPlayer ep, int range) {
		pings.add(new Ping(ep, range, 0.125));
	}

	private static class EntityEntry {

		private final int entityID;
		private final int dimID;
		private final DecimalPosition location;
		private final Class entityType;
		private final AxisAlignedBB box;
		private final int baseColor;
		private final float soundPitch;

		private EntityEntry(Entity e) {
			entityID = e.getEntityId();
			dimID = e.worldObj.provider.dimensionId;
			location = new DecimalPosition(e);
			entityType = e.getClass();
			box = e.boundingBox != null ? e.boundingBox.copy() : null;
			int c = 0xffffff;
			MusicKey k = MusicKey.C5;
			if (e instanceof EntityTameable) {
				c = 0xba69ff;
				k = MusicKey.C6;
			}
			else if (e instanceof EntityAnimal) {
				c = 0x22ff22;
				k = MusicKey.G5;
			}
			else if (e instanceof EntityMob) {
				c = 0xff2222;
				k = MusicKey.C4;
			}
			else if (e instanceof EntityWaterMob) {
				c = 0x22aaff;
				k = MusicKey.G4;
			}
			baseColor = c;
			soundPitch = (float)MusicKey.C5.getRatio(k);
		}

		@Override
		public int hashCode() {
			return entityID;//location.hashCode() ^ entityType.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof EntityEntry && ((EntityEntry)o).entityID == entityID;//.location.equals(location) && ((EntityEntry)o).entityType == entityType;
		}

	}

	private static class Ping {

		private final DecimalPosition center;
		private final double maxRadius;
		private final double radialSpeed;

		private double radius;
		private final HashSet<Integer> usedIDs = new HashSet();

		private Ping(Entity e, double r, double vr) {
			center = new DecimalPosition(e);
			maxRadius = r;
			radialSpeed = vr;
		}

		private boolean tick(World world) {
			radius += radialSpeed;

			List<Entity> li = this.getEntities(world);
			for (Entity e : li) {
				instance.addCoordinate(e);
			}

			return radius >= maxRadius;
		}

		@SideOnly(Side.CLIENT)
		private List<Entity> getEntities(World world) {
			ArrayList<Entity> li = new ArrayList();
			AxisAlignedBB box = center.getAABB(radius);
			List<Entity> in = world.getEntitiesWithinAABB(EntityLiving.class, box);
			for (Entity e : in) {
				if (e == Minecraft.getMinecraft().thePlayer)
					continue;
				double d = e.getDistanceSq(center.xCoord, center.yCoord, center.zCoord);
				double d2 = radius*radius;
				//if (true || ReikaMathLibrary.approxrAbs(d2, d, 4)) {
				if (!usedIDs.contains(e.getEntityId())) {
					//ReikaJavaLibrary.pConsole(d);
					li.add(e);
					usedIDs.add(e.getEntityId());
				}
			}
			return li;
		}

	}

}
