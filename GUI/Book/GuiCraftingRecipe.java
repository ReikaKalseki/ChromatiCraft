/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import codechicken.nei.NEIClientConfig;

public class GuiCraftingRecipe extends GuiBookSection {

	private final ArrayList<IRecipe> recipes;
	private int index = 0;
	private int recipeTextOffset = 0;
	private boolean centeredMouse = false;

	public GuiCraftingRecipe(EntityPlayer ep, ArrayList<IRecipe> out, int offset) {
		super(ep, null, 256, 220, false);
		recipes = new ArrayList(out);
		index = offset;
	}

	private IRecipe getActiveRecipe() {
		return recipes.get(index);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";
		if (recipes.size() > 1) {
			buttonList.add(new ImagedGuiButton(0, j+205, k-3, 10, 12, 183, 6, file, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(1, j+215, k-3, 10, 12, 193, 6, file, ChromatiCraft.class));
		}

		if (subpage == 0 && this.getItemCounts().size() > 10) {
			buttonList.add(new ImagedGuiButton(2, j+205, k+50, 12, 10, 100, 6, file, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(3, j+205, k+60, 12, 10, 112, 6, file, ChromatiCraft.class));
		}
	}

	@Override
	public final void keyTyped(char c, int key) {
		super.keyTyped(c, key);

		if (ModList.NEI.isLoaded() && key == NEIClientConfig.getKeyBinding("gui.recipe")) {
			int x = ReikaGuiAPI.instance.getMouseRealX();
			int y = ReikaGuiAPI.instance.getMouseRealY();
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;
			if (x >= j && y >= k && x < j+xSize && y < k+ySize) {
				ItemStack is = ReikaGuiAPI.instance.getItemRenderAt(x, y);
				if (is != null) {
					codechicken.nei.recipe.GuiCraftingRecipe.openRecipeGui("item", is);
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0 && index > 0) {
			index--;
			recipeTextOffset = 0;
			subpage = Math.min(subpage, this.getMaxSubpage());
		}
		else if (button.id == 1 && index < recipes.size()-1) {
			index++;
			recipeTextOffset = 0;
			subpage = Math.min(subpage, this.getMaxSubpage());
		}
		if (button.id == 2 && recipeTextOffset > 0) {
			recipeTextOffset--;
		}
		else if (button.id == 3 && recipeTextOffset < this.getItemCounts().size()-11) {
			recipeTextOffset++;
		}
		//renderq = 22.5F;
		super.actionPerformed(button);
		this.initGui();
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.CRAFTING;
	}

	private final void drawRecipes() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
		try {
			this.drawAuxData(posX, posY);
		}
		catch (Exception e) {
			ReikaChatHelper.write(Arrays.toString(e.getStackTrace()));
			e.printStackTrace();
		}
	}

	protected void drawAuxData(int posX, int posY) {
		//ChromaBookData.drawCastingRecipe(fontRendererObj, ri, this.getActiveRecipe(), subpage, posX, posY);
		IRecipe ir = this.getActiveRecipe();
		ItemStack[] arr = ReikaRecipeHelper.getPermutedRecipeArray(ir);
		for (int i = 0; i < arr.length; i++) {
			ItemStack is = arr[i];
			if (is != null) {
				int x = posX+54+(i%3)*18;
				int y = posY+10+(i/3)*18;
				api.drawItemStackWithTooltip(itemRender, is, x, y);
			}
		}
		api.drawItemStackWithTooltip(itemRender, ir.getRecipeOutput(), posX+7, posY+5);
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();

		this.drawAuxGraphics(posX, posY);
	}

	@Override
	public int getMaxSubpage() {
		return 0;
	}

	private ItemHashMap<Integer> getItemCounts() {
		return ReikaRecipeHelper.getItemCountsForDisplay(this.getActiveRecipe());
	}

	protected void drawAuxGraphics(int posX, int posY) {
		ItemHashMap<Integer> items = this.getItemCounts();
		ArrayList<ItemStack> li = new ArrayList(items.keySet());
		for (int i = recipeTextOffset; i < li.size(); i++) {
			ItemStack is = li.get(i);
			int num = items.get(is);
			String s = String.format("%s%s: x%d", is.getDisplayName(), EnumChatFormatting.RESET.toString(), num);
			fontRendererObj.drawString(s, posX+descX+3, posY+descY+(fontRendererObj.FONT_HEIGHT+2)*(i-recipeTextOffset), 0xffffff);
			if (i-recipeTextOffset > 9)
				break;
		}
	}

	@Override
	public final void drawScreen(int x, int y, float f)
	{
		super.drawScreen(x, y, f);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		//if (subpage == 0 && !disable)
		this.drawRecipes();

		this.drawGraphics();

		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public String getPageTitle() {
		return this.getActiveRecipe().getRecipeOutput().getDisplayName();//+" Casting";
	}

	@Override
	protected int getTitleOffset() {
		return 27;
	}

}
