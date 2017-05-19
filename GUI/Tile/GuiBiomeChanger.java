/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Instantiable.GUI.ColorDistributor;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.GUI.GuiPainter;
import Reika.DragonAPI.Instantiable.GUI.GuiPainter.Brush;
import Reika.DragonAPI.Instantiable.GUI.GuiPainter.PaintElement;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;

public class GuiBiomeChanger extends GuiChromaBase {

	private static final ColorDistributor biomeColors = new ColorDistributor(); //static to make consistent across GUI openings
	private static final HashMap<BiomeGenBase, BiomePaint> biomeEntries = new HashMap();

	private int refreshPosition;

	static {
		for (int i = 0; i <= 39; i++) { //Vanilla biomes
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			biomeColors.forceColor(ReikaBiomeHelper.getBiomeUniqueColor(b));
		}

		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				int color = -1;
				if (i >= 40) {
					if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.DESERT) || BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.HOT)) {
						color = biomeColors.generateRedColor();
					}
					else if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.FROZEN) || BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.COLD)) {
						color = biomeColors.generateBlueColor();
					}
					else if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.WATER)) {
						color = biomeColors.generateBlueColor();
					}
					else {
						color = biomeColors.generateGreenColor();
					}
				}
				else {
					color = i;
				}
				int rgb = biomeColors.getColor(color);
				biomeEntries.put(b, new BiomePaint(b, rgb));
			}
		}
	}

	private final ArrayList<BiomeGenBase> visibleBiomes = new ArrayList();
	private final TileEntityBiomePainter tile;
	private final RegionMap<BiomeGenBase> biomeRegions = new RegionMap();

	private static TileEntityBiomePainter staticTileRef;

	private BiomePainter painter;
	private GuiTextField search;
	private GuiPages page = GuiPages.PAINT;

	private boolean erase = false;

	private int frame = 0;

	public GuiBiomeChanger(EntityPlayer ep, TileEntityBiomePainter te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;

		xSize = 256;
		ySize = 212;

		staticTileRef = te;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int x, int y) {
		super.setWorldAndResolution(mc, x, y);

		int dx = (width - xSize) / 2;
		int dy = (height - ySize) / 2;
		painter = new BiomePainter(dx+10, dy+ySize/2-TileEntityBiomePainter.RANGE, TileEntityBiomePainter.RANGE*2+1, TileEntityBiomePainter.RANGE*2+1, 1, tile);
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		if (page == GuiPages.BIOME) {
			int w =  160;
			search = new GuiTextField(fontRendererObj, j+xSize/2-w/2, k+20, w, 16);
			search.setFocused(false);
			search.setMaxStringLength(32);
			this.recalcSelectedBiomes();
		}

		int w = 10;
		buttonList.add(new CustomSoundGuiButton(0, j+w, k+ySize-20-w, 20, 20, "<", this));
		buttonList.add(new CustomSoundGuiButton(1, j+xSize-20-w, k+ySize-20-w, 20, 20, ">", this));

		if (page == GuiPages.PAINT)
			buttonList.add(new CustomSoundGuiButton(2, j+w, k+16, 50, 20, erase ? "Paint" : "Erase", this));

		if (page == GuiPages.BRUSH) {
			for (int i = 0; i < Brush.brushList.length; i++) {
				Brush b = Brush.brushList[i];
				buttonList.add(b.getButton(10+i, j+20+96*(i%2), k+20+40*(i/2)));
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			page = page.pre();
			this.initGui();
		}
		else if (b.id == 1) {
			page = page.next();
			this.initGui();
		}
		else if (b.id == 2) {
			erase = !erase;
			this.initGui();
		}

		if (b.id >= 10 && page == GuiPages.BRUSH) {
			painter.brush = Brush.brushList[b.id-10];
		}
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (page == GuiPages.BIOME && search.isFocused() && key != Keyboard.KEY_ESCAPE) {
			search.textboxKeyTyped(c, key);
			this.recalcSelectedBiomes();
		}
		else {
			super.keyTyped(c, key);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		if (page == GuiPages.BIOME) {
			search.mouseClicked(x, y, b);
			ReikaJavaLibrary.pConsole(x+":"+y);

			//if (y < 190) {
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;
			BiomeGenBase biome = biomeRegions.getRegion(x-j, y-k);
			if (biome != null || painter.activeElement != null)
				//Minecraft.getMinecraft().thePlayer.playSound("random.click", 2, 1);
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			painter.activeElement = erase ? fallback : biome != null ? biomeEntries.get(biome) : null;
			//}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (page == GuiPages.BIOME) {
			if (search.isFocused()) {

				//this.recalcSelectedBiomes();
			}
		}

		if (erase)
			painter.activeElement = fallback;
	}

	private void recalcSelectedBiomes() {
		visibleBiomes.clear();
		Collection<BiomeGenBase> c = TileEntityBiomePainter.getValidBiomes();
		String code = search.getText();
		for (BiomeGenBase out : c) {
			if (code == null || code.isEmpty() || (out.biomeName != null && out.biomeName.toLowerCase(Locale.ENGLISH).startsWith(code.toLowerCase(Locale.ENGLISH)))) {
				visibleBiomes.add(out);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int x = 8;
		int y = 45;
		int dx = x;
		int dy = y;
		int i = 0;
		int n = 9;
		int h = fontRendererObj.FONT_HEIGHT+6;
		if (page == GuiPages.BIOME) {
			int mw = -1;
			biomeRegions.clear();
			for (BiomeGenBase b : visibleBiomes) {
				String name = b.biomeName != null ? b.biomeName : b.getClass().getSimpleName()+"@"+b.biomeID;
				int w = fontRendererObj.getStringWidth(name);
				fontRendererObj.drawString(name, dx, dy, 0xffffff);
				biomeRegions.addRegionByWH(dx-1, dy-1, w+2, fontRendererObj.FONT_HEIGHT+2, b);
				if (api.isMouseInBox(j+dx-2, j+dx+w+2, k+dy-1, k+dy+fontRendererObj.FONT_HEIGHT+1)) {
					api.drawRectFrame(dx-2, dy-2, w+3, fontRendererObj.FONT_HEIGHT+2, 0xffffff);
				}
				mw = Math.max(mw, 6+w);
				dy += h;
				i++;
				if (i%n == 0) {
					dy = y;
					dx += xSize/2;
					mw = -1;
				}
				if (i >= n*2)
					break;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p, int a, int b) {
		super.drawGuiContainerBackgroundLayer(p, a, b);
		if (page == GuiPages.BIOME)
			search.drawTextBox();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		frame++;

		if (page == GuiPages.PAINT) {
			this.redrawBiomes();
			painter.onRenderTick(a, b);
			painter.draw();
			painter.drawLegend(fontRendererObj, j+10+TileEntityBiomePainter.RANGE*2+3, k+ySize/2-TileEntityBiomePainter.RANGE);
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 100);
			if (GuiScreen.isShiftKeyDown()) {
				for (int dx = -TileEntityBiomePainter.RANGE; dx <= TileEntityBiomePainter.RANGE; dx += 16) {
					for (int dz = -TileEntityBiomePainter.RANGE; dz <= TileEntityBiomePainter.RANGE; dz += 16) {
						int x = j+10+TileEntityBiomePainter.RANGE+dx;
						int y = k+ySize/2+dz;
						api.drawRect(x, y, x+1, y+1, dx == 0 && dz == 0 ? 0xffffffff : 0xffff0000);
					}
				}
			}
			else if (GuiScreen.isCtrlKeyDown()) {
				int x = j+10+TileEntityBiomePainter.RANGE;
				int y = k+ySize/2;
				api.drawRect(x, y, x+1, y+1, 0xffff0000);
			}
			GL11.glPopMatrix();
		}
		if (!erase) {
			String s = String.format("Active Biome: %s", painter.activeElement != null ? painter.activeElement.getName() : "None");
			api.drawCenteredStringNoShadow(fontRendererObj, s, j+xSize/2, k+180, 0xffffff);
		}
		api.drawCenteredStringNoShadow(fontRendererObj, "Brush: "+painter.brush.name, j+xSize/2, k+190, 0xffffff);
	}

	private void redrawBiomes() {
		int r = tile.RANGE*2+1;
		//int rn = painter.isPainted(r-1, r-1) ? 16 : 2;
		int d = r;//r/rn;//r;
		int n = ReikaMathLibrary.intpow2(r, 2);
		//int d1 = frame%n;
		//int d2 = Math.min(d1+32, n-1);
		//for (int ix = -tile.RANGE; ix <= tile.RANGE; ix++) {
		//	for (int iz = -tile.RANGE; iz <= tile.RANGE; iz++) {

		for (int i = 0; i < d; i++) {
			int ix = refreshPosition%r-tile.RANGE;
			int iz = refreshPosition/r-tile.RANGE;
			int dx = tile.xCoord+ix;
			int dz = tile.zCoord+iz;

			BiomeGenBase biome = tile.worldObj.getBiomeGenForCoords(dx, dz);
			BiomePaint bp = biomeEntries.get(biome);
			if (bp != null) {
				painter.force(ix+tile.RANGE, iz+tile.RANGE, bp);
			}

			refreshPosition++;
			if (refreshPosition >= n)
				refreshPosition = 0;
		}
		//	}
		//}
	}

	@Override
	public String getGuiTexture() {
		return "biome";
	}

	private static final PaintElement fallback = new PaintElement() {

		@Override
		public void draw(int x, int y, int s) {
			//api.drawRect(x, y, s, s, 0, true); //transparent
		}

		@Override
		public String getName() {
			return "Natural";
		}

		@Override
		public boolean isPaintable(PaintElement original) {
			return true;
		}

		@Override
		public void onPaintedTo(int x, int y) {
			GuiBiomeChanger gui = (GuiBiomeChanger)Minecraft.getMinecraft().currentScreen;
			if (gui == null)
				return;
			int dx = gui.tile.xCoord+x-TileEntityBiomePainter.RANGE;
			int dz = gui.tile.zCoord+y-TileEntityBiomePainter.RANGE;
			gui.painter.erase(x, y);
			gui.tile.changeBiomeAt(dx, dz, null);
		}

	};

	private static class BiomePaint implements PaintElement {

		private final BiomeGenBase biome;
		private final int color;

		private BiomePaint(BiomeGenBase b, int c) {
			color = 0xff000000 | c;
			biome = b;
		}

		@Override
		public void draw(int x, int y, int s) {
			int c = color;
			int dx = staticTileRef.xCoord+x-TileEntityBiomePainter.RANGE;
			int dz = staticTileRef.zCoord+y-TileEntityBiomePainter.RANGE;
			if (!staticTileRef.canChangeBiomeAt(dx, dz, biome)) {
				int n = 10;
				c = ((dx+dz)%n+n)%n >= n/2 ? ReikaColorAPI.getColorWithBrightnessMultiplier(color, 0.5F) : ReikaColorAPI.getColorWithBrightnessMultiplier(color, 0.25F);
				c = 0xff000000 | c;
			}
			api.drawRect(x, y, s, s, c, true);
		}

		@Override
		public String getName() {
			return biome.biomeName != null ? biome.biomeName : biome.getClass().getSimpleName()+"@"+biome.biomeID;
		}

		@Override
		public boolean isPaintable(PaintElement original) {
			return original == fallback || TileEntityBiomePainter.getValidBiomesFor(((BiomePaint)original).biome).contains(biome);
		}

		@Override
		public void onPaintedTo(int x, int y) {
			int dx = staticTileRef.xCoord+x-TileEntityBiomePainter.RANGE;
			int dz = staticTileRef.zCoord+y-TileEntityBiomePainter.RANGE;
			staticTileRef.changeBiomeAt(dx, dz, biome);
		}

	}

	private class BiomePainter extends GuiPainter {

		private final TileEntityBiomePainter tile;

		public BiomePainter(int x, int y, int w, int h, int s, TileEntityBiomePainter te) {
			super(x, y, w, h, s);
			tile = te;
			this.init();
			//ReikaJavaLibrary.pConsole(biomeEntries+":"+this.getFallbackEntry(0, 0));
		}

		@Override
		protected PaintElement getDefaultEntry(int x, int y) {
			return null;
		}

		@Override
		protected PaintElement getFallbackEntry(int x, int y) {
			return fallback;
		}

		private void force(int x, int y, BiomePaint bp) {
			this.put(x, y, bp);
		}

	}

	private static enum GuiPages {
		PAINT(),
		BIOME(),
		BRUSH();

		private static GuiPages[] list = values();

		private GuiPages pre() {
			return this.ordinal() > 0 ? list[this.ordinal()-1] : this;
		}

		private GuiPages next() {
			return this.ordinal() < list.length-1 ? list[this.ordinal()+1] : this;
		}
	}

}
