package Reika.ChromatiCraft.ModInterface;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandRod;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;

public class CrystalWand extends WandRod {

	public CrystalWand() {
		super("chroma_crystalwand", 2400, ChromaStacks.crystalWand, 120, null, new ResourceLocation("chromaticraft", "crystalwand"));
	}

	@Override
	public String getResearch() {
		return "";
	}

}
