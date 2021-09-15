package Reika.ChromatiCraft.Base;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

public abstract class FragmentStructureWithBonusLoot extends FragmentStructureBase {

	private final ArrayList<WeightedRandomChestContent> bonusItems = new ArrayList();

	protected final void addBonusItem(ItemStack is, int min, int max, int wt) {
		bonusItems.add(new WeightedRandomChestContent(is, min, max, wt));
	}

	@Override
	public final void modifyLootSet(ArrayList<WeightedRandomChestContent> li) {
		li.addAll(bonusItems);
	}

}
