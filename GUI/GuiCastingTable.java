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

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiCastingTable extends GuiChromaBase {

	private final TileEntityCastingTable tile;

	public GuiCastingTable(EntityPlayer ep, TileEntityCastingTable te) {
		super(new ContainerCastingTable(ep, te), ep, te);

		tile = te;
		ySize = this.isMultiForm() ? 240 : 209;
		//xSize = 219;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int a = (width - xSize) / 2;
		int b = (height - ySize) / 2;

		if (this.isMultiForm()) {
			HashMap<List<Integer>, TileEntityItemStand> li = tile.getOtherStands();
			for (List<Integer> key : li.keySet()) {
				TileEntityItemStand te = li.get(key);
				ItemStack is = te.getStackInSlot(0);
				if (is != null) {
					int i = key.get(0);
					int k = key.get(1);
					int sx = i == 0 ? 0 : i < 0 ? -1 : 1;
					int sy = k == 0 ? 0 : k < 0 ? -1 : 1;
					int tx = Math.abs(i) == 2 ? 38 : 64;
					int ty = Math.abs(k) == 2 ? 38 : 63;
					int dx = 80+sx*(tx);
					int dy = 75+sy*(ty);
					api.drawItemStack(itemRender, is, dx, dy);
					int x1 = a+dx-1;
					int x2 = a+dx+17;
					int y1 = b+dy-1;
					int y2 = b+dy+17;/*
				if (api.isMouseInBox(x1, x2, y1, y2)) {
					int mx = api.getMouseRealX();
					int my = api.getMouseRealY();
					String s = is.getDisplayName();
					api.drawTooltipAt(fontRendererObj, s, mx-fontRendererObj.getStringWidth(s)/2, my);
				}*/
				}
			}
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
		ReikaTextureHelper.bindTerrainTexture();
		RecipeType tier = tile.getTier();
		int d = 10;
		int[] color = new int[]{0xffffff, 0xffff99, 0xffff55, 0xffdd00};
		for (int i = 0; i < 1+tier.ordinal(); i++) {
			int x = (i/*%2*/)*d;
			int y = 0;//(i/2)*d;
			float red = ReikaColorAPI.getRedFromInteger(color[i])/255F;
			float green = ReikaColorAPI.getGreenFromInteger(color[i])/255F;
			float blue = ReikaColorAPI.getBlueFromInteger(color[i])/255F;
			GL11.glColor4f(red, green, blue, 1);
			this.drawTexturedModelRectFromIcon(x, y, ChromaIcons.DIAMOND.getIcon(), 16, 16);
		}
		GL11.glDisable(GL11.GL_BLEND);

		CastingRecipe r = tile.getActiveRecipe();
		if (r != null) {
			ChromaResearch res = r.getFragment();
			if (res != null && !ChromaResearchManager.instance.playerHasFragment(player, res)) {
				--draw question mark, also do not draw item--
				return;
			}

			ItemStack out = r.getOutput();
			api.drawItemStack(itemRender, out, 189, 12);
			if (api.isMouseInBox(a+186, a+207, b+10, b+30)) {
				int mx = api.getMouseRealX();
				int my = api.getMouseRealY();
				api.drawTooltipAt(fontRendererObj, out.getDisplayName(), mx-30, my);
			}

			zLevel = 100;

			if (!r.canRunRecipe(player)) {
				ReikaTextureHelper.bindTerrainTexture();
				GL11.glEnable(GL11.GL_BLEND);
				IIcon ico = ChromaIcons.NOENTER.getIcon();
				GL11.glColor4f(1, 1, 1, 0.5F);
				this.drawTexturedModelRectFromIcon(189, 12, ico, 16, 16);
				GL11.glDisable(GL11.GL_BLEND);
			}
			zLevel = 0;
			//this.drawRect(188, 11, 188+18, 29, 0xffABABAB);

			if (r instanceof PylonRecipe) {
				PylonRecipe p = (PylonRecipe)r;
				ElementTagCompound tag = p.getRequiredAura();
				for (CrystalElement e : tag.elementSet()) {
					int energy = tile.getEnergy(e);
					int w = 4;
					int x = 183+e.ordinal()%4*w*2;
					int h = energy*35/tag.getValue(e);
					int dy = Math.max(35-h, 0); //prevent gui overflow
					int y1 = 35+e.ordinal()/4*40;
					int y = 70+e.ordinal()/4*40;
					this.drawRect(x, y1, x+w, y, e.getJavaColor().darker().darker().getRGB());
					this.drawRect(x, y1+dy, x+w, y, e.getColor());
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		this.drawFromBackground(j+xSize, k, 176, 0, 43, ySize);
	}

	@Override
	protected int getTitlePosition() {
		return this.isMultiForm() ? 3 : super.getTitlePosition();
	}

	@Override
	protected boolean labelInventory() {
		return !this.isMultiForm();
	}

	private boolean isMultiForm() {
		return tile.isAtLeast(RecipeType.MULTIBLOCK);
	}

	@Override
	public String getGuiTexture() {
		return tile.isAtLeast(RecipeType.PYLON) ? "table5" : this.isMultiForm() ? "table4" : "table2";
	}

}
