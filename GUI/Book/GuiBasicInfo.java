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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaBookData;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ElementEncodedNumber;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.EnchantmentRecipe;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.Render.RuneShapeRenderer;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.StructureTypeData;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Magic.CastingTuning;
import Reika.ChromatiCraft.Magic.CastingTuning.TuningKey;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Magic.RuneShape.RuneViewer;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.CrystalRenderer;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.World.Dimension.StructureCalculator;
import Reika.DragonAPI.Instantiable.Data.Compass;
import Reika.DragonAPI.Instantiable.Data.Compass.CompassDivisions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.FanDirections;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class GuiBasicInfo extends GuiBookSection {

	private Collection<PylonParticle> particles = new ArrayList();

	private RuneViewer view;
	private int modifier;

	private ArrayList<EnchantmentRecipe> enchants = new ArrayList();
	private static final Map<Enchantment, Integer> enchantMap = TileEntityAutoEnchanter.getBoostedLevels();
	private static final ArrayList<Enchantment> enchantIndices = new ArrayList(enchantMap.keySet());

	public GuiBasicInfo(EntityPlayer ep, ChromaResearch r) {
		super(ChromaGuis.INFO, ep, r, 256, 220, false);

		if (r == ChromaResearch.USINGRUNES) {
			view = this.getAllUsedRunes();
		}
		else if (r == ChromaResearch.ENCHANTING) {
			for (EnchantmentRecipe e : RecipesCastingTable.instance.getAllEnchantingRecipes()) {
				if (this.shouldDisplayFragment(e.parent))
					enchants.add(e);
			}
		}
	}

	private RuneViewer getAllUsedRunes() {
		Map<Coordinate, CrystalElement> data = new HashMap();
		Collection<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipes();
		for (CastingRecipe cr : li) {
			if (cr instanceof TempleCastingRecipe) {
				ChromaResearch r = cr.getFragment();
				if (r == null || this.shouldDisplayFragment(r)) {
					TempleCastingRecipe t = (TempleCastingRecipe)cr;
					if (ChromaResearchManager.instance.playerHasUsedRecipe(player, cr)) {
						Map<Coordinate, CrystalElement> map = t.getRunes().getRunes();
						data.putAll(map);
					}
				}
			}
		}
		RuneShape rs = new RuneShape(data);
		return rs.getView();
	}

	private RuneViewer getAllVisibleRunes() {
		Map<Coordinate, CrystalElement> data = new HashMap();
		Collection<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipes();
		for (CastingRecipe cr : li) {
			if (cr instanceof TempleCastingRecipe) {
				ChromaResearch r = cr.getFragment();
				if (r == null || this.shouldDisplayFragment(r)) {
					TempleCastingRecipe t = (TempleCastingRecipe)cr;
					Map<Coordinate, CrystalElement> map = t.getRunes().getRunes();
					data.putAll(map);
				}
			}
		}
		RuneShape rs = new RuneShape(data);
		return rs.getView();
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String file = "Textures/GUIs/Handbook/buttons.png";

		if (page == ChromaResearch.USINGRUNES && subpage == 1 && view.getSizeY() > 1) {
			buttonList.add(new CustomSoundImagedGuiButton(3, j+230, k+75, 12, 10, 100, 6, file, ChromatiCraft.class, this));
			buttonList.add(new CustomSoundImagedGuiButton(2, j+230, k+85, 12, 10, 112, 6, file, ChromatiCraft.class, this));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (System.currentTimeMillis()-buttoncooldown >= 50) {
			if (button.id == 2 && modifier > 0) {
				modifier--;
			}
			else if (button.id == 3) {
				if (modifier < view.getSizeY()-1)
					modifier++;
			}
			else {
				modifier = 0;
			}
		}
		//renderq = 22.5F;
		super.actionPerformed(button);
		this.initGui();
	}

	@Override
	protected int getMaxSubpage() {
		if (page == ChromaResearch.ELEMENTS)
			return CrystalElement.elements.length;
		else if (page == ChromaResearch.USINGRUNES)
			return 1;
		else if (page == ChromaResearch.ENCHANTS)
			return ChromaEnchants.enchantmentList.length+enchantMap.size();
		else if (page == ChromaResearch.ENCHANTING)
			return enchants.size()+1;
		else if (page == ChromaResearch.STRUCTUREPASSWORDS)
			return 1;
		else if (page == ChromaResearch.CASTTUNING)
			return 1;
		return 0;
	}

	@Override
	protected PageType getGuiLayout() {
		if (this.isElementPage())
			return PageType.ELEMENT;
		else if (page == ChromaResearch.USINGRUNES && subpage == 1)
			return PageType.RUNES;
		else if (page == ChromaResearch.ENCHANTING && subpage > 1)
			return PageType.MULTICAST;
		else if (page == ChromaResearch.ENCHANTING && subpage == 1)
			return PageType.RUNES;
		else if (page == ChromaResearch.CASTTUNING && subpage == 1)
			return PageType.CASTTUNE;
		else if (page == ChromaResearch.STRUCTUREPASSWORDS && subpage == 1)
			return PageType.STRUCTPASS;
		return PageType.PLAIN;
	}

	@Override
	public void drawScreen(int mx, int my, float f) {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;
		super.drawScreen(mx, my, f);

		int c = 0xffffff;
		int px = posX+descX;
		if (subpage == 0 || page.sameTextAllSubpages()) {
			String s = String.format("%s", page.getData());
			boolean flag = page.isUnloadable();
			if (flag) {
				int c1 = 0x6c6c6c; //7a7a7a avg
				int c2 = 0x828282;
				float mix = (float)(0.5+0.5*Math.sin(this.getGuiTick()/16D));
				fontRendererObj.drawSplitString(s, px, posY+descY, 242, ReikaColorAPI.mixColors(c1, c2, mix));
				ChromaFontRenderer.FontType.OBFUSCATED.renderer.drawSplitString(s, px, posY+descY, 242, ReikaColorAPI.mixColors(c1, c2, 1-mix));
				String err = "Something is wrong with the fabric of the world; this entry seems to be illegible, and whatever it pertains to is likely unavailable.";
				err += "\n\nPerhaps someone else might have influenced this, and perhaps they could be of assistance to you.";
				fontRendererObj.drawSplitString(err, px, posY+descY, 242, 0xffffff);
			}
			else {
				fontRendererObj.drawSplitString(s, px, posY+descY, 242, c);
			}
		}
		if (this.isElementPage()) {
			this.renderElementPage(CrystalElement.elements[subpage-1], posX, posY, px, c);
		}
		else if (page == ChromaResearch.CRYSTALS) {
			this.renderCrystal(posX, posY);
		}
		else if (page == ChromaResearch.PYLONS) {
			this.renderPylon(posX, posY);
		}
		else if (page == ChromaResearch.USINGRUNES && subpage == 1) {
			this.renderRunes(posX, posY);
		}
		else if (page == ChromaResearch.ENCHANTING && subpage > 0) {
			if (subpage == 1)
				this.renderEnchantmentRunes(posX, posY);
			else
				this.renderEnchantment(posX, posY, enchants.get(subpage-2));
		}
		else if (page == ChromaResearch.CASTTUNING && subpage > 0) {
			this.renderPlayerCastingTuning(posX, posY, f);
		}
		else if (page == ChromaResearch.ENCHANTS && subpage >= 1) {
			this.renderEnchantDesc(posX, posY, px, c);
		}
		else if (page == ChromaResearch.STRUCTUREPASSWORDS && subpage >= 1) {
			this.renderStructureKeys(posX, posY);
		}
	}

	private void renderPlayerCastingTuning(int posX, int posY, float ptick) {
		int x = posX+xSize/2;
		int y = posY+ySize/2+39;
		double a0 = 0;
		double r = 63;
		double ri = 47;

		TuningKey tk = CastingTuning.instance.getTuningKey(player);
		HashMap<FanDirections, CrystalElement> map = tk.getCompass();
		Compass<CrystalElement> c = new Compass(CompassDivisions.FULL);
		c.squareRender = true;
		for (Entry<FanDirections, CrystalElement> e : map.entrySet()) {
			c.addValue(e.getKey(), e.getValue());
		}
		c.setGeometry(x, y, r, ri, a0);
		c.render(CrystalElement.getColorMap());

		ReikaTextureHelper.bindTerrainTexture();
		int si = 8;
		for (Entry<FanDirections, CrystalElement> e : map.entrySet()) {
			double a = Math.toRadians(a0-e.getKey().angle);
			double ir = c.getOuterRadiusAt(a)*0.9;
			int ix = (int)Math.round(x+ir*Math.cos(a));
			int iy = (int)Math.round(y+ir*Math.sin(a));
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(ix-si/2, iy-si/2, e.getValue().getOutlineRune(), si, si);
		}

		//ReikaGuiAPI.instance.drawItemStack(itemRender, ChromaTiles.TABLE.getCraftedProduct(), x-8, y-8);
		int d = 8;
		ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(x-d, y-d, ChromaTiles.TABLE.getBlock().getIcon(1, ChromaTiles.TABLE.getBlockMetadata()), d*2, d*2);

		ElementEncodedNumber een = new ElementEncodedNumber(player.getUniqueID().hashCode(), 8);
		for (int i = 0; i < een.getLength(); i++) {
			si = 16;
			int ix = x+(i-een.getLength()/2)*(si+si/2-1)+si*3/4-1;
			int iy = posY+66;
			CrystalElement e = een.getSlot(i);
			ReikaGuiAPI.instance.drawTexturedModelRectFromIcon(ix-si/2, iy-si/2, e.getGlowRune(), si, si);
		}

		GL11.glPushMatrix();
		double s = 1.75;
		GL11.glTranslated(x-14, posY+18, 0);
		GL11.glScaled(s, s, s);
		page.renderIcon(itemRender, fontRendererObj, 0, 0);
		//ReikaGuiAPI.instance.drawItemStack(itemRender, new ItemStack(Blocks.brick_block), 0, 0);
		GL11.glPopMatrix();
	}

	private void renderStructureKeys(int posX, int posY) {
		int k = 0;
		int n = 0;
		ArrayList<StructureTypeData> set = new ArrayList(StructureCalculator.getStructureColorTypes().values());

		Collections.shuffle(set, new Random(player.hashCode()));

		for (StructureTypeData data : set) {
			boolean complete = ProgressionManager.instance.hasPlayerCompletedStructureColor(player, data.color);
			String tex = "Textures/dimensionstructures.png";
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
			int idx = data.type.getIconIndex();
			if (!complete)
				idx = 1;
			int u = (idx%8)*32;
			int v = (idx/8)*32;
			int maxw = 4;
			if (k >= maxw) {
				k = 0;
				n++;
			}
			int x = posX+k*60+10;
			int y = posY+n*36+29;

			api.drawTexturedModalRect(x, y, u, v, 32, 32);

			GL11.glColor4f(1, 1, 1, 1);
			if (complete) {
				if (api.isMouseInBox(x, x+33, y, y+33)) {
					//api.drawTooltipAt(font, d.getDisplayTime(j), mx, my);
					ReikaRenderHelper.disableLighting();

					ReikaTextureHelper.bindTerrainTexture();

					int pass = data.getPassword(player);
					ElementEncodedNumber vals = new ElementEncodedNumber(pass, 8);
					for (int i = 0; i < vals.getLength(); i++) {
						CrystalElement e = vals.getSlot(i);
						IIcon ico = e.getGlowRune();
						int dx = posX+35+i*24;
						int dy = posY+189;

						api.drawTexturedModelRectFromIcon(dx, dy, ico, 16, 16);
					}
				}

				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				ReikaRenderHelper.prepareGeoDraw(true);
				api.drawRectFrame(x-1, y-1, 34, 34, 0xff000000 | data.color.getColor());
				GL11.glPopAttrib();

				//fontRendererObj.drawString(data.color.displayName, posX+10, posY+150, 0xffffff);
			}

			k++;
		}
	}

	private void renderEnchantDesc(int posX, int posY, int px, int c) {
		Enchantment e = this.getActiveEnchant();
		String s = StatCollector.translateToLocal(e.getName())+": Up to "+e.getTranslatedName(e.getMaxLevel())+"\n\n";
		if (ReikaEnchantmentHelper.isVanillaEnchant(e)) {
			s = "";
			s += ChromaDescriptions.getNotes(page, ChromaEnchants.enchantmentList.length+1);
			s = String.format(s, e.getTranslatedName(e.getMaxLevel()), e.getTranslatedName(enchantMap.get(e)));
		}
		else {
			s += ChromaDescriptions.getNotes(page, subpage);
		}
		fontRendererObj.drawSplitString(s, px, posY+descY, 242, c);
	}

	private Enchantment getActiveEnchant() {
		if (subpage <= ChromaEnchants.enchantmentList.length)
			return ChromaEnchants.enchantmentList[subpage-1].getEnchantment();
		return enchantIndices.get(subpage-ChromaEnchants.enchantmentList.length-1);
	}

	private void renderEnchantmentRunes(int posX, int posY) {
		Map<Coordinate, CrystalElement> map = EnchantmentRecipe.getEnchantingRunes();
		RuneShape rs = new RuneShape(map);
		RuneViewer rv = rs.getView();
		RuneShapeRenderer.instance.render(rv, posX+xSize/2, posY+ySize/2+8, rv.getMinY());
	}

	private void renderEnchantment(int posX, int posY, EnchantmentRecipe r) {
		ChromaBookData.drawCastingRecipe(fontRendererObj, itemRender, r, 2, posX, posY+8);

		String s = r.getMainInput().getDisplayName()+" - "+r.enchantment.getTranslatedName(r.level);
		fontRendererObj.drawString(s, posX+xSize/2-fontRendererObj.getStringWidth(s)/2, posY+192, 0xffffff);
	}

	private void renderRunes(int posX, int posY) {
		RuneShapeRenderer.instance.render(view, posX+xSize/2, posY+ySize/2+8, modifier+view.getMinY());
	}

	private void renderPylon(int posX, int posY) {
		float mod = 2000F;
		int tick = (int)((System.currentTimeMillis()/(double)mod)%16);
		CrystalElement e1 = CrystalElement.elements[tick];
		CrystalElement e2 = CrystalElement.elements[(tick+1)%16];
		float mix = (float)(System.currentTimeMillis()%(double)mod)/mod;
		mix = Math.min(mix*2, 1);
		int c1 = e1.getColor();
		int c2 = e2.getColor();
		int color = ReikaColorAPI.mixColors(c2, c1, mix);
		ReikaTextureHelper.bindTerrainTexture();
		Tessellator v5 = Tessellator.instance;
		v5.setBrightness(240);
		v5.startDrawingQuads();
		v5.setColorOpaque_I(color);
		IIcon ico = ChromaIcons.ROUNDFLARE.getIcon();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.ADDITIVEDARK.apply();
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		int w = 96;
		int x = posX+115;
		int y = posY-4;
		v5.addVertexWithUV(x, y+w, 0, u, dv);
		v5.addVertexWithUV(x+w, y+w, 0, du, dv);
		v5.addVertexWithUV(x+w, y, 0, du, v);
		v5.addVertexWithUV(x, y, 0, u, v);

		ico = ChromaIcons.BIGFLARE.getIcon();
		u = ico.getMinU();
		v = ico.getMinV();
		du = ico.getMaxU();
		dv = ico.getMaxV();
		w = 8;

		Iterator<PylonParticle> it = particles.iterator();
		while (it.hasNext()) {
			PylonParticle p = it.next();
			v5.addVertexWithUV(p.posX, p.posY+w, 0, u, dv);
			v5.addVertexWithUV(p.posX+w, p.posY+w, 0, du, dv);
			v5.addVertexWithUV(p.posX+w, p.posY, 0, du, v);
			v5.addVertexWithUV(p.posX, p.posY, 0, u, v);

			p.move(180D/ReikaRenderHelper.getFPS());

			p.age++;
			if (!ReikaMathLibrary.isValueInsideBounds(posX, posX+xSize-8, p.posX) || !ReikaMathLibrary.isValueInsideBounds(posY, posY+80, p.posY)) {
				it.remove();
			}
		}

		v5.draw();

		if (rand.nextInt(50) == 0) {
			particles.add(new PylonParticle(245, 40, rand.nextInt(360)));
		}

		BlendMode.DEFAULT.apply();
	}

	private void renderCrystal(int posX, int posY) {
		float mod = 2000F;
		int tick = (int)((System.currentTimeMillis()/(double)mod)%16);
		CrystalElement e1 = CrystalElement.elements[tick];
		CrystalElement e2 = CrystalElement.elements[(tick+1)%16];
		float mix = (float)(System.currentTimeMillis()%(double)mod)/mod;
		mix = Math.min(mix*2, 1);
		int c1 = ((CrystalBlock)ChromaBlocks.CRYSTAL.getBlockInstance()).getTintColor(e1.ordinal());
		int c2 = ((CrystalBlock)ChromaBlocks.CRYSTAL.getBlockInstance()).getTintColor(e2.ordinal());
		int color = ReikaColorAPI.mixColors(c2, c1, mix);
		CrystalRenderer.staticColor = color;
		this.drawBlockRender(posX, posY, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.WHITE.ordinal());
		CrystalRenderer.staticColor = -1;
	}

	private boolean isElementPage() {
		return page == ChromaResearch.ELEMENTS && subpage > 0;
	}

	private void renderElementPage(CrystalElement e, int posX, int posY, int px, int c) {
		String s = ChromaDescriptions.getElementDescription(e);
		fontRendererObj.drawSplitString(String.format("%s", s), px, posY+descY, 242, c);
		IIcon ico = e.getGlowRune();
		ReikaTextureHelper.bindTerrainTexture();
		this.drawTexturedModelRectFromIcon(posX+153, posY+12, ico, 64, 64);
	}

	private static class PylonParticle {

		private double posX;
		private double posY;
		private double angle;
		private int age;

		private PylonParticle(int x, int y, double ang) {
			posX = x;
			posY = y;
			angle = Math.toRadians(ang);
		}

		private void move(double sp) {
			posX += sp*Math.cos(angle);
			posY += sp*Math.sin(angle);
		}

	}

}
