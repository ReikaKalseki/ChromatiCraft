/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityChromaCrafter;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaLiquidRenderer;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class RenderChromaCrafter extends ChromaRenderBase {

	//private final ModelChromaCrafter model = new ModelChromaCrafter();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "chromacrafter.png";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityChromaCrafter te = (TileEntityChromaCrafter)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);
		//this.renderModel(te, model);
		if (!te.isInWorld() || MinecraftForgeClient.getRenderPass() == 0) {
			Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
			ReikaTextureHelper.bindTerrainTexture();
			if (te.isInWorld()) {
				RenderBlocks.getInstance().setRenderBoundsFromBlock(te.getBlockType());
				Tessellator.instance.setNormal(0, 1, 0);
				Tessellator.instance.setBrightness(te.getBlockType().getMixedBrightnessForBlock(te.worldObj, te.xCoord, te.yCoord, te.zCoord));
				Tessellator.instance.startDrawingQuads();
				Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				IIcon ico = b.getIcon(1, BlockType.GLASS.ordinal());
				RenderBlocks.getInstance().renderFaceYNeg(b, 0, 0, 0, ico);
				if (te.hasStructure()) {
					Tessellator.instance.setColorOpaque_F(0.75F, 0.75F, 0.75F);
					RenderBlocks.getInstance().renderFaceXNeg(b, 0, 0, 0, BlockStructureShield.lowerConnectedIcon);
					RenderBlocks.getInstance().renderFaceXPos(b, 0, 0, 0, BlockStructureShield.lowerConnectedIcon);
					RenderBlocks.getInstance().renderFaceXNeg(b, 0, 1, 0, BlockStructureShield.centerConnectedIcon);
					RenderBlocks.getInstance().renderFaceXPos(b, 0, 1, 0, BlockStructureShield.centerConnectedIcon);
					RenderBlocks.getInstance().renderFaceXNeg(b, 0, 2, 0, BlockStructureShield.upperConnectedIcon);
					RenderBlocks.getInstance().renderFaceXPos(b, 0, 2, 0, BlockStructureShield.upperConnectedIcon);
					Tessellator.instance.setColorOpaque_F(0.875F, 0.875F, 0.875F);
					RenderBlocks.getInstance().renderFaceZNeg(b, 0, 0, 0, BlockStructureShield.lowerConnectedIcon);
					RenderBlocks.getInstance().renderFaceZPos(b, 0, 0, 0, BlockStructureShield.lowerConnectedIcon);
					RenderBlocks.getInstance().renderFaceZNeg(b, 0, 1, 0, BlockStructureShield.centerConnectedIcon);
					RenderBlocks.getInstance().renderFaceZPos(b, 0, 1, 0, BlockStructureShield.centerConnectedIcon);
					RenderBlocks.getInstance().renderFaceZNeg(b, 0, 2, 0, BlockStructureShield.upperConnectedIcon);
					RenderBlocks.getInstance().renderFaceZPos(b, 0, 2, 0, BlockStructureShield.upperConnectedIcon);
				}
				else {
					RenderBlocks.getInstance().renderFaceXNeg(b, 0, 0, 0, ico);
					RenderBlocks.getInstance().renderFaceXPos(b, 0, 0, 0, ico);
					Tessellator.instance.setColorOpaque_F(0.875F, 0.875F, 0.875F);
					RenderBlocks.getInstance().renderFaceZNeg(b, 0, 0, 0, ico);
					RenderBlocks.getInstance().renderFaceZPos(b, 0, 0, 0, ico);
					Tessellator.instance.setColorOpaque_F(1, 1, 1);
					RenderBlocks.getInstance().renderFaceYPos(b, 0, 0, 0, b.getIcon(1, BlockType.GLASS.ordinal()));
				}
				Tessellator.instance.draw();
			}
			else {
				double d = 0.0625*1.75;
				GL11.glTranslated(0, d, 0);
				RenderBlocks.getInstance().renderBlockAsItem(b, BlockType.GLASS.ordinal(), 1);
			}
		}
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			this.renderItems(te, par8);
			this.renderFluid(te, par8);
		}
		GL11.glPopMatrix();
	}

	private void renderFluid(TileEntityChromaCrafter te, float par8) {
		double amt = te.getChromaLevel()*3D/te.CAPACITY;
		if (amt <= 0)
			return;

		/*
		FluidStack liquid = new FluidStack(ChromatiCraft.chroma, 1);

		int[] displayList = ReikaLiquidRenderer.getGLLists(liquid, te.worldObj, false);

		if (displayList == null) {
			return;
		}
		 */

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();

		ReikaLiquidRenderer.bindFluidTexture(ChromatiCraft.chroma);
		//ReikaLiquidRenderer.setFluidColor(liquid);

		//GL11.glTranslated(0, 0.005, 0);
		//GL11.glScaled(0.99, 0.99, 0.99);

		IIcon ico = ChromatiCraft.chroma.getStillIcon();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();

		Tessellator.instance.startDrawingQuads();
		double y = 0;
		double o = 0.01;

		Tessellator.instance.addVertexWithUV(1-o, o, 0+o, u, v);
		Tessellator.instance.addVertexWithUV(1-o, o, 1-o, du, v);
		Tessellator.instance.addVertexWithUV(0+o, o, 1-o, du, dv);
		Tessellator.instance.addVertexWithUV(0+o, o, 0+o, u, dv);

		while (amt > 0) {
			double lvl = Math.min(1, amt);
			float dv2 = ico.getInterpolatedV(lvl*16D);
			double o2 = o;
			double o3 = o;
			if (te.worldObj.getBlock(te.xCoord, (int)(te.yCoord+y+1), te.zCoord) == ChromaBlocks.STRUCTSHIELD.getBlockInstance())
				o2 = 0;
			Block bb = te.worldObj.getBlock(te.xCoord, (int)(te.yCoord+y-1), te.zCoord);
			if (bb == ChromaBlocks.STRUCTSHIELD.getBlockInstance() || bb == te.getBlockType())
				o3 = 0;
			//GL11.glCallList(displayList[(int)(lvl * (ReikaLiquidRenderer.LEVELS - 1))]);
			Tessellator.instance.addVertexWithUV(0+o, y-o2+lvl, 0+o, u, dv2);
			Tessellator.instance.addVertexWithUV(1-o, y-o2+lvl, 0+o, du, dv2);
			Tessellator.instance.addVertexWithUV(1-o, y+o3, 0+o, du, v);
			Tessellator.instance.addVertexWithUV(0+o, y+o3, 0+o, u, v);

			Tessellator.instance.addVertexWithUV(0+o, y+o3, 1-o, u, v);
			Tessellator.instance.addVertexWithUV(1-o, y+o3, 1-o, du, v);
			Tessellator.instance.addVertexWithUV(1-o, y-o2+lvl, 1-o, du, dv2);
			Tessellator.instance.addVertexWithUV(0+o, y-o2+lvl, 1-o, u, dv2);

			Tessellator.instance.addVertexWithUV(1-o, y-o2+lvl, 0+o, u, dv2);
			Tessellator.instance.addVertexWithUV(1-o, y-o2+lvl, 1-o, du, dv2);
			Tessellator.instance.addVertexWithUV(1-o, y+o3, 1-o, du, v);
			Tessellator.instance.addVertexWithUV(1-o, y+o3, 0+o, u, v);

			Tessellator.instance.addVertexWithUV(0+o, y+o3, 0+o, u, v);
			Tessellator.instance.addVertexWithUV(0+o, y+o3, 1-o, du, v);
			Tessellator.instance.addVertexWithUV(0+o, y-o2+lvl, 1-o, du, dv2);
			Tessellator.instance.addVertexWithUV(0+o, y-o2+lvl, 0+o, u, dv2);
			amt -= lvl;
			y += lvl;
			//GL11.glTranslated(0, lvl, 0);
		}

		double o2 = y < 3 ? 0 : o;
		Tessellator.instance.addVertexWithUV(0+o, y-o2, 0+o, u, dv);
		Tessellator.instance.addVertexWithUV(0+o, y-o2, 1-o, du, dv);
		Tessellator.instance.addVertexWithUV(1-o, y-o2, 1-o, du, v);
		Tessellator.instance.addVertexWithUV(1-o, y-o2, 0+o, u, v);
		Tessellator.instance.draw();

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderItems(TileEntityChromaCrafter te, float par8) {
		PoolRecipe r = te.getActiveRecipe();
		if (r != null) {
			Collection<ItemStack> li = r.getInputs();
			li.add(r.getMainInput());
			if (te.hasEtherBerries())
				li.add(ChromaStacks.etherBerries);
			int i = 0;
			double da = 360D*4/li.size();
			for (ItemStack is : li) {
				is = ReikaItemHelper.getSizedItemStack(is, 1);
				InertItem ei = new InertItem(te.worldObj, is);
				ei.hoverStart = 0;
				ei.age = te.getTicksExisted();
				double a = Math.toRadians(te.getTicksExisted()*4D+i*da);
				double rd = 0.25;
				double dx = rd*Math.cos(a);
				double dz = rd*Math.sin(a);
				double dy = 1.25+1.25*Math.sin(a/4D);
				RenderItem.renderInFrame = true;
				RenderManager.instance.renderEntityWithPosYaw(ei, 0.5+dx, dy, 0.5+dz, 0, 0/*tick*/);
				RenderItem.renderInFrame = false;
				i++;
			}
		}
	}

}
