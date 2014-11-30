/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;

public class GuiCastingRecipe extends GuiBookSection {

	private final ArrayList<CastingRecipe> recipes;
	private int index = 0;

	public GuiCastingRecipe(EntityPlayer ep, ItemStack out) {
		super(ep, null, 256, 220);
		recipes = RecipesCastingTable.instance.getAllRecipesMaking(out);
	}

	private CastingRecipe getActiveRecipe() {
		return recipes.get(index);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		if (recipes.size() > 1) {
			String file = "Textures/GUIs/Handbook/buttons.png";
			buttonList.add(new ImagedGuiButton(0, j+135, k-3, 36, 10, 126, 6, "Prev", 0x000000, false, file, ChromatiCraft.class));
			buttonList.add(new ImagedGuiButton(1, j+185, k-3, 36, 10, 126, 6, "Next", 0x000000, false, file, ChromatiCraft.class));
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		if (button.id == 0 && index > 0) {
			index--;
		}
		else if (button.id == 1 && index < recipes.size()-1) {
			index++;
		}
		//renderq = 22.5F;
		this.initGui();
	}

	@Override
	protected PageType getGuiLayout() {
		switch(subpage) {
		case 0:
			return PageType.CAST;
		case 1:
			return PageType.RUNES;
		case 2:
			return PageType.MULTICAST;
		case 3:
			return PageType.PYLONCAST;
		}
		return PageType.PLAIN;
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
		ChromaBookData.drawCastingRecipe(fontRendererObj, ri, this.getActiveRecipe(), subpage, posX, posY);
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
		return this.getActiveRecipe().type.ordinal();
	}

	protected void drawAuxGraphics(int posX, int posY) {
		if (subpage == 0) {
			ItemHashMap<Integer> items = this.getActiveRecipe().getItemCounts();
			int i = 0;
			for (ItemStack is : items.keySet()) {
				int num = items.get(is);
				String s = String.format("%s%s: x%d", is.getDisplayName(), EnumChatFormatting.RESET.toString(), num);
				fontRendererObj.drawString(s, posX+descX+3, posY+descY+(fontRendererObj.FONT_HEIGHT+2)*i, 0xffffff);
				i++;
			}
		}
		if (subpage == 3) {
			ElementTagCompound tag = ((PylonRecipe)this.getActiveRecipe()).getRequiredAura();
			int i = 0;
			for (CrystalElement e : tag.elementSet()) {
				String s1 = String.format("%s:", e.displayName);
				String s2 = String.format("%d Lumens", tag.getValue(e));
				int color = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
				fontRendererObj.drawString(s1, posX+descX+3, posY+descY+(fontRendererObj.FONT_HEIGHT+2)*i, color);
				fontRendererObj.drawString(s2, posX+descX+3+56, posY+descY+(fontRendererObj.FONT_HEIGHT+2)*i, color);
				i++;
			}
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
		return this.getActiveRecipe().getOutput().getDisplayName()+" Casting";
	}

}
