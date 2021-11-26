package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.DragonAPI.ModList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DivisionSigilActivationRecipe extends MultiBlockCastingRecipe {

	private static final Item sigil = GameRegistry.findItem(ModList.EXTRAUTILS.modLabel, "divisionSigil");

	public DivisionSigilActivationRecipe() {
		super(createOutput(), new ItemStack(sigil));

		this.addAuxItem(Items.gunpowder, -2, -2);
		this.addAuxItem(Items.gunpowder, 2, -2);
		this.addAuxItem(Items.gunpowder, -2, 2);
		this.addAuxItem(Items.gunpowder, 2, 2);

		this.addAuxItem(Items.leather, 0, 2);
		this.addAuxItem(Items.leather, 0, -2);

		this.addAuxItem(ChromaStacks.bindingCrystal, -2, 0);
		this.addAuxItem(ChromaStacks.bindingCrystal, 2, 0);

		this.addAuxItem(Blocks.obsidian, -4, -4);
		this.addAuxItem(Blocks.obsidian, 4, -4);
		this.addAuxItem(Blocks.obsidian, -4, 4);
		this.addAuxItem(Blocks.obsidian, 4, 4);
	}

	private static ItemStack createOutput() {
		ItemStack is = new ItemStack(sigil);
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("damage", 256);
		return is;
	}

	@Override
	public ItemStack getContainerItem(ItemStack in, ItemStack normal) {
		return in != null && in.getItem() == sigil ? null : super.getContainerItem(in, normal);
	}

	public static boolean isLoadable() {
		return sigil != null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return "Division Sigil Activation";
	}

}
