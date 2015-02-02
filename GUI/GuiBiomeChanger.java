package Reika.ChromatiCraft.GUI;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomeChanger;
import Reika.DragonAPI.Base.CoreContainer;

public class GuiBiomeChanger extends GuiChromaBase {

	private final ArrayList<BiomeGenBase> visibleBiomes = new ArrayList();

	private final TileEntityBiomeChanger tile;

	private GuiTextField search;

	public GuiBiomeChanger(EntityPlayer ep, TileEntityBiomeChanger te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();
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

	private String getBiomeIcon(BiomeGenBase b) {
		return "";
	}

	@Override
	public String getGuiTexture() {
		return null;
	}

}
