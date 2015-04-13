package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiCastingAuto extends GuiChromaBase {

	private static final List<ChromaResearch> list = new ArrayList();

	static {
		for (ChromaResearch r : ChromaResearch.getAllNonParents()) {
			if (r.isCrafting() && r.getRecipeCount() > 0) {
				list.add(r);
			}
		}
	}

	private int index = 0;
	private int subindex = 0;

	private final List<ChromaResearch> visible = new ArrayList();

	private final TileEntityCastingAuto tile;

	public GuiCastingAuto(TileEntityCastingAuto te, EntityPlayer ep) {
		super(new CoreContainer(ep, te), ep, te);
		ySize = 194;

		tile = te;

		for (ChromaResearch r : list) {
			if (ChromaResearchManager.instance.playerHasFragment(ep, r)) {
				visible.add(r);
			}
		}
	}

	private ChromaResearch getActive() {
		return visible.get(index);
	}

	private CastingRecipe getRecipe() {
		List<CastingRecipe> li = this.getActive().getCraftingRecipes();
		return li != null && !li.isEmpty() ? li.get(subindex) : null;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/buttons.png";
		buttonList.add(new CustomSoundImagedGuiButton(0, j+xSize/2-40, k+20, 80, 10, 100, 16, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(1, j+xSize/2-40, k+60, 80, 10, 100, 26, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(2, j+xSize/2-80, k+20, 10, 50, 70, 6, tex, ChromatiCraft.class, this));
		buttonList.add(new CustomSoundImagedGuiButton(3, j+xSize/2+70, k+20, 10, 50, 80, 6, tex, ChromatiCraft.class, this));

		buttonList.add(new CustomSoundImagedGuiButton(4, j+xSize/2+55, k+45, 10, 10, 90, 6, tex, ChromatiCraft.class, this));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		switch(b.id) {
		case 0:
			if (index > 0) {
				subindex = 0;
				index--;
			}
			break;
		case 1:
			if (index < visible.size()-1) {
				subindex = 0;
				index++;
			}
			break;
		case 2:
			if (subindex > 0)
				subindex--;
			break;
		case 3:
			if (subindex < this.getActive().getRecipeCount()-1)
				subindex++;
			break;
		case 4:
			if (this.getActive() != null && this.getRecipe() != null)
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.AUTORECIPE.ordinal(), tile, this.getActive().ordinal(), subindex);
			break;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);

		ChromaResearch r = this.getActive();
		if (r != null) {
			r.drawTabIcon(itemRender, 21, 33);
			fontRendererObj.drawSplitString(r.getTitle(), 40, 36, 120, 0xffffff);

			CastingRecipe cr = this.getRecipe();
			if (cr != null) {
				api.drawItemStack(itemRender, cr.getOutput(), 80, 75);


				ItemHashMap<Integer> map = cr.getItemCounts();
				int dx = 6;
				int dy = 97;
				int c = 0;
				for (ItemStack is : map.keySet()) {
					int amt = map.get(is);
					api.drawItemStack(itemRender, is, dx, dy);
					fontRendererObj.drawString(String.format("x%d", amt), dx+18, dy+5, 0xffffff);
					c++;
					dy += 19;
					if (c%5 == 0) {
						dy = 97;
						dx += 42;
					}
				}
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "automator";
	}

}
