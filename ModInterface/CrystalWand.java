package Reika.ChromatiCraft.ModInterface;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandRod;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;

public class CrystalWand extends WandRod {

	public CrystalWand() {
		super("CRYSTALWAND", 2400, ChromaStacks.crystalWand, 18, null, null);
		this.setTexture(new ResourceLocation("custom_path", "Reika/ChromatiCraft/Textures/Wands/crystalwand.png"));
	}

}
