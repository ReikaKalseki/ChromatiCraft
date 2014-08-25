package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.CastingRecipe.PylonRecipe;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class AcceleratorRecipe extends PylonRecipe {

	private static final Item[] upgrade = {Items.iron_ingot, Items.iron_ingot, Items.gold_ingot, Items.gold_ingot,
		Items.diamond, Items.emerald, Items.nether_star};

	public AcceleratorRecipe(int tier) {
		super(getItem(tier), getMainItem(tier));

		ItemStack corner = tier == 0 ? new ItemStack(Items.diamond) : new ItemStack(upgrade[tier-1]);
		this.addAuxItem(corner, -2, -2);
		this.addAuxItem(corner, 2, -2);
		this.addAuxItem(corner, 2, 2);
		this.addAuxItem(corner, -2, 2);

		ItemStack shard = this.getShard(CrystalElement.BLUE);
		this.addAuxItem(shard, 0, -2);
		this.addAuxItem(shard, 2, 0);
		this.addAuxItem(shard, 0, 2);
		this.addAuxItem(shard, -2, 0);

		this.addAuraRequirement(CrystalElement.LIGHTBLUE, 20000*(tier+1));
		this.addAuraRequirement(CrystalElement.YELLOW, 500*(tier+1));
	}

	private static ItemStack getMainItem(int tier) {
		return tier == 0 ? ChromaStacks.crystalStar : getItem(tier-1);
	}

	private static ItemStack getItem(int tier) {
		ItemStack is = ChromaTiles.ACCELERATOR.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		return is;
	}

}
