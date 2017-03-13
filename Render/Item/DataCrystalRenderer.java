package Reika.ChromatiCraft.Render.Item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Render.TESR.RenderDataNode;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Instantiable.Rendering.MultiSheetItemRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;


public class DataCrystalRenderer extends MultiSheetItemRenderer {

	private static final RayTracer trace = RayTracer.getVisualLOS();

	public DataCrystalRenderer() {
		super(ChromatiCraft.instance, ChromatiCraft.class);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper != helper.ENTITY_ROTATION && helper != helper.ENTITY_BOBBING;
	}

	private boolean checkRayTrace(EntityItem ei) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double r = 0.5;
		for (double i = -r; i <= r; i += r) {
			for (double k = -r; k <= r; k += r) {
				trace.setOrigins(ei.posX+i, ei.posY, ei.posZ+k, ep.posX, ep.posY, ep.posZ);
				if (trace.isClearLineOfSight(ei.worldObj))
					return true;
			}
		}
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.ENTITY) {
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glPushMatrix();

			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.ADDITIVEDARK.apply();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDepthMask(false);

			EntityItem ei = (EntityItem)data[1];
			ei.age = 0;

			double s = 3;
			GL11.glScaled(s, s, s);

			GL11.glTranslated(0, 0.125, 0);
			GL11.glPushMatrix();
			GL11.glRotated(27.5, 0, 0, 1);

			double a = 90;
			if (item.stackTagCompound != null && item.stackTagCompound.hasKey("owner") && !ei.onGround) {
				double n = 20D+System.identityHashCode(ei)%10D;
				a = ei.ticksExisted*90/n;
			}

			GL11.glRotated(a, 1, 0, 0);
			double h = 1.25;
			GL11.glTranslated(0, -h/2, 0);

			RenderDataNode.renderPrism(0, Tessellator.instance, 1, h, 0);
			GL11.glPopMatrix();

			if (this.checkRayTrace(ei)) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glTranslated(0, -0.75, 0);
				RenderDataNode.renderFlare(Tessellator.instance, 1);
			}

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		else {
			super.renderItem(type, item, data);
		}
	}

}
