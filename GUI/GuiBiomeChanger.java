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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomeChanger;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.ColorDistributor;
import Reika.DragonAPI.Instantiable.GUI.GuiPainter;
import Reika.DragonAPI.Instantiable.GUI.GuiPainter.PaintElement;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;

public class GuiBiomeChanger extends GuiChromaBase {

	private static final ColorDistributor biomeColors = new ColorDistributor(); //static to make consistent across GUI openings
	private static final HashMap<BiomeGenBase, BiomeColor> biomeEntries = new HashMap();

	static {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				int color = -1;
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
				int rgb = biomeColors.getColor(color);
				biomeEntries.put(b, new BiomeColor(/*rgb*/ReikaBiomeHelper.getBiomeUniqueColor(b)));
			}
		}
	}

	private final ArrayList<BiomeGenBase> visibleBiomes = new ArrayList();
	private final TileEntityBiomeChanger tile;
	private final GuiPainter painter = new GuiPainter(129, 129, 1);

	private GuiTextField search;

	public GuiBiomeChanger(EntityPlayer ep, TileEntityBiomeChanger te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;

		int r = 64;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				BiomeGenBase b = te.worldObj.getBiomeGenForCoords(te.xCoord+i, te.zCoord+k);
				painter.put(i+r, k+r, biomeEntries.get(b));
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		search = new GuiTextField(fontRendererObj, j+xSize/2-46, k+33, 120, 16);
		search.setFocused(false);
		search.setMaxStringLength(24);
	}

	@Override
	protected void keyTyped(char c, int key) {
		super.keyTyped(c, key);
		search.textboxKeyTyped(c, key);
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		search.mouseClicked(x, y, b);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (search.isFocused()) {

		}
	}

	private void recalcSelectedBiomes(BiomeGenBase in) {
		visibleBiomes.clear();
		Collection<BiomeGenBase> c = TileEntityBiomeChanger.getValidBiomes(in);
		String code = search.getText();
		for (BiomeGenBase out : c) {
			if (out.biomeName != null && out.biomeName.contains(code)) {
				visibleBiomes.add(out);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p, int a, int b) {
		super.drawGuiContainerBackgroundLayer(p, a, b);
		search.drawTextBox();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		painter.onRenderTick(a, b);
		painter.draw(j+xSize/2-64, k+ySize/2-64+12);
	}

	@Override
	public String getGuiTexture() {
		return "biome";
	}

	private static class BiomeColor extends PaintElement {

		private final int color;

		private BiomeColor(int c) {
			color = 0xff000000 | c;
		}

		@Override
		protected void draw(int x, int y, int s) {
			api.drawRect(x, y, s, s, color, true);
		}

	}

}
