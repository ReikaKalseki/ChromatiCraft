/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.CastingAPI.RuneTempleRecipe;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer.FontType;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.GUI.Tile.GuiCastingAuto;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.IO.DelegateFontRenderer;
import Reika.DragonAPI.Instantiable.AlphabeticItemComparator;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import codechicken.nei.NEIClientConfig;

public class GuiCastingRecipe extends GuiBookSection {

	private final ArrayList<CastingRecipe> recipes;
	private int index = 0;
	private int recipeTextOffset = 0;
	private boolean centeredMouse = false;

	public static RuneTempleRecipe runeHintRecipe;

	public GuiCastingRecipe(EntityPlayer ep, ArrayList<CastingRecipe> out, int offset, boolean nei) {
		super(ChromaGuis.RECIPE, ep, null, 256, 220, nei);
		recipes = new ArrayList(out);
		index = offset;
	}

	private CastingRecipe getActiveRecipe() {
		return recipes.get(index);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		if (ChromaResearchManager.instance.playerHasFragment(player, ChromaResearch.AUTO))
			buttonList.add(new CustomSoundGuiButton(4, j+xSize-27-20-28, k-2, 20, 20, " ", this));

		if (recipes.size() > 1) {
			buttonList.add(new CustomSoundImagedGuiButton(0, j+205, k-3, 10, 12, 183, 6, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(1, j+215, k-3, 10, 12, 193, 6, file, ChromatiCraft.class, this));
		}

		if (subpage == 0 && this.getActiveRecipe().getItemCountsForDisplay().size() > 10) {
			buttonList.add(new CustomSoundImagedGuiButton(2, j+205, k+50, 12, 10, 100, 6, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(3, j+205, k+60, 12, 10, 112, 6, file, ChromatiCraft.class, this));
		}

		if (subpage == 1 && !((RuneTempleRecipe)this.getActiveRecipe()).getRunePositions().isEmpty()) {
			CustomSoundImagedGuiButton b = new CustomSoundImagedGuiButton(SAVE_AND_EXIT+1, j+xSize, k+45, 22, 39, 65, 210, file, ChromatiCraft.class, this) {
				@Override
				protected void renderButton() {
					super.renderButton();

					ReikaTextureHelper.bindTerrainTexture();
					BlendMode.ADDITIVEDARK.apply();
					IIcon ico = CrystalElement.elements[(int)((System.currentTimeMillis()/1000L)%16)].getGlowRune();
					ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(xPosition+1, yPosition+11, ico, 16, 16);
					BlendMode.DEFAULT.apply();
				}

			};
			this.addAuxButton(b, "Visualize");
		}

		if (NEItrigger && !centeredMouse) {
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
			centeredMouse = true;
		}
	}

	@Override
	public final void keyTyped(char c, int key) {
		super.keyTyped(c, key);

		if (ModList.NEI.isLoaded() && key == NEIClientConfig.getKeyBinding("gui.recipe") && !NEIClientConfig.isHidden()) {
			int x = ReikaGuiAPI.instance.getMouseRealX();
			int y = ReikaGuiAPI.instance.getMouseRealY();
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;
			if (x >= j && y >= k && x < j+xSize && y < k+ySize) {
				ItemStack is = ReikaGuiAPI.instance.getItemRenderAt(x, y);
				if (is != null) {
					ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.33F, 1);
					if (!MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(null, is)))
						codechicken.nei.recipe.GuiCraftingRecipe.openRecipeGui("item", is);
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (System.currentTimeMillis()-buttoncooldown >= 50) {
			if (button.id == 0 && index > 0) {
				ItemStack is = this.getActiveRecipe().getOutput();
				index--;
				while (GuiScreen.isShiftKeyDown() && index > 0 && ReikaItemHelper.matchStacks(is, this.getActiveRecipe().getOutput())) {
					index--;
				}
				recipeTextOffset = 0;
				subpage = Math.min(subpage, this.getMaxSubpage());
			}
			else if (button.id == 1 && index < recipes.size()-1) {
				CastingRecipe cr = this.getActiveRecipe();
				ItemStack is1 = cr.getOutput();
				index++;
				while (GuiScreen.isShiftKeyDown() && index < recipes.size()-1 && cr.shouldGroupAsRecipe(is1, this.getActiveRecipe().getOutput())) {
					index++;
				}
				recipeTextOffset = 0;
				subpage = Math.min(subpage, this.getMaxSubpage());
			}
			if (button.id == SAVE_AND_EXIT+1) {
				runeHintRecipe = (RuneTempleRecipe)this.getActiveRecipe();
				this.saveAndExit();
			}
			else if (button.id == 2 && recipeTextOffset > 0) {
				recipeTextOffset--;
			}
			else if (button.id == 3 && recipeTextOffset < this.getActiveRecipe().getItemCountsForDisplay().size()-11) {
				recipeTextOffset++;
			}
			else if (button.id == 4) {
				GuiCastingAuto.lexiconSelectedRecipe = this.getActiveRecipe();
				player.closeScreen();
			}
		}
		//renderq = 22.5F;
		super.actionPerformed(button);
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
		this.drawAuxData(posX, posY);
	}

	protected void drawAuxData(int posX, int posY) {
		ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		ChromaBookData.drawCastingRecipe(fontRendererObj, ri, this.getActiveRecipe(), subpage, posX, posY, true);
	}

	private final void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		int msx = ReikaGuiAPI.instance.getMouseRealX();
		int msy = ReikaGuiAPI.instance.getMouseRealY();

		this.drawAuxGraphics(posX, posY);
	}

	@Override
	public int getMaxSubpage() {
		return this.getActiveRecipe().type.ordinal();
	}

	protected void drawAuxGraphics(int posX, int posY) {
		if (ChromaResearchManager.instance.playerHasFragment(player, ChromaResearch.AUTO))
			api.drawItemStack(itemRender, fontRendererObj, ChromaTiles.AUTOMATOR.getCraftedProduct(), posX+xSize-27-16-28, posY+7);

		if (subpage == 0) {
			ItemHashMap<Integer> items = this.getActiveRecipe().getItemCountsForDisplay();
			ArrayList<ItemStack> li = new ArrayList(items.keySet());
			Collections.sort(li, new AlphabeticItemComparator());
			for (int i = recipeTextOffset; i < li.size(); i++) {
				ItemStack is = li.get(i);
				int num = items.get(is);
				String s0 = is.getDisplayName();
				String s = String.format(": x%d", num);
				FontRenderer fr = s0.contains(FontType.OBFUSCATED.id) ? FontType.OBFUSCATED.renderer : fontRendererObj;
				s0 = DelegateFontRenderer.stripFlags(s0);
				fr.drawString(s0, posX+descX+3, posY+descY+(fr.FONT_HEIGHT+2)*(i-recipeTextOffset), 0xffffff);
				fontRendererObj.drawString(s, posX+descX+3+fr.getStringWidth(s0), posY+descY+(fr.FONT_HEIGHT+2)*(i-recipeTextOffset), 0xffffff);
				if (i-recipeTextOffset > 9)
					break;
			}
		}
		if (subpage == 3) {
			/*
			ElementTagCompound tag = ((PylonRecipe)this.getActiveRecipe()).getRequiredAura();
			int i = 0;
			Collection<CrystalElement> set = tag.elementSet();
			boolean wrap = set.size() > 10;
			for (CrystalElement e : set) {
				String s1 = String.format("%s:", e.displayName);
				String s2 = String.format("%d %s", tag.getValue(e), wrap ? "L" : "Lumens");
				int color = ReikaColorAPI.mixColors(e.getColor(), 0xffffff, 0.5F);
				int x1 = posX+descX+3+(wrap ? (i/8)*120 : 0);
				int x2 = x1+56;
				int y = posY+descY+(fontRendererObj.FONT_HEIGHT+2)*(wrap ? i%8 : i);
				fontRendererObj.drawString(s1, x1, y, color);
				fontRendererObj.drawString(s2, x2, y, color);
				i++;
			}
			 */
		}
		if (!this.getActiveRecipe().canRunRecipe(null, player)) {
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glColor4f(1, 1, 1, 0.5F);
			api.drawTexturedModelRectFromIcon(posX+9, posY+13, ChromaIcons.NOENTER.getIcon(), 16, 16);
			GL11.glPopAttrib();
			if (api.isMouseInBox(posX+9, posX+25, posY+13, posY+29)) {
				ChromaBookData.drawRecipeMissingProgress(this.getActiveRecipe(), player, itemRender, fontRendererObj, posX+145, posY+18+4, false);
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
		String s = this.getActiveRecipe().canRunRecipe(null, player) ? "" : ChromaFontRenderer.FontType.OBFUSCATED.id;
		return s+this.getActiveRecipe().getOutputForDisplay().getDisplayName();//+" Casting";
	}

	@Override
	protected int getTitleOffset() {
		return 27;
	}

}
