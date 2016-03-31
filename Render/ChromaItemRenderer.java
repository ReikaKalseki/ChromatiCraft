/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;


public class ChromaItemRenderer implements IItemRenderer {

	public ChromaItemRenderer() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float a = 0; float b = 0;

		RenderBlocks rb = (RenderBlocks)data[0];
		if (type == type.ENTITY) {
			a = -0.5F;
			b = -0.5F;
			GL11.glScalef(0.5F, 0.5F, 0.5F);
		}
		if (item.getItemDamage() >= ChromaTiles.TEList.length)
			return;
		ChromaTiles machine = item.getItem() == ChromaItems.RIFT.getItemInstance() ? ChromaTiles.RIFT : ChromaTiles.TEList[item.getItemDamage()];
		boolean entity = type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
		if (machine.hasRender() && !machine.hasBlockRender()) {
			TileEntity te = machine.createTEInstanceForRender();
			if (machine.hasNBTVariants()) {
				((NBTTile)te).setDataFromItemStackTag(item);
			}
			if (machine == ChromaTiles.LUMENWIRE && type == type.ENTITY) {
				a = b = -1.25F;
				double s = 1.25;
				GL11.glScaled(s, s, s);
				GL11.glTranslated(0, -0.75, 0);
			}
			TileEntityRendererDispatcher.instance.renderTileEntityAt(te, a, -0.1D, b, entity ? -1 : 0);
		}
		else {
			ReikaTextureHelper.bindTerrainTexture();
			if (entity && machine == ChromaTiles.CRYSTAL) {
				GL11.glTranslated(-0.5, -0.5, -0.5);
				CrystalRenderer.renderAllArmsInInventory = true;
			}
			rb.renderBlockAsItem(machine.getBlock(), machine.getBlockMetadata(), 1);
			CrystalRenderer.renderAllArmsInInventory = false;
		}
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (mc.currentScreen == null || mc.currentScreen instanceof GuiContainerCreative) && ProgressStage.USEENERGY.isPlayerAtStage(ep) && machine.isLumenTile() && (item.stackTagCompound == null || !item.stackTagCompound.getBoolean("tooltip"))) {
			int idx = -1;
			if (machine.isPylonPowered()) {
				idx = 1;
			}
			else if (machine.isRelayPowered()) {
				idx = 2;
			}
			else if (machine.isChargedCrystalPowered()) {
				idx = 3;
			}
			else if (machine.isWirelessPowered()) {
				idx = 4;
			}
			if (idx >= 0) {
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				if (machine.hasBlockRender()) {
					GL11.glRotated(-45, 0, 1, 0);
					GL11.glRotated(60, 1, 0, 0);
				}
				else {
					GL11.glRotated(45, 0, 1, 0);
					GL11.glRotated(60, 1, 0, 0);
				}
				GL11.glScaled(1.6, 1.6, 1.6);
				double sc = 0.5;
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
					sc = 1;
				GL11.glScaled(sc, sc, sc);
				GL11.glTranslated((-0.5+0.5)/sc, (1.875-2)/sc, 0);
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
					GL11.glTranslated(0, -0.25, 0);
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
				double u = 0.0625*idx;
				double v = 0;
				double s = 0.0625;
				Tessellator v5 = Tessellator.instance;
				v5.startDrawingQuads();
				v5.addVertexWithUV(0, 0, 1, u, v+s);
				v5.addVertexWithUV(1, 0, 1, u+s, v+s);
				v5.addVertexWithUV(1, 0, 0, u+s, v);
				v5.addVertexWithUV(0, 0, 0, u, v);
				v5.draw();
				GL11.glPopMatrix();
				GL11.glPopAttrib();
			}
		}
	}
}
