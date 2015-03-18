package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import Reika.ChromatiCraft.Registry.ChromaResearch;

public interface ResearchDependentName {

	public Collection<ChromaResearch> getRequiredResearch(ItemStack is);

}
