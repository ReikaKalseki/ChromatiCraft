package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class RecursiveCastingAutomationSystem extends CastingAutomationSystem {

	public RecursiveCastingAutomationSystem(CastingAutomationBlock te) {
		super(te);
	}

	@Override
	public void setRecipe(CastingRecipe c, int amt) {
		if (tile.canRecursivelyRequest()) {
			;//HashMap<CastingRecipe, Integer> li = this.determineMissingRecipes(tile.getTable(), c, amt);
		}
		super.setRecipe(c, amt);
	}

	private HashMap<CastingRecipe, Integer> determineMissingRecipes(TileEntityCastingTable te, CastingRecipe recipe, int amt) {
		ItemHashMap<Integer> missing = new ItemHashMap();
		HashMap<CastingRecipe, Integer> li = new HashMap();
		if (recipe instanceof MultiBlockCastingRecipe) {
			MultiBlockCastingRecipe mr = (MultiBlockCastingRecipe)recipe;
			HashMap<List<Integer>, TileEntityItemStand> map = te.getOtherStands();
			Map<List<Integer>, ItemMatch> items = mr.getAuxItems();
			//ReikaJavaLibrary.pConsole("Need items "+items);
			for (List<Integer> key : map.keySet()) {
				ItemMatch item = items.get(key);
				TileEntityItemStand stand = map.get(key);
				if (stand != null) {
					ItemStack in = stand.getStackInSlot(0);
					if ((item == null && in != null) || (item != null && !item.match(in))) {
						if (in != null) {

						}
						else {
							ItemStack ret = this.findItem(item, amt, true);
							//ReikaJavaLibrary.pConsole("Looking for "+item+", got "+ret);
							if (ret == null || ret.stackSize < amt) {
								if (item.getItemList().size() == 1)
									missing.add(item.getItemList().iterator().next().getItemStack(), ret != null ? amt-ret.stackSize : amt);
								else
									return null;
							}
						}
					}
					else {
						//matches
					}
				}
			}
			ItemStack ctr = mr.getMainInput();
			for (int i = 0; i < 9; i++) {
				ItemStack in = te.getStackInSlot(i);
				if (i == 4) {
					if (in != null) {
						if (ReikaItemHelper.matchStacks(in, ctr) && (ctr.stackTagCompound == null || ItemStack.areItemStackTagsEqual(in, ctr))) {
							//matches
						}
						else {

						}
					}
					else {
						ItemStack ret = this.findItem(ctr, amt, true);
						//ReikaJavaLibrary.pConsole("Looking for center item "+ctr+", got "+ret);
						if (ret == null || ret.stackSize < amt) {
							missing.add(ctr, ret != null ? amt-ret.stackSize : amt);
						}
					}
				}
				else {

				}
			}
		}
		else {
			Object[] arr = recipe.getInputArray();
			//ReikaJavaLibrary.pConsole("Looking for "+Arrays.toString(arr));
			for (int i = 0; i < 9; i++) {
				Object item = arr[i];
				ItemStack in = te.getStackInSlot(i);
				if (this.matches(item, in)) {
					//match
				}
				else {
					if (in != null) {

					}
					else {
						ItemStack ret = this.findItem(item, amt, true);
						//ReikaJavaLibrary.pConsole("Looking for "+item+", got "+ret);
						if (ret == null || ret.stackSize < amt) {
							if (item instanceof ItemStack)
								missing.add((ItemStack)item, ret != null ? amt-ret.stackSize : amt);
							else
								return null;
						}
					}
				}
			}
		}

		ItemHashMap<Integer> moreMissing = new ItemHashMap();

		while (!missing.isEmpty()) {
			for (ItemStack is : missing.keySet()) {
				int req = missing.get(is);
				ArrayList<CastingRecipe> li2 = RecipesCastingTable.instance.getAllRecipesMaking(is);
				if (li2.size() != 1)
					return null;
				CastingRecipe c = li2.get(0);
				for (ItemStack is2 : c.getAllInputs()) {
					if (missing.containsKey(is2)) {
						moreMissing.add(is2, 1);
					}
				}
				int num = MathHelper.ceiling_float_int((float)req/c.getOutput().stackSize);
				Integer get = li.get(c);
				int has = get != null ? get.intValue() : 0;
				li.put(c, has+num);
			}

			missing = moreMissing;
		}

		return li;
	}

}
